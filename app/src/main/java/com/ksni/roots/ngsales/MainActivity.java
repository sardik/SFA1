package com.ksni.roots.ngsales;
import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.app.AlertDialog;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewTreeObserver;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.ksni.roots.ngsales.domain.AsynActivity;
import com.ksni.roots.ngsales.domain.Attendance;
import com.ksni.roots.ngsales.domain.CallPlan;
import com.ksni.roots.ngsales.domain.CallPlanInit;
import com.ksni.roots.ngsales.domain.CallPlanMap;
import com.ksni.roots.ngsales.domain.Customer;
import com.ksni.roots.ngsales.domain.Information;
import com.ksni.roots.ngsales.domain.Login;
import com.ksni.roots.ngsales.domain.ProductBrandData;
import com.ksni.roots.ngsales.domain.ProductData;
import com.ksni.roots.ngsales.domain.ProductStock;
import com.ksni.roots.ngsales.domain.SummaryActivity;
import com.ksni.roots.ngsales.domain.SynchronousData;
import com.ksni.roots.ngsales.domain.Target;
import com.ksni.roots.ngsales.domain.ViewOrderActivity;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.CustomerSKU;
import com.ksni.roots.ngsales.model.InfoPromo;
import com.ksni.roots.ngsales.model.Order;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.model.ServerConfig;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.FontsOverride;
import com.ksni.roots.ngsales.util.Helper;
import com.ksni.roots.ngsales.util.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    //private int versionCode = 9;
    public static int versionCode = 313; //PMA
    private Fragment fSettings          = null;
    private Fragment fLoadingUnloading  = null;
    private Fragment fAttendance        = null;
    private Fragment fCallPlan          = null;
    private Fragment fCallPlanMap       = null;
    private Fragment fProductData       = null;
    private Fragment fCustomer          = null;
    private Fragment fTarget            = null;
    private Fragment fViewOrderActivity = null;
    private Fragment fAsynActivity      = null;
    private Fragment fSummary           = null;
    private String curCompany           = "";
    private String curUserName          = "";

    public static int HTTP_READ_TIMEOUT = 20;
    public static int HTTP_CONECTION_TIMEOUT = 20;
    private static final int MENU_ITEMS = 12;

    public static final     String BASE_URL_PRODUCTION   = "http://sfa.pinusmerahabadi.co.id/sfa-admin/request";//;//
    public static final     String BASE_URL_TRIAL        = "http://sfatrial.pinusmerahabadi.co.id/sfa-admin/request";//;//

    //public static final     String BASE_URL_TRIAL        = "http://192.168.30.79:8888/sfa-barebone/public/request";//;//
    //public static final     String BASE_URL_TRIAL        = "http://192.168.0.8:8888/sfa-barebone/public/request";//;//
    //public static final     String BASE_URL_TRIAL        = "http://192.168.20.41:8888/sfa-barebone/public/request";//;//"http://192.168.200.139:8888/sfa-barebone/public/request";//;//
    //public static final     String BASE_URL_TRIAL        = "http://10.0.2.2:8888/sfa-barebone/public/request";//;//

    public static final     String BASE_URL_DEVELOPMENT  = "http://dev.enerlifegroup.com/sfa-admin/request";//"http://10.1.40.29/sfa/public/request";//
    public static final     String BASE_URL             = BASE_URL_PRODUCTION; //Login.baseUrl;// "http://sfa.enerlifegroup.com/sfa-admin/request";

    //public static final     String BASE_URL             = BASE_URL_PRODUCTION;// "http://sfa.enerlifegroup.com/sfa-admin/request";
    //public static final     String BASE_URL             = "http://10.1.40.29/sfa-pma/public/request";
    //public static final     String BASE_URL         = "http://10.1.40.29/sfa/public/request";
    //public static final     String BASE_URL         = "http://10.1.40.40/sfa/public/request";
    //public static final     String BASE_URL         = "http://10.1.50.166/sfa/public/request";
    //public static final     String BASE_URL         = "http://10.1.50.166/";
    //public static final     String BASE_URL         = "http://192.168.43.120/";

    public static final     String INIT_URL         = BASE_URL + "sls/sls/init";
    public static final     String WORK_TIME        = BASE_URL + "sls/sls/log";
    public static final     String SYNC             = BASE_URL + "sls/sls/sync";
    public static final     String WRITE_URL        = BASE_URL + "sls/sls/write";
    public static final     String WRITE_GPS        = BASE_URL + "sls/sls/gps";


    private ProgressDialog progressDialog;
    private DrawerLayout mDrawer;
    private DrawerLayout dlDrawer;
    private NavigationView nvDrawer;
    private View headerNav; // << PENAMBAHAN VARIABLE BARU, KARENA BUG NAVIGATION HEADER

    private Toolbar toolbar;

    public static String currentSalesman;
    public static String currentSalesmanName;
    private ActionBarDrawerToggle drawerToggle;
    private ViewPager pager;

    public static List<CustomerCall> dataCustomerCall = new ArrayList<CustomerCall>();
    public static List<Product> dataSku = new ArrayList<Product>();
    public static List<OrderItem> dataOrder = new ArrayList<OrderItem>();
    //public static List<OrderItem> dataTemplate = new ArrayList<OrderItem>();
    private String dataInfo;
    private String dataWeek;

    private final ArrayList<View> mMenuItems = new ArrayList<>(MENU_ITEMS);
    private PageAdapter pageAdapter;
    private boolean mBound = false;
    private Location oldLocation;
    String appURI = "";
    private DownloadManager downloadManager;
    private long downloadReference;
    private NgsalesWebReceiver receiver;
    private boolean isFirstCheckVersion = true;

    private IntentFilter filter;



    private void checkVersion(){

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e) {

        }

        //String version = pInfo.versionName;
        //versionCode = pInfo.versionCode;

        TextView versionText = (TextView) headerNav.findViewById(R.id.tVersion);
        versionText.setText("Version 1.0.1 Rev." + String.valueOf(versionCode));

        if(Helper.isNetworkAvailable(this)){
            Intent msgIntent = new Intent(this, UpdateAPK.class);
            msgIntent.putExtra("inVersion", versionCode);
            //msgIntent.putExtra(UpdateAPK.LOG_TAG, UpdateAPK.HTTP_CHECK_VERSION);
            startService(msgIntent);
        }

        IntentFilter filter = new IntentFilter(NgsalesWebReceiver.PROCESS_RESPONSE); //chekversion if lastestversion > versionCode
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        receiver = new NgsalesWebReceiver();
        registerReceiver(receiver, filter);

        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

    }

    public class NgsalesWebReceiver extends BroadcastReceiver{
        public static final String PROCESS_RESPONSE = "com.ksni.roots.ngsales.intent.action.PROCESS_RESPONSE";
        @Override
        public void onReceive(Context context, Intent intent) {
            String reponseMessage = intent.getStringExtra(UpdateAPK.RESPONSE_MESSAGE);

            JSONObject responseObj;
            try {
                responseObj = new JSONObject(reponseMessage);
                String success = responseObj.getString("success");

                if(success.equals("true")){
                    int latestVersion = responseObj.getInt("upload_version");

                    appURI = responseObj.getString("url");

                    Log.e("GET", "Response data = " + reponseMessage);
                    //Log.e("GET", "Lastest Version : " + latestVersion);
                    //Log.e("GET", "Link Download : " + appURI);

                    if(latestVersion > versionCode){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setCancelable(false);
                        builder.setMessage(Helper.getStrResource(MainActivity.this,R.string.app_update_prompt_text))
                                .setPositiveButton(Helper.getStrResource(MainActivity.this, R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        Uri Download_Uri = Uri.parse(appURI);
                                        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                        request.setAllowedOverRoaming(false);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setTitle(Helper.getStrResource(MainActivity.this,R.string.common_msg_confirm));
                                        request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "pma.apk");
                                        Log.e("DIR", Environment.DIRECTORY_DOWNLOADS);
                                        downloadReference = downloadManager.enqueue(request);
                                    }
                                })
                                .setNegativeButton(Helper.getStrResource(MainActivity.this,R.string.app_update_prompt_button_remind_later), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //update
                                        //unregisterReceiver(receiver);
                                        //unregisterReceiver(downloadReceiver);

//                                        if (receiver!=null) unregisterReceiver(receiver);
//                                        if (downloadReceiver!=null)unregisterReceiver(downloadReceiver);

                                        dialog.dismiss();

                                        finish();
                                        //dialog.cancel();
                                    }
                                });

                        builder.create().show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onStart(){
        super.onStart();

        //        if (receiver!=null) unregisterReceiver(receiver);
//        if (downloadReceiver!=null)unregisterReceiver(downloadReceiver);
        //checkVersion();

        //Log.e("START", "onStart");
    }

    @Override
    public void onStop(){
        super.onStop();

        //Log.e("STOP", "onStop");
        //wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (receiver!=null) this.unregisterReceiver(receiver);
//        if (downloadReceiver!=null)this.unregisterReceiver(downloadReceiver);

//        if(Helper.isNetworkAvailable(this)){
//            Intent msgIntent = new Intent(this, UpdateAPK.class);
//            //msgIntent.putExtra(UpdateAPK.LOG_TAG, UpdateAPK.HTTP_CHECK_VERSION);
//            startService(msgIntent);
//        }

        //Log.e("RESUME", "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Log.e("PAUSE", "onPause");

    }

    private void loadSKU(){
        DBManager dm = DBManager.getInstance(this);

        Product prd = null;
        dataSku.clear();
        Cursor cur = dm.database().rawQuery("SELECT * FROM sls_product", null);
        if (cur.moveToFirst()) {
            do{
                prd = new Product();
                prd.setProductId(cur.getString(cur.getColumnIndex("product_id")));
                prd.setProductName(cur.getString(cur.getColumnIndex("product_name")));
                prd.setAlias(cur.getString(cur.getColumnIndex("product_name_alias")));
                prd.setDivision(cur.getString(cur.getColumnIndex("division")));
                prd.setUom(cur.getString(cur.getColumnIndex("base_uom")));
                prd.setProductType(cur.getString(cur.getColumnIndex("product_type")));
                prd.setBrand(cur.getString(cur.getColumnIndex("product_brands")));
                prd.setCategory(cur.getString(cur.getColumnIndex("product_category")));
                prd.setPrice(cur.getDouble(cur.getColumnIndex("price")));
                prd.setUomSmall(cur.getString(cur.getColumnIndex("small_uom")));
                prd.setUomMedium(cur.getString(cur.getColumnIndex("medium_uom")));
                prd.setUomLarge(cur.getString(cur.getColumnIndex("large_uom")));
                prd.setConversionMediumToSmall(cur.getInt(cur.getColumnIndex("medium_to_small")));
                prd.setConversionLargeToSmall(cur.getInt(cur.getColumnIndex("large_to_small")));
                dataSku.add(prd);

            }while(cur.moveToNext());
        }
        cur.close();

    }

    @Override
    public void onDestroy(){

        if (receiver!=null) this.unregisterReceiver(receiver);
        if (downloadReceiver!=null)this.unregisterReceiver(downloadReceiver);

        super.onDestroy();

        Log.e("DESTROY", "onDestroy");



        //wakeLock.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);

        //Log.e("CREATE", "onCreate");//
        //Intent in = getIntent();
        //MainActivity.BASE_URL = in.getStringExtra("BASE_URL");

        DBManager dm = DBManager.getInstance(getApplicationContext());

        String TABLE_REASON_NOCALL =
                "CREATE TABLE IF NOT EXISTS sls_reason_nocall ( " +
                "   `reason_id`	      TEXT NOT NULL PRIMARY KEY, " +
                "   `description`	  TEXT " +
                ")";

        try{
            dm.database().execSQL(TABLE_REASON_NOCALL);
        } catch(Exception x){}

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Titillium-Light.otf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Titillium-Light.otf");
        FontsOverride.setDefaultFont(this, "SANS", "fonts/Titillium-Bold.otf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Titillium-Bold.otf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Titillium-Bold.otf");

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_app);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        headerNav = nvDrawer.inflateHeaderView(R.layout.nav_header);
        ImageView imageView = (ImageView) headerNav.findViewById(R.id.imageView);

        if (Login.baseUrl.equals(BASE_URL_DEVELOPMENT))
            imageView.setImageResource(R.drawable.logo_dev);
        else if (Login.baseUrl.equals(BASE_URL_TRIAL))
            imageView.setImageResource(R.drawable.logo);
        else if (Login.baseUrl.equals(BASE_URL_PRODUCTION))
            imageView.setImageResource(R.drawable.logo);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    TextView tVersion = (TextView)findViewById(R.id.tVersion);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Version 1.0.1 Rev." + String.valueOf(versionCode))
                            .setTitle("SFA Mobile")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    checkVersion();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                    //Helper.msgbox(MainActivity.this,"Version 1.0.0 ("+tVersion.getText().toString()+")","Version");
                    //checkVersion();
                    /*dataOrder.clear();
                    dataSku.clear();

                    new AsyncKSNIinit().execute(BASE_URL, currentSalesman);
                    */

                } catch (Exception ex) {
                    Helper.showToast(getApplicationContext(), "Error while retrieving data.");
                }
            }
        });

        /*CustomNavigationView navigationView = (CustomNavigationView) findViewById(R.id.nvView);
        navigationView.createHeader(R.layout.nav_header);

        View v =getLayoutInflater().inflate(R.layout.nav_header,(ViewGroup) navigationView);

        ViewGroup parent = (ViewGroup) navigationView;
        View view = parent.getChildAt(0);
        navigationView.sizeMenu(view,v.getHeight());
*/

        final Menu navMenu = nvDrawer.getMenu();

        nvDrawer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                nvDrawer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Loop through and find each MenuItem View
                for (int i = 0, length = MENU_ITEMS; i < length; i++) {
                    final String id = "menu" + (i);
                    final MenuItem item = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                    nvDrawer.findViewsWithText(mMenuItems, item.getTitle(), View.FIND_VIEWS_WITH_TEXT);
                }

                String fontPath = "fonts/Titillium-Light.otf";
                Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

                for (final View menuItem : mMenuItems) {
                    ((TextView) menuItem).setTextSize(16);
                    ((TextView) menuItem).setTypeface(tf);
                    ((TextView) menuItem).setShadowLayer(new Float(1), new Float(1), new Float(1), Color.LTGRAY);
                }
            }
        });

  //      pager = (ViewPager)findViewById(R.id.rootPager);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, new Attendance()).commit();

        setupDrawerContent(nvDrawer);

        mDrawer.openDrawer(GravityCompat.START);
