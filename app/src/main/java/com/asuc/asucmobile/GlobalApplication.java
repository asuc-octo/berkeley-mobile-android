package com.asuc.asucmobile;

import android.app.Application;
import android.content.Context;

/**
 * Configuration for the global application
 */
public class GlobalApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
