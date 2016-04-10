package com.rohanx96.senproto;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import android.graphics.Color;
/**
 * Created by Saurabh on 01-04-2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    private JSONArray mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public CustomAdapter(JSONArray dataSet) {
        mDataSet = dataSet;
    }


    public class OrderViewHolder extends ViewHolder {
        TextView orderid,source,destination,goods,status;

        public OrderViewHolder(View v) {
            super(v);
            this.orderid = (TextView) v.findViewById(R.id.orderid);
            this.source = (TextView) v.findViewById(R.id.source);
            this.destination = (TextView) v.findViewById(R.id.destination);
            this.goods = (TextView) v.findViewById(R.id.goods);
            this.status = (TextView) v.findViewById(R.id.status);
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

        OrderViewHolder h=(OrderViewHolder)holder;
        try {
            h.orderid.setText(mDataSet.getJSONObject(position).getString("order_id"));
            h.source.setText(mDataSet.getJSONObject(position).getString("source"));
            h.destination.setText(mDataSet.getJSONObject(position).getString("destination"));
            h.goods.setText(mDataSet.getJSONObject(position).getString("goods_type")+", "+mDataSet.getJSONObject(position).getString("quantity")+"kg" );

            String s=mDataSet.getJSONObject(position).getString("order_status");
            if(s.equals("pending"))h.status.setTextColor(Color.parseColor("#FF5722"));
            else if(s.equals("delivered"))h.status.setTextColor(Color.parseColor("#4CAF50"));
            else if(s.equals("intransit"))h.status.setTextColor(Color.parseColor("#FFEB3B"));

            h.status.setText(s);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.length();
    }
}
