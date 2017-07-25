package com.ksni.roots.ngsales.util;

/**
 * Created by #roots on 20/08/2015.
 */
public class UomConversion {
    private int conversionLarge;
    private int conversionMedium;
    private long medium;
    private long small;
    private long large;
    private long quan;

    public UomConversion(long quan, int pLarge2Small,int pMedium2Small){
        this.quan = quan;
        this.conversionLarge = pLarge2Small;
        this.conversionMedium = pMedium2Small;
        medium = large = small = 0;
    }


    public void fromSmall(){
        if (conversionLarge!=0 && conversionMedium!=0) {
            large = quan / conversionLarge;
            medium = (quan % conversionLarge) / conversionMedium;
            small = (quan % conversionLarge) % conversionMedium;
        }

    }

    public void fromMedium(){
        quan = quan * conversionMedium;
        fromSmall();
    }

    public void large2Small(){
        medium = 0;
        small = quan * conversionLarge;
        large = 0;
    }


    public void large2Medium(){
        small = 0;
        medium = quan * conversionMedium;
        large = 0;
    }

    public void medium2Small(){
        int divMedium  = conversionLarge / conversionMedium;
        medium = 0;
        small = quan * divMedium;
        large = 0;
    }


    public void small2Large(){
        long buffLargeDiv = 0;
        long divMedium = 0;

        divMedium       = conversionLarge / conversionMedium;
        large       	= quan / conversionLarge;
        buffLargeDiv    = quan % conversionLarge;

        if (quan >= conversionLarge){
            if (buffLargeDiv >= divMedium) {
                medium 	= buffLargeDiv / divMedium;
                small 	= buffLargeDiv % divMedium;
            }
            else {
                small 	= buffLargeDiv;
            }
        }else{
            if (quan >= divMedium) {
                medium 	= quan / divMedium;
                small 	= quan % divMedium;
            }
            else {
                small 	= quan;
            }
        }

    }


    public long getSmall(){
        return small;
    }

    public long getMedium(){
        return medium;
    }

    public long getLarge(){
        return large;
    }




}