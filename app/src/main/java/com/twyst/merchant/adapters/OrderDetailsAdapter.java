package com.twyst.merchant.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.merchant.R;
import com.twyst.merchant.Util.AppConstants;
import com.twyst.merchant.model.menu.Items;

import java.util.ArrayList;

/**
 * Created by Raman on 4/5/2016.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private ArrayList<Items> mItemList;


    public OrderDetailsAdapter(Context context, ArrayList<Items> itemsList) {
        mContext = context;
        mItemList = itemsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_order_details_row, parent, false);
        RowsViewHolder vh = new RowsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowsViewHolder mHolder = (RowsViewHolder) holder;
        mHolder.ItemNameTV.setText(mItemList.get(position).getItemName());
        mHolder.ItemQuantityTV.setText("x" + mItemList.get(position).getItemQuantity());
        mHolder.ItemAmountTV.setText(AppConstants.INDIAN_RUPEE_SYMBOL + " " + Double.toString(mItemList.get(position).getItemCost()));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class RowsViewHolder extends RecyclerView.ViewHolder {
        TextView ItemNameTV;
        TextView ItemQuantityTV;
        TextView ItemAmountTV;

        public RowsViewHolder(View itemView) {
            super(itemView);
            ItemNameTV = (TextView) itemView.findViewById(R.id.tv_item_name);
            ItemQuantityTV = (TextView) itemView.findViewById(R.id.tv_item_quantity);
            ItemAmountTV = (TextView) itemView.findViewById(R.id.tv_item_amount);
        }
    }
}
