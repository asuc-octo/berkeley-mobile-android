package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.FoodItem;

import java.util.ArrayList;

public class FoodAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FoodItem> foodItems;

    public FoodAdapter(Context context, ArrayList<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }


    @Override
    public int getCount() {
        return foodItems.size();
    }

    @Override
    public Object getItem(int i) {
        return foodItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        FoodItem foodItem = foodItems.get(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.food_row, parent, false);
        }

        TextView foodName = (TextView) convertView.findViewById(R.id.food_name);
        TextView foodType = (TextView) convertView.findViewById(R.id.food_type);
        TextView foodCalories = (TextView) convertView.findViewById(R.id.calories);

        foodName.setText(foodItem.getName());

        if (!foodItem.getFoodType().equals("None")) {
            foodType.setText(foodItem.getFoodType().toUpperCase());
        } else {
            foodType.setText("");
        }

        if (foodItem.getCalories().equals("null")) {
            foodCalories.setText("");
        } else {
            foodCalories.setText(foodItem.getCalories() + " cal");
        }

        return convertView;
    }

}
