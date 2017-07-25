package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 07/08/2015.
 */
public class Customer implements Serializable{
    public class STATUS_CUSTOMER{
        public static final String INACTIVE = "0";
        public static final String ACTIVE = "1";
    }
    private String zone;

    private String customerNumber;
    private String customerNumberNew;
    private String customerName;
    private String address;
    private String barcode_number;
    private String city;
    private String title;
    private int delivery_day = 0;
    private String status;
    private String picture;
    private String contact;
    private String alias;
    private String phone;
//    private String multi_dist;
    protected String notes = "";
    private String customer_group;
    private String outlet_top_id;
    private String group_channel;
    private String channel;
    private double latitude;
    private double longitude;
    protected double credit_limit;
    private double balance;
    private String outletpricing_group;

    private String region;
    private String classification;
    private String territory;
    private String district;

    private ArrayList<Product> template;
    private SQLiteDatabase db;

    public Customer(){

        template = new ArrayList<Product>();
        customerNumberNew="";

    }


    public Customer(SQLiteDatabase db){
        this.db = db;
    }

    public void addTemplate(Product sku){
        template.add(sku);
    }

    public void setBarcodeNumber(String value) {
        barcode_number=value;
    }

    public String getBarcodeNumber() {return barcode_number;}

    public String getGroupChannel() {
        return group_channel;
    }

    public void setGroupChannel(String value) {
        group_channel=value;
    }

    public void setTop(String value) {
        outlet_top_id=value;
    }

    public String getTop() {
        return outlet_top_id;
    }

    public void setPriceGroup(String value) {
        outletpricing_group=value;
    }

    public String getPriceGroup() {
        return outletpricing_group;
    }

  //  public void setMultiDist(String value) {
     //   multi_dist=value;
//    }
   // public String getMultiDist() {
       // return multi_dist;
  //  }


    public void setStatus(String value) {
        status=value;
    }
    public String getStatus() {
        return status;
    }


    public void setCustomerNumberNew(String value) {
        customerNumberNew=value;
    }

    public void setCustomerNumber(String value) {
        customerNumber=value;
    }

    public void setCustomerGroup(String value) {
        customer_group=value;
    }

    public void setZone(String value) {
        zone=value;
    }

    public void setAlias(String value) {
        alias=value;
    }

    public String getAlias(){
        return alias;
    }

    public void setPicture(String value) {
        picture=value;
    }

    public String getPicture() {
        return picture;
    }

    public void setTitle(String value) {
        title=value;
    }

    public String getTitle(){
        return title;
    }

