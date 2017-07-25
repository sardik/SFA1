package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class Territory {
    private SQLiteDatabase db;
    private String territory;
    private String description;

    public Territory(){
        this.db = null;
    }

    public Territory(SQLiteDatabase db){
        this.db = db;
    }

    public void setTerritory(String value){
        territory = value;
    }

    public String getTerritory(){
        return territory;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static Territory getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            Territory r = new Territory();
            Cursor cur = db.rawQuery("SELECT * FROM sls_territory WHERE territory=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.territory = cur.getString(cur.getColumnIndex("territory")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<Territory> getData(SQLiteDatabase db){
        Territory r = null;
        List<Territory> buff = new ArrayList<Territory>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_territory",null);
            if (cur.moveToFirst()) {
                do{
                    r = new Territory(null);
                    r.territory = cur.getString(cur.getColumnIndex("territory")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_territory WHERE territory=?", new String[]{territory});
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
                cv.put("territory", territory);
                cv.put("description", description);
                db.insert("sls_territory", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_territory", cv, "territory=?",new String[]{territory});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
