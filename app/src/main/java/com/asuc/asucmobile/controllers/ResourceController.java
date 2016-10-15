package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;

import com.asuc.asucmobile.models.Resource;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ResourceController implements Controller {

    private static final String URL = BASE_URL + "/resources";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static ResourceController instance;

    private Context context;
    private ArrayList<Resource> resources;
    private Callback callback;

    private Resource currentResource;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new ResourceController();
        }

        instance.context = context;

        return instance;
    }

    public ResourceController() {
        resources = new ArrayList<>();
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

        resources.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject resourceJSON = array.getJSONObject(i);

                        System.out.println(resourceJSON);

                        String resource = resourceJSON.getString("Resource");
                        String topic = resourceJSON.getString("Topic");
                        String phone1 = resourceJSON.getString("Phone 1");
                        String phone2 = resourceJSON.getString("Phone 2 (Optional)");
                        String email = resourceJSON.getString("Email");
                        String location = resourceJSON.getString("Office Location");
                        String hours = resourceJSON.getString("Hours");
                        String onOrOffCampus = resourceJSON.getString("On/Off Campus");

                        double lat;
                        double lng;

                        /*
                            Can we get some consistency on the null values please, I can't for the
                            life of me figure out how to match a string to "N/A" because nothing
                            actually does. "N/A" and "N\/A" both don't match up to the N/A given
                            from the JSON... so for now I'm just doing a catch-all solution.
                        */
                        try {
                            lat = resourceJSON.getDouble("Latitude");
                            lng = resourceJSON.getDouble("Longitude");
                        } catch (org.json.JSONException j) {
                            /*
                            Replace with the lat/long of Sproul Plaza, I guess.
                            Right now this lat/long is whatever Bing says UC Berkeley is at.
                            Notably this code allows resources to open but some resources have
                                null lat/longs (this is carried over from libraries).
                             */
                            lat = 37.87;
                            lng = -122.259;
                        }

                        String notes = resourceJSON.getString("Notes");

                        resources.add(
                                new Resource(resource, topic, phone1, phone2, email, location, hours, onOrOffCampus, lat, lng, notes));
                    }

                    // Sort the resources alphabetically, putting favorites at top
                    Collections.sort(resources, CustomComparators.FacilityComparators.getSortByFavoriteResource(context));

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(resources);
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
        JSONUtilities.readJSONFromUrl(URL, "resources", ResourceController.getInstance(context));
    }

    public void setCurrentResource(Resource resource) {
        currentResource = resource;
    }

    public Resource getCurrentResource() {
        return currentResource;
    }

}

