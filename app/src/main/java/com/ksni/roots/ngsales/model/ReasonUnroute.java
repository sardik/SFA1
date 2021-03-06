package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class ReasonUnroute {
    private SQLiteDatabase db;
    private String reason_id;
    private String description;

    public ReasonUnroute(){
        this.db = null;
    }

    public ReasonUnroute(SQLiteDatabase db){
        this.db = db;
    }

    public void setReason(String value){
        reason_id = value;
    }

    public String getReason(){
        return reason_id;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static ReasonUnroute getData(SQLiteDatabase db,long no){
        Order ord = null;
        try {
            ReasonUnroute r = new ReasonUnroute();
            Cursor cur = db.rawQuery("SELECT * FROM sls_reason_unroute WHERE reason_id=?", new String[]{String.valueOf(no)});

            if (cur.moveToFirst()) {
                do{
                    r.reason_id = cur.getString(cur.getColumnIndex("reason_id")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<ReasonUnroute> getData(SQLiteDatabase db){
        ReasonUnroute r = null;
        List<ReasonUnroute> buff = new ArrayList<ReasonUnroute>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_reason_unroute",null);
            if (cur.moveToFirst()) {
                do{
                    r = new ReasonUnroute(null);
                    r.reason_id = cur.getString(cur.getColumnIndex("reason_id")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_reason_unroute WHERE reason_id=?", new String[]{reason_id});
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
                cv.put("reason_id", reason_id);
                cv.put("description", description);
                db.insert("sls_reason_unroute", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_reason_unroute", cv, "reason_id=?",new String[]{reason_id});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
