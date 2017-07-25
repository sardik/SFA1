package com.ksni.roots.ngsales.domain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;

public class AddOrderDetail extends AppCompatActivity {

    private Toolbar toolbar;
    private Product prd;
    private TextView tvProductName;

    private String idProduct;
    private String lastActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_add_order_detail);

        tvProductName = (TextView) findViewById(R.id.textViewProductName);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PRODUCT");

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        idProduct = intent.getStringExtra("id_product");
        lastActivity = intent.getStringExtra("last_activity");

        loadProductList();
    }


    private void loadProductList() {
        String custNumber, custChannel, custZone;
        String custDiv, custSku, custUom;
        Product tempProduct;
        //ArrayList<Product> aListTempProduct;

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        custNumber = session.getString("CUR_VISIT", "");
        custChannel = session.getString("CUR_VISIT_CHANNEL", "");
        custZone = session.getString("CUR_VISIT_ZONE", "");

        DBManager dm =  DBManager.getInstance(AddOrderDetail.this); // atau (getApplicationContext())
        tempProduct = Product.getProductData(dm.database(), idProduct);

        tvProductName.setText(tempProduct.getProductName());

        custDiv = tempProduct.getDivision();
        custSku = tempProduct.getProcutId();
        custUom = tempProduct.getUom();

        Pricing price = new Pricing(dm.database(), custNumber, custChannel, custZone, custDiv, custSku, custUom, 0, 0, Helper.getCurrentDate(), getApplicationContext());

        tempProduct.setPrice(price.getPrice());
    }

}
