package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Stop;

import java.util.ArrayList;

public class StopAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Stop> allDestinations;
    private ArrayList<Stop> destinations;

    private double lat;
    private double lng;

    public StopAdapter(Context context, double lat, double lng) {
        this.context = context;
        this.lat = lat;
        this.lng = lng;

        allDestinations = new ArrayList<>();
        destinations = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return destinations.size();
    }

    @Override
    public Stop getItem(int i) {
        return destinations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Stop destination = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.stop_row, parent, false);
        }

        TextView destinationName = (TextView) convertView.findViewById(R.id.stop);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);

        destinationName.setText(destination.getAbbreviatedName());
        destinationName.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
        distance.setText(destination.getDistance(lat, lng) + " mi");
        distance.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));

        return convertView;
    }

    /**
     * setList() updates the list of gyms (typically after calling for a refresh).
     *
     * @param list The updated list of gyms.
     */
    public void setList(ArrayList<Stop> list) {
        allDestinations = list;
        destinations = list;

        notifyDataSetChanged();
    }

    /**
     * getFilter() returns a Filter to be used by an EditText so that the user can filter the list
     * and search for a specific item.
     *
     * @return A filter for the list in this adapter.
     */
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence query) {
                FilterResults results = new FilterResults();
                ArrayList<Stop> filteredDestinations = new ArrayList<>();

                if (query == null || query.length() == 0) {
                    filteredDestinations = allDestinations;
                } else {
                    for (Stop stop : allDestinations) {
                        if (stop.getName().toLowerCase().contains(query.toString().toLowerCase())) {
                            filteredDestinations.add(stop);
                        }
                    }
                }

                results.values = filteredDestinations;
                results.count = filteredDestinations.size();
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                destinations = (ArrayList<Stop>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

}