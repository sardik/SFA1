package com.ksni.roots.ngsales.domain;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.model.Customer;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import com.ksni.roots.ngsales.util.JSON;

//import org.apache.http.entity.StringEntity;
//import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by #roots on 04/09/2015.
 */
public class Synchronous {
    private ProgressDialog progressDialog;
    private SQLiteDatabase db;
    private Context context;
    private String company;
    private String salesman;
    //final static String URL = "http://192.168.43.120/sls/sls/write";
    //final static String URL = "http://10.1.50.166/sls/sls/write";
    //public static final String URL = "http://10.1.20.223/sls/write";

    public Synchronous(Context context,
                       SQLiteDatabase db,
                       String company,
                       String salesman){
        this.db = db;
        this.company = company;
        this.salesman = salesman;
        this.context = context;

    }

    public void syncMaster(){

    }

    public void post(){
        List<Long> listId = new ArrayList<Long>();
        Cursor cur = db.rawQuery("SELECT order_id FROM sls_order where status = 0 and end_call is not null and locked = 0", null);

        if (cur.moveToFirst()) {
            do{
                listId.add(cur.getLong(cur.getColumnIndex("order_id")));
            }while (cur.moveToNext());
        }
        cur.close();

        for (long l:listId){
            postData(l,null);
            break; // satu2 aja
        }

    }

    private JSONArray jCompetitor(String cust){
        JSONObject param = null;
        JSONArray jsonArr =  new JSONArray();
        try {
            DBManager dm = DBManager.getInstance(context);
            Cursor cur = dm.database().rawQuery("SELECT * FROM sls_competitor_entry WHERE outlet_id=?", new String[]{cust});
            if (cur.moveToFirst()) {
                do {
                    param = new JSONObject();
                    param .put("id", cur.getString(cur.getColumnIndex("id")));
                    param .put("outlet_id", cur.getString(cur.getColumnIndex("outlet_id")));
                    param .put("date_visit", cur.getString(cur.getColumnIndex("date_visit")));
                    param .put("competitor", cur.getString(cur.getColumnIndex("competitor")));
                    param .put("activity", cur.getString(cur.getColumnIndex("activity")));
                    param .put("product", cur.getString(cur.getColumnIndex("product")));
                    param .put("times", cur.getString(cur.getColumnIndex("times")));
                    param .put("notes", cur.getString(cur.getColumnIndex("notes")));
                    jsonArr.put(param);
                } while (cur.moveToNext());
            }
            cur.close();
            return jsonArr;
        }catch (JSONException e){
            return null;
        }

    }

    public void sendGPSlocation(float batre,double latitude,double longitude) {

        //start request gps to server
        JSONObject jsonReady = new JSONObject();
        NgantriInformation ngantri = new NgantriInformation(context);
        ngantri.key = NgantriInformation.KEY_GPS;
        try{
            jsonReady.put("company_id", company);
            jsonReady.put("salesman_id", salesman);
            jsonReady.put("command", "tracking");
            jsonReady.put("latitude", latitude);
            jsonReady.put("longitude", longitude);
            jsonReady.put("battery", batre);
            jsonReady.put("time", Helper.getCurrentDateTime());

            ngantri.data = jsonReady.toString();
            ngantri.value = company+"_"+salesman;
            ngantri.description = "sending gps " +company+"_"+salesman;
            ngantri.addAntrian();
        } catch(Exception x){}
        //end request gps to server
    }

