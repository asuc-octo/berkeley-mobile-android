package com.asuc.asucmobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asuc.asucmobile.adapters.FoodAdapter;
import com.asuc.asucmobile.models.Cafe;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by rustie on 9/28/17.
 */

public class CafeMenuFragment extends Fragment {
    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private static FoodAdapter adapter;
    private static Cafe cafe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
