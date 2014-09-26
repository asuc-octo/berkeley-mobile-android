package com.asuc.asucmobile.controllers;

import org.json.JSONArray;

public interface Controller {

    /**
     *  setResources() is a centralized function for all data controllers that the JSON utility
     *  calls after retrieving data. The implementation will vary depending on what type of data
     *  was retrieved. It most likely consists of parsing the data into models for use with the UI.
     */
    public void setResources(final JSONArray array);

}
