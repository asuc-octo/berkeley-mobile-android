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

    private static final String URL = BASE_URL + "/search_items";
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

    public Item createNewItem(JSONObject itemJSON, Context context) throws Exception {
        String name = itemJSON.getString("name");
        String category = itemJSON.getString("category");
        String query = itemJSON.getString("query");
        return new Item(name, category, query);
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
                        items.add(createNewItem(itemJSON, context));
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
        JSONUtilities.readJSONFromUrl(context, URL, "search_list", ItemController.getInstance());
    }

    public void setItem(@NonNull final Context context, final JSONObject obj) {
        try {
            setCurrentItem(createNewItem(obj, context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setItemFromUrl(@NonNull Context context, String ItemURL, String name, Controller controller) {
        JSONUtilities.setObjectFromUrl(context, ItemURL, name, controller);
    }

    public void setCurrentItem(Item item) {
        currentItem = item;
    }

    public Item getCurrentItem() {
        return currentItem;
    }
}

