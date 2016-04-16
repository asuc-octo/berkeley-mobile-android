package com.asuc.asucmobile.controllers;

import com.asuc.asucmobile.utilities.Callback;

import org.json.JSONArray;

public interface Controller {

    String BASE_URL = "http://asuc-mobile.herokuapp.com/api";

    /**
     *  setResources() is a centralized function for all data controllers that the JSON utility
     *  calls after retrieving data. The implementation will vary depending on what type of data
     *  was retrieved. It most likely consists of parsing the data into models for use with the UI.
     */
    void setResources(final JSONArray array);

    /**
     * refreshInBackground() retrieves info from the web and calls the callback function once it's
     * finished in order to update the UI.
     *
     * @param callback The method to be called after information has been retrieved from JSON.
     */
    void refreshInBackground(Callback callback);

}
