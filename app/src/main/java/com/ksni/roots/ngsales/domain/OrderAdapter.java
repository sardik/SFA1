package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 08/08/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.OrderItem;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<OrderItem> {
    Context mContext;
    int layoutResourceId;
    List<OrderItem> data,dataFilter ;
    OrderItemFilter filter ;
    TextView txtTotal;


    public int getAutoId(){
        int id;
           if (dataFilter.size() == 0)
               id = 1;
            else{
               OrderItem itm = dataFilter.get(dataFilter.size()-1);
               id = itm.id + 1;
           }

        return id;

    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new OrderItemFilter();
        }
        return filter;
    }

    public OrderAdapter(Context mContext, int layoutResourceId, List<OrderItem> data,TextView txtTotal) {

        super(mContext, layoutResourceId, data);
        this.txtTotal = txtTotal;



        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<OrderItem>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<OrderItem>();
        this.dataFilter.addAll(data);
        loadTotal();
    }

    private class ViewOrderItem{
        TextView sku_number;
        TextView sku_name;
        TextView qty;
        TextView uom;
        TextView price;
        TextView info;
        TextView info2;
        TextView free;
        ImageView img;
    }

    @Override
    public int getCount() {
        // Number of times getView method call depends upon gridValues.length
        return dataFilter.size();
    }

    public boolean isList() {
        double tot = 0;
        for(OrderItem ord:dataFilter){
            tot = tot + ord.qty;
        }

        if (tot>0)
            return true;
        else
            return false;

    }

    public boolean isStock() {
        int tot = 0;
        for(OrderItem ord:dataFilter){
            tot = tot + ord.stockQty;
        }

        if (tot>0)
            return true;
        else
            return false;
    }

    @Override
    public OrderItem getItem(int position) {

        return dataFilter.get(position);
    }

    @Override
    public void add(OrderItem obj) {
        super.add(obj);
        dataFilter.add(obj);
        data.add(obj);
        loadTotal();
    }
    @Override
    public void remove(OrderItem obj) {
        super.remove(obj);
        dataFilter.remove(obj);
        data.remove(obj);
        loadTotal();
    }

    public void loadTotal(){

        if (txtTotal!=null) {
            txtTotal.setText(Helper.getFormatCurrencyWithDigit(getTotal()));
        }

    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public double getTotalQty(){
        double tot = 0;
        for(OrderItem ord:data){
            tot += ord.qty;
        }
        return tot;
    }

    public double getTotal(){
        double tot = 0;
        for(OrderItem ord:data){
            tot += ord.getTotal();
        }
        return tot;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewOrderItem vOrd = null;
        if(convertView==null){

            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            //convertView = inflater.inflate(layoutResourceId, parent, false);

            LayoutInflater inflater = LayoutInflater.from((Activity) mContext);
            convertView = inflater.inflate(layoutResourceId, null);


            vOrd= new ViewOrderItem();
            vOrd.sku_number =(TextView) convertView.findViewById(R.id.tProductNumber);
            vOrd.sku_name   =(TextView) convertView.findViewById(R.id.tProductName);
            vOrd.qty        =(TextView) convertView.findViewById(R.id.tQty);
            vOrd.uom        =(TextView) convertView.findViewById(R.id.tUom);
            vOrd.price      =(TextView) convertView.findViewById(R.id.tPrice);
            vOrd.info       =(TextView) convertView.findViewById(R.id.tInfo);
            vOrd.info2       =(TextView) convertView.findViewById(R.id.tInfo2);
            vOrd.free       =(TextView) convertView.findViewById(R.id.tFree);


            vOrd.img=(ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(vOrd);

        }
        else{
            vOrd= (ViewOrderItem) convertView.getTag();
        }


        OrderItem objectItem = dataFilter.get(position);

        if (objectItem.itemType.equals("N"))
            vOrd.free.setVisibility(View.GONE);
        else if (objectItem.itemType.equals("F"))
            vOrd.free.setVisibility(View.VISIBLE);





        vOrd.sku_number.setText(objectItem.productId);
        vOrd.sku_name.setText(objectItem.productName + " " +objectItem.productId);
        vOrd.qty.setText( String.valueOf(objectItem.qty));
        vOrd.uom.setText(objectItem.uom);
        vOrd.price.setText(Helper.getFormatCurrencyWithDigit(objectItem.price));
        vOrd.info.setText(objectItem.toString());
        vOrd.info2.setText(objectItem.getInfo());

        switch (objectItem.brand){

            case "11":vOrd.img.setImageResource(R.drawable.cheesewafer);break;
            case "12":vOrd.img.setImageResource(R.drawable.ahh);break;
            case "13":vOrd.img.setImageResource(R.drawable.siip);break;
            case "14":vOrd.img.setImageResource(R.drawable.rolls);break;
            case "15":vOrd.img.setImageResource(R.drawable.pasta);break;
            case "16":vOrd.img.setImageResource(R.drawable.selimut);break;
            case "17":vOrd.img.setImageResource(R.drawable.delis);break;
            case "18":vOrd.img.setImageResource(R.drawable.logo_brand_richoco_wafer);break;
            case "19":vOrd.img.setImageResource(R.drawable.richoco_siip);break;
            case "20":vOrd.img.setImageResource(R.drawable.richoco_bisvit);break;
            case "34":vOrd.img.setImageResource(R.drawable.nextar);break;
            default :vOrd.img.setImageResource(R.drawable.no_logo);break;

            /* old
            //case "ahh":
            case "21":
                vOrd.img.setImageResource(R.drawable.ahh);
                break;
            //case "delis":
            case "99":
                vOrd.img.setImageResource(R.drawable.delis);
                break;
            //case "rolls":
            case "11":
                vOrd.img.setImageResource(R.drawable.rolls);
                break;
            //case "cheeseWafer":
            //  vOrd.img.setImageResource(R.drawable.cheesewafer);
            //break;
            //case "richeese":
            //   vOrd.img.setImageResource(R.drawable.richeese);
            //  break;
            //case "selimut":
            //  vOrd.img.setImageResource(R.drawable.selimut);
            // break;
            //case "siip":
            case "24":
                vOrd.img.setImageResource(R.drawable.siip);
                break;
            //case "pasta":
            //  vOrd.img.setImageResource(R.drawable.pasta);
            // break;
            //case "richoco":
            case "12":
                vOrd.img.setImageResource(R.drawable.richoco);
                break;
                */


        }

        loadTotal();

        return convertView;

    }


    private class OrderItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<OrderItem> filteredItems = new ArrayList<OrderItem>();

                for(int i = 0, l = data.size(); i < l; i++)
                {
                    OrderItem cp = data.get(i);
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

            dataFilter = (ArrayList<OrderItem>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = dataFilter.size(); i < l; i++)
                add(dataFilter.get(i));
            notifyDataSetInvalidated();
            loadTotal();
        }
    }

}
