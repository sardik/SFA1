package com.ksni.roots.ngsales.domain;

import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.model.ProductBrands;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.DBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by User on 9/20/2016.
 */

public class ProductBrandData extends Fragment {

    Toolbar toolbar;
    RecyclerView rvProductBrand;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout srProductBrand;
    ArrayList<ProductBrands> aListProductBrand;

    private String lastActivity = "Product";

    DBManager dm;
    SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override //Create Menu Toolbar
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_product_date, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                Intent in = new Intent(getActivity(), SearchData.class);
                in.putExtra("SearchProductCustomer", "product");
                in.putExtra("last_activity", lastActivity);
                startActivity(in);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.ui_product_brand, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        rvProductBrand = (RecyclerView) view.findViewById(R.id.rvProductBrand);
//        srProductBrand = (SwipeRefreshLayout) view.findViewById(R.id.srProductBrand);

        toolbar.setVisibility(View.GONE);

        dm = DBManager.getInstance(getActivity());
        aListProductBrand = new ArrayList<ProductBrands>();
        aListProductBrand = ProductBrands.getAllBrands(dm.database());

        //layoutManager = new LinearLayoutManager(getActivity());
        //or
        layoutManager = new GridLayoutManager(getActivity(), 1);
        rvProductBrand.setLayoutManager(layoutManager);

        adapter = new ProductBrandAdapter(getActivity(), R.layout.ui_product_brand_item, aListProductBrand, lastActivity);
        rvProductBrand.setAdapter(adapter);

//        Deactivated By Obbie 8-Mei-2017
//        srProductBrand.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //refreshProductBrand();
//
//                refreshBrandAndProduct refresh = new refreshBrandAndProduct();
//                refresh.execute(MainActivity.BASE_URL);
//                //srProductBrand.setRefreshing(false);
//            }
//        });

    }

    private class refreshBrandAndProduct extends AsyncTask <String, String, ArrayList<ProductBrands>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ProductBrands> doInBackground(String... params) {
            URL url;
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            Settings settingan = new Settings(getActivity());
            settingan.loadInfo();

            try{
                url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(NgantriInformation.HTTP_CONECTION_TIMEOUT * 1000);
                conn.setReadTimeout(NgantriInformation.HTTP_READ_TIMEOUT * 1000);
                conn.setRequestMethod("POST");

                JSONObject jsonObjRequest = new JSONObject();
                jsonObjRequest.put("command", "sync_master");
                jsonObjRequest.put("salesman_id",  settingan.salesman);
                jsonObjRequest.put("company_id", settingan.company);
                jsonObjRequest.put("last_modified", "");
                String jsonStringRequest = jsonObjRequest.toString();

                Log.e("SEND", "Request data = " + jsonStringRequest);

                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write("data=" + jsonStringRequest);
                osw.flush();
                osw.close();

                // Check Connection HTTP_OK == 200
                // https://developer.android.com/reference/java/net/HttpURLConnection.html
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader buff = new BufferedReader(isr);
                    StringBuilder sbResponse = new StringBuilder();

                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        sbResponse.append(line);
                    }

                    Log.e("GET", "Response data = " + sbResponse);

                    String jsonStringResponse = sbResponse.toString();
                    JSONObject jsonObjResponse = new JSONObject(jsonStringResponse);

                    // Get array product_brands and products from JSON Response
                    JSONArray arrProductBrands = jsonObjResponse.getJSONArray("product_brands");
                    JSONArray arrProducts = jsonObjResponse.getJSONArray("products");

                    db = dm.getInstance(getActivity()).database();

                    loadProductBrands(db, arrProductBrands);
                    loadProducts(db, arrProducts);

                    aListProductBrand = new ArrayList<ProductBrands>();
                    aListProductBrand = ProductBrands.getAllBrands(dm.database());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return aListProductBrand;
        }

        @Override
        protected void onPostExecute(ArrayList<ProductBrands> result) {
            super.onPostExecute(result);

            srProductBrand.setRefreshing(false);

            //layoutManager = new LinearLayoutManager(getActivity());
            //or
            layoutManager = new GridLayoutManager(getActivity(), 1);
            rvProductBrand.setLayoutManager(layoutManager);

            adapter = new ProductBrandAdapter(getActivity(), R.layout.ui_product_brand_item, result, "Product");
            rvProductBrand.setAdapter(adapter);

            srProductBrand.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //refreshProductBrand();

                    refreshBrandAndProduct refresh = new refreshBrandAndProduct();
                    refresh.execute(MainActivity.BASE_URL);
                    //srProductBrand.setRefreshing(false);
                }
            });
        }
    }

    private void loadProductBrands (SQLiteDatabase db, JSONArray arrProductBrands) {

        // Delete table sls_brand
        db.delete("sls_brand", null, null);

        if (arrProductBrands != null) {
            for (int i = 0; i < arrProductBrands.length(); i++){
                try{
                    JSONObject jsonObjProductBrands = arrProductBrands.getJSONObject(i);

                    ProductBrands prdBrands = new ProductBrands(db);
                    prdBrands.setBrandsId(jsonObjProductBrands.getString("product_brand_id"));
                    prdBrands.setBrandsName(jsonObjProductBrands.getString("name"));

                    prdBrands.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProducts(SQLiteDatabase db, JSONArray arrProducts) {

        // Delete table sls_product
        db.delete("sls_product", null, null);

        if (arrProducts != null) {
            for (int i = 0; i < arrProducts.length(); i++){
                try{
                    JSONObject jsonObjProducts = arrProducts.getJSONObject(i);

                    Product prd = new Product(db);
                    prd.setProductId(jsonObjProducts.optString("product_id"));
                    prd.setProductName(jsonObjProducts.optString("name"));
                    prd.setAlias(jsonObjProducts.optString("alias_name"));
                    prd.setProductType(jsonObjProducts.optString("product_type_id"));
                    prd.setDivision(jsonObjProducts.optString("product_division_id"));
                    prd.setBrand(jsonObjProducts.optString("product_brand_id"));
                    prd.setPrice(jsonObjProducts.optDouble("price"));
                    prd.setUom(jsonObjProducts.optString("base_uom_id"));
                    prd.setUomSmall(jsonObjProducts.optString("small_uom_id"));
                    prd.setUomMedium(jsonObjProducts.optString("medium_uom_id"));
                    prd.setUomLarge(jsonObjProducts.optString("large_uom_id"));
                    prd.setConversionMediumToSmall(jsonObjProducts.optInt("mts_conversion"));
                    prd.setConversionLargeToSmall(jsonObjProducts.optInt("lts_conversion"));
                    prd.setStatus(jsonObjProducts.optString("status"));
                    prd.setPareto(jsonObjProducts.optString("focus"));

                    prd.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
