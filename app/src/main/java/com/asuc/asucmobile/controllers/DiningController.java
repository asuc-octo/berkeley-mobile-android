package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.asuc.asucmobile.utilities.JsonToObject;

import org.json.JSONArray;
import java.util.ArrayList;

public class DiningController implements Controller {

    private static final String TAG = "DiningController";
    private static final String URL = BASE_URL + "/dining_halls";

    private static DiningController instance;

    private ArrayList<DiningHall> diningHalls;
    private Callback callback;
    private DiningHall currentDiningHall;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new DiningController();
        }
        return instance;
    }

    private DiningController() {
        diningHalls = new ArrayList<>();
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
        diningHalls.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        DiningHall diningHall = (DiningHall) JsonToObject.retrieve(array.getJSONObject(i), "dining_halls", context);
                        diningHalls.add(diningHall);
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(diningHalls);
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
        JSONUtilities.readJSONFromUrl(context, URL, "dining_halls", DiningController.getInstance());
    }

    public void setCurrentDiningHall(DiningHall diningHall) {
        currentDiningHall = diningHall;
    }

    public DiningHall getCurrentDiningHall() {
        return currentDiningHall;
    }

}
