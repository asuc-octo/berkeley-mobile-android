package com.asuc.asucmobile.domain.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentManager;

public abstract class Category {

    private int iconId;
    private String name;

    protected Category(int iconId, String name) {
        this.iconId = iconId;
        this.name = name;
    }

    @SuppressWarnings("deprecation")
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(iconId);
    }

    public String getName() {
        return name;
    }

    public abstract void loadFragment(FragmentManager fragmentManager);

}
