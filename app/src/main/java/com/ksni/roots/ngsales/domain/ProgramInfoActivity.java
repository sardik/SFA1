package com.ksni.roots.ngsales.domain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.util.DBManager;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 06/12/2015.
 */
public class ProgramInfoActivity extends AppCompatActivity{
    private ListView lv;
    private Toolbar toolbar;
    private String curCust = "";
    boolean p1;
    private ActionBar ab;
    ArrayAdapter<ProgramsInfo.InfoHolder> adapter =null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }


    private void refresh(){
        String program = getIntent().getStringExtra("program");
        p1 = getIntent().getBooleanExtra("p1", true);

        if (program.equals("pricing")) {
            ab.setTitle("Pricing");
            final CharSequence[] items = { " Pricing ",
                    " Regular Discount ",
                    " Extra Discount ",
                    " Extra Discount (IPT)",
                    " Special Discount ",
                    " Special Discount (IPT)"
            };



            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("View Pricing");


            int check = -1;
            builder.setSingleChoiceItems(items, check,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    ab.setTitle("Pricing");
                                    loadPricing(p1);
                                    lv.setAdapter(adapter);
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    ab.setTitle("Regular Discount");
                                    loadRegDiscount();
                                    lv.setAdapter(adapter);
                                    dialog.dismiss();
                                    break;
                                case 2:
                                    ab.setTitle("Extra Discount");
                                    loadExtDiscount();
                                    lv.setAdapter(adapter);
                                    dialog.dismiss();
                                    break;
                                case 3:
                                    ab.setTitle("Extra Discount (IPT)");
                                    loadExtDiscountIPT();
                                    lv.setAdapter(adapter);
                                    dialog.dismiss();
                                    break;
                                case 4:
                                    ab.setTitle("Special Discount");
                                    loadSpcDiscount();
                                    lv.setAdapter(adapter);
                                    dialog.dismiss();
                                    break;
                                case 5:
                                    ab.setTitle("Special Discount (IPT)");
                                    loadSpcDiscountIPT();
                                    lv.setAdapter(adapter);
                                    dialog.dismiss();
                                    break;

                            }
                        }
                    });

            builder.show();


        }else if (program.equals("free")) {
            ab.setTitle("Free good");
            loadFreeGood();
            lv.setAdapter(adapter);

        }

    }

    private void loadPricing(boolean p1){
        ProgramsInfo info =new ProgramsInfo(getApplicationContext(),curCust);
        final  List<ProgramsInfo.InfoHolder> lists = info.getListPricing(p1);

        adapter = new ArrayAdapter<ProgramsInfo.InfoHolder>(this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {
            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                ProgramsInfo.InfoHolder info = lists.get(position);

                Log.e("info.product_id",String.valueOf(info.product_id));
                Product p = Product.getProductData(DBManager.getInstance(getApplicationContext()).database(), info.product_id);
                if (p!=null)
                    text1.setText(p.getProductName() + " - " + p.getProcutId());
                else
                    text1.setText("N/A");
                text1.setTypeface(Typeface.SANS_SERIF);
                text1.setTextSize(17);

                text2.setText("Price: " + Helper.getFormatCurrencyWithDigit(info.price) + "\n" +
                        info.validFrom + " until " + info.validTo + "\n" +
                        "Conditions: \n"+
                         "UoM " + info.uom+"\n"+
                         "Ranges " + Helper.getFormatCurrencyWithDigit(info.fromValue) + " to "+Helper.getFormatCurrencyWithDigit(info.toValue));
                text1.setTypeface(Typeface.MONOSPACE);


                return view;
            }

        };
    }


    private void loadRegDiscount(){
        ProgramsInfo info =new ProgramsInfo(getApplicationContext(),curCust);

        final  List<ProgramsInfo.InfoHolder> lists = info.getListDiscountReg();

        adapter = new ArrayAdapter<ProgramsInfo.InfoHolder>(this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {
            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                ProgramsInfo.InfoHolder info = lists.get(position);

                Product p = Product.getProductData(DBManager.getInstance(getApplicationContext()).database(), info.product_id);
                if (p!=null) {
                    text1.setText(p.getProductName() + " - " + p.getProcutId());
                    text1.setTypeface(Typeface.SANS_SERIF);
                    text1.setTextSize(17);
                }else{
                    text1.setText("By Total");
                    text1.setTypeface(Typeface.SANS_SERIF);
                    text1.setTextSize(17);
                }

                if (info.isQty.equals("1")) {
                    text2.setText("Regular Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions: Ranges Quantity " + Helper.getFormatCurrency(info.fromQty) + " to " + Helper.getFormatCurrency(info.toQty) + " of UoM " + info.uom);
                    text1.setTypeface(Typeface.MONOSPACE);
                }else{
                    text2.setText("Regular Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions: Ranges Value "+Helper.getFormatCurrency(info.fromValue) +" to " +Helper.getFormatCurrency(info.toValue)+" of UoM "+info.uom);
                    text1.setTypeface(Typeface.MONOSPACE);
                }

                return view;
            }

        };
    }


    private void loadExtDiscount(){
        ProgramsInfo info =new ProgramsInfo(getApplicationContext(),curCust);

        final  List<ProgramsInfo.InfoHolder> lists = info.getListDiscountExt();

        adapter = new ArrayAdapter<ProgramsInfo.InfoHolder>(this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {
            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                ProgramsInfo.InfoHolder info = lists.get(position);

                Product p = Product.getProductData(DBManager.getInstance(getApplicationContext()).database(), info.product_id);
                if (p!=null) {
                    text1.setText(p.getProductName() + " - " + p.getProcutId());
                    text1.setTypeface(Typeface.SANS_SERIF);
                    text1.setTextSize(17);
                }else{
                    text1.setText("By Total");
                    text1.setTypeface(Typeface.SANS_SERIF);
                    text1.setTextSize(17);
                }

                if (info.isQty.equals("1")) {
                    text2.setText("Extra Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions: Ranges Quantity " + Helper.getFormatCurrencyWithDigit(info.fromQty) + " to " + Helper.getFormatCurrency(info.toQty) + " of UoM " + info.uom);
                    text1.setTypeface(Typeface.MONOSPACE);
                }else{
                    text2.setText("Extra Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions: Ranges Value "+Helper.getFormatCurrency(info.fromValue) +" to " +Helper.getFormatCurrency(info.toValue)+" of UoM "+info.uom);
                    text1.setTypeface(Typeface.MONOSPACE);
                }

                return view;
            }

        };
    }

    private void loadSpcDiscount(){
        ProgramsInfo info =new ProgramsInfo(getApplicationContext(),curCust);

        final  List<ProgramsInfo.InfoHolder> lists = info.getListDiscountSpc();

        adapter = new ArrayAdapter<ProgramsInfo.InfoHolder>(this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {
            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                ProgramsInfo.InfoHolder info = lists.get(position);

                Product p = Product.getProductData(DBManager.getInstance(getApplicationContext()).database(), info.product_id);
                text1.setText(p.getProductName() + " - " + p.getProcutId());
                text1.setTypeface(Typeface.SANS_SERIF);
                text1.setTextSize(17);

                if (info.isQty.equals("1")) {
                    text2.setText("Special Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions: Ranges Quantity "+Helper.getFormatCurrency(info.fromQty) +" " + " " +Helper.getFormatCurrency(info.toQty)+" of UoM "+info.uom);
                    text1.setTypeface(Typeface.MONOSPACE);
                }else{
                    text2.setText("Special Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions: Ranges Value "+Helper.getFormatCurrency(info.fromValue) +" " + " " +Helper.getFormatCurrency(info.toValue)+" of UoM "+info.uom);
                    text1.setTypeface(Typeface.MONOSPACE);
                }

                return view;
            }

        };
    }

    private void loadExtDiscountIPT(){
        ProgramsInfo info =new ProgramsInfo(getApplicationContext(),curCust);

        final  List<ProgramsInfo.InfoHolder> lists = info.getListDiscountChannelIPT();

        adapter = new ArrayAdapter<ProgramsInfo.InfoHolder>(this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {
            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                ProgramsInfo.InfoHolder info = lists.get(position);

                text1.setText("Number Of Item "+String.valueOf(info.ipt));
                text1.setTypeface(Typeface.SANS_SERIF);
                text1.setTextSize(17);

                if (info.isPercent.equals("1")) {
                    text2.setText("IPT Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions:\n" +
                            "Min Quantity "+Helper.getFormatCurrency(info.minQty) +"\n" +
                            "UoM "+info.uom );
                    text1.setTypeface(Typeface.MONOSPACE);
                }else{
                    text2.setText("IPT Discount: " + Helper.getFormatCurrency(info.ipt) + "\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions:\n" +
                            "Min Quantity "+Helper.getFormatCurrency(info.minQty) +"\n" +
                            "Min Value " + Helper.getFormatCurrency(info.minValue) +"\n" +
                            "UoM "+info.uom );
                    text1.setTypeface(Typeface.MONOSPACE);
                }

                return view;
            }

        };
    }


    private void loadSpcDiscountIPT(){
        ProgramsInfo info =new ProgramsInfo(getApplicationContext(),curCust);

        final  List<ProgramsInfo.InfoHolder> lists = info.getListSpecDiscountChannelIPT();

        adapter = new ArrayAdapter<ProgramsInfo.InfoHolder>(this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {
            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                ProgramsInfo.InfoHolder info = lists.get(position);

                text1.setText("Number Of Item "+String.valueOf(info.ipt));
                text1.setTypeface(Typeface.SANS_SERIF);
                text1.setTextSize(17);

                if (info.isPercent.equals("1")) {
                    text2.setText("IPT Discount: " + Helper.getFormatCurrencyWithDigit(info.discount) + "%\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions:\n" +
                            "Min Quantity "+Helper.getFormatCurrency(info.minQty) +"\n" +
                            "UoM "+info.uom );
                    text1.setTypeface(Typeface.MONOSPACE);
                }else{
                    text2.setText("IPT Discount: " + Helper.getFormatCurrency(info.discount) + "\n" +
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions:\n" +
                            "Min Quantity "+Helper.getFormatCurrency(info.minQty) +"\n" +
                            "Min Value " + Helper.getFormatCurrency(info.minValue) +"\n" +
                            "UoM "+info.uom );
                    text1.setTypeface(Typeface.MONOSPACE);
                }

                return view;
            }

        };
    }
    private void loadFreeGood(){
        ProgramsInfo info =new ProgramsInfo(getApplicationContext(),curCust);

        final  List<ProgramsInfo.InfoHolder> lists = info.getListFreeGood();

        adapter = new ArrayAdapter<ProgramsInfo.InfoHolder>(this, android.R.layout.simple_list_item_2, android.R.id.text1, lists) {
            @Override
            public View getView(int position,
                                View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                ProgramsInfo.InfoHolder info = lists.get(position);

                Product p = Product.getProductData(DBManager.getInstance(getApplicationContext()).database(), info.product_id);
                text1.setText(p.getProductName() + " - " + p.getProcutId());
                text1.setTypeface(Typeface.SANS_SERIF);
                text1.setTextSize(17);

                Product pFree = Product.getProductData(DBManager.getInstance(getApplicationContext()).database(), info.freeSku);

                    text2.setText(
                            info.validFrom + " until " + info.validTo + "\n" +
                            "Conditions:\n" +
                            "Min Quantity "+Helper.getFormatCurrency(info.minQty) +"\n" +
                            "Buy Quantity "+Helper.getFormatCurrency(info.buyQty) +" "+info.uom+"\n" +
                            "Free SKU " + pFree.getProductName() +" - "+info.freeSku+"\n" +
                            "Free Quantity "+String.valueOf(info.freeQty) +" "+info.uom );
                    text1.setTypeface(Typeface.MONOSPACE);

                return view;
            }

        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_report_per_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ab = getSupportActionBar();



        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);


        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        curCust = session.getString("CUR_VISIT", "");
        lv = (ListView)findViewById(R.id.lstProduct);



            refresh();


        lv.setEmptyView(findViewById(R.id.list_empty));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_refresh:
                refresh();
                break;

        }

        return super.onOptionsItemSelected(item);
    }




}
