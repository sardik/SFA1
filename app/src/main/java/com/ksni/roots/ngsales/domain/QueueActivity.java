package com.ksni.roots.ngsales.domain;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class QueueActivity extends AppCompatActivity {
    private ListView lv;
    private Toolbar toolbar;



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_refresh:
                loadData();
                Helper.notifyQueue(getApplicationContext());
                break;

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_report_per_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setTitle("Log Request");

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);


        lv = (ListView)findViewById(R.id.lstProduct);

        loadData();
    }


    private void loadData(){
        final List<NgantriInformation> lists = NgantriInformation.getListAntrian(getApplicationContext());
        ArrayAdapter<NgantriInformation> adapter = new ArrayAdapter<NgantriInformation> (this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {

            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                NgantriInformation info =  lists.get(position);
                String state ="";
                switch (info.status){
                    case NgantriInformation.STATUS_RELEASE:
                        state = "RELEASE";
                        break;
                    case NgantriInformation.STATUS_ACTIVE:
                        state = "ACTIVE";
                        break;
                    case NgantriInformation.STATUS_DONE:
                        state = "DONE";
                        break;
                    case NgantriInformation.STATUS_RETRY:
                        state = "RETRY";
                        break;
                    case NgantriInformation.STATUS_CANCEL:
                        state = "CANCEL";
                        break;

                }
                text1.setText(info.description+ " " + state);
                text1.setTypeface(Typeface.SANS_SERIF);
                text1.setTextSize(17);

                text2.setText(info.time+ "\n"  );
                text1.setTypeface(Typeface.MONOSPACE);


                return view;
            }

        };

        lv.setAdapter(adapter);

        lv.setEmptyView(findViewById(R.id.list_empty));
    }

}
