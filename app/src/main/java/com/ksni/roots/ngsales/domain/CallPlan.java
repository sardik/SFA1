package com.ksni.roots.ngsales.domain;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.model.Customer;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class CallPlan extends Fragment {
    public static CallPlanAdapter adapter;
    private String curid,customerNotes,customerNumber,customerName,zone,channel,customer_group,curWeek,curDate;
    private long idcur = -1;
    private long orderid = -1;
    private long returnid = -1;
    private View rootView;
    private ListView lv;
    private boolean regularOrder;
    private ProgressDialog progressDialog;
    private String btnc = "";
    private AlertDialog dlgReason;
    //private boolean india;
    private Toolbar toolbar;
    private long globalOrderId = -1;
    private long globalReturnId = -1;
    private EditText searchPlan;
    AlertDialog dlgWeek;
    private static final int REQUEST_ADD_ITEM   =   9;
    private List<CustomerCall> listCallPlan = new ArrayList<CustomerCall>();
    //private BroadcastReceiver bdPlan = null;

    private SwipeRefreshLayout srCallPlan;



    private boolean checkGPS(double lat1,double long1,double lat2,double long2){
        final float[] results = new float[3];
        //read config allow radius tolerance
        String outlet_radius_coordinate = Config.getValue(getActivity().getApplicationContext(), "outlet_radius_coordinate");
        int tolerance = 500;
        if (Helper.isNumber(outlet_radius_coordinate)) {
            int parse = Integer.parseInt(outlet_radius_coordinate);
            if (parse != 0) tolerance = parse;
        }

        Location.distanceBetween(lat1, long1, lat2, long2, results);
        if (results[0] > tolerance) // radius lebih dari x meter
            return false;
        else
            return true;
    }

    private boolean isAllow(double lat1,double long1,double lat2,double long2){
        boolean allow = true;
        //read config allow allow_outlet_coordinate_check
        boolean allow_outlet_coordinate_check = Config.getChecked(getActivity().getApplicationContext(), "allow_outlet_coordinate_check");
        allow = allow_outlet_coordinate_check;

        if (allow) { // allow coordinate check
            boolean allow_to_without_gps = Config.getChecked(getActivity().getApplicationContext(), "allow_to_without_gps");
            // check GPS current
            SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
            String testLat = session.getString("LATITUDE", "0");
            Double parseLat = Double.parseDouble(testLat);
            if (parseLat==0){
               if (allow_to_without_gps){ // check allow gps null
                   allow=true;
               }else{
                   // check tolerance
                    allow= checkGPS(lat1,long1,lat2,lat2);
               }
            }else{
                // check tolerance
                allow= checkGPS(lat1,long1,lat2,lat2);
            }
        }else{
            allow = true;
        }

        return allow;
    }

    private void loadLastData(SQLiteDatabase db,
                              long last_id,
                              String cust){
        com.ksni.roots.ngsales.model.Order ord = com.ksni.roots.ngsales.model.Order.getData(db, last_id, cust, false);
        if (ord!=null) {

            SharedPreferences sessionX = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
            SharedPreferences.Editor e = sessionX.edit();
            e.putLong("CUR_TRANSACTION", last_id);
            e.commit();
            MainActivity.dataOrder.clear();
            MainActivity.dataOrder.addAll(ord.getItems());

            //MainActivity.dataOrder.addAll(ord.getItems());

        }

    }

    private void populateTemplate(String id){
        DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
        SQLiteDatabase db = dm.database();

//        MainActivity.dataOrder.clear();


        long last_id = com.ksni.roots.ngsales.model.Order.getLastId(db, customerNumber);
        if (last_id!=-1){
            loadLastData(db,last_id,customerNumber);
        }
        else
        {
            int cnt = 0;

            List<OrderItem> ords = CustomerCall.getTemplate(dm.database(), customerNumber);

            MainActivity.dataOrder.clear();

            List<Product> produks = Product.getTemplateNPLFocus(db);
            for(Product produk:produks){
                boolean ada= false;
                for(OrderItem ox:  ords){
                    if (produk.getProcutId().equals(ox.productId)){
                        ada = true;
                        break;
                    }
                }
            if (!ada) {
                OrderItem itm = new OrderItem();
                itm.largeToSmall = produk.getConversionLargeToSmall();
                itm.mediumToSmall = produk.getConversionMediumToSmall();
                itm.uom = produk.getUom();
                itm.qty = 0;
                itm.lastQty = 0;
                itm.lastUom = itm.uom;
                itm.price = 0;
                itm.division = produk.getDivision();
                itm.suggestQty = 0;
                itm.suggestUom = itm.uom;
                itm.stockQty = 0;

                itm.uomLarge = produk.getUomLarge();
                itm.uomMedium = produk.getUomSmall();
                itm.uomSmall = produk.getUomSmall();

                itm.itemType = "N";

                itm.stockUom = itm.uom;
                itm.brand = produk.getBrand();
                itm.productId = produk.getProcutId();
                itm.productName = produk.getProductName();
                com.ksni.roots.ngsales.domain.Pricing prc = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, itm.price * itm.qty, Helper.getCurrentDate(),getActivity().getApplicationContext());
                itm.price = prc.getPrice();
                itm.regularDiscount = prc.getDiscountReg();
                itm.extraDiscount = prc.getDiscountExt();
                itm.specialDiscount = prc.getDiscountSpec();
                cnt++;
                itm.id = cnt;
                itm.refItem = 0;
                MainActivity.dataOrder.add(itm);
            }
            }



            for (OrderItem itm : ords) {
                com.ksni.roots.ngsales.domain.Pricing prc = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId,itm.uom,itm.qty,itm.price * itm.qty,Helper.getCurrentDate(),getActivity().getApplicationContext());
                itm.price = prc.getPrice();
                itm.regularDiscount = prc.getDiscountReg();
                itm.extraDiscount = prc.getDiscountExt();
                itm.specialDiscount = prc.getDiscountSpec();
                itm.itemType = "N";
                cnt++;
                itm.id = cnt;
                itm.refItem = 0;
                MainActivity.dataOrder.add(itm);
            }

        }

    }

    private void callCustomer(CustomerCall xcall, int pos, boolean scanned){
        //check GPS
        final CustomerCall call = xcall;
        final int posx = pos;
        SharedPreferences zsession = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        String curLat = zsession.getString("LATITUDE", "0");
        String curLong = zsession.getString("LONGITUDE", "0");

        double doubleLat = Double.parseDouble(curLat);
        double doubleLong = Double.parseDouble(curLong);

        if(!isAllow(call.getLatitude(),call.getLongitude(),doubleLat,doubleLong )) {
            Helper.showToast(getActivity().getApplicationContext(), Helper.getStrResource(getActivity(), R.string.call_plan_restict_invalid_location));
        }else{
            SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
            // load from callPlan Init current visit
            String curCust = session.getString("CUR_VISIT", "");
            String curCustName = session.getString("CUR_VISIT_NAME", "");
            String curStart = session.getString("CUR_WORK_START", "");
            String curEnd = session.getString("CUR_WORK_END", "");

            curWeek = call.getWeek();
            curid = call.getId(); // id call plan customer
            customerNumber = call.getCustomerNumber();
            customerName = call.getCustomerName();
            zone = call.getZone();
            channel = call.getChannel();
            customer_group = call.getCustomerGruop();
            curDate = call.getServerDate();
            customerNotes =   call.getNotes() == null ? "":call.getNotes();

            if (curStart == "" || curEnd != "") {
                Helper.showToast(getActivity().getApplicationContext(), Helper.getStrResource(getActivity(),R.string.call_plan_no_work_start));
            } else {
                // Check Visit Status, VISITED
                if (call.getStatus().equals(CustomerCall.VISITED) && curCust == "") {

                    //read config re-call / re-visit
                    boolean allow_revisit_customer = Config.getChecked(getActivity().getApplicationContext(), "allow_revisit_customer");

                    DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
                    SQLiteDatabase db = dm.database();
                    com.ksni.roots.ngsales.model.Order ord = new com.ksni.roots.ngsales.model.Order(db);

                    if (!allow_revisit_customer) {
                        Helper.msgbox(getActivity(), Helper.getStrResource(getActivity(),R.string.call_plan_restict_recall), Helper.getStrResource(getActivity(),R.string.common_msg_warning));
                    } else if (ord.isOrder(getActivity().getApplicationContext(), customerNumber)) {
                        Toast.makeText(getActivity().getApplicationContext(),"Sudah melakukan transaksi order",Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(Helper.getStrResource(getActivity(),R.string.call_plan_msg_confirm_call_back));
                        builder.setTitle(Helper.getStrResource(getActivity(),R.string.common_msg_confirm));

                        builder.setPositiveButton(Helper.getStrResource(getActivity(),R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog.Builder builderX = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                final View lay = inflater.inflate(R.layout.ui_order_view, null);

                                TextView tv = (TextView) lay.findViewById(R.id.list_empty);
                                tv.setText(Helper.getStrResource(getActivity(),R.string.common_msg_no_data_found));

                                Toolbar t = (Toolbar) lay.findViewById(R.id.toolbar);
                                t.setVisibility(View.GONE);

                                FloatingActionsMenu fff = (FloatingActionsMenu) lay.findViewById(R.id.multiple_actions);
                                fff.setVisibility(View.GONE);

                                DBManager dm = DBManager.getInstance(getActivity());
                                //globalOrderId = com.ksni.roots.ngsales.model.Order.getLastId(dm.database(), customerNumber);
                                //globalOrderId = com.ksni.roots.ngsales.model.Order.getLastIdAll(dm.database(), customerNumber);

                                globalOrderId = call.getOrderId();
                                globalReturnId = call.getReturnId();

                                idcur = -1;

                                if (globalOrderId > 0) {
                                    regularOrder = true;
                                    idcur = globalOrderId;
                                } else {
                                    if (globalReturnId > 0) {
                                        regularOrder = false;
                                        idcur = globalReturnId;
                                    }
                                }


                                // lock order
                                //com.ksni.roots.ngsales.model.Order.lockUnlockOrder(getActivity(),globalOrderId,1);
                                final com.ksni.roots.ngsales.model.Order last_order = com.ksni.roots.ngsales.model.Order.getData(dm.database(), idcur, customerNumber, false);
                                final List<OrderItem> ords = last_order.getItems();
                                final OrderAdapter adapterX = new OrderAdapter(getActivity(), R.layout.ui_order_item, ords, (TextView) lay.findViewById(R.id.tTotal));
                                final ListView lvx = (ListView) lay.findViewById(R.id.lstOrderItem);


                                lvx.setEmptyView(lay.findViewById(R.id.list_empty));
                                lvx.setStackFromBottom(false);
                                lvx.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                                lvx.setAdapter(adapterX);
                                TextView tEmpty = (TextView) lay.findViewById(R.id.list_empty);
                                //if (ords.size() > 0)
                                if (globalOrderId != -1 || globalReturnId != -1) {
                                    if (regularOrder)
                                        btnc = Helper.getStrResource(getActivity(),R.string.call_plan_button_text_change_order);
                                    else
                                        btnc = Helper.getStrResource(getActivity(),R.string.call_plan_button_text_change_return);

                                    tEmpty.setVisibility(View.GONE);
                                } else {
                                    tEmpty.setVisibility(View.VISIBLE);
                                    btnc = Helper.getStrResource(getActivity(),R.string.call_plan_button_text_create_order);
                                }

                                builderX.setPositiveButton(btnc, null);
                                builderX.setNegativeButton(Helper.getStrResource(getActivity(),R.string.common_msg_cancel), null);

                                builderX.setView(lay);

                                final AlertDialog ad = builderX.create();
                                //if (ords.size() > 0)
                                if (globalOrderId != -1 || globalReturnId != -1) {
                                    if (regularOrder)
                                        ad.setTitle(Helper.getStrResource(getActivity(),R.string.call_plan_title_view_order));
                                    else
                                        ad.setTitle(Helper.getStrResource(getActivity(),R.string.call_plan_title_view_return));
                                } else
                                    ad.setTitle(Helper.getStrResource(getActivity(),R.string.common_msg_no_data_found));

                                ad.setCanceledOnTouchOutside(false);
                                ad.setCancelable(false);


                                ad.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {

                                        Button b = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                                        b.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (idcur > -1) {
                                                    if (last_order != null) {
                                                        MainActivity.dataOrder.clear();
                                                        MainActivity.dataOrder.addAll(last_order.getItems());

                                                        // reopen order
                                                        // diaktifin lg 13 july 2016 -->
                                                        if (globalOrderId>0)
                                                            com.ksni.roots.ngsales.model.Order.reOpenOrder(DBManager.getInstance(getActivity().getApplicationContext()).database(),String.valueOf(globalOrderId));

                                                        if (globalReturnId>0)
                                                            com.ksni.roots.ngsales.model.Order.reOpenOrder(DBManager.getInstance(getActivity().getApplicationContext()).database(),String.valueOf(globalReturnId));

                                                        //-------


                                                        SharedPreferences sessionX = getActivity().getSharedPreferences("ngsales", 0);
                                                        SharedPreferences.Editor e = sessionX.edit();

                                                        e.putLong("TRANSACTION_SAVED_ORDER", globalOrderId);
                                                        e.putLong("TRANSACTION_SAVED_RETURN", globalReturnId);

                                                        Log.e("globalOrderId", String.valueOf(globalOrderId));
                                                        Log.e("globalReturnId", String.valueOf(globalReturnId));

                                                        if (globalOrderId > 0) {
                                                            e.putLong("CUR_TRANSACTION", globalOrderId);
                                                            e.putLong("CUR_TRANSACTION_RETURN", -1);
                                                            e.putInt("CUR_ORDER_TYPE", com.ksni.roots.ngsales.model.Order.REGULAR_ORDER);

                                                        } else {
                                                            if (globalReturnId > 0) {
                                                                e.putLong("CUR_TRANSACTION", -1);
                                                                e.putLong("CUR_TRANSACTION_RETURN", globalReturnId);
                                                                e.putInt("CUR_ORDER_TYPE", com.ksni.roots.ngsales.model.Order.RETURN_ORDER);
                                                            }
                                                        }

                                                        e.commit();
                                                        startOrder(false);
                                                        ad.dismiss();

                                                    }
                                                } else {
                                                    ad.dismiss();
                                                    startOrder(false);
                                                }

                                            }
                                        });


                                        Button c = ad.getButton(AlertDialog.BUTTON_NEGATIVE);
                                        c.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {
                                                ad.dismiss();
                                            }
                                        });

                                    }
                                });


                                ad.show();


                            }
                        });

                        builder.setNegativeButton(Helper.getStrResource(getActivity(),R.string.common_msg_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.create().show();
                    }
                } else { // Check Visit Status, Visiting / Paused / Not Visited

                    if (curCust != "" && !call.getCustomerNumber().equals(curCust)) {
                        Helper.showToast(getActivity().getApplicationContext(),  curCustName.toUpperCase() + " " + Helper.getStrResource(getActivity(),R.string.call_plan_msg_still_visit));
                    } else {
                        // Check visiting/paused atau not visited
                        if ((call.getStatus().equals(CustomerCall.VISIT) && curCust != "") || (call.getStatus().equals(CustomerCall.PAUSED))) {   // Jika sedang visit/paused outlet, maka resume

                            curWeek = call.getWeek();
                            curid = call.getId(); // id call plan
                            customerNumber = call.getCustomerNumber();
                            customerName = call.getCustomerName();
                            zone = call.getZone();
                            channel = call.getChannel();
                            customer_group = call.getCustomerGruop();
                            curDate = call.getServerDate();
                            //customerNotes = call.getNotes();
                            customerNotes =   call.getNotes() ==null?"":call.getNotes();
                            orderid = call.getOrderId();
                            returnid = call.getReturnId();

                            SharedPreferences sessionx = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                            SharedPreferences.Editor e = sessionx.edit();

                            e.putString("FORCE_END_CALL", "0");
                            e.putString("CUR_VISIT", customerNumber);
                            e.putString("CUR_VISIT_NAME", customerName);
                            e.putString("CUR_VISIT_CHANNEL", channel);
                            e.putString("CUR_WEEK", curWeek);
                            e.putString("CUR_VISIT_ZONE", zone);
                            e.putString("CUR_VISIT_GROUP", customer_group);
                            e.putString("CUR_VISIT_DATE", curDate);
                            e.putString("CUR_VISIT_NOTES", customerNotes);
                            e.putLong("CUR_TRANSACTION", orderid);
                            e.putLong("CUR_TRANSACTION_RETURN", returnid);
                            e.putInt("CUR_ORDER_TYPE", com.ksni.roots.ngsales.model.Order.REGULAR_ORDER);
                            e.putLong("TRANSACTION_SAVED_ORDER", orderid);
                            e.putLong("TRANSACTION_SAVED_RETURN", returnid);
                            e.commit();

                            if (call.getStatus().equals(CustomerCall.PAUSED)) { // Jika Paused
                                String waktu_resume = Helper.getCurrentDateTime();
                                adapter.getItem(pos).setStatus(CustomerCall.VISIT);

                                String awal = adapter.getItem(pos).getPauseTime();
                                String akhir = waktu_resume;
                                long selisih = Helper.getTimeElapsedMinute(awal, akhir);
                                long dlong = selisih + adapter.getItem(pos).getDuration();
                                adapter.getItem(pos).setDuration(dlong);
                                adapter.getItem(pos).setLastResume(waktu_resume);
                                adapter.notifyDataSetChanged();
                                call.resumeCall(DBManager.getInstance(getActivity()).database(), waktu_resume, dlong);

                            }

                            Intent intCall = new Intent(getActivity(), PreCall.class);
                            startActivity(intCall);

                            Intent intOrd = new Intent(getActivity(), Order.class);
                            startActivity(intOrd);

                        } else { // Jika baru pertama kali mau visit outlet, maka scan barcode terlebih dahulu

                            boolean scan_barcode = Config.getChecked(getActivity().getApplicationContext(), "scan_barcode");

                            if (!scanned && scan_barcode){
                                DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
                                final List<ReasonNoBarcode> ls = ReasonNoBarcode.getData(dm.database());
                                final List<String> s = new ArrayList<String>();

                                for (ReasonNoBarcode r : ls) {
                                    s.add(r.getReason() + " " + r.getDescription());
                                }


                                final CharSequence[] items = s.toArray(new CharSequence[s.size()]);


                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(Helper.getStrResource(getActivity(), R.string.transaction_order_title_reason_no_barcode));
                                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        ReasonNoBarcode r = (ReasonNoBarcode) ls.get(item);
                                        adapter.getItem(posx).setReasonNoBarcode(r.getReason());
                                        adapter.notifyDataSetChanged();
                                        call.setReasonNoBarcode(DBManager.getInstance(getActivity().getApplicationContext()).database(), r.getReason());
                                        startOrder(true);
                                        //Log.e("r.getReason()", r.getReason());
                                        dlgReason.dismiss();
                                    }
                                });

                                dlgReason = builder.create();
                                dlgReason.show();

                            }else {
                                startOrder(true);
                            }



                            //read config allow scan barcode
                            /*
                            boolean scan_barcode = Config.getChecked(getActivity().getApplicationContext(), "scan_barcode");
                            if(!scan_barcode){
                                startOrder(true);
                            }else {
                                // pertama kunjungan
                                final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
                                try {
                                    Intent intent = new Intent(ACTION_SCAN);
                                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivityForResult(intent, 0);
                                } catch (ActivityNotFoundException anfe) {

                                }
                            }

                            */


                        }
                    }

                }

            }
        }
    }

    private void startScan(CustomerCall xcall,int pos, boolean scanned){
        final CustomerCall call = xcall;
        final int posx = pos;
        boolean scan_barcode = Config.getChecked(getActivity().getApplicationContext(), "scan_barcode");
        if (!scanned && scan_barcode){
            DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
            final List<ReasonNoBarcode> ls = ReasonNoBarcode.getData(dm.database());
            final List<String> s = new ArrayList<String>();

            for (ReasonNoBarcode r : ls) {
                s.add(r.getReason() + " " + r.getDescription());
            }


            final CharSequence[] items = s.toArray(new CharSequence[s.size()]);


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(Helper.getStrResource(getActivity(), R.string.transaction_order_title_reason_no_barcode));
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    ReasonNoBarcode r = (ReasonNoBarcode) ls.get(item);
                    adapter.getItem(posx).setReasonNoBarcode(r.getReason());
                    adapter.notifyDataSetChanged();
                    call.setReasonNoBarcode(DBManager.getInstance(getActivity().getApplicationContext()).database(), r.getReason());
                    startOrder(true);
                    //Log.e("r.getReason()", r.getReason());
                    dlgReason.dismiss();
                }
            });

            dlgReason = builder.create();
            dlgReason.show();

        }else {
            startOrder(true);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.ui_call_plan, container, false);
        lv = (ListView) rootView.findViewById(R.id.lstCallPlan);
        //srCallPlan = (SwipeRefreshLayout) rootView.findViewById(R.id.srCallPlan);

        listCallPlan.clear();
        listCallPlan.addAll(MainActivity.dataCustomerCall);

        //Checkbox Suggestion Route
        //Sorting Jarak Outlet
        CheckBox chkSuggestionRoute = (CheckBox)rootView.findViewById(R.id.chkSuggestRoute);
        chkSuggestionRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    CallPlanInit.loadPlan(getActivity().getApplicationContext());
                    CallPlanInit.sortJarak();
                    listCallPlan.clear();
                    listCallPlan.addAll(MainActivity.dataCustomerCall);
                    adapter = new CallPlanAdapter(getActivity(), R.layout.ui_call_plan_item, listCallPlan,CallPlan.this);
                    lv.setAdapter(adapter);
                }else{
                    CallPlanInit.loadPlan(getActivity().getApplicationContext());
                    listCallPlan.clear();
                    listCallPlan.addAll(MainActivity.dataCustomerCall);
                    adapter = new CallPlanAdapter(getActivity(), R.layout.ui_call_plan_item, listCallPlan,CallPlan.this);
                    lv.setAdapter(adapter);
                }
            }
        });

        //String base_price_value = Config.getValue(getActivity().getApplicationContext(), "base_price_value");
        //double base_value = Double.parseDouble(base_price_value);

        //if (base_value>0)
        //    india = true;
        //else
         //   india = false;

        adapter = new CallPlanAdapter(getActivity(), R.layout.ui_call_plan_item, listCallPlan, CallPlan.this);
        lv.setAdapter(adapter);
        lv.setTextFilterEnabled(true);
        lv.setEmptyView(rootView.findViewById(R.id.list_empty));

        // LONG PRESS, add context menu, mirip AlertDialog / POPUP untuk ListView >> lv
        //registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    final int pos = position;
                    final CustomerCall call = (CustomerCall) parent.getItemAtPosition(position);
                    String[] menuItems = getResources().getStringArray(R.array.menu_call_plan);

                    SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                    String curCust = session.getString("CUR_VISIT", "");
                    String curCustName = session.getString("CUR_VISIT_NAME", "");

                    if(call.getStatus().equals(CustomerCall.NO_VISIT) && curCust == "") { // UNVISITED
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int menuItemIndex) {
                                if (menuItemIndex == 0) {
                                    callCustomer(call, pos, false);
                                } else if (menuItemIndex == 1) { //
                                    if (call.getCallStatus().equals("1") && call.getStatus().equals(CustomerCall.NO_VISIT)) {
                                        final DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
                                        final List<ReasonNoCall> ls = ReasonNoCall.getData(dm.database());
                                        final List<String> s = new ArrayList<String>();

                                        for (ReasonNoCall r : ls) {
                                            s.add(r.getReason() + " " + r.getDescription());
                                        }

                                        final CharSequence[] items = s.toArray(new CharSequence[s.size()]);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle(Helper.getStrResource(getActivity(), R.string.transaction_order_title_reason_no_call));
                                        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {

                                                ReasonNoCall r = (ReasonNoCall) ls.get(item);

                                                // send to server directly
                                                SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                                                String curSls = session.getString("CUR_SLS", "");
                                                String curCompany = session.getString("CUR_COMPANY", "");

                                                NgantriInformation ngantri = new NgantriInformation(getActivity().getApplicationContext());
                                                ngantri.key = NgantriInformation.KEY_NO_CALL;
                                                try {

                                                    JSONObject param = new JSONObject();
                                                    param.put("command", "no_call");
                                                    param.put("salesman_id", curSls);
                                                    param.put("company_id", curCompany);
                                                    param.put("outlet_id", call.getCustomerNumber());
                                                    param.put("no_call_date", Helper.getCurrentDate());
                                                    param.put("reason_no_call_id", r.getReason());

                                                    ngantri.data = param.toString();
                                                    ngantri.value = curCompany + "_" + curSls;
                                                    ngantri.description = "sending reason no call " + curCompany + "_" + curSls + "_" + call.getCustomerNumber();

                                                    ngantri.addAntrian();
                                                } catch (JSONException ex) {

                                                } catch (Exception ex) {

                                                }

                                                //end request order to server

                                                adapter.getItem(pos).setStatus(CustomerCall.NOCALL);
                                                adapter.notifyDataSetChanged();
                                                call.setNoCall(dm.database());
                                                dlgReason.dismiss();
                                            }
                                        });

                                        dlgReason = builder.create();
                                        dlgReason.show();
                                    } else {
                                        Helper.showToast(getActivity().getApplicationContext(), "Tidak dapat memilih 'alasan tidak dikunjungi'");
                                    }
                                }
                            }
                        });

                        dlgReason = builder.create();
                        dlgReason.show();
                    } else if(call.getStatus().equals(CustomerCall.VISIT) && curCust != "" && !call.getCustomerNumber().equals(curCust)){ // VISITING
                        Helper.showToast(getActivity().getApplicationContext(),  curCustName.toUpperCase() + " " + Helper.getStrResource(getActivity(),R.string.call_plan_msg_still_visit));
                    } else if(call.getStatus().equals(CustomerCall.NOCALL)) { // NOCALL
                        Toast.makeText(getActivity().getApplicationContext(),"Status No Call tidak dapat order",Toast.LENGTH_SHORT).show();
                    } else {
                        callCustomer(call, pos, false);
                    }

            }
                //}
        });

        searchPlan = (EditText) rootView.findViewById(R.id.tSearchPlan);
        searchPlan.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

