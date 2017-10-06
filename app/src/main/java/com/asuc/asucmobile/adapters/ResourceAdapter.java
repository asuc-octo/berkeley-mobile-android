package com.asuc.asucmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.main.ListOfFavorites;
import com.asuc.asucmobile.models.Resources;
import com.asuc.asucmobile.models.Resources.Resource;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import java.util.ArrayList;
import java.util.List;

public class ResourceAdapter extends BaseAdapter {

    private Context context;
    private List<Resource> resources;
    private List<Resource> allResources;

    public ResourceAdapter(Context context) {
        this.context = context;

        allResources = new ArrayList<>();
        resources = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return resources.size();
    }

    @Override
    public Resource getItem(int i) {
        return resources.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public List<Resource> getResources() {
        return resources;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final Resource resource = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.resource_row, parent, false);
        }

        TextView resourceResource = (TextView) convertView.findViewById(R.id.resource);
        TextView resourceTopic = (TextView) convertView.findViewById(R.id.topic);

        resourceResource.setText(resource.getResource());
        resourceTopic.setTextColor(context.getResources().getColor(R.color.pavan_light));

        final ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(context);

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.pre_favorite);
        if (listOfFavorites != null && listOfFavorites.contains(resource.getResource())) {
            imageView.setImageResource(R.drawable.post_favorite);
        } else {
            imageView.setImageResource(R.drawable.pre_favorite);
        }

        //Set the topic.
        resourceTopic.setText(resource.getTopic());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfFavorites != null && listOfFavorites.contains(resource.getResource())) {
                    listOfFavorites.remove(resource.getResource());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.pre_favorite);
                } else if (listOfFavorites != null){
                    listOfFavorites.add(resource.getResource());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.post_favorite);
                }
            }
        });

        return convertView;
    }

    /**
     * setList() updates the list of gyms (typically after calling for a refresh).
     *
     * @param list The updated list of gyms.
     */
    public void setList(List<Resource> list) {
        resources = list;
        allResources = list;
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
                List<Resource> filteredResources = new ArrayList<>();

                if (query == null || query.length() == 0) {
                    filteredResources = allResources;
                } else {
                    for (Resource resource : allResources) {
                        if(resource.getLocation() != null) {
                            if (resource.getResource().toLowerCase().contains(query.toString().toLowerCase()) ||
                                    resource.getLocation().toLowerCase().contains(query.toString().toLowerCase())) {
                                filteredResources.add(resource);
                            }
                        }

                    }
                }

                results.values = filteredResources;
                results.count = filteredResources.size();
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                resources = (ArrayList<Resource>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

}
