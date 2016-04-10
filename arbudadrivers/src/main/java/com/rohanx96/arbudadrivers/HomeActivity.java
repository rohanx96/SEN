package com.rohanx96.arbudadrivers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    @Bind(R.id.start_trip_progress) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.home_start_trip)
    public void startTrip(){
        mProgressBar.setVisibility(View.VISIBLE);
        int driverId = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.pref_driver_id),-1);
        FetchSourceDestination fetchSourceDestinationTask = new FetchSourceDestination(driverId);
        fetchSourceDestinationTask.execute();
    }

    public class FetchSourceDestination extends AsyncTask<Void, Void, Boolean> {

        private int mDriverID;

        FetchSourceDestination(int driverID) {
            mDriverID = driverID;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.\
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
                            for(int j =0;i<root2.length();j++) {
                                JSONObject tripObject = root2.getJSONObject(j);
                                if (tripObject.getInt("trip_id") == mTripID){
                                    mOrderID = tripObject.getInt("order_id");
                                    String json3 = getJSONFromUrl("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/orderslist/");
                                    JSONArray root3 = new JSONArray(json3);
                                    for(int k =0;i<root3.length();k++) {
                                        JSONObject orderObject = root3.getJSONObject(k);
                                        if (orderObject.getInt("order_id") == mOrderID){
                                            mSource = orderObject.getString("source");
                                            mDestination = orderObject.getString("destination");
                                            return true;
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
}
