package com.asuc.asucmobile.controllers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.utilities.Callback;

import org.json.JSONArray;


/**
 * Created by alexthomas on 5/28/17.
 */

public interface Controller {

    String BASE_URL = "http://asuc-mobile-dev.herokuapp.com/api";

    /**
     * setResources() is a centralized function for all data controllers that the JSON utility
     * calls after retrieving data. The implementation will vary depending on what type of data
     * was retrieved. It most likely consists of parsing the data into models for use with the UI.
     */
    void setResources(@NonNull final Context context, final JSONArray array);

    /**
     * refreshInBackground() retrieves info from the web and calls the callback function once it's
     * finished in order to update the UI.
     *
     * @param callback The method to be called after information has been retrieved from JSON.
     */
    void refreshInBackground(@NonNull Context context, Callback callback);
}
