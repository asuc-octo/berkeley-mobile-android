package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Route;

import java.text.SimpleDateFormat;

public class RouteAdapter extends BaseAdapter {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm a");

    private Context context;
    private Route route;

    public RouteAdapter(Context context, Route route) {
        this.context = context;
        this.route = route;
    }

    @Override
    public int getCount() {
        return route.getStops().length;
    }

    @Override
    public String getItem(int i) {
        return route.getStops()[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.route_row, parent, false);
        }

        TextView endpoint = (TextView) convertView.findViewById(R.id.endpoint);
        ImageView stopIcon = (ImageView) convertView.findViewById(R.id.lollipop);
        TextView stopName = (TextView) convertView.findViewById(R.id.stop_name);

        endpoint.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
        stopName.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));

        // Set the left-hand route icons and endpoints
        if (i == 0) {
            endpoint.setText("Start\n" + TIME_FORMAT.format(route.getStartTime()));
            stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_start));
        } else if (i == getCount() - 1) {
            stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_end));
            endpoint.setText("Arrive\n" + TIME_FORMAT.format(route.getEndTime()));
        } else {
            stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_mid));
            endpoint.setText("");
        }

        stopName.setText(getItem(i));

        return convertView;
    }

}
