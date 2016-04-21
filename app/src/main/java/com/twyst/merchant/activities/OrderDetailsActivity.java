package com.twyst.merchant.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import com.twyst.merchant.R;
import com.twyst.merchant.Util.AppConstants;
import com.twyst.merchant.Util.OrdersDataBaseSingleTon;
import com.twyst.merchant.Util.TwystProgressHUD;
import com.twyst.merchant.Util.Utils;
import com.twyst.merchant.adapters.OrderDetailsAdapter;
import com.twyst.merchant.adapters.OrderStatusAdapter;
import com.twyst.merchant.layout.CustomSwipeRefreshLayout;
import com.twyst.merchant.model.BaseResponse;
import com.twyst.merchant.model.LoginResponse;
import com.twyst.merchant.model.menu.Items;
import com.twyst.merchant.model.menu.OrderAction;
import com.twyst.merchant.model.order.Address;
import com.twyst.merchant.model.order.OrderHistory;
import com.twyst.merchant.model.order.OrderUpdate;
import com.twyst.merchant.service.HttpService;

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
    public static final int REQUEST_CALL = 9999;
    public static final int USER = 0;
    public static final int OUTLET = 1;
    String userPhone;
    String outletPhone;
    OrderUpdate orderUpdate;
    String orderStatus;
    OrderAction orderPlacedAction;
    LinearLayout acceptLL;
    LinearLayout rejectLL;
    LinearLayout dispatchedLL;
    LinearLayout abandonedLL;
    LinearLayout deliveredLL;
    LinearLayout orderActionsLL;
    String mPhoneNum;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra(AppConstants.NEW_DATA_AVAILABLE, 0);
            if (resultCode == AppConstants.DOWNLOAD_SUCCESS) {
                OrderHistory order = OrdersDataBaseSingleTon.getInstance(OrderDetailsActivity.this)
                        .getOrderFromOrderId(orderId);
                genActionList(order);
                orderStatusAdapter.notifyDataSetChanged();
            }
        }
    };

    private void genActionList(OrderHistory order) {
        actionList.clear();
        actionList.add(orderPlacedAction);
        if (order.getOrderActionsList().size() != 0)
            actionList.addAll(order.getOrderActionsList());
    }

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
                                genActionList(order);
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
        orderPlacedAction = new OrderAction();
        orderPlacedAction.setActionTime(order.getOrderDate());
        orderPlacedAction.setActionType("PLACED");
        orderPlacedAction.setMessage("Order has been placed by the User.");
        updateOrderDetails(order);
    }

    private void updateOrderDetails(OrderHistory orderHistory) {
        ArrayList<Items> itemsList = orderHistory.getItems();
        genActionList(orderHistory);

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
        final View dialogView = LayoutInflater.from(OrderDetailsActivity.this).inflate(R.layout.layout_dialog, null);
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
                                                                     dialog.dismiss();
                                                                     mPhoneNum = phone_number;
                                                                     if (ActivityCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                                         //    ActivityCompat#requestPermissions
                                                                         // here to request the missing permissions, and then overriding
                                                                         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                                         //                                          int[] grantResults)
                                                                         // to handle the case where the user grants the permission. See the documentation
                                                                         // for ActivityCompat#requestPermissions for more details.
                                                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                             requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                                                                         }
                                                                     } else {
                                                                         makeTheCall();
                                                                     }
                                                                 }
                                                             }
        );
    }

    private void makeTheCall() {
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + mPhoneNum));
        startActivity(call);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(OrderDetailsActivity.this, "Call Permission granted", Toast.LENGTH_SHORT).show();
                    makeTheCall();
                } else {
                    Toast.makeText(OrderDetailsActivity.this, "Call Permission Denied, Can not make the call", Toast.LENGTH_SHORT).show();

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
//        orderUpdate.set_id(order.getOrderID());
        orderUpdate.setAm_email(loginResp.getEmail());

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
                boolean showDispatch = true;
                showAbandonedButton();
                showDeliveredButton();
                if (actionList != null) {
                    for (int i = 0; i < actionList.size(); i++) {
                        if (actionList.get(i).getActionType().equals(AppConstants.ORDER_DISPATCHED)) {
                            showDispatch = false;
                            break;
                        }
                    }
                }
                if (showDispatch)
                    showDispatchedButton();
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
                    showDialogConfirmation(AppConstants.REJECT);
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
                    showDialogConfirmation(AppConstants.ACCEPT);
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
                    showDialogConfirmation(AppConstants.DISPATCH);
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
                    showDialogConfirmation(AppConstants.ABANDONED);
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
                    showDialogConfirmation(AppConstants.DELIVERED);
                }
            });
        }
    }

    private void makeActionsVisible() {
        if (orderActionsLL != null) {
            orderActionsLL.setVisibility(View.VISIBLE);
        }
    }

    private void updateOrderWithServer(String s) {
        orderUpdate.setUpdate_type(s);
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        HttpService.getInstance().putOrderUpdate(orderId, Utils.getUserToken(OrderDetailsActivity.this), orderUpdate, new Callback<BaseResponse<OrderHistory>>() {
            @Override
            public void success(BaseResponse<OrderHistory> baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    OrderHistory order = baseResponse.getData();
                    OrdersDataBaseSingleTon.getInstance(OrderDetailsActivity.this).addOrUpdateOrder(orderId, order);
                    OrdersDataBaseSingleTon.getInstance(OrderDetailsActivity.this).storeInSharedPrefs();
                    finish();
                }
                twystProgressHUD.dismiss();
                hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                hideSnackbar();
                handleRetrofitError(error);
            }
        });
    }

    private void hideAllActionButtons() {
        acceptLL.setVisibility(View.GONE);
        rejectLL.setVisibility(View.GONE);
        dispatchedLL.setVisibility(View.GONE);
        abandonedLL.setVisibility(View.GONE);
        deliveredLL.setVisibility(View.GONE);
        if (orderActionsLL.getVisibility() == View.VISIBLE) {
            orderActionsLL.setVisibility(View.GONE);
        }
    }


    private void showDialogConfirmation(final String action) {

        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        final View dialogView = LayoutInflater.from(OrderDetailsActivity.this).inflate(R.layout.layout_dialog, null);
        TextView tv = (TextView) dialogView.findViewById(R.id.tvTitle);
        tv.setText("Do you wish to proceed?");
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
                                                                     dialog.dismiss();
                                                                     updateOrderWithServer(action);
                                                                 }
                                                             }
        );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String s = getIntent().getStringExtra(AppConstants.INTENT_START_SOURCE);
        if (s != null) {
            if (s.equals(AppConstants.START_SOURCE_IS_NOTIFICATION)) {
                startActivity(new Intent(OrderDetailsActivity.this, MainActivity.class));
            }
        }
    }
}
