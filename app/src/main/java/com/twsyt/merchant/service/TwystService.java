package com.twsyt.merchant.service;


import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.order.OrderHistory;

import java.util.ArrayList;

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


//    http://staging.twyst.in/api/v4/outlet/orders/5687a31aaf76ee153f804e08?token=HAba02nFxNIrQGreYIv9JUev078YDF2q
    @GET("/api/v4/outlet/orders/{order_id}")
    public void getAllOrders(@Path("order_id") String orderId, @Query("token") String token, Callback<BaseResponse<ArrayList<OrderHistory>>> callback);
}
