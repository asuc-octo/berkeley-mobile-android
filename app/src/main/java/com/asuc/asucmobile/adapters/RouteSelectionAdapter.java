package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Journey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.security.AccessController.getContext;

/**
 * Created by alexthomas on 10/12/17.
 */

public class RouteSelectionAdapter extends BaseAdapter {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private Context context;
    private ArrayList<Journey> routes;

    public RouteSelectionAdapter(Context context, ArrayList<Journey> routes) {
        this.context = context;
        this.routes = routes;
    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Journey getItem(int i) {
        return routes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View convertView, ViewGroup parent) {
        final Journey route = getItem(i);
        Date startTime = route.getTrips().get(0).getDepartureTime();
        Date endTime = route.getTrips().get(route.getTrips().size() - 1).getArrivalTime();
        long timeDiff = endTime.getTime() - startTime.getTime();
        int duration = (int) timeDiff / (60 * 1000);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.route_selection_row, parent, false);
        }


        //TextView tripBusTime = (TextView) convertView.findViewById(R.id.busTime);
        TextView tripDuration = (TextView) convertView.findViewById(R.id.duration);
        //View tripLine2 = convertView.findViewById(R.id.line2);

        //tripBusTime.setText(TIME_FORMAT.format(startTime));
        tripDuration.setText(String.format("%d min", duration));

        TextView startingStopName = (TextView) convertView.findViewById(R.id.startingStop);
        TextView tripLineName = (TextView) convertView.findViewById(R.id.lineName);
        tripLineName.setText(route.getTrips().get(0).getLineName());
        startingStopName.setText(route.getTrips().get(0).getStops().get(0).getName());


        return convertView;
    }
}
