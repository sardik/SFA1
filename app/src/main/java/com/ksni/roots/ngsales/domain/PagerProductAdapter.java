package com.ksni.roots.ngsales.domain;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by #roots on 29/09/2015.
 */
    public class PagerProductAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        private ProductGeneral tab1;
        private ProductUom tab2;
        private ProductClassification tab3;

    public PagerProductAdapter(FragmentManager fm, int NumOfTabs,
                               ProductGeneral t1,
                               ProductUom t2,
                               ProductClassification t3) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;

            tab1 = t1;
            tab2 = t2;
            tab3 = t3;

            //tab1 = new CustomerGeneral();
            //tab2 = new CustomerAddress();
            //tab3 = new CustomerTax();

        }


        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    //CustomerGeneral tab1 = new CustomerGeneral();
                    return tab1;
                case 1:
                    //CustomerAddress tab2 = new CustomerAddress();
                    return tab2;
                case 2:
                    //CustomerTax tab3 = new CustomerTax();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
}
