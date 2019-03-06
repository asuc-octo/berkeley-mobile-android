package com.asuc.asucmobile.domain.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.main.OpenGymActivity;
import com.asuc.asucmobile.domain.models.Gym;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by rustie on 10/4/17.
 */

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTextView;
        ImageView imageView;
        TextView openStatusTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            openStatusTextView = (TextView) itemView.findViewById(R.id.open_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    Gym gym = mGymList.get(getAdapterPosition());
                    OpenGymActivity.setGym(gym);
                    intent = new Intent(mContext, OpenGymActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

    private List<Gym> mGymList;
    private Context mContext;

    /**
     * Sets a GymAdapter for the type of food place specified
     * @param context
     * @param Gyms
     */
    public GymAdapter(Context context, List<Gym> Gyms) {
        mGymList = Gyms;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gym_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView nameTextView = holder.nameTextView;
        ImageView imageView = holder.imageView;
        TextView openStatusTextView = holder.openStatusTextView;

        if (getItem(position).isOpen()) {
            openStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_light));
            openStatusTextView.setText(mContext.getText(R.string.open));
        } else {
            openStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
            openStatusTextView.setText(mContext.getText(R.string.closed));
        }

        nameTextView.setText(getItem(position).getName());
        Glide.with(mContext)
                .load(getItem(position)
                        .getImageUrl()).into(imageView);


    }

    public Gym getItem(int position) {
        return mGymList.get(position);
    }
    

    @Override
    public int getItemCount() {
        return mGymList.size();
    }
}
