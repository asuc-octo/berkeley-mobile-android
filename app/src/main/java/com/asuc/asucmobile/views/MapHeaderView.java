package com.asuc.asucmobile.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.asuc.asucmobile.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MapHeaderView extends LinearLayout {

    private GoogleMap map;

    public MapHeaderView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.map_header_view, this);
    }

}
