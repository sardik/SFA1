package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.util.UomConversion;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 07/08/2015.
 */

public class Product implements Serializable{
    public class STATUS_PRODUCT{
        public static final String INACTIVE = "0";
        public static final String ACTIVE = "1";
        public static final String NPL = "2";
    }
    private long calQtyLarge = 0;
    private long calQtyMedium = 0;
    private long calQtySmall = 0;

    private String productId;
    private String productName;
    private String uom;
    private String status;
    private String is_pareto;
    private String alias;
    private String division;
    private String productType;
    private String category;
    private String brands;
    private String uomLarge;
    private String uomMedium;
    private String uomSmall;
    private int mediumToSmall;
    private long stock;
    private int largeToSmall;
    private double price;
    private SQLiteDatabase db;

    public Product() {

    }

    public Product(SQLiteDatabase db)
    {
        this.db = db;

    }

    public void setDivision(String value) {
        division=value;
    }

    public void setBrand(String value) {
        brands=value;
    }

    public String getBrand() {
        return brands;
    }

    public long getStock() {
        return stock;
    }

    public void setStock(long value) {
        stock=value;
    }

    public String getDivision() {
        return division;
    }

    public void setProductId(String value) {
        productId=value;
    }

    public String getProcutId() {
        return productId;
    }

    public void setProductName(String value) {
        productName=value;
    }

    public String getProductName() {
        return productName;
    }

    public void setUom(String value) {uom=value;}

    public void setCategory(String value) {category=value;}
    public void setProductType(String value) {productType=value;}

    public void setStatus(String value) {status=value;}
    public String getStatus() {return status;}

    public void setPareto(String value) {is_pareto=value;}
    public String getPareto() {return is_pareto;}

    public String getCategory() {return category;}
    public String getProductType() {return productType;}

    public void setUomLarge(String value) {uomLarge=value;}
    public void setUomMedium(String value) {uomMedium=value;}
    public void setUomSmall(String value) {uomSmall=value;}

    public void setConversionLargeToSmall(int value) {largeToSmall=value;}
    public void setConversionMediumToSmall(int value) {mediumToSmall=value;}

    public String getUom() {
        return uom;
    }

    public String getUomSmall() {
        return uomSmall;
    }
    public String getUomMedium() {
        return uomMedium;
    }
    public String getUomLarge() {
        return uomLarge;
    }

    public void setPrice(double value) {
        price=value;
    }

    public void setAlias(String value) {
        alias=value;
    }

    public String getAlias(){
        return alias;
    }

    public Double getPrice() {
        return price;
    }
    public int getConversionLargeToSmall() {
        return largeToSmall;
    }
    public int getConversionMediumToSmall() {
        return mediumToSmall;
    }

    public String[] toArrayUom() {
        String dUom[] = null;

        if (uomLarge !="" && uomMedium !="" && uomSmall !="")
            dUom = new String[] {uomLarge,uomMedium,uomSmall};
        else if (uomLarge !="" && uomMedium !="")
            dUom = new String[] {uomLarge,uomMedium};
        else if (uomLarge !="" && uomSmall !="")
            dUom = new String[] {uomLarge,uomSmall};

        return dUom;


    }

    public void delete(){
        db.delete("sls_product","product_id=?",new String[]{productId});
    }

    public String toString() {
        return productId + " " +productName + " " + uom+" "+alias;
    }

