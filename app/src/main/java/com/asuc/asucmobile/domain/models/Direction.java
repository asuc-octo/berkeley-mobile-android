package com.asuc.asucmobile.domain.models;

/**
 * Created by alexthomas on 5/26/17.
 */

public class Direction {
    private String id;
    private String title;

    public Direction(String id, String title){
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
