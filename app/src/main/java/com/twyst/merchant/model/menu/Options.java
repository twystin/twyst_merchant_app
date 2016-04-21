package com.twyst.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Raman on 3/30/2016.
 */
public class Options implements Serializable {
    public Options(Options optionOriginal) {
        this.id = optionOriginal.getId();
        this.isVegetarian = optionOriginal.isVegetarian();
        this.optionCost = optionOriginal.getOptionCost();
        this.optionValue = optionOriginal.getOptionValue();
        this.addonsList.addAll(optionOriginal.getAddonsList());
        this.subOptionsList.addAll(optionOriginal.getSubOptionsList());
    }

    @SerializedName("_id")
    private String id;

    @SerializedName("is_vegetarian")
    private boolean isVegetarian;

    @SerializedName("option_cost")
    private double optionCost;

    @SerializedName("option_value")
    private String optionValue;

    @SerializedName("addons")
    private ArrayList<Addons> addonsList = new ArrayList<>();

    @SerializedName("sub_options")
    private ArrayList<SubOptions> subOptionsList = new ArrayList<>();

    @SerializedName("is_available")
    private boolean isAvailable;

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

    public ArrayList<SubOptions> getSubOptionsList() {
        return subOptionsList;
    }

    public void setSubOptionsList(ArrayList<SubOptions> subOptionsList) {
        this.subOptionsList = subOptionsList;
    }

    public ArrayList<Addons> getAddonsList() {
        return addonsList;
    }

    public void setAddonsList(ArrayList<Addons> addonsList) {
        this.addonsList = addonsList;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public double getOptionCost() {
        return optionCost;
    }

    public void setOptionCost(double optionCost) {
        this.optionCost = optionCost;
    }
}
