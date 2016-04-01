package com.twsyt.merchant;

import android.app.Application;
import android.content.Intent;

import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.receivers.OrderTrackerResultReceiver;
import com.twsyt.merchant.service.HttpService;
import com.twsyt.merchant.service.WebSocketService;

/**
 * Created by Vipul Sharma on 3/28/2016.
 */
public class TwystApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OrderTrackerResultReceiver receiver = OrderTrackerResultReceiver.getInstance();
        Intent intent = new Intent(this, WebSocketService.class);
        intent.putExtra(AppConstants.RESULT_RECEIVER, receiver);
        startService(intent);


        HttpService.getInstance().setup(getApplicationContext());

    }


}
