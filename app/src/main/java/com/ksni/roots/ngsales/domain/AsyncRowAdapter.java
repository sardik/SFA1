    package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 08/08/2015.
 */
    import android.graphics.Color;
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
    import com.ksni.roots.ngsales.model.HeaderStruct;

    import java.util.ArrayList;
    import java.util.List;

    public class AsyncRowAdapter extends ArrayAdapter<HeaderStruct> {
        Context mContext;
        int layoutResourceId;
        List<HeaderStruct> data,dataFilter ;
        AsycFilter filter ;


        @Override
        public Filter getFilter() {



            if (filter == null){
                filter  = new AsycFilter();
            }
            return filter;
        }

        public AsyncRowAdapter(Context mContext, int layoutResourceId,List<HeaderStruct> data) {

            super(mContext, layoutResourceId, data);

            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;

            this.data = new ArrayList<HeaderStruct>();
            this.data.addAll(data);

            this.dataFilter = new ArrayList<HeaderStruct>();
            this.dataFilter.addAll(data);
        }

        private class ViewHeader {
            TextView trs_id;
            TextView cust_name;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHeader vCust = null;
            if(convertView==null){

                //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layoutResourceId, parent, false);

                vCust = new ViewHeader();
                vCust.trs_id=(TextView) convertView.findViewById(R.id.tTrsId);
                vCust.cust_name=(TextView) convertView.findViewById(R.id.tCustomerName);
                convertView.setTag(vCust);

            }
            else{
                vCust = (ViewHeader) convertView.getTag();
            }


            HeaderStruct objectItem = dataFilter.get(position);


            vCust.trs_id.setText(String.valueOf(objectItem.order_id) );
            vCust.cust_name.setText(objectItem.outlet_name);

            return convertView;

        }

        private class AsycFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<HeaderStruct> filteredItems = new ArrayList<HeaderStruct>();

                    for(int i = 0, l = data.size(); i < l; i++)
                    {
                        HeaderStruct cp = data.get(i);
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

                dataFilter = (ArrayList<HeaderStruct>)results.values;
                notifyDataSetChanged();
                clear();
                for(int i = 0, l = dataFilter.size(); i < l; i++)
                    add(dataFilter.get(i));
                notifyDataSetInvalidated();
            }
        }

    }

