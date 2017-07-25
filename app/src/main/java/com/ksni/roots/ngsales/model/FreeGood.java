package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by #roots on 01/10/2015.
 */
public class FreeGood {
    public String   channel;
    public String   division;
    public String   outlet_id;
    public String   product_id;
    public String   date;
    public String   zone;

    public int      qty;
    public String   uom;

    private int      min_qty;
    private int      buy_qty;
    private String   product_free;
    private int      free_qty;
    private String   multiple;
    private String   proportional;
    private String   free_uom;

    private SQLiteDatabase db;

    public FreeGood(SQLiteDatabase db){
        outlet_id= "";
        product_id= "";
        this.db = db;
    }


    public String getFreeSKU(){
        return product_free;
    }

    public int getMinQty(){
        return min_qty;
    }
    public int getBuyQty(){
        return buy_qty;
    }

    public int getFreeQty(){
        return free_qty;
    }

    public String getFreeUoM(){
        return free_uom;
    }

    public String getMultiply(){
        return multiple;
    }

    public String getProportional(){
        return proportional;
    }


    public boolean getFree(){
        boolean ada = false;
        if (!getFreeByCustomer()) {
            if (!getFreeByChannel()) {
                if (getFreeByZone()) ada = true;
            }
            else
                ada = true;
        }
        else
            ada = true;
        Log.e("aadaaa",String.valueOf(ada));
        return ada;

    }

    public boolean getFreeByCustomer(){
        double harga = -1;
        int qtyMin;
        int qtyBuy;
        String xuom;
        boolean ada = false;

        Log.e("CEK","FREE");
        Cursor cur = db.rawQuery("SELECT * FROM sls_free_good_customer WHERE outlet_id=? and product_id=? and uom=? and "+
                                 "DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and valid_from=(SELECT MAX(valid_from) FROM sls_free_good_customer WHERE outlet_id=? and product_id=? and uom=? and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )", new String[]{outlet_id,product_id,uom,outlet_id,product_id,uom});

        if (cur.moveToFirst()) {
            do {
                //xuom = cur.getString(cur.getColumnIndex("uom"));
                qtyMin = cur.getInt(cur.getColumnIndex("min_qty"));
                qtyBuy = cur.getInt(cur.getColumnIndex("buy_qty"));

                if (qty>=qtyMin ){
                //if ( ( qty>=qtyMin ) && uom.equals(xuom)  ){
                    this.product_free    = cur.getString(cur.getColumnIndex("product_free"));
                    this.min_qty        = qtyMin;
                    this.buy_qty        = qtyBuy;
                    this.free_qty        = cur.getInt(cur.getColumnIndex("free_qty"));
                    this.multiple        = cur.getString(cur.getColumnIndex("multiple"));
                    this.proportional   = cur.getString(cur.getColumnIndex("proportional"));
                    this.free_uom        = cur.getString(cur.getColumnIndex("free_uom"));
                    ada = true;
                    break;
                }

            } while (cur.moveToNext());
        }

        cur.close();
        return ada;
    }

    public boolean getFreeByChannel(){
        double harga = -1;
        int qtyMin;
        int qtyBuy;
        String xuom;
        boolean ada = false;
        Cursor cur = null;
        Log.e("pre ada free","");
        cur = db.rawQuery("SELECT * FROM sls_free_good_channel WHERE channel=? and product_id=? and uom=? and "+
                "DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and valid_from=(SELECT MAX(valid_from) FROM sls_free_good_channel WHERE channel=? and product_id=? and uom=? and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )", new String[]{channel,product_id,uom,channel,product_id,uom});

        if (cur.moveToFirst()) {
            do {
                Log.e("ada free","");
                qtyMin = cur.getInt(cur.getColumnIndex("min_qty"));
                qtyBuy = cur.getInt(cur.getColumnIndex("buy_qty"));

                if ( ( qty>=qtyMin )){
                    this.product_free    = cur.getString(cur.getColumnIndex("product_free"));
                    this.min_qty        = qtyMin;
                    this.buy_qty        = qtyBuy;
                    this.free_qty        = cur.getInt(cur.getColumnIndex("free_qty"));
                    this.multiple        = cur.getString(cur.getColumnIndex("multiple"));
                    this.proportional   = cur.getString(cur.getColumnIndex("proportional"));
                    this.free_uom        = cur.getString(cur.getColumnIndex("free_uom"));
                    ada = true;

                    break;

                }

            } while (cur.moveToNext());
        }

        cur.close();

        if (!ada) {

            // cek yg kondisi berikutnya dengan product ALL
            cur = db.rawQuery("SELECT * FROM sls_free_good_channel WHERE channel=? and product_id='ALL' and uom=? and " +
                    "DATE('" + date + "') between DATE(valid_from) and DATE(valid_to) and valid_from=(SELECT MAX(valid_from) FROM sls_free_good_channel WHERE channel=? and product_id='ALL' and uom=? and DATE('" + date + "') between DATE(valid_from) and DATE(valid_to) )", new String[]{channel, uom, channel, uom});

            if (cur.moveToFirst()) {
                do {

                    qtyMin = cur.getInt(cur.getColumnIndex("min_qty"));
                    qtyBuy = cur.getInt(cur.getColumnIndex("buy_qty"));

                    if ((qty >= qtyMin)) {
                        this.product_free = cur.getString(cur.getColumnIndex("product_free"));
                        this.min_qty = qtyMin;
                        this.buy_qty = qtyBuy;
                        this.free_qty = cur.getInt(cur.getColumnIndex("free_qty"));
                        this.multiple = cur.getString(cur.getColumnIndex("multiple"));
                        this.proportional   = cur.getString(cur.getColumnIndex("proportional"));
                        this.free_uom = cur.getString(cur.getColumnIndex("free_uom"));
                        ada = true;
                        break;
                    }

                } while (cur.moveToNext());
            }

            cur.close();
        }


        return ada;
    }


    public boolean getFreeByZone(){
        double harga = -1;
        int qtyDari;
        int qtySampai;
        String xuom;
        boolean ada = false;

//        Cursor cur = db.rawQuery("SELECT * FROM sls_free_good_channel_division WHERE channel=? and division=? ORDER BY period DESC", new String[]{channel,division});

        Cursor cur = db.rawQuery("SELECT * FROM sls_free_good_zone WHERE zone=? and product_id=? and uom=? and "+
                "DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and valid_from=(SELECT MAX(valid_from) FROM sls_free_good_zone WHERE zone=? and product_id=? and uom=? and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )", new String[]{zone,product_id,uom,zone,product_id,uom});

        if (cur.moveToFirst()) {
            do {

                //xuom = cur.getString(cur.getColumnIndex("uom"));
                qtyDari = cur.getInt(cur.getColumnIndex("min_qty"));
                qtySampai = cur.getInt(cur.getColumnIndex("buy_qty"));

                if ( qty>=qtyDari ){
                    //if ( ( qty>=qtyDari ) && uom.equals(xuom)  ){
                    this.product_free    = cur.getString(cur.getColumnIndex("product_free"));
                    this.min_qty        = qtyDari;
                    this.buy_qty        = qtySampai;
                    this.free_qty        = cur.getInt(cur.getColumnIndex("free_qty"));
                    this.multiple = cur.getString(cur.getColumnIndex("multiple"));
                    this.proportional   = cur.getString(cur.getColumnIndex("proportional"));
                    this.free_uom        = cur.getString(cur.getColumnIndex("free_uom"));
                    ada = true;
                    break;
                }

            } while (cur.moveToNext());
        }

        cur.close();
        return ada;
    }


}
