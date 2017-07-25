package com.ksni.roots.ngsales.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by #roots on 24/08/2015.
 */
public class DBManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "enerlife.db";
    private static final int DB_VER = 31304;

    private final Context myContext;
    private static DBManager mInstance;
    private static SQLiteDatabase myWritableDb;

    private DBManager(Context context) {

        super(context, DB_NAME, null, DB_VER);
        this.myContext = context;
    }

        public boolean isFieldExist(String tableName, String fieldName) {
                boolean isExist = true;
                Cursor res = this.getWritableDatabase().rawQuery("PRAGMA table_info(" + tableName+")",null);
                int value = res.getColumnIndex(fieldName);

                if(value == -1) isExist = false;

                return isExist;
        }

    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBManager(context);
        }
        return mInstance;
    }

    public SQLiteDatabase database() {
        if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
            myWritableDb = this.getWritableDatabase();
        }

        return myWritableDb;
    }

    @Override
    public void close() {
        super.close();
        if (myWritableDb != null) {
            myWritableDb.close();
            myWritableDb = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String TABLE_QUEUE = "CREATE TABLE queue ( " +
                    "`id`	    INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                    "`key`	            INTEGER, " +
                    "`description`	    TEXT," +
                    "`value`	        TEXT, " +
                    "`data`	            TEXT, " +
                    "`time`	            TEXT, " +
                    "`status`	    INTEGER DEFAULT 0" +
                    ")";

        String TABLE_FREE_GOOD_BY_CUSTOMER = "CREATE TABLE sls_free_good_customer ( " +
                "`outlet_id`	          TEXT NOT NULL, " +
                "`product_id`	          TEXT NOT NULL, " +
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	      INTEGER, " +
                "`min_qty`	      INTEGER DEFAULT 0, " +
                "`buy_qty`	      INTEGER DEFAULT 0, " +
                "`uom`	      TEXT, " +
                "`product_free`	      TEXT, " +
                "`free_qty`	      INTEGER DEFAULT 0, " +
                "`multiple`	      TEXT, " +
                "`proportional`	      TEXT, " +
                "`free_uom`	      TEXT, " +
                " PRIMARY KEY(outlet_id,product_id,valid_from,valid_to,id)"+
                ")";

        String TABLE_FREE_GOOD_BY_ZONE = "CREATE TABLE sls_free_good_zone ( " +
                "`zone`	          TEXT NOT NULL, " +
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`product_id`	  TEXT NOT NULL, " +
                "`id`	          INTEGER, " +
                "`min_qty`	      INTEGER DEFAULT 0, " +
                "`buy_qty`	      INTEGER DEFAULT 0, " +
                "`uom`	          TEXT, " +
                "`product_free`	  TEXT, " +
                "`free_qty`	      INTEGER DEFAULT 0, " +
                "`multiple`	      TEXT, " +
                "`proportional`	  TEXT, " +
                "`free_uom`	      TEXT, " +
                " PRIMARY KEY(zone,valid_from,valid_to,product_id,id)"+
                ")";

        String TABLE_FREE_GOOD_BY_CHANNEL = "CREATE TABLE sls_free_good_channel ( " +
                "`channel`	            TEXT NOT NULL, " +
                "`product_id`	        TEXT NOT NULL, " +
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	                INTEGER, " +
                "`min_qty`	            INTEGER DEFAULT 0, " +
                "`buy_qty`	            INTEGER DEFAULT 0, " +
                "`uom`	                TEXT, " +
                "`product_free`	        TEXT, " +
                "`free_qty`	            INTEGER DEFAULT 0, " +
                "`multiple`	            TEXT, " +
                "`proportional`	        TEXT, " +
                "`free_uom`	            TEXT, " +
                " PRIMARY KEY(channel,product_id,valid_from,valid_to,id)"+
                ")";

        String TABLE_PLAN_STATUS = "CREATE TABLE sls_plan_status ( " +
                "`id`	          TEXT NOT NULL, " +
                "`week`	          TEXT NOT NULL, " +
                "`date`	          TEXT NOT NULL, " +
                "`sls_id`	          TEXT NOT NULL, " +
                "`outlet_id`	  TEXT NOT NULL, " +
                "`status`	      TEXT, " +
                "`duration`	          LONG DEFAULT 0, " +
                "`last_pause`	     TEXT, " +
                "`last_resume`	     TEXT, " +
                "`call_status`	      TEXT DEFAULT '1', " +
                "`credit_limit`	            REAL DEFAULT 0, " +
                "`squence`	      INTEGER, " +
                "`notes`	      TEXT, " +
                "`reason_unroute`	      TEXT, " + // new unroute reason
                "`order_id`	      LONG DEFAULT -1, " +
                "`return_id`	  LONG DEFAULT -1, " +
                "`route`	      TEXT, " +
                "`reason_no_barcode`	  TEXT, " +
                "`start_time`	  TEXT, " +
                "`end_time`	      TEXT, " +
                " PRIMARY KEY(id,outlet_id,sls_id,date)"+
                ")";

        // TEMPLATE
        String TABLE_SKU_TEMPLATE = "CREATE TABLE sls_sku_template ("+
                "`id`	TEXT,"+
                "`outlet_id`	TEXT,"+
                "`product_id`	TEXT,"+
                "`qty_last`	    REAL DEFAULT 0,"+
                "`uom`	TEXT,"+
                " PRIMARY KEY(id,outlet_id,product_id)"+
                ")";

            String TABLE_VAN = "CREATE TABLE sls_van ( " +
                    "`transaction_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "`transaction_date`	        TEXT, " +
                    "`locked`	      INTEGER DEFAULT 1, " +
                    "`status`	      INTEGER DEFAULT 0, " +
                    "`type_transaction`	        INTEGER, " +
                    "`sls_id`	                TEXT, " +
                    "`reference`	            TEXT " +
                    ")";

            String TABLE_VAN_STOCK = "CREATE TABLE sls_van_stock ( " +
                    "`period`	        TEXT, " +
                    "`product_id`	    TEXT, " +
                    "`description`	    TEXT, " +
                    "`brand`	    TEXT, " +
                    "`qty`	            LONG DEFAULT 0, " +
                    "`qty_bs`	        LONG DEFAULT 0, " +
                    "`qty_ret`	        LONG DEFAULT 0, " +
                    "`small_uom`	    TEXT, " +
                    "`medium_uom`	    TEXT, " +
                    "`large_uom`	    TEXT, " +
                    "`medium_to_small`	INTEGER, " +
                    "`large_to_small`   INTEGER, " +
                    " PRIMARY KEY(period,product_id))";


            String TABLE_VAN_ITEMS = "CREATE TABLE sls_van_item ( " +
                    "`transaction_id`	INTEGER NOT NULL, " +
                    "`type_transaction`	        INTEGER, " +
                    "`product_id`	    TEXT NOT NULL, " +
                    "`item`	            INTEGER NOT NULL, " +
                    "`description`	    TEXT, " +
                    "`qty`	            LONG DEFAULT 0, " +
                    "`qty_pcs`	        LONG DEFAULT 0, " +
                    "`uom`	            TEXT, " +
                    "`brand`	        TEXT, " +
                    "`small_uom`	    TEXT, " +
                    "`medium_uom`	    TEXT, " +
                    "`large_uom`	    TEXT, " +
                    "`medium_to_small`	INTEGER, " +
                    "`large_to_small`   INTEGER, " +
                    " PRIMARY KEY(transaction_id,product_id,item)"+
                    ")";




        String TABLE_ORDER = "CREATE TABLE sls_order ( " +
                "`order_id`	      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "`order_date`	  TEXT, " +
                "`delivery_date`	  TEXT, " +
                "`outlet_id`	  TEXT, " +
                "`docking`	  INTEGER DEFAULT 0, " + // 0 = order, 1  retur
                "`order_type`	  INTEGER, " + // 0 = order, 1  retur
                "`salesman_type`  INTEGER, " + // 0 = TO, 1  CANVAS
                "`imei`	          TEXT, " +
                "`locked`	      INTEGER DEFAULT 1, " +
                "`delivered`	  INTEGER DEFAULT 0, " +
                "`sls_id`	      TEXT, " +
                "`outlet_top_id`	      TEXT, " +
                "`notes`	      TEXT, " +
                "`signature`	  TEXT, " +
                "`pic_outlet`	  TEXT, " +
                "`status`	      INTEGER DEFAULT 0, " +
                "`ppn`	          REAL DEFAULT 0, " +
                "`duration`	      LONG DEFAULT 0, " +
                "`latitude`	      TEXT , " +
                "`longitude`	  TEXT , " +
                "`sub_total`	  REAL DEFAULT 0, " +
                "`total_discount` REAL DEFAULT 0, " +
                "`grand_total`	  REAL DEFAULT 0, " +
            "`reason_unroute`	      TEXT, " +
                "`reason`	      TEXT, " +
                "`reason_nobarcode`	      TEXT, " +
                "`start_call`	  TEXT DEFAULT NULL, " +
                "`end_call`	      TEXT DEFAULT NULL, " +
                "`week`	          TEXT, " +
                "`year`	          INTEGER, " +
                "`period`	      INTEGER, " +
                "`week_num`	      INTEGER, " +
                "`date_create`	  TEXT " +
                ")";

        String TABLE_ORDER_ITEMS = "CREATE TABLE sls_order_item ( " +
                "`order_id`	        INTEGER NOT NULL, " +
                "`product_id`	    TEXT NOT NULL, " +
                "`item`	            INTEGER NOT NULL, " +
                "`order_type`	  INTEGER, " + // 0 = order, 1  retur
                "`description`	    TEXT, " +
                "`qty`	            REAL DEFAULT 0, " +
                "`qty_pcs`	        REAL DEFAULT 0, " +
                "`uom`	            TEXT, " +
                "`brand`	        TEXT, " +
                "`reason_return`	TEXT, " +
                "`price`	        REAL DEFAULT 0, " +
                "`total_gross`	    REAL DEFAULT 0, " +
                "`regular_discount`	REAL DEFAULT 0, " +
                "`extra_discount`	REAL DEFAULT 0, " +
                "`special_discount`	REAL DEFAULT 0, " +
                "`total_net`	    REAL DEFAULT 0, " +
                "`last_stock`	    REAL DEFAULT 0, " +
                "`last_stock_uom`	TEXT, " +
                "`suggest_qty`	    REAL DEFAULT 0, " +
                "`suggest_uom`	    TEXT, " +
                "`is_ipt`	        TEXT, " +
                "`is_ipt_percent`	TEXT, " +
                "`current_stock`	    REAL DEFAULT 0, " +
                "`current_stock_uom`	TEXT, " +
                "`item_type`	TEXT, " +
                "`ref_item`	INT DEFAULT 0, " +
                "`small_uom`	            TEXT, " +
                "`medium_uom`	            TEXT, " +
                "`large_uom`	            TEXT, " +
                "`medium_to_small`	INTEGER, " +
                "`large_to_small`INTEGER, " +
                " PRIMARY KEY(ord" +
                "er_id,product_id,item)"+
                ")";


        String TABLE_CUSTOMER = "CREATE TABLE sls_customer ( " +
                "`outlet_id`	            TEXT NOT NULL PRIMARY KEY, " +
                "`outlet_name`	            TEXT, " +
                "`outlet_name_alias`	    TEXT, " +
                "`title`	                TEXT, " +
                "`address`	                TEXT, " +
                "`barcode_number`	        TEXT, " +
                "`contact_person`	        TEXT, " +
                "`city`	                    TEXT, " +
                "`phone`	                TEXT, " +
                "`outlet_top_id`	                TEXT, " +
                "`outletpricing_group`	                TEXT, " +
                "`group_channel`	        TEXT, " +
                "`channel`	                TEXT, " +
                "`zone`	                    TEXT, " +
                "`notes`	                TEXT, " +
                "`region`	                TEXT, " +
                "`classification`	        TEXT, " +
                "`territory`	            TEXT, " +
                "`district`	                TEXT, " +
                "`picture`	                TEXT, " +
                "`status`	                TEXT, " +
                "`outlet_group`	            TEXT, " +
                "`delivery_day`	            INTEGER DEFAULT 0, " +
                "`credit_limit`	            REAL DEFAULT 0, " +
                "`balance`	                REAL DEFAULT 0, " +
                "`latitude`	                REAL DEFAULT 0, " +
                "`longitude`	            REAL DEFAULT 0 " +
                ")";


        String TABLE_TARGET = "CREATE TABLE sls_target ( " +
                "`id`	                    TEXT NOT NULL, " +
                "`sls_id`	                TEXT NOT NULL, " +
                "`year`	                    INTEGER NOT NULL, " +
                "`period`	                INTEGER NOT NULL, " +
                "`product_id`	            TEXT NOT NULL, " +
                "`target_qty`	            REAL default 0, " +
                "`target_value`	            REAL default 0, " +
                "`actual_qty`	            REAL default 0, " +
                "`actual_value`	            REAL default 0, " +
                "`target_ec`	            INTEGER default 0, " +
                "`actual_ec`	            INTEGER default 0, " +
                "`target_call`	            INTEGER default 0, " +
                "`actual_call`	            INTEGER default 0, " +
                "`target_ipt`	            INTEGER default 0, " +
                "`actual_ipt`	            INTEGER default 0, " +
                "`achiev_qty`	            REAL default 0, " +
                "`achiev_value`	            REAL default 0, " +
                " PRIMARY KEY(sls_id,id,year,period,product_id)"+
                ")";

            String TABLE_TARGETX = "CREATE TABLE sls_targetx ( " +
                    "`sls_id`	                TEXT NOT NULL, " +
                    "`year`	                    INTEGER NOT NULL, " +
                    "`period`	                INTEGER NOT NULL, " +
                    "`target_call`	            INTEGER, " +
                    "`target_ec`	            INTEGER, " +
                    "`actual_call`	            INTEGER, " +
                    "`actual_ec`	            INTEGER, " +
                    " PRIMARY KEY(sls_id,year,period)"+
                    ")";



            String TABLE_COMPETITOR = "CREATE TABLE sls_competitor ("+
                "`competitor`	            TEXT NOT NULL PRIMARY KEY,"+
                "`description`	    TEXT"+
                ")";

            String TABLE_COMPETITOR_ENTRY = "CREATE TABLE sls_competitor_entry ("+
                    "`id`	                    INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "`sls_id`	                TEXT,"+
                    "`outlet_id`	            TEXT,"+
                    "`competitor`	            TEXT,"+
                    "`activity`	                TEXT,"+
                    "`product`	                TEXT,"+
                    "`cost`	                    TEXT,"+
                    "`date_visit`	            TEXT,"+
                    "`times`	                TEXT,"+
                    "`notes`	                TEXT DEFAULT '0',"+
                    "`sent`	                    TEXT"+ ")";


        String TABLE_WEEK = "CREATE TABLE sls_week ("+
                "`week`	            TEXT NOT NULL PRIMARY KEY,"+
                "`week_name`	    TEXT,"+
                "`year_week`	    INTEGER,"+
                "`month_week`	    INTEGER, " +
                "`week_int`	        INTEGER,"+
                "`from_date`	    TEXT,"+
                "`to_date`          TEXT"+
                ")";

        String TABLE_ROUTE_ASSIGN = "CREATE TABLE sls_route_assign ("+
                "`outlet_id`	    TEXT,"+
                "`sls_id`	        TEXT,"+
                "`day`	            INTEGER, " +
                "`w1`	            TEXT,"+
                "`w2`	            TEXT,"+
                "`w3`               TEXT"+
                "`route`            TEXT"+
                "`squence`          INTEGER,"+
                " PRIMARY KEY(outlet_id,sls_id)"+
                ")";


        String TABLE_PRODUCT = "CREATE TABLE sls_product ("+
                "`product_id`	            TEXT NOT NULL PRIMARY KEY,"+
                "`product_name`	            TEXT,"+
                "`product_name_alias`	    TEXT,"+
                "`division`	                TEXT, " +
                "`base_uom`	                TEXT,"+
                "`status`	                TEXT,"+
                "`stock`	                LONG DEFAULT 0,"+
                "`is_pareto`                TEXT,"+
                "`product_type`	            TEXT,"+
                "`product_brands`            TEXT,"+
                "`product_category`	        TEXT,"+
                "`price`	                REAL DEFAULT 0,"+
                "`small_uom`	            TEXT,"+
                "`medium_uom`	            TEXT,"+
                "`large_uom`	            TEXT,"+
                "`large_to_small`	        TEXT DEFAULT 0,"+
                "`medium_to_small`	    TEXT DEFAULT 0"+
                ")";


        String TABLE_SETTINGS = "CREATE TABLE settings ("+
                "`id`	                INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                "`salesman`	            TEXT,"+
                "`salesman_name`	    TEXT,"+
                "`salesman_type`    	INTEGER,"+
                "`server`	            TEXT,"+
                "`multi_dist`   	    TEXT,"+
                "`last_customer`    	TEXT,"+
                "`last_id`	            LONG DEFAULT -1,"+
                "`work_date`    	    TEXT,"+
                "`week`	                TEXT,"+
                "`barcode_number`   	TEXT,"+
                "`start_odometer`   	REAL DEFAULT 0,"+
                "`end_odometer`	        REAL DEFAULT 0,"+
                "`start_call`	        TEXT DEFAULT NULL,"+
                "`work_start`   	    TEXT,"+
                "`start_date`	        TEXT,"+
                "`end_date`	            TEXT,"+
                "`work_end`	            TEXT,"+
                "`last_sync`    	    TEXT,"+
                "`year`	                INTEGER DEFAULT 0, " +
                "`period`               INTEGER DEFAULT 0, " +
                "`last_login`   	    TEXT,"+
                "`last_logout`  	    TEXT,"+
                "`info`	                TEXT,"+
                "`company`	            TEXT,"+
                "`branch`               INTEGER DEFAULT 0,"+
                "`zone`                 TEXT,"+
                "`is_login`	            INTEGER DEFAULT 0,"+
                "`last_sync_master`	    TEXT,"+
                "`last_sync_call_plan`	TEXT,"+
                "`start_latitude`	    REAL DEFAULT 0,"+
                "`start_longitude`	    REAL DEFAULT 0,"+
                "`end_latitude`	        REAL,"+
                "`end_longitude`	    REAL"+
                ")";


        // PRICING
        String TABLE_PRICING_BY_OUTLET = "CREATE TABLE sls_pricing_by_outlet ("+
                "`outlet_id`	TEXT,"+
                "`product_id`	TEXT,"+
                "`price`	    REAL DEFAULT 0,"+
                "`from_value`	    REAL DEFAULT 0,"+
                "`to_value`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                "`uom`	        TEXT,"+
                " PRIMARY KEY(outlet_id,product_id,valid_from,valid_to,id)"+
                ")";

            String TABLE_PRICING_BY_CHAIN = "CREATE TABLE sls_pricing_by_chain ("+
                    "`chain`	    TEXT,"+
                    "`product_id`	TEXT,"+
                    "`id`	        INTEGER,"+
                    "`price`	    REAL DEFAULT 0,"+
                    "`from_value`	    REAL DEFAULT 0,"+
                    "`to_value`	    REAL DEFAULT 0,"+
                    "`valid_from`	TEXT,"+
                    "`valid_to`	    TEXT,"+
                    "`uom`	        TEXT,"+
                    " PRIMARY KEY(chain,product_id,valid_from,valid_to,id)"+
                    ")";

        String TABLE_PRICING_BY_CHANNEL = "CREATE TABLE sls_pricing_by_channel ("+
                "`channel`	    TEXT,"+
                "`product_id`	TEXT,"+
                "`id`	        INTEGER,"+
                "`price`	    REAL DEFAULT 0,"+
                "`from_value`	    REAL DEFAULT 0,"+
                "`to_value`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`uom`	        TEXT,"+
                " PRIMARY KEY(channel,product_id,valid_from,valid_to,id)"+
                ")";


        String TABLE_PRICING_BY_ZONE = "CREATE TABLE sls_pricing_by_zone ("+
                "`zone`	        TEXT,"+
                "`product_id`	TEXT,"+
                "`price`	    REAL DEFAULT 0,"+
                "`from_value`	    REAL DEFAULT 0,"+
                "`to_value`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                "`uom`	        TEXT,"+
                " PRIMARY KEY(zone,product_id,valid_from,valid_to,id)"+
                ")";

        // DISCOUNT REGULAR
        String TABLE_REG_DISCOUNT_BY_OUTLET = "CREATE TABLE sls_discount_reg_outlet ("+
                "`outlet_id`	TEXT,"+
                "`product_id`	TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(outlet_id,product_id,valid_from,valid_to,id)"+
                ")";

        String TABLE_REG_DISCOUNT_BY_CHANNEL = "CREATE TABLE sls_discount_reg_channel ("+
                "`channel`	    TEXT,"+
                "`product_id`	TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(channel,product_id,valid_from,valid_to,id)"+
                ")";

            String TABLE_REG_DISCOUNT_BY_CHANNEL_INDIA = "CREATE TABLE sls_discount_reg_channel_india ("+
                    "`channel`	    TEXT,"+
                    "`zone`	    TEXT,"+
                    "`from_value`	REAL,"+
                    "`to_value`	    REAL,"+
                    "`discount`	    REAL DEFAULT 0,"+
                    "`valid_from`	TEXT,"+
                    "`valid_to`	    TEXT,"+
                    "`id`	        INTEGER,"+
                    " PRIMARY KEY(channel,zone,valid_from,valid_to,id)"+
                    ")";


            String TABLE_OUTLET_TOP = "CREATE TABLE sls_top ("+
                    "`top_id`	    TEXT,"+
                    "`description`	TEXT,"+
                    " PRIMARY KEY(top_id)"+
                    ")";
            String TABLE_EXT_DISCOUNT_BY_CHANNEL_INDIA = "CREATE TABLE sls_discount_ext_channel_india ("+
                    "`channel`	    TEXT,"+
                    "`zone`	    TEXT,"+
                    "`from_value`	REAL,"+
                    "`to_value`	    REAL,"+
                    "`discount`	    REAL DEFAULT 0,"+
                    "`valid_from`	TEXT,"+
                    "`valid_to`	    TEXT,"+
                    "`id`	        INTEGER,"+
                    " PRIMARY KEY(channel,zone,valid_from,valid_to,id)"+
                    ")";

            String TABLE_SPC_DISCOUNT_BY_CHANNEL_INDIA = "CREATE TABLE sls_discount_spc_channel_india ("+
                    "`channel`	    TEXT,"+
                    "`zone`	    TEXT,"+
                    "`from_value`	REAL,"+
                    "`to_value`	    REAL,"+
                    "`discount`	    REAL DEFAULT 0,"+
                    "`valid_from`	TEXT,"+
                    "`valid_to`	    TEXT,"+
                    "`id`	        INTEGER,"+
                    " PRIMARY KEY(channel,zone,valid_from,valid_to,id)"+
                    ")";


        String TABLE_REG_DISCOUNT_BY_CHANNEL_BY_DIVISION = "CREATE TABLE sls_discount_reg_channel_division ("+
                "`channel`	    TEXT,"+
                "`division`	    TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(channel,division,valid_from,valid_to,id)"+
                ")";

        // DISCOUNT EXTRA
        String TABLE_EXTRA_DISCOUNT_BY_OUTLET = "CREATE TABLE sls_discount_ext_outlet ("+
                "`outlet_id`	TEXT,"+
                "`product_id`	TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(outlet_id,product_id,valid_from,valid_to,id)"+
                ")";

        String TABLE_EXTRA_DISCOUNT_BY_CHANNEL = "CREATE TABLE sls_discount_ext_channel ("+
                "`channel`	    TEXT,"+
                "`product_id`	TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(channel,product_id,valid_from,valid_to,id)"+
                ")";

        String TABLE_EXTRA_DISCOUNT_BY_CHANNEL_IPT = "CREATE TABLE sls_discount_ext_channel_ipt ("+
                "`channel`	    TEXT,"+
                "`min_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`min_value`	REAL,"+
                "`ipt`	        INTEGER,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`value_ex`	    REAL DEFAULT 0,"+
                "`is_percent`	TEXT,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(channel,valid_from,valid_to,id)"+
                ")";


            String TABLE_SPEC_DISCOUNT_BY_CHANNEL_IPT = "CREATE TABLE sls_discount_spc_channel_ipt ("+
                    "`channel`	    TEXT,"+
                    "`min_qty`	    INTEGER,"+
                    "`uom`	        TEXT,"+
                    "`is_qty`	    TEXT,"+
                    "`min_value`	REAL,"+
                    "`ipt`	        INTEGER,"+
                    "`discount`	    REAL DEFAULT 0,"+
                    "`value_ex`	    REAL DEFAULT 0,"+
                    "`is_percent`	TEXT,"+
                    "`valid_from`	TEXT,"+
                    "`valid_to`	    TEXT,"+
                    "`id`	        INTEGER,"+
                    " PRIMARY KEY(channel,valid_from,valid_to,id)"+
                    ")";

        String TABLE_EXTRA_DISCOUNT_BY_CHANNEL_BY_DIVISION = "CREATE TABLE sls_discount_ext_channel_division ("+
                "`channel`	    TEXT,"+
                "`division`	    TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(channel,division,valid_from,valid_to,id)"+
                ")";


        // DISCOUNT SPECIAL
        String TABLE_SPEC_DISCOUNT_BY_OUTLET = "CREATE TABLE sls_discount_spc_outlet ("+
                "`outlet_id`	TEXT,"+
                "`product_id`	TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(outlet_id,product_id,valid_from,valid_to,id)"+
                ")";

        String TABLE_SPEC_DISCOUNT_BY_CHANNEL = "CREATE TABLE sls_discount_spc_channel ("+
                "`channel`	    TEXT,"+
                "`product_id`	TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(channel,product_id,valid_from,valid_to,id)"+
                ")";

        String TABLE_SPEC_DISCOUNT_BY_CHANNEL_BY_DIVISION = "CREATE TABLE sls_discount_spc_channel_division ("+
                "`channel`	    TEXT,"+
                "`division`	    TEXT,"+
                "`from_qty`	    INTEGER,"+
                "`to_qty`	    INTEGER,"+
                "`uom`	        TEXT,"+
                "`is_qty`	    TEXT,"+
                "`from_value`	REAL,"+
                "`to_value`	    REAL,"+
                "`discount`	    REAL DEFAULT 0,"+
                "`valid_from`	TEXT,"+
                "`valid_to`	    TEXT,"+
                "`id`	        INTEGER,"+
                " PRIMARY KEY(channel,division,valid_from,valid_to,id)"+
                ")";



            String TABLE_REASON_NOBARCODE = "CREATE TABLE sls_reason_nobarcode ( " +
                "`reason_id`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";


        String TABLE_REASON_NOCALL = "CREATE TABLE sls_reason_nocall ( " +
                "`reason_id`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

            String TABLE_REASON_UNROUTE = "CREATE TABLE sls_reason_unroute ( " +
                    "`reason_id`	      TEXT NOT NULL PRIMARY KEY, " +
                    "`description`	  TEXT " +
                    ")";


            String TABLE_REASON_NOORDER = "CREATE TABLE sls_reason ( " +
                    "`reason_id`	      TEXT NOT NULL PRIMARY KEY, " +
                    "`description`	  TEXT " +
                    ")";


            String TABLE_REASON_RETURN = "CREATE TABLE sls_reason_return ( " +
                    "`reason_id`	      TEXT NOT NULL PRIMARY KEY, " +
                    "`description`	  TEXT " +
                    ")";



        /*MASTER*/



            String TABLE_MASTER_CONFIG = "CREATE TABLE sls_config ( " +
                    "`setting_key`	      TEXT NOT NULL PRIMARY KEY, " +
                    "`setting_value`	  TEXT, " +
                    "`description`	      TEXT " +
                    ")";


        String TABLE_MASTER_CHANNEL = "CREATE TABLE sls_channel ( " +
                "`channel`	      TEXT NOT NULL PRIMARY KEY, " +
                "`group_channel`	  TEXT, " +
                "`description`	  TEXT " +
                ")";

            String TABLE_MASTER_GROUP_CHANNEL = "CREATE TABLE sls_group_channel ( " +
                    "`group_channel`	      TEXT NOT NULL PRIMARY KEY, " +
                    "`description`	  TEXT " +
                    ")";

        String TABLE_MASTER_REGION = "CREATE TABLE sls_region ( " +
                "`region`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

        String TABLE_MASTER_ZONE = "CREATE TABLE sls_zone ( " +
                "`zone`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

        String TABLE_MASTER_CLASSIFICATION = "CREATE TABLE sls_classification ( " +
                "`classification`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

        String TABLE_MASTER_DISTRICT = "CREATE TABLE sls_district ( " +
                "`district`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

        String TABLE_MASTER_TERRITORY = "CREATE TABLE sls_territory ( " +
                "`territory`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

        String TABLE_MASTER_DIVISION = "CREATE TABLE sls_division( " +
                "`division`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

        String TABLE_MASTER_CATEGORY = "CREATE TABLE sls_product_category( " +
                "`category`	      TEXT NOT NULL PRIMARY KEY, " +
                "`description`	  TEXT " +
                ")";

        String TABLE_MASTER_BRANDS = "CREATE TABLE sls_brand( " +
                "`brand_id`	      TEXT NOT NULL PRIMARY KEY, " +
                "`brand_name`	  TEXT " +
                ")";


            String TABLE_MASTER_INFO = "CREATE TABLE sls_information( " +
                    "`id`	      INTEGER NOT NULL PRIMARY KEY, " +
                    "`valid_from`	  TEXT, " +
                    "`valid_to`	      TEXT, " +
                    "`content`	  TEXT " +
                    ")";

        /*ENDOFMASTER*/

            String TABLE_TRIGGER_INSERT_VAN_LOADING =   "CREATE TRIGGER insert_van_loading AFTER INSERT ON sls_van_item for each row " +
                    "BEGIN " +
                    "		update	sls_product SET stock=stock+NEW.qty_pcs WHERE product_id=new.product_id and new.type_transaction=0  ;  " +
                    "		update	sls_product SET stock=stock-NEW.qty_pcs WHERE product_id=new.product_id and new.type_transaction=1 ;  " +
                    "END;  ";


            String TABLE_TRIGGER_INSERT_SALES_ITEM="CREATE TRIGGER insert_sales_order_item AFTER INSERT ON sls_order_item for each row " +
                    "BEGIN     	 " +
                    "		update	sls_product SET stock=stock-NEW.qty_pcs WHERE  product_id=new.product_id and new.order_type = 0 ;  " +
                    "		update	sls_product SET stock=stock+NEW.qty_pcs WHERE  product_id=new.product_id and new.order_type = 1 ;  " +
                    "END;  ";

            String TABLE_TRIGGER_DELETE_SALES_ITEM  =      "CREATE TRIGGER delete_sales_order_item AFTER DELETE ON sls_order_item for each row " +
                    "BEGIN     	 " +
                    "		update	sls_product SET stock=stock+OLD.qty_pcs WHERE  product_id=OLD.product_id and OLD.order_type = 0;  " +
                    "		update	sls_product SET stock=stock-OLD.qty_pcs WHERE  product_id=OLD.product_id and OLD.order_type = 1;  " +
                    "END;  " ;


        //TRIGGER

        //END OF TRIGGER




        db.execSQL(TABLE_MASTER_INFO);
        db.execSQL(TABLE_CUSTOMER);
        db.execSQL(TABLE_PRODUCT);
        db.execSQL(TABLE_SETTINGS);
        db.execSQL(TABLE_TARGET);
        db.execSQL(TABLE_TARGETX);
        db.execSQL(TABLE_PRICING_BY_OUTLET);
        db.execSQL(TABLE_PRICING_BY_CHANNEL);
        db.execSQL(TABLE_PRICING_BY_CHAIN);
        db.execSQL(TABLE_PRICING_BY_ZONE);
        db.execSQL(TABLE_REG_DISCOUNT_BY_OUTLET);
        db.execSQL(TABLE_REG_DISCOUNT_BY_CHANNEL);
        db.execSQL(TABLE_REG_DISCOUNT_BY_CHANNEL_BY_DIVISION);
        db.execSQL(TABLE_EXTRA_DISCOUNT_BY_OUTLET);
        db.execSQL(TABLE_EXTRA_DISCOUNT_BY_CHANNEL);
        db.execSQL(TABLE_EXTRA_DISCOUNT_BY_CHANNEL_IPT);
        db.execSQL(TABLE_EXTRA_DISCOUNT_BY_CHANNEL_BY_DIVISION);
        db.execSQL(TABLE_SPEC_DISCOUNT_BY_OUTLET);
        db.execSQL(TABLE_SPEC_DISCOUNT_BY_CHANNEL);
        db.execSQL(TABLE_SPEC_DISCOUNT_BY_CHANNEL_BY_DIVISION);
        db.execSQL(TABLE_ORDER);

        db.execSQL(TABLE_VAN_STOCK);
        db.execSQL(TABLE_VAN);
        db.execSQL(TABLE_VAN_ITEMS);

        db.execSQL(TABLE_OUTLET_TOP);

        db.execSQL(TABLE_COMPETITOR);
        db.execSQL(TABLE_COMPETITOR_ENTRY);
        db.execSQL(TABLE_SKU_TEMPLATE);
        db.execSQL(TABLE_ORDER_ITEMS);
        db.execSQL(TABLE_REASON_NOORDER);
        db.execSQL(TABLE_REASON_NOBARCODE);
        db.execSQL(TABLE_REASON_NOCALL);
        db.execSQL(TABLE_REASON_RETURN);
        db.execSQL(TABLE_REASON_UNROUTE);

        db.execSQL(TABLE_PLAN_STATUS);
        db.execSQL(TABLE_FREE_GOOD_BY_CUSTOMER);
        db.execSQL(TABLE_FREE_GOOD_BY_ZONE);
        db.execSQL(TABLE_FREE_GOOD_BY_CHANNEL);
        db.execSQL(TABLE_QUEUE);
        db.execSQL(TABLE_WEEK);
        db.execSQL(TABLE_ROUTE_ASSIGN);

        db.execSQL(TABLE_MASTER_CHANNEL);
        db.execSQL(TABLE_MASTER_REGION);
        db.execSQL(TABLE_MASTER_ZONE);
        db.execSQL(TABLE_MASTER_CLASSIFICATION);
        db.execSQL(TABLE_MASTER_DISTRICT);
        db.execSQL(TABLE_MASTER_TERRITORY);

        db.execSQL(TABLE_REG_DISCOUNT_BY_CHANNEL_INDIA);
        db.execSQL(TABLE_EXT_DISCOUNT_BY_CHANNEL_INDIA);
        db.execSQL(TABLE_SPC_DISCOUNT_BY_CHANNEL_INDIA);
        db.execSQL(TABLE_SPEC_DISCOUNT_BY_CHANNEL_IPT);

        db.execSQL(TABLE_MASTER_DIVISION);
        db.execSQL(TABLE_MASTER_BRANDS);
        db.execSQL(TABLE_MASTER_CATEGORY);
        db.execSQL(TABLE_MASTER_GROUP_CHANNEL);


        db.execSQL(TABLE_TRIGGER_INSERT_VAN_LOADING);
        //db.execSQL(TABLE_TRIGGER_INSERT_VAN_UNLOADING);
        db.execSQL(TABLE_TRIGGER_INSERT_SALES_ITEM);
        db.execSQL(TABLE_TRIGGER_DELETE_SALES_ITEM);

        db.execSQL(TABLE_MASTER_CONFIG);


        //seedProduct(db);
        //seedSettings(db);
        //seedCustomer(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL("DROP TABLE IF EXISTS sls_config");
            db.execSQL("DROP TABLE IF EXISTS queue");
            db.execSQL("DROP TABLE IF EXISTS sls_information");
            db.execSQL("DROP TABLE IF EXISTS product");
            db.execSQL("DROP TABLE IF EXISTS sls_customer");
            db.execSQL("DROP TABLE IF EXISTS sls_route_assign");
            db.execSQL("DROP TABLE IF EXISTS sls_reason_unroute");
            db.execSQL("DROP TABLE IF EXISTS sls_reason_nocall");
            db.execSQL("DROP TABLE IF EXISTS sls_target");
            db.execSQL("DROP TABLE IF EXISTS sls_targetx");
            db.execSQL("DROP TABLE IF EXISTS sls_product");
            db.execSQL("DROP TABLE IF EXISTS sls_top");
            db.execSQL("DROP TABLE IF EXISTS settings");
            db.execSQL("DROP TABLE IF EXISTS sls_pricing_by_outlet");
            db.execSQL("DROP TABLE IF EXISTS sls_pricing_by_channel");
            db.execSQL("DROP TABLE IF EXISTS sls_pricing_by_chain");
            db.execSQL("DROP TABLE IF EXISTS sls_pricing_by_zone");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_reg_outlet");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_reg_channel");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_reg_channel_division");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_ext_outlet");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_ext_channel");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_ext_channel_ipt");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_spc_channel_ipt");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_ext_channel_division");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_spc_outlet");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_spc_channel");

            db.execSQL("DROP TABLE IF EXISTS sls_discount_reg_channel_india");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_ext_channel_india");
            db.execSQL("DROP TABLE IF EXISTS sls_discount_spc_channel_india");


            db.execSQL("DROP TABLE IF EXISTS sls_discount_spc_channel_division");
            db.execSQL("DROP TABLE IF EXISTS sls_order");
            db.execSQL("DROP TABLE IF EXISTS sls_order_item");
            db.execSQL("DROP TABLE IF EXISTS sls_van_stock");
            db.execSQL("DROP TABLE IF EXISTS sls_van");
            db.execSQL("DROP TABLE IF EXISTS sls_van_item");
            db.execSQL("DROP TABLE IF EXISTS sls_reason");
            db.execSQL("DROP TABLE IF EXISTS sls_reason_nobarcode");
            db.execSQL("DROP TABLE IF EXISTS sls_reason_return");
            db.execSQL("DROP TABLE IF EXISTS sls_plan_status");
            db.execSQL("DROP TABLE IF EXISTS sls_sku_template");
            db.execSQL("DROP TABLE IF EXISTS sls_free_good_customer");
            db.execSQL("DROP TABLE IF EXISTS sls_free_good_zone");
            db.execSQL("DROP TABLE IF EXISTS sls_free_good_channel");
            db.execSQL("DROP TABLE IF EXISTS sls_route_assign");
            db.execSQL("DROP TABLE IF EXISTS sls_week");

            db.execSQL("DROP TABLE IF EXISTS sls_channel");
            db.execSQL("DROP TABLE IF EXISTS sls_group_channel");
            db.execSQL("DROP TABLE IF EXISTS sls_region");
            db.execSQL("DROP TABLE IF EXISTS sls_zone");
            db.execSQL("DROP TABLE IF EXISTS sls_classification");
            db.execSQL("DROP TABLE IF EXISTS sls_district");
            db.execSQL("DROP TABLE IF EXISTS sls_territory");

            db.execSQL("DROP TABLE IF EXISTS sls_competitor");
            db.execSQL("DROP TABLE IF EXISTS sls_competitor_entry");

            db.execSQL("DROP TABLE IF EXISTS sls_division");
            db.execSQL("DROP TABLE IF EXISTS sls_brand");
            db.execSQL("DROP TABLE IF EXISTS sls_product_category");

            db.execSQL("DROP TRIGGER IF EXISTS insert_van_loading");
            db.execSQL("DROP TRIGGER IF EXISTS insert_van_unloading");
            db.execSQL("DROP TRIGGER IF EXISTS insert_sales_order_item");
            db.execSQL("DROP TRIGGER IF EXISTS delete_sales_order_item");

            onCreate(db);

        } catch (SQLiteException e) {
            Log.e("error_bray", "exception "+ e.getLocalizedMessage().toString());
        }
    }

}
