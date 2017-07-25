package com.ksni.roots.ngsales.domain;

import android.content.Context;
import android.database.Cursor;

import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by #roots on 13/12/2015.
 */
public class ProgramsInfo {
    private String outlet_id;
    private String zone;
    private String channel;
    private Context ctx;

    public ProgramsInfo(Context ctx,String outlet_id){
        this.outlet_id = outlet_id;
        this.ctx = ctx;

         com.ksni.roots.ngsales.model.Customer cust = com.ksni.roots.ngsales.model.Customer.getCustomer(ctx,outlet_id);
        if (cust!=null) {
            this.zone = cust.getZone();
            this.channel = cust.getChannel();
        }
    }

    private Map<String,InfoHolder> priceTable(String table,String kond,String []kondValue){
        InfoHolder ih = null;
        DBManager dm = DBManager.getInstance(ctx);
        Map<String,InfoHolder>  list = new HashMap<String,InfoHolder>();
        Cursor cur = dm.database().rawQuery("SELECT * FROM "+table+" WHERE DATE(?) between DATE(valid_from) AND  DATE(valid_to) and "+kond,kondValue) ;
        if (cur.moveToFirst()){
            do{
                String prd = cur.getString(cur.getColumnIndex("product_id"));
                String uom = cur.getString(cur.getColumnIndex("uom"));
                String key = prd + uom;
                ih = new InfoHolder();
                ih.product_id = prd;
                ih.uom = uom;

                ih.price = cur.getDouble(cur.getColumnIndex("price"));

                ih.validFrom = cur.getString(cur.getColumnIndex("valid_from"));
                ih.validTo = cur.getString(cur.getColumnIndex("valid_to"));
                ih.fromValue = cur.getDouble(cur.getColumnIndex("from_value"));
                ih.toValue = cur.getDouble(cur.getColumnIndex("to_value"));
                ih.description = table;

                if (!list.containsKey(key)) {
                    list.put(key, ih);
                }
            }while(cur.moveToNext());
        }
        cur.close();
        return list;

    }

    public List<InfoHolder> getListPricing(boolean p1){
        Map<String,InfoHolder>  price_outlet = priceTable("sls_pricing_by_outlet","outlet_id=?",new String[]{Helper.getCurrentDate(),outlet_id});
        Map<String,InfoHolder>  price_channel = priceTable("sls_pricing_by_channel", "channel=?", new String[]{Helper.getCurrentDate(), channel});
        Map<String,InfoHolder>  price_zone = priceTable("sls_pricing_by_zone","zone=?",new String[]{Helper.getCurrentDate(),zone});

        for(InfoHolder listChannel:price_channel.values()){
            if (!price_outlet.containsKey(listChannel.product_id+listChannel.uom)){
                price_outlet.put(listChannel.product_id+listChannel.uom,listChannel);
            }
        }

        for(InfoHolder listZone:price_zone.values()){
            if (!price_outlet.containsKey(listZone.product_id+listZone.uom)){
                price_outlet.put(listZone.product_id+listZone.uom,listZone);
            }
        }

        return new ArrayList<InfoHolder>(price_outlet.values());

    }


    private Map<String,InfoHolder> discountTable(String table,String kond,String []kondValue){
        InfoHolder ih = null;
        DBManager dm = DBManager.getInstance(ctx);
        Map<String,InfoHolder>  list = new HashMap<String,InfoHolder>();
        Cursor cur = dm.database().rawQuery("SELECT * FROM "+table+" WHERE DATE(?) between DATE(valid_from) AND  DATE(valid_to) and "+kond,kondValue) ;
        if (cur.moveToFirst()){
            do{
                String prd = cur.getString(cur.getColumnIndex("product_id"));
                String uom = cur.getString(cur.getColumnIndex("uom"));

                ih = new InfoHolder();
                ih.product_id = prd;
                ih.uom = uom;
                ih.discount = cur.getDouble(cur.getColumnIndex("discount"));
                ih.validFrom = cur.getString(cur.getColumnIndex("valid_from"));
                ih.validTo = cur.getString(cur.getColumnIndex("valid_to"));
                ih.fromQty= cur.getInt(cur.getColumnIndex("from_qty"));
                ih.toQty = cur.getInt(cur.getColumnIndex("to_qty"));
                ih.fromValue = cur.getDouble(cur.getColumnIndex("from_value"));
                ih.toValue = cur.getDouble(cur.getColumnIndex("to_value"));
                ih.isQty = cur.getString(cur.getColumnIndex("is_qty"));
                ih.description = table;
                String key = ih.product_id + ih.uom + ih.isQty;
                if (!list.containsKey(key)) {
                    list.put(key, ih);
                }
            }while(cur.moveToNext());
        }
        cur.close();
        return list;
    }


