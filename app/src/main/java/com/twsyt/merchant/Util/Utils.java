package com.twsyt.merchant.Util;


/**
 * Created by tushar on 31/03/16.
 */
public class Utils {
    public static String[] getTabtoOrderStatusMapping(String tab) {
        switch (tab) {
            case AppConstants.ORDER_STATUS_TAKE_ACTION:
                return new String[]{"LATE_ACCEPT, LATE_DELIVERY"};

            case AppConstants.ORDER_STATUS_PENDING:
                return new String[]{"PENDING"};

            case AppConstants.ORDER_STATUS_LATE_ACCEPT:
                return new String[]{"LATE_ACCEPT"};

            case AppConstants.ORDER_STATUS_ACCEPTED:
                return new String[]{"ACCEPTED"};

            case AppConstants.ORDER_STATUS_DISPATCHED:
                return new String[]{"DISPATCHED"};

            case AppConstants.ORDER_STATUS_ASSUMED_DELIVERED:
                return new String[]{"ASSUMED_DELIVERED"};

            case AppConstants.ORDER_STATUS_LATE_DELIVERY:
                return new String[]{"LATE_DELIVERY"};

            case AppConstants.ORDER_STATUS_DELIVERED:
                return new String[]{"DELIVERED"};

            case AppConstants.ORDER_STATUS_OTHERS:
                return new String[]{"ABANDONED", "REJECTED", "CLOSED"};
        }
        return null;
    }
}
