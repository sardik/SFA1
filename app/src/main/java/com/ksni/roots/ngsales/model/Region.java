package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class Region {
    private SQLiteDatabase db;
    private String region;
    private String description;

    public Region(){
        this.db = null;
    }

    public Region(SQLiteDatabase db){
        this.db = db;
    }

    public void setRegion(String value){
        region = value;
    }

    public String getRegion(){
        return region;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static Region getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            Region r = new Region();
            Cursor cur = db.rawQuery("SELECT * FROM sls_region WHERE region=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.region = cur.getString(cur.getColumnIndex("region")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<Region> getData(SQLiteDatabase db){
        Region r = null;
        List<Region> buff = new ArrayList<Region>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_region",null);
            if (cur.moveToFirst()) {
                do{
                    r = new Region(null);
                    r.region = cur.getString(cur.getColumnIndex("region")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_region WHERE region=?", new String[]{region});
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
                cv.put("region", region);
                cv.put("description", description);
                db.insert("sls_region", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_region", cv, "region=?",new String[]{region});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
