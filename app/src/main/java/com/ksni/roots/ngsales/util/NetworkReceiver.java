package com.ksni.roots.ngsales.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by #roots on 29/10/2015.
 */
public class NetworkReceiver extends BroadcastReceiver{
    public static final String FILTER_TAG = "android.net.conn.CONNECTIVITY_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {

        if(isConnected(context)){
            // connect eventt iniiii
            Helper.notifyQueue(context.getApplicationContext());
        }
        else {

        }

    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }
}

/*
        enable

        ComponentName receiver = new ComponentName(MainActivity.this, MyReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP);
        Toast.makeText(this, "Disabled broadcst receiver", Toast.LENGTH_SHORT).show();
        }
        disable

        ComponentName receiver = new ComponentName(MainActivity.this, MyReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP);
        Toast.makeText(this, "Enabled broadcast receiver", Toast.LENGTH_SHORT).show();
        }
       */