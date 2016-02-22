package com.twsyt.merchant.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.twsyt.merchant.R;
import com.twsyt.merchant.adapters.OrderTrackerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolBar();
        setup();
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

    public void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
    }
}
