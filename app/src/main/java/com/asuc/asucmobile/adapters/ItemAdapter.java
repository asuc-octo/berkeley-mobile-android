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
import com.asuc.asucmobile.models.Items.Item;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private List<Item> allItems;
    private List<Item> items;

    public ItemAdapter(Context context) {
        this.context = context;

        allItems = new ArrayList<>();
        items = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getView(int i, View convertView, ViewGroup parent) {
        final Item item = getItem(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.name);

        itemName.setText(item.getName());

        final ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(context);

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.pre_favorite);
        if (listOfFavorites != null && listOfFavorites.contains(item.getName())) {
            imageView.setImageResource(R.drawable.post_favorite);
        } else {
            imageView.setImageResource(R.drawable.pre_favorite);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfFavorites != null && listOfFavorites.contains(item.getName())) {
                    listOfFavorites.remove(item.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.pre_favorite);
                } else if (listOfFavorites != null){
                    listOfFavorites.add(item.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.post_favorite);
                }
            }
        });

        return convertView;
    }

    /**
     * setList() updates the list of items (typically after calling for a refresh).
     *
     * @param list The updated list of items.
     */
    public void setList(List<Item> list) {
        allItems = list;
        items = new ArrayList<>();

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
                ArrayList<Item> filteredItems = new ArrayList<>();

                if (query == null || query.length() == 0) {
                    //filteredItems = allItems;
                } else {
                    System.out.println("AllItems:");
                    System.out.println(allItems);
                    for (Item item : allItems) {
                        if (item.getName().toLowerCase().contains(query.toString().toLowerCase()) ||
                                item.getCategory().toLowerCase().contains(query.toString().toLowerCase())) {
                            filteredItems.add(item);
                        }
                    }
                }
                results.values = filteredItems;
                results.count = filteredItems.size();
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                items = (ArrayList<Item>) filterResults.values;
                System.out.println(items);
                notifyDataSetChanged();
            }

        };
    }

}
