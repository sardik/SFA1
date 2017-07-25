package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by #roots on 08/08/2015.
 */

public class CustomerCall extends Customer implements Serializable{
    public static final String NO_VISIT = "Unvisit";
    public static final String VISIT = "Visit";
    public static final String VISITED = "Visited";
    public static final String PAUSED = "Pause";
    public static final String NOCALL = "No Call";

    private SQLiteDatabase db;
    private String last_pause;
    private String reasonNoBarcode;
    private Bitmap bitmap = null;
    private String last_resume;
    private String week;
    private long duration;
    private String call_status;
    private String slsid;
    private String id;
    private long order_id;
    private long return_id;
    private String serverDate;
    private String status;
    private String route;
    private String startTime;
    private String reasonUnroute;
    private String endTime;
    private double jarak;


    private int squence;

    public void setReasonUnroute(String value) {
        reasonUnroute=value;
    }

    public void setJarak(double jrk){
       jarak = jrk;
   }

    public double getJarak(){
        return jarak;
    }

    public CustomerCall(){
        super();
        status = NO_VISIT;
    }

    public CustomerCall(SQLiteDatabase db){
        super();
        status = NO_VISIT;
        this.db = db;
    }

    public void setSlsId(String stat){
        slsid = stat;
    }

    public String getSlsId(){
        return slsid;
    }

    public void setServerDate(String stat){
        serverDate = stat;
    }

    public void setDuration(long durasi){
        duration = durasi;
    }

    public void setReasonNoBarcode(String brc){
        reasonNoBarcode = brc;
    }

    public String getReasonNoBarcode(){
        return reasonNoBarcode ;
    }

    public void setOrderId(long ordid){
        order_id = ordid;
    }

    public void setReturnId(long retid){
        return_id = retid;
    }

    public long getReturnId(){
        return return_id ;
    }

    public long getOrderId(){
        return order_id;
    }

    public long getDuration(){
        return duration;
    }

    public void setLastPause(String val){

        last_pause = val;

    }

    public void setLastResume(String val){
        last_resume = val;
    }

    public String getReasonUnroute(){
        return reasonUnroute;
    }

    public String getServerDate(){
        return serverDate;
    }

    public void setCallStatus(String stat){
        call_status = stat;
    }

    public String getCallStatus(){
        return call_status;
    }

    public void setId(String stat){
        id = stat;
    }

    public String getId(){
        return id;
    }

    public void setRoute(String stat){
        route = stat;
    }

    public String getRoute(){
        return route;
    }

    public void setStartTime(String stat){
        startTime = stat;
    }

    public String getStartTime(){
        return startTime;
    }

    public String getPauseTime(){
        return last_pause;
    }

    public String getResumeTime(){
        return last_resume;
    }

    public void setEndTime(String stat){
        endTime = stat;
    }

    public String getEndtTime(){
        return endTime;
    }

    public  void  setSquence(int stat){
        squence = stat;
    }

    public int getSquence(){
        return squence;
    }

    public  void  setStatus(String stat){
        status = stat;
    }

    public String getStatus(){
        return status;
    }

    public  void  setNotes(String stat){
        notes = stat;
    }

    public String getNotes(){
        return notes;
    }

    public void setWeek(String stat){
        week = stat;
    }

    public String getWeek(){
        return week;
    }

    public static void deleteAll(Context ctx){
        DBManager dm = DBManager.getInstance(ctx);
        //dm.database().delete("sls_plan_status","call_status=? or (date<>? and call_status=?)",new String[]{"1",Helper.getCurrentDate(),"0"});

        //hapus tanggal
        dm.database().delete("sls_plan_status","DATE(date)<? or (date=? and status=? and call_status=?)",new String[]{Helper.getCurrentDate(),Helper.getCurrentDate(),"Unvisit","1"});
    }

