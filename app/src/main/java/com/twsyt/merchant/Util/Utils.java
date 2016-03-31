package com.twsyt.merchant.Util;


/**
 * Created by tushar on 31/03/16.
 */
public class Utils {
    public static String[] getTabtoOrderStatusMapping(String tab) {
        switch (tab) {
            case AppConstants.ORDER_STATUS_TAKE_ACTION:
                return new String[]{"late_accept, late_delivered"};

            case AppConstants.ORDER_STATUS_PENDING:
                return new String[]{"pending"};

            case AppConstants.ORDER_STATUS_LATE_ACCEPT:
                return new String[]{"late_accept"};

            case AppConstants.ORDER_STATUS_DISPATCHED:
                return new String[]{"dispatched"};

            case AppConstants.ORDER_STATUS_ASSUMED_DELIVERED:
                return new String[]{"assumed_delivered"};

            case AppConstants.ORDER_STATUS_LATE_DELIVERY:
                return new String[]{"late_delivery"};

            case AppConstants.ORDER_STATUS_DELIVERED:
                return new String[]{"delivered"};

            case AppConstants.ORDER_STATUS_OTHERS:
                return new String[]{"abandoned", "rejected", "closed"};
        }
        return null;
    }
}
