package com.rohanx96.senproto;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TrackOrderActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap mp;

    String tripid;
    //JSONArray trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);


        tripid = getIntent().getStringExtra("tripid");

        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mp = mapFragment.getMap();

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            mapFragment.getMapAsync(TrackOrderActivity.this);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000 * 60 * 2);




        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

    }


    @Override
    public void onMapReady(GoogleMap map) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /*LatLng sydney = new LatLng(-33.867,151.206);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));*/


        /*SweetAlertDialog pDialog;
        pDialog = new SweetAlertDialog(TrackOrderActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#BF4F3B"));
        pDialog.setTitleText("Getting position...");
        pDialog.setCancelable(false);
        pDialog.show();*/


        new getTrips(map).execute();

        /*JSONObject tripFound = null;
        boolean tripExists=false;
        for(int i=0;i<trips.length();i++){
            try {
                JSONObject t=trips.getJSONObject(i);
                if(t.getString("trip_id").equals(tripid) && t.getString("status").equals("InTransit")){
                    tripFound=t;
                    tripExists=true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (pDialog.isShowing())
            pDialog.dismiss();


        if(!tripExists){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Couldn't find order with ID "+tripid+" currently in transit.")
                    .show();
        }
        else{
            try {
                Double lat=Double.parseDouble(tripFound.getString("location").split(",")[0]);
                Double lon=Double.parseDouble(tripFound.getString("location").split(",")[1]);

                Log.d("coordinates",lat+" , "+lon);
                LatLng position = new LatLng(lat,lon);
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
                map.addMarker(new MarkerOptions()
                        .title("Order ID: "+tripid)
                        //.snippet("")
                        .position(position));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/


    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    SweetAlertDialog pDialog;

    class getTrips extends AsyncTask<Void, Void, JSONArray> {

        JSONArray jarray;
        GoogleMap map;

        JSONObject tripFound = null;
        boolean tripExists = false;

        public getTrips(GoogleMap map) {
            this.map = map;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(TrackOrderActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#BF4F3B"));
            pDialog.setTitleText("Getting position...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(Void... params) {

            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/triplist/?format=json";
                HttpGet httpget = new HttpGet();
                httpget.setURI(new URI(URL));
                response = client.execute(httpget);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            String json_string = null;
            try {
                assert response != null;
                json_string = EntityUtils.toString(response.getEntity());
                jarray = new JSONArray(json_string);


                for (int i = 0; i < jarray.length(); i++) {
                    try {
                        JSONObject t = jarray.getJSONObject(i);
                        if (t.getString("trip_id").equals(tripid) && t.getString("status").equals("InTransit")) {
                            tripFound = t;
                            tripExists = true;
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            Log.d("GetTrips ", json_string);

            return null;
        }


        @Override
        protected void onPostExecute(JSONArray result) {

            if (pDialog.isShowing())
                pDialog.dismiss();


            if (!tripExists) {
                new SweetAlertDialog(TrackOrderActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Couldn't find order with ID " + tripid + " currently in transit.")
                        .show();
            } else {
                try {
                    Double lat = Double.parseDouble(tripFound.getString("location").split(",")[0]);
                    Double lon = Double.parseDouble(tripFound.getString("location").split(",")[1]);

                    Log.d("coordinates", lat + " , " + lon);
                    LatLng position = new LatLng(lat, lon);
                    if (ActivityCompat.checkSelfPermission(TrackOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackOrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    map.setMyLocationEnabled(true);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
                    map.addMarker(new MarkerOptions()
                            .title("Order ID: "+tripid)
                                    //.snippet("")
                            .position(position));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }



    }
}
