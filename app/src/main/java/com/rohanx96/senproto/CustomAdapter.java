package com.rohanx96.senproto;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Saurabh on 01-04-2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    private String[] mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public CustomAdapter(String[] dataSet) {
        mDataSet = dataSet;
    }


    public class OrderViewHolder extends ViewHolder {
        TextView header,details;

        public OrderViewHolder(View v) {
            super(v);
            this.header = (TextView) v.findViewById(R.id.header);
            this.details = (TextView) v.findViewById(R.id.details);
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
        ((OrderViewHolder) holder).details.setText(mDataSet[position]);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
