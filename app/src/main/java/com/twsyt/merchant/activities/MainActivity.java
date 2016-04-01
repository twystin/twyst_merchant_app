package com.twsyt.merchant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.adapters.OrderTrackerFragmentAdapter;
import com.twsyt.merchant.receivers.OrderTrackerResultReceiver;
import com.twsyt.merchant.service.WebSocketService;


public class MainActivity extends AppCompatActivity implements OrderTrackerResultReceiver.Receiver {

    private ViewPager mViewPager;
    TabLayout slidingTabs_orderTracker;

    OrderTrackerFragmentAdapter mPagerAdapter;
    private OrderTrackerResultReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        receiver = new OrderTrackerResultReceiver(new Handler());
        receiver.addReceiver(this);

        Intent intent = new Intent(this, WebSocketService.class);
        intent.putExtra(AppConstants.RESULT_RECEIVER, receiver);
        startService(intent);

        setupToolBar();
        setupFragmentAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        receiver = OrderTrackerResultReceiver.getInstance();
//        receiver = new OrderTrackerResultReceiver(new Handler());
//        receiver.addReceiver(this);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        receiver.removeReceiver(this);
//    }

    private void setupFragmentAdapter() {
        // Setup the ViewPager
        mViewPager = (ViewPager) findViewById(R.id.orderTrackerPager);
        mPagerAdapter = new OrderTrackerFragmentAdapter(MainActivity.this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        // Setup the Tab Layout
        slidingTabs_orderTracker = (TabLayout) findViewById(R.id.slidingTabs_orderTracker);
        slidingTabs_orderTracker.setupWithViewPager(mViewPager);
        slidingTabs_orderTracker.setTabsFromPagerAdapter(mPagerAdapter);
    }

    public void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Overridden method for interface:  Receiver, class: OrderTrackerResultReceiver.
     * Provides the below given params whenever a new message arrives in service.
     *
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == AppConstants.NEW_DATA_AVAILABLE) {
//            slidingTabs_orderTracker.setTabsFromPagerAdapter(mPagerAdapter);
            mPagerAdapter.notifyDataSetChanged();
        }
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

}
