package com.asuc.asucmobile.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

import noman.weekcalendar.listener.OnDateClickListener;
import retrofit2.Call;
import retrofit2.Response;

public class GymFragment extends Fragment {
    public static final String TAG = "GymFragment";

    private HashSet<Integer> filter;
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
        filter = new HashSet<>();
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
        allAround.setOnClickListener(new ExerciseFilterOnClickListener(GymClass.ALL_AROUND, R.drawable.opaque_rounded_shape_1,
                getResources().getColor(R.color.card_background), R.drawable.transparent_rounded_shape_1,
                getResources().getColor(R.color.excercise_color_1)));

        cardio = (Button) layout.findViewById(R.id.cardio);
        cardio.setOnClickListener(new ExerciseFilterOnClickListener(GymClass.CARDIO, R.drawable.opaque_rounded_shape_2,
                getResources().getColor(R.color.card_background), R.drawable.transparent_rounded_shape_2,
                getResources().getColor(R.color.excercise_color_2)));

        mind = (Button) layout.findViewById(R.id.mind);
        mind.setOnClickListener(new ExerciseFilterOnClickListener(GymClass.MIND, R.drawable.opaque_rounded_shape_3,
                getResources().getColor(R.color.card_background), R.drawable.transparent_rounded_shape_3,
                getResources().getColor(R.color.excercise_color_3)));

        core = (Button) layout.findViewById(R.id.core);
        core.setOnClickListener(new ExerciseFilterOnClickListener(GymClass.CORE, R.drawable.opaque_rounded_shape_4,
                getResources().getColor(R.color.card_background), R.drawable.transparent_rounded_shape_4,
                getResources().getColor(R.color.excercise_color_4)));

        dance = (Button) layout.findViewById(R.id.dance);
        dance.setOnClickListener(new ExerciseFilterOnClickListener(GymClass.DANCE, R.drawable.opaque_rounded_shape_5,
                getResources().getColor(R.color.card_background), R.drawable.transparent_rounded_shape_5,
                getResources().getColor(R.color.excercise_color_5)));

        strength = (Button) layout.findViewById(R.id.strength);
        strength.setOnClickListener(new ExerciseFilterOnClickListener(GymClass.STRENGTH, R.drawable.opaque_rounded_shape_6,
                getResources().getColor(R.color.card_background), R.drawable.transparent_rounded_shape_6,
                getResources().getColor(R.color.excercise_color_6)));

        aqua = (Button) layout.findViewById(R.id.aqua);
        aqua.setOnClickListener(new ExerciseFilterOnClickListener(GymClass.AQUA, R.drawable.opaque_rounded_shape_7,
                getResources().getColor(R.color.card_background), R.drawable.transparent_rounded_shape_7,
                getResources().getColor(R.color.excercise_color_7)));



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


    /**
     * OnClickListener for each of the class type tags
     */
    class ExerciseFilterOnClickListener implements View.OnClickListener{

        int cBackground;
        int cTextColor;
        int ucBackground;
        int ucTextColor;
        int classType;

        public ExerciseFilterOnClickListener(int type, int clickedBackground, int clickedTextColor ,
                                             int unclickedBackground, int unclickedTextColor) {

            classType = type;
            cBackground = clickedBackground;
            cTextColor = clickedTextColor;
            ucBackground = unclickedBackground;
            ucTextColor = unclickedTextColor;

        }

        @Override
        public void onClick(View v) {
            mFirebaseAnalytics.logEvent("clicked_excercise_filter", bundle);
            Button b = (Button) v;
            if (clickTracker.get(b)) {
                filter.remove(classType);
                b.setBackgroundResource(cBackground);
                b.setTextColor(cTextColor);
                clickTracker.put((Button) v, false);
            } else {
                filter.add(classType);
                b.setBackgroundResource(ucBackground);
                b.setTextColor(ucTextColor);
                clickTracker.put((Button) v, true);
            }
            initClassTable();

        }
    }



}

