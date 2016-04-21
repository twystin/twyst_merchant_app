package com.twsyt.merchant.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.twsyt.merchant.R;

public class CheckinPanelActivity extends BaseActionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_panel);
        setupToolBar();
    }
}
