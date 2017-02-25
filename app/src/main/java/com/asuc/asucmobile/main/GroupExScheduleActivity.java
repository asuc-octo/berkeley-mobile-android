package com.asuc.asucmobile.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.GroupExController;
import com.asuc.asucmobile.models.GroupExs;
import com.asuc.asucmobile.models.GroupExs.GroupEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                Log.d("deeebug", t.getMessage());
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
        for (Date date : dates) {
            Map<String, String> tempParentMap = new HashMap<>();
            tempParentMap.put("date", groupDateSDF.format(date));
            groupData.add(tempParentMap);

            List<Map<String, String>> tempChildList = new ArrayList<>();


            for (GroupEx groupEx : dateGroupExHashMap.get(date)) {
                Map<String, String> tempChildMap = new HashMap<>();
                tempChildMap.put("name", groupEx.getName());
                tempChildMap.put("trainer", groupEx.getTrainer());
                //TODO: Add location
                tempChildMap.put("location", "");
                String times = childTimeSDF.format(groupEx.getStartTime()) + " / " +
                        childTimeSDF.format(groupEx.getEndTime());
                tempChildMap.put("times", times);
                tempChildList.add(tempChildMap);
            }
            childData.add(tempChildList);
        }
        Log.d("deeebug", "parsemania");
        expandableListView.setAdapter(new SimpleExpandableListAdapter(
                this, groupData, R.layout.group_ex_schedule_group, new String[]{"date"},
                new int[]{R.id.date}, childData, R.layout.group_ex_schedule_child_item,
                new String[]{"name", "trainer", "location", "times"},
                new int[]{R.id.name, R.id.trainer, R.id.location, R.id.times}));

    }


}
