package com.ksni.roots.ngsales.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by #roots on 12/12/2015.
 */
public class SyncCallPlan {
    private Context context;

    public SyncCallPlan(Context context){
        this.context = context;
    }

    public boolean parseCallPlan(JSONObject response) {
        boolean buff = false;
        try {
            if (response.optString("command").equals("sync_call_plan")){
                JSONArray dCall         = response.optJSONArray("call_plans");
                JSONArray dTarget       = response.optJSONArray("targets");
                JSONArray dTemplate     = response.optJSONArray("last_calls");
                JSONArray dInformation  = response.optJSONArray("information");


                DBManager dm = DBManager.getInstance(context);
                String dataWeek = response.optString("week_in_year");

                SharedPreferences session = context.getSharedPreferences("ngsales", 0);
                String curSls = session.getString("CUR_SLS", "");

                SharedPreferences.Editor e = session.edit();
                e.putString("CUR_WEEK",dataWeek);
                e.commit();

                // Save from Server to Local db
                CallPlanInit.getCallPlanFromServer(context, dm.database(), dCall);
                CallPlanInit.getTargetFromServer(dm.database(), dTarget);
                CallPlanInit.getSkuTemplateFromServer(dm.database(), dTemplate);
                CallPlanInit.getInformationFromServer(dm.database(), dInformation);

                // update last syn callplan
                String start_d = response.optString("start_date");
                String end_d = response.optString("end_date");
                int year = response.optInt("year");
                int period = response.optInt("period");
                Settings.initCallPlan(context, curSls, dataWeek, "", Helper.getCurrentDateTime(), start_d, end_d, year, period);
                CallPlanInit.loadPlan(context);

            }


        } catch (Exception e) {
            buff= false;
        }

        return buff;

    }
}
