package com.ksni.roots.ngsales.domain;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.LoadingUnloading;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.model.Stock;
import com.ksni.roots.ngsales.util.DBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProductStock extends Fragment {
    private Parcelable state;
    private StockAdapter adapter;
    private ListView lv;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private EditText searchProduct;

    private List<Stock> listStock = new ArrayList<Stock>();;

/*    @Override
    public void onPause() {
        state = lv.onSaveInstanceState();
        super.onPause();
    }
*/

    private void refresh(String sortBy){
        listStock.clear();

        DBManager dm =  DBManager.getInstance(getActivity());
        List<Stock> ls = Stock.getStockListFromProduct(getActivity().getApplicationContext(), sortBy);

        listStock.addAll(ls);

        adapter = new StockAdapter(getActivity(), R.layout.ui_stock_item, listStock);
        lv.setAdapter(adapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.ui_product, container, false);
        View rootView = inflater.inflate(R.layout.ui_stock, container, false);
        lv = (ListView) rootView.findViewById(R.id.lstProduct);

        Product item;
        refresh("");



        /*
        for (int i = 0; i < 20; i++) {
            item = new Product();
            item.setProductId("1");
            item.setProductName("XHadi Kusumah, Toko" + String.valueOf(i));
            item.setUom("CRT");

            listProduct.add(item);
        }
*/

        adapter = new StockAdapter(getActivity(), R.layout.ui_stock_item, listStock);
        lv.setAdapter(adapter);
        lv.setEmptyView(rootView.findViewById(R.id.list_empty));
        lv.setTextFilterEnabled(true);


        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                        final int pos =position;
                        Product c = adapter.getItem(pos);
                        Intent i = new Intent(getActivity(),ProductInput.class);
                        DBManager dm = DBManager.getInstance(getActivity());
                        Product cc = Product.getData(dm.database(),c.getProcutId());
                        i.putExtra("objPrd",cc);

                        startActivity(i);

            }
        });
*/
        searchProduct =  (EditText)rootView.findViewById(R.id.tSearchProduct);


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

        //if(state != null) {
          //  lv.onRestoreInstanceState(state);
        //}

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_load_unload, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)         {
            //case android.R.id.home:
            //   super.onBackPressed();
            //  break;
            case R.id.action_refresh:
                refresh("name");
                break;

            case R.id.menu_sort_by_code:
                refresh("code");
                item.setChecked(true);
                break;

            case R.id.menu_sort_by_alias:
                refresh("alias");
                item.setChecked(true);
                break;

            case R.id.menu_sort_by_name:
                item.setChecked(true);
                refresh("description");
                break;

            case R.id.menu_sort_by_smallest:
                refresh("smallest");
                item.setChecked(true);
                break;

            case R.id.menu_sort_by_largest:
                refresh("largest");
                item.setChecked(true);
                break;


            case R.id.action_loading:
                Intent i= new Intent(getActivity(),CanvasLoadUnload.class);
                i.putExtra("type", LoadingUnloading.TYPE_LOADING);
                startActivity(i);
                break;
            case R.id.action_unloading:
                Intent i2= new Intent(getActivity(),CanvasLoadUnload.class);
                i2.putExtra("type", LoadingUnloading.TYPE_UNLOADING);
                startActivity(i2);
                break;


        }

        return super.onOptionsItemSelected(item);
    }



}
