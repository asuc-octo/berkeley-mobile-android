package com.asuc.asucmobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Card;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.ImageDownloadThread;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CardAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Card> cards;

    public CardAdapter(Context context) {
        this.context = context;
        cards = new ArrayList<>();
    }

    @Override
    public int getCount() {
            return cards.size();
        }

    @Override
    public Card getItem(int i) {
            return cards.get(i);
        }

    @Override
    public long getItemId(int i) {
            return i;
        }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        final Card card = getItem(i);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
//        new ImageDownloadThread((Activity) context, card.getImageUrl(), new Callback() {
//            @Override
//            public void onDataRetrieved(Object data) {
//                if (data != null) {
//                    Bitmap bitmap = (Bitmap) data;
//                    Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
//                    imageView.setBackgroundDrawable(bitmapDrawable);
//                }
//                imageView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onRetrievalFailed() {
//                if (card.getData() instanceof Gym) imageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_gym));
//                if (card.getData() instanceof DiningHall) imageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_dining));
//                imageView.setVisibility(View.VISIBLE);
//            }
//        }).start();
        Glide.with(context).load(card.getImageUrl()).into(imageView);
        TextView times = (TextView) convertView.findViewById(R.id.times);
        times.setText(card.getTimes());
        name.setText(card.getName());
        if (card.getStatus()) {
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
    public void setList(ArrayList<Card> list) {
        cards = list;
        notifyDataSetChanged();
    }


}
