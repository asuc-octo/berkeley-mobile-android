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

    Activity activity;
    ProgressBar progressBar;
    ImageView image;
    String url;
    ProgressBar percentFullBar;
    ProgressBar background;
    Integer percentFull;

    public ImageDownloadThread(Activity activity, String url) {
        this.activity = activity;
        this.progressBar = (ProgressBar) activity.findViewById(R.id.progress_bar);
        this.image = (ImageView) activity.findViewById(R.id.image);
        this.url = url;
        if(activity instanceof OpenGymActivity) {
            this.percentFull = ((OpenGymActivity)activity).getGym().getPercentFull();
        }
        this.percentFullBar = (ProgressBar) activity.findViewById(R.id.percent_full_bar);
        this.background = (ProgressBar) activity.findViewById(R.id.backing_ring);
    }

    @Override
    public void run() {
        try {
            InputStream input = new java.net.URL(url).openStream();
            final BitmapDrawable bitmap = new BitmapDrawable(activity.getResources(), input);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    if (percentFull == null) {
                        if(image != null) {
                            //regular page (library perhaps)
                            if (bitmap != null) {
                                image.setImageDrawable(bitmap);
                                image.setVisibility(View.VISIBLE);
                            } else {
                                //regular page fail
                                image.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            //gym with no data
                            if (bitmap != null) {
                                background.setBackgroundDrawable(bitmap);
                                background.setProgressDrawable(new ColorDrawable(Color.TRANSPARENT));
                            } else {
                                background.setVisibility(View.GONE);
                            }

                        }
                    } else {
                        //gym with data
                        if (bitmap != null) {
                            background.setBackgroundDrawable(bitmap);
                            ObjectAnimator animation = ObjectAnimator.ofInt(percentFullBar,
                                    "progress", 0, percentFull);
                            animation.setDuration (1000); //in milliseconds
                            animation.setInterpolator (new AccelerateDecelerateInterpolator());
                            animation.start();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            percentFullBar.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    image.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

}