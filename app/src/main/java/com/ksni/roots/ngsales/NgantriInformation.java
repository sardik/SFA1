package com.ksni.roots.ngsales;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 10/12/2015.
 */
public class NgantriInformation {
    public static int HTTP_CONECTION_TIMEOUT = 300;
    public static int HTTP_READ_TIMEOUT = 300;

    public final static int KEY_CALL_PLAN = 0;
    public final static int KEY_MASTER_DATA = 1;
    public final static int KEY_GPS = 2;
    public final static int KEY_NOO = 3;
    public final static int KEY_ORDER = 4;
    public final static int KEY_WORK_IN = 5;
    public final static int KEY_WORK_OUT = 6;
    public final static int KEY_LOADING = 7;
    public final static int KEY_NO_CALL = 8;

    public final static int STATUS_RELEASE = 0;
    public final static int STATUS_ACTIVE = 1;
    public final static int STATUS_DONE = 2;
    public final static int STATUS_ERROR = 3;
    public final static int STATUS_CANCEL = 4;
    public final static int STATUS_RETRY = 5;

    public long id;
    public int key;
    public String description;
    public String value;
    public String time;
    public String data;
    public int status;
    private Context context;

    public NgantriInformation(Context ctx){
        context = ctx;
    }

    private boolean isExistCallPlan(){
        boolean ada=false;
        DBManager dm= DBManager.getInstance(context);
        Cursor cur = dm.database().rawQuery("SELECT key,status FROM queue WHERE key=? and status<>?",new String[]{String.valueOf(KEY_CALL_PLAN),String.valueOf(STATUS_DONE)});
        if (cur.moveToFirst())
            ada= true;
        else
            ada= false;
        cur.close();
        return ada;
    }

    private boolean isExistMasterData(){
        boolean ada=false;
        DBManager dm= DBManager.getInstance(context);
        Cursor cur = dm.database().rawQuery("SELECT key,status FROM queue WHERE key=? and status<>?",new String[]{String.valueOf(KEY_MASTER_DATA),String.valueOf(STATUS_DONE)});
        if (cur.moveToFirst())
            ada= true;
        else
            ada= false;
        cur.close();
        return ada;
    }

    public long addAntrian(){
        boolean executed = true;
        long ret = -1;

        if(key==KEY_CALL_PLAN){
            if(isExistCallPlan()) executed = false;
        }else if(key==KEY_MASTER_DATA){
            if(isExistMasterData()) executed = false;
        }

        if (executed) {
            DBManager dm = DBManager.getInstance(context);

            ContentValues cv = new ContentValues();
            cv.put("key", key);
            cv.put("value", value);
            cv.put("description", description);
            cv.put("data", data);
            cv.put("time", Helper.getCurrentDateTime());
            cv.put("status", STATUS_RELEASE);
            ret=  dm.database().insert("queue", null, cv);

            //Log.e("Set Status", "" + NgantriInformation.STATUS_RELEASE);
            Log.e("SET STATUS", "READY TO RELEASE"); //NgantriInformation.STATUS_RELEASE
        }

        //Log.e("ANTRIAN", "KEY = " + key);
        Helper.notifyQueue(context); // <<< PENTING

        return ret;
    }

