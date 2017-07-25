package com.ksni.roots.ngsales.domain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Channel;
import com.ksni.roots.ngsales.model.Classification;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.DiscountExtByChannel;
import com.ksni.roots.ngsales.model.DiscountExtByChannelByDivision;
import com.ksni.roots.ngsales.model.DiscountExtByChannelIPT;
import com.ksni.roots.ngsales.model.DiscountExtByOutlet;
import com.ksni.roots.ngsales.model.DiscountRegByChannel;
import com.ksni.roots.ngsales.model.DiscountRegByChannelByDivision;
import com.ksni.roots.ngsales.model.DiscountRegByOutlet;
import com.ksni.roots.ngsales.model.DiscountSpcByChannel;
import com.ksni.roots.ngsales.model.DiscountSpcByChannelByDivision;
import com.ksni.roots.ngsales.model.DiscountSpcByOutlet;
import com.ksni.roots.ngsales.model.District;
import com.ksni.roots.ngsales.model.FreeGood1ByChannel;
import com.ksni.roots.ngsales.model.FreeGood1ByOutlet;
import com.ksni.roots.ngsales.model.FreeGood1ByZone;
import com.ksni.roots.ngsales.model.GroupChannel;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.model.PricingByChannel;
import com.ksni.roots.ngsales.model.PricingByOutlet;
import com.ksni.roots.ngsales.model.PricingByZone;
import com.ksni.roots.ngsales.model.ProductBrands;
import com.ksni.roots.ngsales.model.ProductCategory;
import com.ksni.roots.ngsales.model.ProductDivision;
import com.ksni.roots.ngsales.model.Region;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.model.Territory;
import com.ksni.roots.ngsales.model.Zone;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 04/09/2015.
 */
public class SynchronousData {
    private ProgressDialog progressDialog;
    private SQLiteDatabase db;
    private Context context;
    private String company;
    private String sls;

    public SynchronousData(Context context,
                           SQLiteDatabase db,
                           String company,
                           String sls){
        this.db = db;
        this.company = company;
        this.context = context;
        this.sls = sls;

        Log.e("company",company);
        Log.e("sls",sls);

    }



    public void doSync(){
        new AsyncMasterPost().execute(MainActivity.BASE_URL,sls,company);
    }

