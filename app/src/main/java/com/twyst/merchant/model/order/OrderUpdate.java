package com.twyst.merchant.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Raman on 4/11/2016.
 */
public class OrderUpdate implements Serializable {

    @SerializedName("_id")
    String _id;

    @SerializedName("am_email")
    String am_email;

    @SerializedName("update_type")
    String update_type;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAm_email() {
        return am_email;
    }

    public void setAm_email(String am_email) {
        this.am_email = am_email;
    }

    public String getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(String update_type) {
        this.update_type = update_type;
    }
}
