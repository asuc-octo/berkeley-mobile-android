package com.asuc.asucmobile.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.FoodPlaceAdapter;
import com.asuc.asucmobile.adapters.GymAdapter;
import com.asuc.asucmobile.controllers.BMRetrofitController;
import com.asuc.asucmobile.models.GymClass;
import com.asuc.asucmobile.models.WeekCalendar;
import com.asuc.asucmobile.models.responses.GymClassesResponse;
import com.asuc.asucmobile.models.responses.GymsResponse;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import noman.weekcalendar.listener.OnDateClickListener;
import retrofit2.Call;
import retrofit2.Response;

public class GymFragment extends Fragment {
    public static final String TAG = "GymFragment";

    private ArrayList<Integer> filter;
    private ArrayList<GymClass> mClasses;
    private HashMap<Button, Boolean> clickTracker;
    private int currentDate;

    private ScrollView gymClassView;
    private RecyclerView gymRecyclerView;
    private Button allAround, cardio, mind, core, dance, strength, aqua;
    private TableLayout table;
    private WeekCalendar calendar;
    private View layout;
    private LayoutInflater inflater;
    private Bundle bundle;

    Call<GymsResponse> gymsCall;
    Call<GymClassesResponse> gymClassesCall;

    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        NavigationGenerator.closeMenu(getActivity());

        mClasses = new ArrayList<>();
        filter = new ArrayList<>();
        clickTracker = new HashMap<>();
        currentDate = new DateTime().getDayOfMonth();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("opened_gym_screen", bundle);

        this.inflater = inflater;
        layout = inflater.inflate(R.layout.fragment_class, container, false);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Gyms");

        gymClassView = (ScrollView) layout.findViewById(R.id.gymClassView);

        table = (TableLayout) layout.findViewById(R.id.class_table);

        calendar = (com.asuc.asucmobile.models.WeekCalendar) layout.findViewById(R.id.weekCalendar);
        calendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
                mFirebaseAnalytics.logEvent("gym_date_clicked", bundle);
                currentDate = dateTime.getDayOfMonth();
                initClassTable();
            }
        });
        calendar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });



        gymRecyclerView = (RecyclerView) layout.findViewById(R.id.listGyms);
        gymRecyclerView.setHasFixedSize(true);
        gymRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

