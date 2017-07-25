package com.ksni.roots.ngsales.domain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.LoadingUnloading;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class CanvasLoadUnload extends AppCompatActivity {
    private String company,  salesman;
    private OrderAdapter adapter;
    private ListView lv;
    private int typeTransaction;
    private Toolbar toolbar;
    private static boolean hasChange = false;
    private List<OrderItem> listOrderItem = new ArrayList<OrderItem>();
    public static final int REQUEST_ADD_ITEM = 0;


    @Override
    public void onResume() {
        super.onResume();
        if (Settings.requiredStart(getApplicationContext())) {
            Settings.restart(getApplicationContext());
            restart();
        }

    }

    private void restart() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }


    @Override
    public void onBackPressed() {
        if ((hasChange && adapter.getCount() > 0 && (adapter.isList()))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Helper.getStrResource(this,R.string.inventory_confirm_containing_item_save));
            builder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));

            builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    postOrder();

                }
            });

            builder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });

            builder.create().show();
        } else {
            super.onBackPressed();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_canvas);

        typeTransaction = getIntent().getIntExtra("type", 0);

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        company = session.getString("CUR_COMPANY", "");
        salesman = session.getString("CUR_SLS", "");


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        listOrderItem.clear();
        if (typeTransaction==LoadingUnloading.TYPE_LOADING)
            ab.setTitle(Helper.getStrResource(this,R.string.inventory_loading));
        else {
            ab.setTitle(Helper.getStrResource(this,R.string.inventory_unloading));

            List<OrderItem> items =  LoadingUnloading.getStockList(getApplicationContext());
            listOrderItem.addAll(items);

        }


        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);


        lv = (ListView) findViewById(R.id.lstOrderItem);
        adapter = new OrderAdapter(this, R.layout.ui_canvas_item, listOrderItem, null);
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

                final AlertDialog.Builder builder = new AlertDialog.Builder(CanvasLoadUnload.this);
                LayoutInflater inflater = getLayoutInflater();
                final View lay = inflater.inflate(R.layout.ui_canvas_qyt, null);


                    List<String> list = new ArrayList<String>();
                    if (ord.uomLarge != "") list.add(ord.uomLarge);
                    if (ord.uomMedium != "") list.add(ord.uomMedium);
                    if (ord.uomSmall != "") list.add(ord.uomSmall);

                    List<String> listLast = new ArrayList<String>();
                    listLast.add(ord.lastUom);


                    Spinner spOrder = (Spinner) lay.findViewById(R.id.tOrderUom);
                    ArrayAdapter<String> dataAdapterOrd = new ArrayAdapter<String>(parent.getContext(), android.R.layout.simple_spinner_item, list);
                    dataAdapterOrd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spOrder.setAdapter(dataAdapterOrd);


                    final EditText tOrd = (EditText) lay.findViewById(R.id.tOrder);
                    tOrd.setText(String.valueOf(ord.qty));


                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).equals(ord.uom)) {
                            spOrder.setSelection(i);
                            break;
                        }
                    }


                    builder.setView(lay);
                    final AlertDialog ad = builder.create();

                    final EditText tQty = (EditText) lay.findViewById(R.id.tOrder);
                    final Spinner tQtyUom = (Spinner) lay.findViewById(R.id.tOrderUom);


                    tQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {


                                boolean ada = false;
                                for (OrderItem oi : adapter.dataFilter) {
                                    if (oi.id != adapter.getItem(pos).id) {
                                        String listkey = oi.productId + oi.uom;
                                        String key = adapter.getItem(pos).productId + tQtyUom.getSelectedItem().toString();
                                        if (listkey.equals(key)) {
                                            ada = true;
                                            break;
                                        }
                                    }
                                }
                                if (ada) {
                                    Helper.showToast(getApplicationContext(), adapter.getItem(pos).productName + " " + tQtyUom.getSelectedItem().toString() + " "+Helper.getStrResource(CanvasLoadUnload.this,R.string.common_text_item_already_add));

                                } else {

                                    adapter.getItem(pos).qty = Helper.toInt(tQty.getText().toString());
                                    adapter.getItem(pos).uom = tQtyUom.getSelectedItem().toString();



                                    String prod_id = adapter.getItem(pos).productId;
                                    String uom = adapter.getItem(pos).uom;
                                    int qty = Helper.toInt(tQty.getText().toString());
                                    int id = adapter.getItem(pos).id;


                                    adapter.loadTotal();

                                    hasChange = true;
                                    adapter.notifyDataSetChanged();



                                    ad.dismiss();
                                }

                            }
                            return false;
                        }
                    });

                    ad.setTitle(ord.productName + " - " + ord.productId);
                ad.setButton(AlertDialog.BUTTON_POSITIVE, Helper.getStrResource(CanvasLoadUnload.this,R.string.common_msg_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                boolean ada = false;
                                for (OrderItem oi : adapter.dataFilter) {
                                    if (oi.id != adapter.getItem(pos).id) {
                                        String listkey = oi.productId + oi.uom;
                                        String key = adapter.getItem(pos).productId + tQtyUom.getSelectedItem().toString();
                                        if (listkey.equals(key)) {
                                            ada = true;
                                            break;
                                        }
                                    }
                                }
                                if (ada) {
                                    Helper.showToast(getApplicationContext(), adapter.getItem(pos).productName + " " + tQtyUom.getSelectedItem().toString() + " "+Helper.getStrResource(CanvasLoadUnload.this,R.string.common_text_item_already_add));

                                } else {

                                    adapter.getItem(pos).qty = Helper.toInt(tQty.getText().toString());
                                    adapter.getItem(pos).uom = tQtyUom.getSelectedItem().toString();



                                    String prod_id = adapter.getItem(pos).productId;
                                    String uom = adapter.getItem(pos).uom;
                                    int qty = Helper.toInt(tQty.getText().toString());
                                    int id = adapter.getItem(pos).id;


                                    adapter.loadTotal();

                                    hasChange = true;
                                    adapter.notifyDataSetChanged();



                                    ad.dismiss();
                                }



                            }
                        }
                        );

                ad.setButton(AlertDialog.BUTTON_NEGATIVE, Helper.getStrResource(CanvasLoadUnload.this,R.string.common_msg_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ad.dismiss();
                            }
                        });


                    //tStock.setSelectAllOnFocus(true);


                    if (tQty.getText().toString().equals("0")) tQty.setText("");
                    ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


                    ad.show();


            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lstOrderItem) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //menu.setHeaderTitle(xx.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final NumberPicker np;
        final PopupWindow pw;
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final OrderItem ord;
        int menuItemIndex = item.getItemId();
        ord = adapter.getItem(info.position);
        if (menuItemIndex == 1000) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.ui_canvas_qyt,
                    (ViewGroup) findViewById(R.id.popupqty));

            np = (NumberPicker) layout.findViewById(R.id.tQty);
            np.setWrapSelectorWheel(false);
            np.setMaxValue(999);
            np.setMinValue(1);

            np.setValue(ord.qty);

            layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            pw = new PopupWindow(layout, 400, 400, true);
            //pw.setAnimationStyle(android.R.anim.fade_in);

            Button btnok = (Button) layout.findViewById(R.id.btnOk);
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


            Button btncancel = (Button) layout.findViewById(R.id.btnCancel);
            btncancel.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    pw.dismiss();
                }
            });


            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);


        } else if (menuItemIndex == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CanvasLoadUnload.this);
                builder.setMessage(Helper.getStrResource(this,R.string.common_msg_delete)+"  " + ord.productName + "?");
                builder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));

                builder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.showToast(getApplicationContext(), "'" + ord.productName + "' "+Helper.getStrResource(CanvasLoadUnload.this,R.string.common_msg_deleted));
                        //OrderAdapter buff = adapter;

                        int i = adapter.getCount();
                        for (int ii = 0; ii < i; ii++) {
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


                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton(Helper.getStrResource(this,R.string.common_msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();


        } else if (menuItemIndex == 2) {
            Toast.makeText(getApplicationContext(), "test2", Toast.LENGTH_LONG).show();


        }


        return true;
    }

    private void addItem(OrderItem item) {
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




    private boolean saveOrder() {
        try {
            hasChange = false;
            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
            String curSales = session.getString("CUR_SLS", "");
            String curCompany = session.getString("CUR_COMPANY", "");

            DBManager dm = DBManager.getInstance(getApplicationContext());
            SQLiteDatabase db = dm.database();
            LoadingUnloading ord = new LoadingUnloading(db);


            EditText tReference = (EditText) findViewById(R.id.tReference);
            ord.setLoadingDate(Helper.getCurrentDateTime());
            ord.setReference(tReference.getText().toString());
            ord.setTransaction(typeTransaction);
            ord.setSales(curSales);

            for (OrderItem itm : listOrderItem) {
                //itm.id = adapter.getAutoId();
                ord.addItem(itm);
            }

                long lastno = ord.save();


                if (lastno != -1) {

                         Synchronous synCanvas = new Synchronous(getApplicationContext(),DBManager.getInstance(getApplicationContext()).database(),curCompany,curSales);
                         synCanvas.postDataCanvas(lastno);
                         Helper.notifyQueue(getApplicationContext());

                    Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.inventory_msg_save_success));

                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    return true;
                } else {
                    Helper.showToast(getApplicationContext(),  Helper.getStrResource(this,R.string.inventory_msg_save_fail));
                    return false;
                }

        } catch (Exception x) {
            Helper.showToast(getApplicationContext(),  Helper.getStrResource(this,R.string.inventory_msg_save_fail));
            return false;
        }

    }



    private void postOrder() {
        if (adapter.isList()) {
            DBManager dm = DBManager.getInstance(getApplicationContext());
            saveOrder();
            finish();
        }else{
            Helper.showToast(getApplicationContext(), Helper.getStrResource(this,R.string.inventory_msg_no_items));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_clear_order:

                AlertDialog.Builder cbuilder = new AlertDialog.Builder(this);
                cbuilder.setMessage(Helper.getStrResource(this,R.string.inventory_msg_confirm_clear_items));
                cbuilder.setTitle(Helper.getStrResource(this,R.string.common_msg_confirm));

                cbuilder.setPositiveButton(Helper.getStrResource(this,R.string.common_msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listOrderItem.clear();
                        adapter = new OrderAdapter(CanvasLoadUnload.this, R.layout.ui_canvas_item, listOrderItem, (TextView) findViewById(R.id.tTotal));
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
                Intent intent = new Intent(CanvasLoadUnload.this, AddOrder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("result", "order");
                startActivityForResult(intent, REQUEST_ADD_ITEM);
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_ADD_ITEM) {
            if (resultCode == RESULT_OK) {
                DBManager dm = DBManager.getInstance(getApplicationContext());
                OrderItem itm = new OrderItem();

                hasChange = true;


                itm.uomSmall = intent.getStringExtra("small_uom");
                itm.uomMedium = intent.getStringExtra("medium_uom");
                itm.uomLarge = intent.getStringExtra("large_uom");

                itm.productId = intent.getStringExtra("id");
                itm.productName = intent.getStringExtra("name");
                itm.brand = intent.getStringExtra("brand");
                itm.qty = intent.getIntExtra("order", 0);
                itm.uom = intent.getStringExtra("uom");
                itm.uomSmall = intent.getStringExtra("uomsmall");
                itm.uomMedium = intent.getStringExtra("uommedium");
                itm.uomLarge = intent.getStringExtra("uomlarge");
                itm.largeToSmall = intent.getIntExtra("large_to_small", 0);
                itm.mediumToSmall = intent.getIntExtra("medium_to_small", 0);


                boolean ada = false;
                for (OrderItem oi : adapter.dataFilter) {
                    String listkey = oi.productId + oi.uom;
                    String key = itm.productId + itm.uom;
                    if (listkey.equals(key)) {
                        ada = true;
                        break;
                    }
                }

                if (ada) {
                    Helper.showToast(getApplicationContext(), itm.productName + " " + itm.uom + " "+Helper.getStrResource(this,R.string.common_text_item_already_add));
                } else {
                    itm.id = adapter.getAutoId();
                    addItem(itm);
                }

            }

        }
    }

    }
