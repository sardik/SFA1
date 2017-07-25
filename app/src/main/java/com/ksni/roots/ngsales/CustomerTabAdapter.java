package com.ksni.roots.ngsales;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 15/09/2015.
 */
public class CustomerTabAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments =  new ArrayList<Fragment>();

    public void addFragment(Fragment frg){
        fragments.add(frg);
    }
    public CustomerTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "aaa";
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
