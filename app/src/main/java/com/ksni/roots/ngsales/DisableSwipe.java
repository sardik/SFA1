package com.ksni.roots.ngsales;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by #roots on 09/08/2015.
 */
public class DisableSwipe extends ViewPager {

    private boolean enabled;

    public DisableSwipe(Context context) {
        super(context);
    }

    public DisableSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}