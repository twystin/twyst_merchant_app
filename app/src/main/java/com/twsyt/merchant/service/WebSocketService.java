package com.twsyt.merchant.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.OrdersDataBaseSingleTon;
import com.twsyt.merchant.Util.Utils;
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

public class WebSocketService extends IntentService implements FayeListener {

    public static boolean isRunning = false;

    public WebSocketService() {
        super("WebSocketService");
    }

    public final String TAG = this.getClass().getSimpleName();

    FayeClient mClient;
    // Login Data
    private int role;
    private String token;
    private String channelName;

    private OrderHistory mOrderHistory;

    //    private String token = "HAba02nFxNIrQGreYIv9JUev078YDF2q";


    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Starting Web Socket Service");

        getLoginInfo();

//        try {

//        String baseUrl = "http://twyst.in/faye/";
//        String baseUrl = "http://staging.twyst.in/faye/";

        URI uri = URI.create(String.format("%s", AppConstants.FAYE_HOST));
        String channel = String.format("/%s", channelName);

//        URI uri = URI.create(String.format("wss://%s:443/events", baseUrl));
//        String channel = String.format("/%s", "56879bf4af76ee153f804dd3");
//        String channel = String.format("/%s", channelIntent.replace("@", "").replace(".", ""));

        Log.d(TAG, "URI : " + uri + " \n Channel : " + channel);
//            String channel = String.format("/%s/**", User.getCurrentUser().getUserId());

        JSONObject ext = new JSONObject();
//            ext.put("authToken", User.getCurrentUser().getAuthorizationToken());
//            mClient = new FayeClient(uri, channel);
        mClient = new FayeClient(new Handler(Looper.getMainLooper()), uri, channel);
        mClient.setFayeListener(this);
        mClient.connectToServer(ext);

//        } catch (JSONException ex) {}
    }

    private void getLoginInfo() {
        SharedPreferences sp = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        LoginResponse loginResp = new Gson().fromJson(sp.getString(AppConstants.LOGIN_RESPONSE_JSON, null), type);

        role = loginResp.getRole();
        token = loginResp.getToken();
        channelName = "";
        switch (role) {
            case AppConstants.ROLE_ADMIN:
                String email = "";
                if (loginResp.getProfile() != null) {
                    email = loginResp.getProfile().getEmail();
                }
                channelName = email.replace("@", "").replace(".", "");
                break;

            case AppConstants.ROLE_MERCHANT:
                channelName = loginResp.get_id();
                break;

            default:
                channelName = "";
        }
    }

    /**
     * Store all the current orders in SharedPreferences before the service is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        OrdersDataBaseSingleTon.getInstance(WebSocketService.this).storeInSharedPrefs();
    }

    /**
     * FayeListener methods.
     */
    @Override
    public void connectedToServer() {
        Log.i(TAG, "Connected to Server");
        mClient.subscribe();
    }

    @Override
    public void disconnectedFromServer() {
        Log.i(TAG, "Disonnected from Server");
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
                            mOrderHistory = orderHistoryBaseResponse.getData();
                            if (mOrderHistory != null) {
                                updateOrdersDb(mOrderID);
                                Utils.callRegisteredReceivers(WebSocketService.this, AppConstants.DOWNLOAD_SUCCESS);
                            } else {
                                Log.d(TAG, "Order fetched is null");
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // TODO - Need to improve the UX here. show snackbar or something
                    }
                }
        );
    }

    /**
     * Make changes to the SingleTon Orders DB object as per the orderId.
     *
     * @param orderId Id of the order
     */
    private void updateOrdersDb(String orderId) {
        OrdersDataBaseSingleTon.getInstance(WebSocketService.this).addOrUpdateOrder(orderId, mOrderHistory);
        OrdersDataBaseSingleTon.getInstance(WebSocketService.this).storeInSharedPrefs();
    }
}
