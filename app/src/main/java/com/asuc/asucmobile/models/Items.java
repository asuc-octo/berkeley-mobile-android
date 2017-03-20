package com.asuc.asucmobile.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Items {
    @SerializedName("search_list")
    public List<Item> items;

    public class Item implements Comparable<Item>{

        private String name;
        private String category;
        private String query;

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getQuery() {
            return query;
        }

        public Item(String name, String category, String query) {
            this.name = name;
            this.category = category;
            this.query = query;
        }

        @Override
        public int compareTo(@NonNull Item other) {
            return this.getName().compareTo(other.getName());
        }

    }
}

