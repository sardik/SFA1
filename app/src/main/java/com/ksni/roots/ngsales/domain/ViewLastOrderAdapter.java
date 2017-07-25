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
import com.ksni.roots.ngsales.model.OrderHead;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class ViewLastOrderAdapter extends ArrayAdapter<OrderHead> {
    Context mContext;
    int layoutResourceId;
    List<OrderHead> data,dataFilter ;
    CallPlanFilter filter ;


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new CallPlanFilter();
        }
        return filter;
    }

    public ViewLastOrderAdapter(Context mContext, int layoutResourceId, List<OrderHead> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<OrderHead>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<OrderHead>();
        this.dataFilter.addAll(data);
    }

    private class ViewCustomerPlan {
        TextView torder_id;
        TextView torder_date;
        TextView toutlet_id;
        TextView toutlet_name;
        TextView ttotal;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewCustomerPlan vCust = null;
        if(convertView==null){

            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            vCust = new ViewCustomerPlan();
            vCust.torder_id=(TextView) convertView.findViewById(R.id.tOrderId);
            vCust.torder_date=(TextView) convertView.findViewById(R.id.tOrderDate);
            vCust.toutlet_id=(TextView) convertView.findViewById(R.id.tCustomerNumber);
            vCust.toutlet_name=(TextView) convertView.findViewById(R.id.tCustomerName);
            vCust.ttotal=(TextView) convertView.findViewById(R.id.tTotal);
            convertView.setTag(vCust);

        }
        else{
            vCust = (ViewCustomerPlan) convertView.getTag();
        }


        OrderHead objectItem = dataFilter.get(position);


        vCust.torder_id.setText(objectItem.order_id);
        vCust.torder_date.setText(objectItem.order_date);
        vCust.toutlet_id.setText(objectItem.outlet_id);
        vCust.toutlet_name.setText(objectItem.outlet_name);

        vCust.ttotal.setText(Helper.getFormatCurrency(objectItem.grand_total));


        return convertView;

    }

    private class CallPlanFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<OrderHead> filteredItems = new ArrayList<OrderHead>();

                for(int i = 0, l = data.size(); i < l; i++)
                {
                    OrderHead cp = data.get(i);
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

            dataFilter = (ArrayList<OrderHead>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = dataFilter.size(); i < l; i++)
                add(dataFilter.get(i));
            notifyDataSetInvalidated();
        }
    }

}
