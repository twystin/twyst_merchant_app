package com.twsyt.merchant.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.adapters.OrderDetailsAdapter;
import com.twsyt.merchant.adapters.OrderStatusAdapter;
import com.twsyt.merchant.model.menu.Items;
import com.twsyt.merchant.model.menu.OrderAction;
import com.twsyt.merchant.model.order.Address;
import com.twsyt.merchant.model.order.OrderHistory;

import java.util.ArrayList;


public class OrderDetailsActivity extends BaseActionActivity {
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        setupToolBar();

        Intent intent = getIntent();
        OrderHistory orderHistory = (OrderHistory) intent.getSerializableExtra(AppConstants.ORDER_DETAIL);

        updateOrderDetails(orderHistory);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                int resultCode = intent.getIntExtra(AppConstants.NEW_DATA_AVAILABLE, 0);
            }
        };

    }

    private void updateOrderDetails(OrderHistory orderHistory) {
        ArrayList<Items> itemsList = orderHistory.getItems();
        ArrayList<OrderAction> actionList = null;
        if(orderHistory.getOrderActionsList().size()!= 0){
            actionList = orderHistory.getOrderActionsList();
        }
        String paymentMode;

        if (orderHistory.getUser() != null) {
            Address userAddress = orderHistory.getAddress();
            String address = userAddress.getLine1() + " " + userAddress.getLine2() + " " + userAddress.getCity();
            String userName = orderHistory.getUser().getFirst_name() + " " + orderHistory.getUser().getLast_name();

            TextView userNameTV = (TextView) findViewById(R.id.tv_user_name);
            TextView userAddressTV = (TextView) findViewById(R.id.tv_user_address);

            userNameTV.setText(userName);
            userAddressTV.setText(address);
        }

        TextView orderCostTV = (TextView) findViewById(R.id.tv_order_amount);
        orderCostTV.setText(AppConstants.INDIAN_RUPEE_SYMBOL + " " + Double.toString(orderHistory.getOrderCost()));

        TextView outletNameTV = (TextView) findViewById(R.id.tv_outlet_name);
        outletNameTV.setText(orderHistory.getOutletName());

        updateOrderDetails(itemsList);
        updateOrderStatus(actionList);

        if (orderHistory.getPaymentInfo().is_inapp()) {
            paymentMode = "COD";
        } else {
            paymentMode = "Paid Online";
        }

        TextView paymentTV = (TextView) findViewById(R.id.tv_payement_mode);
        paymentTV.setText(paymentMode);
    }

    private void updateOrderDetails(ArrayList<Items> itemsList) {
        setupOrderDetailsRV(itemsList);
    }

    private void setupOrderDetailsRV(ArrayList<Items> itemsList) {
        RecyclerView orderDetailsRV = (RecyclerView) findViewById(R.id.rv_orderDetails);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        orderDetailsRV.setLayoutManager(mLayoutManager);
        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this,itemsList);
        orderDetailsRV.setAdapter(orderDetailsAdapter);

    }

    private void updateOrderStatus(ArrayList<OrderAction> actionList) {
        setupOrderStatusRV(actionList);
    }

    private void setupOrderStatusRV(ArrayList<OrderAction> actionList) {
        RecyclerView orderStatusRV = (RecyclerView) findViewById(R.id.rv_orderStatus);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        orderStatusRV.setLayoutManager(mLayoutManager);
        OrderStatusAdapter orderStatusAdapter = new OrderStatusAdapter(OrderDetailsActivity.this,actionList);
        orderStatusRV.setAdapter(orderStatusAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(OrderDetailsActivity.this).registerReceiver(mReceiver, new IntentFilter(AppConstants.INTENT_DOWNLOADED_ORDER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(OrderDetailsActivity.this).unregisterReceiver(mReceiver);
    }

}
