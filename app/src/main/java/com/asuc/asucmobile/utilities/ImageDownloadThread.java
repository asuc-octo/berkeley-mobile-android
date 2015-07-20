package com.asuc.asucmobile.utilities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.main.OpenGymActivity;

import java.io.InputStream;

public class ImageDownloadThread extends Thread {

    private String url;
    private Activity activity;
    private Callback callback;

    public ImageDownloadThread(Activity activity, String url, Callback callback) {
        this.activity = activity;
        this.url = url;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            InputStream input = new java.net.URL(url).openStream();
            final Bitmap bitmap = BitmapFactory.decodeStream(input);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onDataRetrieved(bitmap);
                }
            });
        } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onRetrievalFailed();
                }
            });
        }
    }

}