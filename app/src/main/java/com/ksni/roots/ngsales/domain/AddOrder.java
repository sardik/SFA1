package com.ksni.roots.ngsales.domain;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.model.ProductBrands;
import com.ksni.roots.ngsales.model.Stock;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class AddOrder extends AppCompatActivity {
//public class AddOrder extends AppCompatActivity {

    Toolbar toolbar;
    ListView lv;
    RecyclerView rvProductBrand;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    DBManager dm;
    ArrayList<ProductBrands> aListProductBrands;

    ProductBrandAdapter brandAdapter;

    //ProductBrandAdapter adapter;
    StockAdapter adapter_stock;

    boolean india = false;
    EditText searchItem;

    private boolean orderCanvas = false;
    private String lastActivity = "AddOrder";

    private List<Product> listProduct = new ArrayList<Product>();
    private List<Stock> listStock = new ArrayList<Stock>();

    private void refresh(String sortBy){

        listStock.clear();

        DBManager dm =  DBManager.getInstance(getApplicationContext());
        List<Stock> ls = Stock.getStockListFromProduct(getApplicationContext(), sortBy);

        listStock.addAll(ls);

        adapter_stock = new StockAdapter(getApplicationContext(), R.layout.ui_stock_item, listStock);
        lv.setAdapter(adapter_stock);

    }

    private void loadProductList(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.ui_product_brand);
        setContentView(R.layout.ui_product_brand);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rvProductBrand = (RecyclerView) findViewById(R.id.rvProductBrand);

        india = getIntent().getBooleanExtra("india",false);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(Helper.getStrResource(this,R.string.transaction_order_title_choose_product)  );
        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        dm = DBManager.getInstance(getApplicationContext());
        aListProductBrands = new ArrayList<ProductBrands>();
        aListProductBrands = ProductBrands.getAllBrands(dm.database());

        layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        rvProductBrand.setLayoutManager(layoutManager);

        //------------------------- BISA PAKE YANG INI -------------------------//
//        adapter = new ProductBrandAdapter(getApplicationContext(), R.layout.ui_product_brand_item, aListProductBrands, "AddOrder", new ProductBrandAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(String idBrand) {
//                Toast.makeText(getApplicationContext(), "Item : " + idBrand, Toast.LENGTH_LONG).show();
//            }
//        });

        //rvProductBrand.setAdapter(adapter);
        //---------------------------- ATAU -----------------------------//
        brandAdapter = new ProductBrandAdapter(getApplicationContext(), R.layout.ui_product_brand_item, aListProductBrands, lastActivity);
        brandAdapter.SetOnItemClickListener(new ProductBrandAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String idBrand, String lastActivity) {
//                Toast.makeText(getApplicationContext(), "Item : " + idBrand, Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(AddOrder.this, ProductDataByBrand.class);
                //intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("id_brand", idBrand);
                intent1.putExtra("last_activity", lastActivity);
                //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivityForResult(intent1, 100);

            }
        });

        rvProductBrand.setAdapter(brandAdapter);
        //-------------------------------------------------------------//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;

            case R.id.action_search:
                Intent intent3 = new Intent(getApplicationContext(), SearchData.class);
                intent3.putExtra("SearchProductCustomer", "product");
                intent3.putExtra("last_activity", lastActivity);
                startActivityForResult(intent3, 100);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        addOrder = new Intent();

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {

                Intent intent2 = new Intent();

                intent2.putExtra("uom", data.getStringExtra("uom"));
                intent2.putExtra("small_uom", data.getStringExtra("small_uom"));
                intent2.putExtra("medium_uom", data.getStringExtra("medium_uom"));
                intent2.putExtra("large_uom", data.getStringExtra("large_uom"));

                intent2.putExtra("uomsmall", data.getStringExtra("uomsmall"));
                intent2.putExtra("uommedium", data.getStringExtra("uommedium"));
                intent2.putExtra("uomlarge", data.getStringExtra("uomlarge"));

                intent2.putExtra("id", data.getStringExtra("id"));
                intent2.putExtra("name", data.getStringExtra("name"));
                intent2.putExtra("division", data.getStringExtra("division"));
                intent2.putExtra("price", data.getIntExtra("price", 0));
                intent2.putExtra("brand", data.getStringExtra("brand"));

                intent2.putExtra("large_to_small", data.getIntExtra("large_to_small", 0));
                intent2.putExtra("medium_to_small", data.getIntExtra("medium_to_small", 0));

                intent2.putExtra("last", data.getStringExtra("last"));
                intent2.putExtra("lastuom", data.getStringExtra("lastuom"));
                intent2.putExtra("stock", data.getStringExtra("stock"));
                intent2.putExtra("stockuom", data.getStringExtra("stockuom"));
                intent2.putExtra("suggest", data.getStringExtra("suggest"));
                intent2.putExtra("suggestuom", data.getStringExtra("suggestuom"));
                intent2.putExtra("order", data.getStringExtra("order"));

                setResult(RESULT_OK, intent2);
                finish();
            }
        }
    }

}
