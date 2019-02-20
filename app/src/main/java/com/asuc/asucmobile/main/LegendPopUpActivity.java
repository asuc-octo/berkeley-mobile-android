package com.asuc.asucmobile.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asuc.asucmobile.R;

import java.util.ArrayList;

public class LegendPopUpActivity extends FragmentActivity {
    public class LegendAdapter extends ArrayAdapter<Pair<Drawable, String>> {
        public LegendAdapter(Context context, ArrayList<Pair<Drawable, String>> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Pair<Drawable, String> pair = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.legend_row, parent, false);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.legend_icon);
            TextView text = (TextView) convertView.findViewById(R.id.legend_text);
            icon.setImageDrawable(pair.first);
            text.setText(pair.second);

            return convertView;
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_food_legend);

        ListView legendList = (ListView) findViewById(R.id.legend_list);
        ArrayList<Pair<Drawable, String>> items = new ArrayList<>();
        items.add(new Pair(ContextCompat.getDrawable(this, R.drawable.gluten), "Gluten"));
        items.add(new Pair(ContextCompat.getDrawable(this, R.drawable.vegan), "Vegan"));
        items.add(new Pair(ContextCompat.getDrawable(this, R.drawable.wheat), "Gluten Free"));
        items.add(new Pair(ContextCompat.getDrawable(this, R.drawable.tree_nuts), "Contains Nuts"));
        items.add(new Pair(ContextCompat.getDrawable(this, R.drawable.egg), "Contains Eggs"));
        items.add(new Pair(ContextCompat.getDrawable(this, R.drawable.milk), "Contains Milk"));

        legendList.setAdapter(new LegendAdapter(this, items));

        adjustScreen(0.7, 0.5);
    }

    private void adjustScreen(double fractWidth, double fractHeight) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * fractWidth), (int) (height * fractHeight));
    }
}
