package com.asuc.asucmobile.domain.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Created by rustie on 10/4/17.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public abstract class FoodPlace implements Serializable{

    protected String id;
    protected String name;

    @SerializedName("image_link")
    protected String imageUrl;

    // TODO: do something with this
    private boolean isOpen;

    private String hours;
}
