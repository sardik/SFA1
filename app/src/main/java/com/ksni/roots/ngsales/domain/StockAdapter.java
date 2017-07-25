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
import android.widget.TextView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Stock;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends ArrayAdapter<Stock> {
    Context mContext;
    int layoutResourceId;
    List<Stock> data,dataFilter ;
    ProductFilter filter ;


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new ProductFilter();
        }
        return filter;
    }

    public StockAdapter(Context mContext, int layoutResourceId, List<Stock> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<Stock>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<Stock>();
        this.dataFilter.addAll(data);
    }

    private class ViewProduct {
        TextView product_number;
        TextView product_name;
        ImageView img;

        TextView qtyLarge;
        TextView qtyMedium;
        TextView qtySmall;

        TextView uomLarge;
        TextView uomMedium;
        TextView uomSmall;

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
            vCust.img=(ImageView) convertView.findViewById(R.id.list_image);


            vCust.qtyLarge=(TextView) convertView.findViewById(R.id.tQtyLarge);
            vCust.qtyMedium=(TextView) convertView.findViewById(R.id.tQtyMedium);
            vCust.qtySmall=(TextView) convertView.findViewById(R.id.tQtySmall);

            vCust.uomLarge=(TextView) convertView.findViewById(R.id.tUomLarge);
            vCust.uomMedium=(TextView) convertView.findViewById(R.id.tUomMedium);
            vCust.uomSmall=(TextView) convertView.findViewById(R.id.tUomSmall);



            convertView.setTag(vCust);

        }
        else{
            vCust = (ViewProduct) convertView.getTag();
        }


        Stock objectItem = dataFilter.get(position);


        vCust.product_number.setText(objectItem.product_id);
        vCust.product_name.setText(objectItem.description+" "+objectItem.product_id);



        vCust.qtyLarge.setText(Helper.getFormatCurrency(objectItem.getQtyLarge()));
        vCust.qtyMedium.setText(Helper.getFormatCurrency(objectItem.getQtyMedium()));
        vCust.qtySmall.setText(Helper.getFormatCurrency(objectItem.getQtySmall()));

        /*if (objectItem.getQtyLarge()==0)
            vCust.qtyLarge.setVisibility(View.GONE);
        else
            vCust.qtyLarge.setVisibility(View.VISIBLE);


        if (objectItem.getQtyMedium()==0)
            vCust.qtyMedium.setVisibility(View.GONE);
        else
            vCust.qtyMedium.setVisibility(View.VISIBLE);

        if (objectItem.getQtySmall()==0)
            vCust.qtySmall.setVisibility(View.GONE);
        else
            vCust.qtySmall.setVisibility(View.VISIBLE);
        */


        vCust.uomLarge.setText(objectItem.uomLarge);
        vCust.uomMedium.setText(objectItem.uomMedium);
        vCust.uomSmall.setText(objectItem.uomSmall);
        switch (objectItem.brand) {
            case "11":
                vCust.img.setImageResource(R.drawable.cheesewafer);
                break;
            case "12":
                vCust.img.setImageResource(R.drawable.ahh);
                break;
            case "13":
                vCust.img.setImageResource(R.drawable.siip);
                break;
            case "14":
                vCust.img.setImageResource(R.drawable.rolls);
                break;
            case "15":
                vCust.img.setImageResource(R.drawable.pasta);
                break;
            case "16":
                vCust.img.setImageResource(R.drawable.selimut);
                break;
            case "17":
                vCust.img.setImageResource(R.drawable.delis);
                break;
            case "18":
                vCust.img.setImageResource(R.drawable.logo_brand_richoco_wafer);
                break;
            case "19":
                vCust.img.setImageResource(R.drawable.richoco_siip);
                break;
            case "20":
                vCust.img.setImageResource(R.drawable.richoco_bisvit);
                break;
            case "34":vCust.img.setImageResource(R.drawable.nextar);break;
            default:
                vCust.img.setImageResource(R.drawable.no_logo);
                break;
        }

/*
        switch (objectItem.brand){
            //case "ahh":
            case "21":
                vCust.img.setImageResource(R.drawable.ahh);
                break;
            //case "delis":
            case "99":
                vCust.img.setImageResource(R.drawable.delis);
                break;
            //case "rolls":
            case "11":
                vCust.img.setImageResource(R.drawable.rolls);
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
                vCust.img.setImageResource(R.drawable.siip);
                break;
            //case "pasta":
            //  vOrd.img.setImageResource(R.drawable.pasta);
            // break;
            //case "richoco":
            case "12":
                vCust.img.setImageResource(R.drawable.richoco);
                break;
        }
*/
/*
        switch (objectItem.getBrand()){
            case "ahh":
                vCust.img.setImageResource(R.drawable.ahh);
                break;
            case "delis":
                vCust.img.setImageResource(R.drawable.delis);
                break;
            case "rolls":
                vCust.img.setImageResource(R.drawable.rolls);
                break;
            case "cheeseWafer":
                vCust.img.setImageResource(R.drawable.cheesewafer);
                break;
            case "richeese":
                vCust.img.setImageResource(R.drawable.richeese);
                break;
            case "selimut":
                vCust.img.setImageResource(R.drawable.selimut);
                break;
            case "siip":
                vCust.img.setImageResource(R.drawable.siip);
                break;
            case "pasta":
                vCust.img.setImageResource(R.drawable.pasta);
                break;
            case "richoco":
                vCust.img.setImageResource(R.drawable.richoco);
                break;


        }
*/

        //list_image

        return convertView;

    }

    private class ProductFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Stock> filteredItems = new ArrayList<Stock>();

                for(int i = 0, l = data.size(); i < l; i++)
                {
                    Stock cp = data.get(i);
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

            dataFilter = (ArrayList<Stock>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = dataFilter.size(); i < l; i++)
                add(dataFilter.get(i));
            notifyDataSetInvalidated();
        }
    }

}
