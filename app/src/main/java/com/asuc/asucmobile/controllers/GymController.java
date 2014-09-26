package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;

import com.asuc.asucmobile.models.Gym;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GymController implements Controller {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    private static GymController instance;

    private Context context;
    private ArrayList<Gym> gyms;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new GymController();
        }

        instance.context = context;

        return instance;
    }

    public GymController() {
        gyms = new ArrayList<Gym>();
    }
    
    @Override
    public void setResources(final JSONArray array) {
        gyms.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject gymJSON = array.getJSONObject(i);

                        String id = gymJSON.getString("id");
                        String name = gymJSON.getString("name");
                        String address = gymJSON.getString("address");

                        Date opening = null;
                        Date closing = null;
                        String openingString = gymJSON.getString("opening_time_today");
                        String closingString = gymJSON.getString("closing_time_today");

                        if (!openingString.equals("null")) {
                            opening = DATE_FORMAT.parse(openingString);
                        }
                        if (!closingString.equals("null")) {
                            closing = DATE_FORMAT.parse(closingString);
                        }

                        gyms.add(new Gym(id, name, address, opening, closing));
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateUI() {
    }

}
