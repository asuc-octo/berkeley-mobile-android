package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;

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

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static RouteController instance;

    private LatLng start;
    private LatLng dest;

    private Context context;
    private ArrayList<Route> routes;
    private Callback callback;

    private Route currentRoute;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new RouteController(new LatLng(0, 0), new LatLng(0, 0));
        }

        instance.context = context;

        return instance;
    }

    public static Controller createInstance(Context context, LatLng start, LatLng dest) {
        instance = new RouteController(start, dest);
        instance.context = context;

        return instance;
    }

    public RouteController(LatLng start, LatLng dest) {
        this.start = start;
        this.dest = dest;

        routes = new ArrayList<>();
    }

    @Override
    public void setResources(final JSONArray array) {
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
                    LineController lineController = (LineController) LineController.getInstance(context);

                    // Iterate through all possible routes.
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject routeJSON = array.getJSONObject(i);
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

                        routes.add(new Route(trips));
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
    public void refreshInBackground(Callback callback) {
        this.callback = callback;

        JSONUtilities.readJSONFromUrl(
                "http://asuc-mobile.herokuapp.com/api/bt_trips/new?" +
                        "startlat=" + start.latitude + "&startlon=" + start.longitude +
                        "&destlat=" + dest.latitude + "&destlon=" + dest.longitude,
                "journeys",
                RouteController.getInstance(context));
    }

    public void setCurrentRoute(Route route) {
        this.currentRoute = route;
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

}