    public JSONObject extract(Context ctx,int id) {
        JSONObject ok = null;
        String sign = "";
        String orderNumberServer = "";
        JSONObject jsonParam = null;
        JSONObject jsonParam2 = null;
        String cust = "";
        JSONArray jsonArr = new JSONArray();
        JSONObject jsonReady = new JSONObject();
        boolean ada = false;

        SharedPreferences session = context.getSharedPreferences("ngsales", 0);
        String company = session.getString("CUR_COMPANY", "");
        String cursls = session.getString("CUR_SLS", "");

        int order_type = -1;

        String sql =
                "SELECT " +
                        "sls_order.order_id, " +
                        "sls_order.order_type, " +
                        "sls_order.start_call, " +
                        "sls_order.end_call, " +
                        "sls_order.order_id, " +
                        "sls_order.duration, " +
                        "sls_order.order_date, " +
                        "sls_order.outlet_id, " +
                        "sls_order.sls_id, " +
                        "sls_order.notes, " +
                        "sls_order.ppn, " +
                        "sls_order.sub_total, " +
                        "sls_order.total_discount, " +
                        "sls_order.grand_total, " +
                        "sls_order.reason, " +
                        "sls_order.`week`, " +
                        "sls_order.date_create, " +
                        "sls_order.outlet_top_id, " +
                        "sls_order.imei, " +
                        "sls_order.latitude, " +
                        "sls_order.longitude, " +

                        "sls_order_item.product_id, " +
                        "sls_order_item.item, " +
                        "sls_order_item.item_type, " +
                        "sls_order_item.ref_item, " +
                        "sls_order_item.description, " +
                        "sls_order_item.qty, " +
                        "sls_order.pic_outlet,sls_order.signature,sls_order_item.uom, " +
                        "sls_order_item.current_stock, " +
                        "sls_order_item.current_stock_uom, " +
                        "sls_order_item.small_uom, " +
                        "sls_order_item.medium_uom, " +
                        "sls_order_item.large_uom, " +
                        "sls_order_item.price, " +
                        "sls_order_item.total_gross, " +
                        "sls_order_item.reason_return, " +
                        "sls_order_item.regular_discount, " +
                        "sls_order_item.extra_discount, " +
                        "sls_order_item.special_discount, " +
                        "sls_order_item.total_net, " +
                        "sls_order_item.last_stock, " +
                        "sls_order_item.last_stock_uom, " +
                        "sls_order_item.medium_to_small, " +
                        "sls_order_item.large_to_small " +
                        "FROM " +
                        "sls_order " +
                        "left Join sls_order_item ON sls_order.order_id = sls_order_item.order_id " +
                        "WHERE sls_order.status =0 and sls_order.order_id=?  ORDER BY sls_order.order_id";
        try {

            Cursor cur = db.rawQuery(sql, new String[]{String.valueOf(id)});
            if (cur.moveToFirst()) {
                // header bray
                cust = cur.getString(cur.getColumnIndex("outlet_id"));
                jsonParam = new JSONObject();

                order_type = cur.getInt(cur.getColumnIndex("order_type"));

                jsonParam.put("company_id", company);
                jsonParam.put("order_id", cur.getInt(cur.getColumnIndex("order_id")));
                jsonParam.put("order_date", cur.getString(cur.getColumnIndex("order_date")));
                jsonParam.put("outlet_id", cur.getString(cur.getColumnIndex("outlet_id")));
                jsonParam.put("week", cur.getString(cur.getColumnIndex("week")));

                jsonParam.put("pause_duration", cur.getLong(cur.getColumnIndex("duration")));


                jsonParam.put("outlet_top_id", cur.getString(cur.getColumnIndex("outlet_top_id")));
                jsonParam.put("sls_id", cur.getString(cur.getColumnIndex("sls_id")));
                jsonParam.put("notes", cur.getString(cur.getColumnIndex("notes")));
                jsonParam.put("ppn", cur.getDouble(cur.getColumnIndex("ppn")));
                jsonParam.put("sub_total", cur.getDouble(cur.getColumnIndex("sub_total")));
                jsonParam.put("total_discount", cur.getDouble(cur.getColumnIndex("total_discount")));
                jsonParam.put("grand_total", cur.getDouble(cur.getColumnIndex("grand_total")));
                jsonParam.put("date_create", cur.getString(cur.getColumnIndex("date_create")));
                jsonParam.put("reason", cur.getString(cur.getColumnIndex("reason")));

                jsonParam.put("imei", cur.getString(cur.getColumnIndex("imei")));
                jsonParam.put("latitude", cur.getString(cur.getColumnIndex("latitude")));
                jsonParam.put("longitude", cur.getString(cur.getColumnIndex("longitude")));

                jsonParam.put("start_call", cur.getString(cur.getColumnIndex("start_call")));
                jsonParam.put("end_call", cur.getString(cur.getColumnIndex("end_call")));

                jsonReady.put("command", "order");
                jsonReady.put("company_id", company);
                jsonReady.put("salesman_id", cursls);
                jsonReady.put("header", jsonParam);


                //if (gambar!=null) {
                //  jsonReady.put("img", gambar);
                //}

                jsonReady.put("picture_outlet", "");


                if (!cur.isNull(cur.getColumnIndex("signature"))) {
                    sign = cur.getString(cur.getColumnIndex("signature"));
                    jsonReady.put("picture_signature", cur.getString(cur.getColumnIndex("signature")));
                    //jsonReady.put("picture_signature", "");
                } else {
                    jsonReady.put("picture_signature", "");
                }


                ada = false;
                do {
                    ada = true;
                    // detail bro
                    jsonParam2 = new JSONObject();
                    if (cur.getString(cur.getColumnIndex("product_id")) != null) {
                        jsonParam2.put("product_id", cur.getString(cur.getColumnIndex("product_id")));
                        jsonParam2.put("item", cur.getString(cur.getColumnIndex("item")));
                        jsonParam2.put("description", cur.getString(cur.getColumnIndex("description")));
                        jsonParam2.put("qty", cur.getDouble(cur.getColumnIndex("qty")));
                        jsonParam2.put("uom", cur.getString(cur.getColumnIndex("uom")));
                        jsonParam2.put("item_type", cur.getString(cur.getColumnIndex("item_type")));
                        jsonParam2.put("ref_item", cur.getInt(cur.getColumnIndex("ref_item")));
                        jsonParam2.put("price", cur.getDouble(cur.getColumnIndex("price")));
                        jsonParam2.put("reason_return_id", Helper.getNullString(cur.getString(cur.getColumnIndex("reason_return"))));
                        jsonParam2.put("total_gross", cur.getDouble(cur.getColumnIndex("total_gross")));
                        jsonParam2.put("regular_discount", cur.getDouble(cur.getColumnIndex("regular_discount")));
                        jsonParam2.put("extra_discount", cur.getDouble(cur.getColumnIndex("extra_discount")));
                        jsonParam2.put("special_discount", cur.getDouble(cur.getColumnIndex("special_discount")));
                        jsonParam2.put("total_net", cur.getDouble(cur.getColumnIndex("total_net")));


                        //Log.e("uom",cur.getString(cur.getColumnIndex("small_uom")));
                        jsonParam2.put("small_uom", cur.getString(cur.getColumnIndex("small_uom")));
                        jsonParam2.put("medium_uom", cur.getString(cur.getColumnIndex("medium_uom")));
                        jsonParam2.put("large_uom", cur.getString(cur.getColumnIndex("large_uom")));


                        jsonParam2.put("current_stock", cur.getDouble(cur.getColumnIndex("current_stock")));
                        jsonParam2.put("current_stock_uom", cur.getString(cur.getColumnIndex("current_stock_uom")));


                        jsonParam2.put("last_stock", cur.getDouble(cur.getColumnIndex("last_stock")));
                        jsonParam2.put("last_stock_uom", cur.getString(cur.getColumnIndex("last_stock_uom")));

                        jsonParam2.put("large_to_small", cur.getInt(cur.getColumnIndex("large_to_small")));
                        jsonParam2.put("medium_to_small", cur.getInt(cur.getColumnIndex("large_to_small")));

                        jsonArr.put(jsonParam2);
                    }

                    jsonReady.put("items", jsonArr);

                } while (cur.moveToNext());

            }

            if (order_type == Order.REGULAR_ORDER) {
                JSONArray compt = jCompetitor(cust);
                jsonReady.put("competitors", compt);
            }else{
                jsonReady.put("competitors", "");
            }


            if (Customer.isNewCustomer(db, cust)) {
                jsonReady.put("status", "3");
                Customer customerNew = Customer.getCustomer(context, cust);
                JSONObject param = new JSONObject();
                param.put("outlet_id", customerNew.getCustomerNumber());
                param.put("name", customerNew.getCustomerName());
                param.put("alias", customerNew.getAlias());
                param.put("address", customerNew.getAddress());
                param.put("city", customerNew.getCity());
                param.put("latitude", customerNew.getLatitude());
                param.put("longitude", customerNew.getLongitude());
                param.put("channel_id", customerNew.getChannel());
                //Log.e("getGroupChannel()", customerNew.getGroupChannel());
                param.put("channel_group_id", customerNew.getGroupChannel());
                param.put("notes", customerNew.getNotes());
                param.put("contact_person", customerNew.getContact());
                param.put("region_id", customerNew.getRegion());
                param.put("zone_id", customerNew.getZone());
                param.put("outlet_classification_id", customerNew.getClassification());
                param.put("district_id", customerNew.getDistrict());
                param.put("territory_id", customerNew.getTerritory());

                JSONArray jsCust = new JSONArray();
                jsCust.put(customerNew.getCustomerNumber());

                jsonReady.put("noo", param);
            } else {
                jsonReady.put("noo", "");
                jsonReady.put("status", "1"); // 1=order,2=retur; 3=noo
            }



            ok = jsonReady;


        } catch (JSONException e) {
            ok = null;
        } catch (Exception e) {
            ok = null;
        }
        return ok;
    }

