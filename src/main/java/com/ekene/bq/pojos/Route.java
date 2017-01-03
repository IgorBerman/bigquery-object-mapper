package com.ekene.bq.pojos;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;
import java.util.Date;

@Generated("org.jsonschema2pojo")
public class Route {

    @SerializedName("OID")
    @Expose
    public String Id;
    @SerializedName("NumberOfAdults")
    @Expose
    public Integer NumberOfAdults;
    @SerializedName("NumberOfChildren")
    @Expose
    public Integer NumberOfChildren;
    @SerializedName("NumberOfInfants")
    @Expose
    public Integer NumberOfInfants;
    @SerializedName("OriginAirportCode")
    @Expose
    public String OriginAirportCode;
    @SerializedName("DestinationAirportCode")
    @Expose
    public String DestinationAirportCode;
    @SerializedName("Route")
    @Expose
    public String Route;
    @SerializedName("DepartureDate")
    @Expose
    public Date DepartureDate;
    @SerializedName("ArrivalDate")
    @Expose
    public Date ArrivalDate;
    @SerializedName("HasAlerts")
    @Expose
    public Boolean HasAlerts;

    private Integer NumberOfSegments = null;

    public Integer getNumberOfSegments() {
        if (Segments != null)
            NumberOfSegments = Segments.getRecords().size();
        return NumberOfSegments;
    }



    @SerializedName("segments")
    @Expose
    public SegmentRecord Segments;

}