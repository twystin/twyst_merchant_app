package com.twsyt.merchant.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.twsyt.merchant.R;
import com.twsyt.merchant.TwystApplication;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.adapters.OrderTrackerFragmentAdapter;
import com.twsyt.merchant.fragments.OrderTrackerPageFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    TabLayout slidingTabs_orderTracker;
    OrderTrackerFragmentAdapter mPagerAdapter;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Generate a broadcast receiver. This will be used to get updates from service. This is registered in
        // onStart and unregistered in onStop of this Activity.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int resultCode = intent.getIntExtra(AppConstants.NEW_DATA_AVAILABLE, 0);
                notifyAllFrags(resultCode);
            }
        };

        Utils.checkAndStartWebSocketService(MainActivity.this);
        setupToolBar();
        setupFragmentAdapter();
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

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mReceiver, new IntentFilter(AppConstants.INTENT_DOWNLOADED_ORDER));
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyAllFrags(AppConstants.DOWNLOAD_SUCCESS);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mReceiver);
    }

    /**
     * Setup the adpater for all fragments. Since we have a lot of fragments, OrderTrackerFragmentAdapter extends
     * FragmentStatePagerAdapter.
     */
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


    /**
     * Get list of all created fragments and notify their respective adapters.
     * Also update all the tab names with correct counts.
     *
     * @param resultCode result code received from service.
     *                   1: new Data available.
     *                   2: TODO - Add more for good UX.
     */
    public void notifyAllFrags(int resultCode) {
        if (resultCode == AppConstants.DOWNLOAD_SUCCESS) {
            // Tab names update
            if ((slidingTabs_orderTracker == null)) {
                return;
            }

            int numTabs = slidingTabs_orderTracker.getTabCount();
            for (int i = 0; i < numTabs; i++) {
                // Updating all tab names. Need to see if possible to update only required
                // tab names.
                slidingTabs_orderTracker.getTabAt(i).setText(mPagerAdapter.getPageTitle(i));
            }

            // Notify adapter in all running fragments.
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
