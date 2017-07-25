package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 21/09/2015.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AsynActivity extends Fragment implements View.OnClickListener {
    private ProgressBar progressDialog;
    private Button btnSync;
    private Button btnDocking;
    private View rootView;
    private LinearLayout llSyncDocking;
    private Boolean syncDockingClicked;
    private ExpandableRelativeLayout elSyncDocking;

    private ListView lv;
    private TextView tv;

    public AsyncRowAdapter adapter;
    List<HeaderStruct> listOrder = new ArrayList<HeaderStruct>();

    private SQLiteDatabase db;
    private Settings sett;



    private void loadData(){
        final List<NgantriInformation> lists = NgantriInformation.getListAntrian(getActivity().getApplicationContext());
        ArrayAdapter<NgantriInformation> adapter = new ArrayAdapter<NgantriInformation> (getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, lists) {

            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                NgantriInformation info =  lists.get(position);
                String state ="";
                switch (info.status){
                    case NgantriInformation.STATUS_RELEASE:
                        state = Helper.getStrResource(getActivity(),R.string.sync_master_release);
                        break;
                    case NgantriInformation.STATUS_ACTIVE:
                        state = Helper.getStrResource(getActivity(),R.string.sync_master_active);
                        break;
                    case NgantriInformation.STATUS_DONE:
                        state = Helper.getStrResource(getActivity(),R.string.sync_master_done);
                        break;
                    case NgantriInformation.STATUS_RETRY:
                        state = Helper.getStrResource(getActivity(),R.string.sync_master_retry);
                        break;
                    case NgantriInformation.STATUS_CANCEL:
                        state = Helper.getStrResource(getActivity(),R.string.sync_master_cancel);
                        break;

                }
                text1.setText(info.description.replace("sending ","").toUpperCase()+ " " + state);
                text1.setTypeface(Typeface.SANS_SERIF);
                text1.setTextSize(17);

                text2.setText(info.time+ "\n"  );
                text1.setTypeface(Typeface.MONOSPACE);


                return view;
            }

        };

        lv.setAdapter(adapter);

        lv.setEmptyView(rootView.findViewById(R.id.list_empty));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void requestData(){

        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        String curCustName = session.getString("CUR_VISIT_NAME", "");
        String curSls = session.getString("CUR_SLS", "");
        String curCompany = session.getString("CUR_COMPANY", "");

        //removeAllDataDb(db);

        //hold by antrian
        //DBManager dm = DBManager.getInstance(getActivity());
        //SynchronousData syn = new SynchronousData(getActivity(),dm.database(),curCompany, curSls);
        //syn.doSync();

        //start request master to server
        try{
            // sync master data
            NgantriInformation ngantri = new NgantriInformation(getActivity().getApplicationContext());
            ngantri.key = NgantriInformation.KEY_MASTER_DATA;
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("command", "sync_master");
            jsonParam.put("salesman_id", curSls);
            jsonParam.put("company_id", curCompany);

            if (!Settings.hasSyncMaster(getActivity().getApplicationContext())){ // belum pernah disyn, ambil smua
                jsonParam.put("last_modified", "");
            }else{
                jsonParam.put("last_modified", Settings.getLastModified(getActivity().getApplicationContext()));
            }

            ngantri.data = jsonParam.toString();
            ngantri.value = curCompany+"_"+curSls;
            ngantri.description = "sending master data " +curCompany+"_"+curSls;
            ngantri.addAntrian();

            // sync call plan
            NgantriInformation ngantri2 = new NgantriInformation (getActivity().getApplicationContext());
            ngantri2.key = NgantriInformation.KEY_CALL_PLAN;
            JSONObject jsonParam2 = new JSONObject();
            jsonParam2.put("salesman_id", curSls);
            jsonParam2.put("company_id", curCompany);
            jsonParam2.put("command", "sync_call_plan");
            jsonParam2.put("last_modified", "");
            ngantri2.data = jsonParam2.toString();
            ngantri2.value = curCompany+"_"+curSls;
            ngantri2.description = "sending call plan " +curCompany+"_"+curSls;
            ngantri2.addAntrian();

            loadData();
        }catch(JSONException x){}
        catch(Exception x){}
        //end request master to server

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //case android.R.id.home:
            //   super.onBackPressed();
            //  break;
            case R.id.action_extract:
                //read config extract
                boolean allow_extract_docking_data = Config.getChecked(getActivity().getApplicationContext(), "allow_extract_docking_data");
                if (!allow_extract_docking_data) {
                    Helper.msgbox(getActivity().getApplicationContext(), Helper.getStrResource(getActivity(),R.string.sync_master_extract_data_restrict), Helper.getStrResource(getActivity(),R.string.common_msg_error));
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(Helper.getStrResource(getActivity(),R.string.sync_master_extract_data));
                    //builder.setMessage("Extract data will be cancel all pending data. Continue ?");
                    builder.setTitle(Helper.getStrResource(getActivity(),R.string.common_msg_confirm));


                    builder.setPositiveButton(Helper.getStrResource(getActivity(),R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                            //String currentSalesman = session.getString("CUR_SLS", "");

                          //  SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                            String currentSalesman = session.getString("CUR_SLS", "");
                            String curCompany = session.getString("CUR_COMPANY", "");


                            //Order.docking(DBManager.getInstance(getActivity().getApplicationContext()).database(), "123", Helper.getCurrentDate(), currentSalesman);

                            SQLiteDatabase db = DBManager.getInstance(getActivity().getApplicationContext()).database();
                            List<NgantriInformation> antians = NgantriInformation.getListAntrianFailed(getActivity().getApplicationContext());
                            JSONArray jArray = new JSONArray();
                            int cnt = 0;
                            for (NgantriInformation antrian : antians) {
                                try {
                                    if (antrian.key == NgantriInformation.KEY_ORDER) {
                                        Order.setSuccess(db, antrian.value);
                                    }
                                    JSONObject jObject = new JSONObject(antrian.data);
                                    jArray.put(jObject);
                                    cnt++;
                                } catch (JSONException x) {
                                } catch (Exception x) {
                                }
                            }

                            if (jArray.length() > 0) {
                                Helper.getExternalPath();

                                String filename = "FULLSYNC_" + curCompany + "_" + currentSalesman + "_" + Helper.getCurrentDateTime("yyyyMMdd") + ".txt";
                                try {
                                    FileOutputStream outStream = new FileOutputStream(Helper.getExternalPath() + "/" + filename,true);
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));

                                    bw.write(jArray.toString());
                                    bw.close();
                                    outStream.close();
                                    Helper.showToast(getActivity().getApplicationContext(), String.valueOf(cnt) + " data has been extracted.");
                                } catch (IOException e) {
                                    Helper.showToast(getActivity().getApplicationContext(), "Extract error");
                                } catch (Exception e) {
                                    Helper.showToast(getActivity().getApplicationContext(), "Extract error");
                                }

                            } else {
                                Helper.showToast(getActivity().getApplicationContext(), "No data to extract.");
                            }


                            //DBManager db = DBManager.getInstance(getActivity().getApplicationContext());
                            db.delete("queue", "status<>?", new String[]{String.valueOf(NgantriInformation.STATUS_DONE)});

                        }

                    });

                    builder.setNegativeButton(Helper.getStrResource(getActivity(),R.string.common_msg_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();


                }

                break;
            case R.id.action_refresh:
                //startActivity(new Intent(getActivity(),QueueActivity.class));
                loadData();
                Helper.notifyQueue(getActivity().getApplicationContext());
                break;
            case R.id.action_log:

                AlertDialog.Builder xbuilder = new AlertDialog.Builder(getActivity());
                xbuilder.setMessage(Helper.getStrResource(getActivity(),R.string.sync_master_prompt_text));
                xbuilder.setTitle(Helper.getStrResource(getActivity(),R.string.common_msg_warning));

                xbuilder.setPositiveButton(Helper.getStrResource(getActivity(),R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestData();
                    }
                });

                xbuilder.setNegativeButton(Helper.getStrResource(getActivity(),R.string.common_msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                xbuilder.create().show();




        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
        String curCustName = session.getString("CUR_VISIT_NAME", "");

        if (curCustName.length()==0){
            //adapter = new AsyncRowAdapter(getActivity(), R.layout.ui_sync_row, listOrder);
            //lv.setAdapter(adapter);

        }
            //new AsyncPost().execute();
//        else
           // Toast.makeText(getActivity(), "Customer " + curCustName.toUpperCase() + " still visiting.", Toast.LENGTH_LONG).show();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if (rootView == null) {
        rootView = inflater.inflate(R.layout.ui_asyn, container, false);
        tv =(TextView)rootView.findViewById(R.id.list_empty);
        //progressDialog = (ProgressBar) rootView.findViewById(R.id.progressAsyn);

              //      loadData();

        lv = (ListView) rootView.findViewById(R.id.lstSync);
        llSyncDocking = (LinearLayout) rootView.findViewById(R.id.linearLayoutSyncDocking);
        elSyncDocking = (ExpandableRelativeLayout) rootView.findViewById(R.id.expandableLayoutSyncDocking);
        btnSync = (Button) rootView.findViewById(R.id.buttonSync);
        btnDocking = (Button) rootView.findViewById(R.id.buttonDocking);

        btnSync.setOnClickListener(this);
        btnDocking.setOnClickListener(this);

        loadData();

        syncDockingClicked = false;

        llSyncDocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elSyncDocking.setVisibility(View.VISIBLE);
                if (!syncDockingClicked) {
                    elSyncDocking.expand();
                    syncDockingClicked = true;
                } else {
                    elSyncDocking.collapse();
                    syncDockingClicked = false;
                }
            }
        });


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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.buttonDocking :

                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                String curTime = format.format(new Date());
                String curFullTime = Helper.getCurrentDateTime();
                String curDateLocal = Helper.getCurrentDate();

                sett = new Settings();
                sett = Settings.getSettings(getActivity().getApplicationContext());

                if (sett.start_date == null && sett.end_date == null) {
                    //Toast.makeText(getActivity(), "FALSE & FALSE", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setMessage(Helper.getStrResource(getActivity(),R.string.docking_requirement_1));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                } else if (!sett.start_date.equals(curDateLocal) && !sett.end_date.equals(curDateLocal)) { //#1 ERRDOC2
                    //Toast.makeText(getActivity(), "FALSE & FALSE", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setMessage(Helper.getStrResource(getActivity(),R.string.docking_requirement_2));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                } else if (sett.start_date.equals(curDateLocal) && !sett.end_date.equals(curDateLocal)) { //#2 ERRDOC3
                    //Toast.makeText(getActivity(), "TRUE & FALSE", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setMessage(Helper.getStrResource(getActivity(),R.string.docking_requirement_3));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                } else if (sett.start_date.equals(curDateLocal) && sett.end_date.equals(curDateLocal)) { //#3
                    //read config extract
                    boolean allow_extract_docking_data = Config.getChecked(getActivity().getApplicationContext(), "allow_extract_docking_data");
                    if (!allow_extract_docking_data) {
                        Helper.msgbox(getActivity().getApplicationContext(), Helper.getStrResource(getActivity(),R.string.sync_master_extract_data_restrict), Helper.getStrResource(getActivity(),R.string.common_msg_error));
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(Helper.getStrResource(getActivity(),R.string.sync_master_extract_data));
                        //builder.setMessage("Extract data will be cancel all pending data. Continue ?");
                        builder.setTitle(Helper.getStrResource(getActivity(),R.string.common_msg_confirm));


                        builder.setPositiveButton(Helper.getStrResource(getActivity(),R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);

                                //String currentSalesman = session.getString("CUR_SLS", "");

                                //  SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
                                String currentSalesman = session.getString("CUR_SLS", "");
                                String curCompany = session.getString("CUR_COMPANY", "");

                                //Order.docking(DBManager.getInstance(getActivity().getApplicationContext()).database(), "123", Helper.getCurrentDate(), currentSalesman);

                                SQLiteDatabase db = DBManager.getInstance(getActivity().getApplicationContext()).database();
                                List<NgantriInformation> antians = NgantriInformation.getListAntrianFailed(getActivity().getApplicationContext());
                                JSONArray jArray = new JSONArray();
                                int cnt = 0;
                                for (NgantriInformation antrian : antians) {
                                    try {
                                        if (antrian.key == NgantriInformation.KEY_ORDER) {
                                            Order.setSuccess(db, antrian.value);
                                        }
                                        JSONObject jObject = new JSONObject(antrian.data);
                                        jArray.put(jObject);
                                        cnt++;
                                    } catch (JSONException x) {
                                    } catch (Exception x) {
                                    }
                                }

                                if (jArray.length() > 0) {
                                    Helper.getExternalPath();

                                    String filename = "FULLSYNC_" + curCompany + "_" + currentSalesman + "_" + Helper.getCurrentDateTime("yyyyMMddHHmm") + ".txt";
                                    try {
                                        FileOutputStream outStream = new FileOutputStream(Helper.getExternalPath() + "/" + filename,true);
                                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));

                                        bw.write(jArray.toString());
                                        bw.close();
                                        outStream.close();
                                        Helper.showToast(getActivity().getApplicationContext(), String.valueOf(cnt) + " data has been extracted.");
                                    } catch (IOException e) {
                                        Helper.showToast(getActivity().getApplicationContext(), "Extract error");
                                    } catch (Exception e) {
                                        Helper.showToast(getActivity().getApplicationContext(), "Extract error");
                                    }

                                } else {
                                    Helper.showToast(getActivity().getApplicationContext(), "No data to extract.");
                                }

                                //DBManager db = DBManager.getInstance(getActivity().getApplicationContext());
                                db.delete("queue", "status<>?", new String[]{String.valueOf(NgantriInformation.STATUS_DONE)});

                            }

                        });

                        builder.setNegativeButton(Helper.getStrResource(getActivity(),R.string.common_msg_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.create().show();

                    }
                } //#3
                break;

            case R.id.buttonSync:
                AlertDialog.Builder xbuilder = new AlertDialog.Builder(getActivity());
                xbuilder.setMessage(Helper.getStrResource(getActivity(),R.string.sync_master_prompt_text));
                xbuilder.setTitle(Helper.getStrResource(getActivity(),R.string.common_msg_warning));

                xbuilder.setPositiveButton(Helper.getStrResource(getActivity(),R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestData();
                    }
                });

                xbuilder.setNegativeButton(Helper.getStrResource(getActivity(),R.string.common_msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                xbuilder.create().show();

                break;
        }
    }

    public class AsyncPost extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {

            progressDialog.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
//            btnRefresh.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
            String curCompany = session.getString("CUR_COMPANY", "");
            String curSls = session.getString("CUR_SLS", "");

            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestMethod("POST");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());
                JSONObject jsonParam = new JSONObject();
                JSONObject jsonReady = new JSONObject();
                jsonParam.put("sls",        params[1]);
                jsonParam.put("company",     curCompany);
                jsonParam.put("time",       "10");
                jsonParam.put("latitude",   "10");
                jsonParam.put("longitude", "10");

                wr.writeBytes("data="+jsonParam.toString());
                wr.flush();
                wr.close();

                int statusCode = urlConnection.getResponseCode();
                //  Log.e("ERROR",String.valueOf(statusCode));
                if (statusCode ==  200) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1;
                }else{
                    result = 0;
                }

            } catch (Exception e) {

            }

            return result;

        }

        protected void onProgressUpdate(Integer... progress){
            progressDialog.setProgress(progress[0]);
        }

        private void loadRouteAssign(SQLiteDatabase db, JSONArray arr){

        }

        private void loadCalendar(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // customer
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonCust = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Customer cust = new com.ksni.roots.ngsales.model.Customer(db);
                    cust.setCustomerNumber(jsonCust.optString("outlet_id"));
                    cust.setCustomerName(jsonCust.optString("outlet_name"));
                    cust.setAlias(jsonCust.optString("outlet_name_alias"));
                    cust.setTitle(jsonCust.optString("title"));
                    cust.setAddress(jsonCust.optString("address"));
                    cust.setCity(jsonCust.optString("city"));
                    cust.setPhone(jsonCust.optString("phone_number"));
                    cust.setGroupChannel(jsonCust.optString("group_channel"));
                    cust.setChannel(jsonCust.optString("channel"));
                    cust.setCustomerGroup(jsonCust.optString("outlet_group"));
                    cust.setZone(jsonCust.optString("zone"));
                    cust.setCreditLimit(jsonCust.optDouble("credit_limit"));
                    cust.setBalance(jsonCust.optDouble("balance"));
                    cust.setNotes(jsonCust.optString("notes"));
                    cust.setStatus(jsonCust.optString("status"));
                    cust.setLatitude(jsonCust.optDouble("latitude"));
                    cust.setLongitude(jsonCust.optDouble("longitude"));
                    cust.setRegion(jsonCust.optString("region"));
                    cust.setClassification(jsonCust.optString("classification"));
                    cust.setTerritory(jsonCust.optString("territory"));
                    cust.setDistrict(jsonCust.optString("district"));
                    cust.save();
                }
            }
        }

        private void loadCustomer(SQLiteDatabase db, JSONArray arr){

            if(arr!=null) {
                // customer
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonCust = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Customer cust = new com.ksni.roots.ngsales.model.Customer(db);
                    cust.setCustomerNumber(jsonCust.optString("outlet_id"));
                    cust.setCustomerName(jsonCust.optString("outlet_name"));
                    cust.setAlias(jsonCust.optString("outlet_name_alias"));
                    cust.setTitle(jsonCust.optString("title"));
                    cust.setAddress(jsonCust.optString("address"));

                    cust.setCity(jsonCust.optString("city"));
                    cust.setPhone(jsonCust.optString("phone_number"));
                    cust.setGroupChannel(jsonCust.optString("group_channel"));
                    cust.setChannel(jsonCust.optString("channel"));
                    cust.setCustomerGroup(jsonCust.optString("outlet_group"));
                    cust.setZone(jsonCust.optString("zone"));
                    cust.setCreditLimit(jsonCust.optDouble("credit_limit"));
                    cust.setBalance(jsonCust.optDouble("balance"));
                    cust.setNotes(jsonCust.optString("notes"));
                    cust.setStatus(jsonCust.optString("status"));
                    cust.setLatitude(jsonCust.optDouble("latitude"));
                    cust.setLongitude(jsonCust.optDouble("longitude"));
                    cust.setRegion(jsonCust.optString("region"));
                    cust.setClassification(jsonCust.optString("classification"));
                    cust.setTerritory(jsonCust.optString("territory"));
                    cust.setDistrict(jsonCust.optString("district"));
                    cust.save();
                }
            }
        }

        private void loadPricing(JSONObject dPrice, JSONObject dFreeGood){
                try {
                    //JSONObject res = new JSONObject(result);
                    //JSONObject dPrice = res.optJSONObject("pricing");
                    //JSONObject dFreeGood = res.optJSONObject("free");


                    DBManager dm = DBManager.getInstance(getActivity());
                    SQLiteDatabase db = dm.database();


                    //free good
                    if (dFreeGood!=null){
                        JSONArray  dpArr =  dFreeGood.names();
                        for(int i=0;i<dpArr.length();i++){
                            JSONArray b = dFreeGood.optJSONArray(dpArr.getString(i));
                            for(int j=0;j<b.length();j++){
                                JSONObject a = b.optJSONObject(j);
                                if (a!=null) {
                                    switch (dpArr.getString(i)){
                                        case "f1": // free by outlet
                                            FreeGood1ByOutlet pbo = new FreeGood1ByOutlet(db);
                                            pbo.outlet_id = a.optString("outlet_id");
                                            pbo.product_id = a.optString("product_id");
                                            pbo.id = a.optInt("id");
                                            pbo.valid_from = a.optString("valid_from");
                                            pbo.valid_to = a.optString("valid_to");
                                            pbo.min_qty  = a.optInt("min_qty");
                                            pbo.buy_qty  = a.optInt("buy_qty");
                                            pbo.uom  = a.optString("uom");
                                            pbo.product_free  = a.optString("product_free");
                                            pbo.free_qty  = a.optInt("free_qty");
                                            pbo.multiple  = a.optString("multiple");
                                            pbo.free_uom  = a.optString("free_uom");
                                            pbo.save();
                                            break;
                                        case "f1a": // free by channel
                                            FreeGood1ByChannel pboa = new FreeGood1ByChannel(db);
                                            pboa.channel = a.optString("channel");
                                            pboa.product_id = a.optString("product_id");
                                            pboa.id = a.optInt("id");
                                            pboa.valid_from = a.optString("valid_from");
                                            pboa.valid_to = a.optString("valid_to");
                                            pboa.min_qty  = a.optInt("min_qty");
                                            pboa.buy_qty  = a.optInt("buy_qty");
                                            pboa.uom  = a.optString("uom");
                                            pboa.product_free  = a.optString("product_free");
                                            pboa.free_qty  = a.optInt("free_qty");
                                            pboa.multiple  = a.optString("multiple");
                                            pboa.proportional  = a.optString("proportional");
                                            pboa.free_uom  = a.optString("free_uom");
                                            pboa.save();
                                            break;
                                        case "f1b": // free by channel division
                                            FreeGood1ByZone pbob = new FreeGood1ByZone(db);
                                            pbob.zone = a.optString("zone");
                                            pbob.product_id = a.optString("product_id");
                                            pbob.id = a.optInt("id");
                                            pbob.valid_from = a.optString("valid_from");
                                            pbob.valid_to = a.optString("valid_to");
                                            pbob.min_qty  = a.optInt("min_qty");
                                            pbob.buy_qty  = a.optInt("buy_qty");
                                            pbob.uom  = a.optString("uom");
                                            pbob.product_free  = a.optString("product_free");
                                            pbob.free_qty  = a.optInt("free_qty");
                                            pbob.multiple  = a.optString("multiple");
                                            pbob.proportional  = a.optString("proportional");
                                            pbob.free_uom  = a.optString("free_uom");
                                            pbob.save();
                                            break;

                                    }
                                }
                            }
                        }

                    }

                    // pricing
                    if (dPrice!=null){
                        JSONArray  dpArr =  dPrice.names();
                        for(int i=0;i<dpArr.length();i++){
                            JSONArray b = dPrice.optJSONArray(dpArr.getString(i));
                            for(int j=0;j<b.length();j++){
                                JSONObject a = b.optJSONObject(j);
                                if (a!=null) {
                                    switch (dpArr.getString(i)){
                                        // PRICING
                                        case "p1": //$pricing_by_outlet
                                            PricingByOutlet pbo = new PricingByOutlet(db);
                                            pbo.outlet_id = a.optString("outlet_id");
                                            pbo.product_id = a.optString("product_id");
                                            pbo.id = a.optInt("id");
                                            pbo.uom = a.optString("uom");
                                            pbo.price  = a.optDouble("price");
                                            pbo.valid_from = a.optString("valid_from");
                                            pbo.valid_to = a.optString("valid_to");
                                            pbo.save();
                                            break;
                                        case "p2": //$pricing_by_channel
                                            PricingByChannel pbc = new PricingByChannel(db);
                                            pbc.channel = a.optString("channel");
                                            pbc.product_id = a.optString("product_id");
                                            pbc.id = a.optInt("id");
                                            pbc.uom = a.optString("uom");
                                            pbc.price  = a.optDouble("price");
                                            pbc.valid_from = a.optString("valid_from");
                                            pbc.valid_to = a.optString("valid_to");
                                            pbc.save();
                                            break;
                                        case "p3": //pricing_by_zone
                                            PricingByZone pbz = new PricingByZone(db);
                                            pbz.zone = a.optString("zone");
                                            pbz.product_id = a.optString("product_id");
                                            pbz.price  = a.optDouble("price");
                                            pbz.id = a.optInt("id");
                                            pbz.uom = a.optString("uom");
                                            pbz.valid_from = a.optString("valid_from");
                                            pbz.valid_to = a.optString("valid_to");
                                            pbz.save();
                                            break;

                                        // REGULAR DISCOUNT
                                        case "rd1": //$reg_discount_by_outlet
                                            DiscountRegByOutlet drbo = new DiscountRegByOutlet(db);
                                            drbo.outlet_id = a.optString("outlet_id");
                                            drbo.product_id = a.optString("product_id");
                                            drbo.discount =a.optDouble("discount");
                                            drbo.valid_from = a.optString("valid_from");
                                            drbo.valid_to = a.optString("valid_to");
                                            drbo.is_qty = a.optString("is_qty");
                                            drbo.id = a.optInt("id");
                                            drbo.from_qty = a.optInt("from_qty");
                                            drbo.to_qty = a.optInt("to_qty");
                                            drbo.from_value = a.optDouble("from_value");
                                            drbo.to_value = a.optDouble("to_value");
                                            drbo.uom = a.optString("uom");

                                            drbo.save();
                                            break;
                                        case "rd2"://$reg_discount_by_channel
                                            DiscountRegByChannel drbc = new DiscountRegByChannel(db);
                                            drbc.channel = a.optString("channel");
                                            drbc.product_id = a.optString("product_id");
                                            drbc.discount =a.optDouble("discount");
                                            drbc.valid_from = a.optString("valid_from");
                                            drbc.valid_to = a.optString("valid_to");
                                            drbc.id = a.optInt("id");
                                            drbc.is_qty = a.optString("is_qty");
                                            drbc.from_qty = a.optInt("from_qty");
                                            drbc.to_qty = a.optInt("to_qty");
                                            drbc.from_value = a.optDouble("from_value");
                                            drbc.to_value = a.optDouble("to_value");
                                            drbc.uom = a.optString("uom");

                                            drbc.save();
                                            break;
                                        case "rd3": //$reg_discount_by_channel_division
                                            DiscountRegByChannelByDivision drbcd = new DiscountRegByChannelByDivision(db);
                                            drbcd.channel = a.optString("channel");
                                            drbcd.division = a.optString("division");
                                            drbcd.discount =a.optDouble("discount");
                                            drbcd.valid_from = a.optString("valid_from");
                                            drbcd.valid_to = a.optString("valid_to");
                                            drbcd.is_qty = a.optString("is_qty");
                                            drbcd.id = a.optInt("id");
                                            drbcd.from_qty = a.optInt("from_qty");
                                            drbcd.to_qty = a.optInt("to_qty");
                                            drbcd.from_value = a.optDouble("from_value");
                                            drbcd.to_value = a.optDouble("to_value");
                                            drbcd.uom = a.optString("uom");

                                            drbcd.save();
                                            break;

                                        // EXTRA DISCOUNT
                                        case "re1": //$ext_discount_by_outlet
                                            DiscountExtByOutlet debo = new DiscountExtByOutlet(db);
                                            debo.outlet_id = a.optString("outlet_id");
                                            debo.product_id = a.optString("product_id");
                                            debo.discount =a.optDouble("discount");
                                            debo.valid_from = a.optString("valid_from");
                                            debo.valid_to = a.optString("valid_to");
                                            debo.id = a.optInt("id");
                                            debo.is_qty = a.optString("is_qty");
                                            debo.from_qty = a.optInt("from_qty");
                                            debo.to_qty = a.optInt("to_qty");
                                            debo.from_value = a.optDouble("from_value");
                                            debo.to_value = a.optDouble("to_value");
                                            debo.uom = a.optString("uom");

                                            debo.save();
                                            break;
                                        case "re2": //$ext_discount_by_channel
                                            DiscountExtByChannel debc = new DiscountExtByChannel(db);
                                            debc.channel = a.optString("channel");
                                            debc.product_id = a.optString("product_id");
                                            debc.discount =a.optDouble("discount");
                                            debc.valid_from = a.optString("valid_from");
                                            debc.valid_to = a.optString("valid_to");
                                            debc.id = a.optInt("id");
                                            debc.is_qty = a.optString("is_qty");
                                            debc.from_qty = a.optInt("from_qty");
                                            debc.to_qty = a.optInt("to_qty");
                                            debc.from_value = a.optDouble("from_value");
                                            debc.to_value = a.optDouble("to_value");
                                            debc.uom = a.optString("uom");

                                            debc.save();
                                            break;
                                        case "re3"://$ext_discount_by_channel_division
                                            DiscountExtByChannelByDivision debcx = new DiscountExtByChannelByDivision(db);
                                            debcx.channel = a.optString("channel");
                                            debcx.division = a.optString("division");
                                            debcx.discount =a.optDouble("discount");
                                            debcx.valid_from = a.optString("valid_from");
                                            debcx.valid_to = a.optString("valid_to");
                                            debcx.is_qty = a.optString("is_qty");
                                            debcx.id = a.optInt("id");
                                            debcx.from_qty = a.optInt("from_qty");
                                            debcx.to_qty = a.optInt("to_qty");
                                            debcx.from_value = a.optDouble("from_value");
                                            debcx.to_value = a.optDouble("to_value");
                                            debcx.uom = a.optString("uom");

                                            debcx.save();
                                            break;
                                        case "re4": //$ext_discount_by_channel_ipt
                                            DiscountExtByChannelIPT debcxy = new DiscountExtByChannelIPT(db);
                                            debcxy.channel = a.optString("channel");
                                            debcxy.discount =a.optDouble("discount");
                                            debcxy.valid_from = a.optString("valid_from");
                                            debcxy.valid_to = a.optString("valid_to");
                                            debcxy.id = a.optInt("id");
                                            debcxy.is_qty = a.optString("is_qty");
                                            debcxy.is_percent = a.optString("is_percent");
                                            debcxy.min_qty = a.optInt("min_qty");
                                            debcxy.min_value = a.optDouble("min_value");
                                            debcxy.ipt = a.optInt("ipt");
                                            debcxy.uom = a.optString("uom");
                                            debcxy.value_ex = a.optDouble("value_ex");
                                            Log.e("IPT","SYNC");

                                            debcxy.save();
                                            break;

                                        // SPECIAL DISCOUNT
                                        case "rs1": //$spc_discount_by_outlet
                                            DiscountSpcByOutlet dsbo = new DiscountSpcByOutlet(db);
                                            dsbo.outlet_id = a.optString("outlet_id");
                                            dsbo.product_id = a.optString("product_id");
                                            dsbo.discount =a.optDouble("discount");
                                            dsbo.valid_from = a.optString("valid_from");
                                            dsbo.valid_to = a.optString("valid_to");
                                            dsbo.id = a.optInt("id");
                                            dsbo.from_qty = a.optInt("from_qty");
                                            dsbo.to_qty = a.optInt("to_qty");
                                            dsbo.is_qty = a.optString("is_qty");
                                            dsbo.from_value = a.optDouble("from_value");
                                            dsbo.to_value = a.optDouble("to_value");
                                            dsbo.uom = a.optString("uom");

                                            dsbo.save();
                                            break;
                                        case "rs2": //$spc_discount_by_channel
                                            DiscountSpcByChannel dsbc = new DiscountSpcByChannel(db);
                                            dsbc.channel = a.optString("channel");
                                            dsbc.product_id = a.optString("product_id");
                                            dsbc.discount =a.optDouble("discount");
                                            dsbc.valid_from = a.optString("valid_from");
                                            dsbc.valid_to = a.optString("valid_to");
                                            dsbc.id = a.optInt("id");
                                            dsbc.is_qty = a.optString("is_qty");
                                            dsbc.from_qty = a.optInt("from_qty");
                                            dsbc.to_qty = a.optInt("to_qty");
                                            dsbc.from_value = a.optDouble("from_value");
                                            dsbc.to_value = a.optDouble("to_value");
                                            dsbc.uom = a.optString("uom");

                                            dsbc.save();
                                            break;
                                        case "rs3": //$spc_discount_by_channel_division
                                            DiscountSpcByChannelByDivision dsbcd = new DiscountSpcByChannelByDivision(db);
                                            dsbcd.channel = a.optString("channel");
                                            dsbcd.division = a.optString("division");
                                            dsbcd.discount =a.optDouble("discount");
                                            dsbcd.valid_from = a.optString("valid_from");
                                            dsbcd.valid_to = a.optString("valid_to");
                                            dsbcd.id = a.optInt("id");
                                            dsbcd.is_qty = a.optString("is_qty");
                                            dsbcd.from_qty = a.optInt("from_qty");
                                            dsbcd.to_qty = a.optInt("to_qty");
                                            dsbcd.from_value = a.optDouble("from_value");
                                            dsbcd.to_value = a.optDouble("to_value");
                                            dsbcd.uom = a.optString("uom");

                                            dsbcd.save();
                                            break;

                                    }


                                }
                            }
                        }

                    }


                    //dm.close();
                }
                catch (JSONException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error while retrieving data structure.", Toast.LENGTH_LONG).show();
                    //Log.e("buff",e.toString());

                }
            }

        private void loadCallPlan(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonSku = arr.optJSONObject(i);

                    com.ksni.roots.ngsales.model.CustomerCall prd = new com.ksni.roots.ngsales.model.CustomerCall(db);
                    prd.setId(jsonSku.optString("id"));
                    prd.setServerDate(jsonSku.optString("date"));
                    prd.setWeek(jsonSku.optString("week"));
                    prd.setSlsId(jsonSku.optString("sls_id"));
                    prd.setCustomerNumber(jsonSku.optString("outlet_id"));
                    prd.setRoute(jsonSku.optString("route"));
                    prd.setSquence(jsonSku.optInt("squence"));
                    prd.setNotes(jsonSku.optString("notes"));
                    prd.setCreditLimit(jsonSku.optDouble("credit_limit"));
                    prd.setStatus(CustomerCall.NO_VISIT);
                    prd.save();
                }
            }

        }

        private void loadCompetitor(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Competitor prd = new com.ksni.roots.ngsales.model.Competitor(db);

                    prd.competitor = jsonSku.optString("competitor");
                    prd.description = jsonSku.optString("description");

                    prd.save();
                }
            }

        }

        private void loadChannel(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Channel prd = new Channel(db);
                    prd.setChannel(jsonSku.optString("channel"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadRegion(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Region prd = new Region(db);
                    prd.setRegion(jsonSku.optString("region"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadZone(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Zone prd = new Zone(db);
                    prd.setZone(jsonSku.optString("zone"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadClassification(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Classification prd = new Classification(db);
                    prd.setClassification(jsonSku.optString("classification"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadDistrict(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    District prd = new District(db);
                    prd.setDistrict(jsonSku.optString("district"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadTerritory(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    Territory prd = new Territory(db);
                    prd.setTerritory(jsonSku.optString("territory"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadProduct(SQLiteDatabase db, JSONArray arr){
            // product

            Log.d("Load ", "Product");

            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    if (jsonSku!=null) {
                        com.ksni.roots.ngsales.model.Product prd = new com.ksni.roots.ngsales.model.Product(db);
                        prd.setProductId(jsonSku.optString("product_id"));
                        prd.setProductName(jsonSku.optString("product_name"));
                        prd.setAlias(jsonSku.optString("product_name_alias"));
                        prd.setDivision(jsonSku.optString("division"));
                        prd.setUom(jsonSku.optString("base_uom"));
                        prd.setProductType(jsonSku.optString("product_type"));
                        prd.setBrand(jsonSku.optString("product_brands"));
                        prd.setCategory(jsonSku.optString("product_category"));
                        prd.setCategory(jsonSku.optString("product_category"));
                        prd.setPrice(jsonSku.optDouble("price"));
                        prd.setStatus(jsonSku.optString("status"));
                        prd.setPareto(jsonSku.optString("is_pareto"));
                        prd.setUomSmall(jsonSku.optString("small_uom"));
                        prd.setUomMedium(jsonSku.optString("medium_uom"));
                        prd.setUomLarge(jsonSku.optString("large_uom"));
                        prd.setConversionMediumToSmall(jsonSku.optInt("mediumToSmall"));
                        prd.setConversionLargeToSmall(jsonSku.optInt("largeToSmall"));
                        prd.save();
                    }
                }
            }
        }

        private void loadProductDivision(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    ProductDivision prd = new ProductDivision(db);
                    prd.setDivision(jsonSku.optString("division"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadProductCategory(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    ProductCategory prd = new ProductCategory(db);
                    prd.setCategory(jsonSku.optString("category"));
                    prd.setDescription( jsonSku.optString("description"));
                    prd.save();
                }
            }
        }

        private void loadProductBrands(SQLiteDatabase db, JSONArray arr){

            Log.d("Load ", "Product Brand");

            if(arr!=null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    ProductBrands prd = new ProductBrands(db);
                    prd.setBrandsId(jsonSku.optString("brand_product_id"));
                    prd.setBrandsName( jsonSku.optString("name"));
                    prd.save();
                }
            }
        }

        private void loadTarget(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Target prd = new com.ksni.roots.ngsales.model.Target(db);

                    prd.year = jsonSku.optInt("year");
                    prd.period = jsonSku.optInt("period");
                    prd.sls = jsonSku.optString("sls_id");
                    prd.sku = jsonSku.optString("product_id");

                    prd.targetQty = jsonSku.optDouble("target_qty");
                    prd.actualQty = jsonSku.optDouble("actual_qty");
                    prd.achieveQty = jsonSku.optDouble("achiev_qty");

                    prd.targetValue = jsonSku.optDouble("target_value");
                    prd.actualValue = jsonSku.optDouble("actual_value");
                    prd.achieveValue = jsonSku.optDouble("achiev_value");


                    prd.save();
                }
            }

        }

        private void loadReason(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.Reason prd = new com.ksni.roots.ngsales.model.Reason(db);

                    prd.setReason( jsonSku.optString("reason_id"));
                    prd.setDescription(jsonSku.optString("description"));
                    prd.save();
                }
            }

        }

        private void loadSKUTemplate(SQLiteDatabase db, JSONArray arr){
            if(arr!=null) {
                // product
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonSku = arr.optJSONObject(i);
                    com.ksni.roots.ngsales.model.CustomerSKU prd = new com.ksni.roots.ngsales.model.CustomerSKU(db);

                    prd.product_id = jsonSku.optString("product_id");
                    prd.id =  jsonSku.optString("id");
                    prd.qty_last =  jsonSku.optInt("qty_last");
                    prd.save();
                }
            }

        }

        private void parseResult(String result) {
        DBManager dm = DBManager.getInstance(getActivity());
            try {

                JSONObject response     = new JSONObject(result);

                JSONArray dSku = response.optJSONArray("sku");
                loadProduct(dm.database(),dSku);

                JSONArray dCustomer     = response.optJSONArray("customer");
                loadCustomer(dm.database(), dCustomer);

                JSONArray dCall         = response.optJSONArray("call");
                loadCallPlan(dm.database(), dCall);

                JSONArray dTemplate     = response.optJSONArray("template");
                loadSKUTemplate(dm.database(), dTemplate);

                JSONArray dCompetitor     = response.optJSONArray("competitor");
                loadCompetitor(dm.database(), dCompetitor);

                JSONArray dReason     = response.optJSONArray("reason");
                loadReason(dm.database(), dReason);

                JSONArray dTarget     = response.optJSONArray("target");
                loadTarget(dm.database(), dTarget);

                JSONArray dChannel     = response.optJSONArray("channel");
                loadChannel(dm.database(), dChannel);

                JSONArray dRegion     = response.optJSONArray("region");
                loadRegion(dm.database(), dRegion);

                JSONArray dZone     = response.optJSONArray("zone");
                loadZone(dm.database(), dZone);

                JSONArray dClass     = response.optJSONArray("classification");
                loadClassification(dm.database(), dClass);

                JSONArray dDistrict     = response.optJSONArray("district");
                loadDistrict(dm.database(), dDistrict);

                JSONArray dTerritory     = response.optJSONArray("territory");
                loadTerritory(dm.database(), dTerritory);

                JSONArray dDivision     = response.optJSONArray("division");
                loadProductDivision(dm.database(), dDivision);

                JSONArray dCategory     = response.optJSONArray("category");
                loadProductCategory(dm.database(), dCategory);

                JSONArray dBrands     = response.optJSONArray("brands");
                loadProductBrands(dm.database(), dBrands);

                loadPricing(response.optJSONObject("pricing"),response.optJSONObject("free"));



            } catch (JSONException e) {
                Log.e("buff",e.getMessage());
                //e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            //loadData();
            //adapter = new AsyncRowAdapter(getActivity(), R.layout.ui_sync_row, listOrder);
            //lv.setAdapter(adapter);
            //lv.setEmptyView(rootView.findViewById(R.id.list_empty));

        }
    }

    public void removeAllDataDb (SQLiteDatabase db) {
        db.execSQL("DELETE FROM sls_config");
        db.execSQL("DELETE FROM queue");
        db.execSQL("DELETE FROM sls_information");
        db.execSQL("DELETE FROM product");
        db.execSQL("DELETE FROM sls_customer");
        db.execSQL("DELETE FROM sls_route_assign");
        db.execSQL("DELETE FROM sls_reason_unroute");
        db.execSQL("DELETE FROM sls_reason_nocall");
        db.execSQL("DELETE FROM sls_target");
        db.execSQL("DELETE FROM sls_targetx");
        db.execSQL("DELETE FROM sls_product");
        db.execSQL("DELETE FROM sls_top");
        db.execSQL("DELETE FROM settings");
        db.execSQL("DELETE FROM sls_pricing_by_outlet");
        db.execSQL("DELETE FROM sls_pricing_by_channel");
        db.execSQL("DELETE FROM sls_pricing_by_chain");
        db.execSQL("DELETE FROM sls_pricing_by_zone");
        db.execSQL("DELETE FROM sls_discount_reg_outlet");
        db.execSQL("DELETE FROM sls_discount_reg_channel");
        db.execSQL("DELETE FROM sls_discount_reg_channel_division");
        db.execSQL("DELETE FROM sls_discount_ext_outlet");
        db.execSQL("DELETE FROM sls_discount_ext_channel");
        db.execSQL("DELETE FROM sls_discount_ext_channel_ipt");
        db.execSQL("DELETE FROM sls_discount_spc_channel_ipt");
        db.execSQL("DELETE FROM sls_discount_ext_channel_division");
        db.execSQL("DELETE FROM sls_discount_spc_outlet");
        db.execSQL("DELETE FROM sls_discount_spc_channel");

        db.execSQL("DELETE FROM sls_discount_reg_channel_india");
        db.execSQL("DELETE FROM sls_discount_ext_channel_india");

        db.execSQL("DELETE FROM sls_discount_spc_channel_division");
        db.execSQL("DELETE FROM sls_order");
        db.execSQL("DELETE FROM sls_order_item");
        db.execSQL("DELETE FROM sls_van_stock");
        db.execSQL("DELETE FROM sls_van");
        db.execSQL("DELETE FROM sls_van_item");
        db.execSQL("DELETE FROM sls_reason");
        db.execSQL("DELETE FROM sls_reason_nobarcode");
        db.execSQL("DELETE FROM sls_reason_return");
        db.execSQL("DELETE FROM sls_plan_status");
        db.execSQL("DELETE FROM sls_sku_template");
        db.execSQL("DELETE FROM sls_free_good_customer");
        db.execSQL("DELETE FROM sls_free_good_zone");
        db.execSQL("DELETE FROM sls_free_good_channel");
        db.execSQL("DELETE FROM sls_route_assign");
        db.execSQL("DELETE FROM sls_week");

        db.execSQL("DELETE FROM sls_channel");
        db.execSQL("DELETE FROM sls_group_channel");
        db.execSQL("DELETE FROM sls_region");
        db.execSQL("DELETE FROM sls_zone");
        db.execSQL("DELETE FROM sls_classification");
        db.execSQL("DELETE FROM sls_district");
        db.execSQL("DELETE FROM sls_territory");

        db.execSQL("DELETE FROM sls_competitor");
        db.execSQL("DELETE FROM sls_competitor_entry");

        db.execSQL("DELETE FROM sls_division");
        db.execSQL("DELETE FROM sls_brand");

        db.execSQL("DELETE FROM insert_van_loading");
        db.execSQL("DELETE FROM insert_van_unloading");
        db.execSQL("DELETE FROM insert_sales_order_item");
        db.execSQL("DELETE FROM delete_sales_order_item");
    }

}
