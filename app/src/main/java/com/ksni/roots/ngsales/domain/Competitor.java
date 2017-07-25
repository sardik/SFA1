package com.ksni.roots.ngsales.domain;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.CompetitorEntry;
import com.ksni.roots.ngsales.util.Collection;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by #roots on 07/08/2015.
 */
public class Competitor extends AppCompatActivity{
    private static View systemUIView;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                Spinner cCompet = (Spinner)findViewById(R.id.cCompet );
                EditText tActivity = (EditText)findViewById(R.id.tActivity );
                EditText tProduct = (EditText)findViewById(R.id.tProduct );
                EditText tCost = (EditText)findViewById(R.id.tCost );
                EditText tTimes = (EditText)findViewById(R.id.tTimes );
                EditText tNotes = (EditText)findViewById(R.id.tNotes );

                String err = "";


                if (Helper.isEmpty(cCompet.getSelectedItem().toString()))   {err += Helper.getStrResource(this,R.string.competitor_err_message_blank_competitor)+"\n";}
                if (Helper.isEmpty(tActivity.getText().toString()))         {err += Helper.getStrResource(this,R.string.competitor_err_message_blank_activity)+"\\n";}
                if (Helper.isEmpty(tProduct.getText().toString()))          {err += Helper.getStrResource(this,R.string.competitor_err_message_blank_product)+"\n";}
                if (Helper.isEmpty(tCost.getText().toString()))             {err += Helper.getStrResource(this,R.string.competitor_err_message_blank_cost)+"\n";}
                if (Helper.isEmpty(tTimes.getText().toString()))            {err += Helper.getStrResource(this,R.string.competitor_err_message_blank_times)+"\n";}
                if (Helper.isEmpty(tNotes.getText().toString()))            {err += Helper.getStrResource(this,R.string.competitor_err_message_blank_notes)+"\n";}

        if (err.length() >0){
            Helper.msgbox(this,err,Helper.getStrResource(this,R.string.common_msg_error));
        }else{
            CompetitorEntry cn = new CompetitorEntry(DBManager.getInstance(this).database());
            Collection comp = (Collection)cCompet.getSelectedItem();
            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
            String curSales = session.getString("CUR_SLS", "");
            String curCust = session.getString("CUR_VISIT", "");

            cn.sls_id = curSales;
            cn.competitor = comp.tag.toString();
            cn.activity = tActivity.getText().toString();
            cn.outlet_id= curCust;
            cn.product= tProduct.getText().toString();
            cn.date_visit= Helper.getCurrentDateTime();
            cn.cost= tCost.getText().toString();
            cn.times= tTimes.getText().toString();
            cn.notes= tNotes.getText().toString();
            if (cn.save()){
                Toast.makeText(getApplicationContext(),Helper.getStrResource(this,R.string.competitor_save_ok),  Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),Helper.getStrResource(this,R.string.competitor_save_fail),  Toast.LENGTH_SHORT).show();
            }

        }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void hideSystemUI() {
        systemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUI();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.ui_competitor);


        /*
        systemUIView = getWindow().getDecorView();
        hideSystemUI();

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == 0) {
                                hideSystemUI();
                            }
                        }
                    });
        }*/

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setTitle("Input Competitor");


        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        List<Collection> list = new ArrayList<Collection>();
        DBManager dm = DBManager.getInstance(this);
        Cursor cur = dm.database().rawQuery("SELECT * FROM sls_competitor", null);
        list.add(new Collection("", 0));
        if(cur.moveToFirst()) {
                do {
                    list.add(new Collection(cur.getString(cur.getColumnIndex("description")).toUpperCase(), cur.getString(cur.getColumnIndex("competitor"))));
                } while (cur.moveToNext());

        }

        final ArrayAdapter<Collection> adap = new ArrayAdapter<Collection>(this, android.R.layout.simple_spinner_item, list);

        final Spinner cCompet = (Spinner)findViewById(R.id.cCompet);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        cCompet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cCompet.setAdapter(adap);





    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

}
