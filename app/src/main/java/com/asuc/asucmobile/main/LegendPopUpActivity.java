package com.asuc.asucmobile.main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asuc.asucmobile.R;

import java.util.ArrayList;

public class LegendPopUpActivity extends DialogFragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_food_legend, container, false);

        ListView legendList = (ListView) v.findViewById(R.id.legend_list);
        ArrayList<Pair<Drawable, String>> items = new ArrayList<>();
        items.add(new Pair(ContextCompat.getDrawable(this.getActivity(), R.drawable.gluten), "Gluten"));
        items.add(new Pair(ContextCompat.getDrawable(this.getActivity(), R.drawable.vegan), "Vegan"));
        items.add(new Pair(ContextCompat.getDrawable(this.getActivity(), R.drawable.wheat), "Gluten Free"));
        items.add(new Pair(ContextCompat.getDrawable(this.getActivity(), R.drawable.tree_nuts), "Contains Nuts"));
        items.add(new Pair(ContextCompat.getDrawable(this.getActivity(), R.drawable.egg), "Contains Eggs"));
        items.add(new Pair(ContextCompat.getDrawable(this.getActivity(), R.drawable.milk), "Contains Milk"));

        legendList.setAdapter(new LegendAdapter(this.getActivity(), items));

        return v;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
