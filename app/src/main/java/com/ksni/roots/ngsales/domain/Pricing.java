package com.ksni.roots.ngsales.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by #roots on 01/09/2015.
 */
public class Pricing {
    private String customer;
    private String sku;
    private double qty;
    private double value;
    private String uom;
    private String channel;
    private String groupChannel;
    private String chain;
    private String zone;
    private String date;
    private String division;
    private SQLiteDatabase db;
    private Context context;


    public Pricing(SQLiteDatabase db,
                   String channel,
                   String date) {
        this.db         = db;
        this.date       = date;
        this.channel    = channel;

    }

    public Pricing (SQLiteDatabase db,
                   String customer,
                   String channel,
                   String zone,
                   String division,
                   String sku,
                   String uom,
                   double qty,
                   double value,
                   String date,
                   Context ctx) {

                        this.db         = db;
                        this.customer   = customer;
                        this.sku        = sku;
                        this.context    = ctx;
                        this.date       = date;
                        this.uom        = uom;
                        this.qty        = qty;
                        this.channel    = channel;
                        this.zone       = zone;
                        this.division   = division;
                        this.value = value;

    }

    private ResultPricing getValuePrice(String tbl,String pCond,String val[]) {
        ResultPricing buff = new ResultPricing();

        double harga = 0;
        boolean ada = false;
        Cursor cur = null;

        cur = db.rawQuery("SELECT price FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and "+
                "valid_from=(SELECT MAX(valid_from) FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )",val );
        if (cur.moveToFirst()) {
            harga = cur.getDouble(0);
            ada = true;
        }
        buff.ada = ada;
        buff.harga = harga;

        cur.close();
        return buff;
    }



    private ResultPricing getValuePriceInvoice(String tbl,String pCond,String val[]) {
        ResultPricing buff = new ResultPricing();

        double harga = 0;
        boolean ada = false;
        Cursor cur = null;

        cur = db.rawQuery("SELECT price,from_value,to_value,uom FROM "+tbl+" WHERE " + pCond + " and DATE('"+ date +"') between DATE(valid_from) and DATE(valid_to) and "+
                "valid_from=(SELECT MAX(valid_from) FROM "+tbl+" WHERE " + pCond + " and DATE('"+ date +"') between DATE(valid_from) and DATE(valid_to) )", val );
        if (cur.moveToFirst()) {
            do{
                double from_val = cur.getDouble(cur.getColumnIndex("from_value"));
                double to_val = cur.getDouble(cur.getColumnIndex("to_value"));
                if (value>=from_val && value<=to_val){
                    harga = cur.getDouble(cur.getColumnIndex("price"));
                    ada = true;
                    break;
                }
            }while(cur.moveToNext());


        }
        buff.ada = ada;
        buff.harga = harga;

        cur.close();
        return buff;
    }


