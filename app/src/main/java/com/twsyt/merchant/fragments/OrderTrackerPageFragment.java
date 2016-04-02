package com.twsyt.merchant.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.twsyt.merchant.Util.OrdersDataBase;
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
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private final String LOG_TAG = OrderTrackerPageFragment.this.getClass().getSimpleName();
    private int mPosition;
    private RecyclerView rv;
    private OrderTrackerRVAdapter orderTrackerRVAdapter;
    private HashMap<String, ArrayList<OrderHistory>> mOrderStatusMap;

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
        mOrderStatusMap = new OrdersDataBase(getContext()).genOrderStatusList();
        ArrayList<OrderHistory> orderList = getSortedOrdersList(AppConstants.ORDER_TRACK_TABS_LIST[mPosition]);
        orderTrackerRVAdapter = new OrderTrackerRVAdapter(orderList, getContext());
        rv.setAdapter(orderTrackerRVAdapter);
    }

    public void updateList() {
        setAdapter();
        Log.d(LOG_TAG, "Checking for updateList call");
    }

    /**
     * Method to generate list of orders to be shown.
     * This is based on the mapping of TABS and possible status of a particular order.
     * Check for Utils.getTabtoOrderStatusMapping for the mapping.
     *
     * @param tabTitle Name of the tab
     * @return ArrayList of orders to be displayed in one particular fragment.
     */
    private ArrayList<OrderHistory> getSortedOrdersList(String tabTitle) {
        String[] strings = Utils.getTabtoOrderStatusMapping(tabTitle);
        ArrayList<OrderHistory> list = new ArrayList<>();
        if ((strings != null) && (mOrderStatusMap != null)) {
            for (String key : strings) {
                if (mOrderStatusMap.get(key) != null)
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
                    return orderDateRhs.compareTo(orderDateLhs);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return list;
    }
}
