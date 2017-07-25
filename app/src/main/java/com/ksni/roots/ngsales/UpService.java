package com.ksni.roots.ngsales;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by #roots on 30/11/2015.
 */
public class UpService extends IntentService {

    public UpService(){
     super("UpService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
