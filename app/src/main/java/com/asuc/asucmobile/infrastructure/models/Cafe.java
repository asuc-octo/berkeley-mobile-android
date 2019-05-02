package com.asuc.asucmobile.infrastructure.models;

import com.asuc.asucmobile.domain.models.FoodItem;
import com.asuc.asucmobile.domain.models.responses.CafeMenuResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Cafe {

    protected String id;
    protected String name;

    @SerializedName("image_link")
    protected String imageUrl;

    // because JSON response for Cafes has a deeper structure...
    @SerializedName("menus")
    public ArrayList<CafeMenuResponse> cafeMenus;
    private CafeMenuResponse breakfast;
    private CafeMenuResponse lunchDinner;

    private ArrayList<FoodItem> breakfastMenu;
    private ArrayList<FoodItem> lunchDinnerMenu;
    private Date breakfastOpen;
    private Date lunchDinnerOpen;
    private Date breakfastClose;
    private Date lunchDinnerClose;
}
