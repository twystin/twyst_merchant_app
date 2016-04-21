package com.twyst.merchant.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twyst.merchant.Util.AppConstants;
import com.twyst.merchant.Util.OrdersDataBaseSingleTon;
import com.twyst.merchant.Util.Utils;
import com.twyst.merchant.fragments.OrderTrackerPageFragment;

import java.util.ArrayList;

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
        String tabTitle = AppConstants.ORDER_TRACK_TABS_LIST[position];
        String[] actualStatus = Utils.getTabtoOrderStatusMapping(tabTitle);
        int count = 0;
        if (actualStatus != null) {
            for (String s : actualStatus) {
                ArrayList<String> temp = OrdersDataBaseSingleTon.getInstance(mContext).getOrderStatusMap().get(s);
                if (temp != null)
                    count += temp.size();
            }
        }
        if (count != 0) {
            return tabTitle + "(" + count + ")";
        }
        return tabTitle;
    }
}
