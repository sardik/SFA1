package com.ksni.roots.ngsales.domain;

import java.util.ArrayList;

/**
 * Created by #roots on 29/09/2015.
 */
public class ViewOrderParent {
    private String name;
    private String text1;
    private String text2;
    private String total;
    private String checkedtype;

    private boolean checked;
    private ArrayList<ViewOrderChild> children;

    private int status;


    public void setTotal(String value)
    {
        this.total = value;
    }
    public String getTotal()
    {
        return total;
    }
    public void setStatus(int value)
    {
        this.status = value;
    }
    public int getStatus()
    {
        return status;
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
    public String getCheckedType()
    {
        return checkedtype;
    }
    public void setCheckedType(String checkedtype)
    {
        this.checkedtype = checkedtype;
    }
    public boolean isChecked()
    {
        return checked;
    }
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public ArrayList<ViewOrderChild> getChildren()
    {
        return children;
    }
    public void setChildren(ArrayList<ViewOrderChild> children)
    {
        this.children = children;
    }

}
