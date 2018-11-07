package com.asuc.asucmobile.domain.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alexthomas on 3/12/18.
 */

public class CategoryLoc {

    @SerializedName("category")
    private String category;

    @SerializedName("description1")
    private String description1;
    @SerializedName("description2")
    private String description2;
    @SerializedName("image_link")
    private String imagelink;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lon")
    private double lon;


    private LatLng position;


    public CategoryLoc(String category, String desc1, String desc2, String imagelink, double lat, double lon) {
        this.category = category;
        this.description1 = desc1;
        this.description2 = desc2;
        this.imagelink = imagelink;
        this.lat = lat;
        this.lon = lon;
        this.position = new LatLng(lat, lon);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc1() {
        return description1;
    }

    public void setDesc1(String desc1) {
        this.description1 = desc1;
    }

    public String getDesc2() {
        return description2;
    }

    public void setDesc2(String desc2) {
        this.description2 = desc2;
    }

    public String getImagelink() {
        return imagelink;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}
