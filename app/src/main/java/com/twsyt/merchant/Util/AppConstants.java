package com.twsyt.merchant.Util;

import com.twsyt.merchant.model.User;

/**
 * Created by Raman on 3/30/2016.
 */
public class AppConstants {


    //    public static final String HOST = "http://192.168.0.113:3000";
    public static final String HOST = "http://staging.twyst.in";
    //    public static final String HOST = "https://twyst.in";
    public static final String FAYE_HOST = HOST + "/faye/";

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


    public static final String ORDER_PENDING = "PENDING";
    public static final String ORDER_LATE_ACCEPT = "LATE_ACCEPT";
    public static final String ORDER_LATE_DELIVERY = "LATE_DELIVERY";
    public static final String ORDER_ACCEPTED = "ACCEPTED";
    public static final String ORDER_DISPATCHED = "DISPATCHED";
    public static final String ORDER_ASSUMED_DELIVERED = "ASSUMED_DELIVERED";
    public static final String ORDER_DELIVERED = "DELIVERED";
    public static final String ORDER_ABANDONED = "ABANDONED";
    public static final String ORDER_REJECTED = "REJECTED";
    public static final String ORDER_CLOSED = "CLOSED";
    public static final String ORDER_CANCELLED = "CANCELLED";

    // if below sequence of strings are changed, it will lead to change of visible position of tabs in app. Please be careful.
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

    public static final String[] GET_NOTIFIED_FOR_STATUSES =
            {
                    ORDER_PENDING,
                    ORDER_LATE_ACCEPT,
                    ORDER_LATE_DELIVERY,
                    ORDER_REJECTED,
                    ORDER_CANCELLED,
                    ORDER_ABANDONED
            };

    public static final String INTENT_ORDER_ID = "order_id_from_main_to_detail";
    public static final String INTENT_DOWNLOADED_ORDER = "downloaded_order";
    public static final String NEW_DATA_AVAILABLE = "new_data_available";
    public static final int DOWNLOAD_SUCCESS = 1;
    public static final boolean IS_DEVELOPMENT = true;
    /*
        Role Book
        1    Root (Twyst Owners and Managers)
        2    Admin (Our sales persons or dev guys /account manager)
        3    Merchant / customer (The owner of Outlet)
        4    Marketing Manager (Manage all Outlets and Users)
        5    Outlet Manager (Outlet specific managers)
        6    User (The phone app User)
        7    OTP users
        8    Public (Anonymous Users)
    */
    public static final int ROLE_ROOT = 1;
    public static final int ROLE_ADMIN = 2;
    public static final int ROLE_MERCHANT = 3;
    public static final int ROLE_MARKETING_MANAGER = 4;
    public static final int ROLE_OUTLET_MANAGER = 5;
    public static final int ROLE_USER = 6;
    public static final int ROLE_OTP_USERS = 7;
    public static final int ROLE_PUBLIC_ANON = 8;

    public static final String LOGIN_RESPONSE_JSON = "login_response_json";
    public static final String LOGGED_IN_ATLEAST_ONCE = "login_status";

    public static final String INDIAN_RUPEE_SYMBOL = "₹";
    public static final String TAB_POSITION = "tab_position_to_show";

    public static final String ACCEPT = "accept";
    public static final String REJECT = "reject";
    public static final String DISPATCH = "dispatch";
    public static final String DELIVERED = "delivered";
}
