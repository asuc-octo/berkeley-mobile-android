package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Cardable;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends BaseAdapter{

    private Context context;
    private List<? extends Cardable> cards;

    public CardAdapter(Context context) {
        this.context = context;
        cards = new ArrayList<>();
    }

    @Override
    public int getCount() {
            return cards.size();
        }

    @Override
    public Cardable getItem(int i) {
            return cards.get(i);
        }

    @Override
    public long getItemId(int i) {
            return i;
        }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

    final Cardable card = getItem(i);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        Glide.with(context).load(card.getImageLink()).into(imageView);
        TextView times = (TextView) convertView.findViewById(R.id.times);
        times.setText(card.getTimes());
        name.setText(card.getName());
        if (card.isOpen()) {
            status.setText(R.string.open);
            status.setTextColor(Color.GREEN);
        } else {
            status.setText(R.string.closed);
            status.setTextColor(Color.RED);
        }
        return convertView;
    }

    /**
     * setList() updates the list of dining halls (typically after calling for a refresh).
     *
     * @param list The updated list of dining halls.
     */
    public void setList(List<? extends Cardable> list) {
        cards = list;
        notifyDataSetChanged();
    }


}
