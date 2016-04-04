package com.twsyt.merchant.model;

import java.io.Serializable;

/**
 * Created by tushar on 04/04/16.
 */
public class Profile implements Serializable {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