    private Map<String,InfoHolder> discountTableInvoice(String table,String kond,String []kondValue){
        InfoHolder ih = null;
        DBManager dm = DBManager.getInstance(ctx);
        Map<String,InfoHolder>  list = new HashMap<String,InfoHolder>();
        Cursor cur = dm.database().rawQuery("SELECT * FROM "+table+" WHERE DATE(?) between DATE(valid_from) AND  DATE(valid_to) and "+kond,kondValue) ;
        if (cur.moveToFirst()){
            do{

                ih = new InfoHolder();
                ih.product_id = "000000";
                ih.uom = "-";
                ih.isQty= "0";

                ih.discount = cur.getDouble(cur.getColumnIndex("discount"));
                ih.validFrom = cur.getString(cur.getColumnIndex("valid_from"));
                ih.validTo = cur.getString(cur.getColumnIndex("valid_to"));
                ih.fromValue = cur.getDouble(cur.getColumnIndex("from_value"));
                ih.toValue = cur.getDouble(cur.getColumnIndex("to_value"));
                ih.description = table;
                String key = ih.product_id + ih.uom + ih.isQty;
                if (!list.containsKey(key)) {
                    list.put(key, ih);
                }
            }while(cur.moveToNext());
        }
        cur.close();
        return list;
    }
    public List<InfoHolder> getListDiscountReg(){
        Map<String,InfoHolder>  discount_outlet = discountTable("sls_discount_reg_outlet", "outlet_id=?", new String[]{Helper.getCurrentDate(), outlet_id});
        Map<String,InfoHolder>  discount_channel = discountTable("sls_discount_reg_channel", "channel=?", new String[]{Helper.getCurrentDate(), channel});
        Map<String,InfoHolder>  discount_channel_inv = discountTableInvoice("sls_discount_reg_channel_india", "channel=?", new String[]{Helper.getCurrentDate(), channel});
        Map<String,InfoHolder>  discount_division = discountTable("sls_discount_reg_channel_division", "channel=?", new String[]{Helper.getCurrentDate(), channel});

        for(InfoHolder listChannel:discount_channel.values()){
            if (!discount_outlet.containsKey(listChannel.product_id+listChannel.uom+listChannel.isQty)){
                discount_outlet.put(listChannel.product_id+listChannel.uom+listChannel.isQty,listChannel);
            }
        }

        for(InfoHolder listChannel:discount_channel_inv.values()){
            if (!discount_outlet.containsKey(listChannel.product_id+listChannel.uom+listChannel.isQty)){
                discount_outlet.put(listChannel.product_id+listChannel.uom+listChannel.isQty,listChannel);
            }
        }


        for(InfoHolder listDivision:discount_division.values()){
            if (!discount_outlet.containsKey(listDivision.product_id+listDivision.uom+listDivision.isQty)){
                discount_outlet.put(listDivision.product_id+listDivision.uom+listDivision.isQty,listDivision);
            }
        }

        return new ArrayList<InfoHolder>(discount_outlet.values());

    }

    public List<InfoHolder> getListDiscountExt(){
        Map<String,InfoHolder>  discount_outlet = discountTable("sls_discount_ext_outlet", "outlet_id=?", new String[]{Helper.getCurrentDate(), outlet_id});
        Map<String,InfoHolder>  discount_channel = discountTable("sls_discount_ext_channel", "channel=?", new String[]{Helper.getCurrentDate(), channel});
        Map<String,InfoHolder>  discount_channel_inv = discountTableInvoice("sls_discount_ext_channel_india", "channel=?", new String[]{Helper.getCurrentDate(), channel});
        Map<String,InfoHolder>  discount_division = discountTable("sls_discount_ext_channel_division", "channel=?", new String[]{Helper.getCurrentDate(), channel});




        for(InfoHolder listChannel:discount_channel.values()){
            if (!discount_outlet.containsKey(listChannel.product_id+listChannel.uom+listChannel.isQty)){
                discount_outlet.put(listChannel.product_id+listChannel.uom+listChannel.isQty,listChannel);
            }
        }

        for(InfoHolder listChannel:discount_channel_inv.values()){
            if (!discount_outlet.containsKey(listChannel.product_id+listChannel.uom+listChannel.isQty)){
                discount_outlet.put(listChannel.product_id+listChannel.uom+listChannel.isQty,listChannel);
            }
        }

        for(InfoHolder listDivision:discount_division.values()){
            if (!discount_outlet.containsKey(listDivision.product_id+listDivision.uom+listDivision.isQty)){
                discount_outlet.put(listDivision.product_id+listDivision.uom+listDivision.isQty,listDivision);
            }
        }

        return new ArrayList<InfoHolder>(discount_outlet.values());

    }


