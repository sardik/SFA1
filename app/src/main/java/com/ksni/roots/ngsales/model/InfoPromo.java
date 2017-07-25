package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

/**
 * Created by #roots on 28/10/2015.
 */
public class InfoPromo {
    final static String TABLE = "sls_information";
    public int id;
    public String valid_from;
    public String valid_to;
    public String content;
    private SQLiteDatabase db;

    public InfoPromo(SQLiteDatabase db){
        this.db = db;
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE id =?", new String[]{String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }


    public static String isShow(Context ctx){
        String con = null;

        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur=dm.database().rawQuery("SELECT content FROM sls_information WHERE ? between valid_from and valid_to", new String[]{Helper.getCurrentDate()});
        if(cur.moveToFirst()){
            con = "";
            do{
                con += cur.getString(0)+"\n";
            }while(cur.moveToNext());

        }
        cur.close();
        return con;

    }
    public boolean save() {
        try {

            ContentValues cv = new ContentValues();
            if (isExist()) {
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("content", content);
                db.update(TABLE, cv, "id=?", new String[]{String.valueOf(id)});
            } else {
                cv.put("id", id);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("content", content);
                db.insert(TABLE, null, cv);

            }
            return true;
        } catch (Exception x) {
            return false;
        }
    }

}
