package com.asuc.asucmobile.utilities;

import android.content.Context;

import com.asuc.asucmobile.R;

public class GoogleApiUtil {
    public static String getGoogleApiKey(Context context) {
        // Ask before changing the following line.
        return context.getResources().getString(R.string.google_api_key_android_dev);
    }
}
