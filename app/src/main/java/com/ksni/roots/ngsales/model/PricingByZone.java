package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by #roots on 08/09/2015.
 */
public class PricingByZone {
    final static String TABLE = "sls_pricing_by_zone";
    public String   zone;
    public String   product_id;
    public double   price;
    public String   valid_from;
    public String   valid_to;
    public String   uom;
    public int      id;
    public double   from_value;
    public double   to_value;

    private SQLiteDatabase db;

    public PricingByZone(SQLiteDatabase db){
        zone= "";
        product_id= "";
        price = 0;
        from_value = 0;
        to_value = 0;

        this.db = db;
    }


    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE zone=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{zone,product_id,valid_from,valid_to,String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public void delete(){
        db.delete(TABLE, "zone=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{zone,product_id,valid_from,valid_to,String.valueOf(id)});
    }

    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if (!isExist()) {
                cv.put("zone", zone);
                cv.put("product_id", product_id);
                cv.put("price", price);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                cv.put("uom", uom);
                cv.put("id", id);
                //Log.e("insert price", "zone");
                db.insert(TABLE, null, cv);
            }else
            {
                //Log.e("update price","zone");
                cv.put("price", price);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                cv.put("uom", uom);
                db.update(TABLE, cv,"zone=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{zone,product_id,valid_from,valid_to,String.valueOf(id)});
            }
            return true;
        }catch(Exception x){
            Log.e("error price", x.toString());
            return false;
        }

    }

}
