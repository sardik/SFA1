package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class OutletTop {
    private SQLiteDatabase db;
    public String top_id;
    public String description;

    public OutletTop(){
        this.db = null;
    }
    public OutletTop(SQLiteDatabase db){
        this.db = db;
    }


    public static OutletTop getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            OutletTop r = new OutletTop();
            Cursor cur = db.rawQuery("SELECT * FROM sls_top WHERE top_id=?", new String[]{no});

            if (cur.moveToFirst()){
                do{
                    r.top_id = cur.getString(cur.getColumnIndex("top_id")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<OutletTop> getData(SQLiteDatabase db){
        OutletTop r = null;
        List<OutletTop> buff = new ArrayList<OutletTop>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_top",null);
            if (cur.moveToFirst()){
                do{
                    r = new OutletTop(null);
                    r.top_id = cur.getString(cur.getColumnIndex("top_id")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_top WHERE top_id=?", new String[]{top_id});
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
                cv.put("top_id", top_id);
                cv.put("description", description);
                db.insert("sls_top", null, cv);
            }else{

                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_top", cv, "top_id=?", new String[]{top_id});

                 }
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
