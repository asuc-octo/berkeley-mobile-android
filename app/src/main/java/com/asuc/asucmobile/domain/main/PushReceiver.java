package com.asuc.asucmobile.domain.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pushwoosh.Pushwoosh;

public class PushReceiver extends BroadcastReceiver {

    private static final String TAG = "PushReceiver";

    /*
     * This method is called when the phone boots
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Register for push notifications
            Pushwoosh.getInstance().registerForPushNotifications(result -> {
                if (result.isSuccess()) {
                    Log.d(TAG, "Successfully registered for push notifications with token: "
                            + result.getData());
                } else {
                    if (result.getException() != null) {
                        Log.d(TAG, "Failed to register for push notifications:u "
                                + result.getException().getMessage());
                    }
                }
            });
        }
    }
}
