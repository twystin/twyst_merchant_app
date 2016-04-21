package com.twyst.merchant.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Raman on 3/30/2016.
 */
public class Address implements Serializable {

    private String city;
    private String landmark;
    private String line1;
    private String line2;
    private String state;
    private String delivery_zone;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDelivery_zone() {
        return delivery_zone;
    }

    public void setDelivery_zone(String delivery_zone) {
        this.delivery_zone = delivery_zone;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @SerializedName("coords")

    private Coords coords;

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }
}
