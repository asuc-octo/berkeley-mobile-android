package com.asuc.asucmobile.domain.main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.DialogFragment;

import androidx.core.content.ContextCompat;

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
import com.asuc.asucmobile.values.FoodTypes;

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
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.milk), FoodTypes.MILK));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.egg), FoodTypes.EGGS));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.shellfish), FoodTypes.SHELLFISH));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.fish), FoodTypes.FISH));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.tree_nuts), FoodTypes.TREE_NUTS));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.wheat), FoodTypes.WHEAT));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.peanuts), FoodTypes.PEANUTS));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.sesame), FoodTypes.SESAME));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.soybeans), FoodTypes.SOYBEANS));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.vegan), FoodTypes.VEGAN));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.vegetarian), FoodTypes.VEGETARIAN));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.gluten), FoodTypes.GLUTEN));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.pork), FoodTypes.PORK));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.alcohol), FoodTypes.ALCOHOL));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.halal), FoodTypes.HALAL));
        items.add(new Pair<>(ContextCompat.getDrawable(this.getActivity(), R.drawable.kosher), FoodTypes.KOSHER));

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

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1000);
    }
}
