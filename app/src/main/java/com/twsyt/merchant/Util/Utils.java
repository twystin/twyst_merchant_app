package com.twsyt.merchant.Util;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.twsyt.merchant.activities.LoginActivity;
import com.twsyt.merchant.service.WebSocketService;

/**
 * Created by tushar on 31/03/16.
 */
public class Utils {
    public static String[] getTabtoOrderStatusMapping(String tab) {
        switch (tab) {
            case AppConstants.ORDER_STATUS_TAKE_ACTION:
                return new String[]{"LATE_ACCEPT, LATE_DELIVERY"};

            case AppConstants.ORDER_STATUS_PENDING:
                return new String[]{"PENDING"};

            case AppConstants.ORDER_STATUS_LATE_ACCEPT:
                return new String[]{"LATE_ACCEPT"};

            case AppConstants.ORDER_STATUS_ACCEPTED:
                return new String[]{"ACCEPTED"};

            case AppConstants.ORDER_STATUS_DISPATCHED:
                return new String[]{"DISPATCHED"};

            case AppConstants.ORDER_STATUS_ASSUMED_DELIVERED:
                return new String[]{"ASSUMED_DELIVERED"};

            case AppConstants.ORDER_STATUS_LATE_DELIVERY:
                return new String[]{"LATE_DELIVERY"};

            case AppConstants.ORDER_STATUS_DELIVERED:
                return new String[]{"DELIVERED"};

            case AppConstants.ORDER_STATUS_OTHERS:
                return new String[]{"ABANDONED", "REJECTED", "CLOSED"};
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startMyService(Class<?> serviceClass, Context context) {
        if (!Utils.isMyServiceRunning(serviceClass, context)) {
            Intent intent = new Intent(context, WebSocketService.class);
            context.startService(intent);
        }
    }

}
