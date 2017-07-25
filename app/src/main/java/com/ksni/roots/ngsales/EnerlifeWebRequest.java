package com.ksni.roots.ngsales;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;

import com.ksni.roots.ngsales.domain.CallPlanInit;
import com.ksni.roots.ngsales.domain.SyncCallPlan;
import com.ksni.roots.ngsales.domain.SyncMasterData;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by #roots on 11/12/2015.
 */
public class EnerlifeWebRequest extends IntentService {
    public final static String NOTIFY_RECEIVE_CALL_PLAN = "NOTIFY_RECEIVE_CALL_PLAN";
    private int result = 0;

    @Override
    protected void onHandleIntent(Intent intent) {

        SystemClock.sleep(5 * 1000);

        if(Helper.isOnline(getApplicationContext())) {
            if (intent.getStringExtra("init").equals("1")) {
                // init service
                boolean forceActive = false;
                if (intent.getStringExtra("forceActive") != null) {
                    if (intent.getStringExtra("forceActive").equals("1")) {
                        forceActive = true;
                    }
                }

                NgantriInformation last = NgantriInformation.getLastAntrian(getApplicationContext(), forceActive);
                if (last != null) {
                    Intent msgIntent = new Intent(getApplicationContext(), EnerlifeWebRequest.class);
                    msgIntent.putExtra("init", "0");
                    msgIntent.putExtra("data", last.data);
                    msgIntent.putExtra("id", last.id);
                    msgIntent.putExtra("key", last.key);
                    msgIntent.putExtra("value", last.value);

                    //Log.e("SET STATUS", "STATUS = " + NgantriInformation.STATUS_ACTIVE);
                    Log.e("SET STATUS", "ACTIVE");
                    NgantriInformation.setStatus(getApplicationContext(), NgantriInformation.STATUS_ACTIVE, last.id);
                    //sendBroadcast(new Intent("serviceok"));
                    startService(msgIntent);
                } else {
                    // jika sudah tidak ada antrian cek yg force active
                    if (NgantriInformation.isExistActive(getApplicationContext())) {
                        Intent msgIntent = new Intent(getApplicationContext(), EnerlifeWebRequest.class);
                        msgIntent.putExtra("init", "1");
                        msgIntent.putExtra("forceActive", "1");
                        startService(msgIntent);
                        Log.e("FORCE_ACTIVE", "FORCE_ACTIVE");
                    }
                }

            } else if (intent.getStringExtra("init").equals("0")) {
                // execute sending data
                long id = intent.getLongExtra("id", -1);
                if (id != -1) {
                    // method http
                    String param = intent.getStringExtra("data");
                    int key = intent.getIntExtra("key", -1);
                    String value = intent.getStringExtra("value");
                    String description = intent.getStringExtra("description");
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(param);
                    } catch (JSONException e) {
                        Log.e("JSONException", e.toString());
                        result = 99;
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                        result = 99;
                    }


                    if (sendData(key, value, jObject)) { // <<< SEND DATA
                        // if ok chcek next antrian
                        //NgantriInformation.setStatus(getApplicationContext(), NgantriInformation.STATUS_DONE, id);

                        //Log.e("SET STATUS", "STATUS = " + NgantriInformation.STATUS_DONE); //test status
                        Log.e("SET STATUS", "DONE"); //test status
                        NgantriInformation.setDoneStatus(getApplicationContext(), id);
                        if (key == NgantriInformation.KEY_ORDER) {
                            Order.setSuccess(DBManager.getInstance(getApplicationContext()).database(), value);
                        } else if (key == NgantriInformation.KEY_CALL_PLAN) {
                            //sendBroadcast(new Intent(NOTIFY_RECEIVE_CALL_PLAN));

                         }

                        Intent msgIntent = new Intent(getApplicationContext(), EnerlifeWebRequest.class);
                        msgIntent.putExtra("init", "1");
                        startService(msgIntent);
                        //sendBroadcast(new Intent("serviceok"));
                    } else {
                        // if not ok retry
                        NgantriInformation.setStatus(getApplicationContext(), NgantriInformation.STATUS_RETRY, id);
                        Intent msgIntent = new Intent(getApplicationContext(), EnerlifeWebRequest.class);
                        msgIntent.putExtra("init", "0");
                        msgIntent.putExtra("id", id);
                        msgIntent.putExtra("key", key);
                        msgIntent.putExtra("description", description);
                        msgIntent.putExtra("data", param);
                        //Log.e("data",param);
                        startService(msgIntent);
                        //sendBroadcast(new Intent("serviceok"));

                    }
                }
            }
        }
    }

    public EnerlifeWebRequest() {
        super("EnerlifeWebRequest");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleEnerlifeResponse(int key, JSONObject response){
        switch (key){
            case NgantriInformation.KEY_CALL_PLAN:
                SyncCallPlan scp = new SyncCallPlan(getApplicationContext()); // << SYNCCALLPLAN
                scp.parseCallPlan(response);
                break;
            case NgantriInformation.KEY_MASTER_DATA:
                SyncMasterData smd = new SyncMasterData(getApplicationContext()); // << SYNCMASTER
                smd.parseMasterData(response);
                break;
            case NgantriInformation.KEY_ORDER:
                break;
            case NgantriInformation.KEY_WORK_IN:
                break;
            case NgantriInformation.KEY_WORK_OUT:
                break;

        }
    }

    private boolean sendData(int keyProc, String value, JSONObject jsonValue){

        boolean ok = false;

        try {
            if (jsonValue!=null) {
                java.net.URL url = new URL(MainActivity.BASE_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(NgantriInformation.HTTP_CONECTION_TIMEOUT * 1000);
                urlConnection.setReadTimeout(NgantriInformation.HTTP_READ_TIMEOUT * 1000);
                //bugs & char, urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestMethod("POST");

                Log.e("SEND", "Request data = " + jsonValue);

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                //wr.writeBytes("data=" + jsonValue.toString());

                String buffByte =  URLEncoder.encode(jsonValue.toString(),"UTF-8");
                wr.writeBytes("data=" + buffByte);

                wr.flush();
                wr.close();
                int statusCode = urlConnection.getResponseCode();
                //Log.e("Status", String.valueOf(statusCode));

                if (statusCode == 200) {
                    //Log.e("TEST", "TEST");
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    Log.e("GET", "Response data = " + response);

                    JSONObject json = new JSONObject(response.toString());
                    if (json != null) {
                        if (json.optString("success").equals("true")) {
                            handleEnerlifeResponse(keyProc,json); //<< RESPONSE
                            ok = true;
                        }
                    }
                }
            }
            return  ok;
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.toString());
            return false;
        } catch (SocketException e) {
            Log.e("SocketException", e.toString());
            return false;
        } catch (SocketTimeoutException e) {
            Log.e("SocketTimeoutException", e.toString());
            return false;
        } catch (ProtocolException e) {
            Log.e("ProtocolException", e.toString());
            return false;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return false;
        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
            return false;
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            return false;
        }
    }


}