//        pager.setCurrentItem(0);
        setTitle("PINUS MERAH ABADI");

        init();

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        int slst = session.getInt("CUR_SLS_TYPE",-1);

        if(slst==Order.SALES_TAKING_ORDER)
            navMenu.findItem(R.id.menu11).setVisible(false);
        else
            navMenu.findItem(R.id.menu11).setVisible(true);

        if (!Settings.hasSyncMaster(getApplicationContext())){
            AlertDialog.Builder builder = new Builder(MainActivity.this);
            builder.setMessage(Helper.getStrResource(this, R.string.app_load_data_confim));
            builder.setTitle(Helper.getStrResource(this, R.string.common_msg_confirm));


            builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    //hold by antrian
                    //DBManager dm = DBManager.getInstance(getActivity());
                    //SynchronousData syn = new SynchronousData(getActivity(),dm.database(),curCompany, curSls);
                    //syn.doSync();

                    //start request master to server
                    NgantriInformation ngantri = new NgantriInformation(getApplicationContext());
                    ngantri.key = NgantriInformation.KEY_MASTER_DATA;

                    try{
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("command", "sync_master");
                        jsonParam.put("salesman_id", currentSalesman);
                        jsonParam.put("company_id", curCompany);
                        jsonParam.put("last_modified", "");

                        ngantri.data = jsonParam.toString();
                        ngantri.value = curCompany+"_"+currentSalesman;
                        ngantri.description = "sending master data " +curCompany+"_"+currentSalesman;
                        ngantri.addAntrian();
                    }catch(JSONException x){}
                    catch(Exception x){}

                    //end request master to server


                //start request callplan to server

                    ngantri = new NgantriInformation (getApplicationContext());
                    ngantri.key = NgantriInformation.KEY_CALL_PLAN;
                    try{
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("salesman_id", currentSalesman);
                        jsonParam.put("company_id", curCompany);
                        jsonParam.put("command", "sync_call_plan");

                        jsonParam.put("last_modified", "");
                        ngantri.data = jsonParam.toString();
                        ngantri.value = curCompany+"_"+currentSalesman;
                        ngantri.description = "sending call plan " +curCompany+"_"+currentSalesman;
                        ngantri.addAntrian();
                    }catch(JSONException x){}
                    catch(Exception x){}

                    //end request callplan to server

                    dialog.dismiss();

                    //hold by antrian
                    //DBManager dm = DBManager.getInstance(MainActivity.this);
                    //SynchronousData syn = new SynchronousData(MainActivity.this,dm.database(),curCompany, currentSalesman);
                    //syn.doSync();

                }
            });

            builder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create().show();


        }else{
            Config.loadAppPrivacyConfig(getApplicationContext());
            //bootstrap(); //5-Mei-2017 Dimatiin GPS Command Tracking
        }

        //Order.docking(DBManager.getInstance(this).database(), "123", Helper.getCurrentDate(), currentSalesman);

    }

    private void setSchedule(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);
        Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void loadSettings(){

        TextView label = (TextView) headerNav.findViewById(R.id.tInfo);
        TextView label2 = (TextView) headerNav.findViewById(R.id.tInfo2);

        Settings s = new Settings(this);
        s.loadInfo();

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();

        e.putString("CUR_BARCODE_NUMBER", s.barcode_number);
        e.putInt("CUR_SLS_TYPE", s.salesman_type);
        e.putString("CUR_WORK_START", s.workStart);
        e.putString("CUR_WORK_END", s.workEnd);
        e.putString("CUR_MULTI_DIST", s.multi_dist);
        e.putString("CUR_START_DATE", s.start_date);
        e.putString("CUR_END_DATE", s.end_date);
        e.putInt("CUR_YEAR", s.year);
        e.putInt("CUR_PERIOD", s.period);

        if (s.week!=null) {
            if (s.week.length() > 0) {
                e.putString("CUR_WEEK", s.week);
                label2.setVisibility(View.VISIBLE);
                label.setText(currentSalesmanName + "-"+ currentSalesman);
                label2.setText("Week : " + s.week);
            }
        }else{
            label.setText(currentSalesmanName + "-"+ currentSalesman);
        }

        e.commit();
    }

    @Override
    public void onBackPressed(){

        if(!mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.openDrawer(GravityCompat.START);
        }else {
            AlertDialog.Builder builder = new Builder(MainActivity.this);
            builder.setMessage("Are you sure to exit app ?");
            builder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));


            builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            });

            builder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create().show();
        }
    }

    private void test(){
        DBManager dm =  DBManager.getInstance(getApplicationContext());        //dm.database().delete("sls_plan_status",null,null);
        //dm.database().delete("settings",null,null);

        Cursor cur = dm.database().rawQuery("SELECT * from sls_plan_status",null);
        if (cur.moveToFirst()) {
            do{
                //Log.e("whoa!!",cur.getString(cur.getColumnIndex("status")));
                //Log.e("whoa!!",cur.getString(cur.getColumnIndex("start_time")));
                ///Log.e("whoa!!",cur.getString(cur .getColumnIndex("start_time")));
            }while (cur.moveToNext());

        }
        cur.close();

    }

    private void bootstrap(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        startService(new Intent(this, ScheduleProcess.class));
        Helper.notifyQueue(getApplicationContext());

        // hold by antrian
        //new AsyncKSNIinit().execute(BASE_URL, currentSalesman);

        //start request callplan to server
        /*
        NgantriInformation ngantri = new NgantriInformation(getApplicationContext());
        ngantri.key = NgantriInformation.KEY_CALL_PLAN;
        try{
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("salesman_id", currentSalesman);
            jsonParam.put("company_id", curCompany);
            jsonParam.put("command", "sync_call_plan");
            jsonParam.put("last_modified", "");
            ngantri.data = jsonParam.toString();
            ngantri.value = curCompany+"_"+currentSalesman;
            ngantri.description = "sending call plan " +curCompany+"_"+currentSalesman;
            ngantri.addAntrian();
        }catch(JSONException x){}
         catch(Exception x){}

        //end request callplan to server
        */
    }

    private void  init(){
        // unlock for locked pending
        //Order.unlockAllOrder(getApplicationContext());

        //currentSalesman = "3000001";
        //currentSalesmanName = "REDI SAGARA";

        //currentSalesman = "3000002";
        //currentSalesmanName = "TOMI GUNAWAN";

        //curCompany = getResources().getString(R.string.company);

        DBManager dm = DBManager.getInstance(getApplicationContext());
        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();

        curUserName = session.getString("CUR_USER","");
        currentSalesman = session.getString("CUR_SLS","");
        currentSalesmanName = session.getString("CUR_SLS_NAME","");
        curCompany = session.getString("CUR_COMPANY","");
        //curCompany = getResources().getString(R.string.company);

        e.putString("CUR_VISIT", "");
        e.putString("CUR_WEEK", "");
        e.putString("CUR_PAUSE", "");
        e.putString("CUR_VISIT_DATE", "");
        e.putString("CUR_VISIT_NOTES", "");
        e.putString("CUR_VISIT_NAME", "");
        e.putString("CUR_WORK_START", "");
        e.putString("CUR_WORK_END", "");
        e.putLong("CUR_TRANSACTION", -1);
        e.putString("IMEI", Helper.getIMEI(getApplicationContext()));
        e.commit();

        loadSettings();
        CallPlanInit.loadPlan(getApplicationContext());
        loadSKU();
        //checkVersion();

        dataInfo = InfoPromo.isShow(getApplicationContext());

        if (dataInfo!=null) {
            Intent i = new Intent(MainActivity.this, Information.class);
            i.putExtra("info", dataInfo);
            startActivity(i);
        }

    }

    private void balik(){
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage("Are you sure to Logout ?");
        builder.setTitle(Helper.getStrResource(this,R.string.logout_msg_confirm));

        builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                stopService(new Intent(MainActivity.this, EnerlifeWebRequest.class));
                stopService(new Intent(MainActivity.this, ScheduleProcess.class));
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        });

        builder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        MenuItem xmenu = menuItem;

        switch(menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu0: // attendance
                if (fAttendance==null)
                    fragment = new Attendance();
                else
                    fragment = fAttendance;
                break;
            case R.id.menu1:// call plan
                if(Settings.requiredStart(this)){
                    Settings.restart(this);
                    if (fAttendance==null)
                        fragment = new Attendance();
                    else
                        fragment = fAttendance;

                    xmenu = nvDrawer.getMenu().getItem(0);
                }else{
                    CallPlanInit.loadPlan(this);

                    if (fCallPlan==null)
                        fragment = new CallPlan();
                    else
                        fragment = fCallPlan;
                }
                break;
            case R.id.menu2:// product
                if (fProductData==null)
                    //fragment = new ProductData();
                    fragment = new ProductBrandData();
                else
                    fragment = fProductData;
                break;
            case R.id.menu3: // customer
                if (fCustomer==null)
                    fragment = new Customer();
                else
                    fragment = fCustomer;
                break;
            case R.id.menu4: // target
                if (fTarget==null)
                    fragment = new Target();
                else
                    fragment = fTarget;
                break;
            case R.id.menu5: //competitor
                /*if (fCompetitor==null)
                    fragment = new Competitor();
                else00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000rnt                                                                                                                                                                                                                                                                                             0
                    fragment = fCompetitor;
                    */
                break;
            case R.id.menu6:
                if (fViewOrderActivity==null)
                    fragment = new ViewOrderActivity();
                else
                    fragment = fViewOrderActivity;
                break;
            case R.id.menu7:
                if (fAsynActivity==null)
                    fragment = new AsynActivity();
                else
                    fragment = fAsynActivity;
                break;
            case R.id.menu8:
                if (fSummary==null)
                    fragment = new SummaryActivity();
                else
                    fragment = fSummary;
                break;
            case R.id.menu10:
                if (fSettings==null)
                    fragment = new AppConfig();
                else
                    fragment = fSettings;
                //checkVersion();
                break;
            case R.id.menu9:
                AlertDialog.Builder builder = new Builder(MainActivity.this);
                builder.setMessage(Helper.getStrResource(this,R.string.logout_msg_confirm));
                builder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));


                builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Settings.logout(MainActivity.this);
                        doLogout();
                    }
                });

                builder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
                break;

            case R.id.menu11: //inventory

                if (fLoadingUnloading==null)
                    fragment = new ProductStock();
                else
                    fragment = fLoadingUnloading;
                break;


        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }

        xmenu.setChecked(true);
        setTitle(xmenu.getTitle());
        //setTitle("");
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu1:
                break;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // asyn
    public class AsyncKSNIinit extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Integer doInBackground(String... params) {


            InputStream inputStream = null;
            Integer result = 0;
            HttpURLConnection urlConnection = null;
            if (Helper.isOnline(getApplicationContext())) {
                try {
                    URL url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setUseCaches(false);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setConnectTimeout(10 * 1000);
                    urlConnection.setReadTimeout(10 * 1000);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("charset", "utf-8");
                    urlConnection.setRequestMethod("POST");


                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    JSONObject jsonParam = new JSONObject();
                    JSONObject jsonReady = new JSONObject();

                    //request callplan to server

                    jsonParam.put("salesman_id", params[1]);
                    jsonParam.put("company_id", curCompany);
                    jsonParam.put("command", "sync_call_plan");
                    jsonParam.put("last_modified", "");

                    wr.writeBytes("data=" + jsonParam.toString());
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
                        if (parseResult(response.toString())) result= 1;


                    } else {
                        result = 0;
                    }


                }
                catch (MalformedURLException e) {result = 0;}
                catch (SocketException ex) {result = 0;}
                catch (SocketTimeoutException ex) {result = 0;}
                catch (ProtocolException e) {result = 0;}
                catch (IOException e) {result = 0;}
                catch (JSONException e) {result = 0;}
                catch (Exception e) {result = 0;}

                finally
                {
                    urlConnection.disconnect();
                    urlConnection = null;
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (result == 1) {

                dataInfo = InfoPromo.isShow(getApplicationContext());

                if (dataInfo!=null) {
                    Intent i = new Intent(MainActivity.this, Information.class);
                    i.putExtra("info", dataInfo);
                    startActivity(i);
                }

                loadSettings();
            } else {
                // default kan ke local call plan database
                CallPlanInit.loadPlan(getApplicationContext());

                Helper.showToast(getApplicationContext(), "Error while retrieving data.");

                //Toast.makeText(MainActivity.this,"Error while retrieving data from server",Toast.LENGTH_LONG).show();
            }



        }
    }

    private void testPic(){

        String path = Environment.getExternalStorageDirectory()+"/signature.txt";
        try {
            FileOutputStream outStream = new FileOutputStream(path);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));
            String h=Helper.getEncodeImage(Environment.getExternalStorageDirectory()+"/signature.png");
            JSONObject j = new JSONObject();
            j.put("JS",h);
            bw.write(j.toString());
            bw.close();
            outStream.close();
            }

    catch(IOException ex){
        Toast.makeText(MainActivity.this,"Error saat write data", Toast.LENGTH_LONG).show();
    }
    catch(Exception ex){
        Toast.makeText(MainActivity.this,"Error saat write data", Toast.LENGTH_LONG).show();
    }

    }

    private boolean parseResult(String result) {
        boolean buff = false;
        try {
            JSONObject response     = new JSONObject(result);

            if (response.optString("command").equals("sync_call_plan")){
                JSONArray dCall         = response.optJSONArray("call_plans");
                JSONArray dTarget         = response.optJSONArray("targets");
                JSONArray dTemplate     = response.optJSONArray("last_calls");
                JSONArray dInformation     = response.optJSONArray("information");


                DBManager dm = DBManager.getInstance(this);
                //dataInfo = response.optString("information");
                dataWeek = response.optString("week_in_year");

                SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor e = session.edit();
                e.putString("CUR_WEEK",dataWeek);
                e.commit();


                //TextView label = (TextView) findViewById(R.id.tInfo);
                //label.setText(currentSalesmanName + " " + dataWeek );


                CallPlanInit.getCallPlanFromServer(getApplicationContext(), dm.database(), dCall);
                CallPlanInit.getTargetFromServer(dm.database(), dTarget);
                CallPlanInit.getSkuTemplateFromServer(dm.database(), dTemplate);
                CallPlanInit.getInformationFromServer(dm.database(), dInformation);


                // update last syn callplan
                String start_d = response.optString("start_date");
                String end_d = response.optString("end_date");
                int year = response.optInt("year");
                int period = response.optInt("period");
                Settings.initCallPlan(getApplicationContext(), currentSalesman, dataWeek, dataInfo, Helper.getCurrentDateTime(),start_d,end_d, year,period);

                CallPlanInit.loadPlan(getApplicationContext());

                if (response.optString("success").equals("true")) buff = true;
            }


        } catch (JSONException e) {
            Log.e("ngsales",this.getLocalClassName()+ " parseResult::JSON ERROR "+e.getMessage());
            buff= false;
        } catch (Exception e) {
            Log.e("ngsales",this.getLocalClassName()+ " parseResult::GLOBAL ERROR "+e.getMessage());
            buff= false;
        }

        return buff;

    }

    private void doLogout(){
        new  AsyncTask<String, Void, String>(){
            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();

            }

            @Override
            protected String doInBackground(String... params) {
                String result = "0";
                try {
                    java.net.URL url = new URL(BASE_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setUseCaches(false);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setConnectTimeout(MainActivity.HTTP_CONECTION_TIMEOUT * 1000);
                    urlConnection.setReadTimeout(MainActivity.HTTP_READ_TIMEOUT * 1000);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("charset", "utf-8");
                    urlConnection.setRequestMethod("POST");

                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                    JSONObject jsonReady = new JSONObject();
                    jsonReady.put("command","logout");
                    jsonReady.put("salesman_id",currentSalesman);
                    jsonReady.put("company_id", curCompany);
                    jsonReady.put("username", curUserName);
                    jsonReady.put("time", Helper.getCurrentDateTime());

                    wr.writeBytes("data=" + jsonReady.toString());
                    Log.e("LOGOUT",jsonReady.toString());

                    wr.flush();
                    wr.close();
                    int statusCode = urlConnection.getResponseCode();
                    Log.e("Status",String.valueOf(statusCode));

                    if (statusCode == 200) {

                        BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                    }

                }

                catch (MalformedURLException e) {result = "99";}
                catch (SocketException ex) {result = "99";}
                catch (SocketTimeoutException ex) {result = "99";}
                catch (ProtocolException e) {result = "99";}
                catch (IOException e) {result = "99";}
                catch (JSONException e) {result = "99";}
                catch (Exception e) {result = "99";}
                return result;
            }

            @Override
            protected void onPostExecute(String result) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                stopService(new Intent(MainActivity.this, EnerlifeWebRequest.class));
                stopService(new Intent(MainActivity.this, ScheduleProcess.class));

                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }

        }.execute();
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadReference == referenceId){
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadReference),
                        "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installIntent);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("main activity result","");
    }

}
