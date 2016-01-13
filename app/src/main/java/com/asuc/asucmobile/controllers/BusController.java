package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.asuc.asucmobile.models.Bus;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class BusController implements Controller{

    private static final String URL = BASE_URL + "/bt_buses";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static BusController instance;
    private Context context;
    private Callback callback;

    private ArrayList<Bus> buses;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new BusController();
        }

        instance.context = context;

        return instance;
    }

    public BusController() {
        buses = new ArrayList<>();
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
        buses.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Iterate through buses
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject bus = array.getJSONObject(i);
                        boolean inService = bus.getBoolean("in_service");
//                        if (!inService) {
//                            continue;
//                        }
                        int id = bus.getInt("id");
                        LatLng location = new LatLng(bus.getDouble("latitude"), bus.getDouble("longitude"));
                        Log.d("buses", id + location.toString());
                        // FIXME: 1/8/2016
//                        int lineId = bus.getInt("line_id");
//                        double speed = bus.getDouble("speed");
//                        double bearing = bus.getDouble("bearing");
//                        int vehicleId = bus.getInt("vehicle_id");
                        Bus newBus = new Bus(id, location, 0, 0, 0, 0, inService);
                        buses.add(newBus);
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(buses);
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
        JSONUtilities.readJSONFromUrl(URL, "buses", BusController.getInstance(context));
    }

    public ArrayList<Bus> getBuses() {
        return buses;
    }
}
