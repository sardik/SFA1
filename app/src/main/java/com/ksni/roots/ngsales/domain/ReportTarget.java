package com.ksni.roots.ngsales.domain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 06/12/2015.
 */
public class ReportTarget extends AppCompatActivity{
    private ListView lv;
    
    private List<ReportTargetStruct> listProduct = new ArrayList<ReportTargetStruct>();;
    private Toolbar toolbar;
    private ReportTargetAdapter adapter;
    private int report_position=0;
    private double totQty = 0;
    private double totValue= 0;
    private int totIPT= 0;
    private int totCall= 0;
    private int totEffectiveCall=0;
    private int totUnplanCall=0;

    private double totSummaryTargetQty=0;
    private double totSummaryTargetValue=0;
    private double totSummaryActualQty=0;
    private double totSummaryActualValue=0;
    private  int targetIPT = 0;
    private  int targetEC = 0;
    private  int targetCall = 0;
    private  int actualIPT = 0;
    private  int actualEC = 0;
    private  int actualCall = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_add_order, menu);
        return true;
    }

    private void loadTargetProductSummary(int year,int period,String fromDate,String toDate){
        ReportTargetStruct buff = null;

        Cursor cur = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());

        cur= dm.database().rawQuery("SELECT sum(target_qty) tot_qty,sum(target_value) tot_value FROM sls_target WHERE year=? and period=?",new String[]{String.valueOf(year),String.valueOf(period)});
        if (cur.moveToFirst()){
            totSummaryTargetQty=cur.getDouble(0);
            totSummaryTargetValue=cur.getDouble(1);
        }
        cur.close();

        cur= dm.database().rawQuery("SELECT sum(i.qty_pcs) total,sum(i.total_net) totalNet  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and ( DATE(h.order_date) between ? and ? ) and i.item_type='N' and i.qty_pcs>0 ",new String[]{fromDate,toDate});
        if (cur.moveToFirst()){
            totSummaryActualQty=cur.getDouble(0);
            totSummaryActualValue=cur.getDouble(1);
        }
        cur.close();

        buff = new ReportTargetStruct();
        buff.description = Helper.getStrResource(this,R.string.target_text_total_target_product_by_qty);
        buff.target = totSummaryTargetQty;
        buff.actual = totSummaryActualQty;
        setAchieve(buff);
        listProduct.add(buff);


        buff = new ReportTargetStruct();
        buff.description = Helper.getStrResource(this,R.string.target_text_total_target_product_by_value);
        buff.target = totSummaryTargetValue;
        buff.actual = totSummaryActualValue;
        setAchieve(buff);
        listProduct.add(buff);


    }


    private void setAchieve(ReportTargetStruct buff){
        double target =0;

        if (buff.target>=0) target = buff.target;

        //start
        if (buff.actual>target){
            buff.sisa = Helper.getFormatCurrency(buff.actual-target)+"\n"+Helper.getStrResource(this,R.string.target_over);
        }else{
            if (buff.actual==target){
                buff.sisa = "0 ("+Helper.getStrResource(this,R.string.target_achieve)+")";
            }else{
                buff.sisa = Helper.getFormatCurrency(target-buff.actual)+"\n"+Helper.getStrResource(this,R.string.target_under);
            }
        }

        if (target!=0) {
            buff.persen = (buff.actual / target) * 100;
            if (buff.persen>100) buff.persen = 100;
        }else{
            buff.persen = 0;
            buff.sisa = "0";
        }
        //end
    }

    private void loadTargetProduct(int year,int period,boolean qty,String fromDate,String toDate){
        List<ReportTargetStruct> listTarget = new ArrayList<ReportTargetStruct>();;
        List<ReportTargetStruct> listActual = new ArrayList<ReportTargetStruct>();;
        Cursor cur = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());
        ReportTargetStruct buff = null;
        cur= dm.database().rawQuery("SELECT t.product_id,p.product_name description, t.target_qty,t.target_value FROM sls_target t INNER JOIN sls_product p ON p.product_id=t.product_id WHERE t.year=? and t.period=?",new String[]{String.valueOf(year),String.valueOf(period)});
        if(cur.moveToFirst()){
            do{
                buff = new ReportTargetStruct();
                buff.product_id = cur.getString(cur.getColumnIndex("product_id"));
                buff.description = cur.getString(cur.getColumnIndex("description")) + " "+buff.product_id;
                if (qty)
                    buff.target = cur.getDouble(cur.getColumnIndex("target_qty"));
                else
                    buff.target = cur.getDouble(cur.getColumnIndex("target_value"));

                listTarget.add(buff);
            }while(cur.moveToNext());
        }
        cur.close();

        cur= dm.database().rawQuery("SELECT i.product_id,i.description, sum(i.qty_pcs) tot_qty, sum(i.total_net) tot_value  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and ( DATE(h.order_date) between ? and ? ) and i.item_type='N' and i.qty_pcs>0 GROUP BY i.product_id,i.description",new String[]{fromDate,toDate});
        if (cur.moveToFirst()){
            do{
                buff = new ReportTargetStruct();
                buff.product_id = cur.getString(cur.getColumnIndex("product_id"));
                buff.description = cur.getString(cur.getColumnIndex("description")) + " "+buff.product_id;
                if (qty)
                    buff.actual = cur.getDouble(cur.getColumnIndex("tot_qty"));
                else
                    buff.actual = cur.getDouble(cur.getColumnIndex("tot_value"));

                listActual.add(buff);
            }while(cur.moveToNext());
        }
        cur.close();




        for(ReportTargetStruct r:listActual){
            boolean ada = false;
            for(ReportTargetStruct r2:listTarget){
                if(r.product_id.equals(r2.product_id)){
                    ada = true;
                    break;
                }
            }

            if (!ada){
                buff = new ReportTargetStruct();
                buff.target = -1;
                buff.product_id = r.product_id;
                buff.description = r.description;
                listTarget.add(buff);
            }


        }


        for(ReportTargetStruct r:listTarget){

            buff = new ReportTargetStruct();
            buff.target = r.target;
            buff.description = r.description;

            for(ReportTargetStruct r2:listActual){
                if(r2.product_id.equals(r.product_id)){
                    buff.actual = r2.actual;
                    break;
                }
            }

            setAchieve(buff);
            listProduct.add(buff);
        }


    }

    private void loadCallPlanUnPlan(int year,int period,String fromDate,String toDate){
        Cursor cur = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());
        ReportTargetStruct buff = null;
        cur= dm.database().rawQuery("SELECT target_call,target_ec FROM sls_target  WHERE  product_id='999999' and year=? and period=?",new String[]{String.valueOf(year),String.valueOf(period)});
        if(cur.moveToFirst()){
            targetCall = cur.getInt(0);
            targetEC = cur.getInt(1);
        }
        cur.close();


        actualEC = 0;
        cur= dm.database().rawQuery("SELECT h.outlet_id  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and ( DATE(h.order_date) between ? and ? ) and i.item_type='N' and i.qty_pcs>0 GROUP BY h.outlet_id",new String[]{fromDate,toDate});
        if (cur.moveToFirst()){
            do{
                actualEC++;
            }while(cur.moveToNext());
        }
        cur.close();

        actualCall = 0;
        cur= dm.database().rawQuery("SELECT h.outlet_id  FROM sls_order h WHERE h.order_type = 0 and ( DATE(h.order_date) between ? and ? )  GROUP BY h.outlet_id",new String[]{fromDate,toDate});
        if (cur.moveToFirst()){
            do{
                actualCall++;
            }while(cur.moveToNext());
        }
        cur.close();


        buff = new ReportTargetStruct();
        buff.description = Helper.getStrResource(this,R.string.target_call_text);
        buff.target = targetCall;
        buff.actual = actualCall;
        setAchieve(buff);
        listProduct.add(buff);


        buff = new ReportTargetStruct();
        buff.description = Helper.getStrResource(this,R.string.target_effective_call_text);
        buff.target = targetEC;
        buff.actual = actualEC;
        setAchieve(buff);
        listProduct.add(buff);

    }


    private void loadTargetIP(int year,int period,String fromDate,String toDate){
        Cursor cur = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());
        ReportTargetStruct buff = null;
        cur= dm.database().rawQuery("SELECT target_ipt FROM sls_target  WHERE year=? and period=? and product_id='999999'",new String[]{String.valueOf(year),String.valueOf(period)});
        if(cur.moveToFirst()){
            targetIPT = cur.getInt(0);
        }
        cur.close();


        actualIPT = 0;
        int curIpt = 0;
        int cust = 0;
        cur= dm.database().rawQuery("SELECT h.outlet_id,count(i.product_id)tot  FROM sls_order h "+
                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id WHERE h.order_type = 0 and ( DATE(h.order_date) between ? and ? ) and i.item_type='N' and i.qty_pcs>0 GROUP BY h.outlet_id",new String[]{fromDate,toDate});
        if (cur.moveToFirst()){
            do{
                int ipt = cur.getInt(cur.getColumnIndex("tot"));
                curIpt = curIpt + ipt;
                cust++;
            }while(cur.moveToNext());
        }
        cur.close();

        if (cust!=0){
            actualIPT = Math.round(curIpt / cust);
        }

        buff = new ReportTargetStruct();
        buff.description = Helper.getStrResource(this,R.string.target_effective_ipt);
        buff.target = targetIPT;
        buff.actual = actualIPT;
        setAchieve(buff);
        listProduct.add(buff);

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

        Intent i = getIntent();
        report_position =i.getIntExtra("report_type",0);

        listProduct.clear();
        lv = (ListView)findViewById(R.id.lstProduct);

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        String startDate = session.getString("CUR_START_DATE", "");
        String endDate = session.getString("CUR_END_DATE", "");

        int tahun=session.getInt("CUR_YEAR", Integer.parseInt(Helper.getCurrentDateTime("yyyy")));
        int period = session.getInt("CUR_PERIOD", Integer.parseInt( Helper.getCurrentDateTime("mm")));


        switch (report_position){
            case 0:
                ab.setTitle(Helper.getStrResource(this,R.string.target_text_summary_product));
                loadTargetProductSummary(tahun,period,startDate,endDate);
                break;
            case 1:
                ab.setTitle(Helper.getStrResource(this,R.string.target_text_summary_by_product_qty));
                loadTargetProduct(tahun,period,true,startDate,endDate);
                break;
            case 2:
                ab.setTitle(Helper.getStrResource(this,R.string.target_text_summary_by_product_value));
                loadTargetProduct(tahun,period,false,startDate,endDate);
                break;
            case 3:
                ab.setTitle(Helper.getStrResource(this,R.string.target_text_summary_by_call_ec_ipt));
                loadCallPlanUnPlan(tahun,period,startDate,endDate);
                loadTargetIP(tahun,period,startDate,endDate);
                break;

        }

        adapter = new ReportTargetAdapter(this, R.layout.report_target_item, listProduct);
        lv.setAdapter(adapter);

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
