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
import com.asuc.asucmobile.models.Stop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RouteAdapter extends BaseAdapter {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm a");

    private Context context;
    private Route route;
    private ArrayList<Stop> stops;
    private Typeface typeface;

    public RouteAdapter(Context context, Route route) {
        this.context = context;
        this.route = route;
        this.stops = route.getStopsAndTransfers();
        this.typeface = Typeface.createFromAsset(context.getAssets(), "young.ttf");
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
    public View getView(int i, View convertView, ViewGroup parent) {
        Stop stop = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.route_row, parent, false);
        }

        TextView line = (TextView) convertView.findViewById(R.id.line);
        TextView endpoint = (TextView) convertView.findViewById(R.id.endpoint);
        ImageView stopIcon = (ImageView) convertView.findViewById(R.id.lollipop);
        TextView stopName = (TextView) convertView.findViewById(R.id.stop_name);

        line.setTypeface(typeface);
        endpoint.setTypeface(typeface);
        stopName.setTypeface(typeface);

        if (stop.isTransfer()) {
            line.setVisibility(View.VISIBLE);

            endpoint.setVisibility(View.INVISIBLE);
            stopIcon.setVisibility(View.INVISIBLE);
            stopName.setVisibility(View.INVISIBLE);

            if (stop.getStartTime() != null) {
                if (i == 0) {
                    line.setText(stop.getName());
                    line.setBackgroundColor(context.getResources().getColor(R.color.ASUC_blue));
                } else {
                    line.setText("TRANSFER: " + stop.getName());
                    line.setBackgroundColor(context.getResources().getColor(R.color.two_chainz_gold));
                }
            }
        } else {
            line.setVisibility(View.INVISIBLE);

            endpoint.setVisibility(View.VISIBLE);
            stopIcon.setVisibility(View.VISIBLE);
            stopName.setVisibility(View.VISIBLE);

            // Set the left-hand route icons and endpoints
            if (getItem(i - 1).isTransfer()) {
                endpoint.setText("Start\n" + TIME_FORMAT.format(getItem(i - 1).getStartTime()));

                if (i > route.getTrips().get(0).getStops().size()) {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_start_yellow));
                } else {
                    stopIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.route_start_blue));
                }
            } else if (getItem(i + 1).isTransfer()) {
                endpoint.setText("Arrive\n" + TIME_FORMAT.format(getItem(i + 1).getPreviousArrival()));

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
                stopName.setTextColor(context.getResources().getColor(R.color.ASUC_blue));
            }
        }

        return convertView;
    }

}
