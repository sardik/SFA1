package com.ksni.roots.ngsales.domain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Fragment;
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

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.CustomerCall;
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

public class ProductData extends Fragment {
    private Parcelable state;
    private ProductAdapter adapter;
    private ListView lv;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private EditText searchProduct;

    private String id_brand;

    private ArrayList<Product> aListProductByBrand = new ArrayList<Product>();

/*    @Override
    public void onPause() {
        state = lv.onSaveInstanceState();
        super.onPause();
    }
*/

    private void refresh(String sortBy){
        //aListProductByBrand.clear();
        //listProduct.addAll(MainActivity.dataSku);

        DBManager dm =  DBManager.getInstance(getActivity());
        //List<Product> ls = Product.getDataPure(dm.database(), sortBy);

        aListProductByBrand = new ArrayList<Product>();
        aListProductByBrand = Product.getAllProductsByBrand(dm.database(), id_brand);

        //listProduct.addAll(ls);

        adapter = new ProductAdapter(getActivity(), R.layout.ui_product_item, aListProductByBrand,false);
        lv.setAdapter(adapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.ui_product, container, false);
        View rootView = inflater.inflate(R.layout.ui_product, container, false);
        lv = (ListView) rootView.findViewById(R.id.lstProduct);

        //get data extra
        id_brand = getActivity().getIntent().getExtras().getString("id_brand");

        Product item;
        refresh("");

        adapter = new ProductAdapter(getActivity(), R.layout.ui_product_item, aListProductByBrand,false);
        lv.setAdapter(adapter);
        lv.setEmptyView(rootView.findViewById(R.id.list_empty));
        lv.setTextFilterEnabled(true);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                        final int pos =position;
                        Product c = adapter.getItem(pos);
                        Intent i = new Intent(getActivity(),ProductInput.class);
                        DBManager dm = DBManager.getInstance(getActivity());
                        Product cc = Product.getProductData(dm.database(),c.getProcutId());
                        i.putExtra("objPrd",cc);

                        startActivity(i);

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

    // asyn UDA GA DIPAKE
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
                adapter = new ProductAdapter(getActivity(), R.layout.ui_add_item, aListProductByBrand,false);
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
            if (null == aListProductByBrand) {
                aListProductByBrand = new ArrayList<Product>();
            }

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                Product item = new Product();
                //item.setTitle(post.optString("title"));
                //item.setThumbnail(post.optString("thumbnail"));
                item.setProductName(post.optString("productName"));
                item.setProductId(post.optString("productId"));
                item.setUom(post.optString("uom"));
                aListProductByBrand.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
