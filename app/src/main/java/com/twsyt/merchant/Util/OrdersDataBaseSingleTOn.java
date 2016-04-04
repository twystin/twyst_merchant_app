package com.twsyt.merchant.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.ArrayMap;
import android.view.ViewDebug;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.order.OrderHistory;
import com.twsyt.merchant.service.HttpService;

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

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    private final SharedPreferences mSharedPreferences;
    private ArrayMap<String, OrderHistory> mOrderIdMap;
    private ArrayMap<String, ArrayList<String>> mOrderStatusMap;

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
        if (Utils.isNetworkAvailable(mContext)) {
            String token = mSharedPreferences.getString(AppConstants.LOGIN_TOKEN, "");
            int role = mSharedPreferences.getInt(AppConstants.MY_ROLE_IS, 0);
            if (role != 0 && !token.equals("")) {
                syncWithServer(role, token);
            }
        } else {
        }

        if (mSharedPreferences.getString(AppConstants.ALL_ORDERS, null) != null) {
            Type type = new TypeToken<ArrayMap<String, OrderHistory>>() {
            }.getType();
            mOrderIdMap = new Gson().fromJson(mSharedPreferences.getString(AppConstants.ALL_ORDERS, null), type);
        } else {
            mOrderIdMap = new ArrayMap<>();
        }
        genOrderStatusMap();
    }

    private void syncWithServer(int role, String token) {
        switch (role) {
            case AppConstants.ROLE_MERCHANT:
                break;

            case AppConstants.ROLE_ADMIN:
                HttpService.getInstance().getAllOrdersAM(token, new Callback<BaseResponse<ArrayList<OrderHistory>>>() {
                    @Override
                    public void success(BaseResponse<ArrayList<OrderHistory>> arrayListBaseResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
                break;
        }
    }

    private void genOrderStatusMap() {
        if (mOrderStatusMap == null) {
            mOrderStatusMap = new ArrayMap<>();
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

    /**
     * Store all orders in SharedPreferences.
     * Used by Other components before any process is killed.
     */
    public void storeInSharedPrefs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String ordersMapJson = gson.toJson(mOrderIdMap);
        editor.putString(AppConstants.ALL_ORDERS, ordersMapJson);
        editor.commit();
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

    public ArrayMap<String, ArrayList<String>> getOrderStatusMap() {
        return mOrderStatusMap;
    }

}
