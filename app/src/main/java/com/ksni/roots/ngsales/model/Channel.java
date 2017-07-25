package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 16/09/2015.
 */
public class Channel {
    private SQLiteDatabase db;
    private String gchannel;
    private String channel;
    private String description;
    private String gchannelDesc;

    public Channel(){
        this.db = null;
    }

    public Channel(SQLiteDatabase db){
        this.db = db;
    }

    public void setGroupChannel(String value){
        gchannel = value;
    }

    public String getGroupChannel(){
        return gchannel;
    }

    public void setChannel(String value){
        channel = value;
    }

    public String getChannel(){
        return channel;
    }

    public void setDescription(String value){
        description = value;
    }

    public String getDescription(){
        return description;
    }

    public String getGroupChannelDescription(){
        return gchannelDesc;
    }

    public static Channel getData(SQLiteDatabase db,String no){
        Order ord = null;
        try {
            Channel r = new Channel();
            Cursor cur = db.rawQuery("SELECT a.channel,a.description,a.group_channel, b.description gdescription FROM sls_channel a INNER JOIN sls_group_channel b ON a.group_channel = b.group_channel WHERE a.channel=?", new String[]{no});

            if (cur.moveToFirst()) {
                do{
                    r.channel = cur.getString(cur.getColumnIndex("channel")) ;
                    r.gchannel = cur.getString(cur.getColumnIndex("group_channel")) ;
                    r.description = cur.getString(cur.getColumnIndex("description")) ;
                    r.gchannelDesc = cur.getString(cur.getColumnIndex("gdescription"));
                }while (cur.moveToNext());

            }

            cur.close();
            return r;

        }catch (Exception ex) {
            return null;
        }
    }


    public static List<Channel> getData(SQLiteDatabase db){
        Channel r = null;
        List<Channel> buff = new ArrayList<Channel>();
        try {
            Cursor cur = db.rawQuery("SELECT a.channel,a.description,a.group_channel, b.description as gdescription FROM sls_channel a INNER JOIN sls_group_channel b ON a.group_channel = b.group_channel",null);
            if (cur.moveToFirst()) {
                do{
                    r = new Channel(null);
                    r.channel = cur.getString(cur.getColumnIndex("channel")) ;
                    r.gchannel = cur.getString(cur.getColumnIndex("group_channel")) ;
                    r.description =  cur.getString(cur.getColumnIndex("description")) ;
                    r.gchannelDesc = cur.getString(cur.getColumnIndex("gdescription"));
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
        Cursor cur = db.rawQuery("SELECT * FROM sls_channel WHERE channel=?", new String[]{channel});
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
                cv.put("channel", channel);
                cv.put("group_channel", gchannel);
                cv.put("description", description);
                db.insert("sls_channel", null, cv);
            }else{
                ContentValues cv = new ContentValues();
                cv.put("description", description);
                cv.put("group_channel", gchannel);
                db.update("sls_channel", cv, "channel=?",new String[]{channel});
            }

            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

}