    public static List<Product> getDataTemplate(SQLiteDatabase db,String id){
        List<Product> prds = new ArrayList<Product>();
        Product prd = null;
        Cursor cur = db.rawQuery(" SELECT * FROM sls_product " +
                                 " WHERE product_id IN ("+
                                "  SELECT product_id FROM sls_sku_template WHERE id=? )",new String[]{id});
        if(cur.moveToFirst()) {
            do {
                prd = new Product();
                prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
                prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
                prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
                prd.setDivision(cur.getString(cur.getColumnIndex("division")));
                prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
                prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
                prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
                prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
                prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));

                prd.setStatus(cur.getString(cur.getColumnIndex("status")));
                prd.setPareto(cur.getString(cur.getColumnIndex("is_pareto")));

                prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
                prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
                prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
                prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
                prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));

                prds.add(prd);
            } while (cur.moveToNext());
        }
        cur.close();
        return  prds;
    }

    public static List<Product> getData(SQLiteDatabase db){
        List<Product> prds = new ArrayList<Product>();
        Product prd = null;
        Cursor cur = db.rawQuery("SELECT * FROM sls_product WHERE status = '"+STATUS_PRODUCT.NPL+"' AND is_pareto = '0' "+
                                 "UNION ALL "+
                                 "SELECT * FROM sls_product WHERE status='"+STATUS_PRODUCT.ACTIVE+"' AND is_pareto = '0' "+
                                 "UNION ALL "+
                                 "SELECT * FROM sls_product WHERE status='"+STATUS_PRODUCT.ACTIVE+"' AND is_pareto = '1' ", null);
       if (cur.moveToFirst()) {
           do {
               prd = new Product();
               prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
               prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
               prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
               prd.setDivision(cur.getString(cur.getColumnIndex("division")));
               prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
               prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
               prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
               prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
               prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));

               prd.setStatus(cur.getString(cur.getColumnIndex("status")));
               prd.setPareto(cur.getString(cur.getColumnIndex("is_pareto")));

               prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
               prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
               prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
               prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
               prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));

               prds.add(prd);
           } while (cur.moveToNext());
       }
        cur.close();
        return  prds;
    }

    public static List<Product> getDataPure(SQLiteDatabase db,String sort){
        List<Product> prds = new ArrayList<Product>();
        Product prd = null;
        String orderby = " ORDER BY product_name";

        if(sort.equals("alias"))
            orderby = " ORDER BY product_name_alias";
        else if(sort.equals("code") || sort.equals("product_id"))
            orderby = " ORDER BY product_id";
        else if(sort.equals("smallest"))
            orderby = " ORDER BY stock";
        else if(sort.equals("largest"))
            orderby = " ORDER BY stock DESC";

        Cursor cur = db.rawQuery("SELECT * FROM sls_product "+orderby, null);
        if (cur.moveToFirst()) {
            do {
                prd = new Product();
                prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
                prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
                prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
                prd.setDivision(cur.getString(cur.getColumnIndex("division")));
                prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
                prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
                prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
                prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
                prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));

                prd.setStock(cur.getLong(cur.getColumnIndex("stock")));

                prd.setStatus(cur.getString(cur.getColumnIndex("status")));
                prd.setPareto(cur.getString(cur.getColumnIndex("is_pareto")));

                prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
                prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
                prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
                prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
                prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));

                prds.add(prd);
            } while (cur.moveToNext());
        }
        cur.close();
        return  prds;
    }

    public static List<Product> getTemplateNPLFocus(SQLiteDatabase db){
        List<Product> prds = new ArrayList<Product>();
        Product prd = null;
        Cursor cur = db.rawQuery(
                "SELECT * FROM sls_product WHERE status='"+STATUS_PRODUCT.ACTIVE+"' AND is_pareto = '1' "+
                " UNION ALL "+
                "SELECT * FROM sls_product WHERE status = '"+STATUS_PRODUCT.NPL+"'", null);
        if (cur.moveToFirst()) {
            do {
                prd = new Product();
                prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
                prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
                prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
                prd.setDivision(cur.getString(cur.getColumnIndex("division")));
                prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
                prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
                prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
                prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
                prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));

                prd.setStatus(cur.getString(cur.getColumnIndex("status")));
                prd.setPareto(cur.getString(cur.getColumnIndex("is_pareto")));

                prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
                prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
                prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
                prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
                prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));

                prds.add(prd);
            } while (cur.moveToNext());
        }
        cur.close();
        return  prds;
    }

    // +OBBIE
    public static ArrayList<Product> getAllProducts(SQLiteDatabase db, String sort) {
        ArrayList<Product> prds = new ArrayList<Product>();
        Product prd = null;
        String orderby = " ORDER BY product_name";

        if(sort.equals("alias"))
            orderby = " ORDER BY product_name_alias";
        else if(sort.equals("code") || sort.equals("product_id"))
            orderby = " ORDER BY product_id";
        else if(sort.equals("smallest"))
            orderby = " ORDER BY stock";
        else if(sort.equals("largest"))
            orderby = " ORDER BY stock DESC";

        Cursor cur = db.rawQuery("SELECT * FROM sls_product "+orderby, null);
        if (cur.moveToFirst()) {
            do {
                prd = new Product();
                prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
                prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
                prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
                prd.setDivision(cur.getString(cur.getColumnIndex("division")));
                prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
                prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
                prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
                prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
                prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));

                prd.setStock(cur.getLong(cur.getColumnIndex("stock")));

                prd.setStatus(cur.getString(cur.getColumnIndex("status")));
                prd.setPareto(cur.getString(cur.getColumnIndex("is_pareto")));

                prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
                prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
                prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
                prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
                prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));

                prds.add(prd);
            } while (cur.moveToNext());
        }
        cur.close();
        return  prds;
    }

    public static ArrayList<Product> getAllProductsByBrand(SQLiteDatabase db, String idBrand){

        Product prd = null;
        ArrayList<Product> Products = new ArrayList<Product>();
        Cursor cur = db.rawQuery("SELECT * FROM sls_product WHERE product_brands=?", new String[]{idBrand});

        if (cur.moveToFirst()){
            while (cur.isAfterLast() == false) {
                prd = new Product();
                prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
                prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
                prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
                prd.setDivision(cur.getString(cur.getColumnIndex("division")));
                prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
                prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
                prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
                prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
                prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));

                prd.setStatus(cur.getString(cur.getColumnIndex("status")));
                prd.setPareto(cur.getString(cur.getColumnIndex("is_pareto")));

                prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
                prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
                prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
                prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
                prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));

                Products.add(prd);

                cur.moveToNext();
            }
            cur.close();
        }

        return Products;

    }

    public static Product getProductData(SQLiteDatabase db, String pid){
        Product prd = null;
        Cursor cur = db.rawQuery("SELECT * FROM sls_product WHERE product_id=?", new String[]{pid});
        if (cur.moveToFirst()){
            prd = new Product();
            prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
            prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
            prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
            prd.setDivision(cur.getString(cur.getColumnIndex("division")));
            prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
            prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
            prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
            prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
            prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));

            prd.setStatus(cur.getString(cur.getColumnIndex("status")));
            prd.setPareto(cur.getString(cur.getColumnIndex("is_pareto")));

            prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
            prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
            prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
            prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
            prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));
        }
        cur.close();

        return prd;

    }
    // END +OBBIE


    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery("SELECT product_id FROM sls_product WHERE product_id=?", new String[]{productId});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }

    public boolean save(){
        boolean ok = false;

        //db.delete("sls_product", null, null);

        if (!isExist()){
            try {
                ContentValues cv = new ContentValues();
                cv.put("product_id", productId);
                cv.put("product_name", productName);
                cv.put("product_name_alias", alias );
                cv.put("division", division);
                cv.put("base_uom", uom);
                cv.put("product_type", productType);
                cv.put("product_brands", brands);
                cv.put("product_category", category);
                cv.put("price", price);
                cv.put("status", status);
                cv.put("is_pareto", is_pareto);
                cv.put("small_uom", uomSmall);
                cv.put("medium_uom", uomMedium);
                cv.put("large_uom", uomLarge);
                cv.put("large_to_small", largeToSmall);
                cv.put("medium_to_small", mediumToSmall);
                db.insert("sls_product", null, cv);
                ok = true;
            }catch(Exception ex){
                Log.e("vvv", "", ex);
                ok = false;
            }
        }else{
            try {
                ContentValues cv = new ContentValues();
                cv.put("product_name", productName);
                cv.put("product_name_alias", alias );
                cv.put("division", division);
                cv.put("base_uom", uom);
                cv.put("product_type", productType);
                cv.put("product_brands", brands);
                cv.put("product_category", category);
                cv.put("status", status);
                cv.put("is_pareto", is_pareto);
                cv.put("price", price);
                cv.put("small_uom", uomSmall);
                cv.put("medium_uom", uomMedium);
                cv.put("large_uom", uomLarge);
                cv.put("large_to_small", largeToSmall);
                cv.put("medium_to_small", mediumToSmall);
                db.update("sls_product", cv,"product_id=?",new String[]{productId});
                ok = true;
            }catch(Exception ex){
                Log.e("vvv", "", ex);
                ok = false;
            }
        }

        return ok;
    }

    public void calculateConversion(){

        UomConversion unitc= new UomConversion(stock, largeToSmall, mediumToSmall);
        unitc.fromSmall();
        calQtyLarge = unitc.getLarge();
        calQtyMedium = unitc.getMedium();
        calQtySmall = unitc.getSmall();

    }

    public long getQtyLarge(){
        return calQtyLarge;
    }

    public long getQtyMedium(){
        return calQtyMedium;
    }

    public long getQtySmall(){
        return calQtySmall;
    }


}
