package com.twyst.merchant.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twyst.merchant.model.BaseResponse;
import com.twyst.merchant.model.LoginResponse;
import com.twyst.merchant.model.order.OrderHistory;
import com.twyst.merchant.service.HttpService;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Singleton Class for holding data(order information.)
 * This needs to be thread safe as data will be loaded in WebSocketService and read by activities.
 * <p>
 * Data is stored in two formats.
 * 1. Map(mOrderIdMap)      - key: orderId,     Val: Order Info object
 * 2. Map(mOrderStatusMap)  - key: orderStatus, Val: orderId
 * <p>
 * Before the WebSocketService is destroyed, all the orders present in mOrderIdMap are stored in SharedPreferences for later use.
 * On a new start, data is loaded from Server, if network is available else from SharedPreferences.
 */
public class OrdersDataBaseSingleTon {

    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private final Context mContext;

    // TODO - see if it can be made a local variable
    private final SharedPreferences mSharedPreferences;

    private HashMap<String, OrderHistory> mOrderIdMap;
    private HashMap<String, ArrayList<String>> mOrderStatusMap;

    private static final Object mLock = new Object();

    public static void setInstanceNull() {
        OrdersDataBaseSingleTon.mInstance = null;
    }

    private static OrdersDataBaseSingleTon mInstance;

