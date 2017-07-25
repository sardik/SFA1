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
import com.ksni.roots.ngsales.model.CustomerCall;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends ArrayAdapter<CustomerCall> {
    Context mContext;
    int layoutResourceId;
    List<CustomerCall> data,dataFilter ;
    CallPlanFilter filter ;


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new CallPlanFilter();
        }
        return filter;
    }

    public CustomerAdapter(Context mContext, int layoutResourceId, List<CustomerCall> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<CustomerCall>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<CustomerCall>();
        this.dataFilter.addAll(data);
    }

    private class ViewCustomerPlan {
        TextView cust_number;
        TextView cust_name;
        TextView cust_addr;
        TextView cust_city;
        TextView status;
        TextView alias;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewCustomerPlan vCust = null;
        if(convertView==null){

            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            vCust = new ViewCustomerPlan();
            vCust.cust_number=(TextView) convertView.findViewById(R.id.tCustomerNumber);
            vCust.cust_name=(TextView) convertView.findViewById(R.id.tCustomerName);
            vCust.cust_addr=(TextView) convertView.findViewById(R.id.tCustomerAddress);
            vCust.cust_city=(TextView) convertView.findViewById(R.id.tCustomerCity);
            vCust.status=(TextView) convertView.findViewById(R.id.tStatus);
            vCust.alias=(TextView) convertView.findViewById(R.id.tCustomerAlias);
            convertView.setTag(vCust);

        }
        else{
            vCust = (ViewCustomerPlan) convertView.getTag();
        }


        CustomerCall objectItem = dataFilter.get(position);


        vCust.cust_number.setText(objectItem.getCustomerNumber());
        vCust.cust_name.setText(objectItem.getCustomerName()+" "+objectItem.getCustomerNumber());
        vCust.cust_addr.setText(objectItem.getAddress());
        vCust.cust_city.setText(objectItem.getCity());
        vCust.status.setText(objectItem.getStatus());
        vCust.alias.setText(objectItem.getAlias());

        if (objectItem.getStatus().equals(CustomerCall.NO_VISIT))
            vCust.status.setTextColor(Color.GRAY);
        else if (objectItem.getStatus().equals(CustomerCall.VISIT))
            vCust.status.setTextColor(Color.parseColor("#22b24c"));
        else if (objectItem.getStatus().equals(CustomerCall.VISITED))
            vCust.status.setTextColor(Color.RED);


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

}
