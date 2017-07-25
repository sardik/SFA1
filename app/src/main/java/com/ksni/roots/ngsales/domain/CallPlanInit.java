package com.ksni.roots.ngsales.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.CustomerSKU;
import com.ksni.roots.ngsales.model.InfoPromo;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by #roots on 03/12/2015.
 */
public class CallPlanInit {

    public static void getCallPlanFromServer(Context ctx, SQLiteDatabase db, JSONArray arr){
        try{
            CustomerCall.deleteAll(ctx);

            CustomerSKU.deleteAll(ctx);
            com.ksni.roots.ngsales.model.Target.deleteAll(ctx);
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.CustomerCall prd = new com.ksni.roots.ngsales.model.CustomerCall(db);
                    prd.setId(jsonSku.optString("call_plan_id"));
                    prd.setSlsId(jsonSku.optString("salesman_id"));
                    prd.setCustomerNumber(jsonSku.optString("outlet_id"));
                    prd.setServerDate(jsonSku.optString("date"));
                    prd.setWeek(jsonSku.optString("week"));
                    prd.setRoute(jsonSku.optString("route_id"));
                    prd.setSquence(jsonSku.optInt("sequence"));
                    prd.setNotes(jsonSku.optString("notes"));
                    prd.setCreditLimit(jsonSku.optDouble("credit_limit"));
                    prd.setStatus(CustomerCall.NO_VISIT);
                    prd.setCallStatus("1");
                    prd.save();
                }
            }
            Settings.restart(ctx);
        }
        catch (Exception e){
            Log.e("ngsales", "getCallPlanFromServer::GLOBAL ERROR " + e.getMessage());
        }
    }

    public static void getInformationFromServer(SQLiteDatabase db, JSONArray arr){
        if(arr!=null) {
            // product
            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonSku = arr.optJSONObject(i);
                InfoPromo prd = new InfoPromo(db);

                prd.id = jsonSku.optInt("id");
                prd.valid_from = jsonSku.optString("valid_from");
                prd.valid_to = jsonSku.optString("valid_to");
                prd.content = jsonSku.optString("content");
                prd.save();
            }
        }

    }

    public static void getTargetFromServer(SQLiteDatabase db, JSONArray arr){
        if(arr!=null) {
            // product
            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonSku = arr.optJSONObject(i);
                com.ksni.roots.ngsales.model.Target prd = new com.ksni.roots.ngsales.model.Target(db);

                prd.id = jsonSku.optString("id");
                prd.year = jsonSku.optInt("year");
                prd.period = jsonSku.optInt("period");
                prd.sls = jsonSku.optString("sls_id");
                prd.sku = jsonSku.optString("product_id");

                prd.targetQty = jsonSku.optDouble("target_qty");
                prd.actualQty = jsonSku.optDouble("actual_qty");
                prd.achieveQty = jsonSku.optDouble("achiev_qty");

                prd.targetValue = jsonSku.optDouble("target_value");
                prd.actualValue = jsonSku.optDouble("actual_value");
                prd.achieveValue = jsonSku.optDouble("achiev_value");

                prd.targetIPT = jsonSku.optInt("target_ipt");
                prd.targetCall = jsonSku.optInt("target_call");
                prd.targetEc = jsonSku.optInt("target_ec");


                prd.save();
            }
        }

    }

    public static void getSkuTemplateFromServer(SQLiteDatabase db, JSONArray arr){
        try {
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.CustomerSKU prd = new com.ksni.roots.ngsales.model.CustomerSKU(db);
                    prd.product_id = jsonSku.optString("product_id");
                    prd.id = jsonSku.optString("call_plant_last_call_id");
                    prd.outlet_id = jsonSku.optString("outlet_id");
                    prd.qty_last = jsonSku.optInt("qty");
                    prd.uom = jsonSku.optString("uom_id");
                    prd.save();
                }
            }
        }
        catch (Exception e){
            Log.e("ngsales","getSkuTemplateFromServer::GLOBAL ERROR "+e.getMessage());
        }

    }

    public static void loadPlan(Context ctx){

        SharedPreferences xsession = ctx.getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor ef = xsession.edit();

        double lat =  Double.parseDouble(xsession.getString("LATITUDE", "0"));
        double lon = Double.parseDouble(xsession.getString("LONGITUDE", "0"));


        ef.putString("CUR_VISIT", "");
        ef.putString("CUR_PAUSE", "");
        ef.putString("CUR_VISIT_NAME", "");
        ef.commit();

        MainActivity.dataCustomerCall.clear();
        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery(" SELECT a.reason_unroute,b.latitude,b.longitude, b.picture,a.order_id,a.return_id,a.start_time,a.end_time, a.duration,a.last_resume,a.last_pause, a.call_status,a.status,b.zone,b.channel, b.outlet_name,b.address,a.id,a.date,a.week,a.sls_id,a.outlet_id,a.route,a.squence,a.notes,a.credit_limit FROM sls_plan_status a " +
                " INNER JOIN sls_customer b ON a.outlet_id = b.outlet_id"+
                " WHERE a.date=? ORDER BY a.call_status DESC,a.squence",new String[]{ Helper.getCurrentDate()});

        if (cur.moveToFirst()) {
            do {
                //Log.e("outlet_id",cur.getString(cur.getColumnIndex("outlet_id")));
                CustomerCall cc  = new CustomerCall();
                cc.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
                cc.setId(cur.getString(cur.getColumnIndex("id")));
                cc.setServerDate(cur.getString(cur.getColumnIndex("date")));
                cc.setWeek(cur.getString(cur.getColumnIndex("week")));
                cc.setReasonUnroute(cur.getString(cur.getColumnIndex("reason_unroute")));
                cc.setPicture(cur.getString(cur.getColumnIndex("picture")));
                cc.setDuration(cur.getLong(cur.getColumnIndex("duration")));
                cc.setOrderId(cur.getLong(cur.getColumnIndex("order_id")));
                cc.setReturnId(cur.getLong(cur.getColumnIndex("return_id")));
                //Log.e(cur.getString(cur.getColumnIndex("outlet_id")),cur.getString(cur.getColumnIndex("start_time")));
                cc.setStartTime(cur.getString(cur.getColumnIndex("start_time")));
                cc.setEndTime(cur.getString(cur.getColumnIndex("end_time")));
                cc.setLatitude(cur.getDouble(cur.getColumnIndex("latitude")));
                cc.setLongitude(cur.getDouble(cur.getColumnIndex("longitude")));

                if (lat==0 && lon==0){
                    cc.setJarak(0);
                }else{
                        Location lokasiA = new Location("salesman");
                        lokasiA.setLatitude(lat);
                        lokasiA.setLongitude(lon);

                        Location lokasiB = new Location("customer");
                        lokasiB.setLatitude(cc.getLatitude());
                        lokasiB.setLongitude(cc.getLongitude());

                        cc.setJarak(lokasiA.distanceTo(lokasiB));
                }

                cc.setLastResume(cur.getString(cur.getColumnIndex("last_resume")));
                cc.setLastPause(cur.getString(cur.getColumnIndex("last_pause")));
                cc.setZone(cur.getString(cur.getColumnIndex("zone")));
                cc.setChannel(cur.getString(cur.getColumnIndex("channel")));
                cc.setSlsId(cur.getString(cur.getColumnIndex("sls_id")));
                cc.setRoute(cur.getString(cur.getColumnIndex("route")));
                cc.setSquence(cur.getInt(cur.getColumnIndex("squence")));
                cc.setNotes(cur.getString(cur.getColumnIndex("notes")));
                cc.setCreditLimit(cur.getDouble(cur.getColumnIndex("credit_limit")));
                cc.setStatus(cur.getString(cur.getColumnIndex("status")));

                if(cur.getString(cur.getColumnIndex("call_status")).equals("1"))
                    cc.setCustomerName(cur.getString(cur.getColumnIndex("outlet_name")));
                else{
                    String hari = CustomerCall.getDayByCustomer(dm.database(),cc.getCustomerNumber());
                    cc.setCustomerName(cur.getString(cur.getColumnIndex("outlet_name"))+ " "+hari);
                }

                cc.setAddress(cur.getString(cur.getColumnIndex("address")));
                cc.setCallStatus(cur.getString(cur.getColumnIndex("call_status")));

                if (cc.getStatus().equals(CustomerCall.VISIT)) {
                    //cc.setCallStatus("1");
                    SharedPreferences session = ctx.getApplicationContext().getSharedPreferences("ngsales", 0);
                    SharedPreferences.Editor e = session.edit();

                    e.putString("CUR_VISIT", cc.getCustomerNumber());
                    e.putString("CUR_VISIT_NAME", cc.getCustomerName());
                    e.commit();
                }
                else if (cc.getStatus().equals(CustomerCall.PAUSED)) {
                    //cc.setCallStatus("1");
                    SharedPreferences session = ctx.getApplicationContext().getSharedPreferences("ngsales", 0);
                    SharedPreferences.Editor e = session.edit();

                    e.putString("CUR_PAUSE", "X");
                    e.commit();
                }

                MainActivity.dataCustomerCall.add(cc);
            } while (cur.moveToNext());
        }


        cur.close();


    }

    public static void sortJarak(){
        Collections.sort(MainActivity.dataCustomerCall, new Comparator<CustomerCall>() {
            @Override
            public int compare(CustomerCall c1, CustomerCall c2) {
                return Double.compare(c1.getJarak(), c2.getJarak());
            }
        });
    }

    public static void sortSquence(){
        Collections.sort(MainActivity.dataCustomerCall, new Comparator<CustomerCall>() {
            @Override
            public int compare(CustomerCall c1, CustomerCall c2) {
                return Double.compare(c1.getSquence(), c2.getSquence());
            }
        });
    }



}
