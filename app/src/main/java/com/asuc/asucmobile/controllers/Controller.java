package com.asuc.asucmobile.controllers;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.utilities.Callback;

import org.json.JSONArray;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import butterknife.internal.Utils;
import okhttp3.Interceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by alexthomas on 5/28/17.
 */

public interface Controller {


    String BASE_URL = "http://asuc-mobile-dev.herokuapp.com/api/";
    String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    GsonBuilder gsonBuilder = new GsonBuilder()
            .setDateFormat(ISO_FORMAT)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    Gson gson = gsonBuilder.create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

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
