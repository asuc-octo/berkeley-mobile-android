package com.asuc.asucmobile.singletons;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rustie on 2/16/18.
 */

public class BMRetrofit {


    String BASE_URL = "http://asuc-mobile-dev.herokuapp.com/api/";
    String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static Retrofit retrofit;

    private BMRetrofit() {}

    public Retrofit getInstance() {
        if (retrofit == null) {
            //    File cacheDir = new File("cachedir");
            //    HttpResponseCache cache = new HttpResponseCache(cacheDir, 1024);
            //    okHttpClient.setResponseCache(cache);
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat(ISO_FORMAT)
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            Gson gson = gsonBuilder.create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }







}
