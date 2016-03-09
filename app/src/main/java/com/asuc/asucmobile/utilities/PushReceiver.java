package com.asuc.asucmobile.utilities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.main.InstanceService;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;

import org.json.JSONObject;

public class PushReceiver extends BroadcastReceiver {

    private static final String TAG = "PushReceiver";
    private static final int NOTIFICATION_ID = 1868;

    private static Context context;
    private static PushManager manager;

    // Registration receiver
    private static BroadcastReceiver broadcastReceiver = new BaseRegistrationReceiver()
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
            if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
                Log.d(TAG, "Registration successful");
            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                Log.d(TAG, "Registration unsuccessful");
            }
        }
    };

    // Push message receiver
    private static BroadcastReceiver receiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent)
        {
            try {
                // Get push notification data as a JSON
                String pushData = intent.getExtras().getString(JSON_DATA_KEY);
                JSONObject pushJSON = new JSONObject(pushData);
                String header = "Berkeley Mobile";
                String message = "";
                if (pushJSON.has("header")) {
                    header = pushJSON.getString("header");
                }
                if (pushJSON.has("title")) {
                    message = pushJSON.getString("title");
                }

                // Build the notification to be displayed
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon_notification)
                        .setContentTitle(header)
                        .setContentText(message)
                        .setPriority(2)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setVibrate(new long[] {1000, 1000});

                // Grab the notification manager and display it
                NotificationManager notificationManager = (NotificationManager)
                        context.getApplicationContext()
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    };

    /*
     * This method is called when the phone boots
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start a dummy service so the app's resources load
        Intent i = new Intent(context, InstanceService.class);
        context.startService(i);

        // Register for push notifications
        registerPushes(context);
    }

    public static void registerPushes(Context context) {
        // If we already registered, deregister then re-register
        if (manager != null) {
            try {
                manager.unregisterForPushNotifications();
                context.getApplicationContext().unregisterReceiver(broadcastReceiver);
                context.getApplicationContext().unregisterReceiver(receiver);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            manager = null;
        }

        // Update app instance information
        PushReceiver.context = context;

        // Register receivers with PushWhoosh
        String packageName = context.getPackageName();
        IntentFilter intentFilter = new IntentFilter(packageName + ".action.PUSH_MESSAGE_RECEIVE");
        context.getApplicationContext().registerReceiver(
                        receiver, intentFilter, packageName + ".permission.C2D_MESSAGE", null
        );
        context.getApplicationContext().registerReceiver(
                broadcastReceiver,
                new IntentFilter(packageName + "." + PushManager.REGISTER_BROAD_CAST_ACTION)
        );

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
