package com.ksni.roots.ngsales.util.validator.validations;

 
public interface Validation {

    String getErrorMessage();

    boolean isValid(String text);

}