    public List<InfoHolder> getListDiscountSpc(){
        Map<String,InfoHolder>  discount_outlet = discountTable("sls_discount_spc_outlet", "outlet_id=?", new String[]{Helper.getCurrentDate(), outlet_id});
        Map<String,InfoHolder>  discount_channel = discountTable("sls_discount_spc_channel", "channel=?", new String[]{Helper.getCurrentDate(), channel});
        Map<String,InfoHolder>  discount_division = discountTable("sls_discount_spc_channel_division", "channel=?", new String[]{Helper.getCurrentDate(), channel});

        for(InfoHolder listChannel:discount_channel.values()){
            if (!discount_outlet.containsKey(listChannel.product_id+listChannel.uom+listChannel.isQty)){
                discount_outlet.put(listChannel.product_id+listChannel.uom+listChannel.isQty,listChannel);
            }
        }

        for(InfoHolder listDivision:discount_division.values()){
            if (!discount_outlet.containsKey(listDivision.product_id+listDivision.uom+listDivision.isQty)){
                discount_outlet.put(listDivision.product_id+listDivision.uom+listDivision.isQty,listDivision);
            }
        }

        return new ArrayList<InfoHolder>(discount_outlet.values());

    }


    public List<InfoHolder> getListDiscountChannelIPT(){
        InfoHolder ih = null;
        DBManager dm = DBManager.getInstance(ctx);
        Map<String,InfoHolder>  list = new HashMap<String,InfoHolder>();
        Cursor cur = dm.database().rawQuery("SELECT * FROM sls_discount_ext_channel_ipt WHERE DATE(?) between DATE(valid_from) AND  DATE(valid_to) and channel=?",new String[]{Helper.getCurrentDate(),channel}) ;
        if (cur.moveToFirst()){
            do{
                String uom = cur.getString(cur.getColumnIndex("uom"));

                ih = new InfoHolder();
                ih.uom = uom;

                ih.validFrom = cur.getString(cur.getColumnIndex("valid_from"));
                ih.validTo = cur.getString(cur.getColumnIndex("valid_to"));
                ih.minQty = cur.getInt(cur.getColumnIndex("min_qty"));
                ih.minValue = cur.getDouble(cur.getColumnIndex("min_value"));
                ih.isQty = cur.getString(cur.getColumnIndex("is_qty"));
                ih.ipt = cur.getInt(cur.getColumnIndex("ipt"));
                ih.isPercent = cur.getString(cur.getColumnIndex("is_percent"));

                if (ih.isPercent.equals("1"))
                    ih.discount = cur.getDouble(cur.getColumnIndex("discount"));
                else
                    ih.discount = cur.getDouble(cur.getColumnIndex("value_ex"));

                ih.description = "sls_discount_ext_channel_ipt";
                String key =  ih.isPercent+String.valueOf(ih.ipt);
                if (!list.containsKey(key)) {
                    list.put(key, ih);
                }
            }while(cur.moveToNext());
        }
        cur.close();
        return new ArrayList<InfoHolder>(list.values());

    }


