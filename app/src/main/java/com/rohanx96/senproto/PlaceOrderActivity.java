package com.rohanx96.senproto;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.RequestQueue;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.acl.Owner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
/**
 * Created by rose on 8/3/16.
 */
public class PlaceOrderActivity  extends AppCompatActivity implements OnConnectionFailedListener{


    int PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 2;


    private GoogleApiClient mGoogleApiClient;
    String TAG="PlacesOrderActivity: ";
    MaterialEditText etweight,etinfo,etcontact,etsource,etdestination;
    MaterialSpinner itemspinner;
    Button placeOrderButton;

    String SPname,SPcontact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();



        etsource=(MaterialEditText)findViewById(R.id.etsource);
        etdestination=(MaterialEditText)findViewById(R.id.etdestination);
        etweight=(MaterialEditText)findViewById(R.id.etweight);
        etinfo=(MaterialEditText)findViewById(R.id.etinfo);
        etcontact=(MaterialEditText)findViewById(R.id.etcontact);
        itemspinner=(MaterialSpinner)findViewById(R.id.itemspinner);
        //etdate=(MaterialEditText)findViewById(R.id.etdate);
        //etdate.setKeyListener(null);
        placeOrderButton=(Button)findViewById(R.id.place_order_button);



        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);



        etsource.setKeyListener(null);
        etsource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(PlaceOrderActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
            }
        });
        etsource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(PlaceOrderActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });



        etdestination.setKeyListener(null);
        etdestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(PlaceOrderActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
            }
        });
        etdestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(PlaceOrderActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });



        String contact = prefs.getString("contact", "");
        if(!(contact.equals(""))){
            SPcontact=contact;
            SPname=prefs.getString("name","");
            Log.d("SharedPref",contact);
            etcontact.setText(contact);
        }


        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new UpdateOrder().execute();

                if(!validateAllFields() )return;

                if(isConnectingToInternet()){
                        PostOrder post = new PostOrder();
                        post.execute();
                }
                else{
                    SweetAlertDialog d=new SweetAlertDialog(PlaceOrderActivity.this, SweetAlertDialog.ERROR_TYPE);
                    d.setCancelable(false);
                    d.setTitleText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    finish();
                                }
                            })
                            .setContentText("Connect to an internet connection and try again.")
                            .show();
                }


            }
        });

        //entries for items' spinner
        List<String> nameslist = new ArrayList<String>();
        nameslist.add("Grains");
        nameslist.add("Cotton");
        nameslist.add("Tiles");
        nameslist.add("Drinks");
        nameslist.add("Cement");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameslist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemspinner.setAdapter(adapter);




        /*Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        c.add(Calendar.DATE, 1);
        etdate.setText(sdf.format(c.getTime()));
        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etdate.setHelperText("Only orders to be delivered tomorrow will be accepted now.");
            }
        });*/

        /*Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etdate.setText(dateFormatter.format(newDate.getTime()));    //=================>date format
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        etdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show();
                }
            }
        });
        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });*/

    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                etsource.setText(place.getName()+", "+place.getAddress());

                Log.i(TAG, "PlaceSource: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                etdestination.setText(place.getName()+", "+place.getAddress());

                Log.i(TAG, "PlaceDestination: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }



    private boolean validateAllFields() {
        String w=etweight.getText().toString().trim();
        String c=etcontact.getText().toString().trim();
        String s=etsource.getText().toString().trim();
        String d=etdestination.getText().toString().trim();
        String i=itemspinner.getSelectedItem().toString().trim();

        if(c.equals("") ||  w.equals("") || i.equals(itemspinner.getHint().toString()) || s.equals("") || d.equals("") || (!s.equals("") && !d.equals("") && s.equals(d))){

            if(c.equals(""))etcontact.validateWith(new RegexpValidator("Enter contact number.", "\\d+"));
            if(w.equals("")) etweight.validateWith(new RegexpValidator("Enter weight of goods to be delivered.", "\\d+"));
            if(i.equals(itemspinner.getHint().toString()))itemspinner.setError("Select type of goods to be delivered.");
            if(s.equals("")) etsource.validateWith(new RegexpValidator("Select source place.", "\\d+"));
            if(d.equals("")) etdestination.validateWith(new RegexpValidator("Select destination place.", "\\d+"));
            if(!s.equals("") && !d.equals("") && s.equals(d))etdestination.validateWith(new RegexpValidator("Source and destination cannot be same.", "\\d+"));
            return false;
        }
        return true;
    }






    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("TAG", res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(PlaceOrderActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }




    private SweetAlertDialog pDialog;


    class PostOrder extends AsyncTask<Void, Void, Void> {

        int orderid,driverid;
        String truckid;
        int statusCode=0;//statusCode2=0;
        String goods,contact,weight,info,source,destination;

        JSONArray jarrayOrders;/*jarrayDrivers,jarrayTrucks;

        Boolean truckAvailable=false;
        Boolean driverAvailable=false;

        JSONObject chosenDriver;*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SweetAlertDialog(PlaceOrderActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#BF4F3B"));
            pDialog.setTitleText("Placing Order...");
            pDialog.setCancelable(false);
            pDialog.show();

            goods=itemspinner.getSelectedItem().toString();
            weight=etweight.getText().toString().trim();
            contact=etcontact.getText().toString().trim();
            source=etsource.getText().toString();
            destination=etdestination.getText().toString();
            info=etinfo.getText().toString().trim();
            if(info.equals(""))info="No additional information";
        }
        @Override
        protected Void doInBackground(Void... params) {


            //FETCHING ORDERS
            try {
                HttpClient client = new DefaultHttpClient();
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/orderslist/?format=json";
                HttpGet httpget = new HttpGet();
                httpget.setURI(new URI(URL));
                HttpResponse getresponse = client.execute(httpget);

                String get_json_string = EntityUtils.toString(getresponse.getEntity());
                jarrayOrders = new JSONArray(get_json_string);

                if(jarrayOrders.length()>0){
                    JSONObject obj=jarrayOrders.getJSONObject(jarrayOrders.length() - 1);
                    orderid=Integer.parseInt(obj.getString("order_id"));
                    orderid++;
                }
                else{
                    orderid=0;
                }


            } catch (URISyntaxException | IOException | JSONException e) {
                e.printStackTrace();
            }


            /*

            //FETCHING TRUCKS
            try {
                HttpClient client = new DefaultHttpClient();
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/trucklist/?format=json";
                HttpGet httpget = new HttpGet();
                httpget.setURI(new URI(URL));
                HttpResponse getresponse = client.execute(httpget);

                String get_json_string = EntityUtils.toString(getresponse.getEntity());
                jarrayTrucks = new JSONArray(get_json_string);

                //sorting according to capacity
                JSONArray sortedTrucksArray = new JSONArray();
                List<JSONObject> jsonValues = new ArrayList<JSONObject>();
                for (int i = 0; i < jarrayTrucks.length(); i++) {
                    jsonValues.add(jarrayTrucks.getJSONObject(i));
                }

                Collections.sort(jsonValues, new Comparator<JSONObject>() {

                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        int valA = 0,valB=0;
                        try {
                            valA = a.getInt("truck_capacity");
                            valB = b.getInt("truck_capacity");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(valA >= valB)
                            return 1;
                        if(valA < valB)
                            return -1;

                        return 0;
                    }
                });

                for (int i = 0; i < jarrayTrucks.length(); i++) {
                    sortedTrucksArray.put(jsonValues.get(i));
                    Log.d("TrucksCapacity", String.valueOf(sortedTrucksArray.getJSONObject(i).getInt("truck_capacity")));
                }


                for(int i = 0; i < sortedTrucksArray.length(); i++){
                    JSONObject obj=sortedTrucksArray.getJSONObject(i);
                    if(obj.getInt("truck_capacity") >= Integer.parseInt(weight) && obj.getString("truck_status").equals("InGarage")){
                        truckAvailable=true;
                        truckid=obj.getString("truck_id");
                        Log.d("TruckChosen",String.valueOf(truckid));
                        break;
                    }
                }


            } catch (URISyntaxException | IOException | JSONException e) {
                e.printStackTrace();
            }



            //FETCHING DRIVERS
            try {
                HttpClient client = new DefaultHttpClient();
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/driverlist/?format=json";
                HttpGet httpget = new HttpGet();
                httpget.setURI(new URI(URL));
                HttpResponse getresponse = client.execute(httpget);

                String get_json_string = EntityUtils.toString(getresponse.getEntity());
                jarrayDrivers = new JSONArray(get_json_string);


                for(int i = 0; i < jarrayDrivers.length(); i++){
                    JSONObject obj=jarrayDrivers.getJSONObject(i);
                    if(obj.getString("attendance").equals("Present")){
                        chosenDriver=obj;
                        driverAvailable=true;
                        driverid=obj.getInt("driver_id");
                        Log.d("DriverChosen",String.valueOf(driverid)+" "+obj.getString("name"));
                        break;
                    }
                }

            } catch (URISyntaxException | IOException | JSONException e) {
                e.printStackTrace();
            }


            Log.d("booleans",""+truckAvailable+" "+driverAvailable);
            if(truckAvailable && driverAvailable){


                */

                //POSTING ORDER NOW
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/orderslist/?format=json");

                try {

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("order_id", String.valueOf(orderid)));
                    nameValuePairs.add(new BasicNameValuePair("name", SPname));
                    nameValuePairs.add(new BasicNameValuePair("goods_type", goods));
                    nameValuePairs.add(new BasicNameValuePair("order_status", "Pending"));
                    nameValuePairs.add(new BasicNameValuePair("quantity", weight));
                    nameValuePairs.add(new BasicNameValuePair("source", source));
                    nameValuePairs.add(new BasicNameValuePair("destination", destination));
                    nameValuePairs.add(new BasicNameValuePair("add_contact_num", contact));
                    nameValuePairs.add(new BasicNameValuePair("additional_info", info));
                    nameValuePairs.add(new BasicNameValuePair("contact_num", SPcontact));

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    String json_string = EntityUtils.toString(response.getEntity());
                    statusCode=response.getStatusLine().getStatusCode();
                    Log.d("PostOrder", statusCode+" "+json_string);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }

            /*

                //POSTING TRIP NOW
                httpclient = new DefaultHttpClient();
                httppost = new HttpPost("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/triplist/?format=json");

                try {

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("trip_id", String.valueOf(orderid)));
                    nameValuePairs.add(new BasicNameValuePair("trip_capacity", weight));
                    nameValuePairs.add(new BasicNameValuePair("waypoint", "pata nahi"));
                    nameValuePairs.add(new BasicNameValuePair("location", "0,0"));
                    nameValuePairs.add(new BasicNameValuePair("status", "ready"));
                    nameValuePairs.add(new BasicNameValuePair("order_id", String.valueOf(orderid)));
                    nameValuePairs.add(new BasicNameValuePair("truck_id", truckid));

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    String json_string = EntityUtils.toString(response.getEntity());
                    statusCode2=response.getStatusLine().getStatusCode();
                    Log.d("PostTrip", statusCode2+" "+json_string);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }



            }


            //UPDATING DRIVER AVAILABILITY
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPut put= new HttpPut("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/driverdetail/"+driverid+"?format=json");


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("driver_id", chosenDriver.getString("driver_id")));
                nameValuePairs.add(new BasicNameValuePair("name", chosenDriver.getString("name")));
                nameValuePairs.add(new BasicNameValuePair("password", chosenDriver.getString("password")));
                nameValuePairs.add(new BasicNameValuePair("attendance", "assigned"));
                nameValuePairs.add(new BasicNameValuePair("trip_id", String.valueOf(orderid)));



                put.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(put);
                String json_string = EntityUtils.toString(response.getEntity());
                Log.d("UpdateDriver", response.toString()+" "+String.valueOf(response.getStatusLine().getStatusCode()));
                Log.d("UpdateDriver", json_string);

            } catch (MalformedURLException e) {
                Log.d("UpdateOrder", "MalformedURLException");
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                Log.d("UpdateOrder", "ClientProtocolException");
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                Log.d("UpdateOrder", "UnsupportedEncodingException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("UpdateOrder", "IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            */
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /*if (pDialog.isShowing())
                pDialog.dismiss();*/

            //if(truckAvailable && driverAvailable){
                if(statusCode==201){
                    if (pDialog.isShowing()){
                        pDialog.setTitleText("Order Placed.")
                                .setContentText("Order ID: "+orderid+"\nAgency will contact you for confirmation."+"\nYou can check your orders in My Orders page.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog pDialog) {
                                        pDialog.dismissWithAnimation();
                                        finish();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                }
                else{
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    new SweetAlertDialog(PlaceOrderActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error in placing order.")
                            .setContentText("Something went wrong.")
                            .show();
                }
            /*}
            else{
                if (pDialog.isShowing())
                    pDialog.dismiss();
                new SweetAlertDialog(PlaceOrderActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error in scheduling order.")
                        .setContentText("Either truck or driver unavailability.Try again later.")
                        .show();
            }*/


        }

    }






}
