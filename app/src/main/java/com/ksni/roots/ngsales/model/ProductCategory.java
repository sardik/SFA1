package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class ProductCategory {
    private SQLiteDatabase db;
    private String category;
    private String description;

    public ProductCategory(){
        this.db = null;
    }

    public ProductCategory(SQLiteDatabase db){
        this.db = db;
    }

    public void setCategory(String value){
        category = value;
    }

    public String getCategory(){
        return category;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static ProductCategory getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            ProductCategory r = new ProductCategory();
            Cursor cur = db.rawQuery("SELECT * FROM sls_product_category WHERE category=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.category = cur.getString(cur.getColumnIndex("category")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<ProductCategory> getData(SQLiteDatabase db){
        ProductCategory r = null;
        List<ProductCategory> buff = new ArrayList<ProductCategory>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_product_category",null);
            if (cur.moveToFirst()) {
                do{
                    r = new ProductCategory(null);
                    r.category = cur.getString(cur.getColumnIndex("category")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                    buff.add(r);
                }while (cur.moveToNext());
            }
            cur.close();
            return buff;

        }catch (Exception ex) {
            return null;
        }
    }


    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT * FROM sls_product_category WHERE category=?", new String[]{category});
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
                cv.put("category", category);
                cv.put("description", description);
                db.insert("sls_product_category", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_product_category", cv, "category=?",new String[]{category});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
