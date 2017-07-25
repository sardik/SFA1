package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by #roots on 08/09/2015.
 */
public class DiscountExtByChannelIPT {
    final static String TABLE = "sls_discount_ext_channel_ipt";
    public String   channel;
    public String   product_id;
    public double   discount;
    public String   valid_from;
    public String   valid_to;
    public String   is_qty;
    public String   is_percent;
    public int      id;
    public int      ipt;
    public int      min_qty;
    public double   min_value;
    public double   value_ex;
    public String   uom;

    private SQLiteDatabase db;

    public DiscountExtByChannelIPT(SQLiteDatabase db){
        channel= "";
        product_id= "";
        discount = 0;
        this.db = db;
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE channel=? AND valid_from=? and valid_to=? and id=?", new String[]{channel,valid_from,valid_to,String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public void delete(){
        db.delete(TABLE,  "channel=? AND valid_from=? and valid_to=?  and id=?", new String[]{channel,valid_from,valid_to,String.valueOf(id)});
    }

    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if (isExist()){
                cv.put("discount", discount);
                cv.put("min_qty", min_qty);
                cv.put("min_value", min_value);
                cv.put("is_qty", is_qty);
                cv.put("is_percent", is_percent);
                cv.put("ipt", ipt);
                cv.put("uom", uom);
                cv.put("value_ex", value_ex);
                Log.e("UPDATE IPT MASTER", "");
                db.update(TABLE, cv, "channel=? AND valid_from=? and valid_to=?  and id=?", new String[]{channel,valid_from,valid_to,String.valueOf(id)});

            }
            else{
                cv.put("channel", channel);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("id", id);
                cv.put("ipt", ipt);
                cv.put("min_qty", min_qty);
                cv.put("is_qty", is_qty);
                cv.put("is_percent", is_percent);
                cv.put("value_ex", value_ex);
                cv.put("min_value", min_value);
                cv.put("uom", uom);
                cv.put("discount", discount);
                Log.e("INSERT IPT MASTER","");
                db.insert(TABLE, null, cv);

            }
            return true;
        }catch(Exception x){
            Log.e("ERR IPT",x.toString());
            return false;
        }

    }

    public boolean deleteAll(){
        try {
            db.delete(TABLE, null, null);
            return true;
        }catch(Exception x){
            return false;
        }
    }

}
