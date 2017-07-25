package com.ksni.roots.ngsales.domain;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.OrderItem;
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
import java.util.List;

public class Customer extends Fragment {
    public static CallPlanAdapter adapter;
    String customerNumber,customerName,zone,channel,customer_group,division;
    private ListView lv;
    SwipeRefreshLayout srCustomer;

    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private EditText searchPlan;
    AlertDialog dlgWeek;
    private List<CustomerCall> listCustomerCall;
    private List<com.ksni.roots.ngsales.model.Customer> listCustomer;

    DBManager dm;
    SQLiteDatabase db;

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
        List<com.ksni.roots.ngsales.model.Customer> c = com.ksni.roots.ngsales.model.Customer.getCustomer(getActivity(),false);
        listCustomerCall = new ArrayList<CustomerCall>();
        listCustomerCall.clear();
        for(com.ksni.roots.ngsales.model.Customer cc:c){
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_customer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CustomerCall cus = null;
        View rootView = inflater.inflate(R.layout.ui_customer, container, false);
        lv = (ListView) rootView.findViewById(R.id.lstCustomer);
//        srCustomer = (SwipeRefreshLayout) rootView.findViewById(R.id.srCustomer);
        searchPlan =  (EditText)rootView.findViewById(R.id.tSearchPlan);

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                final int pos = position;
//                final CustomerCall call = (CustomerCall) parent.getItemAtPosition(position);
//                Log.e("CUSTOMER", call.getCustomerNumber());
//
//            }
//        });

        refresh();

        adapter = new CallPlanAdapter(getActivity(), R.layout.ui_customer_item, listCustomerCall,null);
        lv.setAdapter(adapter);
        lv.setTextFilterEnabled(true);
        lv.setEmptyView(rootView.findViewById(R.id.list_empty));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final int pos = position;
                CustomerCall c = adapter.getItem(pos);
                Intent i = new Intent(getActivity(),CustomerInput.class);
                //if (c.getCustomerNumber()!=null) Log.e("barcode cust", c.getBarcodeNumber());
                i.putExtra("customer_id", c.getCustomerNumber());

                com.ksni.roots.ngsales.model.Customer cc = com.ksni.roots.ngsales.model.Customer.getCustomer(getActivity(),c.getCustomerNumber());

                i.putExtra("objCust",cc);

                /*
                i.putExtra("name",cc.getCustomerName());
                i.putExtra("alias",cc.getAlias());
                i.putExtra("phone",cc.getPhone());
                i.putExtra("status",cc.getStatus());

                i.putExtra("address",cc.getAddress());
                i.putExtra("city",cc.getCity());
                i.putExtra("lat",cc.getLatitude());
                i.putExtra("long",cc.getLongitude());
                */

                startActivity(i);

            }
        });

//        Deactivated By Obbie 8-Mei-2017
//        srCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshCustomer refresh = new refreshCustomer();
//                refresh.execute(MainActivity.BASE_URL);
//            }
//        });

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

        return rootView;

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

