package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Library;
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

public class LibraryController implements Controller {

    private static final String URL = BASE_URL + "/weekly_libraries";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static LibraryController instance;

    private ArrayList<Library> libraries;
    private Callback callback;

    private Library currentLibrary;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new LibraryController();
        }
        return instance;
    }

    private LibraryController() {
        libraries = new ArrayList<>();
    }

    public Library createNewItem(JSONObject libraryJSON, Context context) throws Exception {
        int id = libraryJSON.getInt("id");
        String name = libraryJSON.getString("name");
        String location = libraryJSON.getString("campus_location");
        String phone = libraryJSON.getString("phone_number");
        long tmpDate;
        Date opening;
        Date closing;
        JSONArray weeklyOpenArray =
                libraryJSON.getJSONArray("weekly_opening_times");
        JSONArray weeklyCloseArray =
                libraryJSON.getJSONArray("weekly_closing_times");
        String openingString;
        String closingString;
        Date[] weeklyOpen = new Date[7];
        Date[] weeklyClose = new Date[7];
        for (int j = 0; j < weeklyOpenArray.length(); j++) {
            openingString = weeklyOpenArray.getString(j);
            opening = null;
            if (!openingString.equals("null")) {
                tmpDate = DATE_FORMAT.parse(openingString).getTime();
                opening = new Date(tmpDate + PST.getOffset(tmpDate));
            }
            weeklyOpen[j] = opening;
        }
        for (int j = 0; j < weeklyCloseArray.length(); j++) {
            closingString = weeklyCloseArray.getString(j);
            closing = null;
            if (!closingString.equals("null")) {
                tmpDate = DATE_FORMAT.parse(closingString).getTime();
                closing = new Date(tmpDate + PST.getOffset(tmpDate));
            }
            weeklyClose[j] = closing;
        }
        double lat;
        double lng;
        if (!libraryJSON.getString("latitude").equals("null") &&
                !libraryJSON.getString("longitude").equals("null")) {
            lat = libraryJSON.getDouble("latitude");
            lng = libraryJSON.getDouble("longitude");
        } else {
            lat = Library.INVALID_COORD;
            lng = Library.INVALID_COORD;
        }
        JSONArray weeklyAppointmentArray =
                libraryJSON.getJSONArray("weekly_by_appointment");
        boolean[] weeklyAppointments = new boolean[7];
        for (int j = 0; j < weeklyAppointmentArray.length(); j++) {
            weeklyAppointments[j] = weeklyAppointmentArray.getBoolean(j);
        }
        boolean byAppointment = weeklyAppointments[0];
        Calendar c = Calendar.getInstance();
        Date d = DATE_FORMAT.parse(libraryJSON.getString("updated_at"));
        c.setTime(d);
        int weekday = c.get(Calendar.DAY_OF_WEEK);

        return new Library(id, name, location, phone, weeklyOpen[0],
                weeklyClose[0], weeklyOpen, weeklyClose, lat, lng, byAppointment,
                weeklyAppointments, weekday);
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
                        libraries.add(createNewItem(libraryJSON, context));
                    }

                    // Sort the libraries alphabetically, putting favorites at top
                    Collections.sort(libraries, CustomComparators.FacilityComparators.getSortByFavoriteLibrary(context));
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

    public void setItem(@NonNull final Context context, final JSONObject obj) {
        try {
            setCurrentLibrary(createNewItem(obj, context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshInBackground(@NonNull Context context, Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl(context, URL, "libraries", LibraryController.getInstance());
    }

    public void setCurrentLibrary(Library library) {
        currentLibrary = library;
    }

    public Library getCurrentLibrary() {
        return currentLibrary;
    }

}

