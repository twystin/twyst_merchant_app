package com.twsyt.merchant.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.OrdersDataBaseSingleTon;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.adapters.OrderTrackerRVAdapter;
import com.twsyt.merchant.model.order.OrderHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTrackerPageFragment extends Fragment {

    private final String LOG_TAG = OrderTrackerPageFragment.this.getClass().getSimpleName();
    private int mPosition;
    private RecyclerView rv;
    private OrderTrackerRVAdapter orderTrackerRVAdapter;

    public OrderTrackerPageFragment() {
        // Required empty public constructor
    }

    public static OrderTrackerPageFragment newInstance(int position) {
        OrderTrackerPageFragment myFragment = new OrderTrackerPageFragment();
        Bundle args = new Bundle();
        args.putInt(AppConstants.ORDER_TRACKER_TYPE, position);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPosition = (getArguments() != null) ? getArguments().getInt(AppConstants.ORDER_TRACKER_TYPE) : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_tracker, container, false);
        setupRvAdapter(view);
        return view;
    }

    /**
     * Setup the Rv Adapter for fragment.
     *
     * @param view
     */
    private void setupRvAdapter(View view) {

        rv = (RecyclerView) view.findViewById(R.id.orderTrackerRecyclerView);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);

        setAdapter();
    }

    private void setAdapter() {
        ArrayList<OrderHistory> orderList = OrdersDataBaseSingleTon.getInstance(getContext()).getSortedOrdersList(AppConstants.ORDER_TRACK_TABS_LIST[mPosition]);
        orderTrackerRVAdapter = new OrderTrackerRVAdapter(orderList, getContext());
        rv.setAdapter(orderTrackerRVAdapter);
    }

    public void updateList() {
        setAdapter();
        Log.d(LOG_TAG, "Checking for updateList call");
    }

}