    public String postDataCanvas(long id){
        boolean ada = false;
        String orderNumberServer = "";
        JSONObject jsonParam = null;
        JSONObject jsonParam2 = null;
        JSONArray jsonArr =  new JSONArray();
        JSONObject jsonReady = new JSONObject();
        String customerNumberName = "";

        SharedPreferences session =context.getSharedPreferences("ngsales", 0);
        String company = session.getString("CUR_COMPANY","");
        String cursls = session.getString("CUR_SLS", "");

        String sql =
                "SELECT " +
                        "sls_van.transaction_id, " +
                        "sls_van.transaction_date, " +
                        "sls_van.locked, " +
                        "sls_van.status, " +
                        "sls_van.type_transaction, " +
                        "sls_van.sls_id, " +
                        "sls_van.reference, " +
                        "sls_van_item.item, " +
                        "sls_van_item.product_id, " +
                        "sls_van_item.description, " +
                        "sls_van_item.qty, " +
                        "sls_van_item.qty_pcs, " +
                        "sls_van_item.uom, " +
                        "sls_van_item.brand, " +
                        "sls_van_item.small_uom, " +
                        "sls_van_item.medium_uom, " +
                        "sls_van_item.large_uom, " +
                        "sls_van_item.medium_to_small, " +
                        "sls_van_item.large_to_small " +
                        "FROM " +
                        "sls_van " +
                        "inner Join sls_van_item ON sls_van.transaction_id = sls_van_item.transaction_id "+
                        "WHERE sls_van.transaction_id=?  ORDER BY sls_van.transaction_id";
        //"WHERE sls_order.status =0 and sls_order.order_id=?  ORDER BY sls_order.order_id";
        try {

            Cursor cur = db.rawQuery(sql, new String[]{String.valueOf(id)});
            if (cur.moveToFirst()) {
                // header bray
                jsonParam = new JSONObject();
                jsonParam.put("company_id",             company);
                jsonParam.put("canvas_id",  cur.getInt(cur.getColumnIndex("transaction_id")));
                jsonParam.put("transaction_date",       cur.getString(cur.getColumnIndex("transaction_date")));
                //jsonParam.put("delivered",             cur.getInt(cur.getColumnIndex("delivered")));
                jsonParam.put("delivered","0");

                int tipe = cur.getInt(cur.getColumnIndex("type_transaction"));

                if (tipe==LoadingUnloading.TYPE_LOADING)
                    jsonParam.put("type",1);
                else
                    jsonParam.put("type", 2);

                jsonParam.put("salesman_id",             cur.getString(cur.getColumnIndex("sls_id")));
                jsonParam.put("ref_number",             Helper.getNullString(cur.getString(cur.getColumnIndex("reference"))));


                jsonReady.put("command","canvas");
                jsonReady.put("company_id",company);
                jsonReady.put("salesman_id",cursls);
                jsonReady.put("header",jsonParam);

                ada = false;
                do{
                    ada = true;
                    // detail bro
                    jsonParam2 = new JSONObject();
                    if (cur.getString(cur.getColumnIndex("product_id"))!=null) {
                        jsonParam2.put("canvas_item_id", cur.getString(cur.getColumnIndex("item")));
                        jsonParam2.put("canvas_id", cur.getString(cur.getColumnIndex("transaction_id")));
                        jsonParam2.put("product_id", cur.getString(cur.getColumnIndex("product_id")));
                        jsonParam2.put("qty", cur.getDouble(cur.getColumnIndex("qty_pcs")));
                        jsonParam2.put("uom", cur.getString(cur.getColumnIndex("uom")));
                        jsonArr.put(jsonParam2);
                    }

                    jsonReady.put("items", jsonArr);

                }while (cur.moveToNext());


                cur.close();




                //start request order to server
                NgantriInformation ngantri = new NgantriInformation(context);
                ngantri.key = NgantriInformation.KEY_LOADING;
                try{
                    //ngantri.data = jsonReady.toString();
                    ngantri.data = Helper.convertToUTF8(jsonReady.toString());
                    ngantri.value = String.valueOf(id);
                    if (tipe==LoadingUnloading.TYPE_LOADING)
                        ngantri.description = "sending loading " +company+"_"+cursls+"_"+customerNumberName;
                    else
                        ngantri.description = "sending unloading " +company+"_"+cursls+"_"+customerNumberName;

                    ngantri.addAntrian();
                } catch(Exception x){Log.e("ERROR CANVAS",x.toString());}
                //end request order to server



            }
            return orderNumberServer;

        }
        catch (JSONException e) {Log.e("canvas err",e.toString()); return "";}
        catch (Exception e) {Log.e("err","s",e);return "";}

    }

