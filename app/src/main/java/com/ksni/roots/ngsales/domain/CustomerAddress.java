package com.ksni.roots.ngsales.domain;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ksni.roots.ngsales.R;

/**
 * Created by #roots on 15/09/2015.
 */
public class CustomerAddress extends Fragment{
    private OnCompleteListener mListener;

    public static interface OnCompleteListener {
        public abstract void onCompleteCustomerAddress();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }


    public void onViewCreated (View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mListener.onCompleteCustomerAddress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ui_customer_address, container, false);
        setRetainInstance(true);

        return v;
    }

}
