package com.rohanx96.senproto;

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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class MyOrders extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView mRecyclerView;
        CustomAdapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(MyOrders.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        String[] mDataset = {"order1", "order2",
                "order3", "order4"};

        mAdapter = new CustomAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        new GetOrders().execute();

    }

    class GetOrders extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpResponse response = null;
            try {
                // Create http client object to send request to server
                HttpClient client = new DefaultHttpClient();
                // Create URL string
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/driverlist/?format=json";
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
                JSONArray temp1 = new JSONArray(json_string);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("GetResponse ", json_string);
            return null;
        }
    }

}


