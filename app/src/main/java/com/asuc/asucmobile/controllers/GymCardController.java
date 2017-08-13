package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Card;
import com.asuc.asucmobile.models.Gyms.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.asuc.asucmobile.utilities.JsonToObject;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.asuc.asucmobile.controllers.Controller.BASE_URL;

public class GymCardController {

    private static String URL = BASE_URL+"/gyms";
    private static GymCardController instance;
    private ArrayList<Card> cards;
    private Callback callback;
    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private GymCardController() {
        cards = new ArrayList<>();
    }

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
                cards.clear();
                try {
                    for (int i = 0; i < array.length(); i++) {
                        Gym data = (Gym) JsonToObject.retrieve(array.getJSONObject(i), "gyms", context);
                        Card card = new Card(data.getImageLink(), data.getName(), "Today: " + HOURS_FORMAT.format(data.getOpening()) + "- " + HOURS_FORMAT.format(data.getClosing()), data.isOpen(), data);
                        cards.add(card);
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(cards);
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

}