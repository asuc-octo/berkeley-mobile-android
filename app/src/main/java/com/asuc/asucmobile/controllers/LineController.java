package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;

import com.asuc.asucmobile.models.Line;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LineController implements Controller {

    private static LineController instance;

    // SparseArray is an Android HashMap that accepts primitive integers as keys.
    private Context context;
    private SparseArray<Line> linesMap;
    private Callback callback;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new LineController();
        }

        instance.context = context;

        return instance;
    }

    public LineController() {
        linesMap = new SparseArray<Line>();
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

        linesMap.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StopController stopController = (StopController) StopController.getInstance(context);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject lineJSON = array.getJSONObject(i);

                        int id = lineJSON.getInt("id");
                        String name = lineJSON.getString("name");
                        JSONArray stopsJSON = lineJSON.getJSONArray("stop_list");

                        ArrayList<Stop> stops = new ArrayList<Stop>();

                        for (int j = 0; j < stopsJSON.length(); j++) {
                            stops.add(stopController.getStop(stopsJSON.getJSONObject(j).getInt("id")));
                        }

                        linesMap.put(id, new Line(id, name, stops));
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(linesMap);
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
        JSONUtilities.readJSONFromUrl(
                "http://asuc-mobile.herokuapp.com/api/bt_lines",
                "lines",
                LineController.getInstance(context));
    }

    public SparseArray<Line> getLines() {
        return linesMap;
    }

    public Line getLine(int id) {
        return linesMap.get(id);
    }

    public int getId(Line line) {
        return linesMap.keyAt(linesMap.indexOfValue(line));
    }

}
