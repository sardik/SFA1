package com.ksni.roots.ngsales.domain;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;


/**
 * Created by #roots on 29/09/2015.
 */
    public class PagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public PagerAdapter(FragmentManager fm,List<Fragment> fragments) {
            super(fm);
        this.fragments = fragments;

    }


    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }


}
