package com.ksni.roots.ngsales.domain;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Channel;
import com.ksni.roots.ngsales.model.Classification;
import com.ksni.roots.ngsales.model.Customer;
import com.ksni.roots.ngsales.model.District;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.model.ProductBrands;
import com.ksni.roots.ngsales.model.ProductCategory;
import com.ksni.roots.ngsales.model.ProductDivision;
import com.ksni.roots.ngsales.model.Region;
import com.ksni.roots.ngsales.model.Territory;
import com.ksni.roots.ngsales.model.Zone;
import com.ksni.roots.ngsales.util.Collection;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 15/09/2015.
 */

public class ProductInput extends AppCompatActivity implements ProductGeneral.OnCompleteListener,
                                                               ProductUom.OnCompleteListener,
                                                               ProductClassification.OnCompleteListener{
    private com.ksni.roots.ngsales.domain.PagerProductAdapter adapter;
    private Menu menu;
    private ProductGeneral          tab1 = new ProductGeneral();
    private ProductUom              tab2 = new ProductUom();
    private ProductClassification   tab3 = new ProductClassification();

    @Override
    public void onCompleteProductGeneral() {
        View v = adapter.getItem(0).getView();

        Product c = (Product)getIntent().getSerializableExtra("objPrd");
        if (c!=null) {

            EditText tNo = (EditText) v.findViewById(R.id.tProductNumber);
            EditText tName = (EditText) v.findViewById(R.id.tProductName);
            EditText tAl = (EditText) v.findViewById(R.id.tProductAlias);
            EditText tStatus = (EditText) v.findViewById(R.id.tStatus);

            tNo.setEnabled(false);
            tName.setEnabled(false);
            tAl.setEnabled(false);
            tStatus.setEnabled(false);

            tNo.setText(c.getProcutId());
            tName.setText(c.getProductName());
            tAl.setText(c.getAlias());
            tStatus.setText(c.getStatus());
        }

    }

    @Override
    public void onCompleteProductUom() {
        View v = adapter.getItem(1).getView();

        Product c = (Product)getIntent().getSerializableExtra("objPrd");
        if (c!=null) {
            EditText tBaseUom = (EditText) v.findViewById(R.id.tBaseUom);
            EditText tSmallUom = (EditText) v.findViewById(R.id.tSmallUom);
            EditText tMediumUom = (EditText) v.findViewById(R.id.tMediumUom);
            EditText tLargeUom = (EditText) v.findViewById(R.id.tLargeUom);
            EditText tLarge2Small = (EditText) v.findViewById(R.id.tLarge2Small);
            EditText tMedium2Small = (EditText) v.findViewById(R.id.tMedium2Small);


            tBaseUom.setEnabled(false);
            tSmallUom.setEnabled(false);
            tMediumUom.setEnabled(false);
            tLargeUom.setEnabled(false);
            tLarge2Small.setEnabled(false);
            tMedium2Small.setEnabled(false);

            tBaseUom.setText(c.getUom());
            tSmallUom.setText(c.getUomSmall());
            tMediumUom.setText(c.getUomMedium());
            tLargeUom.setText(c.getUomLarge());

            tLarge2Small.setText(String.valueOf(c.getConversionLargeToSmall()));
            tMedium2Small.setText(String.valueOf(c.getConversionMediumToSmall()));

        }
    }


    @Override
    public void onCompleteProductClassification() {
        View v = adapter.getItem(2).getView();
        Product c = (Product)getIntent().getSerializableExtra("objPrd");
        if (c!=null) {
            DBManager dm = DBManager.getInstance(getApplicationContext());

            ProductBrands brand = ProductBrands.getBrand(dm.database(), c.getBrand());
            if (brand!=null){
                EditText tBrand = (EditText) v.findViewById(R.id.tProductBrands);
                tBrand.setText(brand.getBrandsId() + " " +brand.getBrandsName());
                tBrand.setEnabled(false);
            }


            ProductDivision division = ProductDivision.getData(dm.database(),c.getDivision());
            if (division!=null){
                EditText tDivision = (EditText) v.findViewById(R.id.tProductDivision);
                tDivision.setText(division.getDivision() + " " +division.getDescription());
                tDivision.setEnabled(false);
            }


            ProductCategory category = ProductCategory.getData(dm.database(),c.getCategory());
            if (category!=null){
                EditText tCat = (EditText) v.findViewById(R.id.tProductCategory);
                tCat.setText(category.getCategory() + " " +category.getDescription());
                tCat.setEnabled(false);
            }



        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_input_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        Product c = (Product)getIntent().getSerializableExtra("objPrd");
        if (c!=null)
            ab.setTitle(Helper.getStrResource(this,R.string.product_title_view));
        else
            ab.setTitle(Helper.getStrResource(this,R.string.product_title_create_product));

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(Helper.getStrResource(this, R.string.product_general_tab)));
        tabLayout.addTab(tabLayout.newTab().setText(Helper.getStrResource(this, R.string.product_uom_tab)));
        tabLayout.addTab(tabLayout.newTab().setText(Helper.getStrResource(this, R.string.product_others_tab)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new com.ksni.roots.ngsales.domain.PagerProductAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                tab1,tab2,tab3);


        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        adapter.notifyDataSetChanged();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.menu_customer_add, menu);
        Customer c = (Customer)getIntent().getSerializableExtra("objCust");
        if (c!=null){
            menu.getItem(0).setVisible(false);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}