package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
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

    private static LineController instance;

    // SparseArray is an Android HashMap that accepts primitive integers as keys.
    private Context context;
    private SparseArray<Stop> stops;
    private SparseArray<Line> lines;
    private Callback callback;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new LineController();
        }

        instance.context = context;

        return instance;
    }

    public LineController() {
        stops = new SparseArray<>();
        lines = new SparseArray<>();
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

                        lines.put(id, new Line(id, lineStops));
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
    public void refreshInBackground(Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl(
                "http://asuc-mobile.herokuapp.com/api/bt_lines",
                "lines",
                LineController.getInstance(context));
    }

    public SparseArray<Stop> getStops() {
        return stops;
    }

    public Stop getStop(int id) {
        return stops.get(id);
    }

    public Line getLine(int id) {
        return lines.get(id);
    }

}
