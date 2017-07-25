package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import com.ksni.roots.ngsales.util.UomConversion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 18/12/2015.
 */
public class Stock {
    private String period = "99";
    public String product_id;
    public String description;
    public String brand;
    public String uomLarge="";
    public String uomMedium="";
    public String uomSmall="";
    public int largeToSmall = 0;
    public int mediumToSmall = 0;
    public long qty = 0;
    public long qty_bs = 0;
    public long qty_ret = 0;

    private long calQtyLarge = 0;
    private long calQtyMedium = 0;
    private long calQtySmall = 0;

    private SQLiteDatabase db;
    final static String TABLE = "sls_van_stock";

    public Stock(){
        this.db = null;
    }

    public Stock(SQLiteDatabase db){
        this.db = db;
    }


    public static List<Stock> getStockListFromProduct(Context ctx,String sort){
        List<Product> prds = Product.getDataPure(DBManager.getInstance(ctx).database(),sort);
        List<Stock> lists = new ArrayList<Stock>();
        Stock b = null;
        for(Product prd:prds){
            if (prd.getStock()!=0){
            b = new Stock();
            b.product_id = prd.getProcutId();
            b.description = prd.getProductName();

            b.uomLarge = prd.getUomLarge();
            b.uomMedium = prd.getUomMedium();
            b.uomSmall = prd.getUomSmall();

            b.brand =prd.getBrand();


            b.largeToSmall = prd.getConversionLargeToSmall();
            b.mediumToSmall = prd.getConversionMediumToSmall();

            b.qty =prd.getStock();

            b.calculateConversion();

            lists.add(b);
            }
        }
        return lists;
    }

    public static List<Stock> getStockList(Context ctx,String sort){

        String orderby = " ORDER BY description";

        if(sort.equals("code"))
            orderby = " ORDER BY product_id";

        List<Stock> lists = new ArrayList<Stock>();
        DBManager dm = DBManager.getInstance(ctx);
        Stock b = null;
        Cursor cur = dm.database().rawQuery("SELECT * FROM " + TABLE + " WHERE qty<>0 "+orderby , null);
        if (cur.moveToFirst()){
            do{
                b = new Stock();
                b.product_id = cur.getString(cur.getColumnIndex("product_id"));
                b.description = cur.getString(cur.getColumnIndex("description"));

                b.uomLarge = cur.getString(cur.getColumnIndex("large_uom"));
                b.uomMedium = cur.getString(cur.getColumnIndex("medium_uom"));
                b.uomSmall = cur.getString(cur.getColumnIndex("small_uom"));

                b.brand = cur.getString(cur.getColumnIndex("brand"));


                b.largeToSmall = cur.getInt(cur.getColumnIndex("large_to_small"));
                b.mediumToSmall = cur.getInt(cur.getColumnIndex("medium_to_small"));

                b.qty = cur.getLong(cur.getColumnIndex("qty"));
                b.qty_bs = cur.getLong(cur.getColumnIndex("qty_bs"));
                b.qty_ret = cur.getLong(cur.getColumnIndex("qty_ret"));

                b.calculateConversion();

                lists.add(b);

            }while (cur.moveToNext());
        }
        cur.close();

        return lists;

    }


    public long getQtyLarge(){
        return calQtyLarge;
    }

    public long getQtyMedium(){
        return calQtyMedium;
    }

    public long getQtySmall(){
        return calQtySmall;
    }

    public void calculateConversion(){

        UomConversion unitc= new UomConversion(qty, largeToSmall, mediumToSmall);
        unitc.fromSmall();
        calQtyLarge = unitc.getLarge();
        calQtyMedium = unitc.getMedium();
        calQtySmall = unitc.getSmall();

    }


    private void addRemoveStock(String field,String sign,long value){
            double b = 0;
            save();
            Cursor  cur= db.rawQuery("SELECT "+field+" FROM "+TABLE+" WHERE product_id=?",new String[]{product_id});
            if(cur.moveToFirst()){
                b=cur.getLong(0);
            }
        Log.e("b",String.valueOf(b));
            cur.close();
            ContentValues cv = new ContentValues();
            if (sign.equals("+"))
                cv.put(field,b + value);
            else
                cv.put(field,b - value);

        db.update(TABLE, cv,"product_id=?", new String[]{product_id});
    }

    public void addGoodStock(){addRemoveStock("qty","+",qty);}

    public void substractGoodStock(){
        addRemoveStock("qty","-",qty);
    }

    public void addReturnStock(){
        addRemoveStock("qty_ret","+",qty_ret);
    }


    public void substractReturnStock(){
        addRemoveStock("qty_ret","-",qty_ret);
    }

    public void addBadStock(){
        addRemoveStock("qty_bs","+",qty_bs);
    }


    public void substractBadStock(){
        addRemoveStock("qty_bs","-",qty_bs);
    }


    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE product_id=?", new String[]{product_id});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public boolean save(){
        try {
            ContentValues cv = new ContentValues();
            if (!isExist()){
                cv.put("period", period);
                cv.put("product_id", product_id);
                cv.put("description", description);
                cv.put("brand", brand);

                cv.put("small_uom", uomSmall);
                cv.put("medium_uom", uomMedium);
                cv.put("large_uom", uomLarge);

                cv.put("medium_to_small", mediumToSmall);
                cv.put("large_to_small", largeToSmall);


                db.insert(TABLE, null, cv);
            }
            else{
                cv.put("brand", brand);

                cv.put("small_uom", uomSmall);
                cv.put("medium_uom", uomMedium);
                cv.put("large_uom", uomLarge);
                cv.put("description", description);
                cv.put("medium_to_small", mediumToSmall);
                cv.put("large_to_small", largeToSmall);


                db.update(TABLE, cv,"product_id=?", new String[]{product_id});

            }
            return true;
        }catch(Exception x){
            return false;
        }

    }


}
