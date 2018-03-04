package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.models.GymClass;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GymClassController implements Controller {

    private static final String URL = BASE_URL + "/group_exs";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static GymClassController instance;

    private ArrayList<GymClass> classes;
    private GymClass currentClass;
    private Callback callback;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new GymClassController();
        }
        return instance;
    }

    private GymClassController() {
        classes = new ArrayList<>();
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
        classes.clear();

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

                        /*int id = gymJSON.getInt("id");

                        String name = gymJSON.getString("name");
                        String description = gymJSON.getString("description");
                        String trainer = gymJSON.getString("trainer");
                        int classType = GymClass.ALL_AROUND;
                        switch (gymJSON.getString("classType")) {
                            case "ALL-AROUND WORKOUT":
                                classType = GymClass.ALL_AROUND; break;
                            case "CARDIO":
                                classType = GymClass.CARDIO; break;
                            case "MIND":
                                classType = GymClass.MIND; break;
                            case "CORE":
                                classType = GymClass.CORE; break;
                            case "DANCE":
                                classType = GymClass.DANCE; break;
                            case "STRENGTH":
                                classType = GymClass.STRENGTH; break;
                            case "AQUA":
                                classType = GymClass.AQUA; break;

                        }
                        String location = gymJSON.getString("location");

                        long tmpDate;
                        Date date = null;
                        Date startTime = null;
                        Date endTime = null;

                        String dateString = gymJSON.getString("date");
                        String openingString = gymJSON.getString("startTime");
                        String closingString = gymJSON.getString("endTime");
                        if (!dateString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(dateString).getTime();
                            date = new Date(dateString);
                        }
                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            startTime = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            endTime = new Date(tmpDate + PST.getOffset(tmpDate));
                        }

                        classes.add(new GymClass(id, date, startTime, endTime, name, description, trainer, classType, location));*/
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(classes);
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
        JSONUtilities.readJSONFromUrl(context, URL, "group_exs", GymClassController.getInstance());
    }

    public void setCurrentGym(GymClass currentClass) {
        this.currentClass = currentClass;
    }

    public GymClass getCurrentClass() {
        return currentClass;
    }

}
