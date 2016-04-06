package com.twsyt.merchant.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.model.menu.Items;
import com.twsyt.merchant.model.menu.OrderAction;
import com.twsyt.merchant.model.menu.TimeStamp;

import java.util.ArrayList;

/**
 * Created by Raman on 4/5/2016.
 */
public class OrderStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final Context mContext;
    private ArrayList<OrderAction> mActionList;

    public OrderStatusAdapter(Context context, ArrayList<OrderAction> actionList) {
        this.mContext = context;
        this.mActionList = actionList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_order_status_row, parent, false);
        RowsViewHolder vh = new RowsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowsViewHolder mHolder = (RowsViewHolder) holder;

        TimeStamp timeStamp = Utils.getTimeStamp(mActionList.get(position).getActionTime());
        String time = timeStamp.getTime();

        if(mActionList!=null){
            mHolder.TimeTV.setText(time);
            mHolder.OrderStateTV.setText(mActionList.get(position).getActionType());
        }
    }

    @Override
    public int getItemCount() {
        if (mActionList != null)
            return mActionList.size();
        else
            return 0;
    }

    public class RowsViewHolder extends RecyclerView.ViewHolder {
        TextView TimeTV;
        TextView OrderStateTV;

        public RowsViewHolder(View itemView) {
            super(itemView);
            TimeTV = (TextView) itemView.findViewById(R.id.tv_time);
            OrderStateTV = (TextView) itemView.findViewById(R.id.tv_order_state);
        }
    }
}
