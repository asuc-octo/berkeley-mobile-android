package com.asuc.asucmobile.utilities;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rustie on 2/10/18.
 */

public class ServiceGenerator {

    public static final String BASE_URL = "http://asuc-mobile-dev.herokuapp.com/api/";
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static GsonBuilder gsonBuilder = new GsonBuilder()
            .setDateFormat(ISO_FORMAT)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

    private static Gson gson = gsonBuilder.create();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
