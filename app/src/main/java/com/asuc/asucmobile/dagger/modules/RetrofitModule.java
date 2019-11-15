package com.asuc.asucmobile.dagger.modules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.domain.services.BMService;
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


    private String mBaseUrl;

    public RetrofitModule(String baseUrl) {
        mBaseUrl = baseUrl;
    }


    @Provides @Singleton
    public BMService provideBMAPI(final Retrofit retrofit) {
        Log.d(TAG, "BMService getting made");
        return retrofit.create(BMService.class);
    }

    @Provides @Singleton
    public Retrofit provideRetrofit(final Gson gson, final OkHttpClient httpClient) {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides @Singleton
    public OkHttpClient provideOkHttpClient() {
        // caching config
        File cacheDir = new File(GlobalApplication.getAppContext().getCacheDir(), "okHttpCache");
        Cache cache = new Cache(cacheDir, CACHE_SIZE * 1024 * 1024);

        // okhttp config for cache
        return new OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(new ResponseCachingInterceptor())
                .addInterceptor(new OfflineResponseCacheInterceptor())
                .build();
    }

    @Provides @Singleton
    public Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setDateFormat(ISO_FORMAT)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
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
                (ConnectivityManager) GlobalApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

}
