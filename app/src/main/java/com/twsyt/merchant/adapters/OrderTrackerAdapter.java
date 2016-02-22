package com.twsyt.merchant.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twsyt.merchant.fragments.OrderTrackerPageFragment;
import com.twsyt.merchant.model.OrderTrackInfo;

import java.util.ArrayList;

/**
 * Created by tushar on 19/02/16.
 */
public class OrderTrackerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<OrderTrackInfo> mOrderTrackInfo;

    public OrderTrackerAdapter(FragmentManager fm, ArrayList<OrderTrackInfo> orderTrackInfo) {
        super(fm);
        this.mOrderTrackInfo = orderTrackInfo;

    }

    @Override
    public int getCount() {
        return mOrderTrackInfo.size();
    }

    @Override
    public Fragment getItem(int position) {
        // Make an model object OrderInfo
//        OrderInfo orderInfo = mOrderTrackInfo.get(position);

        // a Static instance, needs to be fed with all the info.
        return OrderTrackerPageFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
