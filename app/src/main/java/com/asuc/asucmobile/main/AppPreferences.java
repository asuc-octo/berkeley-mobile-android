package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Victor on 12/4/15.
 */
public class AppPreferences {
    public static final String KEY_PREFS_SMS_BODY = "sms_body";
    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getSmsBody() {
        return _sharedPrefs.getString(KEY_PREFS_SMS_BODY, "");
    }

    public void saveSmsBody(String text) {
        _prefsEditor.putString(KEY_PREFS_SMS_BODY, text);
        _prefsEditor.commit();
    }
}