    public static OrdersDataBaseSingleTon getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new OrdersDataBaseSingleTon(context.getApplicationContext());
            }
            return mInstance;
        }
    }

    private OrdersDataBaseSingleTon(Context context) {
        this.mContext = context;
        this.mSharedPreferences = mContext.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        loadOrders();
    }

    /**
     *
     */
    private void loadOrders() {
        getFromSharedPrefs();
        if (Utils.isNetworkAvailable(mContext)) {
            Log.d(OrdersDataBaseSingleTon.class.getSimpleName(), "Network is available, Syncing with server");
            syncWithServer();
        }

    }

    private void getFromSharedPrefs() {
        if (mSharedPreferences.getString(AppConstants.ALL_ORDERS, null) != null) {
            Type type = new TypeToken<HashMap<String, OrderHistory>>() {
            }.getType();
            mOrderIdMap = new Gson().fromJson(mSharedPreferences.getString(AppConstants.ALL_ORDERS, null), type);
        } else {
            mOrderIdMap = new HashMap<>();
        }
        genOrderStatusMap();
    }

    public void syncWithServer() {
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        LoginResponse loginResp = new Gson().fromJson(mSharedPreferences.getString(AppConstants.LOGIN_RESPONSE_JSON, null), type);
        int role = loginResp.getRole();
        String token = loginResp.getToken();

        switch (role) {
            case AppConstants.ROLE_MERCHANT:
                break;

            case AppConstants.ROLE_ADMIN:
                HttpService.getInstance().getAllOrdersAM(token, new Callback<BaseResponse<ArrayList<OrderHistory>>>() {
                    @Override
                    public void success(BaseResponse<ArrayList<OrderHistory>> arrayListBaseResponse, Response response) {
                        if (arrayListBaseResponse.isResponse()) {
                            ArrayList<OrderHistory> ordersList = arrayListBaseResponse.getData();
                            mOrderStatusMap.clear();
                            mOrderIdMap.clear();
                            for (OrderHistory order : ordersList) {
                                addOrUpdateOrder(order.getOrderID(), order);
                            }
                            storeInSharedPrefs();
                            Utils.callRegisteredReceivers(mContext, AppConstants.DOWNLOAD_SUCCESS, null);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConstants.RETROFIT_FAILURE_NO_RESPONSE_MESSAGE, arrayListBaseResponse.getMessage());
                            Utils.callRegisteredReceivers(mContext, AppConstants.DOWNLOAD_FAILED, bundle);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(AppConstants.RETROFIT_FAILURE_ERROR, error);
                        Utils.callRegisteredReceivers(mContext, AppConstants.DOWNLOAD_FAILED, bundle);
                    }
                });
                break;
        }
    }

    private void genOrderStatusMap() {
        if (mOrderStatusMap == null) {
            mOrderStatusMap = new HashMap<>();
        }
        for (String key : mOrderIdMap.keySet()) {
            OrderHistory order = mOrderIdMap.get(key);
            String currStatusOfOrder = order.getOrderStatus();
            addOrderToOrderStatusMap(order.getOrderID(), currStatusOfOrder);
        }
    }

    /**
     * Adds an Order or Updates the existing order based on the orderId.
     *
     * @param orderId Every orderId is unique.
     * @param order   This is the complete order information.
     */
    public void addOrUpdateOrder(String orderId, OrderHistory order) {
        if (order.getOrderStatus().equals(AppConstants.ORDER_PENDING) && order.isNotified_am()) {
            order.setOrderStatus(AppConstants.ORDER_LATE_ACCEPT);
        }

        String prevStatusOfOrder = null;
        if (mOrderIdMap.containsKey(orderId)) {
            prevStatusOfOrder = mOrderIdMap.get(orderId).getOrderStatus();
        }
//            if (!currStatusOfOrder.equals(prevStatusOfOrder) && (prevStatusOfOrder != null)) {
        if (prevStatusOfOrder != null) {
            mOrderStatusMap.get(prevStatusOfOrder).remove(orderId);
        }

        String currStatusOfOrder = order.getOrderStatus();
        addOrderToOrderStatusMap(orderId, currStatusOfOrder);

        mOrderIdMap.put(orderId, order);
    }

    private void addOrderToOrderStatusMap(String orderId, String currStatusOfOrder) {
        if (mOrderStatusMap.containsKey(currStatusOfOrder)) {
            mOrderStatusMap.get(currStatusOfOrder).add(orderId);
        } else {
            ArrayList<String> o = new ArrayList<>();
            o.add(orderId);
            mOrderStatusMap.put(currStatusOfOrder, o);
        }
    }

    /**
     * Store all orders in SharedPreferences.
     * Used by Other components before any process is killed.
     */
    public boolean storeInSharedPrefs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String ordersMapJson = gson.toJson(mOrderIdMap);
        editor.putString(AppConstants.ALL_ORDERS, ordersMapJson);
        return editor.commit();
    }

    /**
     * Method to generate sorted list of orders to be shown. Sorting is done based on timestamp.
     * This is based on the mapping of TABS and possible status of a particular order.
     * e.g: Take-Action tab shows Late-Accept & Late-Delivery
     * Check for Utils.getTabtoOrderStatusMapping for the mapping.
     *
     * @param title Name of the tab
     * @return ArrayList of orders to be displayed in one particular fragment.
     */
    public ArrayList<OrderHistory> getSortedOrdersList(String title) {
        ArrayList<OrderHistory> ordersList = genOrderStatusList(title);

        Collections.sort(ordersList, new Comparator<OrderHistory>() {
            @Override
            public int compare(OrderHistory lhs, OrderHistory rhs) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                try {
                    Date orderDateLhs = sdf.parse(lhs.getOrderDate());
                    Date orderDateRhs = sdf.parse(rhs.getOrderDate());
                    return orderDateRhs.compareTo(orderDateLhs);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return ordersList;
    }

    public ArrayList<OrderHistory> genOrderStatusList(String tabStatus) {
        String[] actualStatus = Utils.getTabtoOrderStatusMapping(tabStatus);
        ArrayList<OrderHistory> groupedOrders = new ArrayList<>();
        if (actualStatus != null) {
            for (String s : actualStatus) {
                if (mOrderStatusMap.containsKey(s)) {
                    for (String id : mOrderStatusMap.get(s)) {
                        groupedOrders.add(mOrderIdMap.get(id));
                    }
                }
            }
        }
        return groupedOrders;
    }

    public HashMap<String, ArrayList<String>> getOrderStatusMap() {
        return mOrderStatusMap;
    }

    public OrderHistory getOrderFromOrderId(String orderId) {
        if (mOrderIdMap != null)
            if (mOrderIdMap.get(orderId) != null)
                return mOrderIdMap.get(orderId);
        return null;
    }

}
