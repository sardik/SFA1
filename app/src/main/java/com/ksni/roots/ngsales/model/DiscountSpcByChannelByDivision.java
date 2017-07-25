package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by #roots on 08/09/2015.
 */
public class DiscountSpcByChannelByDivision {
    final static String TABLE = "sls_discount_spc_channel_division";
    public String   channel;
    public String   division;
    public double   discount;
    public String   valid_from;
    public String   valid_to;
    public int      id;
    public String   is_qty;
    public int      from_qty;
    public int      to_qty;
    public double   from_value;
    public double   to_value;
    public String   uom;

    private SQLiteDatabase db;

    public boolean deleteAll(){
        try {
            db.delete(TABLE,null, null);
            return true;
        }catch(Exception x){
            return false;
        }
    }
    public DiscountSpcByChannelByDivision(SQLiteDatabase db){
        channel= "";
        division= "";
        discount = 0;
        this.db = db;
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE channel=? AND division=? AND valid_from=? and valid_to=? and id=?", new String[]{channel,division,valid_from,valid_to,String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }
    public void delete(){
        db.delete(TABLE, "channel=? and division=? and valid_from=? and valid_to=? and id=?", new String[]{channel,division,valid_from,valid_to,String.valueOf(id)});
    }

    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if(isExist()) {
                cv.put("discount", discount);
                cv.put("from_qty", from_qty);
                cv.put("to_qty", to_qty);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                cv.put("uom", uom);
                cv.put("is_qty", is_qty);

                db.update(TABLE, cv,"channel=? and division=? and valid_from=? and valid_to=? and id=?", new String[]{channel,division,valid_from,valid_to,String.valueOf(id)});
            }else{
                cv.put("channel", channel);
                cv.put("division", division);
                cv.put("discount", discount);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("id", id);
                cv.put("is_qty", is_qty);
                cv.put("from_qty", from_qty);
                cv.put("to_qty", to_qty);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                cv.put("uom", uom);

                db.insert(TABLE, null, cv);
            }
            return true;
        }catch(Exception x){
            return false;
        }

    }

}
