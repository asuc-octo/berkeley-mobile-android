package com.asuc.asucmobile.domain.models;

/**
 * Created by alexthomas on 10/9/17.
 */

public class Agency {
    private String id;
    private String title;
    private String url;

    public Agency(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