    public List<InfoHolder> getListSpecDiscountChannelIPT(){
        InfoHolder ih = null;
        DBManager dm = DBManager.getInstance(ctx);
        Map<String,InfoHolder>  list = new HashMap<String,InfoHolder>();
        Cursor cur = dm.database().rawQuery("SELECT * FROM sls_discount_spc_channel_ipt WHERE DATE(?) between DATE(valid_from) AND  DATE(valid_to) and channel=?",new String[]{Helper.getCurrentDate(),channel}) ;
        if (cur.moveToFirst()){
            do{
                String uom = cur.getString(cur.getColumnIndex("uom"));

                ih = new InfoHolder();
                ih.uom = uom;

                ih.validFrom = cur.getString(cur.getColumnIndex("valid_from"));
                ih.validTo = cur.getString(cur.getColumnIndex("valid_to"));
                ih.minQty = cur.getInt(cur.getColumnIndex("min_qty"));
                ih.minValue = cur.getDouble(cur.getColumnIndex("min_value"));
                ih.isQty = cur.getString(cur.getColumnIndex("is_qty"));
                ih.ipt = cur.getInt(cur.getColumnIndex("ipt"));
                ih.isPercent = cur.getString(cur.getColumnIndex("is_percent"));

                if (ih.isPercent.equals("1"))
                    ih.discount = cur.getDouble(cur.getColumnIndex("discount"));
                else
                    ih.discount = cur.getDouble(cur.getColumnIndex("value_ex"));

                ih.description = "sls_discount_spc_channel_ipt";
                String key =  ih.isPercent+String.valueOf(ih.ipt);
                if (!list.containsKey(key)) {
                    list.put(key, ih);
                }
            }while(cur.moveToNext());
        }
        cur.close();
        return new ArrayList<InfoHolder>(list.values());

    }
    private Map<String,InfoHolder> freeGoodTable(String table,String kond,String []kondValue){
        InfoHolder ih = null;
        DBManager dm = DBManager.getInstance(ctx);
        Map<String,InfoHolder>  list = new HashMap<String,InfoHolder>();
        Cursor cur = dm.database().rawQuery("SELECT * FROM "+table+" WHERE DATE(?) between DATE(valid_from) AND  DATE(valid_to) and "+kond,kondValue) ;
        if (cur.moveToFirst()){
            do{
                String prd = cur.getString(cur.getColumnIndex("product_id"));
                String uom = cur.getString(cur.getColumnIndex("uom"));

                ih = new InfoHolder();
                ih.product_id = prd;
                ih.uom = uom;
                ih.validFrom = cur.getString(cur.getColumnIndex("valid_from"));
                ih.validTo = cur.getString(cur.getColumnIndex("valid_to"));
                ih.minQty = cur.getInt(cur.getColumnIndex("min_qty"));
                ih.buyQty = cur.getInt(cur.getColumnIndex("buy_qty"));
                ih.freeQty = cur.getInt(cur.getColumnIndex("free_qty"));
                ih.freeUom = cur.getString(cur.getColumnIndex("free_uom"));
                ih.freeSku = cur.getString(cur.getColumnIndex("product_free"));
                ih.description = table;
                String key = ih.product_id + ih.uom + ih.multiple+ih.proportional;
                if (!list.containsKey(key)) {
                    list.put(key, ih);
                }
            }while(cur.moveToNext());
        }
        cur.close();
        return list;
    }


    public List<InfoHolder> getListFreeGood(){
        Map<String,InfoHolder>  free_outlet = freeGoodTable("sls_free_good_customer", "outlet_id=?", new String[]{Helper.getCurrentDate(), outlet_id});
        Map<String,InfoHolder>  free_channel = freeGoodTable("sls_free_good_channel", "channel=?", new String[]{Helper.getCurrentDate(), channel});
        Map<String,InfoHolder>  free_zone = freeGoodTable("sls_free_good_zone", "zone=?", new String[]{Helper.getCurrentDate(), zone});

        for(InfoHolder listChannel:free_channel.values()){
            if (!free_outlet.containsKey(listChannel.product_id+listChannel.uom+listChannel.multiple+listChannel.proportional)){
                free_outlet.put(listChannel.product_id+listChannel.uom+listChannel.isQty+listChannel.multiple+listChannel.proportional,listChannel);
            }
        }

        for(InfoHolder listZone:free_zone.values()){
            if (!free_outlet.containsKey(listZone.product_id+listZone.uom+listZone.isQty+listZone.multiple+listZone.proportional)){
                free_outlet.put(listZone.product_id+listZone.uom+listZone.isQty+listZone.multiple+listZone.proportional,listZone);
            }
        }

        return new ArrayList<InfoHolder>(free_outlet.values());


    }


    class InfoHolder{
        public String product_id;
        public String uom;
        public double price;
        public double discount;
        public String isQty;
        public String validFrom;
        public String validTo;
        public double fromValue;
        public double toValue;
        public int ipt;
        public int minQty;
        public double minValue;
        public int buyQty;
        public int freeQty;
        public int fromQty;
        public int toQty;
        public String freeUom;
        public String freeSku;
        public String isPercent;
        public String description;
        public String multiple;
        public String proportional;
    }

}
