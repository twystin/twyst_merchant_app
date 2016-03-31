package com.twsyt.merchant.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.fragments.OrderTrackerPageFragment;
import com.twsyt.merchant.model.order.OrderHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tushar on 19/02/16.
 */
public class OrderTrackerAdapter extends FragmentStatePagerAdapter {
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private HashMap<String, ArrayList<OrderHistory>> mOrderStatusMap;

    public OrderTrackerAdapter(FragmentManager fm, HashMap<String, ArrayList<OrderHistory>> orderStatusMap) {
        super(fm);
        this.mOrderStatusMap = orderStatusMap;
    }

    @Override
    public int getCount() {
        return 0;
//        return mOrderTrackInfo.size();
    }

    @Override
    public Fragment getItem(int position) {
        ArrayList<OrderHistory> list = getSortedOrdersList(AppConstants.ORDER_TRACK_TABS_LIST[position]);

        // Make an model object OrderInfo
//        OrderInfo orderInfo = mOrderTrackInfo.get(position);

        // a Static instance, needs to be fed with all the info.

        return OrderTrackerPageFragment.newInstance(list);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String tabTitle = AppConstants.ORDER_TRACK_TABS_LIST[position];
        String[] strings = Utils.getTabtoOrderStatusMapping(tabTitle);
        int count = 0;
        if (strings != null) {
            for (String key : strings) {
                count += (mOrderStatusMap.get(key)).size();
            }
        }

        if (count != 0) {
            return tabTitle + "(" + count + ")";
        }
        return tabTitle;
    }

    /** Method to generate list of orders to be shown.
     * This is based on the mapping of TABS and possible status of a perticular order.
     * Check for Utils.getTabtoOrderStatusMapping for the mapping.
     *
     * @param tabTitle Name of the tab
     * @return ArrayList of orders to be displayed in one perticular fragment.
     */
    private ArrayList<OrderHistory> getSortedOrdersList(String tabTitle) {
        String[] strings = Utils.getTabtoOrderStatusMapping(tabTitle);
        ArrayList<OrderHistory> list = new ArrayList<>();
        if (strings != null) {
            for (String key : strings) {
                list.addAll(mOrderStatusMap.get(key));
            }
        }
        Collections.sort(list, new Comparator<OrderHistory>() {
            @Override
            public int compare(OrderHistory lhs, OrderHistory rhs) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                try {
                    Date orderDateLhs = sdf.parse(lhs.getOrderDate());
                    Date orderDateRhs = sdf.parse(rhs.getOrderDate());
                    return orderDateLhs.compareTo(orderDateRhs);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return list;
    }


}
