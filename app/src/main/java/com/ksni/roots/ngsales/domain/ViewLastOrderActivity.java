package com.ksni.roots.ngsales.domain;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Product;

import java.util.List;

/**
 * Created by #roots on 30/11/2015.
 */
public class ViewLastOrderActivity extends AppCompatActivity {
    private ViewLastOrderAdapter adapter = null;
    private ListView lv = null;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_view_last_order);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Product prd = null;
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();

        ab.setTitle("View List Order");

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        List<com.ksni.roots.ngsales.model.OrderHead> ords = null;
        adapter = new ViewLastOrderAdapter(this, R.layout.view_last_order_item, ords);
        lv = (ListView) findViewById(R.id.lstViewLastOrder);
        lv.setAdapter(adapter);
        lv.setEmptyView(findViewById(R.id.list_empty));
        lv.setTextFilterEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_tambah:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
