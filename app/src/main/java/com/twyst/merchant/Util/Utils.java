package com.twyst.merchant.Util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twyst.merchant.model.LoginResponse;
import com.twyst.merchant.model.menu.TimeStamp;
import com.twyst.merchant.service.WebSocketService;

import java.lang.reflect.Type;
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
                return new String[]{AppConstants.ORDER_LATE_ACCEPT, AppConstants.ORDER_LATE_DELIVERY};

            case AppConstants.ORDER_STATUS_PENDING:
                return new String[]{AppConstants.ORDER_PENDING};

            case AppConstants.ORDER_STATUS_LATE_ACCEPT:
                return new String[]{AppConstants.ORDER_LATE_ACCEPT};

            case AppConstants.ORDER_STATUS_ACCEPTED:
                return new String[]{AppConstants.ORDER_ACCEPTED};

            case AppConstants.ORDER_STATUS_DISPATCHED:
                return new String[]{AppConstants.ORDER_DISPATCHED};

            case AppConstants.ORDER_STATUS_ASSUMED_DELIVERED:
                return new String[]{AppConstants.ORDER_ASSUMED_DELIVERED};

            case AppConstants.ORDER_STATUS_LATE_DELIVERY:
                return new String[]{AppConstants.ORDER_LATE_DELIVERY};

            case AppConstants.ORDER_STATUS_DELIVERED:
                return new String[]{AppConstants.ORDER_DELIVERED};

            case AppConstants.ORDER_STATUS_OTHERS:
                return new String[]{AppConstants.ORDER_ABANDONED,
                        AppConstants.ORDER_REJECTED,
                        AppConstants.ORDER_CLOSED,
                        AppConstants.ORDER_CANCELLED};
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
    public static void callRegisteredReceivers(Context context, int resultCode, Bundle bundle) {
        Intent intent = new Intent(AppConstants.INTENT_DOWNLOADED_ORDER);
        intent.putExtra(AppConstants.NEW_DATA_AVAILABLE, resultCode);
        if (bundle != null)
            intent.putExtras(bundle);
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

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static String getUserToken(Context context) {
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        LoginResponse loginResp = new Gson().fromJson(sharedPreferences.getString(AppConstants.LOGIN_RESPONSE_JSON, null), type);
        return loginResp.getToken();
    }
}
