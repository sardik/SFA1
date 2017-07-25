package com.ksni.roots.ngsales;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.ksni.roots.ngsales.domain.GPSTracker;
import com.ksni.roots.ngsales.domain.NotifInfo;
import com.ksni.roots.ngsales.domain.Synchronous;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import com.ksni.roots.ngsales.util.NetworkReceiver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by #roots on 19/10/2015.
 */
public class ScheduleProcess extends Service {

    private PowerManager.WakeLock wakeLock;
    private static int TIME_DIFFERENCE_THRESHOLD =  15 * 60 * 1000; // 1 menit
    private static long MIN_INTERVAL_UPDATE = 15 * 60 * 1000; // 5  menit
    private static long MIN_INTERVAL_DISTANCE = 500; // 500 meter
    private Location lokasi = null;
    //private Timer myTimer;
    private String company;
    private String salesman;
    private Synchronous syn = null;
    private BroadcastReceiver networkChange = null;

    private void loadFromConfig(){
        //read config min distance
        long min = 500;
        String min_distance_location_change = Config.getValue(getApplicationContext(), "min_distance_location_change");
        if(Helper.isNumber(min_distance_location_change)) {
            long parse = Long.parseLong(min_distance_location_change);
            if (parse != 0) min = parse;
        }
        MIN_INTERVAL_DISTANCE = min;

        String gps_tracking_interval = Config.getValue(getApplicationContext(), "gps_tracking_interval");
        long minInterval = 10;
        if(Helper.isNumber(gps_tracking_interval)) {
            long parse = Long.parseLong(gps_tracking_interval);
            if (parse != 0) minInterval = parse;
        }
        MIN_INTERVAL_UPDATE = minInterval * 60 * 1000;

        int difTime = 10;
        String gps_update_different_time = Config.getValue(getApplicationContext(), "gps_update_different_time");
        if(Helper.isNumber(gps_update_different_time)) {
            int parse = Integer.parseInt(gps_update_different_time);
            if (parse != 0) difTime = parse;
        }
        TIME_DIFFERENCE_THRESHOLD  = difTime * 60 * 1000;


    }

    @Override
    public void onCreate() {
        super.onCreate();
        //PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
     //   wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "UpNgsales");
    }

    private void updateLoc(Location lok){
        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();

        if(lok==null) {
            e.putString("LATITUDE", "0");
            e.putString("LONGITUDE", "0");
        }else{
            e.putString("LATITUDE", String.valueOf(lok.getLatitude()));
            e.putString("LONGITUDE", String.valueOf(lok.getLongitude()));
        }
        e.commit();

    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            try {
                if (isBetterLocation(lokasi, location)) {
                    lokasi = location;
                }

                SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                String currentSalesman = session.getString("CUR_SLS", "");
                String curCompany = session.getString("CUR_COMPANY", "");

                Synchronous s = new Synchronous(getApplicationContext(), null, curCompany,currentSalesman);
                s.sendGPSlocation(Helper.getBatteryLevel( getApplicationContext()), location.getLatitude(), location.getLongitude());

                updateLoc(lokasi);
            }catch (Exception x){
                Log.e("GPS ERROR",x.toString());
            }


        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setForeground();
        loadFromConfig();
        SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
        company =  session.getString("CUR_COMPANY", "");
        salesman =  session.getString("CUR_SLS", "");

        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        lokasi = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lokasi==null) lokasi = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(getProvider(locationManager), MIN_INTERVAL_UPDATE, MIN_INTERVAL_DISTANCE, listener);
        updateLoc(lokasi);

        if (lokasi!=null){
            Synchronous s = new Synchronous(getApplicationContext(), null, company, salesman);
            s.sendGPSlocation(Helper.getBatteryLevel( getApplicationContext()), lokasi.getLatitude(), lokasi.getLongitude());
        }


        if(networkChange==null){
            networkChange = new NetworkReceiver();
            registerReceiver(networkChange,new IntentFilter(NetworkReceiver.FILTER_TAG));
        }

/* hold by antrian
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
                //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WAKE ROCKET");
                //wl.acquire();
                try {
                    if(Helper.isOnline(getApplicationContext())) {
                        DBManager db = DBManager.getInstance(getApplicationContext());
                        syn = new Synchronous(getApplicationContext(), db.database(), company,salesman);
                        syn.post();

                    }
                }
                catch (Exception e) {
                    Log.e("WAKEUP","ERROR");
                }
                //finally{
                  //  wl.release();
                //}

            }

        }, 5000, 5*60*1000); // 5 menit ajah, delay 5 detik awal
*/

        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    //        myTimer.cancel();
    //      myTimer = null;

        if(networkChange!=null){
            unregisterReceiver(networkChange);
        }


    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setForeground() {
        startForeground(NotifInfo.notifId, NotifInfo.getNotification(getApplicationContext()));
    }

    public void setBackground() {
        stopForeground(true);
    }


    /* GPS */
    public class AsyncGPS extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
            } else {

            }


        }
    }



    private String getProvider(LocationManager lm) {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        //return LocationManager.NETWORK_PROVIDER;
        return lm.getBestProvider(criteria, true);
    }


    private boolean isBetterLocation(Location oldLocation, Location newLocation) {
        if(oldLocation == null) {
            return true;
        }

        boolean isNewer = newLocation.getTime() > oldLocation.getTime();

        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if(isMoreAccurate && isNewer) {
            return true;
        } else if(isMoreAccurate && !isNewer) {
            long timeDifference = newLocation.getTime() - oldLocation.getTime();

            if(timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
                return true;
            }
        }

        return false;
    }

}
