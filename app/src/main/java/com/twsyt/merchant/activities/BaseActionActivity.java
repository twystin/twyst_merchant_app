package com.twsyt.merchant.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.twsyt.merchant.R;

//import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by tushar on 16/02/16.
 */
public class BaseActionActivity extends AppCompatActivity {


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

/*    public void hideProgressHUDInLayout() {
        CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
    }*/
}
