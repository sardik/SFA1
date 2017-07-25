package com.ksni.roots.ngsales.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.UomConversion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 11/09/2015.
 */
public class LoadingUnloading {
    public static final int TYPE_LOADING = 0;
    public static final int TYPE_UNLOADING = 1;
    private long transaction_id;
    private String loading_date;
    private String sls_id;
    private String reference;
    private int type_trans;
    private List<OrderItem> items;
    private SQLiteDatabase db;

    public List<OrderItem> getItems(){
        return items;
    }


    public LoadingUnloading(SQLiteDatabase db){
        this.db = db;
        items = new ArrayList<OrderItem>();
    }

    public void setReference(String value ){
        reference = value;
    }

    public void setLoadingDate(String value ){
        loading_date = value;
    }

    public void setTransaction(int value ){
        type_trans  = value;
    }

    public void setSales(String value ){
        sls_id  = value;
    }
    public void setLoadingId(long value ){
        transaction_id  = value;
    }

    public void addItem(OrderItem itm){
        items.add(itm);
    }


    public static List<OrderItem> getStockList(Context ctx){
        List<OrderItem> lists = new ArrayList<OrderItem>();
        List<Stock> stocks = Stock.getStockListFromProduct(ctx,"");

        OrderItem oi = null;
        int cnt = 0;
        for(Stock stock:stocks){
            oi = new OrderItem();
            oi.productId = stock.product_id;
            oi.productName = stock.description;
            oi.largeToSmall = stock.largeToSmall;
            oi.mediumToSmall = stock.mediumToSmall;
            oi.uomLarge = stock.uomLarge;
            oi.uomMedium = stock.uomMedium;
            oi.uomSmall = stock.uomSmall;
            oi.brand = stock.brand;
            oi.qty = 0;
            oi.uom ="";

            cnt++;
            oi.id = cnt;
            UomConversion conv = new UomConversion(stock.qty,oi.largeToSmall,oi.mediumToSmall);
            conv.fromSmall();
            if (conv.getLarge()>0) {
                try {
                    OrderItem objLarge = oi.clone();
                    objLarge.uom = oi.uomLarge;
                    objLarge.qty = (int) conv.getLarge();
                    lists.add(objLarge);
                }catch (CloneNotSupportedException ex){}
            }

            if (conv.getMedium()>0) {
                try {
                    OrderItem objMedium = oi.clone();
                    objMedium.uom = oi.uomMedium;
                    objMedium.qty = (int) conv.getMedium();
                    lists.add(objMedium);
                }catch (CloneNotSupportedException ex){}
            }

            if (conv.getSmall()>0) {
                try {
                    OrderItem objSmall = oi.clone();
                    objSmall.uom = oi.uomSmall;
                    objSmall.qty = (int) conv.getSmall();
                    lists.add(objSmall);
                }catch (CloneNotSupportedException ex){}
            }


        }

        return lists;
    }
    public boolean delete(){
        try {
            db.delete("sls_van", "transaction_id=?", new String[]{String.valueOf(transaction_id)});
            db.delete("sls_van_item", "transaction_id=?", new String[]{String.valueOf(transaction_id)});
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }
    public boolean update(){
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("reference", reference);
            //cv.put("sls_id", sls_id);
            cv.put("transaction_date", loading_date);
            //cv.put("type_transaction", type_trans);


            db.update("sls_van", cv, "transaction_id=?", new String[]{String.valueOf(transaction_id)});
            db.delete("sls_van_item", "transaction_id=?", new String[]{String.valueOf(transaction_id)});

            int cnt = 0;
            for (OrderItem itm : items) {
                if(itm.qty>0 ) {
                    ContentValues cvi = new ContentValues();
                    cvi.put("transaction_id", transaction_id);
                    cnt++;
                    cvi.put("item", itm.id);
                    cvi.put("product_id", itm.productId);
                    cvi.put("description", itm.productName);
                    cvi.put("qty", itm.qty);
                    cvi.put("brand", itm.brand);
                    cvi.put("uom", itm.uom);

                    if(itm.uom.equals(itm.uomSmall))
                        cvi.put("qty_pcs", itm.qty);
                    else if(itm.uom.equals(itm.uomLarge))
                        cvi.put("qty_pcs", itm.qty*itm.largeToSmall);
                    else if(itm.uom.equals(itm.uomMedium))
                        cvi.put("qty_pcs", itm.qty*itm.mediumToSmall);
                    else
                        cvi.put("qty_pcs", itm.qty);


                    cvi.put("large_to_small", itm.largeToSmall);
                    cvi.put("medium_to_small", itm.mediumToSmall);

                    cvi.put("small_uom", itm.uomSmall);
                    cvi.put("medium_uom", itm.uomMedium);
                    cvi.put("large_uom", itm.uomLarge);


                    db.insert("sls_van_item", null, cvi);
                }
            }

            db.setTransactionSuccessful();
            return true;
        }
        catch(Exception ex){
            return false;
        }
        finally {
            db.endTransaction();
        }

    }

