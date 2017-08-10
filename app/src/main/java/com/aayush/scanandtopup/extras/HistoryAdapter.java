package com.aayush.scanandtopup.extras;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.aayush.scanandtopup.R;
import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<HistoryData>{
    private static final String TAG = "HistoryAdapter";
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    static class ViewHolder {
        TextView date;
        TextView carrier;
        TextView pin;
    }

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
        final View result;
        ViewHolder holder = new ViewHolder();
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.textView1);
            holder.carrier = (TextView) convertView.findViewById(R.id.textView2);
            holder.pin = (TextView) convertView.findViewById(R.id.textView3);
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition)?R.anim.load_down_anim:R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;
        holder.date.setText(date);
        holder.carrier.setText(carrier);
        holder.pin.setText(pin);
        return convertView;
    }
}
