package com.ksni.roots.ngsales.domain;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 06/12/2015.
 */
public class ReportSummaryByProduct extends AppCompatActivity{
    private ListView lv;
    private List<ReportSummaryByProductStruct> listProduct = new ArrayList<ReportSummaryByProductStruct>();;
    private Toolbar toolbar;
    private ReportSummaryByProductAdapter adapter;
    private int report_position=0;
    private double totQty = 0;
    private double totValue = 0;
    private int totIPT= 0;
    private int totCall= 0;
    private int totEffectiveCall=0;
    private int totUnPlaneEffectiveCall=0;
    private int totUnplanCall=0;
    private int totCallPlan = 0;
    private int totCallUnplan;

    private Boolean flagQty;
    private Boolean flagTotal;

    private int selisihtotCallUncall;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_add_order, menu);
        return true;
    }

    private void loadDataProduct(boolean qty){
        listProduct.clear();
        ReportSummaryByProductStruct buff = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());

//        "SELECT i.product_id, i.description, " +
//                "sum(i.qty_pcs) as total_pcs, sum(i.total_net) as total_net " +
//                "FROM sls_order h " +
//                "INNER JOIN sls_order_item i " +
//                "ON  i.order_id = h.order_id " +
//                "WHERE h.order_type = 0 and DATE(h.order_date) = ? and i.item_type = 'N' and i.qty_pcs > 0 " +
//                "GROUP BY i.product_id", new String[]{Helper.getCurrentDate()});

        // Edited By Obbie 27-12-2016
        Cursor cur= dm.database().rawQuery(
                " SELECT i.product_id, i.description, " +
                "         IFNULL(j.qty_crt, 0) as qty_crt, IFNULL(k.qty_box, 0) as qty_box, IFNULL(l.qty_pcs, 0) as qty_pcs, " +
                "         m.total_pcs, m.total_net " +
                " FROM sls_order h " +
                " INNER JOIN sls_order_item i " +
                " ON  i.order_id = h.order_id " +
                " LEFT JOIN ( " +
                "         SELECT product_id, sum(qty) as qty_crt, uom " +
                "         FROM sls_order_item " +
                "         WHERE uom = 'CRT' " +
                "         GROUP BY product_id " +
                " ) AS j " +
                " ON i.product_id = j.product_id " +
                " LEFT JOIN ( " +
                "         SELECT product_id, sum(qty) as qty_box, uom " +
                "         FROM sls_order_item " +
                "         WHERE (uom = 'BOX' OR uom = 'RCG' OR uom = 'HGR') " +
                "         GROUP BY product_id " +
                " ) AS k " +
                " ON i.product_id = k.product_id " +
                " LEFT JOIN ( " +
                "         SELECT product_id, sum(qty) as qty_pcs, uom " +
                "         FROM sls_order_item " +
                "         WHERE uom = 'PCS' " +
                "         GROUP BY product_id " +
                " ) AS l " +
                " ON i.product_id = l.product_id " +
                " LEFT JOIN ( " +
                "         SELECT product_id, sum(qty_pcs) as total_pcs, sum(total_net) as total_net " +
                "         FROM sls_order_item " +
                "         GROUP BY product_id " +
                " ) AS m " +
                " ON i.product_id = m.product_id " +
                " WHERE h.order_type = 0 and DATE(h.order_date) = ? and i.item_type = 'N' and i.qty_pcs > 0 " +
                " GROUP BY i.product_id " +
                " ORDER BY i.product_id ", new String[]{Helper.getCurrentDate()});

        //Cursor cur= dm.database().rawQuery("SELECT i.product_id,i.description,sum(i.qty_pcs) total,sum(i.total_net) totalNet  FROM sls_order h "+
        //                                   "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 GROUP BY i.product_id,i.description",new String[]{Helper.getCurrentDate()});

        if(cur.moveToFirst()){
            do{
                buff = new ReportSummaryByProductStruct();
                buff.product_id = cur.getString(cur.getColumnIndex("product_id"));
                buff.product_name = cur.getString(cur.getColumnIndex("description"));
                buff.crt = cur.getInt(cur.getColumnIndex("qty_crt"));
                buff.box = cur.getInt(cur.getColumnIndex("qty_box"));
                buff.pcs = cur.getInt(cur.getColumnIndex("qty_pcs"));
                if (qty)
                    buff.qty = cur.getInt(cur.getColumnIndex("total_pcs"));
                else
                    buff.qty = cur.getInt(cur.getColumnIndex("total_net"));

                listProduct.add(buff);
            }while(cur.moveToNext());
        }
        cur.close();
    }

    private void loadTotal(){
        listProduct.clear();
        ReportSummaryByProductStruct buff = null;

        Cursor cur = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());
        cur= dm.database().rawQuery("SELECT sum(i.qty_pcs) total,sum(i.total_net) totalNet  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id INNER JOIN sls_customer c ON c.outlet_id=h.outlet_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 ",new String[]{Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            totQty = cur.getDouble(0);
            totValue = cur.getDouble(1);
        }
        cur.close();


        int callPlan = 0;
        int unplan = 0;
        cur= dm.database().rawQuery("SELECT call_status FROM sls_plan_status WHERE DATE(date)=?",new String[]{Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            do{
                if (cur.getString(0).equals("1")) {
                    callPlan++;
                }else{
                    unplan++;
                }
            }while(cur.moveToNext());
        }
        cur.close();
        totCallPlan = callPlan;
        totUnplanCall = unplan;


        int call = 0;
        cur= dm.database().rawQuery("SELECT h.outlet_id  FROM sls_order h "+
                " WHERE DATE(h.order_date)=? and h.outlet_id in (SELECT outlet_id FROM sls_plan_status WHERE call_status='1' and DATE(date)=? ) GROUP BY h.outlet_id",new String[]{Helper.getCurrentDate(),Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            do{
                call++;
            }while(cur.moveToNext());
        }
        cur.close();
        totCall = call;


        int unplancall = 0;
        cur= dm.database().rawQuery("SELECT h.outlet_id FROM sls_order h "+
                "WHERE DATE(h.order_date)=? and h.outlet_id in (SELECT outlet_id FROM sls_plan_status WHERE call_status='0' and DATE(date)=? ) GROUP BY h.outlet_id",new String[]{Helper.getCurrentDate(),Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            do{
                unplancall++;
            }while(cur.moveToNext());
        }
        cur.close();
        totCallUnplan = unplancall;


        int ecall = 0;
        cur= dm.database().rawQuery("SELECT h.outlet_id  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 and h.outlet_id in (SELECT outlet_id FROM sls_plan_status WHERE call_status='1' and DATE(date)=? ) GROUP BY h.outlet_id",new String[]{Helper.getCurrentDate(),Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            do{
                ecall++;
            }while(cur.moveToNext());
        }
        cur.close();
        totEffectiveCall = ecall;


        int eucall = 0;
        cur= dm.database().rawQuery("SELECT h.outlet_id  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 and h.outlet_id in (SELECT outlet_id FROM sls_plan_status WHERE call_status='0' and DATE(date)=? ) GROUP BY h.outlet_id",new String[]{Helper.getCurrentDate(),Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            do{
                eucall++;
            }while(cur.moveToNext());
        }
        cur.close();
        totUnPlaneEffectiveCall = eucall;


        int ipt = 0;
        cur= dm.database().rawQuery("SELECT i.product_id  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 GROUP BY i.product_id",new String[]{Helper.getCurrentDate()});
        if (cur.moveToFirst()){
            do{
                    ipt++;

            }while(cur.moveToNext());
        }
        cur.close();
        totIPT = ipt;



        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_call_plan);
        buff.qty = totCallPlan;
        listProduct.add(buff);

        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_call);
        buff.qty = totCall;
        listProduct.add(buff);

        //add req pma
        selisihtotCallUncall = callPlan - totCall;
        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_un_call);
        buff.qty = selisihtotCallUncall;
        listProduct.add(buff);
        //add pma


        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_effective_call);
        buff.qty = totEffectiveCall;
        listProduct.add(buff);

        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_unplan);
        buff.qty = totUnplanCall;
        listProduct.add(buff);

        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_call_unplan);
        buff.qty = totCallUnplan;
        listProduct.add(buff);

        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_unplan_effective_call);
        buff.qty = totUnPlaneEffectiveCall;
        listProduct.add(buff);


        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_product);
        buff.qty = totIPT;
        listProduct.add(buff);

        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_qty);
        buff.qty = totQty;
        listProduct.add(buff);

        buff = new ReportSummaryByProductStruct();
        buff.product_id = "";
        buff.product_name = Helper.getStrResource(this,R.string.summary_total_value);
        buff.qty = totValue;
        listProduct.add(buff);

    }

    private void loadDataCustomer(boolean qty){
        listProduct.clear();
        ReportSummaryByProductStruct buff = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());

