package com.ksni.roots.ngsales.domain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;

public class ProductDataByBrand extends AppCompatActivity {
    private Parcelable state;
    private ProductAdapter adapter;
    private ListView lv;
    private TextView tvNoData;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private EditText searchProduct;

    private String idBrand;
    private String lastActivity;

    private int salesType;
    Intent addOrder;

    private boolean orderCanvas = false;



    private ArrayList<Product> aListProductByBrand;

//    private void refresh(String sortBy){
//        //aListProductByBrand.clear();
//        //listProduct.addAll(MainActivity.dataSku);
//
//        DBManager dm =  DBManager.getInstance(ProductData2.this);
//        //List<Product> ls = Product.getDataPure(dm.database(), sortBy);
//
//        aListProductByBrand = new ArrayList<Product>();
//        aListProductByBrand = Product.getAllProductsByBrand(dm.database(), id_brand);
//
//        //listProduct.addAll(ls);
//
//        adapter = new ProductAdapter(getApplicationContext(), R.layout.ui_product_item, aListProductByBrand,false);
//        lv.setAdapter(adapter);
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        //return inflater.inflate(R.layout.ui_product, container, false);
//        View rootView = inflater.inflate(R.layout.ui_product, container, false);
//        lv = (ListView) rootView.findViewById(R.id.lstProduct);
//
//        //get data extra
//        id_brand = getActivity().getIntent().getExtras().getString("id_brand");
//
//        refresh("");
//
//        adapter = new ProductAdapter(getActivity(), R.layout.ui_product_item, aListProductByBrand,false);
//        lv.setAdapter(adapter);
//        lv.setEmptyView(rootView.findViewById(R.id.list_empty));
//        lv.setTextFilterEnabled(true);
//
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//
//                        final int pos =position;
//                        Product c = adapter.getItem(pos);
//                        Intent i = new Intent(getActivity(),ProductInput.class);
//                        DBManager dm = DBManager.getInstance(getActivity());
//                        Product cc = Product.getProductData(dm.database(),c.getProcutId());
//                        i.putExtra("objPrd",cc);
//
//                        startActivity(i);
//
//            }
//        });
//
//        searchProduct =  (EditText)rootView.findViewById(R.id.tSearchProduct);
//
//        searchProduct.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//                adapter.getFilter().filter(cs.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                          int arg3) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//            }
//        });
//        return rootView;
//
//    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // Do something that differs the Activity's menu here
//        inflater.inflate(R.menu.menu_product_date, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    private void loadProductList() {
        String custNumber, custChannel, custZone;
        String custDiv, custSku, custUom;
        ArrayList<Product> tempProduct;
        //ArrayList<Product> aListTempProduct;

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        custNumber = session.getString("CUR_VISIT", "");
        custChannel = session.getString("CUR_VISIT_CHANNEL", "");
        custZone = session.getString("CUR_ZONE", "");

        DBManager dm =  DBManager.getInstance(ProductDataByBrand.this); // atau (getApplicationContext())
        tempProduct = Product.getAllProductsByBrand(dm.database(), idBrand);

        aListProductByBrand = new ArrayList<Product>();

        for(Product p:tempProduct) {
            custDiv = p.getDivision();
            custSku = p.getProcutId();
            custUom = p.getUom();

            Pricing price = new Pricing(dm.database(), custNumber, custChannel, custZone, custDiv, custSku, custUom, 0, 0, Helper.getCurrentDate(), getApplicationContext());

            p.setPrice(price.getPrice());

            aListProductByBrand.add(p);
        }

        if (aListProductByBrand.size() == 0){
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_product);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PRODUCT");

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.lstProduct);
        tvNoData = (TextView) findViewById(R.id.list_empty);

        Intent intent = getIntent();
        idBrand = intent.getStringExtra("id_brand");
        lastActivity = intent.getStringExtra("last_activity");

        //refresh("");

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        salesType = session.getInt("CUR_SLS_TYPE", 0);

        if (salesType == Order.SALES_TAKING_ORDER) {
            orderCanvas = false;
        } else {
            orderCanvas = true;
        }

        loadProductList();

        if (orderCanvas) {
            adapter = new ProductAdapter(getApplicationContext(), R.layout.ui_product_item, aListProductByBrand,true);
        } else {
            adapter = new ProductAdapter(getApplicationContext(), R.layout.ui_product_item, aListProductByBrand,false);
        }

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                int pos = position;
//                Product itemProduct = adapter.getItem(pos);
//
//                if (lastActivity.equals("AddOrder")) {
//                    Intent in = new Intent(ProductDataByBrand.this, AddOrderDetail.class);
//                    in.putExtra("id_product", itemProduct.getProcutId());
//                    in.putExtra("last_activity", lastActivity);
//                    startActivityForResult(in, 100);
//                } else {
//
//                }


                if (lastActivity.equals("AddOrder")) {
                    // Jika yang dipanggil activity/fragment AddOrder maka ...

                    int pos = position;
                    Product itemProduct = adapter.getItem(pos);

                    // Cek Pricing
                    if (itemProduct.getPrice() != 0.0) {
                        addOrder = new Intent();

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
                        Toast.makeText(getApplicationContext(), R.string.transaction_order_fail, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Jika yang dipanggil activity/fragment Product maka ga ngapa2in
                }
            }
        });

        searchProduct =  (EditText) findViewById(R.id.tSearchProduct);
        searchProduct.setVisibility(View.GONE);

        searchProduct.addTextChangedListener(new TextWatcher() {

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

        switch(id) {
            case android.R.id.home :
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        switch (id)         {
//            //case android.R.id.home:
//            //   super.onBackPressed();
//            //  break;
//            case R.id.action_refresh:
//                refresh("name");
//                break;
//
//            case R.id.menu_sort_by_code:
//                refresh("code");
//                item.setChecked(true);
//                break;
//
//            case R.id.menu_sort_by_alias:
//                item.setChecked(true);
//                refresh("alias");
//                break;
//
//            case R.id.menu_sort_by_name:
//                item.setChecked(true);
//                refresh("name");
//                break;
//
//
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
