package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by #roots on 08/09/2015.
 */
public class PricingByOutlet {
    final static String TABLE = "sls_pricing_by_outlet";
    public String   outlet_id;
    public String   product_id;
    public double   price;
    public int id;
    public String uom;
    public String   valid_from;
    public String   valid_to;
    public double   from_value;
    public double   to_value;

    private SQLiteDatabase db;

    public PricingByOutlet(SQLiteDatabase db){
        outlet_id= "";
        product_id= "";
        from_value = 0;
        to_value = 0;
        price = 0;
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
        db.delete(TABLE,  "outlet_id=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{outlet_id,product_id,valid_from,valid_to,String.valueOf(id)});
    }
    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if (!isExist()){
                cv.put("outlet_id", outlet_id);
                cv.put("product_id", product_id);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                cv.put("price", price);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("uom", uom);
                cv.put("id", id);
                db.insert(TABLE, null, cv);
            }else {
                cv.put("price", price);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);

                cv.put("uom", uom);
                db.update(TABLE,  cv,"outlet_id=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{outlet_id,product_id,valid_from,valid_to,String.valueOf(id)});
            }
            return true;
        }catch(Exception x){
            return false;
        }

    }

}
