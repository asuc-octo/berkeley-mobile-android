package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashSet;

public class FoodAdapter extends BaseAdapter {

    public static final String TAG = "FoodAdapter";

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
        final FoodItem foodItem = foodItems.get(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.food_row, parent, false);
        }

        final TextView foodName = (TextView) convertView.findViewById(R.id.food_name);

        foodName.setText(foodItem.getName());

        if (foodItem.getFoodTypes() != null && foodItem.getFoodTypes().size() > 0) {

            // make imageViews dynamically, switch on types
            final LinearLayout foodTypesLayout = (LinearLayout) convertView.findViewById(R.id.food_types_layout);
            foodTypesLayout.removeAllViews();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            for (String str : foodItem.getFoodTypes()) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(selectFoodIcon(str));
                imageView.setLayoutParams(params);
                foodTypesLayout.addView(imageView);
                Log.d(TAG, "added imageView" + imageView.toString());

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
                }

                MenuFragment.refreshLists();
            }
        });

        return convertView;
    }

    private int selectFoodIcon(String string) {
        switch (string) {
            case ("alcohol"):
                return R.drawable.alcohol;
            case ("egg"):
                return R.drawable.egg;
            case ("fish"):
                return R.drawable.fish;
            case ("gluten"):
                return R.drawable.gluten;
            case ("halal"):
                return R.drawable.halal;
            case ("kosher"):
                return R.drawable.kosher;
            case ("milk"):
                return R.drawable.milk;
            case ("peanuts"):
                return R.drawable.peanuts;
            case ("pork"):
                return R.drawable.pork;
            case ("sesame"):
                return R.drawable.sesame;
            case ("shellfish"):
                return R.drawable.shellfish;
            case ("soybeans"):
                return R.drawable.soybeans;
            case ("tree_nuts"):
                return R.drawable.tree_nuts;
            case ("vegan"):
                return R.drawable.vegan;
            case ("vegetarian"):
                return R.drawable.vegetarian;
            case ("wheat"):
                return R.drawable.wheat;
            default:
                return R.drawable.dining_hall;
        }
    }

}