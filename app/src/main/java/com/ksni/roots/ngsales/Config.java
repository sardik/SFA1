package com.ksni.roots.ngsales;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ksni.roots.ngsales.model.ServerConfig;
import com.ksni.roots.ngsales.util.DBManager;

import java.util.List;

/**
 * Created by #roots on 24/12/2015.
 */
public class Config {


    public static String getValue(Context context,String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key,null);
    }

    public static boolean getChecked(Context context,String key){
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getBoolean(key, false);
        }catch(Exception ex){
            return false;
        }
    }


    public static void loadAppPrivacyConfig(Context ctx){
        List<ServerConfig> lists = ServerConfig.getData(DBManager.getInstance(ctx).database());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor e= sp.edit();
        for(ServerConfig list:lists){
            if (list.getValue().equals("Yes") || list.getValue().equals("No")){
                if (list.getValue().equals("Yes"))
                    e.putBoolean(list.getKey(),true);
                else
                    e.putBoolean(list.getKey(),false);
            }else{
                e.putString(list.getKey(),list.getValue());
            }
        }
        e.commit();
    }
}
