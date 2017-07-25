package com.ksni.roots.ngsales;
import java.lang.reflect.Field;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by #roots on 11/08/2015.
 */

    public class nbtiNumberPicker extends NumberPicker {

        public nbtiNumberPicker(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if(child instanceof EditText) {
            //((EditText) child).setBackgroundResource(R.drawable.list_bg);
            ((EditText) child).setTextSize(15);
            ((EditText) child).setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            ((EditText) child).setTextColor(Color.DKGRAY);

        }

    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        if(child instanceof EditText) {
            //((EditText) child).setBackgroundResource(R.drawable.list_bg);
            ((EditText) child).setTextSize(15);

            ((EditText) child).setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            ((EditText) child).setTextColor(Color.DKGRAY);
        }

    }
        @Override
        public void addView(View child) {
            super.addView(child);
            if(child instanceof EditText) {
                //((EditText) child).setBackgroundResource(R.drawable.list_bg);
                ((EditText) child).setTextSize(15);
                ((EditText) child).setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                ((EditText) child).setTextColor(Color.YELLOW);
            }
        }
    }