    public static String getDayByCustomer(SQLiteDatabase db,String cust){

        String ret = "";
        Date tanggal = null;
        Cursor cur = db.rawQuery("SELECT date FROM sls_plan_status WHERE outlet_id=?", new String[]{ cust});
        if (cur.moveToFirst()){
            SimpleDateFormat tgl = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = cur.getString(cur.getColumnIndex("date"));

            try {
                tanggal = tgl.parse(strDate);
            }catch(Exception ex){

            }
            Calendar c = Calendar.getInstance();
            c.setTime(tanggal);

            int result = c.get(Calendar.DAY_OF_WEEK);
            switch (result) {
                case Calendar.MONDAY:ret    = "(Sen)";break;
                case Calendar.TUESDAY:ret   = "(Sel)";break;
                case Calendar.WEDNESDAY:ret = "(Rab)";break;
                case Calendar.THURSDAY:ret  = "(Kam)";break;
                case Calendar.FRIDAY:ret    = "(Jum)";break;
                case Calendar.SATURDAY:ret  = "(Sab)";break;
                case Calendar.SUNDAY:ret    = "(Mgg)";break;
            }
        }
        cur.close();

        return ret;
    }

    public static List<CustomerCall> getCallPlan(SQLiteDatabase db,String tgl){
        CustomerCall call = null;
        List<CustomerCall> calls = new ArrayList<CustomerCall>();
        Cursor cur = db.rawQuery("SELECT * FROM sls_plan_status WHERE date=? ORDER BY squence", new String[]{ tgl});
        if (cur.moveToFirst()){
            do{
                call = new CustomerCall();


                call.setOrderId(cur.getLong(cur.getColumnIndex("order_id")));

                call.setReasonUnroute(cur.getString(cur.getColumnIndex("reason_unroute")));

                call.setReasonNoBarcode(cur.getString(cur.getColumnIndex("reason_no_barcode")));

                call.setLastPause(cur.getString(cur.getColumnIndex("last_pause")));
                call.setLastResume(cur.getString(cur.getColumnIndex("last_resume")));

                call.setDuration(cur.getLong(cur.getColumnIndex("duration")));

                call.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
                call.setWeek(cur.getString(cur.getColumnIndex("week")));
                call.setServerDate(cur.getString(cur.getColumnIndex("date")));
                call.setSlsId(cur.getString(cur.getColumnIndex("sls_id")));
                call.setStartTime(cur.getString(cur.getColumnIndex("start_time")));
                call.setEndTime(cur.getString(cur.getColumnIndex("end_time")));
                call.setStatus(cur.getString(cur.getColumnIndex("status")));
                call.setCallStatus(cur.getString(cur.getColumnIndex("call_status")));
                call.setSquence(cur.getInt(cur.getColumnIndex("squence")));
                calls.add(call);
            }while (cur.moveToNext());

        }
        return calls;
    }

    public static boolean isVisited(Context cctx,String outletidx){
        boolean vstd = false;
        SQLiteDatabase db= DBManager.getInstance(cctx).database();
        Cursor cur = db.rawQuery("SELECT outlet_id,status FROM sls_plan_status WHERE outlet_id=? and date=? and status=?",new String[]{outletidx,Helper.getCurrentDate(),VISITED});
        if (cur.moveToFirst()) vstd = true;

        cur.close();
        return vstd;
    }

