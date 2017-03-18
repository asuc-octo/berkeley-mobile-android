package com.asuc.asucmobile.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asuc.asucmobile.controllers.Controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JSONUtilities {

    private static final String TAG = "JSONUtilities";

    /**
     *  getUrlBody() takes a website and puts all its contents into a String format. Used for JSON
     *  parsing.
     *
     *  @param reader The buffer attached to a website to retrieve all characters.
     *  @return JSON data in String format.
     */
    private static String getUrlBody(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int character = reader.read();
        while (character != -1) {
            builder.append((char) character);
            character = reader.read();
        }
        return builder.toString();
    }

    /**
     * readJSONFromUrl() retrieves JSON info from an api and calls an update function through the
     * controller to pass on the data.
     *
     * @param context The Android context from which we're loading the JSON.
     * @param url Url where JSON data is located.
     * @param name Label of main JSONArray in the url.
     * @param controller Data Controller to host the callback function once the info is retrieved.
     */
    public static void readJSONFromUrl(Context context, String url, String name, Controller controller) {
        new ReadJSONTask(context, url, name, controller).execute("");
    }

    private static class ReadJSONTask extends AsyncTask<String, Void, Void> {

        Context context;
        String url;
        String name;
        Controller controller;

        private ReadJSONTask(Context context, String url, String name, Controller controller) {
            this.context = context;
            this.url = url;
            this.name = name;
            this.controller = controller;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                InputStream input = (new URL(url)).openStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
                String jsonText = getUrlBody(buffer);
                JSONObject json = new JSONObject(jsonText);
                JSONArray jsonArray = json.getJSONArray(name);
                input.close();
                controller.setResources(context, jsonArray);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                controller.setResources(context, null);
            }
            return null;
        }

    }

    public static void setObjectFromUrl(Context context, String url, String name, Controller controller) {
        try {
            new JSONItemTask(context, url, name, controller).execute().get(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }

    private static class JSONItemTask extends AsyncTask<String, Void, Void> {

        Context context;
        String url;
        String name;
        Controller controller;

        private JSONItemTask(Context context, String url, String name, Controller controller) {
            this.context = context;
            this.url = url;
            this.name = name;
            this.controller = controller;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                InputStream input = (new URL(url)).openStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
                String jsonText = getUrlBody(buffer);
                JSONObject json = new JSONObject(jsonText).getJSONObject(name);;
                input.close();
                controller.setItem(context, json);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                controller.setItem(context, null);
            }
            return null;
        }

    }

 }