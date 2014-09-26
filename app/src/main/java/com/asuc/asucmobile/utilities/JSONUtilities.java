package com.asuc.asucmobile.utilities;

import android.os.AsyncTask;

import com.asuc.asucmobile.controllers.Controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class JSONUtilities {

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
     * @param url Url where JSON data is located.
     * @param name Label of main JSONArray in the url.
     * @param controller Data Controller to host the callback function once the info is retrieved.
     */
    public static void readJSONFromUrl(String url, String name, Controller controller) {
        new ReadJSONTask(url, name, controller).execute("");
    }

    private static class ReadJSONTask extends AsyncTask<String, Void, Void> {

        String url;
        String name;
        Controller controller;

        public ReadJSONTask(String url, String name, Controller controller) {
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

                controller.setResources(jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
                controller.setResources(null);
            }

            return null;
        }

    }

 }