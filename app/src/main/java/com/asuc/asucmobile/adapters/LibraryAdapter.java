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
import com.asuc.asucmobile.models.Library;

import java.util.ArrayList;

public class LibraryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Library> allLibraries;
    private ArrayList<Library> libraries;

    public LibraryAdapter(Context context) {
        this.context = context;

        allLibraries = new ArrayList<Library>();
        libraries = new ArrayList<Library>();
    }

    @Override
    public int getCount() {
        return libraries.size();
    }

    @Override
    public Library getItem(int i) {
        return libraries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Library library = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.facility_row, parent, false);
        }

        TextView libraryName = (TextView) convertView.findViewById(R.id.name);
        TextView libraryAvailability = (TextView) convertView.findViewById(R.id.availability);

        libraryName.setText(library.getName());
        libraryName.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));
        libraryAvailability.setTypeface(Typeface.createFromAsset(context.getAssets(), "young.ttf"));

        if (library.isByAppointment()) {
            libraryAvailability.setTextColor(context.getResources().getColor(R.color.pavan_light));
            libraryAvailability.setText("BY APPOINTMENT");
        } else if (library.isOpen()) {
            libraryAvailability.setTextColor(context.getResources().getColor(R.color.green));
            libraryAvailability.setText("OPEN");
        } else {
            libraryAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            libraryAvailability.setText("CLOSED");
        }

        return convertView;
    }

    /**
     * setList() updates the list of libraries (typically after calling for a refresh).
     *
     * @param list The updated list of libraries.
     */
    public void setList(ArrayList<Library> list) {
        allLibraries = list;
        libraries = list;

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
                ArrayList<Library> filteredLibraries = new ArrayList<Library>();

                if (query == null || query.length() == 0) {
                    filteredLibraries = allLibraries;
                } else {
                    for (Library library : allLibraries) {
                        if (library.getName().toLowerCase().contains(query.toString().toLowerCase()) ||
                                library.getLocation().toLowerCase().contains(query.toString().toLowerCase())) {
                            filteredLibraries.add(library);
                        }
                    }
                }

                results.values = filteredLibraries;
                results.count = filteredLibraries.size();
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                libraries = (ArrayList<Library>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

}