    private ResultPricing getValuePriceInvoiceWithoutUoM(String tbl,String pCond,String val[]) {
        ResultPricing buff = new ResultPricing();

        double harga = 0;
        boolean ada = false;
        Cursor cur = null;

        cur = db.rawQuery("SELECT price,from_value,to_value,uom,product_id FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and "+
                "valid_from=(SELECT MAX(valid_from) FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )",val );
        if (cur.moveToFirst()) {
            do{
                double from_val = cur.getDouble(cur.getColumnIndex("from_value"));
                double to_val = cur.getDouble(cur.getColumnIndex("to_value"));
                if (value>=from_val && value<=to_val) {
                    harga = cur.getDouble(cur.getColumnIndex("price"));


                    String xuom = cur.getString(cur.getColumnIndex("uom"));
                    Product prd = Product.getProductData(db, cur.getString(cur.getColumnIndex("product_id")));
                    if (prd != null) {
                        if (xuom.toLowerCase().equals(prd.getUomSmall().toLowerCase())) {
                            //small conversion
                            if (uom.toLowerCase().equals(prd.getUomMedium().toLowerCase())) {
                                harga = harga * prd.getConversionMediumToSmall();
                            } else if (uom.toLowerCase().equals(prd.getUomLarge().toLowerCase())) {
                                harga = harga * prd.getConversionLargeToSmall();
                            }
                        } else if (xuom.toLowerCase().equals(prd.getUomMedium().toLowerCase())) {
                            //medium conversion
                            if (uom.toLowerCase().equals(prd.getUomSmall().toLowerCase())) {
                                harga = Helper.getDouble2Digit(harga / prd.getConversionMediumToSmall());
                            } else if (uom.toLowerCase().equals(prd.getUomLarge().toLowerCase())) {
                                harga = Helper.getDouble2Digit(harga * (prd.getConversionLargeToSmall() / prd.getConversionMediumToSmall()));
                            }
                        } else if (xuom.toLowerCase().equals(prd.getUomLarge().toLowerCase())) {
                            //large conversion
                            if (uom.toLowerCase().equals(prd.getUomSmall().toLowerCase())) {
                                harga = Helper.getDouble2Digit(harga / prd.getConversionLargeToSmall());
                            } else if (uom.toLowerCase().equals(prd.getUomMedium().toLowerCase())) {
                                harga = Helper.getDouble2Digit(harga / (prd.getConversionLargeToSmall() / prd.getConversionMediumToSmall()));
                            }
                        }


                        ada = true;
                        break;
                    }
                }
            }while(cur.moveToNext());


        }
        buff.ada = ada;
        buff.harga = harga;

        cur.close();
        return buff;
    }




    private ResultPricing getValuePriceWithoutUoM(String tbl,String pCond,String val[]) {
        ResultPricing buff = new ResultPricing();

        double harga = 0;
        boolean ada = false;
        Cursor cur = null;

        cur = db.rawQuery("SELECT price,product_id,uom FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and "+
                "valid_from=(SELECT MAX(valid_from) FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )",val );
        if (cur.moveToFirst()) {
           harga = cur.getDouble(cur.getColumnIndex("price"));

            String xuom = cur.getString(cur.getColumnIndex("uom"));
            Product prd = Product.getProductData(db, cur.getString(cur.getColumnIndex("product_id")));
            if (prd!=null){

                if (xuom.toLowerCase().equals(prd.getUomSmall().toLowerCase())){
                    //small conversion
                    if(uom.toLowerCase().equals(prd.getUomMedium().toLowerCase())){
                        harga = harga * prd.getConversionMediumToSmall();
                    }else if(uom.toLowerCase().equals(prd.getUomLarge().toLowerCase())){
                        harga = harga * prd.getConversionLargeToSmall();
                    }
                }else if (xuom.toLowerCase().equals(prd.getUomMedium().toLowerCase())){
                    //medium conversion
                    if(uom.toLowerCase().equals(prd.getUomSmall().toLowerCase())){
                        harga = Helper.getDouble2Digit(harga / prd.getConversionMediumToSmall());
                    }else if(uom.toLowerCase().equals(prd.getUomLarge().toLowerCase())){
                        harga = Helper.getDouble2Digit(harga * (prd.getConversionLargeToSmall() / prd.getConversionMediumToSmall()));
                    }
                }else if (xuom.toLowerCase().equals(prd.getUomLarge().toLowerCase())){
                    //large conversion
                    if(uom.toLowerCase().equals(prd.getUomSmall().toLowerCase())){
                        harga = Helper.getDouble2Digit(harga / prd.getConversionLargeToSmall());
                    }else if(uom.toLowerCase().equals(prd.getUomMedium().toLowerCase())){
                        harga = Helper.getDouble2Digit(harga / (prd.getConversionLargeToSmall() / prd.getConversionMediumToSmall() ));
                    }
                }

            }

            ada = true;
        }
        buff.ada = ada;
        buff.harga = harga;

        cur.close();
        return buff;
    }