    public static void setStatus(Context ctx,int stat,long id){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        cv.put("status",stat);
        dm.database().update("queue", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public static void setDoneStatus(Context ctx,long id){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        cv.put("status",STATUS_DONE);
        cv.put("time",Helper.getCurrentDateTime());
        dm.database().update("queue", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public static long getStatus(Context ctx,long id){
        long res = -1;
        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery("SELECT id FROM queue WHERE id=? ",new String[]{String.valueOf(id)});
        if (cur.moveToFirst()){
            res = cur.getLong(0);
        }
        cur.close();
        return res;
    }

    public static List<NgantriInformation> getListAntrian(Context ctx ){
        List<NgantriInformation> antris = new ArrayList<NgantriInformation>();
        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery("SELECT * FROM queue WHERE DATE(time)=? or ( status<>? and DATE(time)<>?) ORDER by id DESC",new String[]{Helper.getCurrentDate(),String.valueOf(STATUS_DONE),Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            NgantriInformation antri = null;
            do{
                antri = new NgantriInformation(ctx);
                antri.id = cur.getLong(cur.getColumnIndex("id"));
                antri.key= cur.getInt(cur.getColumnIndex("key"));
                antri.value = cur.getString(cur.getColumnIndex("value"));
                antri.description = cur.getString(cur.getColumnIndex("description"));
                antri.status = cur.getInt(cur.getColumnIndex("status"));
                antri.data = cur.getString(cur.getColumnIndex("data"));
                antri.time = cur.getString(cur.getColumnIndex("time"));
                antris.add(antri);

            }while(cur.moveToNext());

        }
        cur.close();
        return antris;
    }

    public static List<NgantriInformation> getListAntrianFailed(Context ctx ){
        List<NgantriInformation> antris = new ArrayList<NgantriInformation>();
        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery("SELECT * FROM queue WHERE status<>? ORDER by id",new String[]{String.valueOf(STATUS_DONE)});
        if (cur.moveToFirst()){
            NgantriInformation antri = null;
            do{
                antri = new NgantriInformation(ctx);
                antri.id = cur.getLong(cur.getColumnIndex("id"));
                antri.key= cur.getInt(cur.getColumnIndex("key"));
                antri.value = cur.getString(cur.getColumnIndex("value"));
                antri.description = cur.getString(cur.getColumnIndex("description"));
                antri.status = cur.getInt(cur.getColumnIndex("status"));
                antri.data = cur.getString(cur.getColumnIndex("data"));
                antri.time = cur.getString(cur.getColumnIndex("time"));
                antris.add(antri);

            }while(cur.moveToNext());

        }
        cur.close();
        return antris;
    }

    public static void deleteAll(Context ctx ){
        DBManager dm = DBManager.getInstance(ctx);
        dm.database().execSQL("delete from queue");
        dm.database().execSQL("delete from sqlite_sequence where name='queue'");
    }

    public static boolean isServiceRunning(Class<?> serviceClass,Context cx) {
        ActivityManager manager = (ActivityManager)cx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExistActive(Context ctx){
            NgantriInformation antri = null;
            DBManager dm = DBManager.getInstance(ctx);
            boolean ada = false;
            Cursor cur = dm.database().rawQuery("SELECT * FROM queue WHERE status=? or status=? order by id", new String[]{String.valueOf(STATUS_ACTIVE), String.valueOf(STATUS_RETRY)});
            if (cur.moveToFirst()){
                ada = true;
            }
            cur.close();

            return ada;
        }

    public static boolean isOrderAntrian(Context ctx,String idcc){
        boolean ad = false;
        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery("SELECT * FROM queue WHERE key=? and value=? and status<>2", new String[]{String.valueOf(KEY_ORDER),idcc});
        if(cur.moveToFirst()) ad = true;
        cur.close();
        return ad;
    }

    public static NgantriInformation getLastAntrian(Context ctx,boolean forceActive ){
        NgantriInformation antri = null;
        DBManager dm = DBManager.getInstance(ctx);

        Cursor cur = null;
        if (forceActive){
            cur = dm.database().rawQuery("SELECT * FROM queue WHERE status=? or status=? order by id", new String[]{String.valueOf(STATUS_ACTIVE),String.valueOf(STATUS_RETRY)});
        }
        else{
            cur = dm.database().rawQuery("SELECT * FROM queue WHERE status=? or status=? order by id", new String[]{String.valueOf(STATUS_RELEASE), String.valueOf(STATUS_RETRY)});
        }
            if (cur.moveToFirst()) {
                antri = new NgantriInformation(ctx);
                antri.id = cur.getLong(cur.getColumnIndex("id"));
                antri.key = cur.getInt(cur.getColumnIndex("key"));
                antri.value = cur.getString(cur.getColumnIndex("value"));
                antri.description = cur.getString(cur.getColumnIndex("description"));
                antri.status = cur.getInt(cur.getColumnIndex("status"));

                antri.data = cur.getString(cur.getColumnIndex("data"));
                antri.time = cur.getString(cur.getColumnIndex("time"));
            }
            cur.close();

        return antri;
    }

}
