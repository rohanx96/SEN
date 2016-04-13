package com.rohanx96.senproto;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MyOrders extends AppCompatActivity {

    RecyclerView mRecyclerView;
    CustomAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    String SPcontact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);



        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(MyOrders.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        SPcontact = prefs.getString("contact", "");

        Log.d("SharedPref",SPcontact);
        if(isConnectingToInternet()){
            new GetOrders().execute();
        }
        else{
            SweetAlertDialog d=new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
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



    private SweetAlertDialog pDialog;

    class GetOrders extends AsyncTask<Void, Void, Void> {

        JSONArray jarray;
        int statusCode=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SweetAlertDialog(MyOrders.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#BF4F3B"));
            pDialog.setTitleText("Fetching Orders...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
            @Override
        protected Void doInBackground(Void... params) {
            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/orderslist/?format=json";
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
                json_string = EntityUtils.toString(response.getEntity());
                jarray = new JSONArray(json_string);

                int len=jarray.length();
                JSONArray newjarray=new JSONArray();
                for(int i=0;i<len;i++){
                    String c=jarray.getJSONObject(i).getString("contact_num");
                    //Log.d("GetOrders",i+" "+c+" "+SPcontact);
                    if(c.equals(SPcontact)){
                        newjarray.put(jarray.getJSONObject(i));
                    }
                }

                jarray=newjarray;


                statusCode=response.getStatusLine().getStatusCode();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


                Log.d("GetOrders ", statusCode + " " + json_string);
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            if(statusCode==200){
                if (pDialog.isShowing()) {

                    if(jarray.length()>0){
                        pDialog.dismissWithAnimation();
                    }
                    else{
                        pDialog
                                .setTitleText("You haven't placed any order.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        finish();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    }

                }
            } else{
                if (pDialog.isShowing()) {
                    pDialog
                            .setTitleText("Error")
                            .setContentText("Cannot fetch orders.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                    finish();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }


            if(jarray!=null && jarray.length()>0){
                mAdapter = new CustomAdapter(MyOrders.this,jarray);
                mRecyclerView.setAdapter(mAdapter);
            }


        }
    }



}


