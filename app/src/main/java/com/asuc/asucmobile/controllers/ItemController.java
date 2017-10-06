package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.DiningHalls;
import com.asuc.asucmobile.models.GroupExs;
import com.asuc.asucmobile.models.Gyms;
import com.asuc.asucmobile.models.Gyms.Gym;
import com.asuc.asucmobile.models.Items;
import com.asuc.asucmobile.models.Items.Item;
import com.asuc.asucmobile.models.Libraries;
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
import retrofit2.http.Path;

public class ItemController {

    private static Item currentItem;
    private static Object currentSearch;

    public static Object getCurrentSearch() {
        return currentSearch;
    }

    public static void setCurrentSearch(Object currentSearch) {
        ItemController.currentSearch = currentSearch;
    }

    public interface cService {
        @GET("search_items")
        Call<Items> getItems();

        @GET("gyms/{path}")
        Call<Gyms> getGym(@Path("path") String path);

        @GET("weekly_libraries/{path}")
        Call<Libraries> getLibrary(@Path("path") String path);

        @GET("group_exs/{path}")
        Call<GroupExs> getGroupExs(@Path("path") String path);

        @GET("dining_halls/{path}")
        Call<DiningHalls> getDiningHall(@Path("path") String path);
    }

    public static List<Item> parse(Items items) {
        return items.items;
    }

    public static void setCurrentItem(Item item) {
        currentItem = item;
    }

    public static Item getCurrentItem() {
        return currentItem;
    }
}

