package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.DiningHall;

import java.util.ArrayList;

public class DiningHallAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DiningHall> diningHalls;

    public DiningHallAdapter(Context context) {
        this.context = context;

        diningHalls = new ArrayList<>();
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
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        TextView openStatus = (TextView) convertView.findViewById(R.id.open_status);

        if(diningHall.isDoorDash()) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.red_symbol_circular));
            openStatus.setTextColor(context.getResources().getColor(R.color.dark_grey));
            openStatus.setText(context.getText(R.string.door_dash));
        } else if (diningHall.isOpen()) {
            openStatus.setTextColor(context.getResources().getColor(R.color.green));
            openStatus.setText(context.getText(R.string.open));
        } else {
            openStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            openStatus.setText(context.getText(R.string.closed));
        }

        name.setText(diningHall.getName());

        return convertView;
    }

    /**
     * setList() updates the list of dining halls (typically after calling for a refresh).
     *
     * @param list The updated list of dining halls.
     */
    public void setList(ArrayList<DiningHall> list) {
        diningHalls = list;

        notifyDataSetChanged();
    }

}
