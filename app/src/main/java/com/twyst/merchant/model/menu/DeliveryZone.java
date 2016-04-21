package com.twyst.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Raman on 3/30/2016.
 */
public class DeliveryZone implements Serializable {
    @SerializedName("min_amt_for_delivery")
    private String minDeliveryAmt;

    @SerializedName("delivery_estimated_time")
    private String deliveryEstimatedTime;

    @SerializedName("payment_options")
    private ArrayList<String> paymentOptions = new ArrayList<>();

    @SerializedName("zone_type")
    private int zoneType = -1;

    @SerializedName("coord")
    private ArrayList<Coordinates> coordList;

    public String getMinDeliveryAmt() {
        return minDeliveryAmt;
    }

    public void setMinDeliveryAmt(String minDeliveryAmt) {
        this.minDeliveryAmt = minDeliveryAmt;
    }

    public String getDeliveryEstimatedTime() {
        return deliveryEstimatedTime;
    }

    public void setDeliveryEstimatedTime(String deliveryEstimatedTime) {
        this.deliveryEstimatedTime = deliveryEstimatedTime;
    }

    public ArrayList<String> getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(ArrayList<String> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public int getZoneType() {
        return zoneType;
    }

    public void setZoneType(int zoneType) {
        this.zoneType = zoneType;
    }

    public ArrayList<Coordinates> getCoordList() {
        return coordList;
    }

    public void setCoordList(ArrayList<Coordinates> coordList) {
        this.coordList = coordList;
    }
}

