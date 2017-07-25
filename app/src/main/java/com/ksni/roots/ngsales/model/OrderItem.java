package com.ksni.roots.ngsales.model;


import android.util.Log;

import com.ksni.roots.ngsales.Config;
import com.ksni.roots.ngsales.util.Helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by #roots on 07/08/2015.
 */
public class OrderItem implements Cloneable{
    public String productId;
    public String division;
    public String productName;
    public String uom;
    public boolean ext;
    public String brand;
    public String reasonReturId="0";
    public String reasonReturName;
    public int qty = 0;
    public int id = 0;
    public boolean regularOrder = true;
    public double price = 0;
    public double disc = 0;
    public String itemType="";
    public int refItem=0;

    public String uomLarge="";
    public String uomMedium="";
    public String uomSmall="";

    public String isPercentIPT="1";
    public String isIPT="0";

    public double regularDiscount = 0;
    public double extraDiscount = 0;
    public double specialDiscount= 0;

    public int largeToSmall = 0;
    public int mediumToSmall = 0;
    public int lastQty = 0;
    public String lastUom;
    public int stockQty = 0;
    public String stockUom;
    public int suggestQty = 0;
    public String suggestUom;
    public String ref_cust_template = "";


    public OrderItem clone() throws CloneNotSupportedException {
        return (OrderItem) super.clone();
    }

    public double getSubTotal(){
        return qty * price;
    }

    public double getTotalDiscount(){
        double jumlah = qty * price;
        //double jumlahDisc = 0;
        //double totSpcDisc = 0

        //double totRegDisc = jumlah * ( regularDiscount / 100 );

        double totExtDisc = 0;
        double totExtSpc = 0;


        double xExt = 0;
        double xExtVal = 0;

        double xSpc = 0;
        double xSpcVal = 0;

        double xReg = ( regularDiscount / 100 ) * jumlah;
        double xRegVal = jumlah - xReg;

        if (isIPT.equals("1") && ext   ){
            if (isPercentIPT.equals("1")){
                xExt = ( extraDiscount / 100 ) * xRegVal;
                xExtVal = xRegVal - xExt;
            }else{
                xExt = extraDiscount;// * qty;
                xExtVal = xRegVal - xExt;
            }
        }else{
            xExt = ( extraDiscount / 100) * xRegVal;
            xExtVal = xRegVal - xExt;
        }


        if (isIPT.equals("1") && !ext   ){
            if (isPercentIPT.equals("1")){
                xSpc = ( specialDiscount / 100 ) * xExtVal;
                xSpcVal = xExtVal - xSpc;
            }else{
                xSpc = specialDiscount;// * qty;
                xSpcVal = xExtVal - xSpc;
            }
        }else{
            xSpc = ( specialDiscount / 100 ) * xExtVal;
            xSpcVal = xExtVal - xSpc;
        }

        return  xReg + xExt + xSpc;

        /*

        if (isIPT.equals("1")) {
                if (!ext){ // special
                    totExtDisc =  ( extraDiscount / 100 ) *   ( jumlah -   ( jumlah * ( regularDiscount / 100 ) ) );
                    if (isPercentIPT.equals("1")) {
                        // percent
                        totSpcDisc = ( jumlah - ( totRegDisc + totExtDisc ) ) * ( specialDiscount / 100 );
                    } else {
                        // value
                        totSpcDisc = ( jumlah - ( totRegDisc + totExtDisc ) ) - specialDiscount;
                    }

                }else {
                    if (isPercentIPT.equals("1")) {
                        // percent
                        totExtDisc = (extraDiscount / 100) * (jumlah - (jumlah * (regularDiscount / 100)));
                    } else {
                        // value
                        totExtDisc = (extraDiscount / 100) * (jumlah - (jumlah * (regularDiscount / 100)));
                    }
                    totSpcDisc = ( jumlah - ( totRegDisc + totExtDisc ) ) * ( specialDiscount / 100 );

                }

        }else{
            totExtDisc =  ( extraDiscount / 100 ) *   ( jumlah -   ( jumlah * ( regularDiscount / 100 ) ) );
            totSpcDisc = ( jumlah - ( totRegDisc + totExtDisc ) ) * ( specialDiscount / 100 );
        }




        return totRegDisc + totExtDisc + totSpcDisc;
        */

    }


    /*public double getTotalExIPT(){
        double jumlah = qty * price;
        double jumlahDisc = 0;
        double totExtDisc = 0;

        double totRegDisc = jumlah * ( regularDiscount / 100 );

        // exclude ipt
        if(!isIPT.equals("1")) {
            totExtDisc =  ( extraDiscount / 100 ) *   ( jumlah -   ( jumlah * ( regularDiscount / 100 ) ) );
        }

        double totSpcDisc = ( jumlah - ( totRegDisc + totExtDisc ) ) * ( specialDiscount / 100 );
        jumlahDisc = totRegDisc + totExtDisc + totSpcDisc;

        return   jumlah - jumlahDisc;
    }*/


