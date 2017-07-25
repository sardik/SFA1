package com.ksni.roots.ngsales;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.ksni.roots.ngsales.model.ServerConfig;
import com.ksni.roots.ngsales.util.DBManager;

import java.util.List;

/**
 * Created by #roots on 20/12/2015.
 */
public class AppConfig extends PreferenceFragment {


        private Preference getPref(String key,String value,String description, boolean checkbox){
            Preference p = null;
            if (checkbox) {
                CheckBoxPreference chk = new CheckBoxPreference(getActivity());
                chk.setKey(key);
                if (value.equals("Yes"))
                    chk.setChecked(true);
                else
                    chk.setChecked(false);

                chk.setSummary(description);
                chk.setEnabled(false);

                p = chk;
            }else{
                EditTextPreference  text = new EditTextPreference(getActivity());
                text.setKey(key);
                text.setText(value);
                text.setTitle(value);
                text.setSummary(description);
                text.setEnabled(false);

                p = text;

            }

            return p;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            PreferenceCategory settingsCategory = (PreferenceCategory)findPreference("settings");
            List<ServerConfig> lists = ServerConfig.getData(DBManager.getInstance(getActivity().getApplicationContext()).database());

            for(ServerConfig list:lists){
                boolean chk = false;
                if (list.getValue().equals("Yes") || list.getValue().equals("No") ) chk = true;

                Preference pref =  getPref(list.getKey(),list.getValue(),list.getDescription(),chk);
                settingsCategory.addPreference(pref);
            }



            //CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this);
            //checkBoxPreference.setKey("keyName");
            //checkBoxPreference.setChecked(true);
            //targetCategory.addPreference(checkBoxPreference);


            /*
            SharedPreferences session = getActivity().getApplicationContext().getSharedPreferences("ngsales", 0);
            PreferenceManager a  =this.getPreferenceManager();

            EditTextPreference tCompany = (EditTextPreference)findPreference("company");
            tCompany.setTitle(session.getString("CUR_COMPANY", ""));

            EditTextPreference tUserName = (EditTextPreference)findPreference("user_name");
            tUserName.setTitle(session.getString("CUR_USER",""));

            EditTextPreference tVisit = (EditTextPreference)findPreference("current_customer_visit");
            tVisit.setTitle(session.getString("CUR_VISIT","")+ " - "+session.getString("CUR_VISIT_NAME",""));

            EditTextPreference tWorkStart = (EditTextPreference)findPreference("work_start");
            tWorkStart.setTitle(session.getString("CUR_WORK_START",""));

            EditTextPreference tWorkEnd = (EditTextPreference)findPreference("work_end");
            tWorkEnd.setTitle(session.getString("CUR_WORK_END",""));

            EditTextPreference tOdometerStart = (EditTextPreference)findPreference("start_odometer");
            tOdometerStart.setTitle(session.getString("CUR_WORK_START",""));

            EditTextPreference tOdometerEnd = (EditTextPreference)findPreference("end_odometer");
            tOdometerStart.setTitle(session.getString("CUR_WORK_END",""));
            */



  //          ed.setTitle("lalalalalala");

        }
    }
