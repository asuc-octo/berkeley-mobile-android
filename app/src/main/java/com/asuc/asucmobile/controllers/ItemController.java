package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Item;
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

public class ItemController implements Controller {

    private static final String URL = BASE_URL + "/resources";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static ItemController instance;

    private ArrayList<Item> items;
    private Callback callback;

    private Item currentItem;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new ItemController();
        }
        return instance;
    }

    private ItemController() {
        items = new ArrayList<>();
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

        items.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject itemJSON = array.getJSONObject(i);
                        int id = itemJSON.getInt("id");
                        String name = itemJSON.getString("name");
                        String location = itemJSON.getString("campus_location");
                        String phone = itemJSON.getString("phone_number");
                        long tmpDate;
                        Date opening;
                        Date closing;
                        JSONArray weeklyOpenArray =
                                itemJSON.getJSONArray("weekly_opening_times");
                        JSONArray weeklyCloseArray =
                                itemJSON.getJSONArray("weekly_closing_times");
                        String openingString;
                        String closingString;
                        Date[] weeklyOpen = new Date[7];
                        Date[] weeklyClose = new Date[7];
                        for (int j=0; j < weeklyOpenArray.length(); j++) {
                            openingString = weeklyOpenArray.getString(j);
                            opening = null;
                            if (!openingString.equals("null")) {
                                tmpDate = DATE_FORMAT.parse(openingString).getTime();
                                opening = new Date(tmpDate + PST.getOffset(tmpDate));
                            }
                            weeklyOpen[j] = opening;
                        }
                        for (int j=0; j < weeklyCloseArray.length(); j++) {
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
                        if (!itemJSON.getString("latitude").equals("null") &&
                                !itemJSON.getString("longitude").equals("null")) {
                            lat = itemJSON.getDouble("latitude");
                            lng = itemJSON.getDouble("longitude");
                        } else {
                            lat = Item.INVALID_COORD;
                            lng = Item.INVALID_COORD;
                        }
                        JSONArray weeklyAppointmentArray =
                                itemJSON.getJSONArray("weekly_by_appointment");
                        boolean[] weeklyAppointments = new boolean[7];
                        for (int j=0; j < weeklyAppointmentArray.length(); j++) {
                            weeklyAppointments[j] = weeklyAppointmentArray.getBoolean(j);
                        }
                        boolean byAppointment = weeklyAppointments[0];
                        Calendar c = Calendar.getInstance();
                        Date d = DATE_FORMAT.parse(itemJSON.getString("updated_at"));
                        c.setTime(d);
                        int weekday = c.get(Calendar.DAY_OF_WEEK);
                        items.add(new Item(id, name, location, phone, weeklyOpen[0],
                                weeklyClose[0], weeklyOpen, weeklyClose, lat, lng, byAppointment,
                                weeklyAppointments, weekday));
                    }

                    // Sort the items alphabetically
                    Collections.sort(items);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(items);
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
        JSONUtilities.readJSONFromUrl(context, URL, "items", ItemController.getInstance());
    }

    public void setCurrentItem(Item item) {
        currentItem = item;
    }

    public Item getCurrentItem() {
        return currentItem;
    }

}

