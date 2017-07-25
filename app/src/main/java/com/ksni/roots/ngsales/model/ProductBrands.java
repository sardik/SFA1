package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class ProductBrands {
    private SQLiteDatabase db;
    private String brandsId;
    private String brandsName;

    public ProductBrands(){
        this.db = null;
    }

    public ProductBrands(SQLiteDatabase db){
        this.db = db;
    }

    public void setBrandsId(String value){
        brandsId = value;
    }

    public String getBrandsId(){
        return brandsId;
    }

    public void setBrandsName(String value){
        brandsName = value;
    }

    public String getBrandsName(){
        return brandsName;
    }

    public static ArrayList<ProductBrands> getAllBrands(SQLiteDatabase db){
        ProductBrands r = null;
        ArrayList<ProductBrands> Brands = new ArrayList<ProductBrands>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_brand",null);
            if (cur.moveToFirst()) {
                do{
                    r = new ProductBrands(null);
                    r.brandsId = cur.getString(cur.getColumnIndex("brand_id")) ;
                    r.brandsName = cur.getString(cur.getColumnIndex("brand_name")) ;
                    Brands.add(r);
                }while (cur.moveToNext());
            }
            cur.close();
            return Brands;

        }catch (Exception ex) {
            return null;
        }
    }

    public static ProductBrands getBrand(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            ProductBrands r = new ProductBrands();
            Cursor cur = db.rawQuery("SELECT * FROM sls_brand WHERE brand_id=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.brandsId = cur.getString(cur.getColumnIndex("brand_id")) ;
                    r.brandsName = cur.getString(cur.getColumnIndex("brand_name")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT * FROM sls_brand WHERE brand_id=?", new String[]{brandsId});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public boolean save(){

        try {
            if (!isExist()) {
                ContentValues cv = new ContentValues();
                cv.put("brand_id", brandsId);
                cv.put("brand_name", brandsName);
                db.insert("sls_brand", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("brand_name", brandsName);
                db.update("sls_brand", cv, "brand_id=?",new String[]{brandsId});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
