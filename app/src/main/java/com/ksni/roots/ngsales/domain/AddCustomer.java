package com.ksni.roots.ngsales.domain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class AddCustomer extends AppCompatActivity {
    Intent addCustomer;
    public static CallPlanAdapter adapter;
    private ListView lv;
    private Toolbar toolbar;
    private EditText searchPlan;
    private List<CustomerCall> listCustomer = new ArrayList<CustomerCall>();


    /*private void populateTemplate(String cust){
        MainActivity.dataOrder.clear();
        for(OrderItem itm:MainActivity.dataTemplate){
            if (itm.ref_cust_template.equals(cust)){
                MainActivity.dataOrder.add(itm);
            }
        }
    }*/


    private void refresh(){
        CustomerCall custCall = null;
        DBManager dm = DBManager.getInstance(this);
        List<com.ksni.roots.ngsales.model.Customer> c =com.ksni.roots.ngsales.model.Customer.getCustomer(this,false);
        listCustomer.clear();
        for(com.ksni.roots.ngsales.model.Customer cc:c){
            custCall = new CustomerCall();
            String hari = CustomerCall.getDayByCustomer(dm.database(),cc.getCustomerNumber());
            custCall.setCustomerNumber(cc.getCustomerNumber());
            custCall.setCustomerName(cc.getCustomerName() + " "+hari);
            custCall.setAddress(cc.getAddress());
            //custCall.setJarak(0);
            custCall.setCallStatus("0");
            custCall.setStatus(CustomerCall.NO_VISIT);

            boolean ada = false;
            for(CustomerCall xc: MainActivity.dataCustomerCall){
                if(xc.getCustomerNumber().equals(cc.getCustomerNumber())){
                    ada = true;
                    break;
                }
            }

            if(!ada) listCustomer.add(custCall);
        }

        adapter = new CallPlanAdapter(this, R.layout.ui_customer_item, listCustomer,null);
        lv.setAdapter(adapter);

        //listCustomer.addAll((CustomerPlan) c);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_add_order, menu);
        return true;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_add_customer);
        addCustomer = new Intent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setTitle(Helper.getStrResource(this,R.string.title_add_unplan_customer));

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);



        lv = (ListView)findViewById(R.id.lstCustomer);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final int pos = position;

                com.ksni.roots.ngsales.model.Customer customer = adapter.getItem(pos);
                addCustomer.putExtra("resultAddCust", customer);
                setResult(RESULT_OK, addCustomer);
                finish();


            }
        });

        refresh();

        lv.setTextFilterEnabled(true);
        lv.setEmptyView(findViewById(R.id.list_empty));



        searchPlan =  (EditText)findViewById(R.id.tSearchPlan);


        searchPlan.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }




}