    public String postData(long id, String signature){
        boolean ok = false;
        String sign ="";
        String pic ="";
        int order_type = -1;
        String orderNumberServer = "";
        JSONObject jsonParam = null;
        JSONObject jsonParam2 = null;
        String cust = "";
        JSONArray jsonArr =  new JSONArray();
        JSONObject jsonReady = new JSONObject();
        String customerNumberName = "";
        boolean ada = false;

        SharedPreferences session = context.getSharedPreferences("ngsales", 0);
        String company = session.getString("CUR_COMPANY","");
        String cursls = session.getString("CUR_SLS", "");


        String sql =
                        "SELECT " +
                        "sls_order.delivered, " +
                        "sls_order.order_id, " +
                        "sls_order.order_type, " +
                        "sls_order.duration, " +
                        "sls_order.start_call, " +
                        "sls_order.end_call, " +
                        "sls_order.order_id, " +
                        "sls_order.salesman_type, " +
                        "sls_order.order_date, " +
                        "sls_order.delivery_date, " +
                        "sls_order.outlet_id, " +
                        "sls_order.sls_id, " +
                        "sls_order.notes, " +
                        "sls_order.docking, " +
                        "sls_order.ppn, " +
                        "sls_order.sub_total, " +
                        "sls_order.total_discount, " +
                        "sls_order.outlet_top_id, " +
                        "sls_order.grand_total, " +
                        "sls_order.reason, " +
                        "sls_order.reason_unroute," +
                        "sls_order.reason_nobarcode, " +
                        "sls_order.`week`, " +
                        "sls_order.date_create, " +
                        "sls_order.imei, " +
                        "sls_order.latitude, " +
                        "sls_order.longitude, " +
                        "sls_order_item.product_id, " +
                        "sls_order_item.item, " +
                        "sls_order_item.item_type, " +
                        "sls_order_item.ref_item, " +
                        "sls_order_item.description, " +
                        "sls_order_item.qty, " +
                        "sls_order.pic_outlet,sls_order.signature,sls_order_item.uom, " +
                        "sls_order_item.current_stock, " +
                        "sls_order_item.current_stock_uom, " +
                        "sls_order_item.small_uom, " +
                        "sls_order_item.reason_return, " +
                        "sls_order_item.qty_pcs, " +
                        "sls_order_item.medium_uom, " +
                        "sls_order_item.large_uom, " +
                        "sls_order_item.price, " +
                        "sls_order_item.total_gross, " +
                        "sls_order_item.regular_discount, " +
                        "sls_order_item.extra_discount, " +
                        "sls_order_item.special_discount, " +
                        "sls_order_item.total_net, " +
                        "sls_order_item.last_stock, " +
                        "sls_order_item.last_stock_uom, " +
                        "sls_order_item.medium_to_small, " +
                        "sls_order_item.large_to_small " +
                        "FROM " +
                        "sls_order " +
                        "left Join sls_order_item ON sls_order.order_id = sls_order_item.order_id "+
                        "WHERE sls_order.order_id=?  ORDER BY sls_order.order_id";
                        //"WHERE sls_order.status =0 and sls_order.order_id=?  ORDER BY sls_order.order_id";
        try {

            Cursor cur = db.rawQuery(sql, new String[]{String.valueOf(id)});
            if (cur.moveToFirst()) {
                // header bray
                cust = cur.getString(cur.getColumnIndex("outlet_id"));
                jsonParam = new JSONObject();
                jsonParam.put("company_id",         company);
                jsonParam.put("order_id",           cur.getInt(cur.getColumnIndex("order_id")));
                jsonParam.put("order_date",         cur.getString(cur.getColumnIndex("order_date")));
                jsonParam.put("delivered",          cur.getInt(cur.getColumnIndex("delivered")));
                jsonParam.put("docking",            cur.getInt(cur.getColumnIndex("docking")));
                jsonParam.put("delivery_date",         cur.getString(cur.getColumnIndex("delivery_date")));

                order_type= cur.getInt(cur.getColumnIndex("order_type"));
                int tipeSls = cur.getInt(cur.getColumnIndex("salesman_type"));

                if (tipeSls==Order.SALES_TAKING_ORDER)
                    jsonParam.put("salesman_team_id",1);
                else
                    jsonParam.put("salesman_team_id",2);


                if(order_type==Order.REGULAR_ORDER) {

                    JSONArray compt = jCompetitor(cust);
                    jsonReady.put("competitors", compt);

                    if(Customer.isNewCustomer(db,cust)){
                        Customer customerNew =  Customer.getCustomer(context,cust);

                        JSONObject param = new JSONObject();
                        param.put("outlet_id",customerNew.getCustomerNumber());
                        param.put("name",customerNew.getCustomerName());
                        param.put("alias",customerNew.getAlias());
                        param.put("address",customerNew.getAddress());
                        param.put("city",customerNew.getCity());
                        param.put("phone_number",customerNew.getPhone());
                        param.put("latitude",customerNew.getLatitude());
                        param.put("longitude",customerNew.getLongitude());
                        param.put("channel_id", Helper.getNullString(customerNew.getChannel()));
                        param.put("channel_group_id", Helper.getNullString(customerNew.getGroupChannel()));
                        param.put("region_id",Helper.getNullString(customerNew.getRegion()));
                        param.put("zone_id",Helper.getNullString(customerNew.getZone()));
                        param.put("outlet_classification_id",Helper.getNullString(customerNew.getClassification()));
                        param.put("district_id",Helper.getNullString(customerNew.getDistrict()));
                        param.put("territory_id",Helper.getNullString(customerNew.getTerritory()));
                        param.put("notes",customerNew.getNotes());
                        param.put("contact_person",customerNew.getContact());

                        JSONArray jsCust = new JSONArray();
                        jsCust.put(customerNew.getCustomerNumber());

                        jsonReady.put("noo", param );

                        if (signature == null ) {
                            jsonParam.put("status", "8");
                        } else {
                            jsonParam.put("status", "2"); // 1=order, 2=noo
                        }

                    }else{
                        jsonReady.put("noo", "");

                        if (signature == null ) {
                            jsonParam.put("status", "8");
                        } else {
                            jsonParam.put("status", "1"); // 1=order, 2=noo
                        }
                    }

                } else if (order_type==Order.RETURN_ORDER) {
                    jsonReady.put("noo", "");

                    if (signature == null ) {
                        jsonParam.put("status", "8");
                    } else {
                        jsonParam.put("status", "3"); // 1=order, 2=noo, 3=retur
                    }

                }

                customerNumberName = cur.getString(cur.getColumnIndex("outlet_id"));

                jsonParam.put("outlet_top_id", cur.getLong(cur.getColumnIndex("outlet_top_id")));
                jsonParam.put("pause_duration", cur.getLong(cur.getColumnIndex("duration")));
                jsonParam.put("outlet_id",          cur.getString(cur.getColumnIndex("outlet_id")));
                jsonParam.put("week",               cur.getString(cur.getColumnIndex("week")));
                jsonParam.put("sls_id",             cur.getString(cur.getColumnIndex("sls_id")));
                jsonParam.put("notes",              cur.getString(cur.getColumnIndex("notes")));
                jsonParam.put("ppn",                cur.getDouble(cur.getColumnIndex("ppn")));
                jsonParam.put("sub_total",          cur.getDouble(cur.getColumnIndex("sub_total")));
                jsonParam.put("total_discount",     cur.getDouble(cur.getColumnIndex("total_discount")));
                jsonParam.put("grand_total",        cur.getDouble(cur.getColumnIndex("grand_total")));
                jsonParam.put("date_create",        cur.getString(cur.getColumnIndex("date_create")));
                jsonParam.put("reason",             cur.getString(cur.getColumnIndex("reason")));
                jsonParam.put("reason_no_barcode_id",             cur.getString(cur.getColumnIndex("reason_nobarcode")));
                jsonParam.put("reason_no_route_id",             cur.getString(cur.getColumnIndex("reason_unroute")));
                jsonParam.put("imei",             cur.getString(cur.getColumnIndex("imei")));
                jsonParam.put("latitude", cur.getString(cur.getColumnIndex("latitude")));
                jsonParam.put("longitude", cur.getString(cur.getColumnIndex("longitude")));

                double lt = Double.parseDouble(cur.getString(cur.getColumnIndex("latitude")));
                double lg = Double.parseDouble(cur.getString(cur.getColumnIndex("longitude")));
                String gp = Customer.updateGPS(db, cur.getString(cur.getColumnIndex("outlet_id")),lt,lg);

                jsonParam.put("gps", gp);
                jsonParam.put("start_call", cur.getString(cur.getColumnIndex("start_call")));
                jsonParam.put("end_call",           cur.getString(cur.getColumnIndex("end_call")));

                jsonReady.put("command","order");
                jsonReady.put("company_id",company);
                jsonReady.put("salesman_id",cursls);
                jsonReady.put("version", Integer.toString(MainActivity.versionCode));
                jsonReady.put("header",jsonParam);

                //if (gambar!=null) {
                  //  jsonReady.put("img", gambar);
                //}

                //jsonReady.put("picture_outlet", "");

                if (!cur.isNull(cur.getColumnIndex("signature"))) {
                    sign = cur.getString(cur.getColumnIndex("signature"));
                    jsonReady.put("picture_signature", cur.getString(cur.getColumnIndex("signature")));
                }else{
                    jsonReady.put("picture_signature", "");
                }

                if (!cur.isNull(cur.getColumnIndex("pic_outlet"))) {
                    pic = cur.getString(cur.getColumnIndex("pic_outlet"));
                    jsonReady.put("picture_outlet", cur.getString(cur.getColumnIndex("pic_outlet")));
                    //Log.e("INFO PIC",cur.getString(cur.getColumnIndex("pic_outlet")));
                }else{
                    jsonReady.put("picture_outlet", "");
                }

                //Log.e("data", String.valueOf(id) + " " + jsonReady.toString());
                ada = false;
                do{
                    ada = true;
                    // detail bro
                    jsonParam2 = new JSONObject();
                    if (cur.getString(cur.getColumnIndex("product_id"))!=null) {
                        jsonParam2.put("product_id", cur.getString(cur.getColumnIndex("product_id")));
                        jsonParam2.put("item", cur.getString(cur.getColumnIndex("item")));

                        Log.e("test data",cur.getString(cur.getColumnIndex("description")));

                        jsonParam2.put("description", cur.getString(cur.getColumnIndex("description")));
                        jsonParam2.put("qty", cur.getDouble(cur.getColumnIndex("qty")));
                        jsonParam2.put("uom", cur.getString(cur.getColumnIndex("uom")));
                        jsonParam2.put("item_type", cur.getString(cur.getColumnIndex("item_type")));
                        jsonParam2.put("ref_item", cur.getInt(cur.getColumnIndex("ref_item")));
                        jsonParam2.put("price", cur.getDouble(cur.getColumnIndex("price")));
                        jsonParam2.put("total_gross", cur.getDouble(cur.getColumnIndex("total_gross")));
                        jsonParam2.put("regular_discount", cur.getDouble(cur.getColumnIndex("regular_discount")));
                        jsonParam2.put("extra_discount", cur.getDouble(cur.getColumnIndex("extra_discount")));
                        jsonParam2.put("special_discount", cur.getDouble(cur.getColumnIndex("special_discount")));
                        jsonParam2.put("total_net", cur.getDouble(cur.getColumnIndex("total_net")));
                        //Log.e("uom",cur.getString(cur.getColumnIndex("small_uom")));
                        jsonParam2.put("small_qty", cur.getString(cur.getColumnIndex("qty_pcs")));
                        jsonParam2.put("reason_return_id", Helper.getNullString(cur.getString(cur.getColumnIndex("reason_return"))));                        jsonParam2.put("small_uom", cur.getString(cur.getColumnIndex("small_uom")));
                        jsonParam2.put("medium_uom", cur.getString(cur.getColumnIndex("medium_uom")));
                        jsonParam2.put("large_uom", cur.getString(cur.getColumnIndex("large_uom")));
                        jsonParam2.put("current_stock", cur.getDouble(cur.getColumnIndex("current_stock")));
                        jsonParam2.put("current_stock_uom", cur.getString(cur.getColumnIndex("current_stock_uom")));
                        jsonParam2.put("last_stock", cur.getDouble(cur.getColumnIndex("last_stock")));
                        jsonParam2.put("last_stock_uom", cur.getString(cur.getColumnIndex("last_stock_uom")));
                        jsonParam2.put("large_to_small", cur.getInt(cur.getColumnIndex("large_to_small")));
                        jsonParam2.put("medium_to_small", cur.getInt(cur.getColumnIndex("large_to_small")));

                        jsonArr.put(jsonParam2);
                    }

                    jsonReady.put("items", jsonArr);


                }while (cur.moveToNext());


            /*if (compt.length()>0) {
                ContentValues cv = new ContentValues();
                cv.put("sent", "1");
                db.update("sls_competitor_entry", cv, "outlet_id=?", new String[]{cust});
            }*/

            cur.close();
            //Log.e("dd", Helper.convertToUTF8(jsonReady.toString()));

            //Log.e("JSON",jsonReady.toString());
            //start request order to serverd
            NgantriInformation ngantri = new NgantriInformation(context);
            ngantri.key = NgantriInformation.KEY_ORDER;
            try{
                //ngantri.data = jsonReady.toString();

                ngantri.data = Helper.convertToUTF8(jsonReady.toString());

                ngantri.value = String.valueOf(id);
                if(order_type==Order.REGULAR_ORDER)
                    ngantri.description = "sending order " +company+"_"+cursls+"_"+customerNumberName;
                else
                    ngantri.description = "sending return " +company+"_"+cursls+"_"+customerNumberName;

                ngantri.addAntrian();
            } catch(Exception x){Log.e("ERROR ORD",x.toString());}
            //end request order to server


            // hold by antrian
            /*java.net.URL url = new URL(MainActivity.BASE_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());

            wr.writeBytes("data=" + jsonReady.toString());

            wr.flush();
            wr.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    response.append(line);
                }


                try {
                    JSONObject res = new JSONObject(response.toString());

                    DBManager dm = DBManager.getInstance(context.getApplicationContext());
                    if (res.optString("success").equals("true")) {
                        orderNumberServer = res.optString("data");
                        Order.setSuccess(dm.database(), String.valueOf(id));
                    }

                }
                catch (JSONException e) {
                    Log.e("XXX",e.toString());
                }

            } end hold*/

            }
            return orderNumberServer;

        }
        //catch (MalformedURLException e) {return "";}
        //catch (SocketException ex) {return "";}
        //catch (SocketTimeoutException ex) {return "";}
        //catch (ProtocolException e) {return "";}
        //catch (IOException e) {return "";}
        catch (JSONException e) { Log.e("err order",e.toString()); return "";}
        catch (Exception e) {Log.e("err order",e.toString(),e); return "";}


    }

