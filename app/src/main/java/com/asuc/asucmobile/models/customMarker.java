package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by alexthomas on 6/14/17.
 */

public class customMarker {
     public String title;
     public Marker marker;

    public customMarker(String title, Marker marker){
        this.title = title;
        this.marker = marker;
    }
}
