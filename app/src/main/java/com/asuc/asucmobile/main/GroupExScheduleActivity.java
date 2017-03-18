package com.asuc.asucmobile.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.GroupExController;
import com.asuc.asucmobile.models.GroupExs;
import com.asuc.asucmobile.models.GroupExs.GroupEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupExScheduleActivity extends BaseActivity {

    private List<GroupEx> groupExs;

    @BindView(R.id.list) ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_ex_schedule);
        setupToolbar("Group Exercise Schedule", true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.hotline_blue));
        ButterKnife.bind(this);
        GroupExController controller = Controller.retrofit.create(GroupExController.class);
        Call<GroupExs> call = controller.getClasses();

        call.enqueue(new Callback<GroupExs>() {
            @Override
            public void onResponse(Call<GroupExs> call, Response<GroupExs> response) {
                if (response.isSuccessful()) {
                    groupExs = response.body().groupExs;
                    parse();
                } else {
                    onFailure(null, null);
                }
            }

            @Override
            public void onFailure(Call<GroupExs> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }
    private void parse() {
        Set<Date> dates = new TreeSet<>();
        Map<Date, List<GroupEx>> dateGroupExHashMap = new TreeMap<>();
        for (GroupEx groupEx : groupExs) {
            Date exDate = groupEx.getDate();
            dates.add(exDate);
            if (!dateGroupExHashMap.containsKey(exDate)) {
                dateGroupExHashMap.put(exDate, new ArrayList<GroupEx>());
            }
            dateGroupExHashMap.get(exDate).add(groupEx);
        }
        SimpleDateFormat groupDateSDF = new SimpleDateFormat("EEEE',' MMMM d", Locale.US);
        SimpleDateFormat childTimeSDF = new SimpleDateFormat("k':'mm aaa", Locale.US);
        List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childData= new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        for (Date date : dates) {
            calendar.setTime(date);
            Map<String, String> tempParentMap = new HashMap<>();
            tempParentMap.put("dow", calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 2));
            tempParentMap.put("date", groupDateSDF.format(date));
            groupData.add(tempParentMap);

            List<Map<String, String>> tempChildList = new ArrayList<>();


            for (GroupEx groupEx : dateGroupExHashMap.get(date)) {
                Map<String, String> tempChildMap = new HashMap<>();
                tempChildMap.put("name", groupEx.getName());
                tempChildMap.put("trainer", "Instructor: " + groupEx.getTrainer());
                String times_location = childTimeSDF.format(groupEx.getStartTime()) + " - " +
                        childTimeSDF.format(groupEx.getEndTime()) + " @ " + groupEx.getLocation();
                tempChildMap.put("times_location", times_location);
                tempChildList.add(tempChildMap);
            }
            childData.add(tempChildList);
        }
        expandableListView.setAdapter(new SimpleExpandableListAdapter(
                this, groupData, R.layout.group_ex_schedule_group, new String[]{"date", "dow"},
                new int[]{R.id.date, R.id.dow}, childData, R.layout.group_ex_schedule_child_item,
                new String[]{"name", "trainer", "times_location"},
                new int[]{R.id.name, R.id.trainer, R.id.times_place}));

        Display newDisplay = getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(width-150, width);
        } else {
            expandableListView.setIndicatorBoundsRelative(width-150, width);
        }

    }

}
