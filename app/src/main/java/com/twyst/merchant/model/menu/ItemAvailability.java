package com.twyst.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Raman on 3/30/2016.
 */
public class ItemAvailability implements Serializable {
    @SerializedName("regular_item")
    private String regularItem;

    public String getRegularItem() {
        return regularItem;
    }

    public void setRegularItem(String regularItem) {
        this.regularItem = regularItem;
    }
}