    public class AsyncMasterPost extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;

            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            try {
                Log.e("URL",params[0]);
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(MainActivity.HTTP_CONECTION_TIMEOUT * 1000);
                urlConnection.setReadTimeout(MainActivity.HTTP_READ_TIMEOUT * 1000);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestMethod("POST");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("command",        "sync_master");
                jsonParam.put("salesman_id",    params[1]);
                jsonParam.put("company_id", params[2]);
                //Log.e("LAST",Settings.getLastModified(context));
                if (!Settings.hasSyncMaster(context)){ // belum pernah disyn, ambil smua
                    jsonParam.put("last_modified", "");
                }else{
                    jsonParam.put("last_modified", Settings.getLastModified(context));
                }

                wr.writeBytes("data=" + jsonParam.toString());
                wr.flush();
                wr.close();

                int statusCode = urlConnection.getResponseCode();
                Log.e("200",String.valueOf(statusCode));
                if (statusCode ==  200) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    Log.e("dd",response.toString());

                    if (parseResult(response.toString()))  result = 1;
                }else{
                    result = 0;
                }

            }
            catch (MalformedURLException e) {result = 0;}
            catch (SocketException ex) {result = 0;}
            catch (SocketTimeoutException ex) {result = 0;}
            catch (ProtocolException e) {result = 0;}
            catch (IOException e) {result = 0;}
            catch (JSONException e) {result = 0;}
            catch (Exception e) {result = 0;}


            return result;


        }

        protected void onProgressUpdate(Integer... progress){
            progressDialog.setProgress(progress[0]);
        }



        private void loadCustomer(SQLiteDatabase db, JSONArray arr){

            if(arr!=null) {
                // customer
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonCust = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Customer cust = new com.ksni.roots.ngsales.model.Customer(db);
                    cust.setCustomerNumber(jsonCust.optString("outlet_id"));
                    cust.setCustomerName(jsonCust.optString("name"));
                    cust.setAlias(jsonCust.optString("alias"));
                    cust.setContact(jsonCust.optString("contact_person"));
                    cust.setAddress(jsonCust.optString("address"));
                    cust.setCity(jsonCust.optString("city"));
                    cust.setPhone(jsonCust.optString("phone_number"));
                    cust.setCustomerGroup(jsonCust.optString("outlet_group_id"));
                    cust.setClassification(jsonCust.optString("outlet_classification_id"));
                    cust.setGroupChannel(jsonCust.optString("channel_group_id"));
                    cust.setChannel(jsonCust.optString("channel_id"));
                    cust.setCreditLimit(jsonCust.optDouble("credit_limit"));
                    cust.setBalance(jsonCust.optDouble("balance"));
                    cust.setNotes(jsonCust.optString("notes"));
                    cust.setStatus(jsonCust.optString("status"));
                    cust.setLatitude(jsonCust.optDouble("latitude"));
                    cust.setLongitude(jsonCust.optDouble("longitude"));
                    cust.setZone(jsonCust.optString("zone_id"));
                    cust.setRegion(jsonCust.optString("region_id"));
                    cust.setDistrict(jsonCust.optString("district_id"));
                    cust.setTerritory(jsonCust.optString("territory_id"));
                    if (jsonCust.optString("status").equals("0"))
                        cust.delete();
                    else
                        cust.save();
                }
            }
        }


        private void loadPricing(JSONObject dPrice,
                                 JSONObject dFreeGood){
            try {



                //free good
                if (dFreeGood!=null){
                    JSONArray  dpArr =  dFreeGood.names();
                    for(int i=0;i<dpArr.length();i++){
                        JSONArray b = dFreeGood.optJSONArray(dpArr.getString(i));
                        for(int j=0;j<b.length();j++){
                            JSONObject a = b.optJSONObject(j);
                            if (a!=null) {
                                Log.e("ADA UYY",dpArr.getString(i));
                                switch (dpArr.getString(i)){

                                    case "outlets": // free by outlet
                                        FreeGood1ByOutlet pbo = new FreeGood1ByOutlet(db);
                                        pbo.outlet_id = a.optString("outlet_id");
                                        pbo.product_id = a.optString("product_id");
                                        pbo.id = a.optInt("free_goods_outlet_id");
                                        pbo.valid_from = a.optString("valid_from");
                                        pbo.valid_to = a.optString("valid_to");
                                        pbo.min_qty  = a.optInt("min_qty");
                                        pbo.buy_qty  = a.optInt("buy_qty");
                                        pbo.uom  = a.optString("uom_id");
                                        pbo.product_free  = a.optString("free_product_id");
                                        pbo.free_qty  = a.optInt("free_qty");
                                        pbo.multiple  = a.optString("multiple");
                                        pbo.free_uom  = a.optString("free_uom_id");
                                        pbo.save();
                                        break;
                                    case "channels": // free by channel
                                        FreeGood1ByChannel pboa = new FreeGood1ByChannel(db);
                                        pboa.channel = a.optString("channel_id");
                                        pboa.product_id = a.optString("product_id");
                                        pboa.id = a.optInt("free_goods_channel_id");
                                        pboa.valid_from = a.optString("valid_from");
                                        pboa.valid_to = a.optString("valid_to");
                                        pboa.min_qty  = a.optInt("min_qty");
                                        pboa.buy_qty  = a.optInt("buy_qty");
                                        pboa.uom  = a.optString("uom_id");
                                        pboa.product_free  = a.optString("free_product_id");
                                        pboa.free_qty  = a.optInt("free_qty");
                                        pboa.multiple  = a.optString("multiple");
                                        pboa.proportional  = a.optString("proportional");
                                        pboa.free_uom  = a.optString("free_uom_id");
                                        pboa.save();
                                        break;
                                    case "zones": // free by zone

                                        FreeGood1ByZone pbob = new FreeGood1ByZone(db);
                                        pbob.zone = a.optString("zone_id");
                                        pbob.product_id = a.optString("product_id");
                                        pbob.id = a.optInt("free_goods_zone_id");
                                        pbob.valid_from = a.optString("valid_from");
                                        pbob.valid_to = a.optString("valid_to");
                                        pbob.min_qty  = a.optInt("min_qty");
                                        pbob.buy_qty  = a.optInt("buy_qty");
                                        pbob.uom  = a.optString("uom_id");
                                        pbob.product_free  = a.optString("free_product_id");
                                        pbob.free_qty  = a.optInt("free_qty");
                                        pbob.multiple  = a.optString("multiple");
                                        pbob.proportional  = a.optString("proportional");
                                        pbob.free_uom  = a.optString("free_uom_id");
                                        pbob.save();
                                        break;

                                }
                            }
                        }
                    }

                }

                // pricing
                if (dPrice!=null){
                    JSONArray  dpArr =  dPrice.names();
                    for(int i=0;i<dpArr.length();i++){
                        JSONArray b = dPrice.optJSONArray(dpArr.getString(i));
                        for(int j=0;j<b.length();j++){
                            JSONObject a = b.optJSONObject(j);
                            if (a!=null) {
                                switch (dpArr.getString(i)){
                                    // PRICING
                                    case "customers": //$pricing_by_outlet
                                        PricingByOutlet pbo = new PricingByOutlet(db);
                                        pbo.outlet_id = a.optString("outlet_id");
                                        pbo.product_id = a.optString("product_id");
                                        pbo.id = a.optInt("pricing_customer_id");
                                        pbo.uom = a.optString("uom_id");
                                        pbo.price  = a.optDouble("price");
                                        pbo.from_value = a.optDouble("from_value");
                                        pbo.to_value = a.optDouble("to_value");
                                        pbo.valid_from = a.optString("valid_from");
                                        pbo.valid_to = a.optString("valid_to");
                                        pbo.save();
                                        break;
                                    case "channels": //$pricing_by_channel
                                        PricingByChannel pbc = new PricingByChannel(db);
                                        pbc.channel = a.optString("channel_id");
                                        pbc.product_id = a.optString("product_id");
                                        pbc.id = a.optInt("pricing_channel_id");
                                        pbc.uom = a.optString("uom_id");
                                        pbc.price  = a.optDouble("price");
                                        pbc.valid_from = a.optString("valid_from");
                                        pbc.valid_to = a.optString("valid_to");
                                        pbc.save();
                                        break;
                                    case "zones": //pricing_by_zone
                                        PricingByZone pbz = new PricingByZone(db);
                                        pbz.zone = a.optString("pricing_zone_id");
                                        pbz.product_id = a.optString("product_id");
                                        pbz.price  = a.optDouble("price_1");
                                        pbz.price  = a.optDouble("price_2");
                                        pbz.id = a.optInt("pricing_id");
                                        pbz.uom = a.optString("uom_id");
                                        pbz.valid_from = a.optString("valid_from");
                                        pbz.valid_to = a.optString("valid_to");
                                        pbz.save();
                                        break;


                                    // REGULAR DISCOUNT
                                    case "reg_outlets": //$reg_discount_by_outlet
                                        DiscountRegByOutlet drbo = new DiscountRegByOutlet(db);
                                        drbo.outlet_id = a.optString("outlet_id");
                                        drbo.product_id = a.optString("product_id");
                                        drbo.discount =a.optDouble("discount");
                                        drbo.valid_from = a.optString("valid_from");
                                        drbo.valid_to = a.optString("valid_to");
                                        drbo.is_qty = a.optString("is_qty");
                                        drbo.id = a.optInt("reg_outlet_id");
                                        drbo.from_qty = a.optInt("from_qty");
                                        drbo.to_qty = a.optInt("to_qty");
                                        drbo.from_value = a.optDouble("from_value");
                                        drbo.to_value = a.optDouble("to_value");
                                        drbo.uom = a.optString("uom_id");

                                        drbo.save();
                                        break;
                                    case "reg_channels"://$reg_discount_by_channel
                                        Log.e("reg_channels","");
                                        DiscountRegByChannel drbc = new DiscountRegByChannel(db);
                                        drbc.channel = a.optString("channel_id");
                                        drbc.product_id = a.optString("product_id");
                                        drbc.discount =a.optDouble("discount");
                                        drbc.valid_from = a.optString("valid_from");
                                        drbc.valid_to = a.optString("valid_to");
                                        drbc.id = a.optInt("reg_channel_id");
                                        drbc.is_qty = a.optString("is_qty");
                                        drbc.from_qty = a.optInt("from_qty");
                                        drbc.to_qty = a.optInt("to_qty");
                                        drbc.from_value = a.optDouble("from_value");
                                        drbc.to_value = a.optDouble("to_value");
                                        drbc.uom = a.optString("uom_id");

                                        drbc.save();
                                        break;
                                    case "reg_channel_divisions": //$reg_discount_by_channel_division
                                        DiscountRegByChannelByDivision drbcd = new DiscountRegByChannelByDivision(db);
                                        drbcd.channel = a.optString("channel_id");
                                        drbcd.division = a.optString("product_division_id");
                                        drbcd.discount =a.optDouble("discount");
                                        drbcd.valid_from = a.optString("valid_from");
                                        drbcd.valid_to = a.optString("valid_to");
                                        drbcd.is_qty = a.optString("is_qty");
                                        drbcd.id = a.optInt("reg_channel_division_id");
                                        drbcd.from_qty = a.optInt("from_qty");
                                        drbcd.to_qty = a.optInt("to_qty");
                                        drbcd.from_value = a.optDouble("from_value");
                                        drbcd.to_value = a.optDouble("to_value");
                                        drbcd.uom = a.optString("uom_id");

                                        drbcd.save();
                                        break;

                                    // EXTRA DISCOUNT
                                    case "ext_outlets": //$ext_discount_by_outlet
                                        Log.e("ext_outlets","ext_outlets");
                                        DiscountExtByOutlet debo = new DiscountExtByOutlet(db);
                                        debo.outlet_id = a.optString("outlet_id");
                                        debo.product_id = a.optString("product_id");
                                        debo.discount =a.optDouble("discount");
                                        debo.valid_from = a.optString("valid_from");
                                        debo.valid_to = a.optString("valid_to");
                                        debo.id = a.optInt("ext_outlet_id");
                                        debo.is_qty = a.optString("is_qty");
                                        debo.from_qty = a.optInt("from_qty");
                                        debo.to_qty = a.optInt("to_qty");
                                        debo.from_value = a.optDouble("from_value");
                                        debo.to_value = a.optDouble("to_value");
                                        debo.uom = a.optString("uom_id");

                                        debo.save();
                                        break;
                                    case "ext_channels": //$ext_discount_by_channel
                                        DiscountExtByChannel debc = new DiscountExtByChannel(db);
                                        debc.channel = a.optString("channel_id");
                                        debc.product_id = a.optString("product_id");
                                        debc.discount =a.optDouble("discount");
                                        debc.valid_from = a.optString("valid_from");
                                        debc.valid_to = a.optString("valid_to");
                                        debc.id = a.optInt("ext_channel_id");
                                        debc.is_qty = a.optString("is_qty");
                                        debc.from_qty = a.optInt("from_qty");
                                        debc.to_qty = a.optInt("to_qty");
                                        debc.from_value = a.optDouble("from_value");
                                        debc.to_value = a.optDouble("to_value");
                                        debc.uom = a.optString("uom_id");

                                        debc.save();
                                        break;
                                    case "ext_channel_divisions"://$ext_discount_by_channel_division
                                        DiscountExtByChannelByDivision debcx = new DiscountExtByChannelByDivision(db);
                                        debcx.channel = a.optString("channel_id");
                                        debcx.division = a.optString("product_division_id");
                                        debcx.discount =a.optDouble("discount");
                                        debcx.valid_from = a.optString("valid_from");
                                        debcx.valid_to = a.optString("valid_to");
                                        debcx.is_qty = a.optString("is_qty");
                                        debcx.id = a.optInt("ext_channel_division_id");
                                        debcx.from_qty = a.optInt("from_qty");
                                        debcx.to_qty = a.optInt("to_qty");
                                        debcx.from_value = a.optDouble("from_value");
                                        debcx.to_value = a.optDouble("to_value");
                                        debcx.uom = a.optString("uom_id");

                                        debcx.save();
                                        break;

                                        case "ext_channel_ipts": //$ext_discount_by_channel_ipt
                                            Log.e("ext_channel_ipts","ada");
                                        DiscountExtByChannelIPT debcxy = new DiscountExtByChannelIPT(db);
                                        debcxy.channel = a.optString("channel_id");

                                        debcxy.valid_from = a.optString("valid_from");
                                        debcxy.valid_to = a.optString("valid_to");
                                        debcxy.id = a.optInt("ext_channel_ipt_id");
                                        debcxy.is_qty = a.optString("is_qty");
                                        debcxy.is_percent = a.optString("is_percent");

                                        if(a.optString("is_percent").equals("1")){
                                            debcxy.discount =a.optDouble("discount");
                                            debcxy.value_ex = 0;
                                        }else{
                                            debcxy.value_ex = a.optDouble("ipt_value");
                                            debcxy.discount =0;
                                        }

                                            debcxy.min_qty = a.optInt("min_qty");
                                            debcxy.min_value = a.optDouble("min_value");
                                            debcxy.ipt = a.optInt("ipt_item");
                                            debcxy.uom = a.optString("uom_id");


                                        debcxy.save();
                                        break;


                                    // SPECIAL DISCOUNT
                                    case "spc_outlets": //$spc_discount_by_outlet
                                        DiscountSpcByOutlet dsbo = new DiscountSpcByOutlet(db);
                                        dsbo.outlet_id = a.optString("outlet_id");
                                        dsbo.product_id = a.optString("product_id");
                                        dsbo.discount =a.optDouble("discount");
                                        dsbo.valid_from = a.optString("valid_from");
                                        dsbo.valid_to = a.optString("valid_to");
                                        dsbo.id = a.optInt("spc_outlet_id");
                                        dsbo.from_qty = a.optInt("from_qty");
                                        dsbo.to_qty = a.optInt("to_qty");
                                        dsbo.is_qty = a.optString("is_qty");
                                        dsbo.from_value = a.optDouble("from_value");
                                        dsbo.to_value = a.optDouble("to_value");
                                        dsbo.uom = a.optString("uom_id");

                                        dsbo.save();
                                        break;
                                    case "spc_channels": //$spc_discount_by_channel
                                        DiscountSpcByChannel dsbc = new DiscountSpcByChannel(db);
                                        dsbc.channel = a.optString("channel_id");
                                        dsbc.product_id = a.optString("product_id");
                                        dsbc.discount =a.optDouble("discount");
                                        dsbc.valid_from = a.optString("valid_from");
                                        dsbc.valid_to = a.optString("valid_to");
                                        dsbc.id = a.optInt("spc_channels_id");
                                        dsbc.is_qty = a.optString("is_qty");
                                        dsbc.from_qty = a.optInt("from_qty");
                                        dsbc.to_qty = a.optInt("to_qty");
                                        dsbc.from_value = a.optDouble("from_value");
                                        dsbc.to_value = a.optDouble("to_value");
                                        dsbc.uom = a.optString("uom_id");

                                        dsbc.save();
                                        break;
                                    case "spc_channel_divisions": //$spc_discount_by_channel_division
                                        DiscountSpcByChannelByDivision dsbcd = new DiscountSpcByChannelByDivision(db);
                                        dsbcd.channel = a.optString("channel_id");
                                        dsbcd.division = a.optString("product_division_id");
                                        dsbcd.discount =a.optDouble("discount");
                                        dsbcd.valid_from = a.optString("valid_from");
                                        dsbcd.valid_to = a.optString("valid_to");
                                        dsbcd.id = a.optInt("spc_channel_division_id");
                                        dsbcd.is_qty = a.optString("is_qty");
                                        dsbcd.from_qty = a.optInt("from_qty");
                                        dsbcd.to_qty = a.optInt("to_qty");
                                        dsbcd.from_value = a.optDouble("from_value");
                                        dsbcd.to_value = a.optDouble("to_value");
                                        dsbcd.uom = a.optString("uom_id");

                                        dsbcd.save();
                                        break;

                                }


                            }
                        }
                    }

                }


                //dm.close();
            }
            catch (JSONException e) {
                //Toast.makeText(getActivity(), "Error while retrieving data structure.", Toast.LENGTH_LONG).show();
                //Log.e("buff",e.toString());

            }
        }

        private void loadProduct(SQLiteDatabase db, JSONArray arr){
            // product

            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    if (jsonSku!=null) {
                        com.ksni.roots.ngsales.model.Product prd = new com.ksni.roots.ngsales.model.Product(db);

                        prd.setProductId(jsonSku.optString("product_id"));
                        prd.setProductName(jsonSku.optString("name"));
                        prd.setAlias(jsonSku.optString("alias_name"));
                        prd.setProductType(jsonSku.optString("product_type_id"));
                        prd.setDivision(jsonSku.optString("product_division_id"));
                        prd.setBrand(jsonSku.optString("product_brand_id"));
                        prd.setPrice(jsonSku.optDouble("price"));
                        prd.setUom(jsonSku.optString("base_uom_id"));
                        prd.setUomSmall(jsonSku.optString("small_uom_id"));
                        prd.setUomMedium(jsonSku.optString("medium_uom_id"));
                        prd.setUomLarge(jsonSku.optString("large_uom_id"));
                        prd.setConversionMediumToSmall(jsonSku.optInt("mts_conversion"));
                        prd.setConversionLargeToSmall(jsonSku.optInt("lts_conversion"));
                        prd.setStatus(jsonSku.optString("status"));
                        prd.setPareto(jsonSku.optString("focus"));

                        //prd.setCategory(jsonSku.optString("product_category"));
                        //prd.save();
                        if (jsonSku.optString("status").equals("0"))
                            prd.delete();
                        else
                            prd.save();

                    }
                }
            }
        }

        private void loadCallPlan(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonSku = arr.optJSONObject(i);

                    com.ksni.roots.ngsales.model.CustomerCall prd = new com.ksni.roots.ngsales.model.CustomerCall(db);
                    prd.setId(jsonSku.optString("id"));
                    prd.setServerDate(jsonSku.optString("date"));
                    prd.setWeek(jsonSku.optString("week"));
                    prd.setSlsId(jsonSku.optString("sls_id"));
                    prd.setCustomerNumber(jsonSku.optString("outlet_id"));
                    prd.setRoute(jsonSku.optString("route"));
                    prd.setSquence(jsonSku.optInt("squence"));
                    prd.setNotes(jsonSku.optString("notes"));
                    prd.setCreditLimit(jsonSku.optDouble("credit_limit"));
                    prd.setStatus(CustomerCall.NO_VISIT);
                    prd.save();
                }
            }

        }

        private void loadCompetitor(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Competitor prd = new com.ksni.roots.ngsales.model.Competitor(db);

                    prd.competitor = jsonSku.optString("competitor_id");
                    prd.description = jsonSku.optString("name");

                    prd.save();
                }
            }

        }


        private void loadChannel(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Channel prd = new Channel(db);
                    prd.setChannel(jsonSku.optString("channel_id"));
                    prd.setGroupChannel(jsonSku.optString("channel_group_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }

        private void loadGroupChannel(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    GroupChannel prd = new GroupChannel(db);
                    prd.setGroupChannel(jsonSku.optString("channel_group_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }

        private void loadRegion(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Region prd = new Region(db);
                    prd.setRegion(jsonSku.optString("region_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }

        private void loadZone(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Zone prd = new Zone(db);
                    prd.setZone(jsonSku.optString("zone_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }


        private void loadClassification(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Classification prd = new Classification(db);
                    prd.setClassification(jsonSku.optString("outlet_classification_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }

        private void loadDistrict(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    District prd = new District(db);
                    prd.setDistrict(jsonSku.optString("district_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }

        private void loadTerritory(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Territory prd = new Territory(db);
                    prd.setTerritory(jsonSku.optString("territory_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }


        private void loadProductDivision(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    ProductDivision prd = new ProductDivision(db);
                    prd.setDivision(jsonSku.optString("product_division_id"));
                    prd.setDescription( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }

        private void loadProductCategory(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    ProductCategory prd = new ProductCategory(db);
                    prd.setCategory(jsonSku.optString("category"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadProductBrands(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    ProductBrands prd = new ProductBrands(db);
                    prd.setBrandsId(jsonSku.optString("product_brand_id"));
                    prd.setBrandsName( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }




        private void loadReason(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Reason prd = new com.ksni.roots.ngsales.model.Reason(db);

                    prd.setReason(jsonSku.optString("reason_id"));
                    prd.setDescription(jsonSku.optString("name"));
                    prd.save();
                }
            }

        }


        private void loadReasonNoBarcode(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.ReasonNoBarcode prd = new com.ksni.roots.ngsales.model.ReasonNoBarcode(db);

                    prd.setReason(jsonSku.optString("reason_id"));
                    prd.setDescription(jsonSku.optString("name"));
                    prd.save();
                }
            }

        }


        private void loadSKUTemplate(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.CustomerSKU prd = new com.ksni.roots.ngsales.model.CustomerSKU(db);

                    prd.product_id = jsonSku.optString("product_id");
                    prd.id =  jsonSku.optString("id");
                    prd.qty_last =  jsonSku.optInt("qty_last");
                    prd.save();
                }
            }

        }

        private boolean parseResult(String result) {
        boolean buff = false;
            try {

                JSONObject response     = new JSONObject(result);

                JSONArray dSku = response.optJSONArray("products");
                if (dSku != null)loadProduct(db,dSku);

                JSONArray dCustomer     = response.optJSONArray("customers");
                if (dCustomer!=null)loadCustomer(db, dCustomer);


                JSONArray dCompetitor     = response.optJSONArray("competitors");
                if (dCompetitor!=null)loadCompetitor(db, dCompetitor);

                JSONArray dReason     = response.optJSONArray("reasons");
                if (dReason!=null)loadReason(db, dReason);


                JSONArray dReasonNoBarcode     = response.optJSONArray("reason_no_barcodes");
                if (dReasonNoBarcode!=null)loadReasonNoBarcode(db, dReasonNoBarcode);


                //JSONArray dTarget     = response.optJSONArray("targets");
                //if (dTarget!=null)loadTarget(db, dTarget);

                JSONArray dChannel     = response.optJSONArray("channels");
                if (dChannel!=null)loadChannel(db, dChannel);

                JSONArray dGChannel     = response.optJSONArray("channel_groups");
                if (dGChannel!=null)loadGroupChannel(db, dGChannel);


                JSONArray dRegion     = response.optJSONArray("regions");
                if (dRegion!=null)loadRegion(db, dRegion);

                JSONArray dZone     = response.optJSONArray("zones");
                if (dZone!=null)loadZone(db, dZone);

                JSONArray dClass     = response.optJSONArray("outlet_classifications");
                if (dClass!=null)loadClassification(db, dClass);

                JSONArray dDistrict     = response.optJSONArray("districts");
                if (dDistrict!=null)loadDistrict(db, dDistrict);

                JSONArray dTerritory     = response.optJSONArray("territories");
                if (dTerritory!=null)loadTerritory(db, dTerritory);

                JSONArray dDivision     = response.optJSONArray("product_divisions");
                if (dDivision!=null)loadProductDivision(db, dDivision);

                JSONArray dBrands     = response.optJSONArray("product_brands");
                if (dBrands!=null)loadProductBrands(db, dBrands);


                // update last syn master
                SharedPreferences session = context.getApplicationContext().getSharedPreferences("ngsales", 0);
                String curSls = session.getString("CUR_SLS", "");
                String curCompany = session.getString("CUR_COMPANY", "");

                String last_mod = response.optString("last_modified");
                Settings.initSyncMaster(context, curSls, last_mod);

                //Log.e("XXXXX",response.optJSONObject("free_goods").toString());

                loadPricing(response.optJSONObject("pricing"), response.optJSONObject("free_goods"));
                //loadPricing(null,response.optJSONObject("free_goods"));
                //loadPricing(response.optJSONObject("pricing"),null);



                //JSONArray dCategory     = response.optJSONArray("category");
                //if (dCategory!=null)loadProductCategory(db, dCategory);

                //JSONArray dCall         = response.optJSONArray("call");
                //if (dCall!=null)loadCallPlan(db, dCall);

                //JSONArray dTemplate     = response.optJSONArray("template");
                //if (dTemplate!=null)loadSKUTemplate(db, dTemplate);

                if (response.optString("success").equals("true")) buff= true;



            } catch (JSONException e) {
                //Log.e("buff",e.getMessage());
                Log.e("buff",e.getMessage());
                buff= false;

            }

            return buff;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.hide();

            if (result == 1) {
                Toast.makeText(context.getApplicationContext(),"Data updated.",Toast.LENGTH_LONG).show();
                //SharedPreferences session = context.getApplicationContext().getSharedPreferences("ngsales", 0);
                //String curSls = session.getString("CUR_SLS", "");
                //String curCompany = session.getString("CUR_COMPANY", "");

            } else {
                Toast.makeText(context.getApplicationContext(),"Error while retrieving data from server",Toast.LENGTH_LONG).show();
            }



        }
    }
}
