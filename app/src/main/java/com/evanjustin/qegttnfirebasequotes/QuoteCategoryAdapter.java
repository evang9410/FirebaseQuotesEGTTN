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
 * Created by 1432581 on 10/26/2016.
 */
public class QuoteCategoryAdapter extends ArrayAdapter<Quote> {
    private Context context;
    private int resource;
    private Quote[] data;

    public QuoteCategoryAdapter(Context context, int resource, Quote[] data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }
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
        Quote quote = data[position];
        holder.tv.setText(quote.getCategory());
        return row;
    }
}