//            case R.id.action_search:
//                Intent in = new Intent(getActivity(), SearchData.class);
//                in.putExtra("SearchProductCustomer", "customer");
//                startActivity(in);
//                break;

            case R.id.action_refresh:
                refresh();
                break;

            case R.id.action_tambah:
                Intent i = new Intent(getActivity(), CustomerInput.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private class refreshCustomer extends AsyncTask<String, String, List<com.ksni.roots.ngsales.model.CustomerCall>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<com.ksni.roots.ngsales.model.CustomerCall> doInBackground(String... params) {

            URL url;
            HttpURLConnection conn;

            Settings settingan = new Settings(getActivity());
            settingan.loadInfo();

            try {
                url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(NgantriInformation.HTTP_CONECTION_TIMEOUT * 1000);
                conn.setReadTimeout(NgantriInformation.HTTP_READ_TIMEOUT * 1000);
                conn.setRequestMethod("POST");

                JSONObject jsonObjRequest = new JSONObject();
                jsonObjRequest.put("command", "sync_master");
                jsonObjRequest.put("salesman_id", settingan.salesman);
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
                    while ((line = buff.readLine()) != null){
                        sbResponse.append(line);
                    }

                    Log.e("GET", "Response data = " + sbResponse);

                    String jsonStringResponse = sbResponse.toString();
                    JSONObject jsonObjResponse = new JSONObject(jsonStringResponse);

                    // Get array product_brands and products from JSON Response
                    JSONArray arrCustomer = jsonObjResponse.getJSONArray("customers");

                    db = dm.getInstance(getActivity()).database();

                    loadCustomer(db, arrCustomer);

                    listCustomer = com.ksni.roots.ngsales.model.Customer.getCustomer(getActivity(),false);

                    listCustomerCall = new ArrayList<CustomerCall>();
                    listCustomerCall.clear();

                    for(com.ksni.roots.ngsales.model.Customer cust:listCustomer){
                        CustomerCall custCall = new CustomerCall();
                        custCall.setCustomerNumber(cust.getCustomerNumber());
                        custCall.setCustomerName(cust.getCustomerName());
                        custCall.setAddress(cust.getAddress());
                        custCall.setJarak(0);
                        custCall.setCallStatus("1");
                        custCall.setStatus("1");
                        listCustomerCall.add(custCall);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return listCustomerCall;
        }

        @Override
        protected void onPostExecute(List<com.ksni.roots.ngsales.model.CustomerCall> result) {
            super.onPostExecute(result);

            srCustomer.setRefreshing(false);

            adapter = new CallPlanAdapter(getActivity(), R.layout.ui_customer_item, listCustomerCall,null);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    final int pos = position;
                    CustomerCall c = adapter.getItem(pos);
                    Intent i = new Intent(getActivity(),CustomerInput.class);
                    i.putExtra("customer_id", c.getCustomerNumber());

                    com.ksni.roots.ngsales.model.Customer cc = com.ksni.roots.ngsales.model.Customer.getCustomer(getActivity(),c.getCustomerNumber());
                    i.putExtra("objCust",cc);

                    startActivity(i);

                }
            });

            srCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshCustomer refresh = new refreshCustomer();
                    refresh.execute(MainActivity.BASE_URL);
                }
            });

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
    }

    private void loadCustomer(SQLiteDatabase db, JSONArray arrCustomer){

        // Delete table sls_customer
        db.delete("sls_customer", null, null);

        if (arrCustomer != null){
            for (int i = 0; i < arrCustomer.length(); i++) {
                try{
                    JSONObject jsonObjCustomer = arrCustomer.getJSONObject(i);

                    // Customer cust = new Customer(db); // Model
                    com.ksni.roots.ngsales.model.Customer cust = new com.ksni.roots.ngsales.model.Customer(db);

                    boolean noNew = false;
                    if (!jsonObjCustomer.isNull("noo_outlet_id")){
                        if (jsonObjCustomer.optString("noo_outlet_id").trim().length()>0)
                            noNew= true;
                    }

                    if( noNew){
                        cust.setCustomerNumber(jsonObjCustomer.optString("noo_ref_id"));
                        cust.setCustomerNumberNew(jsonObjCustomer.optString("noo_outlet_id"));
                        Log.e("UPDATE NOO","NOO");
                    }else{
                        cust.setCustomerNumberNew("");
                        cust.setCustomerNumber(jsonObjCustomer.optString("outlet_id"));
                    }

                    cust.setCustomerName(jsonObjCustomer.optString("name"));
                    cust.setAlias(jsonObjCustomer.optString("alias"));
                    cust.setContact(jsonObjCustomer.optString("contact_person"));
                    cust.setAddress(jsonObjCustomer.optString("address"));
                    cust.setPriceGroup(jsonObjCustomer.optString("outletpricing_group"));
                    //cust.setMultiDist(jsonCust.optString("multi_dist"));
                    cust.setTop(jsonObjCustomer.optString("outlet_top_id"));
                    cust.setDeliveryDay(jsonObjCustomer.optInt("delivery_day"));
                    cust.setCity(jsonObjCustomer.optString("city"));
                    cust.setPhone(jsonObjCustomer.optString("phone_number"));
                    cust.setCustomerGroup(jsonObjCustomer.optString("outlet_group_id"));
                    cust.setClassification(jsonObjCustomer.optString("outlet_classification_id"));
                    cust.setGroupChannel(jsonObjCustomer.optString("channel_group_id"));
                    cust.setChannel(jsonObjCustomer.optString("channel_id"));
                    cust.setCreditLimit(jsonObjCustomer.optDouble("credit_limit"));
                    cust.setBalance(jsonObjCustomer.optDouble("balance"));
                    cust.setNotes(jsonObjCustomer.optString("notes"));
                    cust.setStatus(jsonObjCustomer.optString("status"));
                    cust.setLatitude(jsonObjCustomer.optDouble("latitude"));
                    cust.setLongitude(jsonObjCustomer.optDouble("longitude"));
                    cust.setZone(jsonObjCustomer.optString("zone_id"));
                    cust.setRegion(jsonObjCustomer.optString("region_id"));
                    cust.setDistrict(jsonObjCustomer.optString("district_id"));
                    cust.setTerritory(jsonObjCustomer.optString("territory_id"));
                    cust.setBarcodeNumber(jsonObjCustomer.optString("barcode_number"));

                    cust.save();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        /*Intent intCall = new Intent(PreCall.this,Call.class);
        intCall.putExtra("CUSTOMER_NUMBER", customerNumber);
        intCall.putExtra("CUSTOMER_NAME", customerName);
        startActivity(intCall);
        finish();
        */


        if (requestCode == 0) {
            //if (resultCode == getActivity().RESULT_OK) {
                //String contents = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                SharedPreferences session =getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor e = session.edit();

                //populateTemplate(customerNumber);
                e.putString("CUR_VISIT", customerNumber);
                e.putString("CUR_VISIT_NAME", customerName);
                e.putString("CUR_VISIT_CHANNEL", channel);
                e.putString("CUR_VISIT_ZONE", zone);
                e.putString("CUR_VISIT_DIVISION", division );
                e.putString("CUR_VISIT_GROUP", customer_group);
                e.putLong("CUR_TRANSACTION", -1);

                e.commit();

                for(int i=0;i< Customer.adapter.getCount();i++){
                    CustomerCall cc = Customer.adapter.getItem(i);
                    if (cc.getCustomerNumber().equals(customerNumber)){
                        Customer.adapter.getItem(i).setStatus(CustomerCall.VISIT);
                        Customer.adapter.notifyDataSetChanged();
                        Log.e("bugs",cc.getCustomerNumber()+" - "+customerNumber);
                        break;
                    }
                }

                //Intent intCall = new Intent(getActivity(),Call.class);
                Intent intCall = new Intent(getActivity(),PreCall.class);
                intCall.putExtra("CUSTOMER_NUMBER", customerNumber);
                intCall.putExtra("CUSTOMER_NAME", customerName);

                startActivity(intCall);

                Intent intOrd = new Intent(getActivity(),Order.class);
                intOrd.putExtra("CUSTOMER_NUMBER", customerNumber);
                intOrd.putExtra("CUSTOMER_NAME", customerName);

                startActivity(intOrd);

                //finish();

            //}
        }

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
                adapter = new CallPlanAdapter(getActivity(), R.layout.ui_customer, listCustomerCall,null);
                lv = (ListView)getView().findViewById(R.id.lstCallPlan);
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
            if (null == listCustomerCall) {
                listCustomerCall = new ArrayList<CustomerCall>();
            }

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                CustomerCall item = new CustomerCall();
                //item.setTitle(post.optString("title"));
                //item.setThumbnail(post.optString("thumbnail"));
                item.setCustomerName(post.optString("customerName"));
                item.setAddress(post.optString("customerAddress"));
                item.setCity(post.optString("customerCity"));
                listCustomerCall.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
