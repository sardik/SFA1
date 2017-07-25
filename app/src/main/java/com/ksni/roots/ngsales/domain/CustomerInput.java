package com.ksni.roots.ngsales.domain;



import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ksni.roots.ngsales.model.Customer;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.util.Collection;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import com.ksni.roots.ngsales.util.validator.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 15/09/2015.
 */


public class CustomerInput extends AppCompatActivity implements CustomerGeneral.OnCompleteListener,
        CustomerAddress.OnCompleteListener,
        CustomerTax.OnCompleteListener,
        CustomerPhoto.OnCompleteListener {
    private PagerAdapter adapter;
    private Menu menu;
    private Form mForm;
    private ViewPager viewPager;

    public static Bitmap bitmap = null;

    private EditText tNo;
    private EditText tName;
    private EditText tAl;
    private EditText tPhone;
    private EditText tContact;
    private EditText tNotes;
    private EditText tStatus;
    private EditText tDeliveryDay;

    private EditText tAddr;
    private EditText tCity;
    private EditText tLat;
    private EditText tLong;

    private Spinner sChannel;
    private Spinner sRegion;
    private Spinner sZone;
    private Spinner sClassification;
    private Spinner sDistrict;
    private Spinner sTerritory;

    // field
    private String XtNo = "";
    private String XtName = "";
    private String XtAl = "";
    private String XtPhone = "";
    private String XtStatus = "";
    private String XtContact = "";
    private String XtNotes = "";
    private String XtTop = "";
    private String XtDeliveryDay = "";

    private String XtAddr = "";
    private String XtCity = "";
    private double XtLat = 0;
    private double XtLong = 0;

    private String XsChannel = "";
    private String XsRegion = "";
    private String XsZone = "";
    private String XsClassification = "";
    private String XsDistrict = "";
    private String XsTerritory = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


/*    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof MyFragment)
            this.myFragment = (MyFragment) fragment;
    }
  */


    @Override
    public void onCompleteCustomerPhoto() {
        View v = adapter.getItem(3).getView();
        Customer c = (Customer) getIntent().getSerializableExtra("objCust");
        TextView t = (TextView) v.findViewById(R.id.tLabel);
        if (c != null) {
            ImageView img = (ImageView) v.findViewById(R.id.imgPhoto);
            if (Helper.getNullString(c.getPicture()).length() > 0) {
                Bitmap b = Helper.getDecodeImage(c.getPicture());
                if (b != null) img.setImageBitmap(b);
            }
            img.setEnabled(false);
            t.setVisibility(View.GONE);
        } else {
            t.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCompleteCustomerGeneral() {
        View v = adapter.getItem(0).getView();
        Customer c = (Customer) getIntent().getSerializableExtra("objCust");

        //Spinner TermofPayment
        Spinner tTop = (Spinner) v.findViewById(R.id.tTop);
        final List<OutletTop> tops = OutletTop.getData(DBManager.getInstance(getApplicationContext()).database());
        String[] arrayX = new String[tops.size()];
        for (int i = 0; i < arrayX.length; i++) {
            arrayX[i] = tops.get(i).top_id;
        }
        ArrayAdapter<String> dataAdapterTop = new ArrayAdapter<String>(CustomerInput.this, R.layout.custom_spinner_item, arrayX);
        tTop.setAdapter(dataAdapterTop);

        //Spinner DeliveryDay
//        String ChooseDay = String.valueOf(R.string.customer_label_choose_delivery_day).toString();
        String ChooseDay = getResources().getString(R.string.customer_label_choose_delivery_day);
        String Monday = getResources().getString(R.string.customer_label_choose_delivery_day_mon);
        String Tuesday = getResources().getString(R.string.customer_label_choose_delivery_day_tue);
        String Wednesday = getResources().getString(R.string.customer_label_choose_delivery_day_wed);
        String Thursday = getResources().getString(R.string.customer_label_choose_delivery_day_thu);
        String Friday = getResources().getString(R.string.customer_label_choose_delivery_day_fri);
        String Saturday = getResources().getString(R.string.customer_label_choose_delivery_day_sat);
        String Sunday = getResources().getString(R.string.customer_label_choose_delivery_day_sun);

        Spinner tDeliveryDay = (Spinner) v.findViewById(R.id.tDeliveryDay);
        String[] arrayDeliveryDay = {ChooseDay, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday};
        ArrayAdapter<String> dataAdapterDeliveryDay = new ArrayAdapter<String>(CustomerInput.this,
                R.layout.custom_spinner_item, arrayDeliveryDay);
        tDeliveryDay.setAdapter(dataAdapterDeliveryDay);


        if (c != null) {

            tNo = (EditText) v.findViewById(R.id.tCustomerNumber);
            tName = (EditText) v.findViewById(R.id.tCustomerName);
            tAl = (EditText) v.findViewById(R.id.tCustomerAlias);
            tPhone = (EditText) v.findViewById(R.id.tPhone);
            tStatus = (EditText) v.findViewById(R.id.tStatus);
            tContact = (EditText) v.findViewById(R.id.tContact);
            tNotes = (EditText) v.findViewById(R.id.tNotes);

            tNo.setEnabled(false);
            tName.setEnabled(false);
            tAl.setEnabled(false);
            tPhone.setEnabled(false);
            tStatus.setEnabled(false);
            tContact.setEnabled(false);
            tNotes.setEnabled(false);
            tTop.setEnabled(false);
            tDeliveryDay.setEnabled(false);


//            tDeliveryDay.setText(String.valueOf(c.getDeliveryDay()));
            tNo.setText(c.getCustomerNumber());
            tName.setText(c.getCustomerName());
            tName.setTypeface(null, Typeface.BOLD);
            tAl.setText(c.getAlias());
            tAl.setTypeface(null, Typeface.BOLD);
            tPhone.setText(c.getPhone());
            tPhone.setTypeface(null, Typeface.BOLD);

            if (c.getStatus().equals("0")) {
                tStatus.setText("INACTIVE");
                tStatus.setTypeface(null, Typeface.BOLD);
            } else if (c.getStatus().equals("1")) {
                tStatus.setText("ACTIVE");
                tStatus.setTypeface(null, Typeface.BOLD);
            } else if (c.getStatus().equals("2")) {
                tStatus.setText("NOO");
                tStatus.setTypeface(null, Typeface.BOLD);
            }

            tContact.setText(c.getContact());
            tContact.setTypeface(null, Typeface.BOLD);
            tNotes.setText(c.getNotes());
            tNotes.setTypeface(null, Typeface.BOLD);


            for (int i = 0; i < tTop.getCount(); i++) {
                if (tTop.getItemAtPosition(i).toString().equals(c.getTop())) {
                    tTop.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < tDeliveryDay.getCount(); i++) {
                if (tDeliveryDay.getItemIdAtPosition(i) == c.getDeliveryDay()) {
                    tDeliveryDay.setSelection(i);
                    break;
                }
            }

        } else {
            LinearLayout linearStatus = (LinearLayout) v.findViewById(R.id.linearStatus);
            linearStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCompleteCustomerAddress() {
        View v = adapter.getItem(1).getView();

        Customer c = (Customer) getIntent().getSerializableExtra("objCust");
        if (c != null) {
            tAddr = (EditText) v.findViewById(R.id.tAddress);
            tCity = (EditText) v.findViewById(R.id.tCity);
            tLat = (EditText) v.findViewById(R.id.tLatitude);
            tLong = (EditText) v.findViewById(R.id.tLongitude);


            tAddr.setEnabled(false);
            tCity.setEnabled(false);
            tLat.setEnabled(false);
            tLong.setEnabled(false);

            tAddr.setText(c.getAddress());
            tAddr.setTypeface(null, Typeface.BOLD);
            tCity.setText(c.getCity());
            tCity.setTypeface(null, Typeface.BOLD);
            tLat.setText(String.valueOf(c.getLatitude()));
            tLat.setTypeface(null, Typeface.BOLD);
            tLong.setText(String.valueOf(c.getLongitude()));
            tLong.setTypeface(null, Typeface.BOLD);
        } else {
            tLat = (EditText) v.findViewById(R.id.tLatitude);
            tLong = (EditText) v.findViewById(R.id.tLongitude);
            tLat.setEnabled(false);
            tLong.setEnabled(false);

            //GPSTracker gps = new GPSTracker(this);
            //tLat.setText(String.valueOf(gps.getLatitude()));
            //tLong.setText(String.valueOf(gps.getLongitude()));

            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
            tLat.setText(session.getString("LATITUDE", "0.0"));
            tLat.setTypeface(null, Typeface.BOLD);
            tLong.setText(session.getString("LONGITUDE", "0.0"));
            tLong.setTypeface(null, Typeface.BOLD);


        }
    }

    @Override
    public void onCompleteCustomerOthers() {
        View v = adapter.getItem(2).getView();

        DBManager dm = DBManager.getInstance(getApplicationContext());

        List<Collection> listChannel = new ArrayList<Collection>();
        List<Channel> lChannel = Channel.getData(dm.database());
        listChannel.add(new Collection("", ""));
        for (Channel r : lChannel) {
            listChannel.add(new Collection(r.getGroupChannelDescription() + " " + r.getDescription(), r.getChannel()));
        }

        List<Collection> listRegion = new ArrayList<Collection>();
        List<Region> lRegion = Region.getData(dm.database());
        listRegion.add(new Collection("", ""));
        for (Region r : lRegion) {
            listRegion.add(new Collection(r.getDescription(), r.getRegion()));
        }


        List<Collection> listZone = new ArrayList<Collection>();
        List<Zone> lZone = Zone.getData(dm.database());
        listZone.add(new Collection("", ""));
        for (Zone r : lZone) {
            listZone.add(new Collection(r.getDescription(), r.getZone()));
        }

        List<Collection> listClassification = new ArrayList<Collection>();
        List<Classification> lClassification = Classification.getData(dm.database());
        listClassification.add(new Collection("", ""));
        for (Classification r : lClassification) {
            listClassification.add(new Collection(r.getDescription(), r.getClassification()));
        }

        List<Collection> listDistrict = new ArrayList<Collection>();
        List<District> lDistrict = District.getData(dm.database());
        listDistrict.add(new Collection("", ""));
        for (District r : lDistrict) {
            listDistrict.add(new Collection(r.getDescription(), r.getDistrict()));
        }

        List<Collection> listTerritory = new ArrayList<Collection>();
        List<Territory> lTerritory = Territory.getData(dm.database());
        listTerritory.add(new Collection("", ""));
        for (Territory r : lTerritory) {
            listTerritory.add(new Collection(r.getDescription(), r.getTerritory()));
        }

        final ArrayAdapter<Collection> aChannel = new ArrayAdapter<Collection>(this, R.layout.custom_spinner_item, listChannel);
        final ArrayAdapter<Collection> aRegion = new ArrayAdapter<Collection>(this, R.layout.custom_spinner_item, listRegion);
        final ArrayAdapter<Collection> aZone = new ArrayAdapter<Collection>(this, R.layout.custom_spinner_item, listZone);
        final ArrayAdapter<Collection> aClassification = new ArrayAdapter<Collection>(this, R.layout.custom_spinner_item, listClassification);
        final ArrayAdapter<Collection> aDistrict = new ArrayAdapter<Collection>(this, R.layout.custom_spinner_item, listDistrict);
        final ArrayAdapter<Collection> aTerritory = new ArrayAdapter<Collection>(this, R.layout.custom_spinner_item, listTerritory);

        sChannel = (Spinner) v.findViewById(R.id.sChannel);
        sRegion = (Spinner) v.findViewById(R.id.sRegion);
        sZone = (Spinner) v.findViewById(R.id.sZone);
        sClassification = (Spinner) v.findViewById(R.id.sClass);
        sDistrict = (Spinner) v.findViewById(R.id.sDistrict);
        sTerritory = (Spinner) v.findViewById(R.id.sTerritory);

        sChannel.setAdapter(aChannel);
        sRegion.setAdapter(aRegion);
        sZone.setAdapter(aZone);
        sClassification.setAdapter(aClassification);
        sDistrict.setAdapter(aDistrict);
        sTerritory.setAdapter(aTerritory);

        Customer c = (Customer) getIntent().getSerializableExtra("objCust");

        Settings settingan = new Settings(getApplicationContext());
        settingan.loadInfo();

        if (c != null) {

            for (int i = 0; i < listChannel.size(); i++) {
                Collection col = listChannel.get(i);
                if (c.getChannel().equals(col.tag)) {
                    sChannel.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < listRegion.size(); i++) {
                Collection col = listRegion.get(i);
                if (c.getRegion().equals(col.tag)) {
                    sRegion.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < listZone.size(); i++) {
                Collection col = listZone.get(i);

                if (c.getZone().equals(col.tag)) {
                    sZone.setSelection(i);
                    break;
                }

            }


            for (int i = 0; i < listClassification.size(); i++) {
                Collection col = listClassification.get(i);
                if (c.getClassification().equals(col.tag)) {
                    sClassification.setSelection(i);
                    break;
                }
            }


            for (int i = 0; i < listDistrict.size(); i++) {
                Collection col = listDistrict.get(i);
                if (c.getDistrict().equals(col.tag)) {
                    sDistrict.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < listTerritory.size(); i++) {
                Collection col = listTerritory.get(i);
                if (c.getTerritory().equals(col.tag)) {
                    sTerritory.setSelection(i);
                    break;
                }
            }


            sChannel.setEnabled(false);
            sRegion.setEnabled(false);
            sZone.setEnabled(false);
            sClassification.setEnabled(false);
            sDistrict.setEnabled(false);
            sTerritory.setEnabled(false);

        } else {
            sZone = (Spinner) v.findViewById(R.id.sZone);

            for (int i = 0; i < listZone.size(); i++) {
                Collection col = listZone.get(i);

//                String zone = settingan.zone;
//                Object temp1 = col.tag;
//                String temp2 = col.string;

                if (col.tag.equals(settingan.zone)) {
                    sZone.setSelection(i);
                    break;
                }

            }

            //sZone.setEnabled(false);
        }


    }

    private void updateData(int loc) {
        if (loc == 0) {
            XtName = ((EditText) adapter.getItem(0).getView().findViewById(R.id.tCustomerName)).getText().toString();
            XtAl = ((EditText) adapter.getItem(0).getView().findViewById(R.id.tCustomerAlias)).getText().toString();
            XtPhone = ((EditText) adapter.getItem(0).getView().findViewById(R.id.tPhone)).getText().toString();
            XtStatus = ((EditText) adapter.getItem(0).getView().findViewById(R.id.tStatus)).getText().toString();
            XtContact = ((EditText) adapter.getItem(0).getView().findViewById(R.id.tContact)).getText().toString();
            XtNotes = ((EditText) adapter.getItem(0).getView().findViewById(R.id.tNotes)).getText().toString();
            XtTop = ((Spinner) adapter.getItem(0).getView().findViewById(R.id.tTop)).getSelectedItem().toString();

            XtDeliveryDay = ((Spinner) adapter.getItem(0).getView().findViewById(R.id.tDeliveryDay)).getSelectedItem().toString();
            if (XtDeliveryDay.equals("Senin")) {
                XtDeliveryDay = "1";
            } else if (XtDeliveryDay.equals("Selasa")) {
                XtDeliveryDay = "2";
            } else if (XtDeliveryDay.equals("Rabu")) {
                XtDeliveryDay = "3";
            } else if (XtDeliveryDay.equals("Kamis")) {
                XtDeliveryDay = "4";
            } else if (XtDeliveryDay.equals("Jumat")) {
                XtDeliveryDay = "5";
            } else if (XtDeliveryDay.equals("Sabtu")) {
                XtDeliveryDay = "6";
            } else if (XtDeliveryDay.equals("Minggu")) {
                XtDeliveryDay = "7";
            } else {
                XtDeliveryDay = "0";
            }
        } else if (loc == 1) {
            XtAddr = ((EditText) adapter.getItem(1).getView().findViewById(R.id.tAddress)).getText().toString();
            XtCity = ((EditText) adapter.getItem(1).getView().findViewById(R.id.tCity)).getText().toString();
            String lat = ((EditText) adapter.getItem(1).getView().findViewById(R.id.tLatitude)).getText().toString();
            XtLat = Double.parseDouble(lat);
            String lon = ((EditText) adapter.getItem(1).getView().findViewById(R.id.tLongitude)).getText().toString();
            XtLong = Double.parseDouble(lon);
        } else if (loc == 2) {

            Spinner s1 = ((Spinner) adapter.getItem(2).getView().findViewById(R.id.sChannel));
            XsChannel = ((Collection) s1.getSelectedItem()).tag.toString();

            Spinner s2 = ((Spinner) adapter.getItem(2).getView().findViewById(R.id.sRegion));
            XsRegion = ((Collection) s2.getSelectedItem()).tag.toString();

            Spinner s3 = ((Spinner) adapter.getItem(2).getView().findViewById(R.id.sZone));
            XsZone = ((Collection) s3.getSelectedItem()).tag.toString();

            Spinner s4 = ((Spinner) adapter.getItem(2).getView().findViewById(R.id.sClass));
            XsClassification = ((Collection) s4.getSelectedItem()).tag.toString();

            Spinner s5 = ((Spinner) adapter.getItem(2).getView().findViewById(R.id.sDistrict));
            XsDistrict = ((Collection) s5.getSelectedItem()).tag.toString();

            Spinner s6 = ((Spinner) adapter.getItem(2).getView().findViewById(R.id.sTerritory));
            XsTerritory = ((Collection) s6.getSelectedItem()).tag.toString();

        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        bitmap = null;

        setContentView(R.layout.ui_input_customer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        final ActionBar ab = getSupportActionBar();

        Customer c = (Customer) getIntent().getSerializableExtra("objCust");

        if (c != null)

            ab.setTitle(Helper.getStrResource(this, R.string.customer_title_view));
        else
            ab.setTitle(Helper.getStrResource(this, R.string.customer_title_create_customer));

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        //Create View Pager
        List<Fragment> fragments = getFragments();
        adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

//        Cara Lama. Updated By: Obbie
//        tabLayout.addTab(tabLayout.newTab().setText(Helper.getStrResource(this,R.string.customer_general_tab)));
//        tabLayout.addTab(tabLayout.newTab().setText(Helper.getStrResource(this,R.string.customer_address_tab)));
//        tabLayout.addTab(tabLayout.newTab().setText(Helper.getStrResource(this,R.string.customer_others_tab)));
//        tabLayout.addTab(tabLayout.newTab().setText(Helper.getStrResource(this,R.string.customer_photo_tab)));
//        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Log.e("onTabSelected", String.valueOf(tab.getPosition()));
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Log.e("onTabUnselected", String.valueOf(tab.getPosition()));
                updateData(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Log.e("onTabReselected", String.valueOf(tab.getPosition()));
            }
        });

        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {

            View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) view.findViewById(R.id.tvTab);
            ImageView iv = (ImageView) view.findViewById(R.id.ivTab);

            if (i == viewPager.getCurrentItem());
            //tv.setText(viewPager.getAdapter().getPageTitle(i));
            switch (i) {
                case 0:
                    tv.setText(Helper.getStrResource(this, R.string.customer_general_tab));
                    iv.setImageResource(R.drawable.ic_person_white_24dp);
                    //iv.setBackgroundResource(R.drawable.ic_person_white_48dp);
                    // tv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_person_white_48dp, 0, 0);
                    break;
                case 1:
                    tv.setText(Helper.getStrResource(this, R.string.customer_address_tab));
                    iv.setImageResource(R.drawable.ic_my_location_white_24dp);
                    // tv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_my_location_white_48dp, 0, 0);
                    break;
                case 2:
                    tv.setText(Helper.getStrResource(this, R.string.customer_others_tab));
                    iv.setImageResource(R.drawable.ic_more_horiz_white_24dp);
                    // tv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_more_horiz_white_48dp, 0, 0);
                    break;
                case 3:
                    tv.setText(Helper.getStrResource(this, R.string.customer_photo_tab));
                    iv.setImageResource(R.drawable.ic_photo_camera_white_24dp);
                    // tv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_photo_camera_white_48dp, 0, 0);
                    break;
            }

            tabLayout.getTabAt(i).setCustomView(view);
        }


        //initValidationForm();

        viewPager.setCurrentItem(0);
        //updateData(0);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(new CustomerGeneral());
        fList.add(new CustomerAddress());
        fList.add(new CustomerTax());
        fList.add(new CustomerPhoto());
        return fList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_add, menu);
        Customer c = (Customer) getIntent().getSerializableExtra("objCust");
        if (c != null) {
            menu.getItem(0).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (id == R.id.action_save_customer) {

            updateData(viewPager.getCurrentItem());
            //Log.e("adapter",viewPager.getCurrentItem())
            //View vGeneral = adapter.getItem(0).getView();
            //View vAddress = adapter.getItem(1).getView();
            //View vOthers = adapter.getItem(2).getView();

//            viewPager.set`
            //View vOthers = ((ViewGroup)findViewById(R.)).getChildAt(2);
            //sChannel = (Spinner)vOthers.findViewById(R.id.sChannel);


            //tName = (EditText) vGeneral.findViewById(R.id.tCustomerName);
            //tAddr = (EditText) vAddress.findViewById(R.id.tAddress);
            //sChannel = (Spinner)vOthers.findViewById(R.id.sChannel);

            //tName.setText("aaaa");
            //tAddr.setText("addr");
            //sChannel.setSelection(1);

            //Log.e("COUNT",String.valueOf(adapter.getCount()));
            //View vGeneral = adapter.getItem(0).getView();
            //View vAddress = adapter.getItem(1).getView();
            //View vOthers =   adapter.getItem(2).getView();

            // general
            //tName = (EditText) vGeneral.findViewById(R.id.tCustomerName);
            //tAl = (EditText) vGeneral.findViewById(R.id.tCustomerAlias);
            //tPhone = (EditText) vGeneral.findViewById(R.id.tPhone);
            //tStatus = (EditText) vGeneral.findViewById(R.id.tStatus);

            // address
            //tAddr = (EditText) vAddress.findViewById(R.id.tAddress);
            //tCity = (EditText) vAddress.findViewById(R.id.tCity);
            //tLat = (EditText) vAddress.findViewById(R.id.tLatitude);
            //tLong = (EditText) vAddress.findViewById(R.id.tLongitude);

            // Others
            //sChannel = (Spinner)vOthers.findViewById(R.id.sChannel);
            //sRegion = (Spinner)vOthers.findViewById(R.id.sRegion);
            //sZone = (Spinner)vOthers.findViewById(R.id.sZone);
            //sClassification = (Spinner)vOthers.findViewById(R.id.sClass);
            //sDistrict = (Spinner)vOthers.findViewById(R.id.sDistrict);
            //sTerritory = (Spinner)vOthers.findViewById(R.id.sTerritory);

            // Photo


            //if (mForm.isValid()) {
            Customer customer = new Customer(DBManager.getInstance(this).database());

            String err = "";
            if (Helper.isEmpty(XtName)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_customer_name);
            }
            //if (Helper.isEmpty(XtAl))               {err += "Customer name alias is required.\n";}
            if (Helper.isEmpty(XtPhone)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_phone);
            }
            if (Helper.isEmpty(XtContact)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_contact_person);
            }
            if (Helper.isEmpty(XtAddr)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_address);
            }
            if (Helper.isEmpty(XtCity)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_city);
            }
            if (Helper.isEmpty(XsRegion)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_region);
            }
            if (Helper.isEmpty(XsZone)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_zone);
            }
            if (Helper.isEmpty(XsClassification)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_classification);
            }
            if (Helper.isEmpty(XsDistrict)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_district);
            }
            if (Helper.isEmpty(XsTerritory)) {
                err += Helper.getStrResource(this, R.string.customer_err_message_blank_territory);
            }

            if (err.length() > 0) {
                Helper.msgbox(this, err, Helper.getStrResource(this, R.string.common_msg_error));
            } else {
                String buffNo = "";
                buffNo = Helper.getCurrentDateTime("yyMMddHHmm"); //yyyy-MM-dd HH:mm:ss
                //Date dt = new Date();
                //String buffNo = String.valueOf(dt.getTime());

                Settings sett;
                sett = Settings.getSettings(getApplicationContext());
                String branch;
                branch = String.valueOf(sett.branch);

                if (branch.length() == 1) {
                    buffNo = "000" + branch + buffNo;
                } else if (branch.length() == 2) {
                    buffNo = "00" + branch + buffNo;
                } else if (branch.length() == 3) {
                    buffNo = "0" + branch + buffNo;
                } else if (branch.length() == 4) {
                    buffNo = branch + buffNo;
                } else {
                    //Lebih dari 4 digit
                }

                customer.setCustomerNumber(buffNo);
                customer.setCustomerName(XtName.toUpperCase());
                customer.setAlias(XtAl.toUpperCase());
                customer.setPhone(XtPhone.toUpperCase());
                customer.setStatus(XtStatus.toUpperCase());
                if (XtDeliveryDay.length() == 0) XtDeliveryDay = "0";
                customer.setDeliveryDay(Integer.parseInt(XtDeliveryDay));
                customer.setContact(XtContact.toUpperCase());
                customer.setNotes(XtNotes.toUpperCase());
                customer.setTop(XtTop.toUpperCase());

                customer.setAddress(XtAddr.toUpperCase());
                customer.setCity(XtCity.toUpperCase());
                customer.setLatitude(XtLat);
                customer.setLongitude(XtLong);

                Channel chn = Channel.getData(DBManager.getInstance(this).database(), XsChannel);
                if (chn != null) customer.setGroupChannel(chn.getGroupChannel());

                customer.setChannel(XsChannel.toUpperCase());
                customer.setRegion(XsRegion.toUpperCase());
                customer.setZone(XsZone.toUpperCase());
                customer.setClassification(XsClassification.toUpperCase());
                customer.setDistrict(XsDistrict.toUpperCase());
                customer.setTerritory(XsTerritory.toUpperCase());

                if (customer.save()) {
                        /*
                        if (MainActivity.dataCustomerCall.size()>0){

                            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                            String currentSalesman= session.getString("CUR_SLS","");

                                // save ke call plan
                                CustomerCall ccx = new CustomerCall(DBManager.getInstance(getApplicationContext()).database());
                                CustomerCall buff =MainActivity.dataCustomerCall.get(0);
                                ccx.setCallStatus("1");
                                ccx.setId(Helper.getCurrentDateTime("yyMMddHHmmss"));
                                ccx.setServerDate(buff.getServerDate());
                                ccx.setWeek(buff.getWeek());
                                ccx.setSlsId(currentSalesman);
                                ccx.setStatus(CustomerCall.NO_VISIT);
                                ccx.setCustomerNumber(customer.getCustomerNumber());
                                ccx.setCustomerName(customer.getCustomerName());
                                ccx.setAlias(customer.getAlias());
                                ccx.setAddress(customer.getAddress());
                                ccx.setCity(customer.getCity());
                                ccx.setGroupChannel(customer.getGroupChannel());
                                ccx.setChannel(customer.getChannel());
                                ccx.setRegion(customer.getRegion());
                                ccx.setZone(customer.getZone());
                                ccx.setClassification(customer.getClassification());
                                ccx.setDistrict(customer.getDistrict());
                                ccx.setTerritory(customer.getTerritory());
                                ccx.save();
                                MainActivity.dataCustomerCall.add(ccx);

                        }
                        */
                    Toast.makeText(getApplicationContext(), "Customer saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Customer saved", Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "CustomerInput Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.ksni.roots.ngsales.domain/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "CustomerInput Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.ksni.roots.ngsales.domain/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }
}