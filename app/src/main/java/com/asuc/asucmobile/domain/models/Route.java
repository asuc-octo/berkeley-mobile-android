package com.asuc.asucmobile.domain.models;

/**
 * Created by alexthomas on 10/9/17.
 */

public class Route {
    private String id;
    private String title;

    public Route(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
