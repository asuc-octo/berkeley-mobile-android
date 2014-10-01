package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.DiningHall;

import java.util.ArrayList;

public class DiningHallAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DiningHall> diningHalls;

    public DiningHallAdapter(Context context) {
        this.context = context;

        diningHalls = new ArrayList<DiningHall>();
    }

    @Override
    public int getCount() {
        return diningHalls.size();
    }

    @Override
    public DiningHall getItem(int i) {
        return diningHalls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        DiningHall diningHall = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dining_row, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);

        name.setText(diningHall.getName());
        name.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));

        return convertView;
    }

    /**
     * setList() updates the list of libraries (typically after calling for a refresh).
     *
     * @param list The updated list of libraries.
     */
    public void setList(ArrayList<DiningHall> list) {
        diningHalls = list;

        notifyDataSetChanged();
    }

}
