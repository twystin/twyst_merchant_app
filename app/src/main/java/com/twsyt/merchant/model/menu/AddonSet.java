package com.twsyt.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Raman on 3/30/2016.
 */
public class AddonSet implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("is_vegetarian")
    private boolean isVegetarian;

    @SerializedName("addon_value")
    private String addonValue;

    @SerializedName("addon_cost")
    private double addonCost;

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

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getAddonValue() {
        return addonValue;
    }

    public void setAddonValue(String addonValue) {
        this.addonValue = addonValue;
    }

    public double getAddonCost() {
        return addonCost;
    }

    public void setAddonCost(double addonCost) {
        this.addonCost = addonCost;
    }
}
