package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Cafe;

import java.util.ArrayList;

/**
 * Created by rustie on 9/29/17.
 */

public class CafeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Cafe> cafes;

    public CafeAdapter(Context context) {
        this.context = context;
        cafes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return cafes.size();
    }

    @Override
    public Cafe getItem(int i) {
        return cafes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Cafe cafe = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.cafe_row, viewGroup, false);
        }
        TextView name = (TextView) view.findViewById(R.id.name);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView openStatus = (TextView) view.findViewById(R.id.open_status);
        if (cafe.isOpen()) {
            imageView.setImageDrawable(null);
            openStatus.setTextColor(context.getResources().getColor(R.color.green));
            openStatus.setText(context.getText(R.string.open));
        } else {
            imageView.setImageDrawable(null);
            openStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            openStatus.setText(context.getText(R.string.closed));
        }
        name.setText(cafe.getName());
        return view;
    }

    /**
     * setList() updates the list of cafes (typically after calling for a refresh).
     *
     * @param list The updated list of cafes
     */
    public void setList(ArrayList<Cafe> list) {
        cafes = list;

        notifyDataSetChanged();
    }
}
