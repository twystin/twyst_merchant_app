package com.twsyt.merchant.service;


import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.Login;
import com.twsyt.merchant.model.LoginResponse;
import com.twsyt.merchant.model.order.OrderHistory;
import com.twsyt.merchant.model.order.OrderUpdate;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Raman on 3/30/2016.
 */
public interface TwystService {

    @GET("/api/v4/order/{order_id}")
    void getOrderDetail(@Path("order_id") String orderId, @Query("token") String token, Callback<BaseResponse<OrderHistory>> callback);


//    http://staging.twyst.in/api/v4/outlet/orders/5687a31aaf76ee153f804e08?token=HAba02nFxNIrQGreYIv9JUev078YDF2q
    @GET("/api/v4/outlet/orders/{order_id}")
    void getAllOrdersOutlet(@Path("order_id") String orderId, @Query("token") String token, Callback<BaseResponse<ArrayList<OrderHistory>>> callback);

//    http://staging.twyst.in/api/v4/accounts/login
    @POST("/api/v4/accounts/login")
    void twystLogin(@Body() Login login, Callback<BaseResponse<LoginResponse>> callback);

    // http://staging.twyst.in/api/v4/orders?token=8XhMaSrQiB-vOUBoMueSIoK6VI_ErH67
    @GET("/api/v4/orders")
    void getAllOrdersAM(@Query("token") String token, Callback<BaseResponse<ArrayList<OrderHistory>>> callback);

    @PUT("/api/v4/order")
    public void putOrderUpdate(@Query("token") String token, @Body() OrderUpdate orderUpdate, Callback<BaseResponse> callback);
}
