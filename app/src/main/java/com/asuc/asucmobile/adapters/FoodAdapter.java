package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.main.ListOfFavorites;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.SerializableUtilities;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashSet;

public class FoodAdapter extends BaseAdapter {

    public static final String TAG = "FoodAdapter";
    private FirebaseAnalytics mFirebaseAnalytics;


    private Context context;
    private ArrayList<FoodItem> foodItems;
    private FoodPlaceAdapter.FoodType foodType;

    public FoodAdapter(Context context, ArrayList<FoodItem> foodItems, FoodPlaceAdapter.FoodType foodType) {
        this.context = context;
        this.foodItems = foodItems;
        this.foodType = foodType;
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
        final FoodItem foodItem = foodItems.get(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.food_row, parent, false);
        }

        final TextView foodName = (TextView) convertView.findViewById(R.id.food_name);
        foodName.setText(foodItem.getName());

        if (this.foodType == FoodPlaceAdapter.FoodType.Cafe) {
            final TextView foodCost = (TextView) convertView.findViewById(R.id.food_cost);
            foodCost.setText(foodItem.getCost());
        }

        if (foodItem.getFoodTypes() != null && foodItem.getFoodTypes().size() > 0) {

            // make imageViews dynamically, switch on types
            final LinearLayout foodTypesLayout = (LinearLayout) convertView.findViewById(R.id.food_types_layout);
            foodTypesLayout.removeAllViews();

            // layout stuff; want to make icons the same size as the food name: 16 sp
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            for (String str : foodItem.getFoodTypes()) {
                int id = selectFoodIcon(str);
                if (id == -1) {
                    continue; // failed to find an icon
                }
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(id);
                imageView.setLayoutParams(params);

                imageView.getLayoutParams().height = size;
                imageView.getLayoutParams().width = size;

                foodTypesLayout.addView(imageView);
            }

        }

        final ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(context);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.favorite);

        if (listOfFavorites != null && listOfFavorites.contains(foodItem.getName())) {
            imageView.setImageResource(R.drawable.post_favorite);
        } else {
            imageView.setImageResource(R.drawable.pre_favorite);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfFavorites != null && listOfFavorites.contains(foodItem.getName())) {
                    listOfFavorites.remove(foodItem.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.pre_favorite);

                } else if (listOfFavorites != null) {
                    listOfFavorites.add(foodItem.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.post_favorite);

                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(FoodAdapter.this.context);
                    Bundle bundle = new Bundle();
                    bundle.putString("food_item", foodItem.getName());
                    mFirebaseAnalytics.logEvent("favorited_food_item", bundle);


                }

                MenuFragment.refreshLists();
            }
        });

        return convertView;
    }

    /**
     * Selects proper food icon given a string food type in lower case
     * @param string
     * @return
     */
    private int selectFoodIcon(String string) {
        switch (string) {
            case ("contains alcohol"):
                return R.drawable.alcohol;
            case ("egg"):
                return R.drawable.egg;
            case ("fish"):
                return R.drawable.fish;
            case ("contains gluten"):
                return R.drawable.gluten;
            case ("halal"):
                return R.drawable.halal;
            case ("kosher"):
                return R.drawable.kosher;
            case ("milk"):
                return R.drawable.milk;
            case ("peanuts"):
                return R.drawable.peanuts;
            case ("contains pork"):
                return R.drawable.pork;
            case ("sesame"):
                return R.drawable.sesame;
            case ("shellfish"):
                return R.drawable.shellfish;
            case ("soybeans"):
                return R.drawable.soybeans;
            case ("tree nuts"):
                return R.drawable.tree_nuts;
            case ("vegan option"):
                return R.drawable.vegan;
            case ("vegetarian option"):
                return R.drawable.vegetarian;
            case ("wheat"):
                return R.drawable.wheat;
            default:
                return -1;
        }
    }

}