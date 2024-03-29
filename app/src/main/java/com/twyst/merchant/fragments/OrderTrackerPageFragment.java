package com.twyst.merchant.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twyst.merchant.R;
import com.twyst.merchant.Util.AppConstants;
import com.twyst.merchant.Util.OrdersDataBaseSingleTon;
import com.twyst.merchant.activities.MainActivity;
import com.twyst.merchant.adapters.OrderTrackerRVAdapter;
import com.twyst.merchant.model.order.OrderHistory;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderTrackerPageFragment extends Fragment {

    SwipeRefreshLayout mSwipeRefreshLayout;
    private final String LOG_TAG = OrderTrackerPageFragment.this.getClass().getSimpleName();
    private int mPosition;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mSwipeRefreshLayout = ((MainActivity) context).mSwipeRefreshLayout;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSwipeRefreshLayout = null;
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

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.orderTrackerRecyclerView);
        rv.setHasFixedSize(true);

        final LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        orderTrackerRVAdapter = new OrderTrackerRVAdapter(getContext());
        updateList();
        rv.setAdapter(orderTrackerRVAdapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mSwipeRefreshLayout != null) {
                    // Two possible solutions.. Use either of them

                    // Solution 1.
                    mSwipeRefreshLayout.setEnabled(llm.findFirstCompletelyVisibleItemPosition() == 0);

                    // Solution 2.
//                    int topRowVerticalPosition =
//                            (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
//                    mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    public void updateList() {
//        Log.d(LOG_TAG, "Checking for updateList call");
        ArrayList<OrderHistory> orderList = OrdersDataBaseSingleTon.getInstance(getContext())
                .getSortedOrdersList(AppConstants.ORDER_TRACK_TABS_LIST[mPosition]);
        orderTrackerRVAdapter.getOrderList().clear();
        orderTrackerRVAdapter.getOrderList().addAll(orderList);
        orderTrackerRVAdapter.notifyDataSetChanged();
    }
}
