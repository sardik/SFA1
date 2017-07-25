package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by #roots on 08/09/2015.
 */
public class DiscountExtByChannelIndia {
    final static String TABLE = "sls_discount_ext_channel_india";
    public String   channel;
    public String   zone;
    public double   discount;
    public String   valid_from;
    public String   valid_to;
    public int      id;
    public double   from_value;
    public double   to_value;

    private SQLiteDatabase db;

    public DiscountExtByChannelIndia(SQLiteDatabase db){
        channel= "";
        discount = 0;
        this.db = db;
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE channel=? AND zone=? and valid_from=? and valid_to=? and id=?", new String[]{channel,zone,valid_from,valid_to,String.valueOf(id)});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }
    public void delete(){
        db.delete(TABLE, "channel=? and zone=? AND valid_from=? and valid_to=? and id=?", new String[]{channel,zone,valid_from,valid_to,String.valueOf(id)});
    }

    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if (isExist()){
                cv.put("discount", discount);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);
                Log.e("UPDATE EXT","OK");
                db.update(TABLE, cv, "channel=? and zone=? AND valid_from=? and valid_to=? and id=?", new String[]{channel,zone,valid_from,valid_to,String.valueOf(id)});

            }
            else{
                cv.put("channel", channel);
                cv.put("zone", zone);
                cv.put("valid_from", valid_from);
                cv.put("valid_to", valid_to);
                cv.put("id", id);
                cv.put("from_value", from_value);
                cv.put("to_value", to_value);

                cv.put("discount", discount);
                db.insert(TABLE, null, cv);

            }
            return true;
        }catch(Exception x){
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