    private ResultPricing getValueDiscount(String tbl,String pCond,String val[]) {
        ResultPricing buff = new ResultPricing();

        double discount = 0;
        boolean ada = false;
        Cursor cur = null;
        Log.e("ada ext","");
        cur = db.rawQuery("SELECT discount,from_qty,to_qty,from_value,to_value,is_qty FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and "+
                "valid_from=(SELECT MAX(valid_from) FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )",val );
        if (cur.moveToFirst()) {
            do {
                if (cur.getString(cur.getColumnIndex("is_qty")).equals("1")) {
                    if (qty>=cur.getInt(cur.getColumnIndex("from_qty")) && qty<=cur.getInt(cur.getColumnIndex("to_qty"))){
                        ada = true;

                        discount = cur.getDouble(cur.getColumnIndex("discount"));
                        break;
                    }
                }else{
                    if (value>=cur.getInt(cur.getColumnIndex("from_value")) && value<=cur.getInt(cur.getColumnIndex("to_value"))){
                        ada = true;
                        discount = cur.getDouble(cur.getColumnIndex("discount"));
                        break;
                    }
                }
            }while(cur.moveToNext());
        }
        buff.ada = ada;
        buff.harga = discount;

        cur.close();
        return buff;
    }

    private ResultPricing getValueDiscountInvoice(String tbl,String pCond,String val[]) {
        ResultPricing buff = new ResultPricing();

        double discount = 0;
        boolean ada = false;
        Cursor cur = null;

        cur = db.rawQuery("SELECT discount,from_value,to_value FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) and "+
                "valid_from=(SELECT MAX(valid_from) FROM "+tbl+" WHERE " + pCond + " and DATE('"+date+"') between DATE(valid_from) and DATE(valid_to) )",val );
        if (cur.moveToFirst()) {
            do {
                    if (value>=cur.getInt(cur.getColumnIndex("from_value")) && value<=cur.getInt(cur.getColumnIndex("to_value"))){
                        ada = true;
                        discount = cur.getDouble(cur.getColumnIndex("discount"));
                        break;
                    }
            }while(cur.moveToNext());
        }
        buff.ada = ada;
        buff.harga = discount;

        cur.close();
        return buff;
    }

    public class Summary{
        public String product_id;
        public String uom;
        public double qty;
        public int id;
        public double value;
        public String uomSmall;
        public String uomMedium;
        public String uomLarge;

    }



