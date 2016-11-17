package com.asuc.asucmobile.models;

public class Card {

    private String imageUrl;
    private String name;
    private String location;
    private boolean status;
    private Object data;

    public Card(String imageUrl, String name, String location, boolean status, Object data) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.location = location;
        this.status = status;
        this.data = data;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