    public long save(){
        long qtyInPcs = 0;
        long lastId = -1;
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("reference", reference);
            cv.put("sls_id", sls_id);
            cv.put("transaction_date", loading_date);
            cv.put("type_transaction", type_trans);


            lastId =  db.insert( "sls_van",null,cv);


            int cnt = 0;
            for (OrderItem itm : items) {
                if(itm.qty>0) {
                    ContentValues cvi = new ContentValues();
                    cvi.put("transaction_id", lastId);
                    cnt++;
                    cvi.put("type_transaction", type_trans);
                    cvi.put("item", itm.id);
                    cvi.put("product_id", itm.productId);
                    cvi.put("description", itm.productName);
                    cvi.put("qty", itm.qty);
                    cvi.put("uom", itm.uom);
                    cvi.put("brand", itm.brand);

                    cvi.put("small_uom", itm.uomSmall);
                    cvi.put("medium_uom", itm.uomMedium);
                    cvi.put("large_uom", itm.uomLarge);



                    cvi.put("large_to_small", itm.largeToSmall);
                    cvi.put("medium_to_small", itm.mediumToSmall);



                    if(itm.uom.equals(itm.uomSmall)) {
                        qtyInPcs = itm.qty;
                        cvi.put("qty_pcs", itm.qty);
                    }
                    else if(itm.uom.equals(itm.uomLarge)) {
                        qtyInPcs = itm.qty * itm.largeToSmall;
                        cvi.put("qty_pcs", itm.qty * itm.largeToSmall);
                    }
                    else if(itm.uom.equals(itm.uomMedium)) {
                        qtyInPcs = itm.qty * itm.mediumToSmall;
                        cvi.put("qty_pcs", itm.qty * itm.mediumToSmall);
                    }
                    else {
                        qtyInPcs = itm.qty;
                        cvi.put("qty_pcs", itm.qty);
                    }


                    /*Stock stk = new Stock(db);
                    stk.product_id = itm.productId;
                    stk.description = itm.productName;

                    stk.uomLarge = itm.uomLarge;
                    stk.uomMedium = itm.uomMedium;
                    stk.uomSmall = itm.uomSmall;

                    stk.brand = itm.brand;

                    stk.largeToSmall = itm.largeToSmall;
                    stk.mediumToSmall = itm.mediumToSmall;

                    stk.qty =qtyInPcs;


                    // remove by trigger
                    if(type_trans==TYPE_LOADING) {
                    //    stk.addGoodStock();
                    }
                    else
                      //  stk.substractGoodStock();
            */
                    Log.e("qtyInPcs",String.valueOf(qtyInPcs));
                    Log.e("type_trans",String.valueOf(type_trans));
                    db.insert("sls_van_item", null, cvi);
                }
            }

            db.setTransactionSuccessful();
            return lastId;
        }
            catch(Exception ex){
                return -1;
            }
        finally {
            db.endTransaction();
        }

    }




    public static long getLastId(SQLiteDatabase db,String cust){
        long no = -1;
        String sql = "SELECT transaction_id FROM sls_van WHERE status = 0 and outlet_id=? ORDER BY transaction_id DESC";
        Cursor cur = db.rawQuery(sql, new String[]{cust});
        if (cur.moveToFirst()){
            no = cur.getLong(0);
        }
        cur.close();
        return no;
    }

    public static void unlockAllOrder(Context ctx){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        cv.put("locked",0);
        dm.database().update("sls_van", cv, null, null);
    }



    public static void lockUnlockOrder(Context ctx,long id,int lockedValue){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        cv.put("locked",lockedValue);
        dm.database().update("sls_van",cv,"transaction_id=?",new String[]{String.valueOf(id)});
    }

}
