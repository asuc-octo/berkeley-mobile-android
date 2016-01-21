package com.asuc.asucmobile.controllers;


import android.app.Activity;
import android.content.Context;

import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StopController implements Controller {

    private static final String URL = BASE_URL + "/bt_stops";

    private static StopController instance;

    private Context context;
    private ArrayList<Stop> stops;
    private Callback callback;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new StopController();
        }
        instance.context = context;
        return instance;
    }

    public StopController() {
        stops = new ArrayList<>();
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
                        int stopId = stopJSON.getInt("id");

                        String stopName = stopJSON.getString("name");
                        Double latitude = stopJSON.getDouble("latitude");
                        Double longitude = stopJSON.getDouble("longitude");

                        Stop stop = new Stop(stopId, stopName, new LatLng(latitude, longitude));

                        stops.add(stop);
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
        JSONUtilities.readJSONFromUrl(URL, "stops", StopController.getInstance(context));
    }


}
