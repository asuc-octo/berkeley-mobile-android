package com.asuc.asucmobile.domain.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexthomas on 5/28/17.
 */

public class MyFragmentPageAdapter extends FragmentPagerAdapter {

    public static int pos = 0;

    private List<Fragment> myFragments;
    private ArrayList<String> categories;
    private Context context;

    public MyFragmentPageAdapter(Context c, FragmentManager fragmentManager, List<Fragment> myFrags) {
        super(fragmentManager);
        myFragments = myFrags;
        this.context = c;
    }

    @Override
    public Fragment getItem(int position) {

        return myFragments.get(position);

    }

    @Override
    public int getCount() {

        return myFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        setPos(position);
        return myFragments.get(position).toString();
    }

    public static int getPos() {
        return pos;
    }

    public void add(Fragment fragment) {
        myFragments.add(fragment);
    }

    public static void setPos(int pos) {
        MyFragmentPageAdapter.pos = pos;
    }
}
