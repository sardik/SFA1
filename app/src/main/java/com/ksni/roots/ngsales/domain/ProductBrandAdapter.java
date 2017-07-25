package com.ksni.roots.ngsales.domain;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.ProductBrands;

import java.util.ArrayList;

/**
 * Created by User on 9/20/2016.
 */
public class ProductBrandAdapter extends RecyclerView.Adapter<ProductBrandAdapter.ViewHolder> {

    ArrayList<ProductBrands> aListProductBrand = new ArrayList<>();
    Context context;
    int resource;
    ViewHolder viewHolder;
    String lastActivity; //Call Plan (AddOrder) atau Product

    OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(String idBrand, String lastActivity);
    }

    public void SetOnItemClickListener (OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBrand;
        TextView tvBrandName;
        ProductBrands objBindingBrand;
        Toolbar toolbar;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.ivBrand = (ImageView) itemView.findViewById(R.id.imageViewProductBrand);
            this.tvBrandName = (TextView) itemView.findViewById(R.id.textViewProductBrandName);

        }

    }

    //Construct 1 (Bisa pake yang ini)
//    public ProductBrandAdapter(Context context, int resource, ArrayList<ProductBrands> objects, String lastActivity, OnItemClickListener listener){
//
//        this.mItemClickListener = listener;
//        this.context = context;
//        this.resource = resource;
//        this.aListProductBrand = objects;
//        this.lastActivity = lastActivity;
//    }

    //Construct 2 (Bisa pake yang ini)
    public ProductBrandAdapter(Context context, int resource, ArrayList<ProductBrands> objects, String lastActivity){

//        this.mItemClickListener = listener;
        this.context = context;
        this.resource = resource;
        this.aListProductBrand = objects;
        this.lastActivity = lastActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.ui_product_brand_item, parent, false);

        viewHolder = new ViewHolder(convertView);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final ProductBrands objItem = aListProductBrand.get(position);

//        holder.objBindingBrand = objItem;

        if (objItem.getBrandsId().equals("000")) {
            holder.ivBrand.setImageResource(R.drawable.richeese);
        } else if (objItem.getBrandsId().equals("110")) {
            holder.ivBrand.setImageResource(R.drawable.brand_richeese_wafer);
        } else if (objItem.getBrandsId().equals("120")) {
            holder.ivBrand.setImageResource(R.drawable.brand_richoco_wafer);
        } else if (objItem.getBrandsId().equals("210")) {
            holder.ivBrand.setImageResource(R.drawable.brand_richeese_snack);
        } else if (objItem.getBrandsId().equals("220")) {
            holder.ivBrand.setImageResource(R.drawable.richoco);
        } else if (objItem.getBrandsId().equals("230")) {
            holder.ivBrand.setImageResource(R.drawable.richeese);
        } else if (objItem.getBrandsId().equals("240")) {
            holder.ivBrand.setImageResource(R.drawable.brand_siip);
        }  else if (objItem.getBrandsId().equals("260")) {
            holder.ivBrand.setImageResource(R.drawable.richeese);
        } else if (objItem.getBrandsId().equals("310")) {
            holder.ivBrand.setImageResource(R.drawable.brand_richeese_biscuit);
        } else if (objItem.getBrandsId().equals("340")) {
                holder.ivBrand.setImageResource(R.drawable.brand_nextar);
        } else if (objItem.getBrandsId().equals("420")) {
            holder.ivBrand.setImageResource(R.drawable.richeese);
        } else if (objItem.getBrandsId().equals("510")) {
            holder.ivBrand.setImageResource(R.drawable.brand_richeese_pastakeju);
        } else if (objItem.getBrandsId().equals("520")) {
            holder.ivBrand.setImageResource(R.drawable.brand_richoco_pastacoklat);
        } else if (objItem.getBrandsId().equals("710")) {
            holder.ivBrand.setImageResource(R.drawable.richeese);
        } else if (objItem.getBrandsId().equals("810")) {
            holder.ivBrand.setImageResource(R.drawable.brand_simba);
        }

        holder.tvBrandName.setText(aListProductBrand.get(position).getBrandsName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastActivity == "AddOrder") {

                    mItemClickListener.onItemClick(objItem.getBrandsId(), lastActivity );

                } else {
//                    Toast.makeText(context, "Item : " + objItem.getBrandsId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, ProductDataByBrand.class);
                    intent.putExtra("id_brand", objItem.getBrandsId());
                    intent.putExtra("last_activity", lastActivity);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return aListProductBrand.size();
    }



}
