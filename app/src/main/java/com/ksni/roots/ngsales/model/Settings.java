package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.Set;

/**
 * Created by #roots on 28/09/2015.
 */
public class Settings {
    public String server;
    public String salesman;
    public String salesman_name;
    public int salesman_type;
    public String workStart;
    public String start_date;
    public String end_date;
    public String workEnd;
    public double startLatitude;
    public double startLongitude;
    public String week;
    public String barcode_number;
    public String workDate;
    public String last_login;
    public int is_login;
    public String last_logout;
    public String company;
    public int branch;
    public String zone;
    public double endLatitude;
    public double endLongitude;
    public String last_sync_call_plan;
    public String last_sync;
    public String info;
    public String multi_dist;

    public int year;
    public int period;
    private Context ctx;

    public Settings(Context context){
        ctx = context;
    }

    public Settings(){    }

    public static void restart(Context ct){
        DBManager dm = DBManager.getInstance(ct.getApplicationContext());
        Cursor cur =dm.database().rawQuery("SELECT work_date FROM settings",null);
        if (cur.moveToFirst()) {
            String tgl = cur.getString(0);
            if(!Helper.getCurrentDate().equals(tgl)){
                ContentValues cv = new ContentValues();
                cv.put("work_start","");
                cv.put("work_end", "");
                cv.put("work_date", Helper.getCurrentDate());
                SharedPreferences session = ct.getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor e = session.edit();
                e.putString("CUR_WORK_START", "");
                e.putString("CUR_WORK_END", "");
                e.commit();

                dm.database().update("settings",cv,null,null);
            }
        }
        cur.close();
    }

    public static boolean requiredStart(Context ct){
        boolean op = false;
        DBManager dm = DBManager.getInstance(ct.getApplicationContext());

        Cursor cur =dm.database().rawQuery("SELECT work_date,work_start,work_end FROM settings",null);
        if (cur.moveToFirst()) {
            String workDate =  cur.getString(0);
            if (workDate==null){
                op = true;
            }
            else if (!workDate.equals(Helper.getCurrentDate())){
                op = true;
            }
            else{
                String start =  cur.isNull(1)?"": cur.getString(1);
                String end = cur.isNull(2)?"": cur.getString(2);
                if (start.toString().length()==0 && end.toString().length()==0 ){
                    op=true;
                }else if (start.toString().length()>0 && end.toString().length()>0 ){
                    op=true;
                }

            }
        }
        cur.close();
        /*Cursor cur =dm.database().rawQuery("SELECT work_start,work_end FROM settings",null);
        if (cur.moveToFirst()) {
            String start =  cur.isNull(0)?"": cur.getString(0);
            String end = cur.isNull(1)?"": cur.getString(1);
            if (start.toString().length()==0 && end.toString().length()==0 ){
                op=true;
            }else if (start.toString().length()>0 && end.toString().length()>0 ){
                op=true;
            }

        }
        cur.close();
        */
        return op;
    }

    public static boolean hasSyncMaster(Context ctx){
        boolean ada = false;
        try {
            DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
            Cursor cur =dm.database().rawQuery("SELECT * FROM settings",null);
            if (cur.moveToFirst()) {
                 if (!cur.isNull(cur.getColumnIndex("last_sync")))
                     ada = true;
                else
                    ada = false;
                }
            else
                ada = false;

            cur.close();
            return ada;

        }catch(Exception x){
            return false;
        }

    }

    public static String getLastModified(Context ctx){
        String last = "";
        try {
            DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
            Cursor cur =dm.database().rawQuery("SELECT last_sync FROM settings",null);
            if (cur.moveToFirst())
                last = cur.getString(0);
            cur.close();
        return last;
        }catch(Exception x){
            return "";
        }
    }

    public static void initSyncMaster(Context ctx,
                                      String sls,
                                      String last) {
        boolean ada = false;
        try {
            //Log.e("SALES",sls);
            DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
            Cursor cur =dm.database().rawQuery("SELECT * FROM settings",null);
            if (cur.moveToFirst())
                ada = true;
            else
                ada = false;

            cur.close();



            ContentValues cv = new ContentValues();
            if (!ada){
                cv.put("salesman", sls);
                cv.put("last_sync", last);

                dm.database().insert("settings", null, cv);
            }
            else{
                cv.put("last_sync", last);
                dm.database().update("settings", cv,null,null);
            }

        }catch(Exception x){

        }
    }

