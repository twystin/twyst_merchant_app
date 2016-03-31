package com.twsyt.merchant.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.twsyt.merchant.R;
import com.twsyt.merchant.adapters.OrderTrackerAdapter;
import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.Order.OrderHistory;
import com.twsyt.merchant.service.HttpService;

import receivers.OrderTrackerResultReceiver;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements OrderTrackerResultReceiver.Receiver {

    private ViewPager mViewPager;
    private String mOrderID = "56fb605ce50eb9a2051171c5";
    private String token = "HAba02nFxNIrQGreYIv9JUev078YDF2q";
    private OrderHistory mOrderHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupToolBar();
        setup();
        getOrder(mOrderID);
    }

    private void getOrder(String mOrderID) {

        HttpService.getInstance().getOrderDetail(mOrderID, token, new Callback<BaseResponse<OrderHistory>>() {
                    @Override
                    public void success(BaseResponse<OrderHistory> orderHistoryBaseResponse, Response response) {
                        if (orderHistoryBaseResponse.isResponse()) {
                            mOrderHistory = orderHistoryBaseResponse.getData();
                            Log.i("Order Details", String.valueOf(mOrderHistory));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                }
        );

    }

    private void setup() {
        // Setup the ViewPager
        mViewPager = (ViewPager) findViewById(R.id.orderTrackerPager);
        OrderTrackerAdapter pagerAdapter = new OrderTrackerAdapter(getSupportFragmentManager(), null);
        mViewPager.setAdapter(pagerAdapter);

        // Setup the Tab Layout
        TabLayout slidingTabs_orderTracker = (TabLayout) findViewById(R.id.slidingTabs_orderTracker);
        slidingTabs_orderTracker.setupWithViewPager(mViewPager);
        slidingTabs_orderTracker.setTabsFromPagerAdapter(pagerAdapter);
    }

    public void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
