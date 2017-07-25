package com.ksni.roots.ngsales.domain;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ksni.roots.ngsales.R;

/**
 * Created by #roots on 07/08/2015.
 */
public class Call extends AppCompatActivity {
    private Toolbar toolbar;
    AlertDialog noDialogOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_call);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();




        Button btn =(Button)findViewById(R.id.btnTakingOrder);


        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Call.this, Order.class));
            }
        });



        //SpannableString s = new SpannableString(getIntent().getStringExtra("CUSTOMER_NAME"));
        //s.setSpan(new TypefaceSpan("MONOSPACE"), 0, s.length(),
        //                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ab.setTitle(getIntent().getStringExtra("CUSTOMER_NAME"));

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);


        Button btnNo = (Button)findViewById(R.id.btnNoOrder);
        btnNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final CharSequence[] items = {" Tutup ", " Banjir ", " Stock Masih ada ", " Wisata "};


                AlertDialog.Builder builder = new AlertDialog.Builder(Call.this);
                builder.setTitle("Please Choose one for reason");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {


                        switch (item) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;

                        }
                        noDialogOrder.dismiss();
                    }
                });
                noDialogOrder = builder.create();
                noDialogOrder.show();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
