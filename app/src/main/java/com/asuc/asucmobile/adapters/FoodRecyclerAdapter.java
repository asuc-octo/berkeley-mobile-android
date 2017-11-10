package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.main.ListOfFavorites;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by rustie on 11/9/17.
 */

public class FoodRecyclerAdapter extends RecyclerView.Adapter<FoodRecyclerAdapter.ViewHolder> {


    public static final String TAG = "FoodRecyclerAdapter";


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView favoriteImageView;
        TextView foodNameTextView;
        LinearLayout foodTypesLayout;



        public ViewHolder(View itemView) {
            super(itemView);

            favoriteImageView = (ImageView) itemView.findViewById(R.id.favorite);
            foodNameTextView = (TextView) itemView.findViewById(R.id.food_name);
            foodTypesLayout = (LinearLayout) itemView.findViewById(R.id.food_types_layout);


        }

        @Override
        public void onClick(View v) {

        }
    }



    private Context context;
    private ArrayList<FoodItem> foodItems;

    public FoodRecyclerAdapter(Context context, ArrayList<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FoodItem foodItem = foodItems.get(position); // get the item

        // start setting params

        final TextView foodName = holder.foodNameTextView;
        foodName.setText(foodItem.getName());


        if (foodItem.getFoodTypes() != null && foodItem.getFoodTypes().size() > 0) {

            // make imageViews dynamically, switch on types
            final LinearLayout foodTypesLayout = holder.foodTypesLayout;
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
        final ImageView imageView = holder.favoriteImageView;

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

    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

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
