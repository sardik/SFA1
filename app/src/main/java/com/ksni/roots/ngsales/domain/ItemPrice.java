package com.ksni.roots.ngsales.domain;

import com.ksni.roots.ngsales.model.OrderItem;

/**
 * Created by #roots on 04/01/2016.
 */
public class ItemPrice {
    private int qty = 0;
    private double price = 0;
    private OrderItem orderItem;

    public ItemPrice(int qty,double price, OrderItem orderItem){
        this.qty = qty;
        this.price = price;
        this.orderItem = orderItem;
    }
    public void setQty(int value){
        qty = value;
    }

    public int getQty(){
        return qty;
    }

    public void setPrice(double value){
        price = value;
    }

    public double getPrice(){
        return price;
    }

    public OrderItem getOrderItem(){
        return orderItem;
    }


}
