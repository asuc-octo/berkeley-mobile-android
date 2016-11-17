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
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

public class DiningCardController implements Controller {

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
                        Card card = new Card(dining.getImageUrl(), dining.getName(), null, dining.isOpen(), dining);
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
