package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class CompetitorEntry {
    private SQLiteDatabase db;
    public int id;
    public String sls_id;
    public String competitor;
    public String outlet_id;
    public String date_visit;
    public String activity;
    public String product;
    public String cost;
    public String times;
    public String notes;

    public CompetitorEntry(){
        this.db = null;
    }
    public CompetitorEntry(SQLiteDatabase db){
        this.db = db;
    }


    public static CompetitorEntry getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            CompetitorEntry r = new CompetitorEntry();
            Cursor cur = db.rawQuery("SELECT * FROM sls_competitor WHERE competitor=?", new String[]{String.valueOf(no)});

            if (cur.moveToFirst()){
                do{
                    r.competitor = cur.getString(cur.getColumnIndex("competitor")) ;
                    r.id = cur.getInt(cur.getColumnIndex("id")) ;
                    r.sls_id = cur.getString(cur.getColumnIndex("sls_id")) ;
                    r.outlet_id = cur.getString(cur.getColumnIndex("outlet_id")) ;
                    r.date_visit = cur.getString(cur.getColumnIndex("date_visit")) ;
                    r.cost = cur.getString(cur.getColumnIndex("cost")) ;
                    r.activity = cur.getString(cur.getColumnIndex("activity")) ;
                    r.product = cur.getString(cur.getColumnIndex("product")) ;
                    r.times = cur.getString(cur.getColumnIndex("times")) ;
                    r.notes = cur.getString(cur.getColumnIndex("notes")) ;

                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<CompetitorEntry> getData(SQLiteDatabase db){
        CompetitorEntry r = null;
        List<CompetitorEntry> buff = new ArrayList<CompetitorEntry>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_competitor",null);
            if (cur.moveToFirst()){
                do{
                    r = new CompetitorEntry(null);
                    r.competitor = cur.getString(cur.getColumnIndex("competitor")) ;
                    r.id = cur.getInt(cur.getColumnIndex("id")) ;
                    r.sls_id = cur.getString(cur.getColumnIndex("sls_id")) ;
                    r.outlet_id = cur.getString(cur.getColumnIndex("outlet_id")) ;
                    r.date_visit = cur.getString(cur.getColumnIndex("date_visit")) ;
                    r.cost = cur.getString(cur.getColumnIndex("cost")) ;
                    r.activity = cur.getString(cur.getColumnIndex("activity")) ;
                    r.product = cur.getString(cur.getColumnIndex("product")) ;
                    r.times = cur.getString(cur.getColumnIndex("times")) ;
                    r.notes = cur.getString(cur.getColumnIndex("notes")) ;
                    buff.add(r);
                }while (cur.moveToNext());
            }
            cur.close();
            return buff;

        }catch (Exception ex) {
            return null;
        }
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT * FROM sls_competitor_entry WHERE id=?", new String[]{String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }
    public boolean save(){
        try {

            if (!isExist()) {
                ContentValues cv = new ContentValues();
                cv.put("competitor", competitor);
                cv.put("sls_id", sls_id);
                cv.put("outlet_id", outlet_id);
                cv.put("date_visit", date_visit);
                cv.put("activity", activity);
                cv.put("product", product);
                cv.put("cost", cost);
                cv.put("sent", "0");
                cv.put("times", times);
                cv.put("notes", notes);
                db.insert("sls_competitor_entry", null, cv);
            }else{

                ContentValues cv = new ContentValues();
                cv.put("competitor", competitor);
                cv.put("sls_id", sls_id);
                cv.put("outlet_id", outlet_id);
                cv.put("date_visit", date_visit);
                cv.put("activity", activity);
                cv.put("product", product);
                cv.put("cost", cost);
                cv.put("times", times);
                cv.put("notes", notes);
                db.update("sls_competitor_entry", cv, "id=?", new String[]{String.valueOf(id)});

                 }
            return true;
        }
        catch(Exception ex){
            Log.e("ERR",ex.toString());
            return false;
        }
    }

}
