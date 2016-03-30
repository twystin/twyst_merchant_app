package com.twsyt.merchant.service;


import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.order.OrderHistory;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Raman on 3/30/2016.
 */
public interface TwystService {

    @GET("/api/v4/order/{order_id}")
    public void getOrderDetail(@Path("order_id") String orderId, @Query("token") String token, Callback<BaseResponse<OrderHistory>> callback);
}
