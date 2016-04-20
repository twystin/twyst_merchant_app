package com.twsyt.merchant.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;
import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.OrdersDataBaseSingleTon;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.activities.OrderDetailsActivity;
import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.LoginResponse;
import com.twsyt.merchant.model.order.OrderHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WebSocketService extends PubSubStickyService implements FayeListener {

    public static boolean isRunning = false;

    public WebSocketService() {
        super("WebSocketService");
    }

    public final String TAG = this.getClass().getSimpleName();

    FayeClient mClient;
    private String token;
    private String channelName;

    //    private String token = "HAba02nFxNIrQGreYIv9JUev078YDF2q";


    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.i(TAG, "OnCreate: creating webService instance");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Starting Web Socket Service");
        getLoginInfo();

//        try {
//        String baseUrl = "http://twyst.in/faye/";
//        String baseUrl = "http://staging.twyst.in/faye/";
//        URI uri = URI.create(String.format("wss://%s:443/events", baseUrl));
//        String channel = String.format("/%s", "56879bf4af76ee153f804dd3");
//        String channel = String.format("/%s", channelIntent.replace("@", "").replace(".", ""));
//            String channel = String.format("/%s/**", User.getCurrentUser().getUserId());
//            ext.put("authToken", User.getCurrentUser().getAuthorizationToken());
//            mClient = new FayeClient(uri, channel);
//        } catch (JSONException ex) {}

        URI uri = URI.create(String.format("%s", AppConstants.FAYE_HOST));
        String channel = String.format("/%s", channelName);
        Log.d(TAG, "URI : " + uri + " \n Channel : " + channel);
        JSONObject ext = new JSONObject();
        mClient = new FayeClient(new Handler(Looper.getMainLooper()), uri, channel);
        mClient.setFayeListener(this);
        mClient.connectToServer(ext);
    }

    private void getLoginInfo() {
        SharedPreferences sp = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        LoginResponse loginResp = new Gson().fromJson(sp.getString(AppConstants.LOGIN_RESPONSE_JSON, null), type);

        int role = loginResp.getRole();
        token = loginResp.getToken();
        channelName = "console";
        /*switch (role) {
            case AppConstants.ROLE_ADMIN:
                String email = loginResp.getEmail();
                *//*if (loginResp.getProfile() != null) {
                    email = loginResp.getProfile().getEmail();
                }*//*
                channelName = email.replace("@", "").replace(".", "");
                break;

            case AppConstants.ROLE_MERCHANT:
                channelName = loginResp.get_id();
                break;

            default:
                channelName = "";
        }*/
    }

    /**
     * Store all the current orders in SharedPreferences before the service is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called in webService");
        OrdersDataBaseSingleTon.getInstance(WebSocketService.this).storeInSharedPrefs();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved called in webService");
        super.onTaskRemoved(rootIntent);
    }

    /**
     * FayeListener methods.
     */

    @Override
    public void connectedToServer() {
        Log.i(TAG, "Connected to Server");
    }

    @Override
    public void disconnectedFromServer() {
        Log.i(TAG, "Disconnected from Server");
    }

    @Override
    public void subscribedToChannel(String subscription) {
        Log.i(TAG, String.format("Subscribed to channel %s on Faye", subscription));
    }

    @Override
    public void subscriptionFailedWithError(String error) {
        Log.i(TAG, String.format("Subscription failed with error: %s", error));
    }

    @Override
    public void messageReceived(JSONObject json) {
        Log.i(TAG, String.format("Received message %s", json.toString()));
        String order_id = null;
        try {
            order_id = json.getString("order_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (order_id != null) {
            getOrder(order_id);
        }

    }

    private void getOrder(final String mOrderID) {
        HttpService.getInstance().getOrderDetail(mOrderID, token, new Callback<BaseResponse<OrderHistory>>() {
                    @Override
                    public void success(BaseResponse<OrderHistory> orderHistoryBaseResponse, Response response) {
                        if (orderHistoryBaseResponse.isResponse()) {
                            updateOrdersDb(mOrderID, orderHistoryBaseResponse.getData());
                            Utils.callRegisteredReceivers(WebSocketService.this, AppConstants.DOWNLOAD_SUCCESS, null);
                            sendNotification(mOrderID);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConstants.RETROFIT_FAILURE_NO_RESPONSE_MESSAGE, orderHistoryBaseResponse.getMessage());
                            Utils.callRegisteredReceivers(WebSocketService.this, AppConstants.DOWNLOAD_FAILED, bundle);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(AppConstants.RETROFIT_FAILURE_ERROR, error);
                        Utils.callRegisteredReceivers(WebSocketService.this, AppConstants.DOWNLOAD_FAILED, bundle);
                    }
                }
        );
    }

    private void sendNotification(String orderId) {
        boolean notifyUser = false;

        OrderHistory order = OrdersDataBaseSingleTon.getInstance(this).getOrderFromOrderId(orderId);
        for (String s : AppConstants.GET_NOTIFIED_FOR_STATUSES) {
            if (order.getOrderStatus().equals(s)) {
                notifyUser = true;
                break;
            }
        }

        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra(AppConstants.INTENT_ORDER_ID, orderId);
        intent.putExtra(AppConstants.INTENT_START_SOURCE, AppConstants.START_SOURCE_IS_NOTIFICATION);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (notifyUser) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String contentTitle = "Twyst Notification";
            switch (order.getOrderStatus()) {
                case AppConstants.ORDER_PENDING: {
                    contentTitle = "Order Placed".toUpperCase();
                    break;
                }
                default: {
                    contentTitle = "ORDER " + order.getOrderStatus().toUpperCase();
                }
            }
            String contentText = "Order No: " + order.getOrderNumber().toUpperCase();

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setContentIntent(resultIntent)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setTicker(contentText)
                    .setSound(alarmSound)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setAutoCancel(true);

            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(orderId, 1, notification.build());
        }
    }


    /**
     * Make changes to the SingleTon Orders DB object as per the orderId.
     *
     * @param orderId Id of the order
     */
    private void updateOrdersDb(String orderId, OrderHistory order) {
        OrdersDataBaseSingleTon.getInstance(WebSocketService.this).addOrUpdateOrder(orderId, order);
        OrdersDataBaseSingleTon.getInstance(WebSocketService.this).storeInSharedPrefs();
    }
}
