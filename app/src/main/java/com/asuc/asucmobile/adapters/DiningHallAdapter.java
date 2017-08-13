package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.DiningHalls.DiningHall;

import java.util.ArrayList;
import java.util.List;

public class DiningHallAdapter extends BaseAdapter {

    private Context context;
    private List<DiningHall> diningHalls;

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
        if (diningHall.isOpen()) {
            imageView.setImageDrawable(null);
            openStatus.setTextColor(context.getResources().getColor(R.color.green));
            openStatus.setText(context.getText(R.string.open));
        } else {
            imageView.setImageDrawable(null);
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
    public void setList(List<DiningHall> list) {
        diningHalls = list;

        notifyDataSetChanged();
    }

}
