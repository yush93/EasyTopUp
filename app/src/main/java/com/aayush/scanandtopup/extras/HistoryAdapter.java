package com.aayush.scanandtopup.extras;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aayush.scanandtopup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aayus on 8/10/2017.
 */

public class HistoryAdapter extends ArrayAdapter<HistoryData>{

    private static final String TAG = "HistoryAdapter";

    private Context mContext;
    int mResource;

    public HistoryAdapter(Context context, int resource, ArrayList<HistoryData> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String date = getItem(position).getDate();
        String carrier = getItem(position).getCarrier();
        String pin = getItem(position).getPin();

        HistoryData historyData = new HistoryData(date, carrier, pin);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView txtDate = (TextView) convertView.findViewById(R.id.textView1);
        TextView txtCarrier = (TextView) convertView.findViewById(R.id.textView2);
        TextView txtPin = (TextView) convertView.findViewById(R.id.textView3);

        txtDate.setText(date);
        txtCarrier.setText(carrier);
        txtPin.setText(pin);

        return convertView;
    }
}
