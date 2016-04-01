package com.twsyt.merchant.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.twsyt.merchant.model.order.OrderHistory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tushar on 31/03/16.
 */
public class OrdersDataBase {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private HashMap<String, OrderHistory> mOrdersMap;

    public OrdersDataBase(Context context) {
        this.mContext = context;
        this.mSharedPreferences = mContext.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.mOrdersMap = null;
    }

    /**
     * Adds and Order or Updates the existing order.
     *
     * @param orderId
     * @param order
     */
    public void addOrUpdateOrder(String orderId, OrderHistory order) {
        mOrdersMap.put(orderId, order);
        updateSharePrefs();
    }

    public void addOrders(ArrayList<OrderHistory> allOrders) {
        mOrdersMap.clear();
        for (OrderHistory o : allOrders) {
            mOrdersMap.put(o.getOrderID(), o);
        }
        updateSharePrefs();
    }

    public void loadOrders() {
        mOrdersMap.clear();
        if (mSharedPreferences.getString(AppConstants.ALL_ORDERS, null) != null) {
            mOrdersMap = new Gson().fromJson(mSharedPreferences.getString(AppConstants.ALL_ORDERS, null), HashMap.class);
        }
    }

    private void updateSharePrefs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String ordersMapJson = gson.toJson(mOrdersMap);
        editor.putString(AppConstants.ALL_ORDERS, ordersMapJson);
        editor.commit();
    }

    public HashMap<String, ArrayList<OrderHistory>> genOrderStatusList() {
        HashMap<String, ArrayList<OrderHistory>> orderStatusHashMap = new HashMap<>();
        for (String key : mOrdersMap.keySet()) {
            OrderHistory order = mOrdersMap.get(key);
            String status = order.getOrderStatus();
            if (orderStatusHashMap.containsKey(status)) {
                orderStatusHashMap.get(status).add(order);
            } else {
                ArrayList<OrderHistory> arrayList = new ArrayList<>();
                arrayList.add(order);
                orderStatusHashMap.put(status, arrayList);
            }
        }
        return orderStatusHashMap;
    }
}
