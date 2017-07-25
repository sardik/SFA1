package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 29/09/2015.
 */
public class ViewOrderChild {

        private String name;
        private String text1;
        private String text2;
        private String itemType;

    public String getItemType()
    {
        return itemType;
    }

    public void setItemType(String value)
    {
        this.itemType = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getText1()
    {
        return text1;
    }

    public void setText1(String text1)
    {
        this.text1 = text1;
    }

    public String getText2()
    {
        return text2;
    }

    public void setText2(String text2)
    {
        this.text2 = text2;
    }

}
