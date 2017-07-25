package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by #roots on 08/09/2015.
 */
public class PricingByChannel {
    final static String TABLE = "sls_pricing_by_channel";
    public String   channel;
    public String   product_id;
    public double   price;
    public String   valid_from;
    public String   valid_to;
    public double   from_value;
    public double   to_value;

    public int   id;
    public String   uom;
    private SQLiteDatabase db;

    public PricingByChannel(SQLiteDatabase db){
        channel= "";
        product_id= "";
        price = 0;
        from_value = 0;
        to_value = 0;
        this.db = db;
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE channel=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{channel,product_id,valid_from,valid_to,String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public void delete(){
        //Log.e("delete bray","hhh");
        db.delete(TABLE, "channel=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{channel,product_id,valid_from,valid_to,String.valueOf(id)});
    }

    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if (!isExist()){
                cv.put("channel", channel);
                cv.put("product_id", product_id);
                cv.put("price", price);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("id", id);
                cv.put("uom", uom);
                db.insert(TABLE, null, cv);
            }
            else{
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                cv.put("price", price);
                cv.put("uom", uom);

                db.update(TABLE, cv,"channel=? AND product_id=? AND valid_from=? and valid_to=? and id=?", new String[]{channel,product_id,valid_from,valid_to,String.valueOf(id)});

            }
            return true;
        }catch(Exception x){
            return false;
        }

    }

}
