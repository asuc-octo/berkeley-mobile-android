package com.asuc.asucmobile.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.R;

import java.io.InputStream;

public class ImageDownloadThread extends Thread {

    Activity activity;
    ProgressBar progressBar;
    ImageView image;
    String url;

    public ImageDownloadThread(Activity activity, String url) {
        this.activity = activity;
        this.progressBar = (ProgressBar) activity.findViewById(R.id.progress_bar);
        this.image = (ImageView) activity.findViewById(R.id.image);
        this.url = url;
    }

    @Override
    public void run() {
        try {
            InputStream input = new java.net.URL(url).openStream();
            final Bitmap bitmap = BitmapFactory.decodeStream(input);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);

                    if (bitmap != null) {
                        image.setImageBitmap(bitmap);
                        image.setVisibility(View.VISIBLE);
                    } else {
                        image.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            // Don't worry about it!
        }
    }

}