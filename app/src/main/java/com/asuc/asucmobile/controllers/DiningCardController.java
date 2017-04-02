package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.models.Card;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.asuc.asucmobile.utilities.JsonToObject;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class DiningCardController implements Controller {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private static String URL = BASE_URL+"/dining_halls";
    private static DiningCardController instance;
    private ArrayList<Card> cards;
    private Callback callback;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new DiningCardController();
        }
        return instance;
    }

    private DiningCardController() {
        cards = new ArrayList<>();
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        DiningHall dining = (DiningHall) JsonToObject.retrieve(array.getJSONObject(i), "dining_halls", context);
                        String menuOpen = null;
                        if (dining.isBreakfastOpen()) {
                            menuOpen = "Breakfast:" + HOURS_FORMAT.format(dining.getBreakfastOpening()) + "- "  + HOURS_FORMAT.format(dining.getBreakfastClosing()) ;
                        } else if (dining.isLunchOpen()) {
                            menuOpen = "Lunch:" + HOURS_FORMAT.format(dining.getLunchOpening()) + "- " + HOURS_FORMAT.format(dining.getLunchClosing());
                        } else if (dining.isDinnerOpen()) {
                            menuOpen = "Dinner: " + HOURS_FORMAT.format(dining.getDinnerOpening()) + "- " + HOURS_FORMAT.format(dining.getDinnerClosing());
                        } else if (dining.isLateNightOpen()) {
                            menuOpen = "Late Night: " + HOURS_FORMAT.format(dining.getLateNightOpening()) + "- " + HOURS_FORMAT.format(dining.getLateNightClosing());
                        }
                        if (menuOpen == null) {
                            if (dining.lateNightToday()) {
                                menuOpen = "Late Night: " + HOURS_FORMAT.format(dining.getLateNightOpening()) + "- " + HOURS_FORMAT.format(dining.getLateNightClosing());
                            } else {
                                menuOpen = "Dinner: " + HOURS_FORMAT.format(dining.getDinnerOpening()) + "- " + HOURS_FORMAT.format(dining.getDinnerClosing());
                            }
                        }
                        Card card = new Card(dining.getImageUrl(), dining.getName(), menuOpen, dining.isOpen(), dining);
                        cards.add(card);

                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(cards);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
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
        JSONUtilities.readJSONFromUrl(context, URL, "dining_halls", DiningCardController.getInstance());
    }
}
