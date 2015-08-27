package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;

import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LibraryController implements Controller {

    private static final String URL = BASE_URL + "/libraries";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static LibraryController instance;

    private Context context;
    private ArrayList<Library> libraries;
    private Callback callback;

    private Library currentLibrary;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new LibraryController();
        }

        instance.context = context;

        return instance;
    }

    public LibraryController() {
        libraries = new ArrayList<Library>();
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

        libraries.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject libraryJSON = array.getJSONObject(i);

                        int id = libraryJSON.getInt("id");
                        String name = libraryJSON.getString("name");
                        String location = libraryJSON.getString("campus_location");
                        String phone = libraryJSON.getString("phone_number");

                        long tmpDate;

                        Date opening = null;
                        Date closing = null;
                        String openingString = libraryJSON.getString("opening_time_today");
                        String closingString = libraryJSON.getString("closing_time_today");

                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            opening = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            closing = new Date(tmpDate + PST.getOffset(tmpDate));
                        }

                        String imageUrl = libraryJSON.getString("image_link");
                        double lat = libraryJSON.getDouble("latitude");
                        double lng = libraryJSON.getDouble("longitude");

                        boolean byAppointment = libraryJSON.getBoolean("by_appointment");

                        libraries.add(
                                new Library(id, name, location, phone, opening, closing, imageUrl, lat, lng, byAppointment));
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(libraries);
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
        JSONUtilities.readJSONFromUrl("http://asuc-mobile.herokuapp.com/api/libraries", "libraries", LibraryController.getInstance(context));
    }

    public void setCurrentLibrary(Library library) {
        currentLibrary = library;
    }

    public Library getCurrentLibrary() {
        return currentLibrary;
    }

}

