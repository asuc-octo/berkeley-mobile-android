package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GymController implements Controller {

    private static final String URL = BASE_URL + "/gyms";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static GymController instance;

    private ArrayList<Gym> gyms;
    private Callback callback;
    private Gym currentGym;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new GymController();
        }
        return instance;
    }

    private GymController() {
        gyms = new ArrayList<>();
    }

    public Gym createNewItem(JSONObject gymJSON, Context context) throws Exception {
        int id = gymJSON.getInt("id");
        String name = gymJSON.getString("name");
        String address = gymJSON.getString("address");
        long tmpDate;
        Date opening = null;
        Date closing = null;
        String openingString = gymJSON.getString("opening_time_today");
        String closingString = gymJSON.getString("closing_time_today");
        if (!openingString.equals("null")) {
            tmpDate = DATE_FORMAT.parse(openingString).getTime();
            opening = new Date(tmpDate + PST.getOffset(tmpDate));
        }
        if (!closingString.equals("null")) {
            tmpDate = DATE_FORMAT.parse(closingString).getTime();
            closing = new Date(tmpDate + PST.getOffset(tmpDate));
        }
        String imageUrl = gymJSON.getString("image_link");

        return new Gym(id, name, address, opening, closing, imageUrl);
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

                        gyms.add(createNewItem(gymJSON, context));
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(gyms);
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
        JSONUtilities.readJSONFromUrl(context, URL, "gyms", GymController.getInstance());
    }

    public void setItem(@NonNull final Context context, final JSONObject obj) {
        try {
            setCurrentGym(createNewItem(obj, context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentGym(Gym gym) {
        currentGym = gym;
    }

    public Gym getCurrentGym() {
        return currentGym;
    }

}
