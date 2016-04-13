package com.twsyt.merchant.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.twsyt.merchant.R;

import retrofit.RetrofitError;

/**
 * Created by tushar on 16/02/16.
 */
public abstract class BaseActionActivity extends AppCompatActivity {


    public void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    public SwipeRefreshLayout mSwipeRefreshLayout;
    public boolean mSwRefreshing;

    protected void setupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.button_orange);
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    if (!mSwRefreshing) {
                        mSwRefreshing = true;
                        mSwipeRefreshLayout.setEnabled(false);
                        swipeRefresh();
                    }
                }
            });
        }
    }

    protected void swipeRefresh() {
    }

    public void buildAndShowSnackbarWithMessage(String msg) {
        final Snackbar snackbar = Snackbar.with(this.getApplicationContext())
                .type(SnackbarType.MULTI_LINE)
                //.color(getResources().getColor(android.R.color.black))
                .text(msg)
                .actionLabel("RETRY") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .swipeToDismiss(false)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        snackBarRetryActionListener();
                    }
                });
        snackbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSnackbar(snackbar); // activity where it is displayed
            }
        }, 500);

    }

    protected void snackBarRetryActionListener() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    protected void showSnackbar(Snackbar snackbar) {
        SnackbarManager.show(snackbar, this);
    }

    public void hideSnackbar() {
        SnackbarManager.dismiss();
    }

    public void handleRetrofitError(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage(getResources().getString(R.string.no_internet_conn));
        } else {
            buildAndShowSnackbarWithMessage(getResources().getString(R.string.unexpected_error));
        }
//        Log.e(getTagName(), "failure", error);
    }



/*    public void hideProgressHUDInLayout() {
        CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
    }*/
}
