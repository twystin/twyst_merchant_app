package com.twsyt.merchant.Util;

/**
 * Created by Raman on 3/30/2016.
 */
public class AppConstants {

    public static final String HOST = "http://staging.twyst.in";

    public static final String ORDER_TRACKER_TYPE = "order_tracker_type";
    public static final String PREFERENCE_SHARED_PREF_NAME = "com.twyst.android";
    public static final String ALL_ORDERS = "all_orders";

    public static final String ORDER_STATUS_TAKE_ACTION = "TAKE ACTION";
    public static final String ORDER_STATUS_PENDING = "Pending";
    public static final String ORDER_STATUS_LATE_ACCEPT = "LATE-ACCEPT";
    public static final String ORDER_STATUS_ACCEPTED = "Accepted";
    public static final String ORDER_STATUS_DISPATCHED = "Dispatched";
    public static final String ORDER_STATUS_ASSUMED_DELIVERED = "Assumed Delivered";
    public static final String ORDER_STATUS_LATE_DELIVERY = "LATE-DELIVERY";
    public static final String ORDER_STATUS_DELIVERED = "Delivered";
    public static final String ORDER_STATUS_OTHERS = "Others";

    public static final String[] ORDER_TRACK_TABS_LIST =
            {
                    ORDER_STATUS_TAKE_ACTION,
                    ORDER_STATUS_PENDING,
                    ORDER_STATUS_LATE_ACCEPT,
                    ORDER_STATUS_ACCEPTED,
                    ORDER_STATUS_DISPATCHED,
                    ORDER_STATUS_ASSUMED_DELIVERED,
                    ORDER_STATUS_LATE_DELIVERY,
                    ORDER_STATUS_DELIVERED,
                    ORDER_STATUS_OTHERS
            };
    public static final String ORDER_DETAIL = "order_detail";
}
