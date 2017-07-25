package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class GroupChannel {
    private SQLiteDatabase db;
    private String groupChannel;
    private String description;

    public GroupChannel(){
        this.db = null;
    }

    public GroupChannel(SQLiteDatabase db){
        this.db = db;
    }

    public void setGroupChannel(String value){
        groupChannel = value;
    }

    public String getGroupChannel(){
        return groupChannel;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }


    public static GroupChannel getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            GroupChannel r = new GroupChannel();
            Cursor cur = db.rawQuery("SELECT * FROM sls_group_channel WHERE group_channel=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.groupChannel = cur.getString(cur.getColumnIndex("group_channel")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<GroupChannel> getData(SQLiteDatabase db){
        GroupChannel r = null;
        List<GroupChannel> buff = new ArrayList<GroupChannel>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM sls_group_channel",null);
            if (cur.moveToFirst()) {
                do{
                    r = new GroupChannel(null);
                    r.groupChannel = cur.getString(cur.getColumnIndex("group_channel")) ;
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_group_channel WHERE group_channel=?", new String[]{groupChannel});
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
                cv.put("group_channel", groupChannel);
                cv.put("description", description);
                db.insert("sls_group_channel", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                db.update("sls_group_channel", cv, "group_channel=?",new String[]{groupChannel});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
