package com.asuc.asucmobile.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Gym;

import java.util.ArrayList;

public class GymAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Gym> gyms;

    public GymAdapter(Context context) {
        this.context = context;

        gyms = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return gyms.size();
    }

    @Override
    public Gym getItem(int i) {
        return gyms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Gym gym = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.facility_row, parent, false);
        }

        TextView gymName = (TextView) convertView.findViewById(R.id.name);
        TextView gymAvailability = (TextView) convertView.findViewById(R.id.availability);

        gymName.setText(gym.getName());

        if (gym.getOpening() == null || gym.getClosing() == null) {
            gymAvailability.setTextColor(context.getResources().getColor(R.color.pavan_light));
            gymAvailability.setText("Unknown hours");
        } else if (gym.isOpen()) {
            gymAvailability.setTextColor(context.getResources().getColor(R.color.green));
            gymAvailability.setText("Open");
        } else {
            gymAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            gymAvailability.setText("Closed");
        }

        View densityRing = convertView.findViewById(R.id.density_ring);
        ProgressBar backgroundBar = (ProgressBar) convertView.findViewById(R.id.backing_ring);
        ProgressBar  percentFullBar = (ProgressBar) convertView.findViewById(R.id.percent_full_bar);
        TextView percentage = (TextView) convertView.findViewById(R.id.percentage);

        densityRing.setVisibility(View.GONE);

        if (gym.getPercentFull() != null) {
            //gym with density + pic
            densityRing.setVisibility(View.VISIBLE);
            ((GradientDrawable) backgroundBar.getProgressDrawable()).setColor(convertView.getResources().getColor(R.color.primary_material_light));
            percentage.setText(gym.getPercentFull().toString() + "%\nFULL");
            ObjectAnimator animation = ObjectAnimator.ofInt(percentFullBar,
                    "progress", 0, gym.getPercentFull());
            animation.setDuration(1000); //in milliseconds
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
        }

        return convertView;
    }

    /**
     * setList() updates the list of gyms (typically after calling for a refresh).
     *
     * @param list The updated list of gyms.
     */
    public void setList(ArrayList<Gym> list) {
        gyms = list;

        notifyDataSetChanged();
    }

}