    public ResultPricing getIPT(List<OrderItem> items,double totalInvoice,boolean ext) {
        Map<String,Summary> ls = new HashMap<String,Summary>();

        String tab = "";
        if (ext)
            tab = "sls_discount_ext_channel_ipt";
        else
            tab = "sls_discount_spc_channel_ipt";


        ResultPricing pp = null;
        int totalPerItem = 0;
        for(OrderItem item:items){
            if (item.itemType.equals("N")) { // khusus regular type

                if (!ls.containsKey(item.productId )) {
                    totalPerItem++;
                    Summary s = new Summary();
                    s.id = item.id;
                    s.product_id = item.productId;
                    s.uom = item.uom;
                    s.qty = item.qty;
                    s.value = item.getSubTotal();

                    s.uomSmall = item.uomSmall;
                    s.uomMedium = item.uomMedium;
                    s.uomLarge = item.uomLarge;

                    ls.put(item.productId, s);
                } else {
                    ls.get(item.productId).qty += item.qty;
                    ls.get(item.productId).value += item.getSubTotal();
                }
            }
        }


        Cursor cur =  db.rawQuery("SELECT * FROM " + tab + " WHERE channel=? and DATE('" + date + "') between DATE(valid_from) and DATE(valid_to) AND " +
                "valid_from = ( SELECT MAX(valid_from) FROM " + tab + " WHERE channel=? and DATE('" + date + "') between DATE(valid_from) and DATE(valid_to) ) ORDER BY ipt DESC", new String[]{channel, channel});
        if (cur.moveToFirst()){
            do{
                String isQty = cur.getString(cur.getColumnIndex("is_qty"));
                String isPercent = cur.getString(cur.getColumnIndex("is_percent"));
                int minQty = cur.getInt(cur.getColumnIndex("min_qty"));
                double minValue = cur.getInt(cur.getColumnIndex("min_value"));
                double valueEx = cur.getDouble(cur.getColumnIndex("value_ex"));
                double discount = cur.getDouble(cur.getColumnIndex("discount"));
                String uom = cur.getString(cur.getColumnIndex("uom"));
                int ipt = cur.getInt(cur.getColumnIndex("ipt"));
                boolean ada = false;

                int iptc = 0;
                Map<String,String> keyProduct = new HashMap<String,String>();

                List<Integer> lineItem = new ArrayList<Integer>();
                lineItem.clear();
                if(totalPerItem>=ipt && totalInvoice>=minValue){
                    for (Summary prd : ls.values()) {
                        for (OrderItem item : items) {
                            if (item.productId.equals(prd.product_id)) {
                                int intUom = 0;
                                if (uom.toLowerCase().equals(item.uomSmall.toLowerCase()))
                                    intUom = 1; //kecil
                                else if (uom.toLowerCase().equals(item.uomMedium.toLowerCase()))
                                    intUom = 2; //medium
                                else if (uom.toLowerCase().equals(item.uomLarge.toLowerCase()))
                                    intUom = 3; //large

                                int intUomInput = 0;

                                if (item.uom.toLowerCase().equals(item.uomSmall.toLowerCase()))
                                    intUomInput = 1; //kecil
                                else if (item.uom.toLowerCase().equals(item.uomMedium.toLowerCase()))
                                    intUomInput = 2; //medium
                                else if (item.uom.toLowerCase().equals(item.uomLarge.toLowerCase()))
                                    intUomInput = 3; //large


                                if (intUomInput>=intUom && item.qty>0) {
                                    if(!keyProduct.containsKey(item.productId)) {
                                        keyProduct.put(item.productId,item.productId);
                                        iptc++;
                                    }

                                    lineItem.add(item.id);

                                }


                            }
                        }
                    }

                    if(iptc>=ipt){
                        //apply discount

                        ada =  true;

                        pp = new ResultPricing();
                        pp.ada = true;
                        pp.min_qty =minQty;
                        pp.uom =uom;
                        pp.ipts = new ArrayList<Integer>();
                        pp.ipts.addAll(lineItem);
                        if (isPercent.equals("1")) {
                            pp.discount = discount;
                            pp.isPercent = "1";
                        }
                        else{
                            pp.discount = valueEx;
                            pp.isPercent = "0";
                        }

                        break;
                    }
                }


                if (ada) break;
            }while(cur.moveToNext());
        }

        cur.close();


        return pp;

    }