// GA JADI DIPAKE
//        srCallPlan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                RefreshCallPlan refresh = new RefreshCallPlan();
//                refresh.execute(MainActivity.BASE_URL);
//            }
//        });

        return rootView;

    }

    // GA JADI DIPAKE
    private class RefreshCallPlan extends AsyncTask<String, String, List<CustomerCall>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<CustomerCall> doInBackground(String... params) {

            URL url;
            HttpURLConnection conn;

            Settings settingan = new Settings(getActivity());
            settingan.loadInfo();

            try {
                url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(NgantriInformation.HTTP_CONECTION_TIMEOUT * 1000);
                conn.setReadTimeout(NgantriInformation.HTTP_READ_TIMEOUT * 1000);
                conn.setRequestMethod("POST");

                JSONObject jsonObjRequest = new JSONObject();
                jsonObjRequest.put("command", "sync_call_plan");
                jsonObjRequest.put("salesman_id", settingan.salesman);
                jsonObjRequest.put("company_id", settingan.company);
                jsonObjRequest.put("last_modified", "");
                String jsonStringRequest = jsonObjRequest.toString();

                Log.e("SEND", "Request data = " + jsonStringRequest);

                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write("data=" + jsonStringRequest);
                osw.flush();
                osw.close();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader buff = new BufferedReader(isr);
                    StringBuilder sbResponse = new StringBuilder();

                    String line = null;
                    while ((line=buff.readLine()) != null) {
                        sbResponse.append(line);
                    }

                    Log.e("GET", "Response data = " + sbResponse);

                    String jsonStringResponse = sbResponse.toString();
                    JSONObject jsonObjResponse = new JSONObject(jsonStringResponse);

                    JSONArray arrCallPlan = jsonObjResponse.getJSONArray("call_plans");
                    JSONArray arrLastCallPlan = jsonObjRequest.getJSONArray("last_calls");

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<CustomerCall> customerCalls) {
            super.onPostExecute(customerCalls);

            srCallPlan.setRefreshing(false);


        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_call_plan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //if (bdPlan!=null) getActivity().unregisterReceiver(bdPlan);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.btnScan:
                //read config allow scan barcode
                boolean scan_barcode = Config.getChecked(getActivity().getApplicationContext(), "scan_barcode");
                if(scan_barcode){
                    // pertama kunjungan
                    final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
                    try {
                        Intent intent = new Intent(ACTION_SCAN);
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(intent, 0);
                    } catch (ActivityNotFoundException anfe) {

                    }
                }
                break;
            case R.id.btnRefresh:

                CallPlanInit.loadPlan(getActivity().getApplicationContext());
                listCallPlan.clear();
                listCallPlan.addAll(MainActivity.dataCustomerCall);
                adapter = new CallPlanAdapter(getActivity(), R.layout.ui_call_plan_item, listCallPlan,CallPlan.this);
                lv.setAdapter(adapter);

                // hold by antrian new AsyncCallPlan().execute(MainActivity.BASE_URL);

                //start request callplan to server

                    /*NgantriInformation ngantri = new NgantriInformation (getActivity().getApplicationContext());
                    ngantri.key = NgantriInformation.KEY_CALL_PLAN;
                    try{
                        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                        String currentSalesman = session.getString("CUR_SLS", "");
                        String curCompany = session.getString("CUR_COMPANY", "");

                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("salesman_id", currentSalesman);
                        jsonParam.put("company_id", curCompany);
                        jsonParam.put("command", "sync_call_plan");
                        jsonParam.put("last_modified", "");
                        ngantri.data = jsonParam.toString();
                        ngantri.value = curCompany+"_"+currentSalesman;
                        ngantri.description = "sending call plan " +curCompany+"_"+currentSalesman;
                        ngantri.addAntrian();
                    }catch(JSONException x){}
                     catch(Exception x){}
                     */

                    //end request callplan to server


                break;
            case R.id.btnMap:
                startActivity(new Intent(getActivity(), CallPlanMap.class));
                break;
            case R.id.btnAdd:
                //read config allow acak route
                boolean allow_unplanned_call = Config.getChecked(getActivity().getApplicationContext(), "allow_unplanned_call");
                if (allow_unplanned_call) {
                    Intent intent = new Intent(getActivity(), AddCustomer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, REQUEST_ADD_ITEM);
                }else{
                    Helper.msgbox(getActivity(),Helper.getStrResource(getActivity(),R.string.call_plan_restict_add_unplan),Helper.getStrResource(getActivity(),R.string.common_msg_warning));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startOrder(boolean isNew){
        SharedPreferences session =getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();
        e.putString("FORCE_END_CALL",       "0");
        e.putString("CUR_VISIT",            customerNumber);
        e.putString("CUR_VISIT_NAME",       customerName);
        e.putString("CUR_VISIT_CHANNEL",    channel);
        e.putString("CUR_WEEK",             curWeek);
        e.putString("CUR_VISIT_ZONE",       zone);
        e.putString("CUR_VISIT_GROUP",      customer_group);
        e.putString("CUR_VISIT_DATE",       curDate);
        e.putString("CUR_VISIT_NOTES",      customerNotes);

        if (isNew) {
            e.putInt("CUR_ORDER_TYPE", com.ksni.roots.ngsales.model.Order.REGULAR_ORDER);
            e.putLong("TRANSACTION_SAVED_ORDER", -1);
            e.putLong("TRANSACTION_SAVED_RETURN", -1);
            e.putLong("CUR_TRANSACTION", -1);
            e.putLong("CUR_TRANSACTION_RETURN", -1);
        }

        e.commit();

        DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
        for(int i=0;i<CallPlan.adapter.getCount();i++){
            CustomerCall cc = CallPlan.adapter.getItem(i);
            if (cc.getCustomerNumber().equals(customerNumber)){
                String waktu_call = Helper.getCurrentDateTime();
                adapter.getItem(i).setStartTime(waktu_call);
                adapter.getItem(i).setStatus(CustomerCall.VISIT);
                adapter.getItem(i).setDuration(0);
                //if (isNew) {
               //     adapter.getItem(i).setOrderId(-1);
                //    adapter.getItem(i).setReturnId(-1);
               // }
                adapter.getItem(i).setLastPause(null);
                adapter.getItem(i).setLastResume(null);
                adapter.notifyDataSetChanged();

                cc.startCall(dm.database(), waktu_call);

                SharedPreferences sessionYYY = getActivity().getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor eyyy = sessionYYY.edit();
                eyyy.putString("CUR_REASON_NO_BARCODE", cc.getReasonNoBarcode());
                eyyy.putString("CUR_REASON_NO_ROUTE", cc.getReasonUnroute());

                //Log.e("XXXXXXXXXXXX",cc.getReasonUnroute());

                eyyy.commit();

                break;
            }
        }



        Intent intCall = new Intent(getActivity(),PreCall.class);
        startActivity(intCall);

        Intent intOrd = new Intent(getActivity(),Order.class);
        startActivity(intOrd);

        if(customerNotes==null) {
            customerNotes = "";
        }

        if (customerNotes.length()>0 && isNew) {
            Intent i = new Intent(getActivity(), CustomerNotes.class);
            i.putExtra("notes", customerNotes);
            startActivity(i);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        boolean ada = false;
        final Intent xintent = intent;
        final Fragment thisFragment = this;
        if (requestCode == 555) { // foto profil
            adapter.onActivityResult(requestCode,resultCode,intent);
        }
        else if (requestCode == 0) { // barcode result
            if (resultCode == getActivity().RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
                final String oid = Customer.getCustomerByBarcode(dm.database(),contents);
//                Log.e("oid",oid);
//                Log.e("contents",contents);
                if(oid.length()>0){

                    //check GPS
                    SharedPreferences zsession = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                    String curLat = zsession.getString("LATITUDE", "0");
                    String curLong = zsession.getString("LONGITUDE", "0");

                    double doubleLat = Double.parseDouble(curLat);
                    double doubleLong = Double.parseDouble(curLong);

                    for(int i=0;i<CallPlan.adapter.getCount();i++){
                        CustomerCall call = CallPlan.adapter.getItem(i);
                        if (call.getCustomerNumber().equals(oid)){
                            callCustomer(call,i,true);
                            ada = true;
                            break;
                        }
                    }


                    if(!ada){
                        final List<ReasonUnroute> ls = ReasonUnroute.getData(dm.database());
                        final List<String> s = new ArrayList<String>();

                        for (ReasonUnroute r : ls) {
                            s.add(r.getReason() + " " + r.getDescription());
                        }

                        final CharSequence[] items = s.toArray(new CharSequence[s.size()]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(Helper.getStrResource(getActivity(), R.string.transaction_order_title_reason_no_route));
                        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                ReasonUnroute r = (ReasonUnroute) ls.get(item);

                                SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                                String currentSalesman = session.getString("CUR_SLS", "");
                                String curWeek = session.getString("CUR_WEEK", "");

                                Customer customer = Customer.getCustomer(getActivity().getApplicationContext(),oid);
                                if(customer!=null){
                                    CustomerCall ccx = new CustomerCall(DBManager.getInstance(getActivity().getApplicationContext()).database());
                                    ccx.setCallStatus("0");
                                    ccx.setId(Helper.getCurrentDateTime("yyMMddHHmmss"));
                                    ccx.setServerDate(Helper.getCurrentDate());
                                    ccx.setPicture(customer.getPicture());
                                    ccx.setWeek(curWeek);
                                    ccx.setReasonUnroute(r.getReason()); // new reason un route
                                    //Log.e("noroute",r.getReason());
                                    ccx.setSlsId(currentSalesman);
                                    ccx.setStatus(CustomerCall.NO_VISIT);
                                    ccx.setCustomerNumber(customer.getCustomerNumber());
                                    //String hari = CustomerCall.getDayByCustomer(DBManager.getInstance(getActivity().getApplicationContext()).database(),customer.getCustomerNumber());
                                    //ccx.setCustomerName(customer.getCustomerName() + " " + hari);
                                    ccx.setCustomerName(customer.getCustomerName() );
                                    ccx.setAlias(customer.getAlias());
                                    ccx.setAddress(customer.getAddress());
                                    ccx.setCity(customer.getCity());
                                    ccx.setGroupChannel(customer.getGroupChannel());
                                    ccx.setChannel(customer.getChannel());
                                    ccx.setRegion(customer.getRegion());
                                    ccx.setZone(customer.getZone());
                                    ccx.setClassification(customer.getClassification());
                                    ccx.setDistrict(customer.getDistrict());
                                    ccx.setJarak(0);
                                    ccx.setTerritory(customer.getTerritory());
                                    ccx.setSquence(Integer.parseInt(Helper.getCurrentDateTime("yyMMdd")));

                                    ccx.save();

                                    listCallPlan.clear();

                                    CallPlanInit.loadPlan(getActivity());
                                    listCallPlan.addAll(MainActivity.dataCustomerCall);

                                    adapter = new CallPlanAdapter(getActivity(), R.layout.ui_call_plan_item, listCallPlan,thisFragment);
                                    lv.setAdapter(adapter);

                                    // call back
                                    for(int i=0;i<CallPlan.adapter.getCount();i++){
                                        CustomerCall call = CallPlan.adapter.getItem(i);
                                        if (call.getCustomerNumber().equals(oid)){
                                            callCustomer(call,i,true);
                                            break;
                                        }
                                    }
                                }
                                dlgReason.dismiss();
                            }

                        });

                        dlgReason = builder.create();
                        dlgReason.show();

                    }
                }
            }
        }
        else if (requestCode == REQUEST_ADD_ITEM && resultCode == Activity.RESULT_OK){

            DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
            final List<ReasonUnroute> ls = ReasonUnroute.getData(dm.database());
            final List<String> s = new ArrayList<String>();

            for (ReasonUnroute r : ls) {
                s.add(r.getReason() + " " + r.getDescription());
            }

            final CharSequence[] items = s.toArray(new CharSequence[s.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(Helper.getStrResource(getActivity(), R.string.transaction_order_title_reason_no_route));
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    ReasonUnroute r = (ReasonUnroute) ls.get(item);

                    SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                    String currentSalesman = session.getString("CUR_SLS", "");
                    String curWeek = session.getString("CUR_WEEK", "");

                    //
                    Customer customer = (Customer)xintent.getSerializableExtra("resultAddCust");


                    CustomerCall ccx = new CustomerCall(DBManager.getInstance(getActivity().getApplicationContext()).database());
                    ccx.setCallStatus("0");
                    ccx.setId(Helper.getCurrentDateTime("yyMMddHHmmss"));
                    ccx.setServerDate(Helper.getCurrentDate());
                    ccx.setPicture(customer.getPicture());
                    ccx.setWeek(curWeek);
                    ccx.setReasonUnroute(r.getReason()); // new reason un route
                    //Log.e("noroute",r.getReason());
                    ccx.setSlsId(currentSalesman);
                    ccx.setStatus(CustomerCall.NO_VISIT);
                    ccx.setCustomerNumber(customer.getCustomerNumber());
                    //String hari = CustomerCall.getDayByCustomer(DBManager.getInstance(getActivity().getApplicationContext()).database(),customer.getCustomerNumber());
                    //ccx.setCustomerName(customer.getCustomerName() + " " + hari);
                    ccx.setCustomerName(customer.getCustomerName() );
                    ccx.setAlias(customer.getAlias());
                    ccx.setAddress(customer.getAddress());
                    ccx.setCity(customer.getCity());
                    ccx.setGroupChannel(customer.getGroupChannel());
                    ccx.setChannel(customer.getChannel());
                    ccx.setRegion(customer.getRegion());
                    ccx.setZone(customer.getZone());
                    ccx.setClassification(customer.getClassification());
                    ccx.setDistrict(customer.getDistrict());
                    ccx.setJarak(0);
                    ccx.setTerritory(customer.getTerritory());
                    ccx.setSquence(Integer.parseInt(Helper.getCurrentDateTime("yyMMdd")));

                    ccx.save();

                    listCallPlan.clear();

                    CallPlanInit.loadPlan(getActivity());
                    listCallPlan.addAll(MainActivity.dataCustomerCall);

                    adapter = new CallPlanAdapter(getActivity(), R.layout.ui_call_plan_item, listCallPlan,thisFragment);
                    lv.setAdapter(adapter);

                    //




                    //adapter.getItem(posx).setReasonNoBarcode(r.getReason());
                    //adapter.notifyDataSetChanged();
                    //call.setReasonNoBarcode(DBManager.getInstance(getActivity().getApplicationContext()).database(), r.getReason());
                    //startOrder(true);
                    //Log.e("r.getReason()", r.getReason());

                    dlgReason.dismiss();
                    for(int i=0;i<adapter.getCount();i++){
                        if(adapter.getItem(i).getCustomerNumber().equals(customer.getCustomerNumber())){
                            callCustomer(adapter.getItem(i), i, false);
                            //startScan(adapter.getItem(i),i,false);
                            break;
                        }
                    }



                }
            });

            dlgReason = builder.create();
            dlgReason.show();

        }

    }

    //LONG PRESS CreateContextMenu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lstCallPlan) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            //menu.setHeaderTitle(xx.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.menu_call_plan);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    //LONG PRESS ContextItemSelected
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final CustomerCall cp = adapter.getItem(info.position);
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){
            if(cp.getStatus().equals(CustomerCall.NOCALL)){
                Toast.makeText(getActivity().getApplicationContext(),"Status No Call tidak dapat order",Toast.LENGTH_SHORT).show();
            }else {
                callCustomer(cp, menuItemIndex, false);
            }
        }
        else if (menuItemIndex == 1){ //
            if (cp.getCallStatus().equals("1") && cp.getStatus().equals(CustomerCall.NO_VISIT)){
                final DBManager dm = DBManager.getInstance(getActivity().getApplicationContext());
                final List<ReasonNoCall> ls = ReasonNoCall.getData(dm.database());
                final List<String> s = new ArrayList<String>();

                for (ReasonNoCall r : ls) {
                    s.add(r.getReason() + " " + r.getDescription());
                }

                final CharSequence[] items = s.toArray(new CharSequence[s.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Helper.getStrResource(getActivity(), R.string.transaction_order_title_reason_no_call));
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        ReasonNoCall r = (ReasonNoCall) ls.get(item);

                        // send to server directly
                        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                        String curSls = session.getString("CUR_SLS", "");
                        String curCompany = session.getString("CUR_COMPANY", "");

                        NgantriInformation ngantri = new NgantriInformation(getActivity().getApplicationContext());
                        ngantri.key = NgantriInformation.KEY_NO_CALL;
                        try{

                            JSONObject param = new JSONObject();
                            param.put("command",            "no_call");
                            param.put("salesman_id",        curSls);
                            param.put("company_id",         curCompany);
                            param.put("outlet_id",          cp.getCustomerNumber());
                            param.put("no_call_date",       Helper.getCurrentDate());
                            param.put("reason_no_call_id",       r.getReason());

                            ngantri.data = param.toString();
                            ngantri.value = curCompany+"_"+curSls;
                            ngantri.description = "sending reason no call " +curCompany+"_"+curSls+"_"+cp.getCustomerNumber();

                            ngantri.addAntrian();
                        }
                        catch(JSONException ex){

                        }
                        catch(Exception ex){

                        }

                        //end request order to server

                        adapter.getItem(info.position).setStatus(CustomerCall.NOCALL);
                        adapter.notifyDataSetChanged();
                        cp.setNoCall(dm.database());
                        dlgReason.dismiss();
                    }
                });

                dlgReason = builder.create();
                dlgReason.show();
            }else{
                Helper.showToast(getActivity().getApplicationContext(),"Tidak dapat memilih 'alasan tidak dikunjungi'");
            }
        }
        else if (menuItemIndex == 2){
                if (cp.getCallStatus().equals("0") && cp.getStatus().equals(CustomerCall.NO_VISIT)){
                    //adapter.remove(cp);
                    //adapter.notifyDataSetChanged();
                    cp.deleteUnRoute(DBManager.getInstance(getActivity().getApplicationContext()).database());
                    CallPlanInit.loadPlan(getActivity().getApplicationContext());
                    listCallPlan.clear();
                    listCallPlan.addAll(MainActivity.dataCustomerCall);
                    adapter = new CallPlanAdapter(getActivity(), R.layout.ui_call_plan_item, listCallPlan,CallPlan.this);
                    lv.setAdapter(adapter);

                }else{
                    Helper.showToast(getActivity().getApplicationContext(),"Call Plan on route tidak dapat dihapus");
                }
            }


        //Log.e("123",cp.getCallStatus());
        //Log.e("321",cp.getStatus());

        return true;
    }

}
