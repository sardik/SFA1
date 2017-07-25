package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 08/08/2015.
 */
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Filter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Customer;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.util.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallPlanAdapter extends ArrayAdapter<CustomerCall> {
    Context mContext;
    Uri imageToUploadUri = null;
    Fragment frag = null;
    int layoutResourceId;
    List<CustomerCall> data,dataFilter ;
    CallPlanFilter filter ;
    private String customerNumb = "";
    private String customerNumbLeft = "";
    private String customerNumbRight = "";
    private CircleImageView cim = null;
    private int lokasi;


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new CallPlanFilter();
        }
        return filter;
    }

    public CallPlanAdapter(Context mContext, int layoutResourceId,List<CustomerCall> data,Fragment ff) {

        super(mContext, layoutResourceId, data);
        frag = ff;
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<CustomerCall>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<CustomerCall>();
        this.dataFilter.addAll(data);
    }

    private class ViewCustomerPlan {
        TextView cust_number;
        TextView cust_numbLeft;
        TextView cust_numbRight;
        TextView cust_name;
        TextView cust_addr;
        TextView cust_city;
        TextView status;
        TextView statusX;
        TextView alias;
        TextView jarak;
        TextView call_status;
        TextView description;
        CircleImageView civ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context parentContect = parent.getContext();
        ViewCustomerPlan vCust = null;
        if(convertView==null){

            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            vCust = new ViewCustomerPlan();
            vCust.cust_number=(TextView) convertView.findViewById(R.id.tCustomerNumber);
            vCust.cust_name=(TextView) convertView.findViewById(R.id.tCustomerName);
            vCust.cust_numbLeft = (TextView) convertView.findViewById(R.id.tCustomerNumbLeft);
            vCust.cust_numbRight = (TextView) convertView.findViewById(R.id.tCustomerNumbRight);
            vCust.cust_addr=(TextView) convertView.findViewById(R.id.tCustomerAddress);
            vCust.cust_city=(TextView) convertView.findViewById(R.id.tCustomerCity);
            vCust.status=(TextView) convertView.findViewById(R.id.tStatus);
            vCust.statusX=(TextView) convertView.findViewById(R.id.tStatusX);
            vCust.alias=(TextView) convertView.findViewById(R.id.tCustomerAlias);
            vCust.call_status=(TextView) convertView.findViewById(R.id.tCallStatus);
            vCust.description=(TextView) convertView.findViewById(R.id.tDescription);
            vCust.jarak=(TextView) convertView.findViewById(R.id.tJarak);
            vCust.civ =(CircleImageView)convertView.findViewById(R.id.imageView4);

            convertView.setTag(vCust);
        }
        else{
            vCust = (ViewCustomerPlan) convertView.getTag();
        }

        final CustomerCall objectItem = dataFilter.get(position);

        customerNumb = objectItem.getCustomerNumber(); //Get 9/10digit CustomerNumber
        if (customerNumb.length() < 10) {
            customerNumbLeft = customerNumb.substring(0, 4); //Get 4digit left of CustomerNumber
            customerNumbRight = customerNumb.substring(4, customerNumb.length()); //Get 5digit right of CustomerNumber
        } else if (customerNumb.length() == 10) {
            customerNumbLeft = customerNumb.substring(0, 4); //Get 4digit left of CustomerNumber
            customerNumbRight = customerNumb.substring(4, 10); //Get 6digit right of CustomerNumber
        } else if (customerNumb.length() > 10) {
            customerNumbLeft = customerNumb.substring(0, 4); //Get 4digit left of CustomerNumber
            customerNumbRight = customerNumb.substring(4, customerNumb.length()); //Get 10digit right of CustomerNumber
            customerNumbRight = customerNumbRight + " (NEW NOO)";
        }


        vCust.cust_number.setText(objectItem.getCustomerNumber());
        vCust.cust_name.setText(objectItem.getCustomerName());
        vCust.cust_numbLeft.setText(customerNumbLeft + "-"); vCust.cust_numbLeft.setTextColor(Color.BLACK);
        vCust.cust_numbRight.setText(customerNumbRight); vCust.cust_numbRight.setTextColor(getContext().getResources().getColor(R.color.item_color_prim));
        vCust.cust_addr.setText(objectItem.getAddress());
        vCust.cust_city.setText(objectItem.getCity());
        vCust.status.setText(objectItem.getStatus());
        vCust.alias.setText(objectItem.getAlias());
        vCust.call_status.setText(objectItem.getCallStatus());

        if (objectItem.getJarak()==0){
            vCust.jarak.setVisibility(View.GONE);
        }
        else {
            vCust.jarak.setVisibility(View.VISIBLE);
            if (objectItem.getJarak() > 1000)
                vCust.jarak.setText(Helper.getFormatCurrencyWithDigit(objectItem.getJarak() / 1000) + " "+Helper.getStrResource(mContext,R.string.common_text_kilometers));
            else
                vCust.jarak.setText(Helper.getFormatCurrency(objectItem.getJarak()) + " "+Helper.getStrResource(mContext,R.string.common_text_meters));
        }

        String xorder = "";
        if (objectItem.getOrderId()!=-1){
            xorder =Helper.getStrResource(mContext,R.string.common_text_order_number)+" :"+String.valueOf(objectItem.getOrderId())+"\n";
        }

        /*

        if (objectItem.getStatus().equals(CustomerCall.VISITED)){
                vCust.description.setText( "End Call "+Helper.getStrTime(objectItem.getEndtTime()) +"\n" + Helper.getInfoTime(objectItem.getStartTime(),objectItem.getEndtTime(),objectItem.getDuration()) ) ;
                vCust.description.setVisibility(View.VISIBLE);
            }
        else  if (objectItem.getStatus().equals(CustomerCall.PAUSED)) {
            vCust.description.setText(xorder + "Start Call " + Helper.getStrTime(objectItem.getStartTime()) + "\n" +
                                               "Last Paused " + Helper.getStrTime(objectItem.getPauseTime()) + "\n" +
                                               Helper.getInfoTime(objectItem.getPauseTime(),objectItem.getResumeTime(),objectItem.getDuration()) ) ;
                    vCust.description.setVisibility(View.VISIBLE);
        } else if (objectItem.getStatus().equals(CustomerCall.VISIT)) {
            if (objectItem.getResumeTime()!=null) {
                vCust.description.setText(xorder + "Start Call " + Helper.getStrTime(objectItem.getStartTime()) + "\n" + Helper.getInfoTime(objectItem.getPauseTime(),objectItem.getResumeTime(),objectItem.getDuration()) ) ;
            }else{
                vCust.description.setText(xorder+"Start Call " + Helper.getStrTime(objectItem.getStartTime()));
            }
            vCust.description.setVisibility(View.VISIBLE);
        }else{
            vCust.description.setVisibility(View.GONE);
        }

        */

        if (objectItem.getStatus().equals(CustomerCall.VISITED)){
            if (Helper.getNullString(objectItem.getResumeTime()).length()>0){
                vCust.description.setText(Helper.getStrResource(mContext,R.string.common_text_start_call)+" " + Helper.getStrTime(objectItem.getStartTime()) +
                        "\n"+Helper.getStrResource(mContext,R.string.common_text_last_pause)+" " + Helper.getStrTime(objectItem.getPauseTime()) +
                        "\n"+Helper.getStrResource(mContext,R.string.common_text_last_resume)+ " " + Helper.getStrTime(objectItem.getResumeTime()) + "\n" +
                        Helper.getStrResource(mContext,R.string.common_text_pause_duration)+" " + String.valueOf(Helper.getInfoTimeFromSecond(objectItem.getDuration())) +
                        "\n"+Helper.getStrResource(mContext,R.string.common_text_visited)+" " + Helper.getStrTime(objectItem.getEndtTime()));
            }else {
                vCust.description.setText(Helper.getStrResource(mContext,R.string.common_text_start_call)+": " + Helper.getStrTime(objectItem.getStartTime())+
                                        "\n"+Helper.getStrResource(mContext,R.string.common_text_visited)+ " " + Helper.getStrTime(objectItem.getEndtTime()));
            }
            //vCust.description.setVisibility(View.VISIBLE);
        }else  if (objectItem.getStatus().equals(CustomerCall.PAUSED)) {
            if (Helper.getNullString(objectItem.getResumeTime()).length()>0) {
                vCust.description.setText(xorder + Helper.getStrResource(mContext,R.string.common_text_start_call)+" " + Helper.getStrTime(objectItem.getStartTime()) + "\n"+Helper.getStrResource(mContext,R.string.common_text_last_pause)+ " "+ Helper.getStrTime(objectItem.getPauseTime()) + "\n"+Helper.getStrResource(mContext,R.string.common_text_pause_duration)+" " + String.valueOf(Helper.getInfoTimeFromSecond(objectItem.getDuration())));
            }
            else
                vCust.description.setText(xorder + Helper.getStrResource(mContext,R.string.common_text_start_call)+" " + Helper.getStrTime(objectItem.getStartTime()) + "\n"+Helper.getStrResource(mContext,R.string.common_text_last_pause)+" " + Helper.getStrTime(objectItem.getPauseTime())) ;
            //vCust.description.setVisibility(View.VISIBLE);
        } else if (objectItem.getStatus().equals(CustomerCall.VISIT)) {
            if (Helper.getNullString(objectItem.getResumeTime()).length()>0) {
                vCust.description.setText(xorder+Helper.getStrResource(mContext,R.string.common_text_start_call)+" " + Helper.getStrTime(objectItem.getStartTime()) +
                        "\n"+Helper.getStrResource(mContext,R.string.common_text_last_pause)+" " + Helper.getStrTime(objectItem.getPauseTime()) +
                        "\n"+Helper.getStrResource(mContext,R.string.common_text_last_resume)+" " + Helper.getStrTime(objectItem.getResumeTime()) + "\n"+Helper.getStrResource(mContext,R.string.common_text_pause_duration)+" " + String.valueOf(Helper.getInfoTimeFromSecond(objectItem.getDuration())) );
            }else{
                vCust.description.setText(xorder+Helper.getStrResource(mContext,R.string.common_text_start_call)+" " + Helper.getStrTime(objectItem.getStartTime()));
            }
            //vCust.description.setVisibility(View.VISIBLE);
        }else{
            vCust.description.setText(Helper.getStrResource(mContext,R.string.common_text_unvisit));
            //vCust.description.setVisibility(View.GONE);
        }


        if (objectItem.getCallStatus().equals("1"))
            vCust.cust_name.setTextColor(Color.BLACK);
        else
            vCust.cust_name.setTextColor(Color.GRAY);

        if (objectItem.getStatus().equals(CustomerCall.NO_VISIT)) {
            vCust.status.setTextColor(Color.GRAY);
            vCust.statusX.setTextColor(Color.GRAY);
            vCust.statusX.setText(Helper.getStrResource(mContext,R.string.textview_call_unvisit_status_list_view));

        }
        else if (objectItem.getStatus().equals(CustomerCall.VISIT)){
            vCust.status.setTextColor(Color.parseColor("#22b24c"));
            vCust.statusX.setTextColor(Color.parseColor("#22b24c"));
            vCust.statusX.setText(Helper.getStrResource(mContext, R.string.textview_call_visit_status_list_view));
        }
        else if (objectItem.getStatus().equals(CustomerCall.PAUSED)) {
            vCust.status.setTextColor(Color.parseColor("#FF9933"));
            vCust.statusX.setTextColor(Color.parseColor("#FF9933"));
            vCust.statusX.setText(Helper.getStrResource(mContext, R.string.textview_call_pause_status_list_view));
        }
        else if (objectItem.getStatus().equals(CustomerCall.VISITED) ){
            vCust.status.setTextColor(Color.RED);
            vCust.statusX.setTextColor(Color.RED);
            vCust.statusX.setText(Helper.getStrResource(mContext, R.string.textview_call_visited_status_list_view));
        }
        else if (objectItem.getStatus().equals(CustomerCall.NOCALL) ){
            vCust.status.setTextColor(Color.MAGENTA);
            vCust.statusX.setTextColor(Color.MAGENTA);
            vCust.statusX.setText(Helper.getStrResource(mContext, R.string.textview_call_no_call_status_list_view));
        }


        if (objectItem.getImage()!=null){
            vCust.civ.setImageBitmap(objectItem.getImage());
        }else{
            vCust.civ.setImageResource(R.drawable.anonymous);
        }

        vCust.civ.setTag(vCust.description.getText().toString());
        vCust.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lokasi = position;
                final View view = v;

                cim = (CircleImageView) v;
                final CharSequence[] items = {Helper.getStrResource(mContext,R.string.common_text_visit_information), Helper.getStrResource(mContext,R.string.common_text_view_customer), Helper.getStrResource(mContext,R.string.common_text_change_picture)};
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(Helper.getStrResource(mContext,R.string.common_text_details));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item){
                            case 0:
                                Helper.msgbox(view.getContext(), view.getTag().toString(),Helper.getStrResource(mContext, R.string.common_msg_information));
                                break;
                            case 1:
                                Intent i = new Intent(mContext,CustomerInput.class);
                                i.putExtra("customer_id", objectItem.getCustomerNumber());
                                com.ksni.roots.ngsales.model.Customer cc = com.ksni.roots.ngsales.model.Customer.getCustomer(mContext, objectItem.getCustomerNumber());
                                i.putExtra("objCust",cc);
                                mContext.startActivity(i);
                                break;
                            case 2:
                                customerNumb = objectItem.getCustomerNumber();
                                /*
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                    frag.startActivityForResult(intent, 555);
                                }
                                */
                                imageToUploadUri = null;
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                                    File file = Helper.getOutputMediaPhotoFileCall(objectItem.getCustomerNumber());
                                    if (file.exists()) file.delete();
                                    takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    Uri fileUri = Uri.fromFile(file);
                                    imageToUploadUri = fileUri;
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                    frag.startActivityForResult(takePictureIntent, 555);
                                }

                                break;
                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        return convertView;

    }

    private class CallPlanFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<CustomerCall> filteredItems = new ArrayList<CustomerCall>();

                for(int i = 0, l = data.size(); i < l; i++)
                {
                    CustomerCall cp = data.get(i);
                    if(cp.toString().toLowerCase().contains(constraint))
                        filteredItems.add(cp);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = data;
                    result.count = data.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            dataFilter = (ArrayList<CustomerCall>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = dataFilter.size(); i < l; i++)
                add(dataFilter.get(i));
            notifyDataSetInvalidated();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

            if (requestCode==555){
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageToUploadUri.getPath());

                    if (bmp.getWidth()>bmp.getHeight()) bmp = Helper.rotateImage(bmp,90);

                    String data  =Helper.getStrResizePhotoBase64(bmp, 70,124 );
                    Customer.updatePictureProfile(mContext, customerNumb, data);

                    dataFilter.get(lokasi).setPicture(data);
                    Helper.deleteFile(imageToUploadUri.getPath());
                    imageToUploadUri = null;
                    notifyDataSetChanged();
                }
            }


    }
}
