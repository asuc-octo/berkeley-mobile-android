package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;

import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

public class StopController implements Controller {

    private static StopController instance;

    // SparseArray is an Android HashMap that accepts primitive integers as keys.
    private Context context;
    private SparseArray<Stop> stops;
    private Callback callback;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new StopController();
        }

        instance.context = context;

        return instance;
    }

    public StopController() {
        stops = new SparseArray<Stop>();
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

        stops.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject stopJSON = array.getJSONObject(i);

                        int id = stopJSON.getInt("id");
                        String name = stopJSON.getString("name");
                        Double latitude = stopJSON.getDouble("latitude");
                        Double longitude = stopJSON.getDouble("longitude");

                        stops.put(id, new Stop(id, name, new LatLng(latitude, longitude)));
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
        JSONUtilities.readJSONFromUrl("http://asuc-mobile.herokuapp.com/api/bt_stops", "stops", StopController.getInstance(context));
    }

    public SparseArray<Stop> getStops() {
        return stops;
    }

    public Stop getStop(int id) {
        return stops.get(id);
    }

}
