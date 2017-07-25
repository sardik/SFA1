package com.ksni.roots.ngsales.domain;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.model.Customer;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by User on 11/17/2016.
 */
public class SearchData extends AppCompatActivity {

    private ListView lvSearchData;
    private Toolbar toolbar;
    private ArrayList<Product> aListProduct;
    private List<CustomerCall> listCustomerCall;
    private List<com.ksni.roots.ngsales.model.Customer> listCustomer;;

    private ProductAdapter productAdapter;
    private CustomerAdapter customerAdapter;
    private CallPlanAdapter callPlanAdapter;

    private int salesType;
    private boolean orderCanvas = false;
    Intent addOrder;
    private String lastActivity;


    DBManager dm;
    SQLiteDatabase db;

    private void loadProductList() {
        String custNumber, custChannel, custZone;
        String custDiv, custSku, custUom;
        ArrayList<Product> tempProduct;
        //ArrayList<Product> aListTempProduct;

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        custNumber = session.getString("CUR_VISIT", "");
        custChannel = session.getString("CUR_VISIT_CHANNEL", "");
        custZone = session.getString("CUR_VISIT_ZONE", "");

        tempProduct = new ArrayList<Product>();
        aListProduct = new ArrayList<Product>();
        dm =  DBManager.getInstance(SearchData.this); // atau (getApplicationContext())
        tempProduct = Product.getAllProducts(dm.database(), "product_id");

        for(Product p:tempProduct) {
            custDiv = p.getDivision();
            custSku = p.getProcutId();
            custUom = p.getUom();

            Pricing price = new Pricing(dm.database(), custNumber, custChannel, custZone, custDiv, custSku, custUom, 0, 0, Helper.getCurrentDate(), getApplicationContext());

            p.setPrice(price.getPrice());

            aListProduct.add(p);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_search_data);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);


        Intent in = getIntent();
        String searchBy = in.getStringExtra("SearchProductCustomer");
        lastActivity = in.getStringExtra("last_activity");

        Log.e("Dari: ", searchBy);

        lvSearchData = (ListView) findViewById(R.id.lvSearchData);

        dm = DBManager.getInstance(getApplicationContext());

        if (searchBy.equals("product")) {

//            aListProduct = new ArrayList<Product>();
//            aListProduct = Product.getAllProducts(dm.database(), "product_id");

            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
            salesType = session.getInt("CUR_SLS_TYPE", 0);

            if (salesType == Order.SALES_TAKING_ORDER) {
                orderCanvas = false;
            } else {
                orderCanvas = true;
            }

            loadProductList();

            if(orderCanvas) {
                productAdapter = new ProductAdapter(getApplicationContext(), R.layout.ui_product_item, aListProduct, true);
            } else {
                productAdapter = new ProductAdapter(getApplicationContext(), R.layout.ui_product_item, aListProduct, false);
            }


            lvSearchData.setAdapter(productAdapter);

            lvSearchData.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (lastActivity.equals("AddOrder")) {
                        // Jika yang dipanggil activity/fragment AddOrder maka ...
                        addOrder = new Intent();

                        int pos = position;
                        Product itemProduct = productAdapter.getItem(pos);

                        addOrder.putExtra("uom", itemProduct.getUom());
                        addOrder.putExtra("small_uom", itemProduct.getUomSmall());
                        addOrder.putExtra("medium_uom", itemProduct.getUomMedium());
                        addOrder.putExtra("large_uom", itemProduct.getUomLarge());

                        addOrder.putExtra("uomsmall", itemProduct.getUomSmall());
                        addOrder.putExtra("uommedium", itemProduct.getUomMedium());
                        addOrder.putExtra("uomlarge", itemProduct.getUomLarge());

                        addOrder.putExtra("id", itemProduct.getProcutId());
                        addOrder.putExtra("name", itemProduct.getProductName());
                        addOrder.putExtra("division", itemProduct.getDivision());
                        addOrder.putExtra("price", itemProduct.getPrice());
                        addOrder.putExtra("brand",itemProduct.getBrand());

                        addOrder.putExtra("large_to_small", itemProduct.getConversionLargeToSmall());
                        addOrder.putExtra("medium_to_small", itemProduct.getConversionMediumToSmall());

                        addOrder.putExtra("last", 0);
                        addOrder.putExtra("lastuom", itemProduct.getUom());
                        addOrder.putExtra("stock", 0);
                        addOrder.putExtra("stockuom", itemProduct.getUom());
                        addOrder.putExtra("suggest", 0);
                        addOrder.putExtra("suggestuom", itemProduct.getUom());
                        addOrder.putExtra("order", 0);

                        setResult(RESULT_OK, addOrder);
                        finish();

                    } else {
                        // Jika yang dipanggil activity/fragment Product maka ga ngapa2in
                    }
                }
            });

        } else if (searchBy.equals("customer")) {
            setCustomerInfo();

//            customerAdapter = new customerAdapter(getApplicationContext(), R.layout.ui_customer_item, listCustomerCall);
            callPlanAdapter = new CallPlanAdapter(getApplicationContext(), R.layout.ui_product_item, listCustomerCall, null);
            lvSearchData.setAdapter(callPlanAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu_search, menu);

//        SearchManager searchManager = (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);
        final MenuItem menuItem = menu.findItem(R.id.ac_search);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(menuItem);
//        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        sv.setIconifiedByDefault(false);
        sv.setIconified(false);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                productAdapter.getFilter().filter(newText);

                return false;
            }
        });

//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setCustomerInfo(){
        CustomerCall custCall = null;

        listCustomerCall = new ArrayList<CustomerCall>();
        listCustomer = com.ksni.roots.ngsales.model.Customer.getCustomer(getApplicationContext(),false);

        for(com.ksni.roots.ngsales.model.Customer cc:listCustomer){
            custCall = new CustomerCall();
            custCall.setCustomerNumber(cc.getCustomerNumber());
            custCall.setCustomerName(cc.getCustomerName());
            custCall.setAddress(cc.getAddress());
            custCall.setJarak(0);
            custCall.setCallStatus("1");
            custCall.setStatus("1");

            listCustomerCall.add(custCall);
        }

    }
}
