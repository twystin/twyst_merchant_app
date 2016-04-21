package com.twyst.merchant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 04/04/16.
 */
public class LoginResponse implements Serializable {

    private String _id;
    private int role;
    private Profile profile;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;


    // This below object is added coz of flaw in our API.
    @SerializedName("data")
    private LoginResponse loginResponse;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    public void setLoginResponse(LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
