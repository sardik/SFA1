package com.ksni.roots.ngsales;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by #roots on 30/11/2015.
 */
public class BootstrapReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, UpService.class);
        context.startService(startServiceIntent);
    }
}
