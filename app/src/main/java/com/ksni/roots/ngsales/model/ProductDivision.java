package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class ProductDivision {
    private SQLiteDatabase db;
    private String division;
    private String description;

    public ProductDivision(){
        this.db = null;
    }

    public ProductDivision(SQLiteDatabase db){
        this.db = db;
    }

    public void setDivision(String value){
        division = value;
    }

    public String getDivision(){
        return division;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static ProductDivision getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            ProductDivision r = new ProductDivision();
            Cursor cur = db.rawQuery("SELECT * FROM sls_division WHERE division=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.division = cur.getString(cur.getColumnIndex("division")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<ProductDivision> getData(SQLiteDatabase db){
        ProductDivision r = null;
        List<ProductDivision> buff = new ArrayList<ProductDivision>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_division",null);
            if (cur.moveToFirst()) {
                do{
                    r = new ProductDivision(null);
                    r.division = cur.getString(cur.getColumnIndex("division")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_division WHERE division=?", new String[]{division});
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
                cv.put("division", division);
                cv.put("description", description);
                db.insert("sls_division", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_division", cv, "division=?",new String[]{division});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
