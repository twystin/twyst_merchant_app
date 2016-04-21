package com.twyst.merchant;

import android.app.Application;

import com.twyst.merchant.service.HttpService;

/**
 * Created by Vipul Sharma on 3/28/2016.
 */
public class TwystApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpService.getInstance().setup(getApplicationContext());
    }
}
