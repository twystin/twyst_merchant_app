package com.twsyt.merchant.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twsyt.merchant.model.order.OrderHistory;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tushar on 31/03/16.
 */
public class OrdersDataBaseSingleTon {
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private HashMap<String, OrderHistory> mOrderIdMap;
    private HashMap<String, ArrayList<String>> mOrderStatusMap;

    private static final Object mLock = new Object();
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

    private void loadOrders() {
        // Load data from server if internet is present else from sharedPrefs.
        if (isNetworkAvailable()) {
        } else {
        }

        if (mSharedPreferences.getString(AppConstants.ALL_ORDERS, null) != null) {
            Type type = new TypeToken<HashMap<String, OrderHistory>>() {
            }.getType();
            mOrderIdMap = new Gson().fromJson(mSharedPreferences.getString(AppConstants.ALL_ORDERS, null), type);
        } else {
            mOrderIdMap = new HashMap<>();
        }
        genOrderStatusMap();
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
     * Adds an Order or Updates the existing order.
     *
     * @param orderId
     * @param order
     */
    public void addOrUpdateOrder(String orderId, OrderHistory order) {
        String prevStatusOfOrder = null;
        if (mOrderIdMap.containsKey(orderId)) {
            prevStatusOfOrder = mOrderIdMap.get(orderId).getOrderStatus();
        }
        String currStatusOfOrder = order.getOrderStatus();

        if (!currStatusOfOrder.equals(prevStatusOfOrder) && (prevStatusOfOrder != null)) {
            mOrderStatusMap.get(prevStatusOfOrder).remove(orderId);
        }
        addOrderToOrderStatusMap(order.getOrderID(), currStatusOfOrder);
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

    public void storeInSharedPrefs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String ordersMapJson = gson.toJson(mOrderIdMap);
        editor.putString(AppConstants.ALL_ORDERS, ordersMapJson);
        editor.commit();
    }

    /**
     * Method to generate list of orders to be shown.
     * This is based on the mapping of TABS and possible status of a particular order.
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public HashMap<String, ArrayList<String>> getOrderStatusMap() {
        return mOrderStatusMap;
    }

    public void setOrderStatusMap(HashMap<String, ArrayList<String>> mOrderStatusMap) {
        this.mOrderStatusMap = mOrderStatusMap;
    }

}
