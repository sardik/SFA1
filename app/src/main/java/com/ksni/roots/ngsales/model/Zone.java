package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class Zone {
    private SQLiteDatabase db;
    private String zone;
    private String description;

    public Zone(){
        this.db = null;
    }

    public Zone(SQLiteDatabase db){
        this.db = db;
    }

    public void setZone(String value){
        zone = value;
    }

    public String getZone(){
        return zone;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static Zone getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            Zone r = new Zone();
            Cursor cur = db.rawQuery("SELECT * FROM sls_zone WHERE zone=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.zone = cur.getString(cur.getColumnIndex("zone")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<Zone> getData(SQLiteDatabase db){
        Zone r = null;
        List<Zone> buff = new ArrayList<Zone>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_zone",null);
            if (cur.moveToFirst()) {
                do{
                    r = new Zone(null);
                    r.zone = cur.getString(cur.getColumnIndex("zone")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_zone WHERE zone=?", new String[]{zone});
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
                cv.put("zone", zone);
                cv.put("description", description);
                db.insert("sls_zone", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_zone", cv, "zone=?",new String[]{zone});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
