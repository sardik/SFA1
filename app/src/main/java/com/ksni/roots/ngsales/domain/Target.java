package com.ksni.roots.ngsales.domain;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Target extends Fragment {
    ListView lv;
    private View rootView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ui_summary, container, false);
        lv = (ListView)rootView.findViewById(R.id.lstSummary);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] summary = new String[] {
                Helper.getStrResource(getActivity(),R.string.target_text_summary_product),
                Helper.getStrResource(getActivity(),R.string.target_text_summary_by_product_qty),
                Helper.getStrResource(getActivity(),R.string.target_text_summary_by_product_value),
                Helper.getStrResource(getActivity(),R.string.target_text_summary_by_call_ec_ipt)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, summary);


        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition = position;


                Intent i = new Intent(getActivity(), ReportTarget.class);
                i.putExtra("report_type",itemPosition);
                startActivity(i);


            }

        });

    }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }


}
