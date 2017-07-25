package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by #roots on 28/10/2015.
 */
public class RouteAssign {
    final static String TABLE = "sls_week";
    public String outlet_id;
    public String sls_id;
    public int day;
    public String w1;
    public String w2;
    public String w3;
    public String w4;
    public String route;
    public int squence;
    private SQLiteDatabase db;

    public RouteAssign(SQLiteDatabase db){
        this.db = db;
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE sls_id = ? AND outlet_id=?", new String[]{sls_id,outlet_id});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }


    public boolean save() {
        try {

            ContentValues cv = new ContentValues();
            if (isExist()) {
                cv.put("day",       day);
                cv.put("w1",        w1);
                cv.put("w2",        w2);
                cv.put("w3",        w3);
                cv.put("w4",        w4);
                cv.put("route",     route);
                cv.put("squence",   squence);
                db.update(TABLE, cv, "outlet_id=? AND sls_id=?", new String[]{outlet_id,sls_id});
            } else {
                cv.put("outlet_id", outlet_id);
                cv.put("sls_id",    sls_id);
                cv.put("day",       day);
                cv.put("w1",        w1);
                cv.put("w2",        w2);
                cv.put("w3",        w3);
                cv.put("w4",        w4);
                cv.put("route",     route);
                cv.put("squence",   squence);
                db.insert(TABLE, null, cv);

            }
            return true;
        } catch (Exception x) {
            return false;
        }
    }

}
