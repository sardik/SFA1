package com.ksni.roots.ngsales.domain;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
public class ReportSummaryByProductCustomer extends AppCompatActivity{
    private ListView lv;
    private List<ReportSummaryByProductStruct> listProduct = new ArrayList<ReportSummaryByProductStruct>();;
    private Toolbar toolbar;
    private ReportSummaryByProductAdapter adapter;
    private int report_position=0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_add_order, menu);
        return true;
    }

    private void loadDataProduct(String prd_id,boolean qty){
        listProduct.clear();;
        ReportSummaryByProductStruct buff = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());

//        "SELECT c.outlet_id, c.outlet_name, i.uom, sum(i.qty_pcs) as total, sum(i.total_net) as totalNet " +
//                "FROM sls_order h " +
//                "INNER JOIN sls_order_item i ON  i.order_id = h.order_id " +
//                "INNER JOIN sls_customer c ON c.outlet_id = h.outlet_id " +
//                "WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 and i.product_id=? " +
//                "GROUP BY c.outlet_id", new String[]{Helper.getCurrentDate(), prd_id});

        // Edited By Obbie 27-12-2016
        Cursor cur= dm.database().rawQuery(
                " SELECT cc.outlet_id, cc.outlet_name, aa.product_id, " +
                "        CASE WHEN aa.uom != 'CRT' THEN aa.qty  = '0' " +
                "        ELSE aa.qty END AS qty_crt, '0' AS qty_box, '0' AS qty_pcs, " +
                "        sum(aa.qty_pcs) AS total_pcs, sum(aa.total_net) AS total_net " +
                " FROM sls_order_item aa " +
                " LEFT JOIN sls_order bb " +
                " ON aa.order_id = bb.order_id " +
                " LEFT JOIN sls_customer cc " +
                " ON bb.outlet_id = cc.outlet_id " +
                " WHERE bb.order_type = 0 and DATE(bb.order_date) = ? and aa.item_type = 'N' and aa.qty_pcs > 0 " +
                " AND aa.product_id = '" + prd_id + "' AND aa.uom = 'CRT' " +
                " GROUP BY cc.outlet_id " +
                " UNION ALL " +
                " SELECT cc.outlet_id, cc.outlet_name, aa.product_id, '0' AS qty_box,  " +
                "        CASE   WHEN aa.uom != 'RCG'  AND aa.uom == 'BOX'  AND aa.uom != 'CRT'  THEN aa.qty " +
                "               WHEN aa.uom == 'RCG' AND aa.uom != 'BOX'  AND aa.uom != 'CRT'  THEN aa.qty " +
                "        ELSE aa.qty = '0' END AS qty_box, '0' AS qty_pcs, " +
                "        sum(aa.qty_pcs) AS total_pcs, sum(aa.total_net) AS total_net " +
                " FROM sls_order_item aa " +
                " LEFT JOIN sls_order bb " +
                " ON aa.order_id = bb.order_id " +
                " LEFT JOIN sls_customer cc " +
                " ON bb.outlet_id = cc.outlet_id " +
                " WHERE bb.order_type = 0 and DATE(bb.order_date) = ? and aa.item_type = 'N' and aa.qty_pcs > 0 " +
                " AND aa.product_id = '" + prd_id + "' AND (aa.uom = 'BOX' OR aa.uom = 'RCG') " +
                " GROUP BY cc.outlet_id " +
                " UNION ALL " +
                " SELECT cc.outlet_id, cc.outlet_name, aa.product_id, '0' AS qty_crt, '0' AS qty_box, " +
                "        CASE WHEN aa.uom != 'PCS' THEN aa.qty  = '0' " +
                "        ELSE aa.qty END AS qty_pcs, " +
                "        sum(aa.qty_pcs) as total_pcs, sum(aa.total_net) as total_net " +
                " FROM sls_order_item aa " +
                " LEFT JOIN sls_order bb " +
                " ON aa.order_id = bb.order_id " +
                " LEFT JOIN sls_customer cc " +
                " ON bb.outlet_id = cc.outlet_id " +
                " WHERE bb.order_type = 0 and DATE(bb.order_date) = ? and aa.item_type = 'N' and aa.qty_pcs > 0 " +
                " AND aa.product_id = '" + prd_id + "' AND aa.uom = 'PCS' " +
                " GROUP BY cc.outlet_id ", new String[]{Helper.getCurrentDate(), Helper.getCurrentDate(), Helper.getCurrentDate() });


        //Cursor cur= dm.database().rawQuery("SELECT c.outlet_id,c.outlet_name,sum(i.qty_pcs) total,sum(i.total_net) totalNet FROM sls_order h "+
        //                                   "INNER JOIN sls_order_item i ON  i.order_id=h.order_id INNER JOIN sls_customer c ON c.outlet_id=h.outlet_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 and i.product_id=? GROUP BY c.outlet_id,c.outlet_name",new String[]{Helper.getCurrentDate(),prd_id});
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

    private void loadDataCustomer(String prd_id,boolean qty){
        listProduct.clear();;
        ReportSummaryByProductStruct buff = null;
        DBManager dm = DBManager.getInstance(getApplicationContext());

//        "SELECT i.product_id,i.description,sum(i.qty_pcs) total,sum(i.total_net) totalNet FROM sls_order h "+
//                "INNER JOIN sls_order_item i ON  i.order_id=h.order_id INNER JOIN sls_customer c ON c.outlet_id=h.outlet_id WHERE h.order_type = 0 and DATE(h.order_date)=? and i.item_type='N' and i.qty_pcs>0 and c.outlet_id=? GROUP BY i.product_id,i.description",new String[]{Helper.getCurrentDate(),prd_id});


        Cursor cur= dm.database().rawQuery(
                " SELECT cc.outlet_id, cc.outlet_name, aa.product_id, aa.description, " +
                " 			 CASE WHEN aa.uom != 'CRT' THEN aa.qty  = '0' " +
                " 			 ELSE aa.qty END AS qty_crt, '0' AS qty_box, '0' AS qty_pcs, " +
                " 			 sum(aa.qty_pcs) AS total_pcs, sum(aa.total_net) AS total_net " +
                " FROM sls_order_item aa " +
                " LEFT JOIN sls_order bb " +
                " ON aa.order_id = bb.order_id " +
                " LEFT JOIN sls_customer cc " +
                " ON bb.outlet_id = cc.outlet_id " +
                " WHERE bb.order_type = 0 and DATE(bb.order_date) = ? and aa.item_type = 'N' and aa.qty_pcs > 0 " +
                " AND cc.outlet_id = '" + prd_id + "' AND aa.uom = 'CRT' " +
                " GROUP BY aa.product_id " +
                " UNION ALL " +
                " SELECT cc.outlet_id, cc.outlet_name, aa.product_id, aa.description, '0' AS qty_box,  " +
                " 			 CASE 	WHEN aa.uom != 'RCG'  AND aa.uom == 'BOX'  AND aa.uom != 'CRT'  THEN aa.qty " +
                " 							WHEN aa.uom == 'RCG' AND aa.uom != 'BOX'  AND aa.uom != 'CRT'  THEN aa.qty " +
                " 			 ELSE aa.qty = '0' END AS qty_box, '0' AS qty_pcs, " +
                " 			 sum(aa.qty_pcs) AS total_pcs, sum(aa.total_net) AS total_net " +
                " FROM sls_order_item aa " +
                " LEFT JOIN sls_order bb " +
                " ON aa.order_id = bb.order_id " +
                " LEFT JOIN sls_customer cc " +
                " ON bb.outlet_id = cc.outlet_id " +
                " WHERE bb.order_type = 0 and DATE(bb.order_date) = ? and aa.item_type = 'N' and aa.qty_pcs > 0 " +
                " AND cc.outlet_id = '" + prd_id + "' AND (aa.uom = 'BOX' OR aa.uom = 'RCG') " +
                " GROUP BY aa.product_id, aa.description " +
                " UNION ALL " +
                " SELECT cc.outlet_id, cc.outlet_name, aa.product_id, aa.description, '0' AS qty_crt, '0' AS qty_box, " +
                " 			 CASE WHEN aa.uom != 'PCS' THEN aa.qty  = '0' " +
                " 			 ELSE aa.qty END AS qty_pcs, " +
                " 			 sum(aa.qty_pcs) as total_pcs, sum(aa.total_net) as total_net " +
                " FROM sls_order_item aa " +
                " LEFT JOIN sls_order bb " +
                " ON aa.order_id = bb.order_id " +
                " LEFT JOIN sls_customer cc " +
                " ON bb.outlet_id = cc.outlet_id " +
                " WHERE bb.order_type = 0 and DATE(bb.order_date) = ? and aa.item_type = 'N' and aa.qty_pcs > 0 " +
                " AND  cc.outlet_id = '" + prd_id + "' AND aa.uom = 'PCS' " +
                " GROUP BY aa.product_id, aa.description ", new String[]{Helper.getCurrentDate(), Helper.getCurrentDate(), Helper.getCurrentDate() });

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_report_per_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        Intent i = getIntent();
        String prd_desc = i.getStringExtra("description");
        String prd_id = i.getStringExtra("product_id");
        report_position =i.getIntExtra("report_type",0);
        Boolean flag = i.getBooleanExtra("flag_total", false);
        Boolean flag2 = i.getBooleanExtra("flag_qty", false);

        ab.setTitle(prd_desc);

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);


        switch (report_position){
            case 0:
                loadDataProduct(prd_id, true);
                flag = false;
                flag2 = true;
                break;
            case 1:
                loadDataProduct(prd_id, false);
                flag = false;
                flag2 = false;
                break;
            case 2:
                loadDataCustomer(prd_id,true);
                flag = false;
                flag2 = true;
                break;
            case 3:
                loadDataCustomer(prd_id,false);
                flag = false;
                flag2 = false;
                break;
        }


        lv = (ListView)findViewById(R.id.lstProduct);

        adapter = new ReportSummaryByProductAdapter(this, R.layout.ui_report_per_product_item, listProduct, flag, flag2 );
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final int pos = position;


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