    public static void initCallPlan(Context ctx,
                            String sls,
                            String week,
                            String info,
                            String last,
                            String startPeriod,
                            String endPeriod,
                            int year,
                            int period
                                        ){
        boolean ada = false;
        try {
            DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
            Cursor cur =dm.database().rawQuery("SELECT * FROM settings",null);
            if (cur.moveToFirst())
                ada = true;
            else
                ada = false;

            cur.close();



            ContentValues cv = new ContentValues();
            if (!ada){
                cv.put("start_date", startPeriod);
                cv.put("end_date", endPeriod);

                cv.put("year", year);
                cv.put("period", period);

                cv.put("week", week);
                cv.put("info", info);
                cv.put("salesman", sls);
                cv.put("last_sync_call_plan", last);
                dm.database().insert("settings", null, cv);
            }
            else{
                cv.put("week", week);
                cv.put("info", info);
                cv.put("last_sync_call_plan", last);
                cv.put("start_date", startPeriod);
                cv.put("end_date", endPeriod);

                cv.put("year", year);
                cv.put("period", period);

                dm.database().update("settings", cv,null,null);
            }


        }catch(Exception x){

        }
    }

    public static void init(Context ctx,
                            String sls,
                            String dt,
                            String week){
        boolean ada = false;
        try {
            DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
            Cursor cur =dm.database().rawQuery("SELECT * FROM settings",null);
            if (cur.moveToFirst())
                ada = true;
            else
                ada = false;

            cur.close();



            ContentValues cv = new ContentValues();
            if (!ada){
                cv.put("work_date", dt);
                //cv.put("week", week);
                //cv.put("salesman", sls);
                dm.database().insert("settings", null, cv);
            }
            else{
                cv.put("work_date", dt);
                //cv.put("week", week);
                dm.database().update("settings",cv, null,null);
            }

        }catch(Exception x){

        }
    }



    public static void doWorkStart(Context ctx,
                                   String sls,
                                   String time,
                                   double latitude,
                                   double longitude,
                                   String tanggal,
                                   double odometer){
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        ContentValues cv = new ContentValues();
        cv.put("work_date", tanggal);
        cv.put("end_odometer", 0);
        cv.put("work_start", time);
        cv.put("start_date", tanggal);
        cv.put("end_date", "");
        cv.put("work_end", "");
        cv.put("start_odometer", odometer);
        cv.put("start_latitude", latitude);
        cv.put("start_longitude", longitude);
        dm.database().update("settings", cv, null, null); // satu salesman ajah
    }

    public static void doWorkEnd(Context ctx,
                                 String sls,
                                 String time,
                                 double latitude,
                                 double longitude,
                                 String tanggal,
                                 double odometer){
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        ContentValues cv = new ContentValues();
        cv.put("end_date", tanggal);
        cv.put("work_end", time);
        cv.put("end_odometer", odometer);
        cv.put("end_latitude", latitude);
        cv.put("end_longitude", longitude);
        dm.database().update("settings", cv, null, null); // satu salesman ajah
    }



    public static long getTransactionLastId(Context ctx,String sls,String cust){
        long a = -1;
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());