//        createDummyData();

        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        refresh();

        initButtons();

        return layout;
    }
    private void initButtons() {
        allAround = (Button) layout.findViewById(R.id.all_around);
        allAround.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);

                    if (clickTracker.get(allAround)) {
                        filter.remove((Integer) GymClass.ALL_AROUND);
                        allAround.setBackgroundResource(R.drawable.opaque_rounded_shape_1);
                        allAround.setTextColor(getResources().getColor(R.color.card_background));
                        clickTracker.put(allAround, false);
                    } else {
                        filter.add(GymClass.ALL_AROUND);
                        allAround.setBackgroundResource(R.drawable.transparent_rounded_shape_1);
                        allAround.setTextColor(getResources().getColor(R.color.excercise_color_1));
                        clickTracker.put(allAround, true);
                    }
                }
                initClassTable();
                return false;
            }
        });

        cardio = (Button) layout.findViewById(R.id.cardio);
        cardio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);
                    if (clickTracker.get(cardio)) {
                        filter.remove((Integer) GymClass.CARDIO);
                        cardio.setBackgroundResource(R.drawable.opaque_rounded_shape_2);
                        cardio.setTextColor(getResources().getColor(R.color.card_background));
                        clickTracker.put(cardio, false);
                    } else {
                        filter.add(GymClass.CARDIO);
                        cardio.setBackgroundResource(R.drawable.transparent_rounded_shape_2);
                        cardio.setTextColor(getResources().getColor(R.color.excercise_color_2));
                        clickTracker.put(cardio, true);
                    }
                }
                initClassTable();
                return false;
            }
        });

        mind = (Button) layout.findViewById(R.id.mind);
        mind.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);
                    if (clickTracker.get(mind)) {
                        filter.remove((Integer) GymClass.MIND);
                        mind.setBackgroundResource(R.drawable.opaque_rounded_shape_3);
                        mind.setTextColor(getResources().getColor(R.color.card_background));
                        clickTracker.put(mind, false);
                    } else {
                        filter.add(GymClass.MIND);
                        mind.setBackgroundResource(R.drawable.transparent_rounded_shape_3);
                        mind.setTextColor(getResources().getColor(R.color.excercise_color_3));
                        clickTracker.put(mind, true);
                    }
                }
                initClassTable();
                return false;
            }
        });

        core = (Button) layout.findViewById(R.id.core);
        core.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);
                    if (clickTracker.get(core)) {
                        filter.remove((Integer) GymClass.CORE);
                        core.setBackgroundResource(R.drawable.opaque_rounded_shape_4);
                        core.setTextColor(getResources().getColor(R.color.card_background));
                        clickTracker.put(core, false);
                    } else {
                        filter.add(GymClass.CORE);
                        core.setBackgroundResource(R.drawable.transparent_rounded_shape_4);
                        core.setTextColor(getResources().getColor(R.color.excercise_color_4));
                        clickTracker.put(core, true);
                    }
                }
                initClassTable();
                return false;
            }
        });

        dance = (Button) layout.findViewById(R.id.dance);
        dance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);
                    if (clickTracker.get(dance)) {
                        filter.remove((Integer) GymClass.DANCE);
                        dance.setBackgroundResource(R.drawable.opaque_rounded_shape_5);
                        dance.setTextColor(getResources().getColor(R.color.card_background));
                        clickTracker.put(dance, false);
                    } else {
                        filter.add(GymClass.DANCE);
                        dance.setBackgroundResource(R.drawable.transparent_rounded_shape_5);
                        dance.setTextColor(getResources().getColor(R.color.excercise_color_5));
                        clickTracker.put(dance, true);
                    }
                }
                initClassTable();
                return false;
            }
        });

        strength = (Button) layout.findViewById(R.id.strength);
        strength.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);
                    if (clickTracker.get(strength)) {
                        filter.remove((Integer) GymClass.STRENGTH);
                        strength.setBackgroundResource(R.drawable.opaque_rounded_shape_6);
                        strength.setTextColor(getResources().getColor(R.color.card_background));
                        clickTracker.put(strength, false);
                    } else {
                        filter.add(GymClass.STRENGTH);
                        strength.setBackgroundResource(R.drawable.transparent_rounded_shape_6);
                        strength.setTextColor(getResources().getColor(R.color.excercise_color_6));
                        clickTracker.put(strength, true);
                    }
                }
                initClassTable();
                return false;
            }
        });

        aqua = (Button) layout.findViewById(R.id.aqua);
        aqua.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);
                    if (clickTracker.get(aqua)) {
                        filter.remove((Integer) GymClass.AQUA);
                        aqua.setBackgroundResource(R.drawable.opaque_rounded_shape_7);
                        aqua.setTextColor(getResources().getColor(R.color.card_background));
                        clickTracker.put(aqua, false);
                    } else {
                        filter.add(GymClass.AQUA);
                        aqua.setBackgroundResource(R.drawable.transparent_rounded_shape_7);
                        aqua.setTextColor(getResources().getColor(R.color.excercise_color_7));
                        clickTracker.put(aqua, true);
                    }
                }
                initClassTable();
                return false;
            }
        });


        clickTracker.put(allAround, false);
        clickTracker.put(cardio, false);
        clickTracker.put(mind, false);
        clickTracker.put(core, false);
        clickTracker.put(dance, false);
        clickTracker.put(strength, false);
        clickTracker.put(aqua, false);
    }

    private void initClassTable() {
        View titleRow = inflater.inflate(R.layout.title_row, null, false);
        table.removeAllViews();

        int numAdded = 0;
        boolean firstIter = true;
        DateTime currentTime = new DateTime();

        for (GymClass gymClass: mClasses) {
            int eventDay = Integer.parseInt(gymClass.getDate().split("-")[2]);

            if (filter.contains(gymClass.getClassType()) ||
                    currentDate != eventDay) {
                continue;
            }

//           This is the completed class filtering code....
//            else if (eventDay == currentTime.getDayOfMonth()  &&
//                    currentTime.getHourOfDay() > gymClass.getStart().getHours()) {
//                continue;
//            }

            View tr = inflater.inflate(R.layout.class_row, null, false);
            Button filterColor = (Button) tr.findViewById(R.id.filterColor);
            LinearLayout instructorDetails = (LinearLayout) tr.findViewById(R.id.instructorDetails);
            TextView excerciseTime = (TextView) tr.findViewById(R.id.excerciseTime);
            TextView excerciseName = (TextView) tr.findViewById(R.id.excerciseName);
            TextView excerciseTrainer = (TextView) tr.findViewById(R.id.excerciseTrainer);
            TextView excerciseLocation = (TextView) tr.findViewById(R.id.excerciseLocation);

            switch (gymClass.getClassType()) {
                case GymClass.ALL_AROUND:
                    filterColor.setBackgroundColor(getResources().getColor(R.color.excercise_color_1)); break;
                case GymClass.CARDIO:
                    filterColor.setBackgroundColor(getResources().getColor(R.color.excercise_color_2)); break;
                case GymClass.MIND:
                    filterColor.setBackgroundColor(getResources().getColor(R.color.excercise_color_3)); break;
                case GymClass.CORE:
                    filterColor.setBackgroundColor(getResources().getColor(R.color.excercise_color_4)); break;
                case GymClass.DANCE:
                    filterColor.setBackgroundColor(getResources().getColor(R.color.excercise_color_5)); break;
                case GymClass.STRENGTH:
                    filterColor.setBackgroundColor(getResources().getColor(R.color.excercise_color_6)); break;
                case GymClass.AQUA:
                    filterColor.setBackgroundColor(getResources().getColor(R.color.excercise_color_7)); break;
            }


            DateFormat df = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("PST"));

            instructorDetails.getLayoutParams().width = (int) Math.round(Resources.getSystem().getDisplayMetrics().widthPixels / 2.5);

            excerciseTime.setText((df.format(gymClass.getStart()) + " -\n" + df.format(gymClass.getEnd())).trim());
            excerciseName.setText(gymClass.getName());
            excerciseTrainer.setText(gymClass.getTrainer());
            excerciseLocation.setText(gymClass.getLocation());

            if (firstIter) {
                table.addView(titleRow);
                firstIter = false;
            }


            table.addView(tr);
            numAdded++;
        }

        if (numAdded == 0) {
            TableRow tr = (TableRow) inflater.inflate(R.layout.no_class_row, null, false);
            table.addView(tr);
        }
    }


    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the gym list
     * from the web.
     */
    private void refresh() {

        gymsCall = BMRetrofitController.bmapi.callGymsList();
        gymClassesCall = BMRetrofitController.bmapi.callGymClasses();

        gymClassView.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        getGyms();
        getGymClasses();
    }

    private void getGyms() {

        gymsCall.enqueue(new retrofit2.Callback<GymsResponse>() {
            @Override
            public void onResponse(Call<GymsResponse> call, Response<GymsResponse> response) {
                gymClassView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                gymRecyclerView.setAdapter(new GymAdapter(getContext(), response.body().getGyms()));
            }

            @Override
            public void onFailure(Call<GymsResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGymClasses(){
        gymClassesCall.enqueue(new retrofit2.Callback<GymClassesResponse>() {
            @Override
            public void onResponse(Call<GymClassesResponse> call, Response<GymClassesResponse> response) {
                mClasses.addAll(response.body().getAllAround());
                mClasses.addAll(response.body().getAqua());
                mClasses.addAll(response.body().getCardio());
                mClasses.addAll(response.body().getCore());
                mClasses.addAll(response.body().getDance());
                mClasses.addAll(response.body().getMindBody());
                mClasses.addAll(response.body().getStrength());
                Collections.sort(mClasses);
                initClassTable();

            }

            @Override
            public void onFailure(Call<GymClassesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}


