package com.asuc.asucmobile.domain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.fragments.LibraryFragment;
import com.asuc.asucmobile.domain.main.ListOfFavorites;
import com.asuc.asucmobile.domain.main.OpenLibraryActivity;
import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import java.util.ArrayList;
import java.util.Collections;

public class LibraryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Library> allLibraries;
    private ArrayList<Library> libraries;

    public LibraryAdapter(Context context) {
        this.context = context;

        allLibraries = new ArrayList<>();
        libraries = new ArrayList<>();
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

    public ArrayList<Library> getLibraries() {
        return libraries;
    }


    @Override
    @SuppressWarnings("deprecation")
    public View getView(int i, View convertView, ViewGroup parent) {
        final Library library = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.library_row, parent, false);
        }

        TextView libraryName = (TextView) convertView.findViewById(R.id.name);
        TextView libraryAvailability = (TextView) convertView.findViewById(R.id.availability);

        libraryName.setText(library.getName());

        if (library.isByAppointment()) {
            libraryAvailability.setTextColor(context.getResources().getColor(R.color.pavan_light));
            libraryAvailability.setText(context.getText(R.string.by_appointment));
        } else if (library.isOpen()) {
            libraryAvailability.setTextColor(context.getResources().getColor(R.color.green));
            libraryAvailability.setText(context.getText(R.string.open));
        } else {
            libraryAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            libraryAvailability.setText(context.getText(R.string.closed));
        }

        final ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(context);

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.pre_favorite);
        if (listOfFavorites != null && listOfFavorites.contains(library.getName())) {
            imageView.setImageResource(R.drawable.post_favorite);
        } else {
            imageView.setImageResource(R.drawable.pre_favorite);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfFavorites != null && listOfFavorites.contains(library.getName())) {
                    listOfFavorites.remove(library.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.pre_favorite);
                } else if (listOfFavorites != null){
                    listOfFavorites.add(library.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.post_favorite);
                }

                Collections.sort(getLibraries(), CustomComparators.FacilityComparators.getSortByFavoriteLibraryThenOpenness(OpenLibraryActivity.self_reference));
                LibraryFragment.refreshLists();

            }
        });

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
                ArrayList<Library> filteredLibraries = new ArrayList<>();

                if (query == null || query.length() == 0) {
                    filteredLibraries = allLibraries;
                } else {
                    for (Library library : allLibraries) {
                        if (library.getName().toLowerCase().contains(query.toString().toLowerCase())) {
//                        if (library.getName().toLowerCase().contains(query.toString().toLowerCase()) ||
//                                library.getLocation().toLowerCase().contains(query.toString().toLowerCase())) {
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
