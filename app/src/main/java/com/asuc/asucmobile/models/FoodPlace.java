package com.asuc.asucmobile.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rustie on 10/4/17.
 */

public abstract class FoodPlace implements Serializable{



    protected String id;
    protected String name;

    @SerializedName("image_link")
    protected String imageUrl;

    // TODO: do something with this
    private boolean isOpen;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