    public static List<CustomerCall> getDataByDate(SQLiteDatabase db, String date,String sls,boolean onlycallplan,String xweek){
        List<CustomerCall> calls = new ArrayList<CustomerCall>();
        Cursor cur = null;
        CustomerCall call;
        String week = xweek;

        if (date!=null) {
            if (!onlycallplan) {
                cur = db.rawQuery(
                        "SELECT a.last_pause,a.last_resume, a.sls_id,a.date,a.week,a.start_time,a.end_time,a.status,b.outlet_id,b.outlet_name,b.address,b.notes,b.group_channel,b.channel,b.zone FROM sls_plan_status a INNER JOIN sls_customer b on a.outlet_id=b.outlet_id WHERE a.date=? and a.sls_id=? ORDER BY a.squence", new String[]{date, sls});
                if (cur.moveToFirst()) {
                    do {
                        call = new CustomerCall();
                        call.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
                        call.setWeek(cur.getString(cur.getColumnIndex("week")));

                        call.setLastPause(cur.getString(cur.getColumnIndex("last_pause")));
                        call.setLastResume(cur.getString(cur.getColumnIndex("last_resume")));

                        week = cur.getString(cur.getColumnIndex("week"));
                        call.setServerDate(cur.getString(cur.getColumnIndex("date")));

                        call.setSlsId(cur.getString(cur.getColumnIndex("sls_id")));
                        call.setCallStatus(cur.getString(cur.getColumnIndex("call_status")));
                        call.setStartTime(cur.getString(cur.getColumnIndex("start_time")));
                        call.setEndTime(cur.getString(cur.getColumnIndex("end_time")));
                        call.setStatus(cur.getString(cur.getColumnIndex("status")));

                        call.setNotes(cur.getString(cur.getColumnIndex("notes")));
                        call.setCustomerName(cur.getString(cur.getColumnIndex("outlet_name")));
                        call.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
                        call.setAddress(cur.getString(cur.getColumnIndex("address")));

                        //Log.e("ADDRESS", cur.getString(cur.getColumnIndex("address")));

                        call.setGroupChannel(cur.getString(cur.getColumnIndex("group_channel")));
                        call.setChannel(cur.getString(cur.getColumnIndex("channel")));
                        call.setZone(cur.getString(cur.getColumnIndex("zone")));

                        calls.add(call);
                    } while (cur.moveToNext());
                }
                cur.close();

            }
            cur = db.rawQuery("SELECT * FROM sls_customer a WHERE a.outlet_id NOT IN  (SELECT b.outlet_id FROM sls_plan_status b WHERE b.date=? AND b.sls_id=? AND b.week = ?)", new String[]{date, sls, week});
            if (cur.moveToFirst()) {
                do {
                    call = new CustomerCall();
                    call.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
                    call.setWeek(week);
                    call.setServerDate(date);
                    call.setSlsId(sls);
                    call.setStatus(NO_VISIT);

                    call.setCallStatus("0");
                    call.setNotes(cur.getString(cur.getColumnIndex("notes")));
                    call.setCustomerName(cur.getString(cur.getColumnIndex("outlet_name")));
                    call.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
                    call.setAddress(cur.getString(cur.getColumnIndex("address")));
                    call.setGroupChannel(cur.getString(cur.getColumnIndex("group_channel")));
                    call.setChannel(cur.getString(cur.getColumnIndex("channel")));
                    call.setZone(cur.getString(cur.getColumnIndex("zone")));

                    calls.add(call);
                } while (cur.moveToNext());
            }
            cur.close();

        }
        return calls;

    }

    public static boolean isExistPause(Context ctx,String date,String sls){
        boolean ada =false;
//        Log.e("date",date);
//        Log.e("sls",sls);
        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery("SELECT * from sls_plan_status WHERE date=? and sls_id=? and status=?", new String[]{date, sls, PAUSED});
        if (cur.moveToFirst()){
//            Log.e("isExistPause", "ada");
            ada = true;
        }
        return ada;

    }

