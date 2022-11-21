package com.nomanforhad.finalproject.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> fragments;
    private List<String> labels;

    public TabAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        fragments = new ArrayList<>();
        labels = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String label) {
        fragments.add(fragment);
        labels.add(label);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return labels.get(position);
//        if (position == 0) {
//            return "Students";
//        } else if (position == 1) {
//            return "Assignments";
//        } else {
//            return "Notice";
//        }
    }
}
