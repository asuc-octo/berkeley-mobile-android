package com.asuc.asucmobile.domain.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pushwoosh.PushManager;

public class PushReceiver extends BroadcastReceiver {

    private static final String TAG = "PushReceiver";

    private static PushManager manager;

    /*
     * This method is called when the phone boots
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Register for push notifications
        registerPushes(context);
    }

    public static void registerPushes(Context context) {
        // If we already registered, deregister then re-register
        if (manager != null) {
            return;
        }

        // Start PushWhoosh
        manager = PushManager.getInstance(context);
        try {
            manager.onStartup(context);
            manager.registerForPushNotifications();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
