package com.asuc.asucmobile.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.asuc.asucmobile.R;

public class MapHeaderView extends LinearLayout {

    public MapHeaderView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.map_header_view, this);
    }

}
