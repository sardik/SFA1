package com.ksni.roots.ngsales.domain;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 29/09/2015.
 */
public class ViewOrderActivity extends Fragment {
    private ProgressBar progressDialog;
    private ExpandableListView elv;
    private int ParentClickStatus=-1;
    private int ChildClickStatus=-1;
    private ArrayList<ViewOrderParent> parents;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        elv = (ExpandableListView) view.findViewById(R.id.list);
        progressDialog = (ProgressBar) view.findViewById(R.id.progressAsyn);
        Resources res = this.getResources();
        Drawable devider = res.getDrawable(R.drawable.line);

        elv.setBackground(res.getDrawable(R.drawable.bar_bg));

        elv.setGroupIndicator(null);
        //    elv.setDivider(devider);
        //   elv.setChildDivider(devider);
       // elv.setDividerHeight(1);
        new OrderActivityAsync().execute();
        //    elv.setAdapter(new ViewOrderListAdapter());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_order, null);
        setHasOptionsMenu(true);
        return v;
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
                if (parents!=null) parents.clear();
                new OrderActivityAsync().execute();
                break;

            case R.id.action_resync:
                final SQLiteDatabase xdb = DBManager.getInstance(getActivity().getApplicationContext()).database();

                final List<OrderHead> pendingOrder = Order.getPendingHeadOrder(getActivity().getApplicationContext());

                //int cntPend = pendingOrder.size();

                int cntPend = 0;

                for(OrderHead h:pendingOrder){
                     if (CustomerCall.isVisited(getActivity().getApplicationContext(),h.outlet_id)){
                         if(!NgantriInformation.isOrderAntrian(getActivity().getApplicationContext(),h.order_id)){
                            cntPend = cntPend + 1;
                         }
                     }
                }

                if (cntPend > 0 ){
                    AlertDialog.Builder xbuilder = new AlertDialog.Builder(getActivity());
                    xbuilder.setMessage("Ada "+String.valueOf(cntPend) +" order pending. Re-sync sekarang?");
                    xbuilder.setTitle(Helper.getStrResource(getActivity(),R.string.common_msg_warning));

                    xbuilder.setPositiveButton(Helper.getStrResource(getActivity(),R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences xsession = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                            String xcurSls = xsession.getString("CUR_SLS", "");
                            String xcurCompany = xsession.getString("CUR_COMPANY", "");

                            for(OrderHead oh:pendingOrder){

                                if (CustomerCall.isVisited(getActivity().getApplicationContext(),oh.outlet_id)){
                                    if (!NgantriInformation.isOrderAntrian(getActivity().getApplicationContext(),oh.order_id)) {
                                        Synchronous sin = new Synchronous( getActivity().getApplicationContext(), xdb, xcurCompany, xcurSls);
                                        sin.postData(Long.parseLong(oh.order_id), null);
                                    }
                                }
                            }
                        }
                    });

                    xbuilder.setNegativeButton(Helper.getStrResource(getActivity(),R.string.common_msg_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    xbuilder.create().show();
                }else{
                    Helper.showToast(getActivity().getApplicationContext(),"Tidak ada order pending");
                }

                break;

            case R.id.action_extract:
                if (parents!=null) parents.clear();

                SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);

                String currentSalesman = session.getString("CUR_SLS", "");
                String curCompany = session.getString("CUR_COMPANY", "");
                SQLiteDatabase db = DBManager.getInstance(getActivity()).database();

                String filename =  currentSalesman+"_"+Helper.getCurrentDateTime("yyyyMMdd")+".txt";
                List<OrderHead> lists = com.ksni.roots.ngsales.model.Order.getPendingHeadOrder(getActivity());

                if(lists.size()>0) {
                    JSONArray joutput = new JSONArray();
                    Helper.getExternalPath();
                    int cnt = 0;
                    Synchronous s = new Synchronous(getActivity(),db,curCompany,currentSalesman);
                    for (OrderHead list : lists) {

                        JSONObject jo= s.extract(getActivity(), Integer.parseInt(list.order_id));
                        if (jo!=null) {
                            cnt++;
                            joutput.put(jo);
                            // disable by antrian
                            //Order.setSuccess(db, String.valueOf(list.order_id));
                        }


                        //JSONObject jo= s.extract(getActivity(), Helper.getExternalPath() + "/" + filename, Integer.parseInt(list.order_id));


                    }

                    try {
                        FileOutputStream outStream = new FileOutputStream(Helper.getExternalPath() + "/" + filename);
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));

