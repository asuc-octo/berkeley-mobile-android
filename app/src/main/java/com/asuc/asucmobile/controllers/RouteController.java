package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Route;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.models.Trip;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RouteController implements Controller {

    private static final String URL = BASE_URL + "/bt_trips";
    //TODO: make use sdf.setTimeZone for UTC
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static final SimpleDateFormat RECEIVE_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    private static RouteController instance;

    private LatLng start;
    private LatLng dest;
    private Date departure;
    private ArrayList<Route> routes;
    private Callback callback;
    private Route currentRoute;
    private LineController lineController;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new RouteController(new LatLng(0, 0), new LatLng(0, 0), new Date());
        }
        return instance;
    }

    public static Controller createInstance(LatLng start, LatLng dest, Date departure) {
        instance = new RouteController(start, dest, departure);
        return instance;
    }

    private RouteController(LatLng start, LatLng dest, Date departure) {
        this.start = start;
        this.dest = dest;
        this.departure = departure;
        routes = new ArrayList<>();
        RECEIVE_DATE_FORMAT.setTimeZone((TimeZone.getTimeZone("UTC")));
    }

    public Route createNewItem(JSONObject routeJSON, Context context) throws Exception {
        JSONArray tripListJSON = routeJSON.getJSONArray("trip_list");
        ArrayList<Trip> trips = new ArrayList<>();

        // Iterate through all Trips in a route.
        for (int j = 0; j < tripListJSON.length(); j++) {
            JSONObject tripJSON = tripListJSON.getJSONObject(j);

            // Getting the start and end times of each Trip.
            Long tmpTime = DATE_FORMAT.parse(tripJSON.getString("departure_time")).getTime();
            Date startTime = new Date(tmpTime + PST.getOffset(tmpTime));
            tmpTime = DATE_FORMAT.parse(tripJSON.getString("arrival_time")).getTime();
            Date endTime = new Date(tmpTime + PST.getOffset(tmpTime));
            String lineName = tripJSON.getString("line_name");
            int startId = tripJSON.getJSONObject("starting_stop").getInt("id");
            String startName = tripJSON.getJSONObject("starting_stop").getString("name");
            int endId = tripJSON.getJSONObject("destination_stop").getInt("id");
            String endName = tripJSON.getJSONObject("destination_stop").getString("name");
            Stop startStop = lineController.getStop(startId, startName);
            Stop endStop = lineController.getStop(endId, endName);
            int lineId = tripJSON.getInt("line_id");
            ArrayList<Stop> lineStops = lineController.getLine(lineId, lineName).getStops();

            // Getting a sub-sequence of Stops in a Trip.
            ArrayList<Stop> stops = new ArrayList<>();
            boolean isPastEnd = false;
            int index = lineStops.indexOf(startStop);
            while (!isPastEnd) {
                Stop stop = lineStops.get(index);
                stops.add(stop);
                if (stop == endStop) {
                    isPastEnd = true;
                } else if (stops.size() > lineStops.size()) {
                    // We have a problem!
                    throw new Exception();
                }
                index = (index + 1) % lineStops.size();
            }
            trips.add(new Trip(startTime, endTime, stops, lineName));
        }
        return new Route(trips);
    }

    @Override
    public void setResources(@NonNull final Context context, final JSONArray array) {
        if (array == null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onRetrievalFailed();
                }
            });
            return;
        }
        routes.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lineController = (LineController) LineController.getInstance();

                    // Iterate through all possible routes.
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject routeJSON = array.getJSONObject(i);
                        routes.add(createNewItem(routeJSON, context));
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(routes);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onRetrievalFailed();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void refreshInBackground(@NonNull Context context, Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl(context, URL + "/new?" + "startlat=" + start.latitude +
                "&startlon=" + start.longitude + "&destlat=" + dest.latitude + "&destlon=" +
                dest.longitude + "&departuretime=" + RECEIVE_DATE_FORMAT.format(departure),
                "journeys", RouteController.getInstance());
    }

    public void setItem(@NonNull final Context context, final JSONObject obj) {
        try {
            setCurrentRoute(createNewItem(obj, context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentRoute(Route route) {
        this.currentRoute = route;
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

}