    public CustomerCall getData(){
        CustomerCall call = null;

        Cursor cur = db.rawQuery("SELECT * FROM sls_plan_status WHERE week=? AND date=? AND outlet_id=?", new String[]{week, serverDate, getCustomerNumber()});
        if (cur.moveToFirst()){
            call = new CustomerCall();
            call.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
            call.setId(cur.getString(cur.getColumnIndex("id")));
            call.setSquence(cur.getInt(cur.getColumnIndex("squence")));
            call.setNotes(cur.getString(cur.getColumnIndex("notes")));
            call.setWeek(cur.getString(cur.getColumnIndex("week")));
            call.setServerDate(cur.getString(cur.getColumnIndex("date")));
            call.setSlsId(cur.getString(cur.getColumnIndex("sls_id")));

            call.setReasonNoBarcode(cur.getString(cur.getColumnIndex("reason_no_barcode")));
            call.setReasonUnroute(cur.getString(cur.getColumnIndex("reason_unroute")));

            call.setStartTime(cur.getString(cur.getColumnIndex("start_time")));
            call.setEndTime(cur.getString(cur.getColumnIndex("end_time")));
            call.setStatus(cur.getString(cur.getColumnIndex("status")));
            call.setCallStatus(cur.getString(cur.getColumnIndex("call_status")));
        }

        return call;

    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT * FROM sls_plan_status WHERE outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(),slsid,serverDate});
        //Cursor cur = db.rawQuery("SELECT * FROM sls_plan_status WHERE id=?", new String[]{id});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public static List<OrderItem> getTemplate(SQLiteDatabase db,String id){
        List<OrderItem> prd = new ArrayList<OrderItem>();
        OrderItem item=null;
        Cursor cur = db.rawQuery(
                "SELECT b.uom uomx,a.price,b.qty_last, a.product_id,a.product_name,a.product_name_alias,a.division,a.base_uom,a.status,a.is_pareto,a.product_type,a.product_brands,a.product_category,"+
                        "a.small_uom,a.medium_uom,a.large_uom,a.large_to_small,a.medium_to_small FROM sls_sku_template b  INNER JOIN sls_product a ON a.product_id=b.product_id WHERE b.outlet_id = ?", new String[]{id});
        if(cur.moveToFirst()) {
            Log.e("ada","template");
            if (!cur.isAfterLast()) {
                do {

                    item = new OrderItem();
                    item.productId      = cur.getString(cur.getColumnIndex("product_id"));
                    item.division       =  cur.getString(cur.getColumnIndex("division"));
                    item.productName    =  cur.getString(cur.getColumnIndex("product_name"));

                    item.price          = cur.getDouble(cur.getColumnIndex("price"));

                    item.brand          = cur.getString(cur.getColumnIndex("product_brands"));

                    item.lastQty        = cur.getInt(cur.getColumnIndex("qty_last"));
                    item.lastUom        = cur.getString(cur.getColumnIndex("uomx"));

                    item.uomSmall       = cur.getString(cur.getColumnIndex("small_uom"));
                    item.uomMedium      = cur.getString(cur.getColumnIndex("medium_uom"));
                    item.uomLarge       = cur.getString(cur.getColumnIndex("large_uom"));


                    item.stockQty       = 0;
                    item.stockUom       = item.lastUom;

                    item.suggestQty     = item.lastQty;
                    item.suggestUom     = item.lastUom;

                    item.qty            = 0;
                    item.uom            = item.lastUom;

                    item.uomSmall       = cur.getString(cur.getColumnIndex("small_uom"));
                    item.uomMedium      = cur.getString(cur.getColumnIndex("medium_uom"));
                    item.uomLarge       = cur.getString(cur.getColumnIndex("large_uom"));


                    item.largeToSmall   = cur.getInt(cur.getColumnIndex("large_to_small"));
                    item.mediumToSmall  = cur.getInt(cur.getColumnIndex("medium_to_small"));
                    prd.add(item);

                } while (cur.moveToNext());
            }
        }
        cur.close();


        return prd;

    }

    public void setPause(SQLiteDatabase db,String waktu){
        ContentValues cv = new ContentValues();
        cv.put("status",PAUSED);
        cv.put("last_pause",waktu);
        db.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
    }

    public void setReasonNoBarcode(SQLiteDatabase db,String reason){
        ContentValues cv = new ContentValues();
        cv.put("reason_no_barcode",reason);
        db.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
    }

    public void resumeCall(SQLiteDatabase d,String waktu,long dur) {
        ContentValues cv = new ContentValues();
        cv.put("status", "Visit");
        cv.put("last_resume", waktu);
        cv.put("duration", dur);

        d.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
    }

    public void setNoCall(SQLiteDatabase d) {
        ContentValues cv = new ContentValues();
        cv.put("status", CustomerCall.NOCALL);
        d.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
    }

    public void startCall(SQLiteDatabase d,String waktu){
        ContentValues cv = new ContentValues();

        cv.put("duration", 0);

        cv.putNull("last_resume");
        cv.putNull("last_pause");

        cv.put("status", "Visit");
        cv.put("start_time", waktu);
        d.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});



        //d.update("sls_plan_status", cv, "id=?", new String[]{id});

        //ContentValues cv2 = new ContentValues();
        //cv2.put("start_call", start);
        //d.update( "settings",cv2,"salesman=?",new String[]{slsid});

    }

    public void updateOrderId(SQLiteDatabase d,long orderId) {
        String end = Helper.getCurrentDateTime();
        ContentValues cv = new ContentValues();
        cv.put("order_id", orderId);
        d.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
    }

    public void deleteUnRoute(SQLiteDatabase d) {
        d.delete( "sls_plan_status",  "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
    }

    public void updateReturnId(SQLiteDatabase d,long orderId) {
        String end = Helper.getCurrentDateTime();
        ContentValues cv = new ContentValues();
        cv.put("return_id", return_id);
        d.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
    }

    public void endCall(SQLiteDatabase d,long orderId,String waktu){

        ContentValues cv = new ContentValues();
        cv.put("status", "Visited");
        cv.put("end_time", waktu);
        //cv.put("order_id", -1); // hold by editable order
        //d.update("sls_plan_status", cv, "id=?", new String[]{id});
        d.update("sls_plan_status", cv, "outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(),slsid,serverDate});


        //String start="";
        //Cursor cur =  d.rawQuery("SELECT start_call FROM settings WHERE salesman = ?", new String[]{slsid});
        //if(cur.moveToFirst()){
        //    start = cur.getString(0);
        //}

        //cur.close();

        //String mulai = "";
        //Cursor c =  d.rawQuery("SELECT start_time FROM sls_plan_status WHERE outlet_id=? and sls_id=? and date=?", new String[]{getCustomerNumber(), slsid, serverDate});
        //if (c.moveToFirst()){
          //  mulai = c.getString(0);
        //}
        //c.close();


        ContentValues cv2 = new ContentValues();
        cv2.put("start_call", startTime );
        cv2.put("end_call", waktu);
        cv2.put("status", 0); /// new, always open if end call
        d.update("sls_order", cv2, "order_id=?", new String[]{String.valueOf(orderId)});
        //d.update( "sls_order",cv2,"end_call is NULL and outlet_id=?",new String[]{getCustomerNumber()});


    }

    public boolean save(){
        boolean ok = false;
        if (!isExist()){
            try {
                ContentValues cv = new ContentValues();
                cv.put("id",            id);
                cv.put("date",          serverDate);
                cv.put("week",          week);
                cv.put("sls_id",        slsid);
                cv.put("reason_unroute",        reasonUnroute);
                cv.put("outlet_id",     getCustomerNumber());
                cv.put("route",         route);
                cv.put("squence",       squence);
                cv.put("notes",         notes);
                cv.put("credit_limit",  credit_limit);

                cv.put("status", "Unvisit");
                cv.put("call_status", call_status);
                cv.put("start_time", "");
                cv.put("end_time", "");

                db.insert("sls_plan_status", null, cv);
                ok = true;
                //Log.e("vvv","insert");
            }catch(Exception ex){
                //Log.e("vvv","",ex);
                ok = false;
            }
        }else{
            try {
                ContentValues cv = new ContentValues();
                //cv.put("date",          serverDate);
                cv.put("week",          week);
                //cv.put("sls_id",        slsid);
                //cv.put("outlet_id",     getCustomerNumber());
                cv.put("route",         route);
                cv.put("squence",       squence);
                cv.put("notes",         notes);
                cv.put("call_status", call_status);
                cv.put("credit_limit",  credit_limit);

                //cv.put("status", "Unvisit");
                //cv.put("start_time", "");
                //cv.put("end_time", "");

                //db.update("sls_plan_status", cv, "id=?", new String[]{id});
                db.update("sls_plan_status", cv, "sls_id=? and outlet_id=? and date=?", new String[]{slsid,getCustomerNumber(),serverDate});
                //Log.e("vvv", "update");
                ok = true;
            }catch(Exception ex){
                //Log.e("vvv","",ex);
                ok = false;
            }
        }

            return ok;
    }

    public void setPicture(String v){
        super.setPicture(v);
        if (Helper.getNullString(v).length()>0){
            bitmap =  Helper.getDecodeImage(v);
        }
        else{
            bitmap= null;
        }

    }

    public Bitmap getImage(){
        return bitmap;
    }

}