    public ResultPricing getIPTOld(List<OrderItem> items,double totalInvoice,boolean ext) {
        Map<String,Summary> ls = new HashMap<String,Summary>();

        String tab = "";
        if (ext)
            tab = "sls_discount_ext_channel_ipt";
        else
            tab = "sls_discount_spc_channel_ipt";


        ResultPricing pp = null;

        for(OrderItem item:items){
            if (item.itemType.equals("N")) { // khusus regular type

                if (!ls.containsKey(item.productId + item.uom)) {
                    Summary s = new Summary();
                    s.id = item.id;
                    s.product_id = item.productId;
                    s.uom = item.uom;
                    s.qty = item.qty;
                    s.value = item.getSubTotal();
                    ls.put(item.productId + item.uom, s);
                } else {
                    ls.get(item.productId + item.uom).qty += item.qty;
                    ls.get(item.productId + item.uom).value += item.getSubTotal();
                }
            }
        }



        Cursor cur =  db.rawQuery("SELECT * FROM " + tab + " WHERE channel=? and DATE('" + date + "') between DATE(valid_from) and DATE(valid_to) AND " +
                "valid_from = ( SELECT MAX(valid_from) FROM " + tab + " WHERE channel=? and DATE('" + date + "') between DATE(valid_from) and DATE(valid_to) ) ORDER BY ipt DESC", new String[]{channel, channel});
        if (cur.moveToFirst()){
            do{

                String isQty = cur.getString(cur.getColumnIndex("is_qty"));
                String isPercent = cur.getString(cur.getColumnIndex("is_percent"));
                int minQty = cur.getInt(cur.getColumnIndex("min_qty"));
                double minValue = cur.getInt(cur.getColumnIndex("min_value"));
                double valueEx = cur.getDouble(cur.getColumnIndex("value_ex"));
                double discount = cur.getDouble(cur.getColumnIndex("discount"));
                String uom = cur.getString(cur.getColumnIndex("uom"));
                int ipt = cur.getInt(cur.getColumnIndex("ipt"));
                boolean ada = false;



                for(Summary prd:ls.values()){
                    Log.e("lada","lada");
                    if(prd.uom.toLowerCase().equals(uom.toLowerCase())){
                        int iptc = 0;

                        List<Integer> lineItem = new ArrayList<Integer>();
                        lineItem.clear();

                        for(Summary prd2:ls.values()){

                            if (prd2.uom.equals(uom)){

                                if(prd2.qty >= minQty) {
                                    if (minValue != 0) {

                                        if (totalInvoice>=minValue){
                                            lineItem.add(prd2.id);
                                            iptc++;
                                        }
                                    } else {
                                        lineItem.add(prd2.id);
                                        iptc++;
                                    }
                                }


                                /*if(isQty.equals("1")) {
                                    if (prd2.qty >= minQty) {
                                        lineItem.add(prd2.id);
                                        iptc++;
                                    }
                                }else{
                                    if (prd2.value >= minValue) {
                                        lineItem.add(prd2.id);
                                        iptc++;
                                    }
                                }*/

                            }
                        }
                        Log.e("iptc",String.valueOf(iptc));
                        if(iptc>=ipt){

                            // break disini dapet lho
                            ada =  true;

                            pp = new ResultPricing();
                            pp.ada = true;
                            pp.min_qty =minQty;
                            pp.uom =uom;
                            Log.e("IPT","HOREEE "+String.valueOf(discount));
                            pp.ipts = new ArrayList<Integer>();
                            pp.ipts.addAll(lineItem);
                            if (isPercent.equals("1")) {
                                pp.discount = discount;
                                pp.isPercent = "1";
                            }
                            else{
                                pp.discount = valueEx;
                                pp.isPercent = "0";
                            }

                            break;
                        }
                    }
                }

            if (ada) break;
            }while(cur.moveToNext());
        }

        cur.close();


        return pp;

    }


    private double getPriceFromProduct(){
        double pr = 0;
        Cursor cur =  db.rawQuery("SELECT price FROM sls_product WHERE product_id=?",new String[]{sku});
        if (cur.moveToFirst()){
            pr = cur.getDouble(0);
        }
        cur.close();
        return pr;
    }


    public double xxxxgetPrice(){
        //PMA disable pricing, user master product
        return this.getPriceFromProduct();
    }

