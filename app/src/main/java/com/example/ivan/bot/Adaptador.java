package com.example.ivan.bot;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Juan on 10/12/2015.
 */
public class Adaptador extends ArrayAdapter<String> {

    private Context ctx;
    private LayoutInflater lInflator;
    private ArrayList<String> values;
    private ArrayList<Boolean> who;
    static class ViewHolder {
        TextView tvMe, tvBot;
    }

    public Adaptador(Context context, ArrayList<String> objects, ArrayList<Boolean> who) {
        super(context, R.layout.item, objects);
        this.ctx = context;
        this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.values = objects;
        this.who = who;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = new ViewHolder();

        if (convertView == null) {
            convertView = lInflator.inflate(R.layout.item, null);
            TextView tvMe = (TextView) convertView.findViewById(R.id.tvMe);
            vh.tvMe = tvMe;
            tvMe = (TextView) convertView.findViewById(R.id.tvBot);
            vh.tvBot = tvMe;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if(who.get(position)) {
            vh.tvBot.setText(values.get(position));
            vh.tvMe.setEnabled(false);
            vh.tvMe.setFocusable(false);
            vh.tvMe.setVisibility(View.INVISIBLE);
        }else{
            vh.tvMe.setText(values.get(position));

            vh.tvBot.setEnabled(false);
            vh.tvBot.setFocusable(false);
            vh.tvBot.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