                        bw.write(joutput.toString());
                        bw.close();
                        outStream.close();
                        Helper.showToast(getActivity().getApplicationContext(), String.valueOf(cnt) + " data has been extracted.");
                        new OrderActivityAsync().execute();
                    }
                    catch (IOException e) {
                        Helper.showToast(getActivity().getApplicationContext(),"Extract error");
                    }
                    catch (Exception e){
                        Helper.showToast(getActivity().getApplicationContext(),"Extract error");
                    }

                }else{
                    Helper.showToast(getActivity().getApplicationContext(),"No data to extract.");
                }


                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_view_order, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //getSupp ActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);



        Resources res = this.getResources();
        Drawable devider = res.getDrawable(R.drawable.line);

        getExpandableListView().setBackground(res.getDrawable(R.drawable.bar_bg));


        getExpandableListView().setGroupIndicator(null);
        getExpandableListView().setDivider(devider);
        getExpandableListView().setChildDivider(devider);
        registerForContextMenu(getExpandableListView());
        getExpandableListView().setDividerHeight(1);
        new OrderActivityAsync().execute("load");
    }

*/
    private class OrderActivityAsync extends AsyncTask<String, Void, List<HeaderStruct>> {

        @Override
        protected void onPreExecute() {

            elv.setVisibility(View.GONE);
            progressDialog.setVisibility(View.VISIBLE);
        }


        protected void onProgressUpdate(Integer... progress){
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }


        @Override
        protected List<HeaderStruct> doInBackground(String... params) {
            String last = "";
            SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
            String sls= sp.getString("CUR_SLS", null);

            List<HeaderStruct> listOrder = new ArrayList<HeaderStruct>();

            DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
            Cursor cur = dm.database().rawQuery("SELECT a.order_type, a.total_discount,b.total_net,b.item_type,a.status,c.outlet_name,a.notes,a.order_id,a.order_date,a.outlet_id,a.grand_total,  " +
                                   "       b.uom,b.product_id,b.item,b.description,b.qty,b.uom " +
                                   "FROM sls_order a LEFT JOIN sls_order_item b ON a.order_id=b.order_id  "+
                                   "INNER JOIN sls_customer c ON c.outlet_id = a.outlet_id WHERE DATE(a.order_date)=? ORDER BY a.order_id DESC,b.item", new String[]{Helper.getCurrentDate()});

            if (cur.moveToFirst()) {
                do{
                    HeaderStruct ord = new HeaderStruct();
                    //Log.e("UID",cur.getString(cur.getColumnIndex("outlet_id")));
                    ord.order_id = cur.getInt(cur.getColumnIndex("order_id"));
                    ord.status = cur.getInt(cur.getColumnIndex("status"));
                    ord.order_type = cur.getInt(cur.getColumnIndex("order_type"));
                    ord.order_date = cur.getString(cur.getColumnIndex("order_date"));
                    ord.outlet_name = cur.getString(cur.getColumnIndex("outlet_name"));
                    ord.product_id = cur.getString(cur.getColumnIndex("product_id"));
                    ord.description = cur.getString(cur.getColumnIndex("description"));
                    ord.uom = cur.getString(cur.getColumnIndex("uom"));
                    ord.qty = cur.getInt(cur.getColumnIndex("qty"));
                    ord.total_net = cur.getDouble(cur.getColumnIndex("total_net"));
                    ord.grand_total = cur.getDouble(cur.getColumnIndex("grand_total"));
                    ord.total_discount = cur.getDouble(cur.getColumnIndex("total_discount"));
                    ord.item_type=cur.getString(cur.getColumnIndex("item_type"));

                    listOrder.add(ord);

                }while (cur.moveToNext());

            }
            cur.close();
            //dm.close();

            return listOrder;
        }


        @Override
        protected void onPostExecute(List<HeaderStruct> list_ord) {

            progressDialog.setVisibility(View.GONE);
            elv.setVisibility(View.VISIBLE);
            String buff = "";
            ViewOrderParent parent =null;
            ViewOrderChild child=null;
            boolean tambah =false;
            ArrayList<ViewOrderParent> list = new ArrayList<ViewOrderParent>();

            for(HeaderStruct ord:list_ord){

                try {

                    if (!buff.equals(String.valueOf(ord.order_id))){
                        //header bro
                        tambah = true;
                        parent = new ViewOrderParent();
                        String tipeOrder = "";
                        if (ord.order_type==Order.REGULAR_ORDER)
                            tipeOrder = "REGULAR ORDER";
                        else if (ord.order_type==Order.RETURN_ORDER)
                            tipeOrder = "RETURN ORDER";

                        parent.setName(ord.outlet_name);
                        parent.setText1(ord.outlet_name);
                        parent.setText2(tipeOrder+ "\nOrder Id :"+String.valueOf(ord.order_id)+"\nOrder date: "+ord.order_date);
                        parent.setTotal("Total: " + Helper.getFormatCurrencyWithDigit(ord.grand_total) + ", Disc: " + Helper.getFormatCurrencyWithDigit(ord.total_discount));
                        parent.setStatus(ord.status);
                        parent.setChildren(new ArrayList<ViewOrderChild>());
                        buff = String.valueOf(ord.order_id);
                    }
                    else
                        tambah = false;


                    if (ord.product_id!=null){
                        child = new ViewOrderChild();
                        child.setName(ord.product_id);
                        child.setItemType(ord.item_type);
                        child.setText1(ord.description);
                        child.setText2("Qty:" + ord.qty + " " + ord.uom+", Net:"+ Helper.getFormatCurrency(ord.total_net));

                        parent.getChildren().add(child);
                    }

                    if (tambah)list.add(parent);


                }

                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            loadHosts(list);
            //final ViewOrderListAdapter mAdapter = new ViewOrderListAdapter();



            //elv.setAdapter(mAdapter);


        }

    }

    private void loadHosts(final ArrayList<ViewOrderParent> newParents)
    {
        if (newParents == null)
            return;

        parents = newParents;

        if (elv.getExpandableListAdapter() == null) {
            final ViewOrderListAdapter mAdapter = new ViewOrderListAdapter();
            elv.setAdapter(mAdapter);
        }
        else
        {
            ((ViewOrderListAdapter)elv.getExpandableListAdapter()).notifyDataSetChanged();
        }
    }

    private class ViewOrderListAdapter extends BaseExpandableListAdapter {


        private LayoutInflater inflater;

        public ViewOrderListAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parentView)
        {
            final ViewOrderParent parent = parents.get(groupPosition);

            // Inflate grouprow.xml file for parent rows
            convertView = inflater.inflate(R.layout.grouprow, parentView, false);

            // Get grouprow.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.text1)).setText(parent.getText1());
            ((TextView) convertView.findViewById(R.id.text)).setText(parent.getText2());
            ((TextView) convertView.findViewById(R.id.txtTotal)).setText(parent.getTotal());

            TextView tv = ((TextView) convertView.findViewById(R.id.tStatus));
            if (parent.getStatus()==1) {
                tv.setText("Synced");
            }
            else{
                tv.setText("Ready for sync");
            }


            //ImageView image=(ImageView)convertView.findViewById(R.id.image);
            //image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"+parent.getName(),null,null));
            return convertView;
        }


        // This Function used to inflate child rows view
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView)
        {
            final ViewOrderParent parent = parents.get(groupPosition);
            final ViewOrderChild child = parent.getChildren().get(childPosition);

            // Inflate childrow.xml file for child rows
            convertView = inflater.inflate(R.layout.childrow, parentView, false);

            // Get childrow.xml file elements and set values

            ((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
            ((TextView) convertView.findViewById(R.id.text2)).setText(child.getText2());

            if (child.getItemType().equals("F")){
                ((TextView) convertView.findViewById(R.id.text1)).setTextColor(Color.RED);
                ((TextView) convertView.findViewById(R.id.text2)).setTextColor(Color.DKGRAY);
            }else{
                ((TextView) convertView.findViewById(R.id.text1)).setTextColor(Color.WHITE);
                ((TextView) convertView.findViewById(R.id.text2)).setTextColor(Color.DKGRAY);
            }


            //ImageView image=(ImageView)convertView.findViewById(R.id.image);
            //image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"+parent.getName(),null,null));

            return convertView;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
            return parents.get(groupPosition).getChildren().get(childPosition);
        }

        //Call when child row clicked
        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            if( ChildClickStatus!=childPosition)
            {
                ChildClickStatus = childPosition;

            }

            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            int size=0;
            if(parents.get(groupPosition).getChildren()!=null)
                size = parents.get(groupPosition).getChildren().size();
            return size;
        }


        @Override
        public Object getGroup(int groupPosition)
        {
            //Log.i("Parent", groupPosition+"=  getGroup ");

            return parents.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return parents.size();
        }

        //Call when parent row clicked
        @Override
        public long getGroupId(int groupPosition)
        {
           // Log.i("Parent", groupPosition+"=  getGroupId "+ParentClickStatus);

            if(groupPosition==2 && ParentClickStatus!=groupPosition){

                //Alert to user
                //Toast.makeText(getApplicationContext(), "Parent :"+groupPosition ,
                //	Toast.LENGTH_LONG).show();
            }

            ParentClickStatus=groupPosition;
            if(ParentClickStatus==0)
                ParentClickStatus=-1;

            return groupPosition;
        }

        @Override
        public void notifyDataSetChanged()
        {
            // Refresh List rows
            super.notifyDataSetChanged();
        }

        @Override
        public boolean isEmpty()
        {
            return ((parents == null) || parents.isEmpty());
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled()
        {
            return true;
        }




    }



}
