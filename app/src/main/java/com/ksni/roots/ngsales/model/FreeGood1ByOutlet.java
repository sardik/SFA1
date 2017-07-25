package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by #roots on 08/09/2015.
 */
public class FreeGood1ByOutlet {
    final static String TABLE = "sls_free_good_customer";
    public String   outlet_id;
    public String   product_id;
    public String   valid_from;
    public String   valid_to;
    public int      qty;
    public int      id;

    public int      min_qty;
    public int      buy_qty;
    public String   uom;

    public String   product_free;
    public int      free_qty;
    public String   multiple;
    public String   proportional;
    public String   free_uom;

    private SQLiteDatabase db;

    public FreeGood1ByOutlet(SQLiteDatabase db){
        outlet_id= "";
        product_id= "";
        this.db = db;
    }


    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE outlet_id=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{outlet_id,product_id,valid_from,valid_to,String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public void delete(){
        db.delete(TABLE, "outlet_id=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{outlet_id, product_id, valid_from, valid_to, String.valueOf(id)});
    }

    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if(isExist()) {
                cv.put("free_uom", free_qty);
                cv.put("min_qty", min_qty);
                cv.put("buy_qty", buy_qty);
                cv.put("proportional", proportional);
                cv.put("uom", uom);
                cv.put("product_free", product_free);
                cv.put("free_qty", free_qty);
                cv.put("multiple", multiple);
                cv.put("free_uom", free_uom);
                db.update(TABLE, cv, "outlet_id=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{outlet_id, product_id, valid_from, valid_to, String.valueOf(id)});
            }else{
                cv.put("outlet_id", outlet_id);
                cv.put("product_id", product_id);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("free_uom", free_qty);
                cv.put("id", id);
                cv.put("min_qty", min_qty);
                cv.put("buy_qty", buy_qty);
                cv.put("uom", uom);
                cv.put("product_free", product_free);
                cv.put("free_qty", free_qty);
                cv.put("multiple", multiple);
                cv.put("proportional", proportional);
                cv.put("free_uom", free_uom);
                db.insert(TABLE, null, cv);
            }
            return true;
        }catch(Exception x){
            return false;
        }

    }


}
