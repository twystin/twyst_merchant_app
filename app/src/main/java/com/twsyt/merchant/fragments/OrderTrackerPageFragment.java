package com.twsyt.merchant.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.adapters.OrderTrackerRVAdapter;
import com.twsyt.merchant.model.order.OrderHistory;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTrackerPageFragment extends Fragment {

    private ArrayList<OrderHistory> list;

    public OrderTrackerPageFragment() {
        // Required empty public constructor
    }

    public static OrderTrackerPageFragment newInstance(ArrayList<OrderHistory> list) {
        OrderTrackerPageFragment myFragment = new OrderTrackerPageFragment();
        Bundle args = new Bundle();
        args.putSerializable(AppConstants.ORDER_TRACKER_TYPE, list);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = (getArguments() != null) ? (ArrayList<OrderHistory>) getArguments().getSerializable(AppConstants.ORDER_TRACKER_TYPE) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_tracker, container, false);
        Context context = getContext();
        setupRvAdapter(view,context);
        return view;
    }

    /**
     * Setup the Rv Adapter for fragment.
     *
     * @param view
     */
    private void setupRvAdapter(View view, Context context) {
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.orderTrackerRecyclerView);
        OrderTrackerRVAdapter adapter = new OrderTrackerRVAdapter(list,context);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
    }
}
