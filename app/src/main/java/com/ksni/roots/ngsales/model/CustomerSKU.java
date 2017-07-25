package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ksni.roots.ngsales.util.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class CustomerSKU {
    private SQLiteDatabase db;
    public String id;
    public String product_id;
    public String outlet_id;
    public int qty_last;
    public String uom;

    public CustomerSKU(){
        this.db = null;
    }

    public CustomerSKU(SQLiteDatabase db){
        this.db = db;
    }

    public static CustomerSKU getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            CustomerSKU r = new CustomerSKU();
            Cursor cur = db.rawQuery("SELECT * FROM sls_sku_template WHERE id=?", new String[]{no});
            if (cur.moveToFirst()){
                do{
                    r.id = cur.getString(cur.getColumnIndex("id")) ;
                    r.product_id = cur.getString(cur.getColumnIndex("product_id")) ;
                    r.qty_last = cur.getInt(cur.getColumnIndex("qty_last"));
                    r.outlet_id = cur.getString(cur.getColumnIndex("outlet_id"));
                    r.uom = cur.getString(cur.getColumnIndex("uom"));
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<CustomerSKU> getData(SQLiteDatabase db){
        CustomerSKU r = null;
        List<CustomerSKU> buff = new ArrayList<CustomerSKU>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_sku_template",null);
            if (cur.moveToFirst()){
                do{
                    r = new CustomerSKU(null);
                    r.id = cur.getString(cur.getColumnIndex("id")) ;
                    r.product_id = cur.getString(cur.getColumnIndex("product_id")) ;
                    r.qty_last = cur.getInt(cur.getColumnIndex("qty_last")) ;
                    r.uom = cur.getString(cur.getColumnIndex("uom"));
                    r.outlet_id = cur.getString(cur.getColumnIndex("outlet_id"));
                    buff.add(r);
                }while (cur.moveToNext());
            }
            cur.close();
            return buff;

        }catch (Exception ex) {
            return null;
        }
    }

    public static void deleteAll(Context ctx){
        DBManager dm = DBManager.getInstance(ctx);
        dm.database().delete("sls_sku_template",null,null);
    }
    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT * FROM sls_sku_template WHERE id=?", new String[]{id});
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
                cv.put("id", id);
                cv.put("product_id", product_id);
                cv.put("qty_last", qty_last);
                cv.put("outlet_id", outlet_id);
                cv.put("uom", uom);
                db.insert("sls_sku_template", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("qty_last", qty_last);
                cv.put("uom", uom);
                db.update("sls_sku_template", cv, "id=? AND product_id", new String[]{id,product_id});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
