package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class Classification {
    private SQLiteDatabase db;
    private String classification;
    private String description;

    public Classification(){
        this.db = null;
    }

    public Classification(SQLiteDatabase db){
        this.db = db;
    }

    public void setClassification(String value){
        classification = value;
    }

    public String getClassification(){
        return classification;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static Classification getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            Classification r = new Classification();
            Cursor cur = db.rawQuery("SELECT * FROM sls_classification WHERE classification=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.classification = cur.getString(cur.getColumnIndex("classification")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<Classification> getData(SQLiteDatabase db){
        Classification r = null;
        List<Classification> buff = new ArrayList<Classification>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_classification",null);
            if (cur.moveToFirst()) {
                do{
                    r = new Classification(null);
                    r.classification = cur.getString(cur.getColumnIndex("classification")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_classification WHERE classification=?", new String[]{classification});
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
                cv.put("classification", classification);
                cv.put("description", description);
                db.insert("sls_classification", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_classification", cv, "classification=?",new String[]{classification});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
