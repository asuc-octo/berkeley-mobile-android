package com.asuc.asucmobile.domain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.CalendarItem;

import java.util.ArrayList;

public class CalendarAdapter extends ArrayAdapter<CalendarItem> implements View.OnClickListener {

    private ArrayList<CalendarItem> calendarItems;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtDate;
        TextView txtInfo;
    }

    public CalendarAdapter(ArrayList<CalendarItem> data, Context context) {
        super(context, R.layout.calendar_row, data);
        this.calendarItems = data;
        this.mContext  =context;

    }

    //private int lastPosition = -1;

    // there's potential to add on click functionality here for certain view types
    @Override
    public void onClick(View v) {
        return;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // The number of distinct view types the getView() will return.
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getIsHeader()){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CalendarItem dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if (dataModel.getIsHeader()) {
                convertView = inflater.inflate(R.layout.calendar_header, parent, false);
                viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date_header);
                viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.info_header);
            } else {
                convertView = inflater.inflate(R.layout.calendar_row, parent, false);
                viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);
                viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.info);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //lastPosition = position;

        viewHolder.txtDate.setText(dataModel.getDate());
        viewHolder.txtInfo.setText(dataModel.getInfo());

        // Return the completed view to render on screen
        return convertView;
    }

}
