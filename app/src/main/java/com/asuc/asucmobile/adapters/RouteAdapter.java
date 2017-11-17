package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Journey;
import com.asuc.asucmobile.models.Stop;
import com.google.android.gms.vision.text.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexthomas on 10/13/17.
 */

public class RouteAdapter extends BaseAdapter {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private Context context;
    private Journey route;
    private ArrayList<Stop> stops;
    private String duration;
    private int id;

    public RouteAdapter(Context context, Journey route) {
        this.context = context;
        this.route = route;
        this.stops = route.getStopsAndTransfers();

        Date startTime = route.getTrips().get(0).getDepartureTime();
        Date endTime = route.getTrips().get(route.getTrips().size() - 1).getArrivalTime();
        long timeDiff = endTime.getTime() - startTime.getTime();
        int x = (int) timeDiff / (60 * 1000);
        this.duration = String.format("%d min", x);
        this.id = stops.get(1).getId();
    }

    @Override
    public int getCount() {
        return stops.size() - 1;
    }

    @Override
    public Stop getItem(int i) {
        return stops.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getView(int i, View convertView, ViewGroup parent) {
        Stop stop = getItem(i);


        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.route_row, parent, false);
        }

        TextView line = (TextView) convertView.findViewById(R.id.lineName);
        TextView endpoint = (TextView) convertView.findViewById(R.id.endpoint);
        ImageView stopIcon = (ImageView) convertView.findViewById(R.id.lollipop);
        TextView stopName = (TextView) convertView.findViewById(R.id.stop_name);
        ImageView busIcon = (ImageView) convertView.findViewById(R.id.busIcon);
        TextView startingStop = (TextView) convertView.findViewById(R.id.startingStop);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);

        if (stop.isTransfer()) {
            line.setVisibility(View.VISIBLE);
            endpoint.setVisibility(View.INVISIBLE);
            stopIcon.setVisibility(View.INVISIBLE);
            stopName.setVisibility(View.INVISIBLE);


            if (stop.getStartTime() != null) {
                if (i == 0 & stop.getId() == 0) {
                    line.setText(stop.getName());
                    busIcon.setVisibility(View.VISIBLE);
                    startingStop.setText(getItem(1).getName());
                    duration.setText(getDuration());
                } else {
                   // stopIcon.setVisibility(View.VISIBLE);
                    // stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_start_yellow));

                }
            }
        } else {
            line.setVisibility(View.INVISIBLE);
            endpoint.setVisibility(View.VISIBLE);
            stopIcon.setVisibility(View.VISIBLE);
            stopName.setVisibility(View.VISIBLE);
            line.setText("");
            duration.setText("");
            busIcon.setVisibility(View.INVISIBLE);
            startingStop.setText("");


            // Set the left-hand route icons and endpoints
            if (getItem(i - 1).isTransfer()) {
                endpoint.setText(TIME_FORMAT.format(getItem(i - 1).getStartTime()));

                if (i > route.getTrips().get(0).getStops().size()) {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_start_yellow));
                } else {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_start_blue));
                }
            } else if (getItem(i + 1).isTransfer()) {
                endpoint.setText(TIME_FORMAT.format(getItem(i + 1).getPreviousArrival()));

                if (i > route.getTrips().get(0).getStops().size()) {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_end_yellow));
                } else {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_end_blue));
                }
            } else {
                endpoint.setText("");

                if (i > route.getTrips().get(0).getStops().size()) {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_mid_yellow));
                } else {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_mid_blue));
                }
            }

            stopName.setText(stop.getName());
            if (i > route.getTrips().get(0).getStops().size()) {
                stopName.setTextColor(context.getResources().getColor(R.color.two_chainz_gold));
            } else {
                stopName.setTextColor(context.getResources().getColor(R.color.hotline_blue));
            }
        }

        return convertView;
    }

    public String getDuration() {
        return duration;
    }
}
