package com.asuc.asucmobile.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;

public class ImageHeaderView extends LinearLayout {

    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textView;

    public ImageHeaderView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.image_header_view, this);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.header_text);
    }

    public void showImage() {
        imageView.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    public void setImage(Bitmap image) {
        if (image != null) {
            imageView.setImageBitmap(image);
        }
        showImage();
    }

    public void setImage(Drawable image) {
        if (image != null) {
            imageView.setImageDrawable(image);
        }
        showImage();
    }

    public void setText(SpannableString string) {
        textView.setText(string);
    }

}
