package com.ekene.bq.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TestPojo {

    @SerializedName("stringValue")
    @Expose
    public String stringValue;

    @SerializedName("dateValue")
    @Expose
    public Date dateValue;

    @SerializedName("intValue")
    @Expose
    public Integer intValue;

    @SerializedName("shortValue")
    @Expose
    public Short shortValue;

    @SerializedName("longValue")
    @Expose
    public Long longValue;

    @SerializedName("doubleValue")
    @Expose
    public Double doubleValue;

    @SerializedName("booleanValue")
    @Expose
    private Boolean booleanValue;

    @SerializedName("nestedPojo")
    @Expose
    public NestedPojoRecord nestedPojo;

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }
}