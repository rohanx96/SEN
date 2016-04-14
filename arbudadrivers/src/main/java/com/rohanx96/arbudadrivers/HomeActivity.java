package com.rohanx96.arbudadrivers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rose on 6/4/16.
 */
public class HomeActivity extends AppCompatActivity {
    private int mTripID;
    private int mOrderID;
    private String mSource;
    private String mDestination;

    private String ORDER_ID = "order_id";
    private String GOODS_TYPE = "goods_type";
    private String ORDER_STATUS = "order_status";
    private String QUANTITY = "quantity";
    private String CONTACT = "contact_num";
    private String SOURCE = "source";
    private String DESTINATION = "destination";
    SharedPreferences pref;

    @Bind(R.id.start_trip_progress) ProgressBar mProgressBar;
    @Bind(R.id.home_end_trip)
    Button endTrip;
    @Bind(R.id.home_end_trip_text)
    TextView endTripText;
    @Bind(R.id.home_start_trip)
    Button startTrip;
    @Bind(R.id.home_container)
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getBoolean(getString(R.string.pref_trip_started),false)){
            startTrip.setText("RESUME TRIP");
            endTripText.setVisibility(View.VISIBLE);
            endTrip.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.home_start_trip)
    public void startTrip(){
        mProgressBar.setVisibility(View.VISIBLE);
        int driverId = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.pref_driver_id),-1);
        FetchSourceDestination fetchSourceDestinationTask = new FetchSourceDestination(driverId);
        fetchSourceDestinationTask.execute();
    }

    @OnClick(R.id.home_end_trip)
    public void endTrip(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to end your trip?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Ending Trip", Toast.LENGTH_LONG).show();
                ChangeOrderStatus orderStatusTask = new ChangeOrderStatus("Delivered",true);
                orderStatusTask.execute();
                TrackingService.shouldContinue = false;
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @OnClick(R.id.home_logout_driver)
    public void logout(){
        pref.edit().putBoolean(getString(R.string.pref_signed_in),false).apply();
        startActivity(new Intent(this,LoginActivity.class));
        Toast.makeText(this,"You have successfully logged out",Toast.LENGTH_LONG).show();
        finish();
    }

    public class FetchSourceDestination extends AsyncTask<Void, Void, Boolean> {

        private int mDriverID;

        FetchSourceDestination(int driverID) {
            mDriverID = driverID;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                String json = getJSONFromUrl("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/driverlist/");
                try {
                    JSONArray root = new JSONArray(json);
                    for(int i =0;i<root.length();i++){
                        JSONObject driverObject = root.getJSONObject(i);
                        if (driverObject.getInt("driver_id") == mDriverID){
                            mTripID = driverObject.getInt("trip_id");
                            String json2 = getJSONFromUrl("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/triplist/");
                            JSONArray root2 = new JSONArray(json2);
                            for(int j =0;j<root2.length();j++) {
                                JSONObject tripObject = root2.getJSONObject(j);
                                if (tripObject.getInt("trip_id") == mTripID){
                                    saveTripDetails(tripObject);
                                    mOrderID = tripObject.getInt("order_id");
                                    String json3 = getJSONFromUrl("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/orderslist/");
                                    JSONArray root3 = new JSONArray(json3);
                                    for(int k =0;k<root3.length();k++) {
                                        JSONObject orderObject = root3.getJSONObject(k);
                                        if (orderObject.getInt(ORDER_ID) == mOrderID){
                                            if (orderObject.getString(ORDER_STATUS).equals("Confirmed")
                                                    ||orderObject.getString(ORDER_STATUS).equals("InTransit")) {
                                                pref.edit().putBoolean(getString(R.string.pref_trip_started), true).apply();
                                                saveOrderDetails(orderObject);
                                                mSource = orderObject.getString(SOURCE);
                                                mDestination = orderObject.getString(DESTINATION);
                                                return true;
                                            }
                                        }
                                    }
                                    return false;
                                }
                            }
                            return false;
                        }
                    }
                    return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //showProgress(false);
            mProgressBar.setVisibility(View.GONE);
            if (success) {
                ChangeOrderStatus changeOrderStatusTask = new ChangeOrderStatus("InTransit",false);
                changeOrderStatusTask.execute();
                TrackingService.shouldContinue = true;
                Intent track = new Intent(HomeActivity.this,TrackingService.class);
                startService(track);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?"+ "&daddr=" + mDestination));
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Starting navigation",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"You have no trips assigned",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"You have no trips assigned",Toast.LENGTH_LONG).show();
        }
    }

    private String getJSONFromUrl(String urlString) throws IOException {
        InputStream is = null;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000 /* milliseconds */);
        connection.setConnectTimeout(15000 /* milliseconds */);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        is = connection.getInputStream();

        // Convert the InputStream into a string
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        Log.i("receivedString", buffer.toString());
        return buffer.toString();
    }


    private ProgressDialog pDialog;
    int count=4;

    class ChangeOrderStatus extends AsyncTask<Void, Void, Boolean> {

        int orderID;
        String status;
        boolean shouldShowProgress;
        public ChangeOrderStatus(String status, boolean shouldShowProgress){
            this.orderID = pref.getInt(ORDER_ID,-1);
            this.status = status;
            this.shouldShowProgress = shouldShowProgress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            if (shouldShowProgress) {
                pDialog = new ProgressDialog(HomeActivity.this);
                pDialog.setMessage("Ending Trip ...");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }
        @Override
        protected Boolean doInBackground(Void... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/ordersdetail/" + orderID);
            Log.i("orderID: "," " + orderID);

            try {

                /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair(ORDER_ID, Integer.toString(pref.getInt(ORDER_ID,-1))));
                nameValuePairs.add(new BasicNameValuePair("goods_type", pref.getString(GOODS_TYPE,"type")));
                nameValuePairs.add(new BasicNameValuePair("order_status", status));
                nameValuePairs.add(new BasicNameValuePair("quantity", Integer.toString(pref.getInt(QUANTITY,-1))));
                nameValuePairs.add(new BasicNameValuePair("source", pref.getString(SOURCE,"source")));
                nameValuePairs.add(new BasicNameValuePair("destination", pref.getString(DESTINATION,"destination")));
                nameValuePairs.add(new BasicNameValuePair("contact_num", Integer.toString(pref.getInt(CONTACT,-1))));*/
                String json = "";
                JSONObject object = new JSONObject();
                object.put(ORDER_ID,pref.getInt(ORDER_ID,-1));
                object.put(GOODS_TYPE,pref.getString(GOODS_TYPE,"type"));
                object.put(ORDER_STATUS,status);
                object.put(QUANTITY,pref.getInt(QUANTITY,-1));
                object.put(SOURCE,pref.getString(SOURCE,"source"));
                object.put(DESTINATION,pref.getString(DESTINATION,"destination"));
                object.put(CONTACT,pref.getString(CONTACT,""));
                object.put(Constants.ADDITIONAL_INFO,pref.getString(Constants.ADDITIONAL_INFO,"null"));
                object.put(Constants.ADD_CONTACT_NUM,pref.getString(Constants.ADD_CONTACT_NUM,"null"));
                object.put(Constants.NAME,pref.getString(Constants.NAME,"null"));
                json = object.toString();
                StringEntity se = new StringEntity(json);
                //httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpPut.setEntity(se);
                httpPut.setHeader("Accept", "application/json");
                httpPut.setHeader("Content-type", "application/json");
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httpPut);
                String json_string = EntityUtils.toString(response.getEntity());
                Log.d("PostOrder", json_string);

            } catch (ClientProtocolException e) {
                return false;
            } catch (IOException e) {
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(shouldShowProgress){
                if (result) {
                    pref.edit().putBoolean(getString(R.string.pref_trip_started), false).apply();
                    endTripText.setVisibility(View.GONE);
                    endTrip.setVisibility(View.GONE);
                    startTrip.setText("START TRIP");
                    Snackbar.make(container,"Your trip has ended",Snackbar.LENGTH_LONG).show();
                }
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }
    }

    public void saveOrderDetails(JSONObject orderObject) throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putInt(ORDER_ID, mOrderID).apply();
        preferences.edit().putString(GOODS_TYPE, orderObject.getString(GOODS_TYPE)).apply();
        preferences.edit().putString(ORDER_STATUS, orderObject.getString(ORDER_STATUS)).apply();
        preferences.edit().putInt(QUANTITY, orderObject.getInt(QUANTITY)).apply();
        preferences.edit().putString(SOURCE, mSource).apply();
        preferences.edit().putString(DESTINATION, mDestination).apply();
        preferences.edit().putString(CONTACT, orderObject.getString(CONTACT)).apply();
        preferences.edit().putString(Constants.ADD_CONTACT_NUM,orderObject.getString(Constants.ADD_CONTACT_NUM)).apply();
        preferences.edit().putString(Constants.ADDITIONAL_INFO,orderObject.getString(Constants.ADDITIONAL_INFO)).apply();
        preferences.edit().putString(Constants.NAME,orderObject.getString(Constants.NAME)).apply();
    }

    public void saveTripDetails(JSONObject tripObject) throws JSONException {
        pref.edit().putInt(Constants.TRIP_ID,tripObject.getInt(Constants.TRIP_ID)).apply();
        pref.edit().putInt(Constants.TRIP_CAPACITY,tripObject.getInt(Constants.TRIP_CAPACITY)).apply();
        pref.edit().putInt(Constants.TRUCK_ID,tripObject.getInt(Constants.TRUCK_ID)).apply();
        pref.edit().putString(Constants.LOCATION,tripObject.getString(Constants.LOCATION)).apply();
        pref.edit().putString(Constants.STATUS,tripObject.getString(Constants.STATUS)).apply();
    }
}
