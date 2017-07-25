package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 21/09/2015.
 */

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.HeaderStruct;
import com.ksni.roots.ngsales.util.DBManager;

import java.util.ArrayList;
import java.util.List;

public class AsynActivity2 extends Fragment {
    private Button btnRefresh;
    private View rootView;
    private ListView lv;
    public AsyncRowAdapter adapter;
    List<HeaderStruct> listOrder = new ArrayList<HeaderStruct>();


    private void loadData(){

        DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
        Cursor cur = dm.database().rawQuery("SELECT a.status,c.outlet_name,a.notes,a.order_id,a.order_date,a.outlet_id,a.grand_total,  " +
                "       b.uom,b.product_id,b.item,b.description,b.qty,b.uom " +
                "FROM sls_order a LEFT JOIN sls_order_item b ON a.order_id=b.order_id  "+
                "INNER JOIN sls_customer c ON c.outlet_id = a.outlet_id ORDER BY a.order_id,b.item",null);
        if (cur.moveToFirst()) {
            do{
                HeaderStruct ord = new HeaderStruct();
                //Log.e("UID",cur.getString(cur.getColumnIndex("outlet_id")));
                ord.order_id = cur.getInt(cur.getColumnIndex("order_id"));
                ord.status = cur.getInt(cur.getColumnIndex("status"));

                ord.order_date = cur.getString(cur.getColumnIndex("order_date"));
                ord.outlet_name = cur.getString(cur.getColumnIndex("outlet_name"));

                ord.product_id = cur.getString(cur.getColumnIndex("product_id"));
                ord.description = cur.getString(cur.getColumnIndex("description"));
                ord.uom = cur.getString(cur.getColumnIndex("uom"));
                ord.qty = cur.getInt(cur.getColumnIndex("qty"));

                listOrder.add(ord );

            }while (cur.moveToNext());

        }
        cur.close();
        //dm.close();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)         {
            //case android.R.id.home:
            //   super.onBackPressed();
            //  break;
            case R.id.action_refresh:
                SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                String curCustName = session.getString("CUR_VISIT_NAME", "");

                if (curCustName.length()==0){
                    //new AsyncPost().execute();
                }
                else
                    Toast.makeText(getActivity(), "Customer " + curCustName.toUpperCase() + " still visiting.", Toast.LENGTH_LONG).show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        String curCustName = session.getString("CUR_VISIT_NAME", "");

        if (curCustName.length()==0) {
          // new AsyncPost().execute();
        }
        else
            Toast.makeText(getActivity(), "Customer " + curCustName.toUpperCase() + " still visiting.", Toast.LENGTH_LONG).show();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if (rootView == null) {
            rootView = inflater.inflate(R.layout.ui_sync, container, false);
        loadData();
        lv = (ListView) rootView.findViewById(R.id.lstSync);
        adapter = new AsyncRowAdapter(getActivity(), R.layout.ui_sync_row, listOrder);
        lv.setEmptyView(rootView.findViewById(R.id.list_empty));
        lv.setAdapter(adapter);

            //progressDialog = (ProgressBar) rootView.findViewById(R.id.progressAsyn);
            /*btnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                    String curCustName = session.getString("CUR_VISIT", "");

                    if (curCustName.length()==0)
                        new AsyncPost().execute();
                    else
                        Toast.makeText(getActivity(), "Customer " + curCustName.toUpperCase() + " still visiting.", Toast.LENGTH_LONG).show();
                }
            });
            */

            //SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
            //String curCustName = session.getString("CUR_VISIT", "");

            //if (curCustName.length()==0)

            //else
              //  Toast.makeText(getActivity(), "Customer " + curCustName.toUpperCase() + " still visiting.", Toast.LENGTH_LONG).show();
        //}
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }



}
