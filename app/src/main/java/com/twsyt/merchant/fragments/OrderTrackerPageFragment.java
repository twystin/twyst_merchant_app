package com.twsyt.merchant.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTrackerPageFragment extends Fragment {

    private String orderTracker_type;

    public OrderTrackerPageFragment() {
        // Required empty public constructor
    }

    public static OrderTrackerPageFragment newInstance(String type) {
        OrderTrackerPageFragment myFragment = new OrderTrackerPageFragment();
        Bundle args = new Bundle();
        args.putString(AppConstants.ORDER_TRACKER_TYPE, type);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderTracker_type = (getArguments() != null) ? getArguments().getString(AppConstants.ORDER_TRACKER_TYPE) : "";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_tracker, container, false);
    }
}
