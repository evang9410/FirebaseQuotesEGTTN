package com.evanjustin.qegttnfirebasequotes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Evan Glicakis on 10/26/2016.
 * Adapter for the List Activity
 */
public class QuoteCategoryAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;
    private String[] data;

    public QuoteCategoryAdapter(Context context, int resource, String[] data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    /**
     * Internal class to hold the handle to the TextView object for list content.
     */
    public class Holder{
        TextView tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Holder holder = null;
        View row =  convertView;
        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource,parent,false);
            holder = new Holder();
            holder.tv = (TextView)row.findViewById(R.id.lv_category_name);
            row.setTag(holder);

        }else{
            holder = (Holder)row.getTag();
        }
        String s = data[position];
        holder.tv.setText(s);
        return row;
    }

}
