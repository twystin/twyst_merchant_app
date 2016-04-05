package com.twsyt.merchant.Util;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.twsyt.merchant.activities.LoginActivity;
import com.twsyt.merchant.model.menu.TimeStamp;
import com.twsyt.merchant.service.WebSocketService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
                return new String[]{"ABANDONED", "REJECTED", "CLOSED", "CANCELLED"};
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


/*
    private static boolean isMyServiceRunning() {
        Log.d("Utils", "Calling isMyServiceRunning from Context: " + context.toString() + "Service : " + serviceClass.getSimpleName());
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("Utils", "Calling isMyServiceRunning from Context: " + context.toString() + "Service is running");
                return true;
            }
        }
        Log.d("Utils", "Service :  " + serviceClass.getSimpleName() + "from Context: " + context.toString() + "Service is NOT running");
        return false;
    }
*/

    public static void checkAndStartWebSocketService(Context context) {
        context = context.getApplicationContext();
        Log.d("Utils", "Calling checkAndStartWebSocketService from Context: " + context.toString());
        if (!WebSocketService.isRunning) {
            Log.d("Utils", "starting the service: " + context.toString());
            Intent intent = new Intent(context, WebSocketService.class);
            context.startService(intent);
        }
    }

    /**
     * BroadCast the message. Any activity with this LocalBroadCast manager will be notified of newly downloaded data.
     *
     * @param resultCode 1: success, 0: failure
     */
    public static void callRegisteredReceivers(Context context, int resultCode) {
        Intent intent = new Intent(AppConstants.INTENT_DOWNLOADED_ORDER);
        intent.putExtra(AppConstants.NEW_DATA_AVAILABLE, resultCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static TimeStamp getTimeStamp(String orderDate) {
        String orderDateOld = orderDate;
        TimeStamp timeStamp = new TimeStamp();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM);
        DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.SHORT);

        try {
            Date d = sdf.parse(orderDateOld);
            timeStamp.setDate(dateInstance.format(d));
            timeStamp.setTime(timeInstance.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }
}
