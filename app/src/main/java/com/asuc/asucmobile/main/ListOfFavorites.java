package com.asuc.asucmobile.main;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Victor on 12/4/15.
 */
public class ListOfFavorites implements Serializable {

    private ArrayList<String> favorited;

    public void add(String x) {
        if (favorited == null) {
            favorited = new ArrayList<String>();
        }
        favorited.add(x);
    }

    public void remove(String x) {
        favorited.remove(x);
    }

    public ArrayList<String> getContents() {
        return favorited;
    }

    public boolean contains(String x) {
        return favorited != null && favorited.contains(x);
    }


}
