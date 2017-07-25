package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class District {
    private SQLiteDatabase db;
    private String district;
    private String description;

    public District(){
        this.db = null;
    }

    public District(SQLiteDatabase db){
        this.db = db;
    }

    public void setDistrict(String value){
        district = value;
    }

    public String getDistrict(){
        return district;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static District getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            District r = new District();
            Cursor cur = db.rawQuery("SELECT * FROM sls_district WHERE district=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.district = cur.getString(cur.getColumnIndex("district")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<District> getData(SQLiteDatabase db){
        District r = null;
        List<District> buff = new ArrayList<District>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_district",null);
            if (cur.moveToFirst()) {
                do{
                    r = new District(null);
                    r.district = cur.getString(cur.getColumnIndex("district")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_district WHERE district=?", new String[]{district});
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
                cv.put("district", district);
                cv.put("description", description);
                db.insert("sls_district", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_district", cv, "district=?",new String[]{district});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
