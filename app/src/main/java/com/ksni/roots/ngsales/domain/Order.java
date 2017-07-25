package com.ksni.roots.ngsales.domain;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.model.Customer;
import com.ksni.roots.ngsales.util.Collection;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Order extends AppCompatActivity {
    private String orderNotes = "";
    private static final int REQUEST_ADD_ITEM           =   0;
    private static final int REQUEST_PHOTO              =   1;
    private static final int REQUEST_PHOTO_OUTLET       =   2;
    private String company,customerNumber,channel,zone,customer_group,salesman;
    private int salesType;
    private boolean exec300 = true;
    private int orderType;
    private boolean allow_invoice_cash_discount;
    private boolean use_alias_product_name_order = false;
    private boolean multidist;
    private boolean regularOrder;
    //private boolean india;
    //private double indiaValue = 0;
    private long currentId = -1;
    private OrderAdapter adapter;
    private Uri imageToUploadUri = null;
    private Uri imageToUploadUriOutlet = null;
    private ListView lv;
    private AlertDialog dlgReason;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private static boolean hasChange = false;
    private EditText searchItem;
    private List<OrderItem> listOrderItem = new ArrayList<OrderItem>();
    private List<OrderItem> listOrderItemSaved = new ArrayList<OrderItem>();



    private double getHarga(double total,OrderItem itm){
        DBManager dm=DBManager.getInstance(getApplicationContext());
        com.ksni.roots.ngsales.domain.Pricing prc = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, total, Helper.getCurrentDate(), getApplicationContext());
        return prc.getPrice();
    }

    private double getTotal(List<ItemPrice> listsPrice){
        double total = 0;
        for(int i=0;i<listsPrice.size();i++){
            ItemPrice iprice = listsPrice.get(i);
            total+=(iprice.getPrice() * iprice.getQty()) ;
        }
        return total;
    }

    private void xcheck300(){
        //exec300 = false;
    }

    private void check300(){
        if (exec300) {

            List<ItemPrice> listsPrice = new ArrayList<ItemPrice>();
            for (int i = 0; i < adapter.getCount(); i++) {
                OrderItem itm = adapter.getItem(i);
                if (itm.itemType.equals("N")) {
                    ItemPrice prc = new ItemPrice(itm.qty, 0, itm);
                    listsPrice.add(prc);
                }
            }


            for (int i = 0; i < listsPrice.size(); i++) {
                listsPrice.get(i).setPrice(getHarga(getTotal(listsPrice), listsPrice.get(i).getOrderItem()));
                for (int j = 0; j <= i; j++) {
                    listsPrice.get(j).setPrice(getHarga(getTotal(listsPrice), listsPrice.get(j).getOrderItem()));
                }
            }


            //reset first
            DBManager dm = DBManager.getInstance(getApplicationContext());
                for (int i = 0; i < adapter.getCount(); i++) {
                    OrderItem itm = adapter.getItem(i);
                    if (itm.itemType.equals("N")) {
                        adapter.getItem(i).regularDiscount = 0;
                        adapter.getItem(i).extraDiscount = 0;
                        adapter.getItem(i).specialDiscount = 0;

                        if(listsPrice.get(i).getOrderItem().id==adapter.getItem(i).id) {
                            adapter.getItem(i).price = listsPrice.get(i).getPrice();
                        }

                        /*com.ksni.roots.ngsales.domain.Pricing prc = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, totalCond, Helper.getCurrentDate(), getApplicationContext());
                        double price = prc.getPrice();
                        adapter.getItem(i).price = price;
                        totalCond += (price * itm.qty);


                        for(int j=0;j<i;j++){
                            OrderItem itmBefore = adapter.getItem(j);
                            com.ksni.roots.ngsales.domain.Pricing prcBefore = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itmBefore.division, itmBefore.productId, itmBefore.uom, itmBefore.qty, totalCond, Helper.getCurrentDate(), getApplicationContext());
                            double priceBefore = prc.getPrice();
                            adapter.getItem(j).price = priceBefore;
                        }
                        */

                        adapter.notifyDataSetChanged();
                    }
                }

            double totGros = 0;
            for (int i =0;i<adapter.getCount();i++) {
                OrderItem itm = adapter.getItem(i);
                if (itm.itemType.equals("N")) {
                    totGros += (itm.price * itm.qty);
                }
            }


                for (int i = 0; i < adapter.getCount(); i++) {
                    OrderItem itm = adapter.getItem(i);
                    if(itm.itemType.equals("N")) {
                        com.ksni.roots.ngsales.domain.Pricing prc = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, totGros, Helper.getCurrentDate(), getApplicationContext());
                        if (multidist){
                            adapter.getItem(i).regularDiscount = 0;

                        }
                        else {
                            Spinner tTop =(Spinner)findViewById(R.id.tTop);
                            if((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals("0")) || (!allow_invoice_cash_discount) ){
                                adapter.getItem(i).regularDiscount = prc.getDiscountReg();
                            }
                            else
                                adapter.getItem(i).regularDiscount = 0;
                        }


                        adapter.getItem(i).extraDiscount = prc.getDiscountExt();
                        adapter.getItem(i).specialDiscount = prc.getDiscountSpec();
                        adapter.notifyDataSetChanged();
                    }
                }

                updateIPT();
                adapter.notifyDataSetChanged();

        }
        exec300 = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (Settings.requiredStart(getApplicationContext())){
            Settings.restart(getApplicationContext());
            restart();
        }

    }

    private void restart(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void loadFAB(){

        final View actionB = findViewById(R.id.action_promo);

        FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        final FloatingActionButton actionNotes = (FloatingActionButton) findViewById(R.id.action_notes);
        actionNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FloatingActionsMenu) findViewById(R.id.multiple_actions)).collapse();
                final EditText txtOdometer = new EditText(Order.this);
                txtOdometer.setText(orderNotes);
                //txtOdometer.setHint("Notes here...");
                new AlertDialog.Builder(Order.this)
                        .setTitle(Helper.getStrResource(Order.this,R.string.transaction_order_input_text_dialog)  )
                        //.setMessage("Please input notes for this order.")
                        .setView(txtOdometer)
                        .setPositiveButton(Helper.getStrResource(Order.this,R.string.common_msg_save), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                orderNotes = txtOdometer.getText().toString();
                            }
                        })
                        .setNegativeButton(Helper.getStrResource(Order.this,R.string.common_msg_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

        final FloatingActionButton actionPrice = (FloatingActionButton) findViewById(R.id.action_pricing);
        actionPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FloatingActionsMenu) findViewById(R.id.multiple_actions)).collapse();
                Intent i = new Intent(Order.this,ProgramInfoActivity.class);
                i.putExtra("program", "pricing");
                //if (india)
                //    i.putExtra("p1", false);
                //else
                 //   i.putExtra("p1", true);
                startActivity(i);
            }
        });


        final FloatingActionButton actionPromo = (FloatingActionButton) findViewById(R.id.action_promo);
        actionPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FloatingActionsMenu) findViewById(R.id.multiple_actions)).collapse();
                Intent i = new Intent(Order.this,ProgramInfoActivity.class);
                i.putExtra("program","free");
                startActivity(i);
            }
        });


        final FloatingActionButton actionPhoto = (FloatingActionButton) findViewById(R.id.action_photo);
        actionPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FloatingActionsMenu) findViewById(R.id.multiple_actions)).collapse();
                imageToUploadUriOutlet = null;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File file = Helper.getOutputMediaPhotoFileCall(customerNumber);
                    if (file.exists()) file.delete();
                    takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Uri fileUri = Uri.fromFile(file);
                    imageToUploadUriOutlet = fileUri;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(takePictureIntent, REQUEST_PHOTO_OUTLET);
                }
            }
        });


        //menuMultipleActions.addButton(actionNotes);
        //menuMultipleActions.addButton(actionC);


    }

    private boolean isExistNullReason(){
        boolean ada=false;

        for(int i=0;i<adapter.getCount();i++){
            if(adapter.getItem(i).reasonReturId.equals("0") && adapter.getItem(i).qty > 0) {
                ada = true;
                break;
            }
        }

        return ada;
    }

    @Override
    public void onBackPressed(){
        if ( (hasChange && adapter.getCount()>0 && (adapter.isList() || adapter.isStock()) )){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Helper.getStrResource(Order.this,R.string.transaction_order_confirm_containing_item_save));
            builder.setTitle(Helper.getStrResource(Order.this,R.string.common_msg_confirm));

            builder.setPositiveButton(Helper.getStrResource(Order.this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    postOrder();
                    //save("");
                    //listOrderItem.clear();
                    //finish();

                }
            });

            builder.setNegativeButton(Helper.getStrResource(Order.this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });

            builder.create().show();
        }
        else {
            super.onBackPressed();
        }

    }

    private void populateTemplate(String id){
        DBManager dm = DBManager.getInstance(getApplicationContext());
        SQLiteDatabase db = dm.database();

//        MainActivity.dataOrder.clear();


        MainActivity.dataOrder.clear();
        if (currentId==-1){
            if (regularOrder) {
                List<OrderItem> ords = CustomerCall.getTemplate(dm.database(), customerNumber);
                int cnt = 0;
                MainActivity.dataOrder.clear();
                List<Product> produks = Product.getTemplateNPLFocus(db);
                for (Product produk : produks) {
                    boolean ada = false;
                    for (OrderItem ox : ords) {
                        if (produk.getProcutId().equals(ox.productId)) {
                            ada = true;
                            break;
                        }
                    }
                    if (!ada) {
                        OrderItem itm = new OrderItem();
                        itm.uom = produk.getUom();
                        itm.qty = 0;
                        itm.lastQty = 0;
                        itm.lastUom = itm.uom;
                        itm.price = 0;
                        itm.division = produk.getDivision();
                        itm.suggestQty = 0;
                        itm.suggestUom = itm.uom;
                        itm.stockQty = 0;

                        itm.uomLarge = produk.getUomLarge();
                        itm.uomMedium = produk.getUomMedium();
                        itm.uomSmall = produk.getUomSmall();

                        itm.largeToSmall = produk.getConversionLargeToSmall();
                        itm.mediumToSmall = produk.getConversionMediumToSmall();

                        itm.stockUom = itm.uom;
                        itm.brand = produk.getBrand();
                        itm.productId = produk.getProcutId();
                        if (use_alias_product_name_order) // add 25 feb 16
                            itm.productName = produk.getAlias();
                        else
                            itm.productName = produk.getProductName();


                        com.ksni.roots.ngsales.domain.Pricing prc = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, itm.price * itm.qty, Helper.getCurrentDate(),getApplicationContext());
                        itm.price = prc.getPrice();

                        Spinner tTop = (Spinner)findViewById(R.id.tTop);
                        if((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals(0)) || (!allow_invoice_cash_discount) )
                            itm.regularDiscount = prc.getDiscountReg();
                        else
                            itm.regularDiscount = 0;


                        itm.extraDiscount = prc.getDiscountExt();
                        itm.specialDiscount = prc.getDiscountSpec();
                        itm.itemType = "N";
                        cnt++;
                        itm.id = cnt;
                        itm.refItem = 0;
                        MainActivity.dataOrder.add(itm);
                    }
                }


                for (OrderItem itm : ords) {
                    com.ksni.roots.ngsales.domain.Pricing prc = new com.ksni.roots.ngsales.domain.Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, itm.price * itm.qty, Helper.getCurrentDate(),getApplicationContext());
                    itm.price = prc.getPrice();

                    Spinner tTop =(Spinner)findViewById(R.id.tTop);
                    if((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals("0")) || (!allow_invoice_cash_discount) )
                        itm.regularDiscount = prc.getDiscountReg();
                    else
                        itm.regularDiscount = 0;

                    itm.extraDiscount = prc.getDiscountExt();
                    itm.specialDiscount = prc.getDiscountSpec();
                    itm.itemType = "N";
                    cnt++;
                    itm.id = cnt;
                    itm.refItem = 0;
                    MainActivity.dataOrder.add(itm);
                }

                Spinner tTop = (Spinner)findViewById(R.id.tTop);
                Customer cust = Customer.getCustomer(Order.this,customerNumber);

                for(int i =0;i<tTop.getCount();i++){
                    if (tTop.getItemAtPosition(i).toString().equals(cust.getTop())){
                        tTop.setSelection(i);
                        break;
                    }
                }

            }

        }
        else{
            com.ksni.roots.ngsales.model.Order ord = com.ksni.roots.ngsales.model.Order.getData(dm.database(),currentId,customerNumber,false);
            if (ord!=null) {

                MainActivity.dataOrder.clear();
                MainActivity.dataOrder.addAll(ord.getItems());

                exec300 = false;
                Spinner tTop = (Spinner)findViewById(R.id.tTop);


                EditText tKirim =(EditText)findViewById(R.id.tKirim);
                tKirim.setText(ord.getDeliveryDate());

                CheckBox chk = (CheckBox)findViewById(R.id.chkDelivered);
                if (ord.getDelivered()==1)
                    chk.setChecked(true);
                else
                    chk.setChecked(false);


                for(int i =0;i<tTop.getCount();i++){
                    if (tTop.getItemAtPosition(i).toString().equals(ord.getTop())){
                        tTop.setSelection(i);
                        break;
                    }
                }


            }

        }

    }

    private void showDialogTanggal(){
        if (salesType==com.ksni.roots.ngsales.model.Order.SALES_TAKING_ORDER) {
            final EditText tKirim = (EditText) findViewById(R.id.tKirim);
            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    tKirim.setText(Helper.getStrDateFromDate(year, monthOfYear, dayOfMonth));

                }
            };

            Date dt = Helper.getDateByStr(tKirim.getText().toString());
            Calendar cdate = Calendar.getInstance();
            cdate.setTime(dt);

            int year = cdate.get(Calendar.YEAR);
            int month = cdate.get(Calendar.MONTH);
            int day = cdate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpDialog = new DatePickerDialog(this, listener, year, month, day);
            dpDialog.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);


        allow_invoice_cash_discount = Config.getChecked(getApplicationContext(), "allow_invoice_cash_discount");

        use_alias_product_name_order = Config.getChecked(getApplicationContext(), "use_alias_product_name_order");

        //String base_price_value = Config.getValue(getApplicationContext(), "base_price_value");
        //double base_value = Double.parseDouble(base_price_value);
        //indiaValue = base_value;
        //if (base_value>0)
        //     india = true;
        //else
        //    india = false;

        setContentView(R.layout.ui_order);

        TextView lKirim =(TextView)findViewById(R.id.lKirim);

        lKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTanggal();
            }
        });

        hasChange = false;

        loadFAB(); //Load FloadActionButton

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        customerNumber =  session.getString("CUR_VISIT","");
        channel =  session.getString("CUR_VISIT_CHANNEL","");
        zone = session.getString("CUR_VISIT_ZONE","");
        customer_group =session.getString("CUR_VISIT_GROUP","");
        company =  session.getString("CUR_COMPANY", "");
        salesman =  session.getString("CUR_SLS", "");

        salesType = session.getInt("CUR_SLS_TYPE", 0);
        orderType = session.getInt("CUR_ORDER_TYPE", 0);

        String mult = session.getString("CUR_MULTI_DIST", "0");


        Customer cust = Customer.getCustomer(Order.this,customerNumber);
        Date ayeuna = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(ayeuna);
        c.add(Calendar.DATE, cust.getDeliveryDay());
        Date dlvDate = c.getTime();

        final EditText tKirim =(EditText)findViewById(R.id.tKirim);
        if (salesType==com.ksni.roots.ngsales.model.Order.SALES_TAKING_ORDER)
            tKirim.setText(Helper.getStrDateFromDate(dlvDate));
        else
            tKirim.setText(Helper.getCurrentDate());

        if (mult.equals("1"))
                multidist = true;
            else
                multidist = false;

        if (orderType== com.ksni.roots.ngsales.model.Order.REGULAR_ORDER)
            regularOrder = true;
        else if (orderType== com.ksni.roots.ngsales.model.Order.RETURN_ORDER)
            regularOrder = false;


        if(regularOrder)
            currentId = session.getLong("TRANSACTION_SAVED_ORDER", -1);
        else
            currentId = session.getLong("TRANSACTION_SAVED_RETURN", -1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

//        CheckBox spnCash =(CheckBox)findViewById(R.id.chkCash);

        TextView lTop =(TextView)findViewById(R.id.lTop);
        Spinner tTop =(Spinner)findViewById(R.id.tTop);

        final List<OutletTop> tops = OutletTop.getData(DBManager.getInstance(getApplicationContext()).database());
        String[] arrayX = new String[tops.size()];
        for(int i=0;i<arrayX.length;i++){
            arrayX[i] = tops.get(i).top_id;
        }

        ArrayAdapter<String> dataAdapterTop = new ArrayAdapter<String>(Order.this, android.R.layout.simple_spinner_item, arrayX);
        tTop.setAdapter(dataAdapterTop);

        tTop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    check300();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if(allow_invoice_cash_discount && regularOrder){
            //spnCash.setVisibility(View.VISIBLE);
            lTop.setVisibility(View.VISIBLE);
            tTop.setVisibility(View.VISIBLE);

        }else{
            //spnCash.setVisibility(View.GONE);
            lTop.setVisibility(View.GONE);
            tTop.setVisibility(View.GONE);

        }


/*
        spnCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check300();
            }
        });

*/
        if (regularOrder) {
            if (salesType==com.ksni.roots.ngsales.model.Order.SALES_TAKING_ORDER)
                ab.setTitle(Helper.getStrResource(this,R.string.common_text_taking_order));
            else
                ab.setTitle(Helper.getStrResource(this,R.string.common_text_sales_order));
        }
        else {
            ab.setTitle(Helper.getStrResource(this,R.string.common_text_return_order));
        }



        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);
        listOrderItem.clear();

        populateTemplate("-1");

        listOrderItem.addAll(MainActivity.dataOrder);
        OrderItem item;
        lv = (ListView)findViewById(R.id.lstOrderItem);
        //lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        //lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        adapter = new OrderAdapter(this, R.layout.ui_order_item, listOrderItem,(TextView)findViewById(R.id.tTotal));
        //lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv.setEmptyView(findViewById(R.id.list_empty));
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
        lv.setTextFilterEnabled(true);


        lv.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final int pos = position;
                final OrderItem ord = adapter.getItem(pos);

                final AlertDialog.Builder builder = new AlertDialog.Builder(Order.this);
                LayoutInflater inflater = getLayoutInflater();
                final View lay = inflater.inflate(R.layout.ui_order_qyt, null);

                if (regularOrder){
                    ((TextView)lay.findViewById(R.id.lblOrder)).setText(Helper.getStrResource(Order.this,R.string.common_text_order));
                }else{
                    ((TextView)lay.findViewById(R.id.lblOrder)).setText(Helper.getStrResource(Order.this,R.string.common_text_return));
                }

                if (ord.itemType.equals("F")){
                    Helper.showToast(getApplicationContext(), Helper.getStrResource(Order.this,R.string.transaction_order_msg_restrict_edit_free_good));

                }
                else{

                       LinearLayout loStock = (LinearLayout)lay.findViewById(R.id.linearStock);
                       LinearLayout loReason = (LinearLayout)lay.findViewById(R.id.linearReason);
                       LinearLayout loLastCall = (LinearLayout)lay.findViewById(R.id.linearLastCall);
                       LinearLayout loSuggest = (LinearLayout)lay.findViewById(R.id.linearSuggestion);
                        if(!regularOrder) {
                            List<ReasonRetur> lsReason = ReasonRetur.getData(DBManager.getInstance(getApplicationContext()).database());
                            List<Collection> listReasonCollection = new ArrayList<Collection>();
                            listReasonCollection.add(new Collection(Helper.getStrResource(Order.this,R.string.common_text_select_hint),"0"));
                            for(ReasonRetur ret:lsReason){
                                listReasonCollection.add(new Collection(ret.getDescription(),ret.getReason()));
                            }

                            Spinner reasonRet = (Spinner) lay.findViewById(R.id.tReason);
                            ArrayAdapter<Collection> dataAdapterReason = new ArrayAdapter<Collection>(parent.getContext(), android.R.layout.simple_spinner_item, listReasonCollection);
                            reasonRet.setAdapter(dataAdapterReason);

                            for (int i = 0; i < listReasonCollection.size(); i++) {
                                Collection col = listReasonCollection.get(i);
                                String tag = String.valueOf(col.tag);
                                if (tag.equals(ord.reasonReturId)) {
                                    reasonRet.setSelection(i);
                                    break;
                                }
                            }

                            loReason.setVisibility(View.VISIBLE);
                            loLastCall.setVisibility(View.GONE);
                            loSuggest.setVisibility(View.GONE);
                            loStock.setVisibility(View.GONE);
                        }else{
                            loReason.setVisibility(View.GONE);
                            loLastCall.setVisibility(View.VISIBLE);
                            loSuggest.setVisibility(View.VISIBLE);
                            loStock.setVisibility(View.VISIBLE);

                        }

                List<String> list = new ArrayList<String>();
                if (ord.uomLarge != "") list.add(ord.uomLarge);
                if (ord.uomMedium != "") list.add(ord.uomMedium);
                if (ord.uomSmall != "") list.add(ord.uomSmall);



                List<String> listLast = new ArrayList<String>();
                listLast.add(ord.lastUom);

                Spinner spLast = (Spinner) lay.findViewById(R.id.tLastUom);
                ArrayAdapter<String> dataAdapterLast = new ArrayAdapter<String>(parent.getContext(), android.R.layout.simple_spinner_item, listLast);
                dataAdapterLast.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spLast.setAdapter(dataAdapterLast);
                spLast.setEnabled(false);

                List<String> listSuggest = new ArrayList<String>();
                listSuggest.addAll(list);
                Spinner spSugg = (Spinner) lay.findViewById(R.id.tSuggestUom);
                ArrayAdapter<String> dataAdapterSuggest = new ArrayAdapter<String>(parent.getContext(), android.R.layout.simple_spinner_item, listSuggest);
                dataAdapterSuggest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSugg.setAdapter(dataAdapterSuggest);

                for (int i = 0; i < listSuggest.size(); i++) {
                    if (listSuggest.get(i).equals(ord.suggestUom)) {
                        spSugg.setSelection(i);
                        break;
                    }
                }
                spSugg.setEnabled(false);

                Spinner spStock = (Spinner) lay.findViewById(R.id.tStockUom);
                ArrayAdapter<String> dataAdapterStk = new ArrayAdapter<String>(parent.getContext(), android.R.layout.simple_spinner_item, list);
                dataAdapterStk.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spStock.setAdapter(dataAdapterStk);

                Spinner spOrder = (Spinner) lay.findViewById(R.id.tOrderUom);
                ArrayAdapter<String> dataAdapterOrd = new ArrayAdapter<String>(parent.getContext(), android.R.layout.simple_spinner_item, list);
                dataAdapterOrd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spOrder.setAdapter(dataAdapterOrd);

                final EditText tSuggest = (EditText) lay.findViewById(R.id.tSuggest);
                tSuggest.setText(String.valueOf(ord.suggestQty));

                final EditText tLast = (EditText) lay.findViewById(R.id.tLast);
                tLast.setText(String.valueOf(ord.lastQty));

                final EditText tStock = (EditText) lay.findViewById(R.id.tStock);
                tStock.setText(String.valueOf(ord.stockQty));

                final EditText tOrd = (EditText) lay.findViewById(R.id.tOrder);
                tOrd.setText(String.valueOf(ord.qty));

                final TextView tHarga = (TextView) lay.findViewById(R.id.tHarga);
                tHarga.setText(Helper.getFormatCurrencyWithDigit(ord.price));


                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(ord.stockUom)) {
                        spStock.setSelection(i);
                        break;
                    }
                }

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(ord.uom)) {
                        spOrder.setSelection(i);
                        break;
                    }
                }
                tStock.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        EditText tSugg = (EditText) lay.findViewById(R.id.tSuggest);


                        int stk = 0, last = 0;

                        if (s.toString().equals(""))
                            stk = 0;
                        else
                            stk = Integer.parseInt(s.toString());

                        if (tLast.getText().toString().equals(""))
                            last = 0;
                        else
                            last = Integer.parseInt(tLast.getText().toString());


                        int sugg = last - stk;
                        if (sugg < 0) sugg = 0;
                        tSugg.setText(String.valueOf(sugg));

                    }
                });


                builder.setView(lay);
                final AlertDialog ad = builder.create();

                final EditText tQty = (EditText) lay.findViewById(R.id.tOrder);
                final Spinner tQtyUom = (Spinner) lay.findViewById(R.id.tOrderUom);

                final EditText tQtyStock = (EditText) lay.findViewById(R.id.tStock);
                final Spinner tQtyStockUom = (Spinner) lay.findViewById(R.id.tStockUom);

                final Spinner tReason = (Spinner) lay.findViewById(R.id.tReason);

                final EditText tQtyLast = (EditText) lay.findViewById(R.id.tLast);
                final Spinner tQtyLastUom = (Spinner) lay.findViewById(R.id.tLastUom);

                final EditText tQtySuggest = (EditText) lay.findViewById(R.id.tSuggest);
                final Spinner tQtySuggestUom = (Spinner) lay.findViewById(R.id.tSuggestUom);


                tQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {


                            boolean ada=false;
                            for(OrderItem oi:adapter.dataFilter) {
                                if (oi.id!=adapter.getItem(pos).id) {
                                    String listkey = oi.productId + oi.uom;
                                    String key = adapter.getItem(pos).productId + tQtyUom.getSelectedItem().toString();
                                    if (listkey.equals(key) && oi.itemType.equals("N")) {
                                        ada = true;
                                        break;
                                    }
                                }
                            }
                            if (ada){
                                Helper.showToast(getApplicationContext(), adapter.getItem(pos).productName + " "+tQtyUom.getSelectedItem().toString() + " "+Helper.getStrResource(Order.this,R.string.common_text_item_already_add));

                            }else {

                                adapter.getItem(pos).qty = Helper.toInt(tQty.getText().toString());
                                adapter.getItem(pos).uom = tQtyUom.getSelectedItem().toString();

                                DBManager dm = DBManager.getInstance(getApplicationContext());

                                // start get price
                                Product p = Product.getProductData(dm.database(), adapter.getItem(pos).productId);
                                Pricing prc = new Pricing(dm.database(), customerNumber, channel, zone, p.getDivision(), p.getProcutId(), adapter.getItem(pos).uom, adapter.getItem(pos).qty, 0, Helper.getCurrentDate(),getApplicationContext());
                                adapter.getItem(pos).price = prc.getPrice(); // p2

                                Spinner tTop =(Spinner)findViewById(R.id.tTop);
                                if((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals("0")) || (!allow_invoice_cash_discount) )
                                    adapter.getItem(pos).regularDiscount = prc.getDiscountReg();
                                else
                                    adapter.getItem(pos).regularDiscount = 0;


                                adapter.getItem(pos).extraDiscount = prc.getDiscountExt();
                                adapter.getItem(pos).specialDiscount = prc.getDiscountSpec();
                                // end get price

                                if (!regularOrder){
                                    Collection col = (Collection)tReason.getSelectedItem();
                                    adapter.getItem(pos).reasonReturId = String.valueOf(col.tag);
                                    adapter.getItem(pos).reasonReturName = col.string;
                                }

                                adapter.getItem(pos).stockQty = Helper.toInt(tStock.getText().toString());
                                adapter.getItem(pos).stockUom = tQtyStockUom.getSelectedItem().toString();

                                adapter.getItem(pos).lastQty = Helper.toInt(tLast.getText().toString());
                                adapter.getItem(pos).lastUom = tQtyLastUom.getSelectedItem().toString();

                                adapter.getItem(pos).suggestQty = Helper.toInt(tQtySuggest.getText().toString());
                                adapter.getItem(pos).suggestUom = tQtySuggestUom.getSelectedItem().toString();

                                String prod_id = adapter.getItem(pos).productId;
                                String uom = adapter.getItem(pos).uom;
                                int qty = Helper.toInt(tQty.getText().toString());
                                int id = adapter.getItem(pos).id;


                                adapter.loadTotal();


                                loadFreeGood(dm.database(),
                                        customerNumber,
                                        prod_id,
                                        qty,
                                        uom,
                                        id);
                                //dm.close();
                                hasChange = true;
                                //updateIPT();
                                adapter.notifyDataSetChanged();

                                check300();
                                ad.dismiss();
                            }

                        }
                        return false;
                    }
                });

                ad.setTitle(ord.productName + " - " + ord.productId);
                ad.setButton(AlertDialog.BUTTON_POSITIVE, Helper.getStrResource(Order.this,R.string.common_msg_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                boolean ada=false;
                                for(OrderItem oi:adapter.dataFilter) {
                                    if (oi.id!=adapter.getItem(pos).id) {
                                        String listkey = oi.productId + oi.uom;
                                        String key = adapter.getItem(pos).productId + tQtyUom.getSelectedItem().toString();
                                        if (listkey.equals(key) && oi.itemType.equals("N")) {
                                            ada = true;
                                            break;
                                        }
                                    }
                                }
                                if (ada){
                                    Helper.showToast(getApplicationContext(), adapter.getItem(pos).productName + " " + tQtyUom.getSelectedItem().toString() + " "+Helper.getStrResource(Order.this,R.string.common_text_item_already_add));
                                }else {


                                    adapter.getItem(pos).qty = Helper.toInt(tQty.getText().toString());
                                    adapter.getItem(pos).uom = tQtyUom.getSelectedItem().toString();

                                    DBManager dm = DBManager.getInstance(getApplicationContext());

                                    // start get price
                                    Product p = Product.getProductData(dm.database(), adapter.getItem(pos).productId);
                                    Pricing prc = new Pricing(dm.database(), customerNumber, channel, zone, p.getDivision(), p.getProcutId(), adapter.getItem(pos).uom, adapter.getItem(pos).qty, 0, Helper.getCurrentDate(),getApplicationContext());
                                    adapter.getItem(pos).price = prc.getPrice();


                                    Spinner tTop =(Spinner)findViewById(R.id.tTop);
                                    if((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals("0")) || (!allow_invoice_cash_discount) )
                                        adapter.getItem(pos).regularDiscount = prc.getDiscountReg();
                                    else
                                        adapter.getItem(pos).regularDiscount = 0;



                                    adapter.getItem(pos).extraDiscount = prc.getDiscountExt();
                                    adapter.getItem(pos).specialDiscount = prc.getDiscountSpec();
                                    // end get price

                                    if (!regularOrder){
                                        Collection col = (Collection)tReason.getSelectedItem();
                                        adapter.getItem(pos).reasonReturId = String.valueOf(col.tag);
                                        adapter.getItem(pos).reasonReturName = col.string;
                                    }

                                    adapter.getItem(pos).stockQty = Helper.toInt(tStock.getText().toString());
                                    adapter.getItem(pos).stockUom = tQtyStockUom.getSelectedItem().toString();

                                    adapter.getItem(pos).lastQty = Helper.toInt(tLast.getText().toString());
                                    adapter.getItem(pos).lastUom = tQtyLastUom.getSelectedItem().toString();

                                    adapter.getItem(pos).suggestQty = Helper.toInt(tQtySuggest.getText().toString());
                                    adapter.getItem(pos).suggestUom = tQtySuggestUom.getSelectedItem().toString();

                                    String prod_id = adapter.getItem(pos).productId;
                                    String uom = adapter.getItem(pos).uom;
                                    int qty = Helper.toInt(tQty.getText().toString());
                                    int id = adapter.getItem(pos).id;


                                    adapter.loadTotal();


                                    loadFreeGood(dm.database(),
                                            customerNumber,
                                            prod_id,
                                            qty,
                                            uom,
                                            id);
                                    //dm.close();
                                    hasChange = true;
                                    //updateIPT();
                                    adapter.notifyDataSetChanged();

                                    check300();
                                    ad.dismiss();
                                }
                            }
                        });
                ad.setButton(AlertDialog.BUTTON_NEGATIVE, Helper.getStrResource(Order.this,R.string.common_msg_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ad.dismiss();
                            }
                        });

                //tStock.setSelectAllOnFocus(true);

                if (tStock.getText().toString().equals("0")) tStock.setText("");
                if (tQty.getText().toString().equals("0")) tQty.setText("");
                ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                ad.show();

            }
        }
        });


