package com.ksni.roots.ngsales.domain;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.widget.TextView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.Button;
import android.content.DialogInterface;

import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import android.provider.MediaStore;

import android.content.ActivityNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by #roots on 07/08/2015.
 */
public class Attendance extends Fragment{

    private int BARCODE_SCAN_IN = 10;
    private int BARCODE_SCAN_OUT = 20;

    final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    View rootView;
    private ProgressDialog progressDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUi();
    }

    @Override
    public void onPause(){
        super.onPause();
        updateUi();
    }



    private boolean isOpen(){
        boolean buffer = false;
        for(CustomerCall cc:MainActivity.dataCustomerCall) {
            if (!cc.getStatus().equals(CustomerCall.VISITED) && !cc.getStatus().equals(CustomerCall.NOCALL)){
               buffer = true;
               break;
            }
        }
        return buffer;
    }

    private boolean isExistPause(){

        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        String cur = session.getString("CUR_VISIT", "");
        String sls = session.getString("CUR_SLS", "");


        return CustomerCall.isExistPause(getActivity().getApplicationContext(),Helper.getCurrentDate(),sls);
    }

    private boolean isExistVisit(){

        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        final String cur = session.getString("CUR_VISIT", "");

        boolean ada =false;
        for(int i=0;i<CallPlan.adapter.getCount();i++){
            CustomerCall cc = CallPlan.adapter.getItem(i);
            if (cc.getCustomerNumber().equals(cur) && CallPlan.adapter.getItem(i).getStatus().equals(CustomerCall.VISIT)){
                ada = true;
                break;
            }
        }
        return ada;
    }

    private void workStart(){

        final Button btn = (Button) rootView.findViewById(R.id.button);
        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        final String cur = session.getString("CUR_WORK_START", "");
        final String curCust = session.getString("CUR_VISIT", "");
        final String curCompany = session.getString("CUR_COMPANY", "");
        final String curSls = session.getString("CUR_SLS", "");
        final String curWeek = session.getString("CUR_WEEK", "");
        final String curDate = session.getString("CUR_VISIT_DATE", "");
        final String curx = session.getString("CUR_WORK_END", "");
        final SharedPreferences.Editor e = session.edit();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        final String curTime = format.format(new Date()); // Time
        final String curFullTime = Helper.getCurrentDateTime(); // Full Time
        final String curDateLocal = Helper.getCurrentDate(); // Date
        final EditText txtOdometer = new EditText(getActivity());

        txtOdometer.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        //txtOdometer.setHint("value here...");
        new AlertDialog.Builder(getActivity())
                .setTitle(Helper.getStrResource(getActivity(), R.string.start_input_box_title_odometer))
                .setMessage(Helper.getStrResource(getActivity(), R.string.start_input_box_text_start_odometer))
                .setView(txtOdometer)
                .setPositiveButton(Helper.getStrResource(getActivity(), R.string.common_msg_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (Helper.isNumber(txtOdometer.getText().toString())) {
                            int ordom = Integer.parseInt(txtOdometer.getText().toString());
                            if (ordom <= 0) {
                                Helper.msgbox(getActivity(), Helper.getStrResource(getActivity(), R.string.common_msg_invalid_numeric), Helper.getStrResource(getActivity(), R.string.common_msg_invalid));
                            }else {
                                //start request work in to server
                                NgantriInformation ngantri = new NgantriInformation(getActivity().getApplicationContext());
                                ngantri.key = NgantriInformation.KEY_WORK_IN;
                                try {
                                    JSONObject jsonParam = new JSONObject();
                                    jsonParam.put("command", "salesman_work");
                                    jsonParam.put("type", "I");
                                    jsonParam.put("odometer", txtOdometer.getText().toString());
                                    jsonParam.put("time", curFullTime);
                                    jsonParam.put("company_id", curCompany);
                                    jsonParam.put("salesman_id", curSls);
                                    ngantri.data = jsonParam.toString();
                                    ngantri.value = curCompany + "_" + curSls;
                                    ngantri.description = "sending work in " + curCompany + "_" + curSls;
                                    ngantri.addAntrian();

                                    e.putString("CUR_WORK_START", curTime);
                                    e.putString("CUR_WORK_END", "");
                                    e.commit();

                                    TextView c1 = (TextView) rootView.findViewById(R.id.tIn);
                                    c1.setText(curTime);

                                    btn.setText(Helper.getStrResource(getActivity(), R.string.start_button_work_end));
                                    btn.setBackgroundResource(R.drawable.button_bg_red);

                                    TextView c2 = (TextView) rootView.findViewById(R.id.tOut);
                                    c2.setText("");


                                    Settings.doWorkStart(getActivity(), curSls, curTime, 0, 0, curDateLocal, Double.parseDouble(txtOdometer.getText().toString()));
                                    Helper.notifyQueue(getActivity().getApplicationContext());

                                } catch (JSONException x) {
                                } catch (Exception x) {
                                }
                            }
                            //end request work in to server

                        } else {
                            Helper.msgbox(getActivity(), Helper.getStrResource(getActivity(), R.string.common_msg_invalid_numeric), Helper.getStrResource(getActivity(), R.string.common_msg_invalid));
                        }
                    }
                })
                .setNegativeButton(Helper.getStrResource(getActivity(), R.string.common_msg_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }

    private void workEnd(){

        final Button btn = (Button) rootView.findViewById(R.id.button);
        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        final String cur = session.getString("CUR_WORK_START", "");
        final String curCust = session.getString("CUR_VISIT", "");
        final String curCompany = session.getString("CUR_COMPANY", "");
        final String curSls = session.getString("CUR_SLS", "");
        final String curWeek = session.getString("CUR_WEEK", "");
        final String curDate = session.getString("CUR_VISIT_DATE", "");
        final String curx = session.getString("CUR_WORK_END", "");
        final SharedPreferences.Editor e = session.edit();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        final String curTime = format.format(new Date());
        final String curFullTime = Helper.getCurrentDateTime();
        final String curDateLocal = Helper.getCurrentDate();
        final EditText txtOdometer = new EditText(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Helper.getStrResource(getActivity(), R.string.start_prompt_text_work_end_confirm));
        builder.setTitle(Helper.getStrResource(getActivity(), R.string.common_msg_confirm));

        builder.setPositiveButton(Helper.getStrResource(getActivity(), R.string.common_msg_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final EditText txtOdometer = new EditText(getActivity());
                txtOdometer.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                //txtOdometer.setHint("value here...");
                new AlertDialog.Builder(getActivity())
                        .setTitle(Helper.getStrResource(getActivity(), R.string.start_input_box_title_odometer))
                        .setMessage(Helper.getStrResource(getActivity(), R.string.start_input_box_text_end_odometer))
                        .setView(txtOdometer)
                        .setPositiveButton(Helper.getStrResource(getActivity(), R.string.common_msg_save), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (Helper.isNumber(txtOdometer.getText().toString())) {

                                    int ordom = Integer.parseInt(txtOdometer.getText().toString());
                                    if (ordom <= 0) {
                                        Helper.msgbox(getActivity(), Helper.getStrResource(getActivity(), R.string.common_msg_invalid_numeric), Helper.getStrResource(getActivity(), R.string.common_msg_invalid));
                                    } else {

                                        //start request work out to server
                                        NgantriInformation ngantri = new NgantriInformation(getActivity().getApplicationContext());
                                        ngantri.key = NgantriInformation.KEY_WORK_OUT;
                                        try {
                                            JSONObject jsonParam = new JSONObject();
                                            jsonParam.put("command", "salesman_work");
                                            jsonParam.put("type", "O");
                                            jsonParam.put("odometer", txtOdometer.getText().toString());
                                            jsonParam.put("time", curFullTime);
                                            jsonParam.put("company_id", curCompany);
                                            jsonParam.put("salesman_id", curSls);
                                            ngantri.data = jsonParam.toString();
                                            ngantri.value = curCompany + "_" + curSls;
                                            ngantri.description = "sending work in " + curCompany + "_" + curSls;
                                            ngantri.addAntrian();

                                            e.putString("CUR_WORK_END", curTime);
                                            e.commit();

                                            btn.setBackgroundResource(R.drawable.button_bg);
                                            btn.setText(Helper.getStrResource(getActivity(), R.string.start_button_work_start));

                                            TextView c2 = (TextView) rootView.findViewById(R.id.tOut);
                                            c2.setText(curTime);

                                            Settings.doWorkEnd(getActivity(), curSls, curTime, 0, 0, curDateLocal, Double.parseDouble(txtOdometer.getText().toString()));
                                            Helper.notifyQueue(getActivity().getApplicationContext());

                                        }catch(JSONException x){
                                        }catch(Exception x){
                                        }
                                    }
                                    //end request work out to server

                                } else {
                                    Helper.msgbox(getActivity(), Helper.getStrResource(getActivity(), R.string.common_msg_invalid_numeric), Helper.getStrResource(getActivity(), R.string.common_msg_invalid));
                                }
                            }
                        })
                        .setNegativeButton(Helper.getStrResource(getActivity(), R

                                        .string.common_msg_cancel), new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }
                        )
                        .show();
            }


        });

        builder.setNegativeButton(Helper.getStrResource(getActivity(), R.string.common_msg_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private void updateUi(){
        Button btn =(Button) rootView.findViewById(R.id.button);
        SharedPreferences session =getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        String cur1 = session.getString("CUR_WORK_START", "");
        String cur2 = session.getString("CUR_WORK_END", "");

        if (cur1=="" || cur2!=""){
            btn.setBackgroundResource(R.drawable.button_bg);
            btn.setText(Helper.getStrResource(getActivity(),R.string.start_button_work_start));
        }else{
            btn.setBackgroundResource(R.drawable.button_bg_red);
            btn.setText(Helper.getStrResource(getActivity(),R.string.start_button_work_end));
        }

        TextView c1 = (TextView)rootView.findViewById(R.id.tIn);
        TextView c2 = (TextView)rootView.findViewById(R.id.tOut);

        c1.setText(cur1);
        c2.setText(cur2);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView==null) {
            rootView = inflater.inflate(R.layout.ui_attendance, container, false);

            final Button btn = (Button) rootView.findViewById(R.id.button);

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                    final String cur = session.getString("CUR_WORK_START", "");
                    final String curx = session.getString("CUR_WORK_END", "");

                    final String curCust = session.getString("CUR_VISIT", "");
                    final String curCompany = session.getString("CUR_COMPANY", "");
                    final String curSls = session.getString("CUR_SLS", "");
                    final String curWeek = session.getString("CUR_WEEK", "");
                    final String curDate = session.getString("CUR_VISIT_DATE", "");

                    final SharedPreferences.Editor e = session.edit();
                    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                    final String curTime = format.format(new Date());
                    final String curFullTime = Helper.getCurrentDateTime();
                    final String curDateLocal = Helper.getCurrentDate();

                    if (cur.equals("") && curx.equals("")) {
                        boolean scan_barcode = Config.getChecked(getActivity().getApplicationContext(), "scan_barcode_start");
                        if (scan_barcode) {
                            try {
                                Intent intent = new Intent(ACTION_SCAN);
                                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(intent, BARCODE_SCAN_IN);
                            } catch (ActivityNotFoundException anfe) { }
                        }
                    } else if (!cur.equals("") && !curx.equals("")) {
                        Helper.showToast(getActivity().getApplication(), Helper.getStrResource(getActivity(), R.string.start_work_end_error_customer_finish_visit));
                    } else if (isOpen()) {
                        Helper.showToast(getActivity().getApplication(), Helper.getStrResource(getActivity(), R.string.start_work_end_error_customer_novisit_nocall));
                    } else if (curCust != "") {
                        Helper.showToast(getActivity().getApplicationContext(), Helper.getStrResource(getActivity(), R.string.start_work_end_error_customer_still_visit));
                    } else if (isExistPause()) {
                        Helper.showToast(getActivity().getApplicationContext(), Helper.getStrResource(getActivity(), R.string.start_work_end_error_customer_still_pause));
                    } else {
                        boolean scan_barcode = Config.getChecked(getActivity().getApplicationContext(), "scan_barcode_start");
                        if (scan_barcode) {
                            try {
                                Intent intent = new Intent(ACTION_SCAN);
                                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(intent, BARCODE_SCAN_OUT);
                            } catch (ActivityNotFoundException anfe) { }
                        }
                    }

                }
            });

        }

        updateUi();

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == BARCODE_SCAN_IN) {
            if (resultCode == getActivity().RESULT_OK) {
                SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                String cur = session.getString("CUR_BARCODE_NUMBER", "");

                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                if(cur.equals(contents)) {
                    workStart();
                }
            }
        }else if (requestCode == BARCODE_SCAN_OUT) {
            if (resultCode == getActivity().RESULT_OK) {
                SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                String cur = session.getString("CUR_BARCODE_NUMBER", "");

                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                if(cur.equals(contents)) {
                    workEnd();
                }
            }
        }
    }


}
