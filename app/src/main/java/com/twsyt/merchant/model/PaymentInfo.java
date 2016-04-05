package com.twsyt.merchant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 05/04/16.
 */
public class PaymentInfo implements Serializable {

    public boolean is_inapp() {
        return is_inapp;
    }

    public void setIs_inapp(boolean is_inapp) {
        this.is_inapp = is_inapp;
    }

    private boolean is_inapp;
}
