package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ksni.roots.ngsales.util.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class ServerConfig {
    private SQLiteDatabase db;
    private String setting_key;
    private String setting_value;
    private String description;


    public static void deleteAll(Context ctx){
        DBManager dm = DBManager.getInstance(ctx);
        dm.database().delete("sls_config",null,null);
    }

    public ServerConfig(){
        this.db = null;
    }

    public ServerConfig(SQLiteDatabase db){
        this.db = db;
    }

    public void setKey(String value){
        setting_key = value;
    }

    public String getKey(){
        return setting_key;
    }

    public void setValue(String value){
        setting_value = value;
    }

    public String getValue(){
        return setting_value;
    }


    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static ServerConfig getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            ServerConfig r = new ServerConfig();
            Cursor cur = db.rawQuery("SELECT * FROM sls_config WHERE setting_key=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.setting_key = cur.getString(cur.getColumnIndex("setting_key")) ;
                    r.setting_value = cur.getString(cur.getColumnIndex("setting_value")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<ServerConfig> getData(SQLiteDatabase db){
        ServerConfig r = null;
        List<ServerConfig> buff = new ArrayList<ServerConfig>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_config",null);
            if (cur.moveToFirst()) {
                do{
                    r = new ServerConfig(null);
                    r.setting_key = cur.getString(cur.getColumnIndex("setting_key")) ;
                    r.setting_value = cur.getString(cur.getColumnIndex("setting_value")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_config WHERE setting_key=?", new String[]{setting_key});
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
                cv.put("setting_key", setting_key);
                cv.put("setting_value", setting_value);
                cv.put("description", description);
                db.insert("sls_config", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                cv.put("setting_value", setting_value);
                db.update("sls_config", cv, "setting_key=?",new String[]{setting_key});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
