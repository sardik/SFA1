package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class Competitor {
    private SQLiteDatabase db;
    public String competitor;
    public String description;

    public Competitor(){
        this.db = null;
    }
    public Competitor(SQLiteDatabase db){
        this.db = db;
    }


    public static Competitor getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            Competitor r = new Competitor();
            Cursor cur = db.rawQuery("SELECT * FROM sls_competitor WHERE competitor=?", new String[]{String.valueOf(no)});

            if (cur.moveToFirst()){
                do{
                    r.competitor = cur.getString(cur.getColumnIndex("competitor")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<Competitor> getData(SQLiteDatabase db){
        Competitor r = null;
        List<Competitor> buff = new ArrayList<Competitor>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_competitor",null);
            if (cur.moveToFirst()){
                do{
                    r = new Competitor(null);
                    r.competitor = cur.getString(cur.getColumnIndex("competitor")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_competitor WHERE competitor=?", new String[]{competitor});
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
                cv.put("description", description);
                db.insert("sls_competitor", null, cv);
            }else{

                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_competitor", cv, "competitor=?", new String[]{competitor});

                 }
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
