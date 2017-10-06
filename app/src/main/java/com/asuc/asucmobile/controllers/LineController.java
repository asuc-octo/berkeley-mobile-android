package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.asuc.asucmobile.models.Line;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LineController implements Controller {

    private static final String URL = BASE_URL + "/bt_lines";

    private static LineController instance;

    // SparseArray is an Android HashMap that accepts primitive integers as keys.
    private SparseArray<Stop> stops;
    private SparseArray<Line> lines;
    private Callback callback;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new LineController();
        }
        return instance;
    }

    private LineController() {
        stops = new SparseArray<>();
        lines = new SparseArray<>();
    }

    public Line createNewItem(JSONObject lineJSON, Context context) throws Exception {
        int id = lineJSON.getInt("id");
        String name = lineJSON.getString("name");
        JSONArray stopsJSON = lineJSON.getJSONArray("stop_list");
        ArrayList<Stop> lineStops = new ArrayList<>();

        // Add all the stops if needed.
        for (int j = 0; j < stopsJSON.length(); j++) {
            JSONObject stopJSON = stopsJSON.getJSONObject(j);
            int stopId = stopJSON.getInt("id");
            if (stops.get(stopId) == null) {
                String stopName = stopJSON.getString("name");
                Double latitude = stopJSON.getDouble("latitude");
                Double longitude = stopJSON.getDouble("longitude");
                Stop stop = new Stop(stopId, stopName, new LatLng(latitude, longitude));
                stops.put(stopId, stop);
                lineStops.add(stop);
            } else {
                lineStops.add(stops.get(stopId));
            }
        }
        return new Line(id, name, lineStops);
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
        lines.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject lineJSON = array.getJSONObject(i);
                        int id = lineJSON.getInt("id");
                        lines.put(id, createNewItem(lineJSON, context));
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(stops);
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
        JSONUtilities.readJSONFromUrl(context, URL, "lines", LineController.getInstance());
    }

    Stop getStop(int id, String name) {
        Stop stop = stops.get(id);
        if (stop == null) {
            for (int i = 0; i < stops.size(); i++) {
                if (stops.valueAt(i).getName().equals(name)) {
                    return stops.valueAt(i);
                }
            }
        }
        return stop;
    }

    Line getLine(int id, String name) {
        Line line = lines.get(id);
        if (line == null) {
            for (int i = 0; i < lines.size(); i++) {
                if (lines.valueAt(i).getName().equals(name)) {
                    return lines.valueAt(i);
                }
            }
        }
        return line;
    }

    public void setItem(@NonNull final Context context, final JSONObject obj) {
        //Not required for this controller.
    }

}
