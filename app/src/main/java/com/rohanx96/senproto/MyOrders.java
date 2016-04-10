package com.rohanx96.senproto;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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


public class MyOrders extends AppCompatActivity {

    RecyclerView mRecyclerView;
    CustomAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);



        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(MyOrders.this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        new GetOrders().execute();





    }

    private ProgressDialog pDialog;

    class GetOrders extends AsyncTask<Void, Void, Void> {

        JSONArray jarray;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MyOrders.this);
            pDialog.setMessage("Fetching Orders...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
            @Override
        protected Void doInBackground(Void... params) {
            HttpResponse response = null;
            try {
                // Create http client object to send request to server
                HttpClient client = new DefaultHttpClient();
                // Create URL string
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/orderslist/?format=json";
                // Create Request to server and get response
                HttpGet httpget = new HttpGet();
                httpget.setURI(new URI(URL));
                response = client.execute(httpget);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            String json_string = null;
            try {
                json_string = EntityUtils.toString(response.getEntity());
                jarray = new JSONArray(json_string);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("GetOrders ", json_string);
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();



            mAdapter = new CustomAdapter(jarray);
            mRecyclerView.setAdapter(mAdapter);

        }
    }

}


