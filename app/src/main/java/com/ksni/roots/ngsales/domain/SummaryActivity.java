package com.ksni.roots.ngsales.domain;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends Fragment {
    private ProgressBar progressDialog;
    private Button btnRefresh;
    private ListView lv;
    private View rootView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)         {
            //case android.R.id.home:
            //   super.onBackPressed();
            //  break;
            case R.id.action_refresh:

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] summary = new String[] {
                Helper.getStrResource(getActivity(),R.string.summary_by_product_qty_text),
                Helper.getStrResource(getActivity(),R.string.summary_by_product_value_text),
                Helper.getStrResource(getActivity(),R.string.summary_by_customer_qty_text),
                Helper.getStrResource(getActivity(),R.string.summary_by_customer_value_text),
                Helper.getStrResource(getActivity(),R.string.summary_total)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, summary);


        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition = position;
                
                    Intent i = new Intent(getActivity(), ReportSummaryByProduct.class);
                    i.putExtra("report_type", itemPosition);
                    startActivity(i);

            }

        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if (rootView == null) {
        rootView = inflater.inflate(R.layout.ui_summary, container, false);
        lv = (ListView)rootView.findViewById(R.id.lstSummary);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


}
