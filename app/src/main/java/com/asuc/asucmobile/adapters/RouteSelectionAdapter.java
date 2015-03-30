package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RouteSelectionAdapter extends BaseAdapter {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private Context context;
    private ArrayList<Route> routes;

    public RouteSelectionAdapter(Context context, ArrayList<Route> routes) {
        this.context = context;
        this.routes = routes;
    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Route getItem(int i) {
        return routes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View convertView, ViewGroup parent) {
        Route route = getItem(i);
        Date startTime = route.getTrips().get(0).getStartTime();
        Date endTime = route.getTrips().get(route.getTrips().size() - 1).getEndTime();
        long timeDiff = endTime.getTime() - startTime.getTime();
        int duration = (int) timeDiff / (60 * 1000);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.route_selection_row, parent, false);
        }

        TextView tripBusTime = (TextView) convertView.findViewById(R.id.busTime);
        TextView tripDuration = (TextView) convertView.findViewById(R.id.duration);
        View tripLine2 = convertView.findViewById(R.id.line2);

        tripBusTime.setText(TIME_FORMAT.format(startTime));
        tripDuration.setText(duration + " min");

        if(route.getTrips().size() > 1) {
            tripLine2.setBackgroundResource(R.color.two_chainz_gold);
            TextView tripLineName1 = (TextView) convertView.findViewById(R.id.lineName1);
            TextView tripLineName2 = (TextView) convertView.findViewById(R.id.lineName2);
            tripLineName1.setText(route.getTrips().get(0).getLineName());
            tripLineName2.setText(route.getTrips().get(route.getTrips().size() - 1).getLineName());
            tripLineName1.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
            tripLineName2.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
        } else {
            TextView tripLineName = (TextView) convertView.findViewById(R.id.lineName);
            tripLineName.setText(route.getTrips().get(0).getLineName());
            tripLineName.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
        }

        tripBusTime.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
        tripDuration.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));

        return convertView;
    }

}