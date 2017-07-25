package com.ksni.roots.ngsales;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


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

/**
 * Created by #roots on 03/12/2015.
 */
public class UpdateAPK extends IntentService {

    public static final String LOG_TAG = "ngSalaseUpdate";
    public static final String RESPONSE_MESSAGE = "ok_update_bray";

    private String version="";
    private int result = 0;

    public UpdateAPK() {
        super("UpdateAPK");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        HttpURLConnection urlConnection = null;

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        String currentSalesman = session.getString("CUR_SLS", "");
        String curCompany = session.getString("CUR_COMPANY", "");

        version = Integer.toString(intent.getIntExtra("inVersion", 0));

        try {
            java.net.URL url = new URL(MainActivity.BASE_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(15 * 1000); //15s
            urlConnection.setReadTimeout(15 * 1000); //15s
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestMethod("POST");

            JSONObject jsonReady = new JSONObject();
            jsonReady.put("command", "version");
            jsonReady.put("company_id", curCompany);
            jsonReady.put("salesman_id", currentSalesman);
            jsonReady.put("curr_version", version);

            Log.e("SEND", "Request data = " + jsonReady.toString());

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes("data=" + jsonReady.toString());
            wr.flush();
            wr.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    response.append(line);
                }

                 version = response.toString();

                 result = 1;

            } else {
                result = 0;
            }

            //Log.e("Update APK", "VERSION : " + version);

        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.toString());
            result = 99;
        } catch (SocketException e) {
            Log.e("SocketException", e.toString());
            result = 99;
        } catch (SocketTimeoutException e) {
            Log.e("SocketTimeoutException", e.toString());
            result = 99;
        } catch (ProtocolException e) {
            Log.e("ProtocolException", e.toString());
            result = 99;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            result = 99;
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            result = 99;
        }


        if (result == 1) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.NgsalesWebReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(RESPONSE_MESSAGE, version);
            sendBroadcast(broadcastIntent);
        }

    }

}
