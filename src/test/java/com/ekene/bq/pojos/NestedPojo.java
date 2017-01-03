package com.ekene.bq.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NestedPojo {

    @SerializedName("stringValue")
    @Expose
    public String stringValue;

    @SerializedName("intValue")
    @Expose
    public Integer intValue;

    @SerializedName("shortValue")
    @Expose
    public Short shortValue;
}