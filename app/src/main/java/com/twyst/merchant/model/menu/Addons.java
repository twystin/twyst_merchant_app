package com.twyst.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Raman on 3/30/2016.
 */
public class Addons implements Serializable {
    public Addons(Addons addonsOriginal) {
        this.id = addonsOriginal.getId();
        this.addonTitle = addonsOriginal.getAddonTitle();
        this.addonSetList.addAll(addonsOriginal.getAddonSetList());
    }

    @SerializedName("_id")
    private String id;

    @SerializedName("addon_set")
    private ArrayList<AddonSet> addonSetList = new ArrayList<>();

    @SerializedName("addon_title")
    private String addonTitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<AddonSet> getAddonSetList() {
        return addonSetList;
    }

    public void setAddonSetList(ArrayList<AddonSet> addonSetList) {
        this.addonSetList = addonSetList;
    }

    public String getAddonTitle() {
        return addonTitle;
    }

    public void setAddonTitle(String addonTitle) {
        this.addonTitle = addonTitle;
    }
}
