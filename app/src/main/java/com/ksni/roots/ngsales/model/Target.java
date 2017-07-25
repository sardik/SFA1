package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.util.DBManager;

/**
 * Created by #roots on 07/08/2015.
 */
public class Target {
    public int  year;
    public int  period;
    public String id;
    public String  sku;
    public String  sls;
    public double  targetQty;
    public double  targetValue;

    public int  targetEc;
    public int  targetIPT;
    public int  targetCall;


    public double  actualQty;
    public double  actualValue;

    public double  achieveQty;
    public double  achieveValue;

    private SQLiteDatabase db;

    public Target(){
        this.db = null;
    }

    public Target(SQLiteDatabase db){
        this.db = db;
    }


    public static void deleteAll(Context ctx){
        DBManager dm = DBManager.getInstance(ctx);
        dm.database().delete("sls_target",null,null);
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT product_id FROM sls_target WHERE sls_id=? AND period=? AND product_id=? and year=?", new String[]{sls,String.valueOf(period),sku,String.valueOf(year)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public boolean save() {
        boolean ok = false;
        if (!isExist()) {
            try {
                ContentValues cv = new ContentValues();
                cv.put("id", id);
                cv.put("sls_id", sls);
                cv.put("year", year);
                cv.put("period", period);
                cv.put("product_id", sku);
                cv.put("target_qty", targetQty);
                cv.put("target_value", targetValue);
                cv.put("actual_qty", actualQty);
                cv.put("actual_value", actualValue);
                cv.put("achiev_qty", achieveQty);
                cv.put("achiev_value", achieveValue);


                cv.put("target_ec", targetEc);
                cv.put("target_call", targetCall);
                cv.put("target_ipt", targetIPT);


                db.insert("sls_target", null, cv);

                ok = true;

            } catch (Exception ex) {
                ok = false;
            }

        }
        else{


            try {
                ContentValues cv = new ContentValues();
                cv.put("target_qty", targetQty);
                cv.put("target_value", targetValue);
                cv.put("actual_qty", actualQty);
                cv.put("actual_value", actualValue);
                cv.put("achiev_qty", achieveQty);
                cv.put("achiev_value", achieveValue);

                cv.put("target_ec", targetEc);
                cv.put("target_call", targetCall);
                cv.put("target_ipt", targetIPT);

                db.update("sls_target", cv,"sls_id=? AND product_id=? AND period=? AND year=? and id=?",new String[]{sls,sku,String.valueOf(period),String.valueOf(year),id});


                ok = true;

            } catch (Exception ex) {
                ok = false;
            }

        }
            return ok;
    }

}
