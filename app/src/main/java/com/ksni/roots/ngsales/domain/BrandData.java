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
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.model.ProductBrands;
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

public class BrandData extends Fragment {
    private Parcelable state;
    private BrandAdapter adapter;
    private ListView lv;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private EditText searchProduct;

    private List<ProductBrands> listProductBrand = new ArrayList<ProductBrands>();;

/*    @Override
    public void onPause() {
        state = lv.onSaveInstanceState();
        super.onPause();
    }
*/

    private void refresh(String sortBy){
        listProductBrand.clear();
        //listProduct.addAll(MainActivity.dataSku);

        DBManager dm =  DBManager.getInstance(getActivity());
        List<ProductBrands> ls = ProductBrands.getAllBrands(dm.database());

        listProductBrand.addAll(ls);

        adapter = new BrandAdapter( getActivity(), R.layout.ui_brand_item, listProductBrand,false);
        lv.setAdapter(adapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.ui_product, container, false);
        View rootView = inflater.inflate(R.layout.ui_brand_product, container, false);
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

        adapter = new BrandAdapter(getActivity(), R.layout.ui_brand_item, listProductBrand,false);
        lv.setAdapter(adapter);
        lv.setEmptyView(rootView.findViewById(R.id.list_empty));
        lv.setTextFilterEnabled(true);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


//                        final int pos =position;
//                        ProductBrands c = adapter.getItem(pos);
//                        Intent i = new Intent(getActivity(),ProductInput.class);
//                        DBManager dm = DBManager.getInstance(getActivity());
//                        Product cc = Product.getData(dm.database(),c.getBrands());
//                        i.putExtra("objPrd",cc);
//
//                        startActivity(i);

            }
        });

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
        inflater.inflate(R.menu.menu_product_date, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
        /*setContentView(R.layout.ui_call_plan); */

    //toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
    //setSupportActionBar(toolbar);


    //final ActionBar ab = getSupportActionBar();
    //final ActionBar ab = getActivity().getActionBar();


    //ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    //ab.setDisplayHomeAsUpEnabled(true);

    //new AsyncCallPlan().execute("http://10.1.50.251/and.php");

/*

        lv = (ListView)findViewById(R.id.lstCallPlan);
        CustomerCall item;

        for (int i=0;i<20;i++ ){
            item = new CustomerCall();
            item.setCustomerName("Hadi Kusumah, Toko"+String.valueOf(i));
            item.setAddress("JL. CIjambe Uber");
            item.setCity("Bandung");
            listCallPlan.add(item);
        }


        adapter = new CallPlanAdapter(CallPlan.this, R.layout.ui_call_plan_item, listCallPlan);
        lv.setAdapter(adapter);

*/
    //}

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
                item.setChecked(true);
                refresh("alias");
                break;

            case R.id.menu_sort_by_name:
                item.setChecked(true);
                refresh("name");
                break;



        }

        return super.onOptionsItemSelected(item);
    }

    // asyn

    public class AsyncCallPlan extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            //setProgressBarIndeterminateVisibility(false);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Antosan...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;
            Integer result = 0;
            HttpURLConnection urlConnection = null;

            try {
                /* forming th java.net.URL object */
                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode ==  200) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    Log.e("Bray", response.toString());
                    parseResult(response.toString());
                    result = 1; // Successful
                }else{
                    result = 0; //"Failed to fetch data!";
                }

            } catch (Exception e) {
                Log.e("Bray", e.getLocalizedMessage());
                // Log.d(TAG, e.getLocalizedMessage());
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

            //setProgressBarIndeterminateVisibility(true);


            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }


            /* Download complete. Lets update UI */
            if (result == 1) {
                adapter = new BrandAdapter(getActivity(), R.layout.ui_add_item, listProductBrand,false);
                lv = (ListView)getView().findViewById(R.id.lstAddItem);
                lv.setAdapter(adapter);
            } else {
                Log.e("Bray", "Failed to fetch data!");
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");

            /*Initialize array if null*/
            if (null == listProductBrand) {
                listProductBrand= new ArrayList<ProductBrands>();
            }

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                ProductBrands item = new ProductBrands();
                //item.setTitle(post.optString("title"));
                //item.setThumbnail(post.optString("thumbnail"));
//                item.setProductName(post.optString("productName"));
//                item.setProductId(post.optString("productId"));
//                item.setUom(post.optString("uom"));
                item.setBrandsId(post.optString("brands"));
                listProductBrand.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // asyn

}