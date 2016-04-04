package com.twsyt.merchant.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.adapters.OrderTrackerFragmentAdapter;
import com.twsyt.merchant.fragments.OrderTrackerPageFragment;
import com.twsyt.merchant.receivers.OrderTrackerResultReceiver;
import com.twsyt.merchant.service.WebSocketService;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    TabLayout slidingTabs_orderTracker;
    OrderTrackerFragmentAdapter mPagerAdapter;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int resultCode = intent.getIntExtra(AppConstants.NEW_DATA_AVAILABLE, 0);
                notifyAllFrags(resultCode);
            }
        };

        String channelName = getIntent().getStringExtra(AppConstants.INTENT_EXTRA_CHANNEL_NAME);

        Intent intent = new Intent(this, WebSocketService.class);
        startService(intent);

        setupToolBar();
        setupFragmentAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mReceiver, new IntentFilter(AppConstants.INTENT_DOWNLOADED_ORDER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mReceiver);
    }

    private void setupFragmentAdapter() {
        // Setup the ViewPager
        ViewPager mViewPager = (ViewPager) findViewById(R.id.orderTrackerPager);
        mPagerAdapter = new OrderTrackerFragmentAdapter(MainActivity.this, getSupportFragmentManager());
        if (mViewPager != null) {
            mViewPager.setAdapter(mPagerAdapter);
        }

        // Setup the Tab Layout
        slidingTabs_orderTracker = (TabLayout) findViewById(R.id.slidingTabs_orderTracker);
        if (slidingTabs_orderTracker != null) {
            slidingTabs_orderTracker.setupWithViewPager(mViewPager);
        }
    }

    public void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }

    /**
     * Overridden method for interface:  Receiver, class: OrderTrackerResultReceiver.
     * Provides the below given params whenever a new message arrives in service.
     *
     * @param resultCode
     */
    public void notifyAllFrags(int resultCode) {
        if (resultCode == AppConstants.DOWNLOAD_SUCCESS) {
            int numTabs = slidingTabs_orderTracker.getTabCount();
            for (int i = 0; i < numTabs; i++) {
                slidingTabs_orderTracker.getTabAt(i).setText(mPagerAdapter.getPageTitle(i));
            }
            List<Fragment> frags = getSupportFragmentManager().getFragments();
            if (frags != null) {
                for (Fragment f : frags) {
                    if (f instanceof OrderTrackerPageFragment) {
                        ((OrderTrackerPageFragment) f).updateList();
                    }
                }
            }
        }
    }
}
