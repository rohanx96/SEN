package com.rohanx96.senproto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Saurabh on 01-04-2016.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    private JSONArray mDataSet;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public CustomAdapter(Context c,JSONArray dataSet) {
        this.context=c;
        mDataSet = dataSet;
    }

    public CustomAdapter(JSONArray dataSet) {
        mDataSet = dataSet;
    }


    public class OrderViewHolder extends ViewHolder {
        TextView orderid,source,destination,goods,status;
        Button cancelOrder;

        public OrderViewHolder(View v) {
            super(v);
            this.orderid = (TextView) v.findViewById(R.id.orderid);
            this.source = (TextView) v.findViewById(R.id.source);
            this.destination = (TextView) v.findViewById(R.id.destination);
            this.goods = (TextView) v.findViewById(R.id.goods);
            this.status = (TextView) v.findViewById(R.id.status);
            this.cancelOrder = (Button) v.findViewById(R.id.cancelOrder);
        }
    }


    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card, parent, false);


        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder holder, int position) {

        position=mDataSet.length()-1-position;
        OrderViewHolder h=(OrderViewHolder)holder;
        try {
            h.orderid.setText(mDataSet.getJSONObject(position).getString("order_id"));
            h.source.setText(mDataSet.getJSONObject(position).getString("source"));
            h.destination.setText(mDataSet.getJSONObject(position).getString("destination"));
            h.goods.setText(mDataSet.getJSONObject(position).getString("goods_type")+", "+mDataSet.getJSONObject(position).getString("quantity")+"kg" );

            String s=mDataSet.getJSONObject(position).getString("order_status");
            if(s.equals("Pending"))h.status.setTextColor(Color.parseColor("#FF5722"));
            else if(s.equals("Delivered"))h.status.setTextColor(Color.parseColor("#009688"));
            else if(s.equals("InTransit"))h.status.setTextColor(Color.parseColor("#FBC02D"));
            else if(s.equals("Cancelled"))h.status.setTextColor(Color.parseColor("#BF4F3B"));
            else if(s.equals("Confirmed"))h.status.setTextColor(Color.parseColor("#CDDC39"));


            if(s.equals("Pending")){
                h.cancelOrder.setVisibility(View.VISIBLE);
            }
            else{
                h.cancelOrder.setVisibility(View.GONE);
            }

            h.status.setText(s);


            final int finalPosition = position;
            h.cancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*try {
                        Toast.makeText(context, mDataSet.getJSONObject(finalPosition).getString("order_id"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Cancel Order?")
                            .setConfirmText("Yes")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    try {
                                        new cancelOrder(mDataSet.getJSONObject(finalPosition)).execute();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.length();
    }



    class cancelOrder extends AsyncTask<Void, Void, Void> {
        private SweetAlertDialog pDialog;
        JSONArray jarray;
        int statusCode=0;
        JSONObject obj;

        public cancelOrder(JSONObject j) {
           this.obj=j;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#BF4F3B"));
            pDialog.setTitleText("Canceling Order...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... params) {

            //cancel order
            try {

                HttpClient client = new DefaultHttpClient();
                HttpPut put= new HttpPut("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/ordersdetail/"+obj.getString("order_id")+"?format=json");


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("order_id", obj.getString("order_id")));
                nameValuePairs.add(new BasicNameValuePair("name", obj.getString("name")));
                nameValuePairs.add(new BasicNameValuePair("goods_type", obj.getString("goods_type")));
                nameValuePairs.add(new BasicNameValuePair("order_status", "Cancelled"));
                nameValuePairs.add(new BasicNameValuePair("quantity", obj.getString("quantity")));
                nameValuePairs.add(new BasicNameValuePair("source", obj.getString("source")));
                nameValuePairs.add(new BasicNameValuePair("destination", obj.getString("destination")));
                nameValuePairs.add(new BasicNameValuePair("add_contact_num", obj.getString("add_contact_num")));
                nameValuePairs.add(new BasicNameValuePair("additional_info", obj.getString("additional_info")));
                nameValuePairs.add(new BasicNameValuePair("contact_num", obj.getString("contact_num")));

                put.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(put);
                String json_string = EntityUtils.toString(response.getEntity());
                statusCode=response.getStatusLine().getStatusCode();
                Log.d("UpdateOrder",  obj.getString("order_id"));
                Log.d("UpdateOrder", response.toString()+" "+statusCode);
                Log.d("UpdateOrder", json_string);

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

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            //notifyDataSetChanged();
            ((Activity)context).finish();
            context.startActivity(new Intent(context,MyOrders.class));
        }
    }
}


