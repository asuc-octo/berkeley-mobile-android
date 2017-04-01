package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Items;
import com.asuc.asucmobile.models.Items.Item;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.http.GET;

public class ItemController {

    private static Item currentItem;

    public interface cService {
        @GET("search_items")
        Call<Items> getItems();
    }

    public static List<Item> parse(Items items) {
        return items.items;
    }

    public static void setItemFromUrl(@NonNull Context context, String ItemURL, String name, Controller controller) {
        JSONUtilities.setObjectFromUrl(context, ItemURL, name, controller);
    }

    public static void setCurrentItem(Item item) {
        currentItem = item;
    }

    public static Item getCurrentItem() {
        return currentItem;
    }
}

