package com.twyst.merchant.layout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by tushar on 11/04/16.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private ScrollView scrollview;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(ScrollView view) {
        this.scrollview = view;
    }

    @Override
    public boolean canChildScrollUp() {
        return scrollview.getScrollY() != 0;
    }
}