package com.asuc.asucmobile.models;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class Category {

    private Drawable icon;
    private String name;
    private Intent intent;

    public Category(Drawable icon, String name, Intent intent) {
        this.icon = icon;
        this.name = name;
        this.intent = intent;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public Intent getIntent() {
        return intent;
    }

}
