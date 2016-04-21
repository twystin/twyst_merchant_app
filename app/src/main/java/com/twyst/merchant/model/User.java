package com.twyst.merchant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 05/04/16.
 */
public class User implements Serializable {

    private String first_name;
    private String last_name;

    @SerializedName("_id")
    private String useId;

    @SerializedName("phone")
    private String phone_number;

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUseId() {
        return useId;
    }

    public void setUseId(String useId) {
        this.useId = useId;
    }
}
