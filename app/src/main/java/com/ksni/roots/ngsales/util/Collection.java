package com.ksni.roots.ngsales.util;

/**
 * Created by #roots on 29/08/2015.
 */
public class Collection {
    public String string;
    public Object tag;

    public Collection(String stringPart, Object tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }
}