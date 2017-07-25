package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 08/08/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class ReportSummaryByProductAdapter extends ArrayAdapter<ReportSummaryByProductStruct> {
    Context mContext;
    int layoutResourceId;
    List<ReportSummaryByProductStruct> data,dataFilter ;
    ProductFilter filter ;
    Boolean flagQty;
    Boolean flagTotal;


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new ProductFilter();
        }
        return filter;
    }

    public ReportSummaryByProductAdapter(Context mContext, int layoutResourceId, List<ReportSummaryByProductStruct> data, Boolean flag, Boolean flag2) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<ReportSummaryByProductStruct>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<ReportSummaryByProductStruct>();
        this.dataFilter.addAll(data);

        this.flagTotal = flag;
        this.flagQty = flag2;

    }

    private class ViewProduct {
        TextView product_number;
        TextView product_name;
        LinearLayout llDetail;
        TextView crt;
        TextView box;
        TextView pcs;
        TextView qty;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewProduct vCust = null;
        if(convertView==null){

            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            vCust = new ViewProduct();
            vCust.product_number=(TextView) convertView.findViewById(R.id.tProductNumber);
            vCust.product_name=(TextView) convertView.findViewById(R.id.tProductName);
            vCust.llDetail = (LinearLayout) convertView.findViewById(R.id.linearLayoutDetail);
            vCust.crt=(TextView) convertView.findViewById(R.id.textViewCRT);
            vCust.box=(TextView) convertView.findViewById(R.id.textViewBOX);
            vCust.pcs=(TextView) convertView.findViewById(R.id.textViewPCS);
            vCust.qty=(TextView) convertView.findViewById(R.id.tQty);
            convertView.setTag(vCust);

        }
        else{
            vCust = (ViewProduct) convertView.getTag();
        }

        ReportSummaryByProductStruct objectItem = dataFilter.get(position);

        vCust.product_number.setText(objectItem.product_id);
        vCust.product_name.setText(objectItem.product_name + " - " + objectItem.product_id);

        if (!flagTotal) {
            if(flagQty) {
                vCust.crt.setText(Helper.getFormatCurrency(objectItem.crt));
                vCust.box.setText(Helper.getFormatCurrency(objectItem.box));
                vCust.pcs.setText(Helper.getFormatCurrency(objectItem.pcs));
                vCust.qty.setText(Helper.getFormatCurrency(objectItem.qty) + " PCS");
            } else {
                vCust.llDetail.setVisibility(View.GONE);
                vCust.qty.setText("Rp " + Helper.getFormatCurrency(objectItem.qty));
            }

        } else {
            vCust.llDetail.setVisibility(View.GONE);
            vCust.qty.setText(Helper.getFormatCurrency(objectItem.qty));
        }



        return convertView;

    }

    private class ProductFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<ReportSummaryByProductStruct> filteredItems = new ArrayList<ReportSummaryByProductStruct>();

                for(int i = 0, l = data.size(); i < l; i++)
                {
                    ReportSummaryByProductStruct cp = data.get(i);
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

            dataFilter = (ArrayList<ReportSummaryByProductStruct>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = dataFilter.size(); i < l; i++)
                add(dataFilter.get(i));
            notifyDataSetInvalidated();
        }
    }

}