//        "SELECT c.outlet_id,c.outlet_name,sum(i.qty_pcs) total,sum(i.total_net) totalNet  FROM sls_order h "+
//                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id INNER JOIN sls_customer c ON c.outlet_id=h.outlet_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 GROUP BY c.outlet_id,c.outlet_name",new String[]{Helper.getCurrentDate()});

        Cursor cur= dm.database().rawQuery(
                " SELECT c.outlet_id, c.outlet_name,  " +
                "         IFNULL(j.qty_crt, 0) as qty_crt, IFNULL(k.qty_box, 0) as qty_box, IFNULL(l.qty_pcs, 0) as qty_pcs,  " +
                "         m.total_pcs, m.total_net  " +
                " FROM sls_order h  " +
                " INNER JOIN sls_order_item i  " +
                " ON  i.order_id = h.order_id  " +
                " INNER JOIN sls_customer c " +
                " ON c.outlet_id = h.outlet_id " +
                " LEFT JOIN (  " +
                "         SELECT cc.outlet_id, sum(aa.qty) as qty_crt " +
                "         FROM sls_order_item aa  " +
                "		 LEFT JOIN sls_order bb " +
                "		 ON bb.order_id = aa.order_id " +
                "		 LEFT JOIN sls_customer cc " +
                "		 ON cc.outlet_id = bb.outlet_id " +
                "         WHERE aa.uom = 'CRT'  " +
                "         GROUP BY cc.outlet_id " +
                " ) AS j  " +
                " ON j.outlet_id= h.outlet_id " +
                " LEFT JOIN (  " +
                "         SELECT cc.outlet_id, sum(aa.qty) as qty_box " +
                "         FROM sls_order_item aa 		  " +
                "		 LEFT JOIN sls_order bb " +
                "		 ON bb.order_id = aa.order_id " +
                "		 LEFT JOIN sls_customer cc " +
                "		 ON cc.outlet_id = bb.outlet_id " +
                "         WHERE (aa.uom = 'BOX' OR aa.uom ='RCG' OR aa.uom = 'HGR')  " +
                "         GROUP BY cc.outlet_id " +
                " ) AS k  " +
                " ON k.outlet_id = h.outlet_id " +
                " LEFT JOIN (  " +
                "         SELECT cc.outlet_id, sum(aa.qty) as qty_pcs " +
                "         FROM sls_order_item aa " +
                "		 LEFT JOIN sls_order bb " +
                "		 ON bb.order_id = aa.order_id " +
                "		 LEFT JOIN sls_customer cc " +
                "		 ON cc.outlet_id = bb.outlet_id " +
                "         WHERE aa.uom = 'PCS'  " +
                "         GROUP BY cc.outlet_id " +
                " ) AS l  " +
                " ON l.outlet_id = h.outlet_id " +
                " LEFT JOIN (  " +
                "         SELECT cc.outlet_id, sum(aa.qty_pcs) as total_pcs, sum(aa.total_net) as total_net  " +
                "         FROM sls_order_item aa " +
                "		 LEFT JOIN sls_order bb " +
                "		 ON bb.order_id = aa.order_id " +
                "		 LEFT JOIN sls_customer cc " +
                "		 ON cc.outlet_id = bb.outlet_id " +
                "         GROUP BY cc.outlet_id " +
                " ) AS m  " +
                " ON m.outlet_id = h.outlet_id " +
                " WHERE h.order_type = 0 and DATE(h.order_date) = ? and i.item_type = 'N' and i.qty_pcs > 0  " +
                " GROUP BY c.outlet_id " +
                " ORDER BY c.outlet_id ", new String[]{Helper.getCurrentDate()});

        if(cur.moveToFirst()){
            do{
                buff = new ReportSummaryByProductStruct();
                buff.product_id = cur.getString(cur.getColumnIndex("outlet_id"));
                buff.product_name = cur.getString(cur.getColumnIndex("outlet_name"));
                buff.crt = cur.getInt(cur.getColumnIndex("qty_crt"));
                buff.box = cur.getInt(cur.getColumnIndex("qty_box"));
                buff.pcs = cur.getInt(cur.getColumnIndex("qty_pcs"));
                if (qty)
                    buff.qty = cur.getInt(cur.getColumnIndex("total_pcs"));
                else
                    buff.qty = cur.getInt(cur.getColumnIndex("total_net"));

                listProduct.add(buff);
            }while(cur.moveToNext());
        }
        cur.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_report_per_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        lv = (ListView)findViewById(R.id.lstProduct);
        Intent i = getIntent();
        report_position =i.getIntExtra("report_type",0);
        switch (report_position){
            case 0:
                ab.setTitle(Helper.getStrResource(this,R.string.summary_by_product_qty_text));
                loadDataProduct(true);
                flagQty = true;
                flagTotal = false;
                break;
            case 1:
                ab.setTitle(Helper.getStrResource(this,R.string.summary_by_product_value_text));
                loadDataProduct(false);
                flagQty = false;
                flagTotal = false;
                break;
            case 2:
                ab.setTitle(Helper.getStrResource(this,R.string.summary_by_customer_qty_text));
                loadDataCustomer(true);
                flagQty = true;
                flagTotal = false;
                break;
            case 3:
                ab.setTitle(Helper.getStrResource(this,R.string.summary_by_customer_value_text));
                loadDataCustomer(false);
                flagQty = false;
                flagTotal = false;
                break;
            case 4:
                ab.setTitle(Helper.getStrResource(this,R.string.summary_total));
                loadTotal();
                flagQty = false;
                flagTotal = true;
                break;

        }

        adapter = new ReportSummaryByProductAdapter(this, R.layout.ui_report_per_product_item, listProduct, flagTotal, flagQty);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final int pos = position;

                if (report_position != 4) {
                    ReportSummaryByProductStruct strct = (ReportSummaryByProductStruct) adapter.getItem(pos);
                    Intent i = new Intent(ReportSummaryByProduct.this, ReportSummaryByProductCustomer.class);
                    i.putExtra("description", strct.product_name);
                    i.putExtra("product_id", strct.product_id);
                    i.putExtra("report_type", report_position);
                    i.putExtra("flag_total", flagTotal);
                    i.putExtra("flag_qty", flagQty);
                    startActivity(i);
                }


            }
        });


        lv.setEmptyView(findViewById(R.id.list_empty));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }




}
