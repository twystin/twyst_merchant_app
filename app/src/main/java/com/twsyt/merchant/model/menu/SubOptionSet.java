package com.twsyt.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Raman on 3/30/2016.
 */
public class SubOptionSet implements Serializable {
    @SerializedName("is_vegetarian")
    private boolean isVegetarian;

    @SerializedName("_id")
    private String id;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("sub_option_value")
    private String subOptionValue;

    @SerializedName("sub_option_cost")
    private double subOptionCost;

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getSubOptionValue() {
        return subOptionValue;
    }

    public void setSubOptionValue(String subOptionValue) {
        this.subOptionValue = subOptionValue;
    }

    public double getSubOptionCost() {
        return subOptionCost;
    }

    public void setSubOptionCost(double subOptionCost) {
        this.subOptionCost = subOptionCost;
    }
}
