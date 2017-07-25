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
import com.ksni.roots.ngsales.model.Product;
import com.ksni.roots.ngsales.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    Context mContext;
    int layoutResourceId;
    List<Product> data,dataFilter ;
    ProductFilter filter ;
    private boolean stock = false;


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new ProductFilter();
        }
        return filter;
    }

    public ProductAdapter(Context mContext, int layoutResourceId, List<Product> data, boolean stk) {

        super(mContext, layoutResourceId, data);
        this.stock = stk ;
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;

        this.data = new ArrayList<Product>();
        this.data.addAll(data);

        this.dataFilter = new ArrayList<Product>();
        this.dataFilter.addAll(data);
    }

    private class ViewProduct {
        TextView product_number;
        TextView product_name;
        TextView price;
        TextView uom;
        TextView alias;
        ImageView img;
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
            vCust.uom=(TextView) convertView.findViewById(R.id.tUom);
            vCust.price=(TextView) convertView.findViewById(R.id.tPrice);
            vCust.alias=(TextView) convertView.findViewById(R.id.tAlias);
            vCust.img=(ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(vCust);
        }
        else{
            vCust = (ViewProduct) convertView.getTag();
        }


        Product objectItem = dataFilter.get(position);

        vCust.product_number.setText(objectItem.getProcutId()); vCust.product_number.setTextColor(getContext().getResources().getColor(R.color.item_color_prim));
        vCust.product_name.setText(objectItem.getProductName());
        vCust.uom.setText(objectItem.getUom());
        vCust.alias.setText(objectItem.getAlias());

        /*if(stock){
            vCust.price.setText("Price "+ Helper.getFormatCurrency(objectItem.getPrice()) + "\n" +
                                Helper.getFormatCurrency(objectItem.getQtyLarge())+" "+objectItem.getUomLarge() +", " +
                                Helper.getFormatCurrency(objectItem.getQtyMedium())+" "+objectItem.getUomMedium() +", " +
                                Helper.getFormatCurrency(objectItem.getQtySmall())+" "+objectItem.getUomSmall() );
        }else { */

            vCust.price.setText("Rp " + Helper.getFormatCurrency(objectItem.getPrice()) + " / " + objectItem.getUom());
            if (objectItem.getPrice() == 0)
                vCust.price.setVisibility(View.INVISIBLE);
            else
                vCust.price.setVisibility(View.VISIBLE);
        //}


        switch (objectItem.getBrand()) {

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
            /*old
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
        }*/

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

    private class  ProductFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Product> filteredItems = new ArrayList<Product>();

                for(int i = 0, l = data.size(); i < l; i++)
                {
                    Product cp = data.get(i);
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

            dataFilter = (ArrayList<Product>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = dataFilter.size(); i < l; i++)
                add(dataFilter.get(i));
            notifyDataSetInvalidated();
        }
    }

}