    public double getPrice(){
        double harga = 0;

        //PMA disable pricing, user master product

        ResultPricing priceCustomerSku = getValuePriceInvoice("sls_pricing_by_outlet", "outlet_id=? AND product_id=? AND uom=?", new String[]{customer, sku, uom, customer, sku, uom});
        if (!priceCustomerSku.ada) {
            ResultPricing priceChannelSku = getValuePriceInvoice("sls_pricing_by_channel", "channel=? AND product_id=? AND uom=?", new String[]{channel, sku, uom, channel, sku, uom});
            if (!priceChannelSku.ada) {
                    ResultPricing priceZoneSku = getValuePriceInvoice("sls_pricing_by_zone", "zone=? AND product_id=? AND uom=?", new String[]{zone, sku, uom, zone, sku, uom});
                    if (priceZoneSku.ada) {
                        harga = priceZoneSku.harga;
                    }
            }else{
                harga = priceChannelSku.harga;
            }
        } else {
            harga = priceCustomerSku.harga;
        }

        //Log.e("harga",String.valueOf(harga) + "::UOM"+uom);
        if (harga==0){ // force convert price by conversion
            ResultPricing XpriceCustomerSku = getValuePriceInvoiceWithoutUoM("sls_pricing_by_outlet", "outlet_id=? AND product_id=?", new String[]{customer, sku, customer, sku});
            if (!XpriceCustomerSku.ada) {
                ResultPricing XpriceChannelSku = getValuePriceInvoiceWithoutUoM("sls_pricing_by_channel", "channel=? AND product_id=?", new String[]{channel, sku, channel, sku});
                if (!XpriceChannelSku.ada) {
                    ResultPricing XpriceZoneSku = getValuePriceInvoiceWithoutUoM("sls_pricing_by_zone", "zone=? AND product_id=?", new String[]{zone, sku, zone, sku});
                    if (XpriceZoneSku.ada) {
                        harga = XpriceZoneSku.harga;
                    }
                } else {
                    harga = XpriceChannelSku.harga;
                }
            } else {
                harga = XpriceCustomerSku.harga;
            }

        }

        return harga;
    }

    /*public double getPricePerItem(boolean india,boolean p1){
        double harga = 0;
        boolean p1exec = false;
        if (india)
            p1exec = p1;
        else
            p1exec = true;

            ResultPricing priceCustomerSku = getValuePrice("sls_pricing_by_outlet", "outlet_id=? AND product_id=? AND uom=?", new String[]{customer, sku, uom, customer, sku, uom}, p1exec);
            if (!priceCustomerSku.ada) {
                ResultPricing priceChannelSku = getValuePrice("sls_pricing_by_channel", "channel=? AND product_id=? AND uom=?", new String[]{channel, sku, uom, channel, sku, uom},p1exec);
                if (!priceChannelSku.ada) {
                    ResultPricing priceZoneSku = getValuePrice("sls_pricing_by_zone", "zone=? AND product_id=? AND uom=?", new String[]{zone, sku, uom, zone, sku, uom},p1exec);
                    if (priceZoneSku.ada) {
                        harga = priceZoneSku.harga;
                    }
                } else {
                    harga = priceChannelSku.harga;
                }
            } else {
                harga = priceCustomerSku.harga;
            }

        if (harga==0){ // force convert price by conversion
            ResultPricing XpriceCustomerSku = getValuePriceWithoutUoM("sls_pricing_by_outlet", "outlet_id=? AND product_id=?", new String[]{customer, sku, customer, sku}, p1exec);
            if (!XpriceCustomerSku.ada) {
                ResultPricing XpriceChannelSku = getValuePriceWithoutUoM("sls_pricing_by_channel", "channel=? AND product_id=?", new String[]{channel, sku, channel, sku}, p1exec);
                if (!XpriceChannelSku.ada) {
                    ResultPricing XpriceZoneSku = getValuePriceWithoutUoM("sls_pricing_by_zone", "zone=? AND product_id=?", new String[]{zone, sku, zone, sku}, p1exec);
                    if (XpriceZoneSku.ada) {
                        harga = XpriceZoneSku.harga;
                    }
                } else {
                    harga = XpriceChannelSku.harga;
                }
            } else {
                harga = XpriceCustomerSku.harga;
            }

        }

        return harga;
    }
*/

