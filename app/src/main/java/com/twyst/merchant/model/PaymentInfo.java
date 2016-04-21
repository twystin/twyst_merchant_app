package com.twyst.merchant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 05/04/16.
 */
public class PaymentInfo implements Serializable {

    @SerializedName("is_inapp")
    private boolean is_inapp;

    @SerializedName("payment_method")
    private String payment_method;

    @SerializedName("payment_mode")
    private String payment_mode;

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public boolean is_inapp() {
        return is_inapp;
    }

    public void setIs_inapp(boolean is_inapp) {
        this.is_inapp = is_inapp;
    }
}
