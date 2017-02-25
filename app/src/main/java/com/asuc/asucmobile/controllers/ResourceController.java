package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Resource;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ResourceController implements Controller {

    private static final String URL = BASE_URL + "/resources";

    private static ResourceController instance;

    private ArrayList<Resource> resources;
    private Callback callback;
    private Resource currentResource;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new ResourceController();
        }
        return instance;
    }

    public Resource createNewItem(JSONObject resourceJSON, Context context) throws Exception {
        String resource = resourceJSON.getString("Resource");
        String topic = resourceJSON.getString("Topic");
        String phone1 = resourceJSON.getString("Phone 1");
        String phone2 = resourceJSON.getString("Phone 2 (Optional)");
        String location = resourceJSON.getString("Office Location");
        String hours = resourceJSON.getString("Hours");
        String email = resourceJSON.getString("Email");
        String onOrOffCampus = resourceJSON.getString("On/Off Campus");
        double lat;
        double lng;
        try {
            lat = resourceJSON.getDouble("Latitude");
            lng = resourceJSON.getDouble("Longitude");
        } catch (org.json.JSONException j) {
            // Insert an invalid Lat and Lng.
            lat = Resource.INVALID_COORD;
            lng = Resource.INVALID_COORD;
        }
        String notes = resourceJSON.getString("Notes");
        return new Resource(resource, topic, phone1, phone2, location, hours,
                email, onOrOffCampus, lat, lng, notes);
    }

    private ResourceController() {
        resources = new ArrayList<>();
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
                        resources.add(createNewItem(resourceJSON, context));
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
    public void refreshInBackground(@NonNull Context context, Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl(context, URL, "resources", ResourceController.getInstance());
    }

    public void setItem(@NonNull final Context context, final JSONObject obj) {
        if (obj == null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onRetrievalFailed();
                }
            });
            return;
        }

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setCurrentResource(createNewItem(obj, context));
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

    public void setCurrentResource(Resource resource) {
        currentResource = resource;
    }

    public Resource getCurrentResource() {
        return currentResource;
    }

}

