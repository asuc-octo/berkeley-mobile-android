package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;


import com.asuc.asucmobile.models.GSONContainer;
import com.asuc.asucmobile.models.Journey;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.models.Trip;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by alexthomas on 6/14/17.
 */

public class RouteController implements Controller {

    private static final String URL = BASE_URL;
    private LatLng origin, destination;
    private static RouteController instance;
    private Callback callback;
    private double startlat, startlon, destlat, destlon;
    private String departuretime;
    private Long millis;
    private final Gson gson = new Gson();
    private HashMap<String, Collection<GSONContainer>> BRC;
    private Type testType;
    private LineController lineController;
    private static final java.text.SimpleDateFormat DATE_FORMAT =
            new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final java.util.TimeZone PST = java.util.TimeZone.getTimeZone("America/Los_Angeles");
    private static final java.text.SimpleDateFormat RECEIVE_DATE_FORMAT =
            new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private ArrayList<Journey> routeList;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Controller getInstance() {
        if (instance == null) {
            instance = new RouteController(null, null, null);
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static RouteController createInstance(LatLng origin, LatLng destination, Long millis) {
        instance = new RouteController(origin, destination, millis);
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public RouteController(LatLng origin, LatLng destination, Long millis) {
        this.origin = origin;
        this.destination = destination;
        this.startlat = origin.latitude;
        this.startlon = origin.longitude;
        this.destlat = destination.latitude;
        this.destlon = destination.longitude;
        this.millis = millis;
        this.departuretime = convertMillisToUTC(millis);
        routeList = new ArrayList<>();
        testType = new TypeToken<HashMap<String, Collection<GSONContainer>>>() {
        }.getType();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String convertMillisToUTC(Long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(millis));
    }

    public Journey createNewItem(JSONObject routeJSON, Context context) throws Exception {

        JSONArray tripListJSON = routeJSON.getJSONArray("trip_list");
        ArrayList<Trip> routeTrips = new ArrayList<>();

        for (int j = 0; j < tripListJSON.length(); j++) {
            JSONObject tripJSON = tripListJSON.getJSONObject(j);

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
                if (stop.equals(endStop)) {
                    isPastEnd = true;
                } else if (stops.size() > lineStops.size()) {
                    // We have a problem!
                    throw new Exception();
                }
                index = (index + 1) % lineStops.size();
            }
            routeTrips.add(new Trip(startTime, endTime, stops, lineName));

        }
        return new Journey(routeTrips);


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

        new Thread(new Runnable() {
            @Override
            public void run() {
                lineController = (LineController) LineController.getInstance();
                try {
                    // Iterate through all possible routes.
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject routeJSON = array.getJSONObject(i);
                        routeList.add(createNewItem(routeJSON, context));
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(routeList);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void refreshInBackground(@NonNull Context context, Callback callback) {
        this.callback = callback;
        String URL_add = "/bt_trips/new?" + "startlat=" + startlat + "&startlon="
                + startlon + "&destlat="
                + destlat + "&destlon=" + destlon + "&departuretime=" + departuretime + "=";
        JSONUtilities.readJSONFromUrl(context, URL + URL_add, "journeys", RouteController.getInstance());
    }
}