        Cursor cur =  dm.database().rawQuery("SELECT order_id FROM sls_order WHERE salesman=? ORDER BY order_id DESC", new String[]{sls});
        if (cur.moveToFirst()){
            a = cur.getLong(0);
        }
        cur.close();
        return a;
    }

    public static void setTransactionLastId(Context ctx, long id){
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        dm.database().execSQL("UPDATE settings SET last_id=" + String.valueOf(id));
    }



    public void loadInfo(){
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        Cursor cur = dm.database().rawQuery("SELECT * FROM settings", null);
        if (cur.moveToFirst()) {
            server = cur.getString(cur.getColumnIndex("server"));
            salesman_type = cur.getInt(cur.getColumnIndex("salesman_type"));
            salesman = cur.getString(cur.getColumnIndex("salesman"));
            week = cur.getString(cur.getColumnIndex("week"));
            workStart = cur.getString(cur.getColumnIndex("work_start"));
            barcode_number = cur.getString(cur.getColumnIndex("barcode_number"));
            workDate = cur.getString(cur.getColumnIndex("work_date"));
            startLatitude = cur.getDouble(cur.getColumnIndex("start_latitude"));
            startLongitude = cur.getDouble(cur.getColumnIndex("start_longitude"));
            workEnd = cur.getString(cur.getColumnIndex("work_end"));
            endLatitude = cur.getDouble(cur.getColumnIndex("end_latitude"));
            endLongitude = cur.getDouble(cur.getColumnIndex("end_longitude"));
            last_sync = cur.getString(cur.getColumnIndex("last_sync"));
            company = cur.getString(cur.getColumnIndex("company"));
            branch = cur.getInt(cur.getColumnIndex("branch"));
            zone = cur.getString(cur.getColumnIndex("zone"));
            salesman_name = cur.getString(cur.getColumnIndex("salesman_name"));
            last_sync_call_plan = cur.getString(cur.getColumnIndex("last_sync_call_plan"));
            last_login = cur.getString(cur.getColumnIndex("last_login"));
            last_logout = cur.getString(cur.getColumnIndex("last_logout"));
            is_login = cur.getInt(cur.getColumnIndex("is_login"));
            info = cur.getString(cur.getColumnIndex("info"));
            start_date = cur.getString(cur.getColumnIndex("start_date"));
            end_date = cur.getString(cur.getColumnIndex("end_date"));
            multi_dist = cur.getString(cur.getColumnIndex("multi_dist"));
            year = cur.getInt(cur.getColumnIndex("year"));
            period  = cur.getInt(cur.getColumnIndex("period"));
        }
        cur.close();
    }



    public static void login(Context ctx, String user, String server, String sls,
                             String slsname, String comp, int branch, String zone,
                             int slstp, String multidist, String barcode_number){
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());

        Cursor cur = dm.database().rawQuery("SELECT * FROM settings", null);
        boolean ada = cur.moveToFirst();

        if (!ada){
            ContentValues cv = new ContentValues();
            cv.put("salesman",sls);
            cv.put("salesman_name",slsname);
            cv.put("salesman_type",slstp);
            cv.put("server", server);
            cv.put("company",comp);
            cv.put("branch", branch);
            cv.put("zone", zone);
            cv.put("multi_dist",multidist);
            cv.put("barcode_number",barcode_number);
            cv.put("is_login",1);
            cv.put("last_login",user.toLowerCase());

            dm.database().insert("settings", null, cv);
        }else{
            ContentValues cv = new ContentValues();
            cv.put("server", server);
            cv.put("salesman",sls);
            cv.put("salesman_name",slsname);
            cv.put("salesman_type",slstp);
            cv.put("company",comp);
            cv.put("branch", branch);
            cv.put("zone", zone);
            cv.put("multi_dist",multidist);
            cv.put("barcode_number",barcode_number);
            cv.put("is_login",1);
            cv.put("last_login",user.toLowerCase());
            dm.database().update("settings", cv, null, null);
        }
    }

    public static void logout(Context ctx){
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        ContentValues cv = new ContentValues();
        cv.put("is_login",0);
        dm.database().update("settings", cv, null, null);
    }


    public static boolean isLogin(Context ctx){
        boolean islog = false;
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        Cursor cur = dm.database().rawQuery("SELECT * FROM settings", null);
        if (cur.moveToFirst()){
            int isl = cur.getInt(cur.getColumnIndex("is_login"));

            if(isl==1)
                islog=true;
            else
                islog=false;
        }
        cur.close();
        return islog;
    }

    public static boolean isLastLogin(Context ctx,String user){
        boolean islast = false;
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        Cursor cur = dm.database().rawQuery("SELECT * FROM settings", null);
        if (cur.moveToFirst()){
            if (!cur.isNull(cur.getColumnIndex("last_login"))){
                String last_login = cur.getString(cur.getColumnIndex("last_login"));
                if (last_login.toLowerCase().equals(user.toLowerCase())){
                    islast = true;
                }
            }
        }else{
            islast = true;
        }
        cur.close();
    return islast;
    }




    public static Settings getSettings(Context ctx){

        Settings sett = null;
        DBManager dm = DBManager.getInstance(ctx.getApplicationContext());
        Cursor cur = dm.database().rawQuery("SELECT * FROM settings", null);

        if (cur.moveToFirst()) {
            sett = new Settings();
            sett.salesman = cur.getString(cur.getColumnIndex("salesman"));
            sett.branch = cur.getInt(cur.getColumnIndex("branch"));
            sett.workDate = cur.getString(cur.getColumnIndex("work_date"));
            sett.workStart = cur.getString(cur.getColumnIndex("work_start"));
            sett.start_date = cur.getString(cur.getColumnIndex("start_date"));
            sett.startLatitude = cur.getDouble(cur.getColumnIndex("start_latitude"));
            sett.startLongitude = cur.getDouble(cur.getColumnIndex("start_longitude"));
            sett.workEnd = cur.getString(cur.getColumnIndex("work_end"));
            sett.end_date = cur.getString(cur.getColumnIndex("end_date"));
            sett.endLatitude = cur.getDouble(cur.getColumnIndex("end_latitude"));
            sett.endLongitude = cur.getDouble(cur.getColumnIndex("end_longitude"));
            sett.last_sync = cur.getString(cur.getColumnIndex("last_sync"));
            sett.last_sync_call_plan = cur.getString(cur.getColumnIndex("last_sync_call_plan"));
            sett.last_login = cur.getString(cur.getColumnIndex("last_login"));
            sett.last_logout = cur.getString(cur.getColumnIndex("last_logout"));
            sett.week = cur.getString(cur.getColumnIndex("week"));
            sett.info = cur.getString(cur.getColumnIndex("info"));
        }
        cur.close();
        return sett;
    }



}
