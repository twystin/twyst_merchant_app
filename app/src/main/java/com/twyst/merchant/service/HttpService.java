package com.twyst.merchant.service;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.twyst.merchant.Util.AppConstants;
import com.twyst.merchant.model.BaseResponse;
import com.twyst.merchant.model.Login;
import com.twyst.merchant.model.LoginResponse;
import com.twyst.merchant.model.order.OrderHistory;
import com.twyst.merchant.model.order.OrderUpdate;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;

/**
 * Created by Raman on 3/30/2016.
 */
public class HttpService {


    private static HttpService instance = new HttpService();
    private Context context;

    //    private LruCache<String, Object> cache = new LruCache<>(4 * 1024 * 1024);
    private TwystService twystService;

    private HttpService() {
    }

    public void setup(Context context) {
        this.context = context;

        OkHttpClient okHttpClient = new OkHttpClient();
        OkClient okClient = new OkClient(okHttpClient);

        Cache cache = new Cache(context.getCacheDir(), 1024);
        okHttpClient.setCache(cache);
//        okHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        Log.d("HttpService", String.valueOf(okHttpClient.getConnectTimeout()));
        RestAdapter jsonRestAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit"))
                .setLogLevel((AppConstants.IS_DEVELOPMENT) ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setEndpoint(AppConstants.HOST)
                .setClient(okClient)
                .build();
        twystService = jsonRestAdapter.create(TwystService.class);
    }

    public static HttpService getInstance() {
        return instance;
    }

    public void getOrderDetail(String orderID, String token, Callback<BaseResponse<OrderHistory>> callback) {
        twystService.getOrderDetail(orderID, token, callback);
    }

    public void getAllOrdersOutlet(String orderId, String token, Callback<BaseResponse<ArrayList<OrderHistory>>> callback) {
        twystService.getAllOrdersOutlet(orderId, token, callback);
    }

    public void twystLogin(Login login, Callback<BaseResponse<LoginResponse>> callback) {
        twystService.twystLogin(login, callback);
    }

    public void getAllOrdersAM(String token, Callback<BaseResponse<ArrayList<OrderHistory>>> callback) {
        twystService.getAllOrdersAM(token, callback);
    }

    public void putOrderUpdate(String orderID, String token, OrderUpdate orderUpdate, Callback<BaseResponse<OrderHistory>> callback) {
        twystService.putOrderUpdate(orderID, token, orderUpdate, callback);
    }

    public void twystLogout(String token, Callback<BaseResponse<String>> callback) {
        twystService.twystLogout(token, callback);
    }


}
