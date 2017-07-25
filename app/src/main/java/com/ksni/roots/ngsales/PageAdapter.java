package com.ksni.roots.ngsales;

/**
 * Created by #roots on 09/08/2015.
 */
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ksni.roots.ngsales.domain.Attendance;
import com.ksni.roots.ngsales.domain.CallPlan;
import com.ksni.roots.ngsales.domain.ProductData;

public class PageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments =  new ArrayList<Fragment>();

    public void addFragment(Fragment frg){
        fragments.add(frg);
    }
    public PageAdapter(FragmentManager fm) {
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

