package com.asuc.asucmobile.models;

public class Card {

    private String imageUrl;
    private String name;
    private String times;
    private boolean status;
    private Object data;

    public Card(String imageUrl, String name, String times, boolean status, Object data) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.times = times;
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

    public String getTimes() {
        return times;
    }

    public void setTimes(String location) {
        this.times = location;
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
