package com.twyst.merchant.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Raman on 3/30/2016.
 */
public class SubOptions implements Serializable {
    public SubOptions(SubOptions subOptionsOriginal) {
        this.id = subOptionsOriginal.getId();
        this.subOptionTitle = subOptionsOriginal.getSubOptionTitle();
        this.subOptionSetList.addAll(subOptionsOriginal.getSubOptionSetList());
    }

    @SerializedName("sub_option_title")
    private String subOptionTitle;

    @SerializedName("_id")
    private String id;

    @SerializedName("sub_option_set")
    private ArrayList<SubOptionSet> subOptionSetList = new ArrayList<>();

    public String getSubOptionTitle() {
        return subOptionTitle;
    }

    public void setSubOptionTitle(String subOptionTitle) {
        this.subOptionTitle = subOptionTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<SubOptionSet> getSubOptionSetList() {
        return subOptionSetList;
    }

    public void setSubOptionSetList(ArrayList<SubOptionSet> subOptionSetList) {
        this.subOptionSetList = subOptionSetList;
    }
}
