package com.twsyt.merchant.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.activities.OrderDetailsActivity;
import com.twsyt.merchant.model.order.Address;
import com.twsyt.merchant.model.order.OrderHistory;

import java.util.ArrayList;

public class OrderTrackerRVAdapter extends RecyclerView.Adapter<OrderTrackerRVAdapter.MyViewHolder> {

    // Don't change the below assignment.
    // This list gets updated from MainActivity via getOrderList() call.
    private ArrayList<OrderHistory> mOrderList = new ArrayList<>();
    Context mContext;

    public OrderTrackerRVAdapter(Context context) {
//        this.mOrderList = (ArrayList<OrderHistory>) orderList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_tracker_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        OrderHistory order = mOrderList.get(position);

        // Setting an click listener on complete row. Need to open full details if clicked.
        holder.order_tracker_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                intent.putExtra(AppConstants.INTENT_ORDER_ID, mOrderList.get(position).getOrderID());
                v.getContext().startActivity(intent);
            }
        });


        // time stamp
        if (order.getOrderDate() != null) {
            String time = Utils.getTimeStamp(order.getOrderDate()).getTime();
            holder.tv_timeStamp.setText(time);
        }

        // Current status of the order
        holder.tv_orderStatus.setText(order.getOrderStatus());

        // Order Number - this is NOT order ID.
        holder.tv_orderId.setText(order.getOrderNumber());

        // Name of the outlet
        holder.tv_outletName.setText(order.getOutletName());

        // Mode/Current Status of Payment

        if (order.getPaymentInfo() != null) {
            String paymentMode = "";
            if (order.getPaymentInfo().is_inapp()) {
                paymentMode = "PAID ONLINE";
            } else
                paymentMode = "TAKE PAYMENT";
            holder.tv_paymentType.setText(paymentMode);
        } // TODO - check if payment will be available in order object.

        // Order Cost
        holder.tv_orderCost.setText(String.valueOf(order.getOrderCost()));

        // User Name
        if (order.getUser() != null) {
            if (order.getUser().getFirst_name() != null) {
                String name = order.getUser().getFirst_name();
                if (order.getUser().getLast_name() != null)
                    name += " " + order.getUser().getLast_name();
                holder.tv_username.setText(name);
            } // TODO - why no name in order object?
        }

        // User address
        String s = "";
        if (order.getAddress() != null) {
            Address address = order.getAddress();
            if (address.getLine1() != null)
                s += address.getLine1();
            if (address.getLine2() != null)
                s += " " + address.getLine2();
            if (address.getCity() != null)
                s += ", " + address.getCity();

        }
        holder.tv_address.setText(s); // TODO - need to see which address object to use.

        // Load user image.
        // TODO - Currently image not available in order object. Check where to get it from.
        Glide.with(holder.iv_userImage.getContext())
                .load(order.getBackground()) // This is not background. Should be user image. temp only;
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_userImage);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public ArrayList<OrderHistory> getOrderList() {
        return mOrderList;
    }

    /**
     * View Holder for this our recycler view adapter
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_timeStamp;
        public TextView tv_orderStatus;
        public TextView tv_orderId;
        public TextView tv_outletName;
        public TextView tv_paymentType;
        public TextView tv_orderCost;
        public ImageView iv_userImage;
        public TextView tv_username;
        public TextView tv_address;
        public View order_tracker_row;

        public MyViewHolder(View itemView) {
            super(itemView);
            order_tracker_row = itemView;
            tv_timeStamp = (TextView) itemView.findViewById(R.id.tv_timeStamp);
            tv_orderStatus = (TextView) itemView.findViewById(R.id.tv_orderStatus);
            tv_orderId = (TextView) itemView.findViewById(R.id.tv_orderId);
            tv_outletName = (TextView) itemView.findViewById(R.id.tv_outletName);
            tv_paymentType = (TextView) itemView.findViewById(R.id.tv_paymentType);
            tv_orderCost = (TextView) itemView.findViewById(R.id.tv_orderCost);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            iv_userImage = (ImageView) itemView.findViewById(R.id.iv_userImage);
        }
    }
}
