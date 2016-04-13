package com.twsyt.merchant.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.OrdersDataBaseSingleTon;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.adapters.OrderDetailsAdapter;
import com.twsyt.merchant.adapters.OrderStatusAdapter;
import com.twsyt.merchant.layout.CustomSwipeRefreshLayout;
import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.LoginResponse;
import com.twsyt.merchant.model.menu.Items;
import com.twsyt.merchant.model.menu.OrderAction;
import com.twsyt.merchant.model.order.Address;
import com.twsyt.merchant.model.order.OrderHistory;
import com.twsyt.merchant.model.order.OrderUpdate;
import com.twsyt.merchant.service.HttpService;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OrderDetailsActivity extends BaseActionActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    ArrayList<OrderAction> actionList = new ArrayList<>();
    String orderId;
    OrderStatusAdapter orderStatusAdapter;
    RecyclerView orderStatusRV;
    public static final int REQUEST_CALL = 1;
    public static final int USER = 0;
    public static final int OUTLET = 1;
    String userPhone;
    String outletPhone;
    OrderUpdate orderUpdate;
    String orderStatus;

    LinearLayout acceptLL;
    LinearLayout rejectLL;
    LinearLayout dispatchedLL;
    LinearLayout abandonedLL;
    LinearLayout deliveredLL;
    LinearLayout orderActionsLL;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra(AppConstants.NEW_DATA_AVAILABLE, 0);
            if (resultCode == AppConstants.DOWNLOAD_SUCCESS) {
                OrderHistory order = OrdersDataBaseSingleTon.getInstance(OrderDetailsActivity.this)
                        .getOrderFromOrderId(orderId);
                actionList.clear();
                actionList.addAll(order.getOrderActionsList());
                orderStatusAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        setupToolBar();
        setupSwipeRefresh();
        processExtraData();
        ((CustomSwipeRefreshLayout) mSwipeRefreshLayout).setScrollView((ScrollView) findViewById(R.id.scrollView));
        orderActionsLL = (LinearLayout) findViewById(R.id.ll_order_actions);
        acceptLL = (LinearLayout) findViewById(R.id.ll_accept);
        rejectLL = (LinearLayout) findViewById(R.id.ll_reject);
        dispatchedLL = (LinearLayout) findViewById(R.id.ll_dispatched);
        abandonedLL = (LinearLayout) findViewById(R.id.ll_abandoned);
        deliveredLL = (LinearLayout) findViewById(R.id.ll_delivered);

        setOnClickActions();
    }

    @Override
    protected void swipeRefresh() {
        if (orderId != null) {
            HttpService.getInstance().getOrderDetail(orderId, Utils.getUserToken(this), new Callback<BaseResponse<OrderHistory>>() {
                        @Override
                        public void success(BaseResponse<OrderHistory> orderHistoryBaseResponse, Response response) {
                            if (orderHistoryBaseResponse.isResponse()) {
                                OrderHistory order = orderHistoryBaseResponse.getData();
                                OrdersDataBaseSingleTon.getInstance(OrderDetailsActivity.this).addOrUpdateOrder(orderId, order);
                                OrdersDataBaseSingleTon.getInstance(OrderDetailsActivity.this).storeInSharedPrefs();
                                actionList.clear();
                                actionList.addAll(order.getOrderActionsList());
                                orderStatusAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(OrderDetailsActivity.this, orderHistoryBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            hideSnackbar();
                            mSwRefreshing = false;
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setEnabled(true);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            hideSnackbar();
                            handleRetrofitError(error);
                            mSwRefreshing = false;
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setEnabled(true);
                        }
                    }
            );
        } else {
            mSwRefreshing = false;
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    private void processExtraData() {
        orderId = getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        OrderHistory order = OrdersDataBaseSingleTon.getInstance(this).getOrderFromOrderId(orderId);
        updateOrderDetails(order);
    }

    private void updateOrderDetails(OrderHistory orderHistory) {
        ArrayList<Items> itemsList = orderHistory.getItems();
        if (orderHistory.getOrderActionsList().size() != 0) {
            actionList.addAll(orderHistory.getOrderActionsList());
        }

        LinearLayout callOutletLL = (LinearLayout) findViewById(R.id.ll_call_outlet);
        LinearLayout callUserLL = (LinearLayout) findViewById(R.id.ll_call_user);

        if (orderHistory.getUser() != null) {
            Address userAddress = orderHistory.getAddress();
            String address = userAddress.getLine1() + " " + userAddress.getLine2() + " " + userAddress.getCity();
            String userName = orderHistory.getUser().getFirst_name() + " " + orderHistory.getUser().getLast_name();
            userPhone = orderHistory.getUser().getPhone_number();
            outletPhone = orderHistory.getPhone();

            TextView userNameTV = (TextView) findViewById(R.id.tv_user_name);
            TextView userAddressTV = (TextView) findViewById(R.id.tv_user_address);

            if (userNameTV != null) {
                userNameTV.setText(userName);
            }
            if (userAddressTV != null) {
                userAddressTV.setText(address);
            }

            if (callUserLL != null) {
                callUserLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogCall(userPhone, USER);
                    }
                });
            }

            if (callOutletLL != null) {
                callOutletLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogCall(outletPhone, OUTLET);
                    }
                });
            }
        }

        TextView orderCostTV = (TextView) findViewById(R.id.tv_order_amount);
        if (orderCostTV != null) {
            orderCostTV.setText(AppConstants.INDIAN_RUPEE_SYMBOL + " " + Double.toString(orderHistory.getOrderCost()));
        }

        TextView outletNameTV = (TextView) findViewById(R.id.tv_outlet_name);
        if (outletNameTV != null) {
            outletNameTV.setText(orderHistory.getOutletName());
        }

        updateOrderDetails(itemsList);
        setupOrderStatusRV();


        String paymentMode;
        if (orderHistory.getPaymentInfo().is_inapp()) {
            paymentMode = "Paid Online";
        } else {
            paymentMode = "COD";
        }

        TextView paymentTV = (TextView) findViewById(R.id.tv_payement_mode);
        if (paymentTV != null) {
            paymentTV.setText(paymentMode);
        }
    }

    private void updateOrderDetails(ArrayList<Items> itemsList) {
        setupOrderDetailsRV(itemsList);
    }

    private void setupOrderDetailsRV(ArrayList<Items> itemsList) {
        RecyclerView orderDetailsRV = (RecyclerView) findViewById(R.id.rv_orderDetails);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        if (orderDetailsRV != null) {
            orderDetailsRV.setLayoutManager(mLayoutManager);
        }
        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, itemsList);
        if (orderDetailsRV != null) {
            orderDetailsRV.setAdapter(orderDetailsAdapter);
        }

    }

    private void setupOrderStatusRV() {
        orderStatusRV = (RecyclerView) findViewById(R.id.rv_orderStatus);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        orderStatusRV.setLayoutManager(mLayoutManager);
        orderStatusAdapter = new OrderStatusAdapter(OrderDetailsActivity.this, actionList);
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

    private void showDialogCall(final String phone_number, int FLAG) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        final View dialogView = LayoutInflater.from(OrderDetailsActivity.this).inflate(R.layout.dialog_call, null);
        TextView tv = (TextView) dialogView.findViewById(R.id.tvTitle);
        switch (FLAG) {
            case USER:
                tv.setText("Call the User?");
                break;
            case OUTLET:
                tv.setText("Call the Outlet?");
        }

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.fCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.fOK).setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View v) {
                                                                     if (ActivityCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                                         ActivityCompat.requestPermissions(OrderDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                                                                     }
                                                                     Intent call = new Intent(Intent.ACTION_CALL);
                                                                     call.setData(Uri.parse("tel:" + phone_number));
                                                                     startActivity(call);
                                                                     dialog.dismiss();
                                                                 }
                                                             }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(OrderDetailsActivity.this, "Call Permission granted", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(OrderDetailsActivity.this, "Call Permission Denied, Can not make the call", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setOnClickActions() {
//        LinearLayout deliveredLL = (LinearLayout)findViewById(R.id.ll_delivered);

        SharedPreferences sp = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        LoginResponse loginResp = new Gson().fromJson(sp.getString(AppConstants.LOGIN_RESPONSE_JSON, null), type);

        OrderHistory order = OrdersDataBaseSingleTon.getInstance(OrderDetailsActivity.this)
                .getOrderFromOrderId(orderId);

        orderStatus = order.getOrderStatus();

        orderUpdate = new OrderUpdate();
        orderUpdate.set_id(order.getOrderID());
        orderUpdate.setAm_email(loginResp.getProfile().getEmail());

        chooseActions();

    }


    private void chooseActions() {
        switch (orderStatus) {
            case AppConstants.ORDER_PENDING: {
                showAcceptButton();
                showRejectButton();
                break;
            }

            case AppConstants.ORDER_ACCEPTED: {
                showDispatchedButton();
                break;
            }

            case AppConstants.ORDER_LATE_ACCEPT: {
                showAcceptButton();
                showRejectButton();
                break;
            }

            case AppConstants.ORDER_LATE_DELIVERY: {
                showAbandonedButton();
                showDispatchedButton();
                showDeliveredButton();
                break;
            }

            default:
                hideAllActionButtons();
        }
    }

    private void showRejectButton() {
        makeActionsVisible();
        rejectLL.setVisibility(View.VISIBLE);
        if (rejectLL != null) {
            rejectLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderwithServer(AppConstants.REJECT);
                }
            });
        }
    }

    private void showAcceptButton() {
        makeActionsVisible();
        acceptLL.setVisibility(View.VISIBLE);
        if (acceptLL != null) {
            acceptLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderwithServer(AppConstants.ACCEPT);
                }
            });
        }
    }

    private void showDispatchedButton() {
        makeActionsVisible();
        dispatchedLL.setVisibility(View.VISIBLE);
        if (dispatchedLL != null) {
            dispatchedLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderwithServer(AppConstants.DISPATCH);
                }
            });
        }
    }

    private void showAbandonedButton() {
        makeActionsVisible();
        abandonedLL.setVisibility(View.VISIBLE);
        if (abandonedLL != null) {
            abandonedLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderwithServer(AppConstants.ABANDONED);
                }
            });
        }
    }

    private void showDeliveredButton() {
        makeActionsVisible();
        deliveredLL.setVisibility(View.VISIBLE);
        if (deliveredLL != null) {
            deliveredLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderwithServer(AppConstants.DELIVERED);
                }
            });
        }
    }

    private void makeActionsVisible() {
        if (orderActionsLL != null) {
            orderActionsLL.setVisibility(View.VISIBLE);
        }
    }

    private void updateOrderwithServer(String s) {
        orderUpdate.setUpdate_type(s);
        HttpService.getInstance().putOrderUpdate(orderId, Utils.getUserToken(OrderDetailsActivity.this), orderUpdate, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    hideAllActionButtons();
                    chooseActions();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void hideAllActionButtons() {
        acceptLL.setVisibility(View.GONE);
        rejectLL.setVisibility(View.GONE);
        dispatchedLL.setVisibility(View.GONE);
        abandonedLL.setVisibility(View.GONE);
        deliveredLL.setVisibility(View.GONE);
        if(orderActionsLL.getVisibility() == View.VISIBLE){
            orderActionsLL.setVisibility(View.GONE);
        }
    }

}
