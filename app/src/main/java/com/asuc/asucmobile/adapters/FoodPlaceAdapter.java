package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.main.OpenCafeActivity;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.models.Cafe;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodPlace;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rustie on 10/4/17.
 */

public class FoodPlaceAdapter extends RecyclerView.Adapter<FoodPlaceAdapter.ViewHolder> {

    public enum FoodType {
        DiningHall,
        Cafe
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTextView;
        ImageView imageView;
        TextView openStatusTextView;
        TextView hoursTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            openStatusTextView = (TextView) itemView.findViewById(R.id.open_status);
            hoursTextView = (TextView) itemView.findViewById(R.id.hours);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;

                    // set the current cafe/dining hall and start the right intent
                    if (foodType == FoodType.Cafe) {
                        Cafe cafe = (Cafe) mFoodPlaceList.get(getAdapterPosition());
                        OpenCafeActivity.setCafe(cafe);
                        intent = new Intent(mContext, OpenCafeActivity.class);
                        mContext.startActivity(intent);
                    } else if (foodType == FoodType.DiningHall) {
                        DiningHall diningHall = (DiningHall) mFoodPlaceList.get(getAdapterPosition());
                        OpenDiningHallActivity.setDiningHall(diningHall);
                        intent = new Intent(mContext, OpenDiningHallActivity.class);
                        mContext.startActivity(intent);
                    }

                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

    private List<FoodPlace> mFoodPlaceList;
    private Context mContext;
    private FoodType foodType;

    /**
     * Sets a FoodPlaceAdapter for the type of food place specified
     * @param context
     * @param foodPlaces
     * @param type
     */
    public FoodPlaceAdapter(Context context, List<FoodPlace> foodPlaces, FoodType type) {
        mFoodPlaceList = foodPlaces;
        mContext = context;
        foodType = type;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.food_place, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView nameTextView = holder.nameTextView;
        ImageView imageView = holder.imageView;
        TextView openStatusTextView = holder.openStatusTextView;
        TextView hoursTextView = holder.hoursTextView;

        if (getItem(position).isOpen()) {
            openStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_light));
            openStatusTextView.setText(mContext.getText(R.string.open));
        } else {
            openStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
            openStatusTextView.setText(mContext.getText(R.string.closed));
        }

        hoursTextView.setText(getItem(position).getHours());

        nameTextView.setText(getItem(position).getName());
        Glide.with(mContext)
                .load(getItem(position)
                        .getImageUrl()).into(imageView);
    }

    public FoodPlace getItem(int position) {
        return mFoodPlaceList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFoodPlaceList.size();
    }
}
