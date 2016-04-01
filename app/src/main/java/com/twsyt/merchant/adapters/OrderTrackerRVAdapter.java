package com.twsyt.merchant.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.model.order.OrderHistory;

import java.util.ArrayList;
import java.util.List;

public class OrderTrackerRVAdapter extends RecyclerView.Adapter<OrderTrackerRVAdapter.MyViewHolder> {

    private ArrayList<OrderHistory> mOrderList;

    public OrderTrackerRVAdapter(List<OrderHistory> orderList) {
        this.mOrderList = (ArrayList<OrderHistory>) orderList;
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
                Intent intent = new Intent(); // TODO - mention class name
                intent.putExtra(AppConstants.ORDER_DETAIL, mOrderList.get(position));
                v.getContext().startActivity(intent);
            }
        });

        // time stamp
        holder.tv_timeStamp.setText(""); // TODO

        // Current status of the order
        holder.tv_orderStatus.setText(order.getOrderStatus());

        // Order Number - this is NOT order ID.
        holder.tv_orderId.setText(order.getOrderNumber());

        // Name of the outlet
        holder.tv_outletName.setText(order.getOutletName());

        // Mode/Current Status of Payment
        holder.tv_paymentType.setText(""); // TODO - check if payment will be available in order object.

        // Order Cost
        holder.tv_orderCost.setText(String.valueOf(order.getOrderCost()));

        // User Name
        holder.tv_username.setText(""); // TODO - why no name in order object?

        // User address
        holder.tv_address.setText(""); // TODO - need to see which address object to use.

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
