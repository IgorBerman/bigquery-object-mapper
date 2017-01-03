package com.ekene.bq.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.Date;

@Generated("org.jsonschema2pojo")
public class Segment {

    @SerializedName("SequenceNumber")
    @Expose
    public Integer SequenceNumber;
    @SerializedName("Direction")
    @Expose
    public String Direction;
    @SerializedName("Origin")
    @Expose
    public String Origin;
    @SerializedName("Destination")
    @Expose
    public String Destination;
    @SerializedName("DepartureDate")
    @Expose
    public Date DepartureDate;
    @SerializedName("ArrivalDate")
    @Expose
    public Date ArrivalDate;
    @SerializedName("Route")
    @Expose
    public String Route;
    @SerializedName("Aircraft")
    @Expose
    public String Aircraft;
    @SerializedName("FlightNumber")
    @Expose
    public String FlightNumber;
    @SerializedName("BookingClass")
    @Expose
    public String BookingClass;
}