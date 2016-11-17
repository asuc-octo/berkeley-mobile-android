package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Card;
import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.asuc.asucmobile.utilities.JsonToObject;

import org.json.JSONArray;
import java.util.ArrayList;

public class GymCardController implements Controller {

    private static String URL = BASE_URL+"/gyms";
    private static GymCardController instance;
    private ArrayList<Card> cards;
    private Callback callback;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new GymCardController();
        }
        return instance;
    }

    private GymCardController() {
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
                        Gym data = (Gym) JsonToObject.retrieve(array.getJSONObject(i), "gyms", context);
                        Card card = new Card(data.getImageUrl(), data.getName(), data.getAddress(), data.isOpen(), data);
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

    @Override
    public void refreshInBackground(@NonNull Context context, Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl(context, URL, "gyms", GymCardController.getInstance());
    }

}