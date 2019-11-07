package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;


import com.asuc.asucmobile.domain.models.BusDeparture;
import com.asuc.asucmobile.domain.models.BusInfo;
import com.asuc.asucmobile.domain.models.PTBusResponse;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by alexthomas on 5/28/17.
 */

public class BusController implements Controller {
    private static final String URL = BASE_URL + "/pt_routes?code=";
    private static BusController instance;
    private String title, id;
    private HashMap<String, HashMap<String, Collection<PTBusResponse>>> PT_times;
    private ArrayList<ArrayList<BusDeparture>> times = new ArrayList<>();
    private Type testType;
    private Callback callback;
    private final Gson gson = new Gson();

    public static Controller getInstance(String id, String title) {
        if (instance == null || id == null || (!id.equals(instance.id))) {
            instance = new BusController(id, title);
        }
        return instance;
    }

    public BusController(String id, String title) {
        this.id = id;
        this.title = title;
        testType = new TypeToken<HashMap<String, HashMap<String, Collection<PTBusResponse>>>>() {
        }.getType();

    }


    @Override
    public void setResources(@NonNull final Context context, final JSONArray json) {

        try {
            if (json == null) {
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
                        PT_times = gson.fromJson((String)json.get(0), testType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<PTBusResponse>  listOfItems =  (ArrayList<PTBusResponse>) PT_times.get("ptbus_response").get("values");
                    final ArrayList<BusInfo> busInfos = new ArrayList<>();

                    for(PTBusResponse busResponse: listOfItems){
                        busInfos.add(new BusInfo(title, (ArrayList<BusDeparture>) busResponse.getValues(), busResponse.getRoute().getTitle(), ((ArrayList<BusDeparture>) busResponse.getValues()).get(0).getBusRouteName()));


                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(busInfos);
                        }
                    });

                }
            }).start();
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


    @Override
    public void refreshInBackground(@NonNull Context context, Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl(context, URL + id, "ptbus_response", BusController.getInstance(id, title));

    }



}
