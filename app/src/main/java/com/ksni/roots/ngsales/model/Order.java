package com.ksni.roots.ngsales.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import com.ksni.roots.ngsales.util.UomConversion;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by #roots on 11/09/2015.
 */
public class Order {
    private long order_id;
    private String order_date;
    private String outlet_id;
    private String sls_id;
    private String delivery_date;
    private String notes;
    private String top;
    private String reason;
    private String reasonNoBarcode;
    private String reasonNoRoute;
    private String latitude;
    private String longitude;
    private String imei;
    private String ppn;
    private String week;
    private String sub_total;
    private String total_discount;
    private int grandTotal;
    private int orderType;
    private int delivered;
    private int salesmanType;
    private int typeSalesman;
    private List<OrderItem> items;
    private SQLiteDatabase db;
    public static final int SALES_TAKING_ORDER = 0;
    public static final int SALES_CANVAS = 1;

    public static final int REGULAR_ORDER = 0;
    public static final int RETURN_ORDER = 1;

    public List<OrderItem> getItems(){
        return items;
    }

    public Order(SQLiteDatabase db){
        this.db = db;
        items = new ArrayList<OrderItem>();
    }



    public void setTop(String value ){
        top = value;
    }
    public String getTop( ){
        return top;
    }
    public void setTypeSalesman(int value ){
        typeSalesman = value;
    }
    public int getTypeSalesman( ){
        return typeSalesman;
    }
    public void setWeek(String value ){
        week = value;
    }
    public void setDelivered(int value ){
        delivered = value;
    }
    public int getDelivered( ){
        return delivered;
    }
    public void setDeliveryDate(String value ){
        delivery_date = value;
    }
    public String getDeliveryDate( ){
        return delivery_date;
    }
    public int getGrandTotal() {
        return grandTotal;
    }
    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }
    public void setOrderType(int value ){
        orderType = value;
    }
    public int getOrderType( ){
        return orderType;
    }
    public void setSalesmanType(int value ){
        salesmanType = value;
    }
    public int getSalesmanType( ){
        return salesmanType;
    }
    public void setReasonNoBarcode(String value ){
        reasonNoBarcode= value;
    }
    public void setReasonNoRoute(String value ){
        reasonNoRoute= value;
    }
    public void setReason(String value ){
        reason = value;
    }
    public void setOrderDate(String value ){
        order_date = value;
    }
    public void setCustomer(String value ){
        outlet_id  = value;
    }
    public void setIMEI(String value ){
        imei  = value;
    }
    public void setLatitude(String value ){
        latitude  = value;
    }
    public void setLongitude(String value ){
        longitude  = value;
    }
    public void setSales(String value ){
        sls_id  = value;
    }
    public void setOrderId(long value ){
        order_id  = value;
    }
    public void setNotes(String value ){
        notes  = value;
    }
    public void addItem(OrderItem itm){
        items.add(itm);
    }


    public static void setSuccess(SQLiteDatabase db, String result){
        db.execSQL("UPDATE sls_order SET status = 1 WHERE status = 0 AND order_id=?",new String[]{result});
    }
    public static void reOpenOrder(SQLiteDatabase db, String result){
        db.execSQL("UPDATE sls_order SET status = 0 WHERE status = 1 AND order_id=?",new String[]{result});
    }


    private double getTotal(){
        double buffTotal = 0;
        for(OrderItem itm:items){
            buffTotal = buffTotal +itm.getTotal();
        }
        return buffTotal;
    }

    public boolean delete(){
        try {
            db.delete("sls_order", "order_id=?", new String[]{String.valueOf(order_id)});
            db.delete("sls_order_item", "order_id=?", new String[]{String.valueOf(order_id)});
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

    public boolean update(){
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("outlet_id", outlet_id);
            cv.put("notes", notes);
            cv.put("date_create", Helper.getCurrentDateTime());
            cv.put("sls_id", sls_id);
            //cv.put("reason_nobarcode", reasonNoBarcode);
            cv.put("salesman_type", salesmanType);
            cv.put("order_type", orderType);
            cv.put("outlet_top_id", top);
            cv.put("week", week);
            cv.put("imei", imei);
            cv.put("latitude", latitude);
            cv.put("longitude", longitude);
            cv.put("reason", reason);
            cv.put("delivered", delivered);
            cv.put("delivery_date", delivery_date);
            //cv.put("order_date", order_date); // jangan di update, karena untk status cancel = 9

            double subTotal = 0,totalDiscount = 0,gross=0,ppn = 0;

            for (OrderItem itm : items) {
                gross += itm.getSubTotal();
                subTotal += itm.getTotal();
                totalDiscount += itm.getTotalDiscount();
            }

            cv.put("sub_total", gross);
            cv.put("total_discount", totalDiscount);
            //double ppn = 0.1 * gross;
            cv.put("ppn", ppn);
            cv.put("grand_total", subTotal + ppn);


            db.update("sls_order", cv, "order_id=?", new String[]{String.valueOf(order_id)});

            db.delete("sls_order_item", "order_id=?", new String[]{String.valueOf(order_id)});

            int cnt = 0;
            for (OrderItem itm : items) {
                if(itm.qty>0 || itm.stockQty>0) {
                    ContentValues cvi = new ContentValues();
                    cvi.put("order_id", order_id);
                    cnt++;
                    cvi.put("item", itm.id);
                    cvi.put("product_id", itm.productId);
                    cvi.put("description", itm.productName);
                    cvi.put("qty", itm.qty);
                    cvi.put("order_type", orderType);
                    cvi.put("brand", itm.brand);
                    cvi.put("reason_return", itm.reasonReturId);
                    cvi.put("uom", itm.uom);
                    cvi.put("price", itm.price);
                    cvi.put("item_type", itm.itemType);
                    cvi.put("ref_item", itm.refItem);

                    if(itm.uom.equals(itm.uomSmall))
                        cvi.put("qty_pcs", itm.qty);
                    else if(itm.uom.equals(itm.uomLarge))
                        cvi.put("qty_pcs", itm.qty*itm.largeToSmall);
                    else if(itm.uom.equals(itm.uomMedium))
                        cvi.put("qty_pcs", itm.qty*itm.mediumToSmall);
                    else
                        cvi.put("qty_pcs", itm.qty);

                    cvi.put("is_ipt", itm.isIPT);
                    cvi.put("is_ipt_percent", itm.isPercentIPT);


                    cvi.put("suggest_qty", itm.suggestQty);
                    cvi.put("suggest_uom", itm.suggestUom);

                    cvi.put("last_stock", itm.lastQty);
                    cvi.put("last_stock_uom", itm.lastUom);

                    cvi.put("current_stock", itm.stockQty);
                    cvi.put("current_stock_uom", itm.stockUom);

                    cvi.put("large_to_small", itm.largeToSmall);
                    cvi.put("medium_to_small", itm.mediumToSmall);

                    cvi.put("small_uom", itm.uomSmall);
                    cvi.put("medium_uom", itm.uomMedium);
                    cvi.put("large_uom", itm.uomLarge);

                    cvi.put("regular_discount", itm.regularDiscount);
                    cvi.put("extra_discount", itm.extraDiscount);
                    cvi.put("special_discount", itm.specialDiscount);

                    cvi.put("total_gross", itm.getSubTotal());
                    cvi.put("total_net", itm.getTotal());

                    db.insert("sls_order_item", null, cvi);
                }
            }

            db.setTransactionSuccessful();
            return true;
        }
        catch(Exception ex){
            Log.e("sss",ex.toString());
            return false;
        }
        finally {
            db.endTransaction();
        }

    }

    public long save(){
        long lastId = -1;
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("outlet_id", outlet_id);
            cv.put("salesman_type", salesmanType);
            cv.put("delivery_date", delivery_date);
            cv.put("order_type", orderType);
            cv.put("delivered", delivered);
            cv.put("outlet_top_id", top);
            cv.put("notes", notes);
            cv.put("sls_id", sls_id);
            cv.put("date_create", Helper.getCurrentDateTime());
            cv.put("week", week);
            cv.put("latitude", latitude);
            cv.put("longitude", longitude);
            cv.put("reason", reason);
            cv.put("reason_nobarcode", reasonNoBarcode);
            cv.put("reason_unroute", reasonNoRoute);
            cv.put("imei", imei);
            cv.put("order_date", order_date);

            double subTotal = 0,totalDiscount = 0,gross=0,ppn = 0;

            for (OrderItem itm : items) {
                gross += itm.getSubTotal();
                subTotal += itm.getTotal();
                totalDiscount += itm.getTotalDiscount();
            }

            cv.put("sub_total", gross);
            cv.put("total_discount", totalDiscount);
            //double ppn = 0.1 * gross;
            cv.put("ppn", ppn);
            cv.put("grand_total", subTotal + ppn);

            lastId =  db.insert( "sls_order",null,cv);


            int cnt = 0;
            for (OrderItem itm : items) {
                if(itm.qty>0 || itm.stockQty>0) {
                    ContentValues cvi = new ContentValues();
                    cvi.put("order_id", lastId);
                    cnt++;
                    cvi.put("item", itm.id);
                    cvi.put("order_type", orderType);
                    cvi.put("product_id", itm.productId);
                    cvi.put("description", itm.productName);
                    cvi.put("qty", itm.qty);
                    cvi.put("uom", itm.uom);
                    cvi.put("brand", itm.brand);
                    cvi.put("price", itm.price);
                    cvi.put("item_type", itm.itemType);
                    cvi.put("ref_item", itm.refItem);
                    cvi.put("reason_return", itm.reasonReturId);
                    cvi.put("small_uom", itm.uomSmall);
                    cvi.put("medium_uom", itm.uomMedium);
                    cvi.put("large_uom", itm.uomLarge);
                    cvi.put("last_stock", itm.lastQty);
                    cvi.put("last_stock_uom", itm.lastUom);
                    cvi.put("current_stock", itm.stockQty);
                    cvi.put("current_stock_uom", itm.stockUom);
                    cvi.put("suggest_qty", itm.suggestQty);
                    cvi.put("suggest_uom", itm.suggestUom);
                    cvi.put("large_to_small", itm.largeToSmall);
                    cvi.put("medium_to_small", itm.mediumToSmall);
                    cvi.put("regular_discount", itm.regularDiscount);
                    cvi.put("extra_discount", itm.extraDiscount);
                    cvi.put("special_discount", itm.specialDiscount);


                    if(itm.uom.equals(itm.uomSmall)) {
                        cvi.put("qty_pcs", itm.qty);
                    } else if(itm.uom.equals(itm.uomLarge)) {
                        cvi.put("qty_pcs", itm.qty*itm.largeToSmall);
                    } else if(itm.uom.equals(itm.uomMedium)) {
                        cvi.put("qty_pcs", itm.qty*itm.mediumToSmall);
                    } else {
                        cvi.put("qty_pcs", itm.qty);
                    }

                    cvi.put("is_ipt", itm.isIPT);
                    cvi.put("is_ipt_percent", itm.isPercentIPT);

                    cvi.put("total_gross", itm.getSubTotal());
                    cvi.put("total_net", itm.getTotal());

                    db.insert("sls_order_item", null, cvi);
                }
            }
            //Log.e("XXX", "OK");
            db.setTransactionSuccessful();

            return lastId;
        }
            catch(Exception ex){
              //  Log.e("XXX",ex.toString());
                return -1;
            }
        finally {
            db.endTransaction();
        }

    }

    public static List<OrderHead> getPendingHeadOrder(Context ctx){
        List<OrderHead> buff = new ArrayList<OrderHead>();
        String sql = "SELECT o.order_id,o.order_date,o.outlet_id,c.outlet_name,o.grand_total FROM sls_order o INNER JOIN sls_customer c " +
                "ON o.outlet_id = c.outlet_id WHERE o.status = 0";

        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery(sql,null);
        if (cur.moveToFirst()){
            do{
                OrderHead head = new OrderHead();
                head.order_id = cur.getString(cur.getColumnIndex("order_id"));
                head.order_date = cur.getString(cur.getColumnIndex("order_date"));
                head.outlet_id = cur.getString(cur.getColumnIndex("outlet_id"));
                head.outlet_name = cur.getString(cur.getColumnIndex("outlet_name"));
                head.grand_total = cur.getDouble(cur.getColumnIndex("grand_total"));

                buff.add(head);
            }while(cur.moveToNext());
        }
        cur.close();

        return buff;

    }

    public static List<OrderHead> getPendingHeadOrder(Context ctx,String outlet){
        List<OrderHead> buff = new ArrayList<OrderHead>();
        String sql = "";
        sql = "SELECT o.order_id,o.order_date,o.outlet_id,c.outlet_name,o.grand_total FROM sls_order o INNER JOIN sls_customer c " +
                "ON o.outlet_id = c.outlet_id WHERE o.outlet_id=?"; // WHERE o.status = 0

        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = dm.database().rawQuery(sql, new String[]{outlet});
        if (cur.moveToFirst()){
            do{
                OrderHead head = new OrderHead();
                head.order_id = cur.getString(cur.getColumnIndex("order_id"));
                head.order_date = cur.getString(cur.getColumnIndex("order_date"));
                head.outlet_id = cur.getString(cur.getColumnIndex("outlet_id"));
                head.outlet_name = cur.getString(cur.getColumnIndex("outlet_name"));
                head.grand_total = cur.getDouble(cur.getColumnIndex("grand_total"));

                buff.add(head);
            }while(cur.moveToNext());
        }

        cur.close();
        return buff;

    }

    public static long getLastId(SQLiteDatabase db,String cust){
        long no = -1;
        String sql = "SELECT order_id FROM sls_order WHERE status = 0 and outlet_id=? ORDER BY order_id DESC";
        Cursor cur = db.rawQuery(sql, new String[]{cust});
        if (cur.moveToFirst()){
            no = cur.getLong(0);
        }
        cur.close();
        return no;
    }

    public static long getLastIdAll(SQLiteDatabase db,String cust){
        long no = -1;
        String sql = "SELECT order_id FROM sls_order WHERE outlet_id=?";
        Cursor cur = db.rawQuery(sql, new String[]{cust});
        if (cur.moveToFirst()){
            no = cur.getLong(0);
        }
        cur.close();
        return no;
    }

    public static void unlockAllOrder(Context ctx){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        cv.put("locked",0);
        dm.database().update("sls_order", cv, null, null);
    }

    public static void updateSignature(Context ctx,long id,String sign){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        //cv.put("locked",0);
        cv.put("signature",sign);
        dm.database().update("sls_order", cv, "order_id=?", new String[]{String.valueOf(id)});
    }

    public static void updateDuration(Context ctx,long id,long valueDurasi){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        //cv.put("locked",0);
        cv.put("duration",Math.round(valueDurasi/60));
        dm.database().update("sls_order", cv, "order_id=?", new String[]{String.valueOf(id)});
    }

    public static void updateSignatureAndPicOutlet(Context ctx,long id,String sign,String picOut){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        //cv.put("locked",0);
        cv.put("signature",sign);
        cv.put("pic_outlet",picOut);
        dm.database().update("sls_order", cv, "order_id=?", new String[]{String.valueOf(id)});
    }

    public static void updatePicOutlet(Context ctx,long id,String picOut){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        cv.put("pic_outlet",picOut);
        dm.database().update("sls_order", cv, "order_id=?", new String[]{String.valueOf(id)});
    }

    public static boolean getPicOutletByOrder(Context ctx,long id){
        DBManager dm = DBManager.getInstance(ctx);
        boolean ada = false;
        Cursor cur = dm.database().rawQuery("SELECT pic_outlet FROM sls_order WHERE order_id=?", new String[]{String.valueOf(id)});
        if (cur.moveToFirst()){
            String pic = Helper.getNullString(cur.getString(0));
              if (pic.length()>0) ada =true;
        }
        cur.close();
        return ada;
    }

    public static void lockUnlockOrder(Context ctx,long id,int lockedValue){
        DBManager dm = DBManager.getInstance(ctx);
        ContentValues cv = new ContentValues();
        cv.put("locked",lockedValue);
        dm.database().update("sls_order", cv, "order_id=?", new String[]{String.valueOf(id)});
    }

    public static Order getData(SQLiteDatabase db,long no,String cust,boolean pending){
        Order ord = null;
        String sql = "";
        if (pending) {
            sql =
                    "SELECT " +
                            "sls_order.delivered, " +
                            "sls_order.order_id, " +
                            "sls_order.order_date, " +
                            "sls_order.delivery_date, " +
                            "sls_order.outlet_id, " +
                            "sls_order.imei, " +
                            "sls_order.sls_id, " +
                            "sls_order.reason, " +
                            "sls_order.order_type, " +
                            "sls_order.salesman_type, " +
                            "sls_order.notes, " +
                            "sls_order_item.small_uom, " +
                            "sls_order_item.medium_uom, " +
                            "sls_order_item.large_uom, " +
                            "sls_order.ppn, " +
                            "sls_order.outlet_top_id, " +
                            "sls_order.sub_total, " +
                            "sls_order.total_discount, " +
                            "sls_order.grand_total, " +
                            "sls_order.date_create, " +
                            "sls_order_item.product_id, " +
                            "sls_order_item.item, " +
                            "sls_order_item.brand, " +
                            "sls_order_item.ref_item, " +
                            "sls_order_item.description, " +
                            "sls_order_item.qty, " +
                            "sls_order_item.current_stock, " +
                            "sls_order_item.current_stock_uom, " +
                            "sls_order_item.suggest_qty, " +
                            "sls_order_item.suggest_uom, " +
                            "sls_order_item.uom, " +
                            "sls_order_item.price, " +
                            "sls_order_item.total_gross, " +
                            "sls_order_item.regular_discount, " +
                            "sls_order_item.extra_discount, " +
                            "sls_order_item.is_ipt_percent, " +
                            "sls_order_item.is_ipt, " +
                            "sls_order_item.special_discount, " +
                            "sls_order_item.reason_return, " +
                            "sls_order_item.total_net, " +
                            "sls_order_item.item_type, " +
                            "sls_order_item.last_stock, " +
                            "sls_order_item.last_stock_uom, " +
                            "sls_order_item.large_to_small, " +
                            "sls_order_item.medium_to_small " +
                            "FROM " +
                            "sls_order " +
                            "left Join sls_order_item ON sls_order.order_id = sls_order_item.order_id WHERE sls_order.order_id=? and sls_order.outlet_id=? and sls_order.status=0 and locked=0 ORDER BY sls_order_item.item";
        }
        else{
            sql =
                    "SELECT " +
                            "sls_order.delivered, " +
                            "sls_order.order_id, " +
                            "sls_order.order_date, " +
                            "sls_order.delivery_date, " +
                            "sls_order.outlet_id, " +
                            "sls_order.imei, " +
                            "sls_order.sls_id, " +
                            "sls_order.reason, " +
                            "sls_order.order_type, " +
                            "sls_order.salesman_type, " +
                            "sls_order.notes, " +
                            "sls_order.outlet_top_id, " +
                            "sls_order_item.small_uom, " +
                            "sls_order_item.medium_uom, " +
                            "sls_order_item.large_uom, " +
                            "sls_order_item.reason_return, " +
                            "sls_order.ppn, " +
                            "sls_order.sub_total, " +
                            "sls_order.total_discount, " +
                            "sls_order.grand_total, " +
                            "sls_order.date_create, " +
                            "sls_order_item.product_id, " +
                            "sls_order_item.item, " +
                            "sls_order_item.brand, " +
                            "sls_order_item.ref_item, " +
                            "sls_order_item.description, " +
                            "sls_order_item.qty, " +
                            "sls_order_item.current_stock, " +
                            "sls_order_item.current_stock_uom, " +
                            "sls_order_item.suggest_qty, " +
                            "sls_order_item.suggest_uom, " +
                            "sls_order_item.uom, " +
                            "sls_order_item.price, " +
                            "sls_order_item.is_ipt_percent, " +
                            "sls_order_item.is_ipt, " +
                            "sls_order_item.total_gross, " +
                            "sls_order_item.regular_discount, " +
                            "sls_order_item.extra_discount, " +
                            "sls_order_item.special_discount, " +
                            "sls_order_item.total_net, " +
                            "sls_order_item.item_type, " +
                            "sls_order_item.last_stock, " +
                            "sls_order_item.last_stock_uom, " +
                            "sls_order_item.large_to_small, " +
                            "sls_order_item.medium_to_small " +
                            "FROM " +
                            "sls_order " +
                            "left Join sls_order_item ON sls_order.order_id = sls_order_item.order_id WHERE sls_order.order_id=? and sls_order.outlet_id=? ORDER BY sls_order_item.item";
        }


        try {
            ord = new Order(null);
            Cursor cur = db.rawQuery(sql, new String[]{String.valueOf(no),cust});

            if (cur.moveToFirst()) {
                //Log.e("description",cur.getString(cur.getColumnIndex("description")) );
                ord.order_id = cur.getLong(cur.getColumnIndex("order_id")) ;
                ord.order_date = cur.getString(cur.getColumnIndex("order_date")) ;
                ord.outlet_id = cur.getString(cur.getColumnIndex("outlet_id")) ;
                ord.sls_id = cur.getString(cur.getColumnIndex("sls_id")) ;
                ord.notes = cur.getString(cur.getColumnIndex("notes")) ;
                ord.reason = cur.getString(cur.getColumnIndex("reason")) ;
                ord.delivered = cur.getInt(cur.getColumnIndex("delivered")) ;
                ord.delivery_date = cur.getString(cur.getColumnIndex("delivery_date")) ;

                ord.top = cur.getString(cur.getColumnIndex("outlet_top_id")) ;

                ord.salesmanType = cur.getInt(cur.getColumnIndex("salesman_type")) ;
                ord.orderType = cur.getInt(cur.getColumnIndex("order_type")) ;

                do{
                    OrderItem itm = new OrderItem();
                    itm.productId = cur.getString(cur.getColumnIndex("product_id")) ;
                    if (itm.productId!=null) {

                        if(ord.orderType==RETURN_ORDER) {
                            itm.regularOrder = false;
                            itm.reasonReturId = cur.getString(cur.getColumnIndex("reason_return"));
                            ReasonRetur retRes = ReasonRetur.getData(db, itm.reasonReturId);
                            if (retRes!=null){
                                itm.reasonReturName = retRes.getDescription();
                            }
                        }else{
                            itm.regularOrder = true;
                        }


                        itm.productName = cur.getString(cur.getColumnIndex("description"));
                        itm.price = cur.getDouble(cur.getColumnIndex("price"));
                        itm.uom = cur.getString(cur.getColumnIndex("uom"));
                        itm.brand = cur.getString(cur.getColumnIndex("brand"));
                        itm.qty = cur.getInt(cur.getColumnIndex("qty"));
                        itm.regularDiscount = cur.getDouble(cur.getColumnIndex("regular_discount"));
                        itm.extraDiscount = cur.getDouble(cur.getColumnIndex("extra_discount"));
                        itm.specialDiscount = cur.getDouble(cur.getColumnIndex("special_discount"));
                        itm.mediumToSmall = cur.getInt(cur.getColumnIndex("medium_to_small"));
                        itm.largeToSmall= cur.getInt(cur.getColumnIndex("large_to_small"));
                        itm.lastQty = cur.getInt(cur.getColumnIndex("last_stock"));
                        itm.itemType = cur.getString(cur.getColumnIndex("item_type"));
                        itm.lastUom = cur.getString(cur.getColumnIndex("last_stock_uom"));

                        itm.isIPT = cur.getString(cur.getColumnIndex("is_ipt"));
                        itm.isPercentIPT = cur.getString(cur.getColumnIndex("is_ipt_percent"));

                        itm.stockQty = cur.getInt(cur.getColumnIndex("current_stock"));
                        itm.stockUom = cur.getString(cur.getColumnIndex("current_stock_uom"));

                        itm.suggestQty = cur.getInt(cur.getColumnIndex("suggest_qty"));
                        itm.suggestUom = cur.getString(cur.getColumnIndex("suggest_uom"));


                        Product prd = Product.getProductData(db,itm.productId);
                        if (prd!=null){
                            itm.division = prd.getDivision();
                        }

                        itm.uomSmall = cur.getString(cur.getColumnIndex("small_uom"));
                        itm.uomMedium = cur.getString(cur.getColumnIndex("medium_uom"));
                        itm.uomLarge = cur.getString(cur.getColumnIndex("large_uom"));


                        itm.id = cur.getInt(cur.getColumnIndex("item"));
                        itm.refItem = cur.getInt(cur.getColumnIndex("ref_item"));


                        ord.addItem(itm);
                    }

                }while (cur.moveToNext());

            }

            cur.close();
            return ord;

        }catch (Exception ex) {
            return null;
        }

    }

    public static boolean docking(SQLiteDatabase db, String depo,String tgl,String sls){
        /*
        int h_NoReferensi      = 8;
        int h_Kode_Salesman    = 6;
        int h_No_Outlet        = 6;
        int h_TglValidasi      = 10;
        int h_No_PO            = 8;
        int h_NO_Mobil         = 12;
        int h_Tipe_Pembayaran  = 1;
        int h_Discount         = 6;
        int h_TOP              = 4;

        int d_NoReferensi      = 8;
        int d_Kode_Produk      = 6;
        int d_Jml_Qty1         = 5;
        int d_Jml_Qty2         = 3;
        int d_Jml_Qty3         = 3;
        */

        int bykH = 0;
        int bykD = 0;
        String firstHeader = "";
        String line_header = "HRLORD\n";
        String line_detaiil = "DRLORD\n";

        String sql = "SELECT h.order_id, " +
                            "h.sls_id, " +
                            "h.outlet_id, " +
                            "h.order_date, " +
                            "h.outlet_top_id, " +
                            "d.product_id, " +
                            "d.medium_to_small, " +
                            "d.large_to_small, " +
                            "d.qty_pcs " +
                    "FROM sls_order h inner join sls_order_item d ON h.order_id = d.order_id " +
                    "WHERE h.docking = 0 AND h.sls_id = ? AND DATE(h.order_date)=? AND d.item_type = 'N' ORDER BY h.order_id,d.item";

        Cursor cur= db.rawQuery(sql,new String[]{sls,tgl});
        //Cursor cur= db.rawQuery(sql,null);
        if(cur.moveToFirst()){
            do{
                bykD++;
                String lastOrderNo = cur.getString(cur.getColumnIndex("order_id"));
                String HNoOrder = Helper.strRepeat(8, lastOrderNo, '0');

                String Dproduk      = cur.getString(cur.getColumnIndex("product_id"));
                int  largeToSmall   = cur.getInt(cur.getColumnIndex("large_to_small"));
                int  mediumToSmall  = cur.getInt(cur.getColumnIndex("medium_to_small"));
                long  qtyPcs        = cur.getLong(cur.getColumnIndex("qty_pcs"));

                UomConversion conv = new UomConversion(qtyPcs,largeToSmall,mediumToSmall);
                conv.fromSmall();
                String crt = Helper.strRepeat(5,String.valueOf(conv.getLarge()).trim(),'0');
                String box = Helper.strRepeat(3, String.valueOf(conv.getMedium()).trim(), '0');
                String pcs = Helper.strRepeat(3,String.valueOf(conv.getSmall()).trim(),'0');

                if(!firstHeader.equals(lastOrderNo)){
                    bykH++;
                    firstHeader = lastOrderNo;

                    // update docking flag
                    ContentValues cv = new ContentValues();
                    cv.put("docking",1);
                    db.update("sls_order",cv,"order_id=?",new String[]{lastOrderNo});


                    String HSalesman = cur.getString(cur.getColumnIndex("sls_id")).trim();
                    String HOutlet = cur.getString(cur.getColumnIndex("outlet_id")).trim();

                    String Htgl = Helper.getDDMMYYYYFromMysqlFormat(cur.getString(cur.getColumnIndex("order_date")).trim(), "-");

                    String top = cur.getString(cur.getColumnIndex("outlet_top_id")).trim();
                    String HCaraBayar = "";
                    String HTop = top.trim();
                    if(top.equals("0"))
                        HCaraBayar = "T";
                    else
                        HCaraBayar = "K";

                    HTop = Helper.strRepeat(4,top,'0');

                    String Hnopo = Helper.strRepeat(8, "", ' ');
                    String Hnomobil = Helper.strRepeat(12, "", ' ');
                    String Hdisc = Helper.strRepeat(6, "", '0');

                    line_header+=HNoOrder+
                                 HSalesman+
                                 HOutlet+
                                 Htgl+
                                 Hnopo+
                                 Hnomobil+
                                 HCaraBayar+
                                 Hdisc+
                                 HTop+"\n";




                    line_detaiil+=  HNoOrder+
                                    Dproduk+
                                    crt+
                                    box+
                                    pcs+"\n";

                }else{
                    line_detaiil+=  HNoOrder+
                            Dproduk+
                            crt+
                            box+
                            pcs+"\n";
                }


            }while(cur.moveToNext());
        }

        line_header+="//END"+Helper.strRepeat(4,String.valueOf(bykH+2).trim(),'0');
        line_detaiil+="//END"+Helper.strRepeat(4,String.valueOf(bykD+2).trim(),'0');

        try {
            String nama = "TR_HEADER_"+Helper.getCurrentDateTime("ddMMyyyy_HHmm");
            FileOutputStream outStream = new FileOutputStream(Helper.getExternalPath() + "/" + nama+"_"+depo+".txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));
            bw.write(line_header);
            bw.close();
            outStream.close();
        } catch (IOException e) {}
          catch (Exception e){}


        try {
            String nama = "TR_DETAIL_"+Helper.getCurrentDateTime("dd_MM_yyyy_HH_mm");
            FileOutputStream outStream = new FileOutputStream(Helper.getExternalPath() + "/" + nama+"_"+depo+".txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));
            bw.write(line_detaiil);
            bw.close();
            outStream.close();
        } catch (IOException e) {}
        catch (Exception e){}





        return true;
    }



    public static boolean isOrder(Context ctx, String idCust) {
        Order ord = null;
        boolean isOrder = false;

        DBManager dm = DBManager.getInstance(ctx);
        SQLiteDatabase db = dm.database();

        Cursor cursor = null;
        cursor = db.rawQuery("SELECT grand_total from sls_order WHERE outlet_id = '" + idCust + "'", null);

        if (cursor.moveToFirst()) {
            ord = new Order(db);
            ord.grandTotal = cursor.getInt(cursor.getColumnIndex("grand_total"));

            int total = ord.grandTotal;

            if (total > 0) {
                isOrder = true;
            } else {
                isOrder = false;
            }
        }

        cursor.close();
        return isOrder;
    };

}
