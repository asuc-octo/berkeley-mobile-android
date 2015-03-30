package com.asuc.asucmobile.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asuc.asucmobile.R;

public class ImageHeaderView extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    public ImageHeaderView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.image_header_view, this);

        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.header_text);

        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
    }

    public void setImage(Bitmap image) {
        imageView.setImageBitmap(image);
    }

    public void setImage(Drawable image) {
        imageView.setImageDrawable(image);
    }

    public void setText(SpannableString string) {
        textView.setText(string);
    }

}
