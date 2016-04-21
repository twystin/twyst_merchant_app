package com.twyst.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Raman on 3/30/2016.
 */
public class Coordinates implements Serializable {
    @SerializedName("latitude")
    private String lat;

    @SerializedName("longitude")
    private String lon;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