/*        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });
        */
/*
        searchItem =  (EditText)findViewById(R.id.tSearchOrderItem);


        searchItem.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lstOrderItem) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            //menu.setHeaderTitle(xx.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final NumberPicker np;
      final   PopupWindow pw;
       final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
       final OrderItem ord;
        int menuItemIndex = item.getItemId();
        ord = adapter.getItem(info.position);
        if (menuItemIndex == 1000){
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.ui_order_qyt,
                    (ViewGroup) findViewById(R.id.popupqty));

            np= (NumberPicker)layout.findViewById(R.id.tQty);
            np.setWrapSelectorWheel(false);
            //np.getChildAt(0).setBackgroundResource(R.drawable.uprow);
            //np.getChildAt(2).setBackgroundResource(R.drawable.downrow);

            /*

            try{
                Field selectorWheelPaintField = np.getClass().getDeclaredField("tQty");
                selectorWheelPaintField.setAccessible(true);
                ((Paint)selectorWheelPaintField.get(np)).setColor(Color.GRAY);
            }
            catch(NoSuchFieldException e){

            }
            catch(IllegalAccessException e){

            }
            catch(IllegalArgumentException e){

            }*/



            np.setMaxValue(999);
            np.setMinValue(1);


            np.setValue(ord.qty);

            layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            pw = new PopupWindow(layout, 400,400, true);
            //pw.setAnimationStyle(android.R.anim.fade_in);

            Button btnok = (Button)layout.findViewById(R.id.btnOk);
            btnok.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // update QTY
                    ord.qty = np.getValue();


                    adapter.getItem(info.position).qty = ord.qty;
                    adapter.loadTotal();
                    adapter.notifyDataSetChanged();
                    pw.dismiss();
                }
            });


            Button btncancel = (Button)layout.findViewById(R.id.btnCancel);
            btncancel.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    pw.dismiss();
                }
            });



            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);


        }
        else if (menuItemIndex == 0){

            //read config allow delete free good
            boolean allow_delete_free_goods = Config.getChecked(getApplicationContext(), "allow_delete_free_goods");


            if (ord.itemType.equals("F") && !allow_delete_free_goods){
                Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_restrict_delete_free_good));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Order.this);
                builder.setMessage(Helper.getStrResource(this,R.string.common_msg_delete)+" " + ord.productName + "?");
                builder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));

                builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.showToast(getApplicationContext(), "'" + ord.productName + "' "+Helper.getStrResource(Order.this,R.string.common_msg_deleted));
                        //OrderAdapter buff = adapter;

                        int i = adapter.getCount();
                        for (int ii=0;ii<i;ii++) {
                            for (OrderItem o : adapter.dataFilter) {
                                if (o.refItem == ord.id) {
                                    adapter.remove(o);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                        hasChange = true;
                        adapter.remove(ord);

                        //updateIPT();
                        adapter.notifyDataSetChanged();
                        check300();
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
        else if (menuItemIndex == 2){
            Toast.makeText(getApplicationContext(),"test2",Toast.LENGTH_LONG).show();;

        }


        return true;
    }

    private void addItem(OrderItem item){
        //listOrderItem.add(item);
        //listOrderItem.add(item);
        adapter.add(item);
        adapter.notifyDataSetChanged();
        lv.setSelection(lv.getCount() - 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    private void updatePicOut(long trsid){
        // update picture outlet
        if (imageToUploadUriOutlet!=null) {
            try {
                Helper.resizePhoto(imageToUploadUriOutlet.getPath(), 200, 300);
                Bitmap bitmap = BitmapFactory.decodeFile(imageToUploadUriOutlet.getPath());
                if (bitmap != null) {
                    //Bitmap bitmapOut = Helper.rotateImage(bitmap, 90);
                    //String picOut = Helper.getEncodeImageOutlet(bitmapOut);


                    String picOut = Helper.getEncodeImageOutlet(bitmap);
                    com.ksni.roots.ngsales.model.Order.updatePicOutlet(getApplicationContext(), trsid, picOut);
                    Helper.deleteFile(imageToUploadUriOutlet.getPath());
                }
            }catch(Exception e){}

        }
    }

    private void updatePicOutReason(long trsid){
        // update picture outlet
        if (imageToUploadUri!=null) {
            try {
                Helper.resizePhoto(imageToUploadUri.getPath(), 200, 300);
                Bitmap bitmap = BitmapFactory.decodeFile(imageToUploadUri.getPath());
                if (bitmap != null) {
                    //Bitmap bitmapOut = Helper.rotateImage(bitmap, 90);
                    //String picOut = Helper.getEncodeImageOutlet(bitmapOut);

                    String picOut = Helper.getEncodeImageOutlet(bitmap);
                    com.ksni.roots.ngsales.model.Order.updatePicOutlet(getApplicationContext(), trsid, picOut);
                    Helper.deleteFile(imageToUploadUri.getPath());
                }
            }catch(Exception e){}

        }
    }

    private boolean saveOrder(String reason){

        try {
            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
            String curCust = session.getString("CUR_VISIT", "");
            String curSales = session.getString("CUR_SLS", "");
            String curWeek = session.getString("CUR_WEEK", "");
            String imei = session.getString("IMEI", "");
            String noreasbarcode = session.getString("CUR_REASON_NO_BARCODE", "");
            String noreasunroute = session.getString("CUR_REASON_NO_ROUTE", "");

            if (noreasunroute==null)  noreasunroute="";
            if (noreasbarcode==null)  noreasbarcode="";

            long curTrs = -1;
            if (regularOrder)
                curTrs = session.getLong("CUR_TRANSACTION", -1);
            else
                curTrs = session.getLong("CUR_TRANSACTION_RETURN", -1);

            String xlatitude = session.getString("LATITUDE", "0");
            String xlongitude = session.getString("LONGITUDE","0");

            DBManager dm = DBManager.getInstance(getApplicationContext());
            SQLiteDatabase db = dm.database();
            com.ksni.roots.ngsales.model.Order ord = new com.ksni.roots.ngsales.model.Order(db);

            ord.setLatitude(xlatitude);
            ord.setLongitude(xlongitude);

            Spinner tTop =(Spinner)findViewById(R.id.tTop);

            CheckBox chk = (CheckBox)findViewById(R.id.chkDelivered);
            if (chk.isChecked())
                ord.setDelivered(1);
            else
                ord.setDelivered(0);


            EditText tKirim =(EditText)findViewById(R.id.tKirim);
            ord.setDeliveryDate(tKirim.getText().toString());
            ord.setReasonNoBarcode(noreasbarcode);
            ord.setReasonNoRoute(noreasunroute);
            ord.setTop(tTop.getSelectedItem().toString());
            ord.setOrderDate(Helper.getCurrentDateTime());
            ord.setCustomer(curCust);
            ord.setNotes(orderNotes);
            ord.setSalesmanType(salesType);
            ord.setOrderType(orderType);
            ord.setIMEI(imei);
            ord.setWeek(curWeek);
            ord.setReason(reason);
            ord.setSales(curSales);

            for (OrderItem itm : listOrderItem) {
                //itm.id = adapter.getAutoId();
                ord.addItem(itm);
            }



            if (curTrs==-1){ //new
                long lastno = ord.save();

                // update last order number
                for(int i=0;i<CallPlan.adapter.getCount();i++){
                    CustomerCall cc = CallPlan.adapter.getItem(i);
                    if (cc.getCustomerNumber().equals(customerNumber)){
                        if(regularOrder)
                            CallPlan.adapter.getItem(i).setOrderId(lastno);
                        else
                            CallPlan.adapter.getItem(i).setReturnId(lastno);

                        CallPlan.adapter.notifyDataSetChanged();

                        if(regularOrder)
                            CallPlan.adapter.getItem(i).updateOrderId(dm.database(), lastno);
                        else
                            CallPlan.adapter.getItem(i).updateReturnId(dm.database(), lastno);

                        break;
                    }
                }

                SharedPreferences sessionX = getApplicationContext().getSharedPreferences("ngsales", 0);
                SharedPreferences.Editor e = sessionX.edit();

                if(regularOrder) {
                    e.putLong("CUR_TRANSACTION", lastno);
                    e.putLong("TRANSACTION_SAVED_ORDER", lastno);
                } else {
                    e.putLong("TRANSACTION_SAVED_RETURN", lastno);
                    e.putLong("CUR_TRANSACTION_RETURN", lastno);
                }
                e.commit();

                if (lastno!=-1) {
                    if (reason.length()>0) {
                            Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_save_success));
                    }
                    else {
                        // update pict
                        updatePicOut(lastno);
                        Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_save_success));
                    }
                    return true;
                }
                else {
                    Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_save_fail));

                    return false;
                }
            }else{ // update
                ord.setOrderId(curTrs);
                boolean updated = ord.update();
                if (updated) {
                    SharedPreferences sessionX = getApplicationContext().getSharedPreferences("ngsales", 0);
                    SharedPreferences.Editor e = sessionX.edit();

                    if(regularOrder) {
                        e.putLong("CUR_TRANSACTION", curTrs);
                    }
                    else {
                        e.putLong("CUR_TRANSACTION_RETURN", curTrs);
                    }
                    e.commit();

                    Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_update_success));
                }
                else
                    Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_update_fail));
                return updated;
            }
        }
        catch (Exception x){
        //    Log.e("XXX",x.toString());
            Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_fail));
            return false;
        }

    }

    private void save(String reason){
        hasChange = false;
        saveOrder(reason);
    }

    private void postOrder(){

        if (isExistNullReason() && !regularOrder){
            Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_reason_blank));
        }
        else if (!adapter.isList()){ //If No Item Order
            if(!regularOrder){
                Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.transaction_order_msg_no_items));
            }
            else {
                DBManager dm = DBManager.getInstance(getApplicationContext());
                final List<Reason> ls = Reason.getData(dm.database());
                final List<String> s = new ArrayList<String>();

                for (Reason r : ls) {
                    s.add(r.getReason() + " " + r.getDescription());
                }

                final CharSequence[] items = s.toArray(new CharSequence[s.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(Helper.getStrResource(this,R.string.transaction_order_title_reason_no_order));
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Reason r = (Reason) ls.get(item);

                        if (r.getReason().equals("02")) {
                            imageToUploadUri = null;
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                File file = Helper.getOutputMediaPhotoFile();
                                Uri fileUri = Uri.fromFile(file);
                                imageToUploadUri = fileUri;
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(takePictureIntent, REQUEST_PHOTO);
                                dlgReason.dismiss();
                            }
                        } else {
                            save(r.getReason());

                            SharedPreferences sessionX = getApplicationContext().getSharedPreferences("ngsales", 0);
                            long lastno = sessionX.getLong("CUR_TRANSACTION", -1);

                            // end call direct
                            SharedPreferences.Editor e = sessionX.edit();
                            e.putLong("CUR_TRANSACTION", -1);
                            e.putLong("CUR_TRANSACTION_RETURN", -1);
                            e.putString("FORCE_END_CALL", "1");
                            e.putString("CUR_VISIT", "");
                            e.putString("CUR_VISIT_NAME", "");
                            e.commit();


                            for (int i = 0; i < CallPlan.adapter.getCount(); i++) {
                                CustomerCall cc = CallPlan.adapter.getItem(i);
                                if (cc.getCustomerNumber().equals(customerNumber)) {
                                    CallPlan.adapter.getItem(i).setStatus(CustomerCall.VISITED);
                                    //CallPlan.adapter.getItem(i).setOrderId(-1);

                                    String waktu = Helper.getCurrentDateTime();
                                    CallPlan.adapter.getItem(i).setEndTime(waktu);
                                    CallPlan.adapter.notifyDataSetChanged();

                                    DBManager dm = DBManager.getInstance(getApplicationContext());
                                    cc.endCall(dm.database(), lastno, waktu);
                                    com.ksni.roots.ngsales.model.Order.updateDuration(getApplicationContext(), lastno, cc.getDuration());
                                    //dm.close();

                                    break;
                                }
                            }


                            // antrian
                            if (lastno != -1) {
                                updatePicOutReason(lastno);
                                Synchronous kirim = new Synchronous(Order.this, DBManager.getInstance(getApplicationContext()).database(), company, salesman);
                                String result = kirim.postData(lastno, null);
                                Helper.notifyQueue(getApplicationContext());
                            }

                            dlgReason.dismiss();
                            finish();

                        }

                    }
                });
                dlgReason = builder.create();
                dlgReason.show();

            }
        } else { //Order Item

            save("");

            // update pic
            SharedPreferences sessionX = getApplicationContext().getSharedPreferences("ngsales", 0);
            long lastno = -1;
            if (regularOrder) {
                lastno = sessionX.getLong("CUR_TRANSACTION", -1);
            } else {
                lastno = sessionX.getLong("CUR_TRANSACTION_RETURN", -1);
            }

            updatePicOut(lastno);

            finish();
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final String reason = "";

        switch (id)         {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_clear_order:

                AlertDialog.Builder cbuilder = new AlertDialog.Builder(this);
                cbuilder.setMessage(Helper.getStrResource(this,R.string.transaction_order_msg_confirm_clear_items));
                cbuilder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));

                cbuilder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listOrderItem.clear();
                        listOrderItemSaved.clear();
                        adapter = new OrderAdapter(Order.this, R.layout.ui_order_item, listOrderItem,(TextView)findViewById(R.id.tTotal));
                        lv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                    }
                });

                cbuilder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                cbuilder.create().show();


                break;

            case R.id.action_save_order:
                postOrder();

                break;
            case R.id.action_tambah_item:
                Intent intent = new Intent(Order.this,AddOrder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("result", "order");
                //intent.putExtra("india", india);
                startActivityForResult(intent, REQUEST_ADD_ITEM);
                break;
            case R.id.action_delete_order:
                //if (adapter.isList() || adapter.isStock()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(Helper.getStrResource(this,R.string.transaction_order_msg_confirm_delete_all_items));
                    builder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));

                    builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes)  , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                            long curTrs = session.getLong("CUR_TRANSACTION", -1);


                            if (curTrs != -1) {
                                //Settings.setTransactionLastId(Order.this, -1,MainActivity.currentSalesman);
                                DBManager dm = DBManager.getInstance(getApplicationContext());
                                com.ksni.roots.ngsales.model.Order ord = new com.ksni.roots.ngsales.model.Order(dm.database());
                                ord.setOrderId(curTrs);
                                if (ord.delete())
                                    Helper.showToast(getApplicationContext(), Helper.getStrResource(Order.this,R.string.transaction_order_msg_transaction_deleted)  );

                                //dm.close();

                            }

                            MainActivity.dataOrder.clear();
                            listOrderItem.clear();
                            adapter = new OrderAdapter(Order.this, R.layout.ui_order_item, listOrderItem, (TextView) findViewById(R.id.tTotal));
                            lv.setAdapter(adapter);

                            SharedPreferences sessionX = getApplicationContext().getSharedPreferences("ngsales", 0);
                            SharedPreferences.Editor e = sessionX.edit();
                            e.putLong("CUR_TRANSACTION", -1);
                            e.commit();
                            finish();

                        }
                    });

                    builder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();
                //}
                //else{
                 //   Helper.msgbox(this,"No list.","Delete Transaction");
               // }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public class CFreeQty{
        public String uom;
        public int qty;

        public CFreeQty(String uom,int qty){
            this.uom = uom;
            this.qty = qty;
        }
    }

    private void loadFreeGood(SQLiteDatabase db,
                              String customer,
                              String sku,
                              int qtyOrder,
                              String uom,
                              int itemid){

    //read config allow free good calculate
    boolean allow_free_goods_return = Config.getChecked(getApplicationContext(), "calculate_free_good_return");
    boolean allow_free_goods = Config.getChecked(getApplicationContext(), "allow_free_goods");
    if ( (allow_free_goods && regularOrder )  || (allow_free_goods_return && !regularOrder ) ) {
        // delete first
        int i = adapter.getCount();
        for (int ii = 0; ii < i; ii++) {
            for (OrderItem o : adapter.dataFilter) {
                if (o.refItem == itemid) {
                    adapter.remove(o);
                    break;
                }
            }
        }


        // free good
        FreeGood fg = new FreeGood(db);
        fg.qty = qtyOrder;
        fg.outlet_id = customer;
        fg.product_id = sku;
        fg.date = Helper.getCurrentDate();
        Product xprd = Product.getProductData(db, sku);
        if (xprd != null) fg.division = xprd.getDivision();
        fg.channel = channel;
        fg.zone = zone;
        fg.uom = uom;
        if (fg.getFree()) {
            Product prd = Product.getProductData(db, fg.getFreeSKU());
            if (prd != null) {
                List<CFreeQty> splitFreegood = new ArrayList<CFreeQty>();
                splitFreegood.clear();
                OrderItem itmFree = new OrderItem();
                itmFree.itemType = "F";
                // ref bonus

                itmFree.uomSmall = prd.getUomSmall();
                itmFree.uomMedium = prd.getUomMedium();
                itmFree.uomLarge = prd.getUomLarge();

                itmFree.refItem = itemid;
                itmFree.productId = fg.getFreeSKU();
                itmFree.productName = prd.getProductName();
                itmFree.division = prd.getDivision();
                itmFree.brand = prd.getBrand();

                Log.e("FREE OK", "");


                if (fg.getMultiply().equals("1")) { // multiple
                    if (fg.getMinQty() > 0) {
                        itmFree.qty = (qtyOrder / fg.getBuyQty()) * fg.getFreeQty();
                    } else
                        itmFree.qty = fg.getFreeQty();
                } else {
                    if (fg.getMinQty() > 0) { // regular
                        itmFree.qty = fg.getFreeQty();
                    }

                }

                /*
                if (fg.getMultiply().equals("1")) { // multiple
                    if (fg.getMinQty() > 0) {
                            itmFree.qty = (qtyOrder / fg.getBuyQty()) * fg.getFreeQty();
                    }
                    else
                        itmFree.qty =  fg.getFreeQty();
                }
                else if (fg.getProportional().equals("1")) { // proportional
                    if (fg.getMinQty() > 0) {

                        UomConversion konv = null;
                        if (fg.getFreeUoM().toLowerCase().equals(prd.getUomLarge().toLowerCase())){
                            int toSmall =  fg.getBuyQty() * prd.getConversionLargeToSmall();
                            konv = new UomConversion(toSmall,prd.getConversionMediumToSmall(),prd.getConversionLargeToSmall());
                            konv.fromSmall();
                        }
                        else if (fg.getFreeUoM().toLowerCase().equals(prd.getUomMedium().toLowerCase())){
                            int toSmall =  fg.getBuyQty() * prd.getConversionMediumToSmall();
                            konv = new UomConversion(toSmall,prd.getConversionMediumToSmall(),prd.getConversionLargeToSmall());
                            konv.fromSmall();
                        }
                        else if (fg.getFreeUoM().toLowerCase().equals(prd.getUomSmall().toLowerCase())){
                            konv = new UomConversion(fg.getBuyQty(),prd.getConversionMediumToSmall(),prd.getConversionLargeToSmall());
                            konv.fromSmall();
                        }

                        if (konv.getIntLarge()>0) splitFreegood.add(new CFreeQty(prd.getUomLarge(),konv.getIntLarge()));
                        if (konv.getIntMedium()>0) splitFreegood.add(new CFreeQty(prd.getUomMedium(),konv.getIntMedium()));
                        if (konv.getIntSmall()>0) splitFreegood.add(new CFreeQty(prd.getUomSmall(),konv.getIntSmall()));



                    }
                    else
                        itmFree.qty =  fg.getFreeQty();
                }
                else if (fg.getMinQty() > 0) { // regular
                    itmFree.qty = fg.getFreeQty();
                }*/

                itmFree.uom = fg.getFreeUoM();
                itmFree.price = 0;
                itmFree.uomSmall = prd.getUomSmall();
                itmFree.uomMedium = prd.getUomMedium();
                itmFree.uomLarge = prd.getUomLarge();
                itmFree.largeToSmall = prd.getConversionLargeToSmall();
                itmFree.mediumToSmall = prd.getConversionMediumToSmall();

                itmFree.lastQty = 0;
                itmFree.lastUom = prd.getUom();

                itmFree.stockQty = 0;
                itmFree.stockUom = prd.getUom();

                itmFree.suggestQty = 0;
                itmFree.suggestUom = prd.getUom();

                itmFree.regularDiscount = 0;
                itmFree.extraDiscount = 0;
                itmFree.specialDiscount = 0;
                // bonus
                itmFree.id = adapter.getAutoId();

                if (itmFree.qty > 0 || splitFreegood.size() > 0) {
                    if (splitFreegood.size() > 0) {
                        for (CFreeQty xfree : splitFreegood) {
                            OrderItem itmFreeX = new OrderItem();
                            itmFreeX = itmFree;
                            itmFreeX.qty = xfree.qty;
                            itmFreeX.uom = xfree.uom;
                            addItem(itmFreeX);
                        }
                    } else {
                        addItem(itmFree);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
      }
    }



    private void updateIPT(){
        // check ipt is in extra disc
        boolean ipt_ext = Config.getChecked(getApplicationContext(), "ipt_ext");

        //read config allow discount
        boolean allow_discount = Config.getChecked(getApplicationContext(), "allow_discount");
        if (allow_discount) {
            DBManager dm = DBManager.getInstance(getApplicationContext());
            List<OrderItem> lstOrder = new ArrayList<OrderItem>();
            lstOrder.addAll(adapter.dataFilter);

            // reset first
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).isIPT.equals("1")) {
                    if (ipt_ext)
                        adapter.getItem(i).extraDiscount = 0;
                    else
                        adapter.getItem(i).specialDiscount = 0;

                    adapter.getItem(i).isIPT = "0";
                }
            }


            double totalInv = 0; // without IPT
            for (int i = 0; i < adapter.getCount(); i++) {
                //totalInv += adapter.getItem(i).getTotalExIPT();
                totalInv += adapter.getItem(i).getSubTotal(); // gross
            }


            // update IPT per item

            Pricing p = new Pricing(dm.database(), channel, Helper.getCurrentDate());
            ResultPricing rp = p.getIPT(adapter.dataFilter, totalInv,ipt_ext);

            if (rp != null) {

                if (rp.ada) {
                    for (int item : rp.ipts) {
                        for (int i = 0; i < lstOrder.size(); i++) {
                            boolean okipt = false;

                            adapter.getItem(i).ext=ipt_ext;

                            if (ipt_ext){
                                if (adapter.getItem(i).extraDiscount==0) okipt = true;
                            }else{
                                if (adapter.getItem(i).specialDiscount==0) okipt = true;
                            }

                            //if (adapter.getItem(i).id == item && adapter.getItem(i).uom.equals(rp.uom) && adapter.getItem(i).qty >= rp.min_qty && okipt) {
                            if (adapter.getItem(i).id == item && okipt) {
                                adapter.getItem(i).isPercentIPT = rp.isPercent;
                                adapter.getItem(i).isIPT = "1";

                                if (rp.isPercent.equals("1")) {// percent
                                    if (ipt_ext)
                                        adapter.getItem(i).extraDiscount = rp.discount;
                                    else
                                        adapter.getItem(i).specialDiscount = rp.discount;
                            }
                                else {// value
                                    if (ipt_ext)
                                        adapter.getItem(i).extraDiscount = rp.discount * adapter.getItem(i).qty;
                                    else
                                        adapter.getItem(i).specialDiscount = rp.discount * adapter.getItem(i).qty;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void showDialog(OrderItem ordx){

        final OrderItem ord = ordx;

        final AlertDialog.Builder builder = new AlertDialog.Builder(Order.this);
        LayoutInflater inflater = getLayoutInflater();
        final View lay = inflater.inflate(R.layout.ui_order_qyt, null);

        if (regularOrder){
            ((TextView)lay.findViewById(R.id.lblOrder)).setText(Helper.getStrResource(Order.this,R.string.common_text_order));
        }else{
            ((TextView)lay.findViewById(R.id.lblOrder)).setText(Helper.getStrResource(Order.this,R.string.common_text_return));
        }


        if (ord.itemType.equals("F")){
            Helper.showToast(getApplicationContext(), Helper.getStrResource(Order.this,R.string.transaction_order_msg_restrict_edit_free_good));

        }
        else{

            LinearLayout loStock = (LinearLayout)lay.findViewById(R.id.linearStock);
            LinearLayout loReason = (LinearLayout)lay.findViewById(R.id.linearReason);
            LinearLayout loLastCall = (LinearLayout)lay.findViewById(R.id.linearLastCall);
            LinearLayout loSuggest = (LinearLayout)lay.findViewById(R.id.linearSuggestion);
            if(!regularOrder) {
                List<ReasonRetur> lsReason = ReasonRetur.getData(DBManager.getInstance(getApplicationContext()).database());
                List<Collection> listReasonCollection = new ArrayList<Collection>();
                listReasonCollection.add(new Collection(Helper.getStrResource(Order.this,R.string.common_text_select_hint),"0"));
                for(ReasonRetur ret:lsReason){
                    listReasonCollection.add(new Collection(ret.getDescription(),ret.getReason()));
                }



                Spinner reasonRet = (Spinner) lay.findViewById(R.id.tReason);
                ArrayAdapter<Collection> dataAdapterReason = new ArrayAdapter<Collection>(Order.this, android.R.layout.simple_spinner_item, listReasonCollection);
                reasonRet.setAdapter(dataAdapterReason);


                for (int i = 0; i < listReasonCollection.size(); i++) {
                    Collection col = listReasonCollection.get(i);
                    String tag = String.valueOf(col.tag);
                    if (tag.equals(ord.reasonReturId)) {
                        reasonRet.setSelection(i);
                        break;
                    }
                }

                loReason.setVisibility(View.VISIBLE);
                loLastCall.setVisibility(View.GONE);
                loSuggest.setVisibility(View.GONE);
                loStock.setVisibility(View.GONE);
            }else{
                loReason.setVisibility(View.GONE);
                loLastCall.setVisibility(View.VISIBLE);
                loSuggest.setVisibility(View.VISIBLE);
                loStock.setVisibility(View.VISIBLE);

            }



            List<String> list = new ArrayList<String>();
            if (ord.uomLarge != "") list.add(ord.uomLarge);
            if (ord.uomMedium != "") list.add(ord.uomMedium);
            if (ord.uomSmall != "") list.add(ord.uomSmall);



            List<String> listLast = new ArrayList<String>();
            listLast.add(ord.lastUom);

            Spinner spLast = (Spinner) lay.findViewById(R.id.tLastUom);
            ArrayAdapter<String> dataAdapterLast = new ArrayAdapter<String>(Order.this, android.R.layout.simple_spinner_item, listLast);
            dataAdapterLast.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLast.setAdapter(dataAdapterLast);
            spLast.setEnabled(false);

            List<String> listSuggest = new ArrayList<String>();
            listSuggest.addAll(list);
            Spinner spSugg = (Spinner) lay.findViewById(R.id.tSuggestUom);
            ArrayAdapter<String> dataAdapterSuggest = new ArrayAdapter<String>(Order.this, android.R.layout.simple_spinner_item, listSuggest);
            dataAdapterSuggest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSugg.setAdapter(dataAdapterSuggest);

            for (int i = 0; i < listSuggest.size(); i++) {
                if (listSuggest.get(i).equals(ord.suggestUom)) {
                    spSugg.setSelection(i);
                    break;
                }
            }
            spSugg.setEnabled(false);




            Spinner spStock = (Spinner) lay.findViewById(R.id.tStockUom);
            ArrayAdapter<String> dataAdapterStk = new ArrayAdapter<String>(Order.this, android.R.layout.simple_spinner_item, list);
            dataAdapterStk.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStock.setAdapter(dataAdapterStk);

            Spinner spOrder = (Spinner) lay.findViewById(R.id.tOrderUom);
            ArrayAdapter<String> dataAdapterOrd = new ArrayAdapter<String>(Order.this, android.R.layout.simple_spinner_item, list);
            dataAdapterOrd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spOrder.setAdapter(dataAdapterOrd);

            final EditText tSuggest = (EditText) lay.findViewById(R.id.tSuggest);
            tSuggest.setText(String.valueOf(ord.suggestQty));

            final EditText tLast = (EditText) lay.findViewById(R.id.tLast);
            tLast.setText(String.valueOf(ord.lastQty));

            final EditText tStock = (EditText) lay.findViewById(R.id.tStock);
            tStock.setText(String.valueOf(ord.stockQty));

            final EditText tOrd = (EditText) lay.findViewById(R.id.tOrder);
            tOrd.setText(String.valueOf(ord.qty));

            //tLast.setFocusable(false);
            //tStock.setFocusable(false);
            //tSuggest.setFocusable(false);
            tOrd.setImeOptions(EditorInfo.IME_ACTION_DONE);



            final TextView tHarga = (TextView) lay.findViewById(R.id.tHarga);
            tHarga.setText(Helper.getFormatCurrencyWithDigit(ord.price));


            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(ord.stockUom)) {
                    spStock.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(ord.uom)) {
                    spOrder.setSelection(i);
                    break;
                }
            }
            tStock.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    EditText tSugg = (EditText) lay.findViewById(R.id.tSuggest);


                    int stk = 0, last = 0;

                    if (s.toString().equals(""))
                        stk = 0;
                    else
                        stk = Integer.parseInt(s.toString());

                    if (tLast.getText().toString().equals(""))
                        last = 0;
                    else
                        last = Integer.parseInt(tLast.getText().toString());


                    int sugg = last - stk;
                    if (sugg < 0) sugg = 0;
                    tSugg.setText(String.valueOf(sugg));

                }
            });


            builder.setView(lay);
            final AlertDialog ad = builder.create();

            final EditText tQty = (EditText) lay.findViewById(R.id.tOrder);
            final Spinner tQtyUom = (Spinner) lay.findViewById(R.id.tOrderUom);

            final EditText tQtyStock = (EditText) lay.findViewById(R.id.tStock);
            final Spinner tQtyStockUom = (Spinner) lay.findViewById(R.id.tStockUom);

            final Spinner tReason = (Spinner) lay.findViewById(R.id.tReason);

            final EditText tQtyLast = (EditText) lay.findViewById(R.id.tLast);
            final Spinner tQtyLastUom = (Spinner) lay.findViewById(R.id.tLastUom);

            final EditText tQtySuggest = (EditText) lay.findViewById(R.id.tSuggest);
            final Spinner tQtySuggestUom = (Spinner) lay.findViewById(R.id.tSuggestUom);


            tQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {


                        boolean ada = false;
                        for (OrderItem oi : adapter.dataFilter) {
                            String listkey = oi.productId + oi.uom;
                            String key = ord.productId + tQtyUom.getSelectedItem().toString();
                            if (listkey.equals(key) && oi.itemType.equals("N")) {
                                ada = true;
                                break;
                            }
                        }

/**/

                        if (ada) {
                            Helper.showToast(getApplicationContext(), ord.productName + " " + tQtyUom.getSelectedItem().toString() + " " + Helper.getStrResource(Order.this, R.string.common_text_item_already_add));

                        } else {

                            ord.qty = Helper.toInt(tQty.getText().toString());
                            ord.uom = tQtyUom.getSelectedItem().toString();

                            DBManager dm = DBManager.getInstance(getApplicationContext());

                            // start get price
                            Product p = Product.getProductData(dm.database(), ord.productId);
                            Pricing prc = new Pricing(dm.database(), customerNumber, channel, zone, p.getDivision(), p.getProcutId(), ord.uom, ord.qty, 0, Helper.getCurrentDate(), getApplicationContext());
                            ord.price = prc.getPrice(); // p2

                            Spinner tTop = (Spinner) findViewById(R.id.tTop);
                            if ((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals("0")) || (!allow_invoice_cash_discount))
                                ord.regularDiscount = prc.getDiscountReg();
                            else
                                ord.regularDiscount = 0;


                            ord.extraDiscount = prc.getDiscountExt();
                            ord.specialDiscount = prc.getDiscountSpec();
                            // end get price

                            if (!regularOrder) {
                                Collection col = (Collection) tReason.getSelectedItem();
                                ord.reasonReturId = String.valueOf(col.tag);
                                ord.reasonReturName = col.string;
                            }

                            ord.stockQty = Helper.toInt(tStock.getText().toString());
                            ord.stockUom = tQtyStockUom.getSelectedItem().toString();

                            ord.lastQty = Helper.toInt(tLast.getText().toString());
                            ord.lastUom = tQtyLastUom.getSelectedItem().toString();

                            ord.suggestQty = Helper.toInt(tQtySuggest.getText().toString());
                            ord.suggestUom = tQtySuggestUom.getSelectedItem().toString();

                            String prod_id = ord.productId;
                            String uom = ord.uom;
                            int qty = Helper.toInt(tQty.getText().toString());
                            int id = ord.id;

                            addItem(ord);


                            adapter.loadTotal();


                            loadFreeGood(dm.database(),
                                    customerNumber,
                                    prod_id,
                                    qty,
                                    uom,
                                    id);
                            //dm.close();
                            hasChange = true;
                            //updateIPT();
                            adapter.notifyDataSetChanged();

                            check300();
                            ad.dismiss();
                        }
            /**/

                    }
                    return false;
                }
            });

            ad.setTitle(ord.productName + " - " + ord.productId);
            ad.setButton(AlertDialog.BUTTON_POSITIVE, Helper.getStrResource(Order.this, R.string.common_msg_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            boolean ada = false;
                            for (OrderItem oi : adapter.dataFilter) {
                                String listkey = oi.productId + oi.uom;
                                String key = ord.productId + tQtyUom.getSelectedItem().toString();
                                if (listkey.equals(key) && oi.itemType.equals("N")) {
                                    ada = true;
                                    break;
                                }
                            }


/**/

                            if (ada) {
                                Helper.showToast(getApplicationContext(), ord.productName + " " + tQtyUom.getSelectedItem().toString() + " " + Helper.getStrResource(Order.this, R.string.common_text_item_already_add));

                            } else {

                                ord.qty = Helper.toInt(tQty.getText().toString());
                                ord.uom = tQtyUom.getSelectedItem().toString();

                                DBManager dm = DBManager.getInstance(getApplicationContext());

                                // start get price
                                Product p = Product.getProductData(dm.database(), ord.productId);
                                Pricing prc = new Pricing(dm.database(), customerNumber, channel, zone, p.getDivision(), p.getProcutId(), ord.uom, ord.qty, 0, Helper.getCurrentDate(), getApplicationContext());
                                ord.price = prc.getPrice(); // p2

                                Spinner tTop = (Spinner) findViewById(R.id.tTop);
                                if ((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals("0")) || (!allow_invoice_cash_discount))
                                    ord.regularDiscount = prc.getDiscountReg();
                                else
                                    ord.regularDiscount = 0;


                                ord.extraDiscount = prc.getDiscountExt();
                                ord.specialDiscount = prc.getDiscountSpec();
                                // end get price

                                if (!regularOrder) {
                                    Collection col = (Collection) tReason.getSelectedItem();
                                    ord.reasonReturId = String.valueOf(col.tag);
                                    ord.reasonReturName = col.string;
                                }

                                ord.stockQty = Helper.toInt(tStock.getText().toString());
                                ord.stockUom = tQtyStockUom.getSelectedItem().toString();

                                ord.lastQty = Helper.toInt(tLast.getText().toString());
                                ord.lastUom = tQtyLastUom.getSelectedItem().toString();

                                ord.suggestQty = Helper.toInt(tQtySuggest.getText().toString());
                                ord.suggestUom = tQtySuggestUom.getSelectedItem().toString();

                                String prod_id = ord.productId;
                                String uom = ord.uom;
                                int qty = Helper.toInt(tQty.getText().toString());
                                int id = ord.id;

                                addItem(ord);


                                adapter.loadTotal();


                                loadFreeGood(dm.database(),
                                        customerNumber,
                                        prod_id,
                                        qty,
                                        uom,
                                        id);
                                //dm.close();
                                hasChange = true;
                                //updateIPT();
                                adapter.notifyDataSetChanged();

                                check300();
                                ad.dismiss();
                            }









                          /**/


                            }
                        }

                        );
                        ad.setButton(AlertDialog.BUTTON_NEGATIVE,Helper.getStrResource(Order.this,R.string.common_msg_cancel),
                                new DialogInterface.OnClickListener()

                        {
                            public void onClick (DialogInterface dialog,int which){
                            ad.dismiss();
                        }
                        }

                        );

                        //tStock.setSelectAllOnFocus(true);

                        if(tStock.getText().

                        toString()

                        .

                        equals("0")

                        )tStock.setText("");
                        if(tQty.getText().

                        toString()

                        .

                        equals("0")

                        )tQty.setText("");
                        ad.getWindow().

                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            ad.show();

                    }
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_ADD_ITEM){
            if (resultCode == RESULT_OK){

                DBManager dm = DBManager.getInstance(getApplicationContext());
                OrderItem itm = new OrderItem();

                hasChange = true;

                itm.regularOrder = regularOrder;

                itm.uomSmall = intent.getStringExtra("small_uom");
                itm.uomMedium = intent.getStringExtra("medium_uom");
                itm.uomLarge = intent.getStringExtra("large_uom");
                itm.reasonReturId = "0";
                itm.productId = intent.getStringExtra("id");
                itm.productName = intent.getStringExtra("name");
                itm.division = intent.getStringExtra("division");
                itm.brand = intent.getStringExtra("brand");
                itm.qty =  intent.getIntExtra( "order",0);
                itm.uom =  intent.getStringExtra("uom");
                itm.price  = intent.getDoubleExtra("price", 0);
                itm.uomSmall =  intent.getStringExtra("uomsmall");
                itm.uomMedium =  intent.getStringExtra("uommedium");
                itm.uomLarge =  intent.getStringExtra("uomlarge");
                itm.largeToSmall =  intent.getIntExtra("large_to_small", 0);
                itm.mediumToSmall =  intent.getIntExtra("medium_to_small", 0);
                itm.itemType = "N";
                itm.equals(listOrderItem);
                itm.lastQty = intent.getIntExtra("last", 0);
                itm.lastUom = intent.getStringExtra("lastuom");
                itm.stockQty = intent.getIntExtra("stock", 0);
                itm.stockUom = intent.getStringExtra("stockuom");
                itm.suggestQty = intent.getIntExtra("suggest", 0);
                itm.suggestUom = intent.getStringExtra("suggestuom");

                boolean ada=false;
                for(OrderItem oi:adapter.dataFilter) {
                    String listkey = oi.productId+oi.uom;
                    String key = itm.productId+itm.uom;
                    if (listkey.equals(key)){
                        ada = true;
                        break;
                    }
                }

                //if(ada){
                //    Helper.showToast(getApplicationContext(), itm.productName + " " + itm.uom + " "+Helper.getStrResource(this,R.string.common_text_item_already_add)  );
                //}else {
                    Pricing pPrice = new Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, 0, Helper.getCurrentDate(),getApplicationContext());
                    double gross = pPrice.getPrice() * itm.qty;

                    Pricing p = new Pricing(dm.database(), customerNumber, channel, zone, itm.division, itm.productId, itm.uom, itm.qty, gross, Helper.getCurrentDate(),getApplicationContext());

                    Spinner tTop = (Spinner)findViewById(R.id.tTop);
                    if((allow_invoice_cash_discount && tTop.getSelectedItem().toString().equals("0")) || (!allow_invoice_cash_discount) )
                        itm.regularDiscount = p.getDiscountReg();
                    else
                        itm.regularDiscount = 0;

                    itm.extraDiscount = p.getDiscountExt();
                    itm.specialDiscount = p.getDiscountSpec();

                    itm.id = adapter.getAutoId();
                    //addItem(itm);
                    //updateIPT();
                    //loadFreeGood(dm.database(), customerNumber, itm.productId, itm.qty, itm.uom, itm.id);

                    showDialog(itm);


                //}






                // free good
                /*
                FreeGood fg = new FreeGood(dm.database());
                fg.qty = itm.qty;
                fg.outlet_id = customerNumber;
                fg.product_id = itm.productId;
                fg.uom = itm.uom;
                if (fg.getFree()){
                    Log.e("XXX","FREE OK");


                    Product prd = Product.getData(dm.database(),fg.getFreeSKU());
                    if (prd!=null) {
                        Log.e("XXX","PRODUK NULL");

                        OrderItem itmFree = new OrderItem();
                        itmFree.itemType = "F";
                        // ref bonus
                        itmFree.refItem = itm.id;
                        itmFree.productId = fg.getFreeSKU();
                        itmFree.productName = prd.getProductName();
                        itmFree.division = prd.getDivision();
                        itmFree.brand = prd.getBrand();
                        if (fg.getMultiply().equals("1")) {
                            if (fg.getFromQty() > 0)
                                    itmFree.qty = ( itm.qty / fg.getFromQty() )  * fg.getFreeQty();
                            else
                                itmFree.qty =  fg.getFreeQty();
                            }
                        else
                            itmFree.qty =  fg.getFreeQty();
                        itmFree.uom =  fg.getFreeUoM();
                        itmFree.price  = 0;
                        itmFree.uomSmall =  prd.getUomSmall();
                        itmFree.uomMedium =  prd.getUomMedium();
                        itmFree.uomLarge =  prd.getUomLarge();
                        itmFree.conversionSmall =  prd.getConversionSmall();
                        itmFree.conversionMedium =  prd.getConversionMedium();

                        itmFree.lastQty = 0;
                        itmFree.lastUom = prd.getUom();

                        itmFree.stockQty = 0;
                        itmFree.stockUom = prd.getUom();

                        itmFree.suggestQty = 0;
                        itmFree.suggestUom = prd.getUom();

                        itmFree.regularDiscount = 0;
                        itmFree.extraDiscount = 0;
                        itmFree.specialDiscount = 0;
                        // bonus
                        itmFree.id = adapter.getAutoId();
                        addItem(itmFree);
                    }
                }
                        */
                // end of free good

                //dm.close();

            }

        }
        else if (requestCode == REQUEST_PHOTO_OUTLET){
            if (resultCode == RESULT_OK) {

                /*if (intent != null) {
                    Bitmap photo = (Bitmap) intent.getExtras().get("data");
                } else {
                    Bitmap photo = BitmapFactory.decodeFile(imageToUploadUriOutlet.getPath());
                }
                */

            }else{
                imageToUploadUriOutlet = null;
            }
        }
        else if (requestCode == REQUEST_PHOTO){ // foto reason
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    Bitmap photo = (Bitmap) intent.getExtras().get("data");
                } else {
                    Bitmap photo = BitmapFactory.decodeFile(imageToUploadUri.getPath());
                }

                save("02");
                SharedPreferences session =getApplicationContext().getSharedPreferences("ngsales", 0);
                String cur = session.getString("CUR_VISIT", "");
                final long cur_trs = session.getLong("CUR_TRANSACTION", -1);
                //
                SharedPreferences.Editor e = session.edit();
                e.putString("CUR_VISIT", "");
                e.putString("CUR_VISIT_NAME", "");
                e.commit();

                for(int i=0;i<CallPlan.adapter.getCount();i++){
                    CustomerCall cc = CallPlan.adapter.getItem(i);
                    if (cc.getCustomerNumber().equals(cur)){
                        CallPlan.adapter.getItem(i).setStatus(CustomerCall.VISITED);

                        String waktu = Helper.getCurrentDateTime();
                        CallPlan.adapter.getItem(i).setEndTime(waktu);

                        CallPlan.adapter.notifyDataSetChanged();
                        DBManager dm = DBManager.getInstance(getApplicationContext());
                        cc.endCall(dm.database(),cur_trs, waktu);
                        com.ksni.roots.ngsales.model.Order.updateDuration(getApplicationContext(), cur_trs, cc.getDuration());
                        //dm.close();

                        break;
                    }
                }
                //MainActivity.dataOrder.clear();
                // kirim langsung ke sever for current transaction
                if (cur_trs>0) {

                    updatePicOutReason(cur_trs);
                    DBManager dm = DBManager.getInstance(getApplicationContext());
                    Synchronous kirim = new Synchronous(Order.this, dm.database(), company,salesman);
                    String result = kirim.postData(cur_trs,null);
                    Helper.notifyQueue(getApplicationContext());

                    SharedPreferences sessionX =getApplicationContext().getSharedPreferences("ngsales", 0);//
                    SharedPreferences.Editor eX = sessionX.edit();
                    e.putString("FORCE_END_CALL", "1");
                    e.commit();

                    finish();
                    // hold by antrian

                    /*

                    new  AsyncTask<String, Void, String>(){
                        @Override
                        protected void onPreExecute() {
                            //setProgressBarIndeterminateVisibility(false);
                            progressDialog = new ProgressDialog(Order.this);
                            progressDialog.setMessage("Loading...");
                            progressDialog.setIndeterminate(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                        }

                        @Override
                        protected String doInBackground(String... params) {
                            Synchronous kirim = new Synchronous(Order.this, dm.database(), company,salesman);
                            String result = kirim.postData(cur_trs,Helper.getEncodeImage(imageToUploadUri.getPath()));


                            return result;
                        }

                        @Override
                        protected void onPostExecute(String result) {

                            //setProgressBarIndeterminateVisibility(true);

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }



                            if (result.equals("OK")) {


                            } else {

                            }

                            finish();

                        }


                    }.execute();*/


                    //dm.close();
                }


                // hold by antrian
                //SharedPreferences sessionX =getApplicationContext().getSharedPreferences("ngsales", 0);//
                //SharedPreferences.Editor eX = sessionX.edit();
                //e.putString("FORCE_END_CALL", "1");
                //e.commit();
                // hold by antrian

                //Log.e("EXIT", "EXIT");
                //finish();
                //startActivityForResult(new Intent(getApplicationContext(), PreCall.class),3);

                //dm.close();

            }

        }

    }

}



