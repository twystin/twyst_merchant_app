package com.twsyt.merchant.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.OrdersDataBase;
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
public class OrderTrackerFragmentAdapter extends FragmentStatePagerAdapter {
    private Context mContext;

    public OrderTrackerFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;

    }

    @Override
    public int getCount() {
        return AppConstants.ORDER_TRACK_TABS_LIST.length;
    }

    @Override
    public Fragment getItem(int position) {
        return OrderTrackerPageFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        HashMap<String, ArrayList<OrderHistory>> orderStatusMap = new OrdersDataBase(mContext).genOrderStatusList();
        String tabTitle = AppConstants.ORDER_TRACK_TABS_LIST[position];
        String[] strings = Utils.getTabtoOrderStatusMapping(tabTitle);
        int count = 0;
        if ((strings != null) && (orderStatusMap != null)) {
            for (String key : strings) {
                if (orderStatusMap.get(key) != null)
                    count += (orderStatusMap.get(key)).size();
            }
        }

        if (count != 0) {
            return tabTitle + "(" + count + ")";
        }
        return tabTitle;
    }

}
