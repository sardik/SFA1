package com.ksni.roots.ngsales.domain;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;
import java.io.File;
import java.util.List;

/**
 * Created by #roots on 07/08/2015.
 */
public class PreCall extends AppCompatActivity {
    private long idcur = -1;
    private boolean regularOrder;
    String company,customerNotes,customerNumber,customerName,zone,channel,customer_group,curWeek,curDate,saleman;;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private Uri imageToUploadUri = null;
    private final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final int REQUEST_SCAN = 0;
    private static final int REQUEST_EXIT = 1;

    @Override
    protected  void onResume(){
        super.onResume();
        Button btn =(Button)findViewById(R.id.btnCall);
        Button btnEnd =(Button)findViewById(R.id.btnEndCall);
        Button btnView =(Button)findViewById(R.id.btnViewOrder);
        Button btnPhoto = (Button)findViewById(R.id.btnPhoto);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageToUploadUri = null;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File file = Helper.getOutputMediaPhotoFileCall(customerNumber);
                    if (file.exists()) file.delete();
                    takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Uri fileUri = Uri.fromFile(file);
                    imageToUploadUri = fileUri;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(takePictureIntent, 9);
                }

            }
        });

        SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
        String cur = session.getString("CUR_VISIT", "");
        String forceEndCall = session.getString("FORCE_END_CALL", "0");
        String notes = session.getString("CUR_VISIT_NOTES", "");
        TextView tNotes = (TextView)findViewById(R.id.tNotes);
        tNotes.setText(notes);

        if (cur!=""){
            btn.setVisibility(View.GONE);
            btnEnd.setVisibility(View.VISIBLE);
            btnView.setVisibility(View.VISIBLE);
        }
        else{
            btn.setVisibility(View.VISIBLE);
            btnEnd.setVisibility(View.GONE);
            btnView.setVisibility(View.GONE);
        }

        if (forceEndCall.equals("1")) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_pre_call);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        TextView tNotes = (TextView)findViewById(R.id.tNotes);
        tNotes.setText(customerNotes);


        TextView tInfo = (TextView)findViewById(R.id.tInfoCall);

        Button btn =(Button)findViewById(R.id.btnCall);
        Button btnEnd =(Button)findViewById(R.id.btnEndCall);
        Button btnCompet =(Button)findViewById(R.id.btnCompetit);
        Button btnReturn =(Button)findViewById(R.id.btnReturOrder);
        Button btnView =(Button)findViewById(R.id.btnViewOrder);
        Button btnPause = (Button)findViewById(R.id.btnPause);

        // Update 1 November
        //btnPause.setEnabled(false);

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //read config allow skip
                boolean allow_skip_call = Config.getChecked(getApplicationContext(), "allow_skip_call");
                if (!allow_skip_call) {
                    Helper.msgbox(PreCall.this,Helper.getStrResource(PreCall.this,R.string.call_plan_pre_call_msg_restrict_skip),Helper.getStrResource(PreCall.this,R.string.common_msg_warning));
                }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(PreCall.this);
                builder.setMessage(Helper.getStrResource(PreCall.this,R.string.call_plan_pre_call_msg_confirm_skip));
                builder.setTitle(Helper.getStrResource(PreCall.this,R.string.common_msg_confirm));

                builder.setPositiveButton(Helper.getStrResource(PreCall.this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                        String cur = session.getString("CUR_VISIT", "");

                        for (int i = 0; i < CallPlan.adapter.getCount(); i++) {
                            CustomerCall cc = CallPlan.adapter.getItem(i);
                            if (cc.getCustomerNumber().equals(cur)) {
                                CallPlan.adapter.getItem(i).setStatus(CustomerCall.PAUSED);

                                String waktu_pause = Helper.getCurrentDateTime();
                                CallPlan.adapter.getItem(i).setLastPause(waktu_pause);


                                CallPlan.adapter.notifyDataSetChanged();
                                DBManager dm = DBManager.getInstance(getApplicationContext());
                                cc.setPause(dm.database(), waktu_pause);

                                //dm.close();

                                break;
                            }
                        }

                        SharedPreferences.Editor e = session.edit();
                        e.putString("CUR_VISIT", "");
                        e.putString("CUR_VISIT_NAME", "");
                        e.putString("CUR_PAUSE", "X");
                        e.commit();

                        dialog.dismiss();
                        finish();


                    }
                });

                builder.setNegativeButton(Helper.getStrResource(PreCall.this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();

            }
        }
        });

        SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
        customerNumber =  session.getString("CUR_VISIT","");
        company =  session.getString("CUR_COMPANY","");
        saleman=  session.getString("CUR_SLS","");
        customerName =  session.getString("CUR_VISIT_NAME","");
        channel =  session.getString("CUR_VISIT_CHANNEL","");
        curWeek =  session.getString("CUR_WEEK","");
        zone =  session.getString("CUR_VISIT_ZONE","");
        customer_group =  session.getString("CUR_VISIT_GROUP","");
        curDate =  session.getString("CUR_VISIT_DATE","");
        customerNotes =  session.getString("CUR_VISIT_NOTES","");

        ab.setTitle(customerName);

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        String cur = session.getString("CUR_VISIT","");
        if (cur!=""){
            tInfo.setText("To start view/add/edit order click green button ('Order Items') or to end call click red button ('End Call').");
            btn.setVisibility(View.GONE);
            btnEnd.setVisibility(View.VISIBLE);
            btnView.setVisibility(View.VISIBLE);
        }
        else{
            tInfo.setText("To start a visit click 'Call'; button below, the system will scan the QR code as the data required customer or options, for other reasons such as not exist or is damaged, these scans can be passed and if this rule has been in previous settings.");
            btn.setVisibility(View.VISIBLE);
            btnEnd.setVisibility(View.GONE);
            btnView.setVisibility(View.GONE);
        }


        btnCompet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreCall.this,Competitor.class));
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //long last_id = com.ksni.roots.ngsales.model.Order.getLastId(DBManager.getInstance(getApplicationContext()).database(),customerNumber);

                SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                long cur_trs = session.getLong("CUR_TRANSACTION", -1);
                long cur_ret = session.getLong("CUR_TRANSACTION_RETURN", -1);

                if (cur_trs==-1 && cur_ret==-1) {
                    Helper.msgbox(PreCall.this,Helper.getStrResource(PreCall.this,R.string.call_plan_pre_call_msg_restrict_blank_reason),Helper.getStrResource(PreCall.this,R.string.common_msg_error));
                }else {

                    //read config allow picture outlet
                    boolean capture_photo = Config.getChecked(getApplicationContext(), "capture_photo");
                    if (cur_trs>0 && capture_photo && !com.ksni.roots.ngsales.model.Order.getPicOutletByOrder(getApplicationContext(),cur_trs)){
                        Helper.msgbox(PreCall.this,Helper.getStrResource(PreCall.this,R.string.call_plan_pre_call_msg_restrict_no_picture),Helper.getStrResource(PreCall.this,R.string.common_msg_error));
                    }else {

                        // start check order

                        idcur = -1;
                        if (cur_trs > 0) {
                            regularOrder = true;
                            idcur = cur_trs;
                        } else {
                            if (cur_ret > 0) {
                                regularOrder = false;
                                idcur = cur_ret;
                            }
                        }

                        final AlertDialog.Builder builderX = new AlertDialog.Builder(PreCall.this);
                        LayoutInflater inflater = getLayoutInflater();
                        final View lay = inflater.inflate(R.layout.ui_order_view, null);

                        TextView tv = (TextView) lay.findViewById(R.id.list_empty);
                        tv.setText("No items.");

                        Toolbar t = (Toolbar) lay.findViewById(R.id.toolbar);
                        t.setVisibility(View.GONE);

                        FloatingActionsMenu fff = (FloatingActionsMenu) lay.findViewById(R.id.multiple_actions);
                        fff.setVisibility(View.GONE);

                        DBManager dm = DBManager.getInstance(PreCall.this);

                        final com.ksni.roots.ngsales.model.Order last_order = com.ksni.roots.ngsales.model.Order.getData(dm.database(), idcur, customerNumber, false);
                        final List<OrderItem> ords = last_order.getItems();

                        final OrderAdapter adapterX = new OrderAdapter(PreCall.this, R.layout.ui_order_item, ords, (TextView) lay.findViewById(R.id.tTotal));
                        final ListView lvx = (ListView) lay.findViewById(R.id.lstOrderItem);


                        lvx.setEmptyView(lay.findViewById(R.id.list_empty));
                        lvx.setStackFromBottom(false);
                        lvx.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                        lvx.setAdapter(adapterX);
                        TextView tEmpty = (TextView) lay.findViewById(R.id.list_empty);

                        if (cur_trs != -1 || cur_ret != -1) {
                            tEmpty.setVisibility(View.GONE);
                        } else {
                            tEmpty.setVisibility(View.VISIBLE);
                        }

                        builderX.setPositiveButton(Helper.getStrResource(PreCall.this,R.string.common_msg_yes), null);
                        builderX.setNegativeButton(Helper.getStrResource(PreCall.this,R.string.common_msg_no), null);

                        builderX.setView(lay);

                        final AlertDialog ad = builderX.create();
                        ad.setTitle(Helper.getStrResource(PreCall.this,R.string.common_msg_confirm));

                        ad.setCanceledOnTouchOutside(false);
                        ad.setCancelable(false);

                        ad.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {

                                Button b = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                                b.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (idcur > -1) {
                                            boolean capture_signature = Config.getChecked(getApplicationContext(), "capture_signature");
                                            if (capture_signature) {
                                                //add 29-apri-2016 leak
                                                ad.dismiss();
                                                Intent intent = new Intent(PreCall.this, CaptureSignature.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                startActivityForResult(intent, 1);
                                            } else {
                                                //add 29-apri-2016 leak
                                                ad.dismiss();
                                                endCall(false);
                                            }

                                        } else {
                                            ad.dismiss();
                                        }

                                    }
                                });


                                Button c = ad.getButton(AlertDialog.BUTTON_NEGATIVE);
                                c.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        ad.dismiss();
                                    }
                                });

                            }
                        });

                        ad.show();

                        // end check order

                        /*
                        AlertDialog.Builder builder = new AlertDialog.Builder(PreCall.this);
                        builder.setMessage("Are you sure to end call ?");
                        builder.setTitle("End Call Confirm");


                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //read config signature
                                boolean capture_signature = Config.getChecked(getApplicationContext(), "capture_signature");
                                if (capture_signature) {
                                    Intent intent = new Intent(PreCall.this, CaptureSignature.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivityForResult(intent, 1);
                                } else {
                                    endCall(false);
                                }

                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.create().show();
                                   */

                    }


                }
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor e = session.edit();
                e.putInt("CUR_ORDER_TYPE",0); // REGULAR ORDER
                e.commit();
                startActivity(new Intent(PreCall.this, Order.class));
            }
        });


        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //read config extract
                boolean allow_return_order = Config.getChecked(getApplicationContext(), "allow_return_order");
                if (!allow_return_order) {
                    Helper.msgbox(PreCall.this, Helper.getStrResource(PreCall.this,R.string.call_plan_pre_call_msg_restrict_return), Helper.getStrResource(PreCall.this,R.string.common_msg_error));
                }else {
                    SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                    SharedPreferences.Editor e = session.edit();
                    e.putInt("CUR_ORDER_TYPE", 1); // RETURN ORDER
                    e.commit();
                    startActivity(new Intent(PreCall.this, Order.class));
                }
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*
                SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor e = session.edit();
                e.putString("CUR_VISIT", customerNumber);
                e.putString("CUR_VISIT_NAME", customerName);
                e.commit();

                Intent intCall = new Intent(PreCall.this,Call.class);
                intCall.putExtra("CUSTOMER_NUMBER", customerNumber);
                intCall.putExtra("CUSTOMER_NAME", customerName);
                startActivity(intCall);
                */



                //finish();

                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {

                    }

        }
    });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
               super.onBackPressed();
              break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void endCall(boolean  captureSignature){
        SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
        String cur = session.getString("CUR_VISIT", "");
        final long cur_trs = session.getLong("CUR_TRANSACTION", -1);
        final long cur_ret = session.getLong("CUR_TRANSACTION_RETURN", -1);
        //
        SharedPreferences.Editor e = session.edit();
        e.putString("CUR_VISIT", "");
        e.putString("CUR_VISIT_NAME", "");
        e.commit();


        if (captureSignature) {
            String signature = Helper.getEncodeImage(Environment.getExternalStorageDirectory() + "/signature.png");

            //update signature and release lock
            if (cur_trs > 0) {
                com.ksni.roots.ngsales.model.Order.updateSignature(getApplicationContext(), cur_trs, signature);
            } else {
                if (cur_ret > 0)
                    com.ksni.roots.ngsales.model.Order.updateSignature(getApplicationContext(), cur_ret, signature);
            }

            File f = new File(Environment.getExternalStorageDirectory() + "/signature.png");
            if (f.exists()) f.delete();
        }

        for(int i=0;i<CallPlan.adapter.getCount();i++){
            CustomerCall cc = CallPlan.adapter.getItem(i);
            if (cc.getCustomerNumber().equals(cur)){
                CallPlan.adapter.getItem(i).setStatus(CustomerCall.VISITED);

                String waktu = Helper.getCurrentDateTime();
                CallPlan.adapter.getItem(i).setEndTime(waktu);

                CallPlan.adapter.notifyDataSetChanged();
                DBManager dm = DBManager.getInstance(getApplicationContext());

                if(cur_trs>0) cc.endCall(dm.database(), cur_trs, waktu);
                if(cur_ret>0) cc.endCall(dm.database(), cur_ret, waktu);
                if (cur_trs>0) com.ksni.roots.ngsales.model.Order.updateDuration(getApplicationContext(), cur_trs, cc.getDuration());
                if (cur_ret>0) com.ksni.roots.ngsales.model.Order.updateDuration(getApplicationContext(), cur_ret, cc.getDuration());

                break;
            }
        }
        // MainActivity.dataOrder.clear();
        // Kirim langsung ke sever for current transaction
        if (cur_trs>0 || cur_ret>0) {
            final DBManager dm = DBManager.getInstance(getApplicationContext());

            Log.e("cur_trs",String.valueOf(cur_trs));
            if(cur_trs>0) { // regular
                Synchronous kirim = new Synchronous(PreCall.this, dm.database(), company, saleman);
                String result = kirim.postData(cur_trs, "adaTTD");
            }

            if(cur_ret>0) { // retur
                Synchronous kirim2 = new Synchronous(PreCall.this, dm.database(), company, saleman);
                String result2 = kirim2.postData(cur_ret, "adaTTD");
            }

            Helper.notifyQueue(getApplicationContext());

            finish();
        } else {
            finish(); // no data finish
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");


                SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor e = session.edit();
                e.putString("CUR_VISIT", customerNumber);
                e.putString("CUR_VISIT_NAME", customerName);
                e.commit();


                for(int i=0;i<CallPlan.adapter.getCount();i++){
                    CustomerCall cc = CallPlan.adapter.getItem(i);
                    if (cc.getCustomerNumber().equals(customerNumber)){
                        CallPlan.adapter.getItem(i).setStatus(CustomerCall.VISIT);
                        CallPlan.adapter.notifyDataSetChanged();
                        break;
                    }
                }

                Intent intCall = new Intent(PreCall.this,Call.class);
                startActivity(intCall);
                finish();

            }
        }else if(requestCode==1 && resultCode == 1){
            endCall(true);
        }else if(requestCode==3 && resultCode == 1){
            finish();

        } else if (requestCode == 9) {
            if (resultCode == RESULT_OK) {
                if (imageToUploadUri!=null) {
                    Helper.resizePhoto(imageToUploadUri.getPath(),800,600);
                    Helper.rotateImage(imageToUploadUri.getPath(), 90);

                    SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
                    long cur_trs = session.getLong("CUR_TRANSACTION", -1);
                    long cur_ret = session.getLong("CUR_TRANSACTION_RETURN", -1);
                    if (cur_trs!=-1) {
                            String picOut = Helper.getEncodeImage(imageToUploadUri.getPath());
                            com.ksni.roots.ngsales.model.Order.updatePicOutlet(getApplicationContext(), cur_trs, picOut);
                    } else {
                        if (cur_ret!=-1) {
                            String picOut = Helper.getEncodeImage(imageToUploadUri.getPath());
                            com.ksni.roots.ngsales.model.Order.updatePicOutlet(getApplicationContext(), cur_ret, picOut);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pre_call, menu);
        return true;
    }



}