    public String postGPS(String latitude,String longitude){
        boolean ok = false;
        String orderNumberServer = "";
        JSONObject jsonParam = null;
        JSONObject jsonParam2 = null;
        JSONArray jsonArr =  new JSONArray();
        JSONObject jsonReady = new JSONObject();
        String cursls ="";

        SharedPreferences session =context.getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();
        cursls = session.getString("CUR_SLS", "");

        try {

            jsonParam = new JSONObject();
            jsonParam.put("sls_id",cursls);
            jsonParam.put("longitude",longitude);
            jsonParam.put("latitude",latitude);



            //jsonReady.put("slsid",jsonParam);

            java.net.URL url = new URL(MainActivity.BASE_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(MainActivity.HTTP_CONECTION_TIMEOUT * 1000);
            urlConnection.setReadTimeout(MainActivity.HTTP_READ_TIMEOUT * 1000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());

            wr.writeBytes("data=" + jsonParam.toString());
            //Log.e("JSON", "data=" + jsonParam.toString());


            wr.flush();
            wr.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    response.append(line);
                }

                //try {
                    JSONObject res = new JSONObject(response.toString());

                    DBManager dm = DBManager.getInstance(context.getApplicationContext());
                    if (res.optString("success").equals("true")) {
                        //orderNumberServer = res.optString("data");
                    }

                //}
                // catch (JSONException ec) {
                //    Log.e("XXX",ec.toString());
                //}

            }


        }
        catch (MalformedURLException e1) {
        } catch (ProtocolException e2) {
        } catch (IOException e3) {
        }
        catch (JSONException e4) {
        }
        catch (Exception e5) {
        }


        return "";
    }

    public class AsyncPost extends AsyncTask<String, Void, Integer> {

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
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }



            if (result == 1) {
            } else {
               // Toast.makeText(MainActivity.this, "Network error.", Toast.LENGTH_LONG).show();
            }


        }
    }

}
