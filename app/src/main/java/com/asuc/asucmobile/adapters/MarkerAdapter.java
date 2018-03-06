package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;


import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.customMarker;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexthomas on 6/13/17.
 */

public class MarkerAdapter extends ArrayAdapter<customMarker> {

    private static final String TAG = "MarkerAdapter";
    List<customMarker> markers, tempItems;
    List<customMarker> suggestions = new ArrayList<>();

    public MarkerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<customMarker> objects) {
        super(context, resource, objects);
        this.markers = objects;
        this.tempItems = new ArrayList<>(markers);


    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //this.tempItems = new ArrayList<>(markers);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return markerFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_list, null);
        TextView textView = (TextView) v.findViewById(R.id.markerTitle);
        textView.setText(this.markers.get(position).title);
        return v;

    }

    @Override
    public int getCount() {
        return this.markers.size();
    }


    Filter markerFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((customMarker) resultValue).title;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                if (tempItems.size() < markers.size())
                    tempItems = new ArrayList<>(markers);
                for (customMarker names : tempItems) {
                    if (names.title.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(names);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<customMarker> filterList = (ArrayList<customMarker>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (customMarker names : filterList) {
                    add(names);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
