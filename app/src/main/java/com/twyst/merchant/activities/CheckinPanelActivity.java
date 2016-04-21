package com.twyst.merchant.activities;

import android.os.Bundle;

import com.twyst.merchant.R;

public class CheckinPanelActivity extends BaseActionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_panel);
        setupToolBar();
    }
}
