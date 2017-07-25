package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 08/08/2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class ReportTargetAdapter extends ArrayAdapter<ReportTargetStruct> {
    Context mContext;
    int layoutResourceId;
    List<ReportTargetStruct> data,dataFilter ;
    ProductFilter filter ;


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new ProductFilter();
        }
        return filter;
    }

    public ReportTargetAdapter(Context mContext, int layoutResourceId, List<ReportTargetStruct> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<ReportTargetStruct>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<ReportTargetStruct>();
        this.dataFilter.addAll(data);
    }

    private class ViewProduct {
        TextView description;
        TextView target;
        TextView actual;
        TextView sisa;
        TextView persen;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewProduct vCust = null;
        if(convertView==null){

            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            vCust = new ViewProduct();
            vCust.description=(TextView) convertView.findViewById(R.id.tJudul);
            vCust.target=(TextView) convertView.findViewById(R.id.tValueTarget);
            vCust.actual=(TextView) convertView.findViewById(R.id.tValueActual);
            vCust.persen=(TextView) convertView.findViewById(R.id.tPencapaian);
            vCust.sisa=(TextView) convertView.findViewById(R.id.tBalance);
            convertView.setTag(vCust);

        }
        else{
            vCust = (ViewProduct) convertView.getTag();
        }


        ReportTargetStruct objectItem = dataFilter.get(position);


        vCust.description.setText(objectItem.description);
        if (objectItem.target==-1)
            vCust.target.setText("0");
        else
            vCust.target.setText(Helper.getFormatCurrency(objectItem.target));

        vCust.actual.setText(Helper.getFormatCurrency(objectItem.actual));


        if (objectItem.sisa.contains("Under"))
            vCust.sisa.setTextColor(Color.RED);
        else
            vCust.sisa.setTextColor(Color.parseColor("#125c00"));

        vCust.sisa.setText(objectItem.sisa);

        vCust.persen.setText(Helper.getFormatCurrencyWithDigit(objectItem.persen)+"%");

        return convertView;

    }

    private class ProductFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<ReportTargetStruct> filteredItems = new ArrayList<ReportTargetStruct>();

                for(int i = 0, l = data.size(); i < l; i++)
                {
                    ReportTargetStruct cp = data.get(i);
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

            dataFilter = (ArrayList<ReportTargetStruct>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = dataFilter.size(); i < l; i++)
                add(dataFilter.get(i));
            notifyDataSetInvalidated();
        }
    }

}
