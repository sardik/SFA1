package com.ksni.roots.ngsales;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by #roots on 05/12/2015.
 */
public class CustomNavigationView extends NavigationView {
    public CustomNavigationView(Context context) {
        super(context);
    }

    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Consumes touch in the NavigationView so it doesn't propagate to views below
    public boolean onTouchEvent(MotionEvent me) {
        return true;
    }

    // Inflates header as a child of NavigationView
    public void createHeader(int res) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(res, this, false);
// Consumes touch in the header so it doesn't propagate to menu items below
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        addView(view);
    }

    // Positions and sizes the menu view
    public void sizeMenu(View view, int header_height) {
// Height of header
        //int header_height = (int) getResources().getDimension(R.dimen.nav_header_height);
// Gets required display metrics
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screen_height = displayMetrics.heightPixels;
// Height of menu
        int menu_height = (int) (screen_height - header_height);
// Layout params for menu
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        params.height = menu_height;
        view.setLayoutParams(params);
    }
}