    public void setLatitude(double value) {
        latitude=value;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double value) {
        longitude=value;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setBalance(double value) {
        balance=value;
    }

    public double getBalance(){
        return balance;
    }

    public void setCreditLimit(double value) {
        credit_limit=value;
    }

    public double getCreditLimit(){
        return credit_limit;
    }

    public void setRegion(String value) {
        region=value;
    }

    public String getRegion(){
        return region;
    }

    public void setClassification(String value) {
        classification=value;
    }

    public String getClassification(){
        return classification;
    }

    public void setTerritory(String value) {
        territory=value;
    }

    public String getTerritory(){
        return territory;
    }

    public void setDistrict(String value) {
        district=value;
    }

    public String getDistrict(){
        return district;
    }

    public void setChannel(String value) {
        channel=value;
    }

    public void setNotes(String value) {
        notes=value;
    }

    public String getNotes() {
        return notes;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getCustomerNumberNew() {
        return customerNumberNew;
    }

    public void setCustomerName(String value) {
        customerName=value;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setDeliveryDay(int value) {
        delivery_day=value;
    }

    public int getDeliveryDay() {
        return delivery_day;
    }

    public void setAddress(String value) {
        address=value;
    }

    public String getAddress() {
        return address;
    }

    public void setCity(String value) {
        city=value;
    }

    public String getCity() {
        return city;
    }

    public void setContact(String value) {
        contact=value;
    }

    public String getContact() {
        return contact;
    }

    public void setPhone(String value) {
        phone=value;
    }

    public String getPhone() {
        return phone;
    }

    public String getChannel() {
        return channel;
    }

    public String getZone() {
        return zone;
    }

    public String getCustomerGruop() {
        return customer_group;
    }

    public void delete(){
        db.delete("sls_customer","outlet_id=?",new String[]{customerNumber});
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT outlet_id FROM sls_customer WHERE outlet_id=?", new String[]{customerNumber});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }


    public static boolean updatePictureProfile(Context ctx,String ido,String pic){
        try {
            DBManager dm = DBManager.getInstance(ctx);
            ContentValues cv = new ContentValues();
            cv.put("picture", pic);
            dm.database().update("sls_customer", cv, "outlet_id=?", new String[]{ido});
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    public static boolean isNewCustomer(SQLiteDatabase db,String cust){
        boolean isn = false;
        Cursor cur = db.rawQuery("SELECT outlet_id FROM sls_customer WHERE status = 2 AND outlet_id=?",new String[]{cust});
        if (cur.moveToFirst()){
            isn = true;
        }
        cur.close();
        return  isn;
    }


    public static String getCustomerByBarcode(SQLiteDatabase db,String barcode){
        String isn = "";
        Cursor cur = db.rawQuery("SELECT outlet_id FROM sls_customer WHERE  barcode_number=?",new String[]{barcode});
        if (cur.moveToFirst()){
            //Log.e("ada bar","");
            isn = cur.getString(0);
        }
        cur.close();
        return  isn;
    }
    public static String updateGPS(SQLiteDatabase db, String id,double latitude,double longitude){
        boolean upd = false;
        String updatetocust = "0";
        if (latitude!=0){
            Cursor cur = db.rawQuery("SELECT latitude FROM sls_customer WHERE outlet_id=?",new String[]{id});
            if (cur.moveToFirst()){
                double lat = cur.getDouble(0);
                if (lat==0) upd = true;

            }
            cur.close();

            if (upd) {
                ContentValues cv = new ContentValues();
                cv.put("latitude", latitude);
                cv.put("longitude", longitude);
                db.update("sls_customer", cv, "outlet_id=?", new String[]{id});
                updatetocust = "1";
            }
        }else
            updatetocust = "0";

        return updatetocust;
    }

    public boolean save(){
        boolean ok = false;
        if (!isExist()){
            try {
                ContentValues cv = new ContentValues();
                cv.put("outlet_id", customerNumber);
                cv.put("outlet_name", customerName);
                cv.put("outlet_name_alias", alias);
                cv.put("title", title);
                cv.put("barcode_number", barcode_number);
                cv.put("phone", phone);
                cv.put("address", address);
                cv.put("delivery_day", delivery_day);
                cv.put("city", city);
                cv.put("outlet_top_id", outlet_top_id);
                cv.put("group_channel", group_channel);
                cv.put("channel", channel);
                cv.put("outlet_group", customer_group);
                cv.put("zone", zone);
                cv.put("credit_limit", credit_limit);
                cv.put("contact_person", contact);
                cv.put("balance", balance);
                cv.put("notes", notes);
                cv.put("status", status);
                cv.put("latitude", latitude);
                cv.put("outletpricing_group", outletpricing_group);

                cv.put("longitude", longitude);
                cv.put("region", region);
                cv.put("classification", classification);
                cv.put("territory", territory);
                cv.put("district", district);

                db.insert("sls_customer", null, cv);
                ok = true;
            }catch(Exception ex){
                Log.e("vvv", "", ex);
                ok = false;
            }
        }
        else{
            try {
                ContentValues cv = new ContentValues();
                cv.put("outlet_name", customerName);
                cv.put("outlet_name_alias", alias);
                cv.put("title", title);
                cv.put("barcode_number", barcode_number);
                cv.put("delivery_day", delivery_day);
                cv.put("outlet_top_id", outlet_top_id);
                cv.put("contact_person", contact);
                cv.put("address", address);
                cv.put("city", city);
                cv.put("group_channel", group_channel);
                cv.put("channel", channel);
                cv.put("outletpricing_group", outletpricing_group);
                cv.put("outlet_group", customer_group);
                cv.put("zone", zone);
                cv.put("phone", phone);
                cv.put("credit_limit", credit_limit);
                cv.put("balance", balance);
                cv.put("notes", notes);
                cv.put("status", status);
                cv.put("latitude", latitude);
                cv.put("longitude", longitude);
                cv.put("region", region);
                cv.put("classification", classification);
                cv.put("territory", territory);
                cv.put("district", district);

                if (customerNumberNew!=""){
                    cv.put("outlet_id", customerNumberNew);
                }

                db.update("sls_customer", cv, "outlet_id=?",new String[]{customerNumber});


                ok = true;
            }catch(Exception ex){
                Log.e("vvv", ex.toString());
                ok = false;
            }
        }

        return ok;
    }


    public static List<Customer> getCustomer(Context ctx,boolean newCustomer){
        Customer cust = null;
        List<Customer> custs = new ArrayList<Customer>();
        DBManager dm = DBManager.getInstance(ctx);
        Cursor cur = null;
        if (newCustomer)
            cur = dm.database().rawQuery("SELECT * FROM sls_customer WHERE status = 2", null);
        else
            cur = dm.database().rawQuery("SELECT * FROM sls_customer", null);

        if (cur.moveToFirst()){
            do {
                cust = new Customer();
                if (cur.isNull(cur.getColumnIndex("delivery_day")))
                    cust.setDeliveryDay(1);
                else
                    cust.setDeliveryDay(cur.getInt(cur.getColumnIndex("delivery_day")));
                    cust.setCustomerNumber(cur.getString(cur.getColumnIndex("outlet_id")));
                    cust.setCustomerName(cur.getString(cur.getColumnIndex("outlet_name")));
                    cust.setContact(cur.getString(cur.getColumnIndex("contact_person")));
                    cust.setAlias(cur.getString(cur.getColumnIndex("outlet_name_alias")));
                    cust.setAddress(cur.getString(cur.getColumnIndex("address")));
                    cust.setCity(cur.getString(cur.getColumnIndex("city")));
                    cust.setPhone(cur.getString(cur.getColumnIndex("phone")));
                    cust.setGroupChannel(cur.getString(cur.getColumnIndex("group_channel")));
                    cust.setChannel(cur.getString(cur.getColumnIndex("channel")));
                    cust.setZone(cur.getString(cur.getColumnIndex("zone")));
                    cust.setTop(cur.getString(cur.getColumnIndex("outlet_top_id")));
                    cust.setCreditLimit(cur.getDouble(cur.getColumnIndex("credit_limit")));
                    cust.setBalance(cur.getDouble(cur.getColumnIndex("balance")));
                    cust.setNotes(cur.getString(cur.getColumnIndex("notes")));
                    cust.setLatitude(cur.getDouble(cur.getColumnIndex("latitude")));
                    cust.setLongitude(cur.getDouble(cur.getColumnIndex("longitude")));
                    cust.setStatus(cur.getString(cur.getColumnIndex("status")));
                    cust.setPicture(cur.getString(cur.getColumnIndex("picture")));
                    cust.setRegion(cur.getString(cur.getColumnIndex("region")));
                    cust.setClassification(cur.getString(cur.getColumnIndex("classification")));
                    cust.setDistrict(cur.getString(cur.getColumnIndex("district")));
                    cust.setTerritory(cur.getString(cur.getColumnIndex("territory")));

                    cust.setPriceGroup(cur.getString(cur.getColumnIndex("outletpricing_group")));
                    custs.add(cust);
            }while (cur.moveToNext());
        }
        cur.close();
        return custs;
    }
    public static Customer getCustomer(Context ctx, String id){
        Customer cust = null;

        DBManager dm = DBManager.getInstance(ctx);
        Cursor     cur = dm.database().rawQuery("SELECT * FROM sls_customer WHERE outlet_id=?", new String[]{id});
        if (cur.moveToFirst()){
            cust = new Customer();
            cust.setBarcodeNumber(cur.getString(cur.getColumnIndex("barcode_number")));
            cust.setDeliveryDay(cur.getInt(cur.getColumnIndex("delivery_day")));
            cust.setCustomerNumber( cur.getString(cur.getColumnIndex( "outlet_id")) );
            cust.setCustomerName(cur.getString(cur.getColumnIndex("outlet_name")));
            cust.setAlias(cur.getString(cur.getColumnIndex("outlet_name_alias")));
            cust.setAddress(cur.getString(cur.getColumnIndex("address")));
            cust.setContact(cur.getString(cur.getColumnIndex("contact_person")));
            cust.setCity(cur.getString(cur.getColumnIndex("city")));
            cust.setPhone(cur.getString(cur.getColumnIndex("phone")));
            cust.setGroupChannel(cur.getString(cur.getColumnIndex("group_channel")));
            cust.setChannel(cur.getString(cur.getColumnIndex("channel")));
            cust.setZone(cur.getString(cur.getColumnIndex("zone")));
            cust.setCreditLimit(cur.getDouble(cur.getColumnIndex("credit_limit")));
            cust.setBalance(cur.getDouble(cur.getColumnIndex("balance")));
            cust.setNotes(cur.getString(cur.getColumnIndex("notes")));
            cust.setTop(cur.getString(cur.getColumnIndex("outlet_top_id")));
            cust.setLatitude(cur.getDouble(cur.getColumnIndex("latitude")));
            cust.setLongitude(cur.getDouble(cur.getColumnIndex("longitude")));
            cust.setStatus(cur.getString(cur.getColumnIndex("status")));
            cust.setRegion(cur.getString(cur.getColumnIndex("region")));
            cust.setPicture(cur.getString(cur.getColumnIndex("picture")));
            cust.setClassification(cur.getString(cur.getColumnIndex("classification")));
            cust.setDistrict(cur.getString(cur.getColumnIndex("district")));
            cust.setTerritory(cur.getString(cur.getColumnIndex("territory")));

            cust.setPriceGroup(cur.getString(cur.getColumnIndex("outletpricing_group")));

        }
        cur.close();
        return cust;
    }


    public String toString(){
        return customerNumber + " "+
               customerName + " "+
               address + " "+
               alias + " "+
               barcode_number + " "+
               city;

    }

}