    public double getTotal(){

        double jumlah = qty * price;
        double totExtDisc = 0;
        double totExtSpc = 0;


        double xExt = 0;
        double xExtVal = 0;

        double xSpc = 0;
        double xSpcVal = 0;

        double xReg = ( regularDiscount / 100 ) * jumlah;
        double xRegVal = jumlah - xReg;

        if (isIPT.equals("1") && ext   ){
            if (isPercentIPT.equals("1")){
                xExt = ( extraDiscount / 100) * xRegVal;
                xExtVal = xRegVal - xExt;
            }else{
                xExt = extraDiscount;// * qty;
                xExtVal = xRegVal - xExt;
            }
        }else{
            xExt = ( extraDiscount / 100) * xRegVal;
            xExtVal = xRegVal - xExt;
        }

        if (isIPT.equals("1") && !ext   ){
            if (isPercentIPT.equals("1")){
                xSpc = ( specialDiscount / 100 ) * xExtVal;
                xSpcVal = xExtVal - xSpc;
            }else{
                xSpc = specialDiscount;// * qty;
                xSpcVal = xExtVal - xSpc;
            }
        }else{
            xSpc = ( specialDiscount / 100 ) * xExtVal;
            xSpcVal = xExtVal - xSpc;
        }


        //Log.e("total",String.valueOf(jumlah));
        //Log.e("xRegVal",String.valueOf(xRegVal));
        //Log.e("xExtVal",String.valueOf(xExtVal));
        //Log.e("xSpcVal",String.valueOf(xSpcVal));

        return xSpcVal;
        //return jumlah - xSpcVal;

/*        double jumlah = qty * price;
        double jumlahDisc = 0;
        double totExtDisc = 0;

        double totRegDisc = jumlah * ( regularDiscount / 100 );


        if(isIPT.equals("1")) {
            if(isPercentIPT.equals("1")){
                totExtDisc =  ( extraDiscount / 100 ) *   ( jumlah -   ( jumlah * ( regularDiscount / 100 ) ) );
            }else{
                totExtDisc =  extraDiscount;// -  ( jumlah - ( jumlah * ( regularDiscount / 100 ) ) );
            }
        }else{
            totExtDisc =  ( extraDiscount / 100 ) *   ( jumlah -   ( jumlah * ( regularDiscount / 100 ) ) );
        }

        double totSpcDisc = ( jumlah - ( totRegDisc + totExtDisc ) ) * ( specialDiscount / 100 );
        jumlahDisc = totRegDisc + totExtDisc + totSpcDisc;

        return   jumlah - jumlahDisc;
        */
    }

    public String toString(){

        if (regularOrder) {
            return "Last " + lastQty + " " + lastUom + ", " +
                    "Stck " + stockQty + " " + stockUom + ", " +
                    "Sugg " + suggestQty + " " + suggestUom;
        }else{
            if(reasonReturId.equals("0")){
                return "No Reason";
            }else{
                return "Reason: "+reasonReturName;
            }
        }

    }

    public String getInfo(){
        List<String> infolist=new ArrayList<String>();


        if (regularDiscount>0){
            infolist.add("reg(" + Helper.getFormatCurrencyWithDigit(regularDiscount) + "%)");
        }

        if (extraDiscount>0){

            if(isIPT.equals("1") && ext) {
                if (isPercentIPT.equals("1")) {
                    infolist.add("ext(" + Helper.getFormatCurrencyWithDigit(extraDiscount) + "%)");
                } else {
                    infolist.add("ext(" + Helper.getFormatCurrencyWithDigit(extraDiscount) + ")");
                }
            }else{
                infolist.add("ext(" + Helper.getFormatCurrencyWithDigit(extraDiscount) + "%)");
            }

        }

        if (specialDiscount>0){
            if(isIPT.equals("1") && !ext) {
                if (isPercentIPT.equals("1")) {
                    infolist.add("spc(" + Helper.getFormatCurrencyWithDigit(specialDiscount) + "%)");
                } else {
                    infolist.add("spc(" + Helper.getFormatCurrencyWithDigit(specialDiscount) + ")");
                }
            }else{
                infolist.add("spc(" + Helper.getFormatCurrencyWithDigit(specialDiscount) + "%)");
            }
        }


        String infox = "";

        for(int i=0;i<infolist.size();i++){
            if (i==infolist.size()-1){
                infox+=infolist.get(i).toString();
            }
            else{
                infox+=infolist.get(i).toString()+",";
            }
        }

        if (infox!=""){
            //infox = "@" + Helper.getFormatCurrencyWithDigit(price) +" "+infox+ "\nNet "+Helper.getFormatCurrencyWithDigit(getTotal());
        }
        else{
            //infox = "@" + Helper.getFormatCurrencyWithDigit(price)+ "\nNet "+Helper.getFormatCurrencyWithDigit(getTotal());
        }

        //production
        infox = Helper.getFormatCurrencyWithDigit(getTotal());
        return infox;
        //production

        /*
        // QA
        if(isPercentIPT.equals("1")) {
            return "@" + Helper.getFormatCurrency(price) +
                    " reg(" + Helper.getFormatCurrency(regularDiscount) + "%)," +
                    " ext(" + Helper.getFormatCurrency(extraDiscount) + "%)," +
                    " spc(" + Helper.getFormatCurrency(specialDiscount) + "%) = " +
                    Helper.getFormatCurrencyWithDigit(getTotal());
        }else{
            return "@" + Helper.getFormatCurrency(price) +
                    " reg(" + Helper.getFormatCurrency(regularDiscount) + "%)," +
                    " ext(" + Helper.getFormatCurrency(extraDiscount) + ")," +
                    " spc(" + Helper.getFormatCurrency(specialDiscount) + ") = " +
                    Helper.getFormatCurrencyWithDigit(getTotal());

        }
        // QA
        */


    }

}
// ----------------------------------------------------------------------------------
//    Note :	Regular Discount Calculate from :  Qty x Price x % Reg.Disc
//    Extra Discount Calculate from : (Qty x Price ) - Regular Discount
//    Special Discount Calculate from : (( Qty x Price ) - (Regular Discount + Extra Discount)) x % Spc Disc
// ----------------------------------------------------------------------------------
