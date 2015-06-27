package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Category;

public class MainMenuAdapter extends BaseAdapter {

    private Context context;
    private Category[] categories;

    public MainMenuAdapter(Context context, Category[] categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Category getItem(int i) {
        return categories[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Category item = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_menu_row, parent, false);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.menu_icon);
        TextView name = (TextView) convertView.findViewById(R.id.menu_name);

        icon.setImageDrawable(item.getIcon());
        name.setText(item.getName());

        return convertView;
    }

}
