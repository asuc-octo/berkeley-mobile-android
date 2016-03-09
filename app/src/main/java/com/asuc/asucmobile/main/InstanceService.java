package com.asuc.asucmobile.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * This class is only used to load the app's resources upon boot. This is to load icons and
 * vibration patterns for push notifications. Af
 */
public class InstanceService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Do nothing
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY; // Let the OS kill the service
    }

}
