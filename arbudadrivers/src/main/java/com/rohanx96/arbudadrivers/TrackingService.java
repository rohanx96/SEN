package com.rohanx96.arbudadrivers;

import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by rose on 13/4/16.
 */
public class TrackingService extends IntentService implements LocationListener{
    public static volatile boolean shouldContinue = true;
    private Timer timer;
    private TimerTask task;
    private Thread loop;
    // Get Class Name
    private static String TAG = TrackingService.class.getName();

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = true;

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public TrackingService(){
        super("TrackingService");
    }

    public TrackingService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (shouldContinue) {
                    getLocation();
                    if (getIsGPSTrackingEnabled()) {
                        Log.i(TAG, "Longitude: " + longitude);
                        Log.i(TAG, "Latitude: " + latitude);
                    } else
                        showSettingsAlert();
                }
                else{
                    timer.cancel();
                    task.cancel();
                }
            }
        };*/
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /*Looper.prepare();
        int delay = 5000; // delay for 5 sec.
        int period = 5000; // repeat every sec.
        timer.scheduleAtFixedRate(task, delay, period);*/
        loop = new Thread(new Runnable() {
            @Override
            public void run() {
                while (shouldContinue) {
                    getLocation();
                    if (getIsGPSTrackingEnabled()) {
                        //Log.i(TAG, "Longitude: " + longitude);
                        //Log.i(TAG, "Latitude: " + latitude);
                    } else
                        showSettingsAlert();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        loop.run();
    }

    public void getLocation() {

        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true;

                Log.d(TAG, "Application use GPS Service");

                /*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 */

                provider_info = LocationManager.GPS_PROVIDER;

            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                this.isGPSTrackingEnabled = true;

                Log.d(TAG, "Application use Network State to get GPS coordinates");

                /*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 */
                provider_info = LocationManager.NETWORK_PROVIDER;

            }
            else provider_info = null;

            // Application can use GPS or Network Provider
            if (!provider_info.isEmpty()) {
                locationManager.requestLocationUpdates(
                        provider_info,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider_info);
                    updateGPSCoordinates();
                }
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e);
            latitude = 0.0;
            longitude = 0.0;
        }
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i(TAG,"Longitude: " + longitude);
            Log.i(TAG,"Latitude: " + latitude);
            updateLocation();
        }
    }

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    public boolean getIsGPSTrackingEnabled() {

        return this.isGPSTrackingEnabled;
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(TrackingService.this);
        }
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Setting Dialog Title
        alertDialog.setTitle("Enable GPS");

        //Setting Dialog Message
        alertDialog.setMessage("Please enable GPS from settings");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
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

    public void updateLocation(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/tripdetail/" + pref.getInt(Constants.TRIP_ID,-1));

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
            object.put(Constants.TRIP_ID,pref.getInt(Constants.TRIP_ID,-1));
            object.put(Constants.TRIP_CAPACITY,pref.getInt(Constants.TRIP_CAPACITY,-1));
            object.put(Constants.LOCATION,latitude + "," + longitude);
            object.put(Constants.TRUCK_ID,pref.getInt(Constants.TRUCK_ID,-1));
            object.put(Constants.STATUS,"InTransit");
            object.put(Constants.ORDER_ID,pref.getInt(Constants.ORDER_ID,-1));
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

        } catch (IOException e) {
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        return;
    }
}
