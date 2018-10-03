package com.asuc.asucmobile.modules;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.controllers.BMAPI;
import com.asuc.asucmobile.controllers.BMRetrofitController;
import com.asuc.asucmobile.utilities.ServerUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    public static final String TAG = "RetrofitModule";

    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final int CACHE_SIZE = 20; // size of cache in MiB
    public static final int MAX_AGE = 60;
    public static final int MAX_STALE = 60 * 60 * 24;

    private static Context context;
    public static BMAPI bmapi;

    private static Retrofit retrofit;

    @Provides @Singleton
    public BMAPI getBMAPI() {
        configureRetrofit();
        return retrofit.create(BMAPI.class);
    }

    /**
     * All the magical caching, okhttp, gson, and retrofit settings
     */
    private static void configureRetrofit() {

        // caching config
        File cacheDir = new File(GlobalApplication.getAppContext().getCacheDir(), "okHttpCache");
        Cache cache = new Cache(cacheDir, CACHE_SIZE * 1024 * 1024);

        // okhttp config for cache
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(new ResponseCachingInterceptor())
                .addInterceptor(new OfflineResponseCacheInterceptor())
                .build();

        // gson config
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setDateFormat(ISO_FORMAT)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();

        // building retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(ServerUtils.getBaseUrl())
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private static class ResponseCachingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Access-Control-Allow-Origin")
                    .removeHeader("Vary")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + MAX_AGE)
                    .build();
        }
    }

    private static class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isConnected()) {
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Access-Control-Allow-Origin")
                        .removeHeader("Vary")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control",
                                "public, max-stale=" + MAX_STALE)
                        .build();
            }
            return chain.proceed(request);
        }
    }

    /**
     * Checks to see if we have internet connection
     * @return
     */
    public static boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

}
