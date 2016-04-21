package com.twsyt.merchant.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.OrdersDataBaseSingleTon;
import com.twsyt.merchant.Util.TwystProgressHUD;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.adapters.OrderTrackerFragmentAdapter;
import com.twsyt.merchant.fragments.OrderTrackerPageFragment;
import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.service.HttpService;
import com.twsyt.merchant.service.WebSocketService;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends BaseActionActivity {
    private static final int MENU_ITEM_LOGOUT = 0;
    TabLayout slidingTabs_orderTracker;
    OrderTrackerFragmentAdapter mPagerAdapter;
    BroadcastReceiver mReceiver;
    ViewPager mViewPager;

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
                Bundle bundle = intent.getExtras();
                if (mSwRefreshing) {
                    mSwRefreshing = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                }
                notifyAllFrags(resultCode, bundle);
            }
        };
        isNetworkAvailable();
        Utils.checkAndStartWebSocketService(MainActivity.this);
        setupToolBar();
        setupSwipeRefresh();
        setupFragmentAdapter();
        // Adding this processing after setupFragmentAdapter makes sure ViewPager is not null.
        processExtraData();
    }

    @Override
    protected void swipeRefresh() {
        syncWithServer();
    }

    private void processExtraData() {
        int tabPos = 0;
        String tabFromIntent = getIntent().getStringExtra(AppConstants.TAB_POSITION);
        if (tabFromIntent != null) {
            String tabTitleToShow = getIntent().getStringExtra(AppConstants.TAB_POSITION);
            for (int i = 0; i < AppConstants.ORDER_TRACK_TABS_LIST.length; i++) {
                if (tabTitleToShow.equals(AppConstants.ORDER_TRACK_TABS_LIST[i])) {
                    tabPos = i;
                    break;
                }
            }
        }
        mViewPager.setCurrentItem(tabPos, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mReceiver, new IntentFilter(AppConstants.INTENT_DOWNLOADED_ORDER));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvailable();
        notifyAllFrags(AppConstants.DOWNLOAD_SUCCESS, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void snackBarRetryActionListener() {
        if (isNetworkAvailable()) {
            syncWithServer();
        }
    }

    /**
     * Setup the adpater for all fragments. Since we have a lot of fragments, OrderTrackerFragmentAdapter extends
     * FragmentStatePagerAdapter.
     */
    private void setupFragmentAdapter() {
        // Setup the ViewPager
        mViewPager = (ViewPager) findViewById(R.id.orderTrackerPager);
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
    public void notifyAllFrags(int resultCode, Bundle bundle) {
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
        } else if (resultCode == AppConstants.DOWNLOAD_FAILED && (bundle != null)) {
            RetrofitError retrofitError = (RetrofitError) bundle.getSerializable(AppConstants.RETROFIT_FAILURE_ERROR);
            String responseMsg = bundle.getString(AppConstants.RETROFIT_FAILURE_NO_RESPONSE_MESSAGE);

            if (responseMsg != null) {
                Toast.makeText(this, responseMsg, Toast.LENGTH_SHORT).show();
            }

            if (retrofitError != null) {
                hideSnackbar();
                handleRetrofitError(retrofitError);
            }
        }
    }

    private void syncWithServer() {
        OrdersDataBaseSingleTon.getInstance(MainActivity.this).syncWithServer();
    }

    private boolean isNetworkAvailable() {
        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            buildAndShowSnackbarWithMessage(getResources().getString(R.string.no_internet_conn));
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;

            case R.id.action_checkinPanel:
                startActivity(new Intent(MainActivity.this, CheckinPanelActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        HttpService.getInstance().twystLogout(Utils.getUserToken(MainActivity.this), new Callback<BaseResponse<String>>() {
            @Override
            public void success(BaseResponse<String> stringBaseResponse, Response response) {
                if (stringBaseResponse.isResponse()) {
                    Toast.makeText(MainActivity.this, stringBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    stopService(new Intent(MainActivity.this, WebSocketService.class));
                    sharedPreferences.edit().clear().commit();
                    OrdersDataBaseSingleTon.setInstanceNull();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
}