    public double getDiscountReg(){
        double discount = 0;

        //read config allow discount
        boolean allow_discount = Config.getChecked(context, "allow_discount");
        if (allow_discount) {
            ResultPricing discountCustomerSku = getValueDiscount("sls_discount_reg_outlet", "outlet_id=? and product_id=?  and uom=? ", new String[]{customer, sku, uom, customer, sku, uom});
            if (!discountCustomerSku.ada) {
                ResultPricing discountChannelSku = getValueDiscount("sls_discount_reg_channel", "channel=? and product_id=?  and uom=? ", new String[]{channel, sku, uom, channel, sku, uom});
                if (!discountChannelSku.ada) {
                    ResultPricing discountZoneSku = getValueDiscount("sls_discount_reg_channel_division", "channel=? and division=?  and uom=?", new String[]{channel, division, uom, channel, division, uom});
                    if (discountZoneSku.ada) {
                        discount = discountZoneSku.harga;
                    }
                } else {
                    discount = discountChannelSku.harga;
                }
            } else {
                discount = discountCustomerSku.harga;
            }


            if(discount==0){
                ResultPricing discountChannel = getValueDiscountInvoice("sls_discount_reg_channel_india", "channel=?", new String[]{channel, channel});
                if (discountChannel.ada) discount = discountChannel.harga;
            }



        }
        return discount;

    }


    public double getDiscountExt(){
        double discount = 0;
        //read config allow discount
        boolean allow_discount = Config.getChecked(context, "allow_discount");
        if (allow_discount) {
            ResultPricing discountCustomerSku = getValueDiscount("sls_discount_ext_outlet", "outlet_id=? and product_id=?  and uom=?", new String[]{customer, sku, uom, customer, sku, uom});
            if (!discountCustomerSku.ada) {
                ResultPricing discountChannelSku = getValueDiscount("sls_discount_ext_channel", "channel=? and product_id=? and uom=?", new String[]{channel, sku, uom, channel, sku, uom});
                if (discountChannelSku.ada) {
                    discount = discountChannelSku.harga;
                } else {
                    ResultPricing discountChannelDivision = getValueDiscount("sls_discount_ext_channel_division", "channel=? and division=?  and uom=?", new String[]{channel, division, uom, channel, division, uom});
                    if (discountChannelDivision.ada) {
                        discount = discountChannelDivision.harga;
                    }

                }
            } else {
                discount = discountCustomerSku.harga;
            }

            if(discount==0){
                Log.e("VALUE",String.valueOf(value));
                //Log.e("INDIA",zone);
                //Log.e("INDIA",channel);
                ResultPricing discountChannel = getValueDiscountInvoice("sls_discount_ext_channel_india", "channel=? and zone=?", new String[]{channel,zone, channel,zone});
                if (discountChannel.ada) discount = discountChannel.harga;
            }

        }
        return discount;

    }


    public double getDiscountSpec(){
        double discount = 0;

        //read config allow discount
        boolean allow_discount = Config.getChecked(context, "allow_discount");
        if (allow_discount) {
            ResultPricing discountCustomerSku = getValueDiscount("sls_discount_spc_outlet", "outlet_id=? and product_id=?  and uom=?", new String[]{customer, sku, uom, customer, sku, uom});
            if (!discountCustomerSku.ada) {
                ResultPricing discountChannelSku = getValueDiscount("sls_discount_spc_channel", "channel=? and product_id=? and uom=?", new String[]{channel, sku, uom, channel, sku, uom});
                if (!discountChannelSku.ada) {
                    ResultPricing discountZoneSku = getValueDiscount("sls_discount_spc_channel_division", "channel=? and division=?  and uom=?", new String[]{channel, division, uom, channel, division, uom});
                    if (discountZoneSku.ada) {
                        discount = discountZoneSku.harga;
                    }
                } else {
                    discount = discountChannelSku.harga;
                }
            } else {
                discount = discountCustomerSku.harga;
            }

            //if(discount==0){
             //   ResultPricing discountChannel = getValueDiscountInvoice("sls_discount_spc_channel_india", "channel=? and zone=?", new String[]{channel,zone, channel,zone});
              //  if (discountChannel.ada) discount = discountChannel.harga;
           // }

        }
        return discount;

    }





}
