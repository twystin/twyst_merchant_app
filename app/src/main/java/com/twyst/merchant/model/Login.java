package com.twyst.merchant.model;

import java.io.Serializable;

/**
 * Created by tushar on 04/04/16.
 */
public class Login implements Serializable {


    private boolean isMerchant;
    private String password;
    private String username;

    public Login (String password, String username) {
        this.password = password;
        this.username = username;
    }

    public boolean isMerchant() {
        return isMerchant;
    }

    public void setMerchant(boolean merchant) {
        isMerchant = merchant;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
