package com.twsyt.merchant.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.Login;
import com.twsyt.merchant.model.LoginResponse;
import com.twsyt.merchant.service.HttpService;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends BaseActionActivity {

    private String LOG_TAG = LoginActivity.this.getClass().getSimpleName();

    private String userId = "";
    private String password = "";
    private EditText et_userId;
    private EditText et_password;
    private LoginResponse mLoginResp;
    private SharedPreferences sharedPrefs;
    private LinearLayout circularProgressBar_ll;
    private CircularProgressBar circularProgressBar;
    private TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_userId = (EditText) findViewById(R.id.tv_userId);
        et_password = (EditText) findViewById(R.id.tv_password);
        loginButton = (TextView) findViewById(R.id.bLogin);
        sharedPrefs = this.getApplicationContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Start the service directly if we have logged in once before and skip the login step.
        if (sharedPrefs.getBoolean(AppConstants.LOGGED_IN_ATLEAST_ONCE, false)) {
            Utils.checkAndStartWebSocketService(LoginActivity.this);
            StartMainActivity();
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        } else {
            if (loginButton != null) {
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performLogin();
                    }
                });
            }
        }

        // This enables login from keyboard by pressing "enter"
        et_password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            performLogin();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void performLogin() {
        loginButton.setEnabled(false);

        if (circularProgressBar_ll == null)
            circularProgressBar_ll = (LinearLayout) findViewById(R.id.circularProgressBar_ll);

        if (circularProgressBar == null)
            circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);

        if (circularProgressBar != null) {
            circularProgressBar.setVisibility(View.VISIBLE);
        }
        circularProgressBar_ll.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(et_userId.getText())) {
            userId = et_userId.getText().toString();
        }
        if (!TextUtils.isEmpty(et_password.getText())) {
            password = et_password.getText().toString();
        }

        final Login loginInfo = new Login(password, userId);
        loginInfo.setMerchant(false);

        HttpService.getInstance().twystLogin(loginInfo, new Callback<BaseResponse<LoginResponse>>() {
            @Override
            public void success(BaseResponse<LoginResponse> loginResponseBaseResponse, Response response) {
                if (loginResponseBaseResponse.isResponse()) {
                    mLoginResp = loginResponseBaseResponse.getData().getLoginResponse();
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    Gson gson = new Gson();
                    String loginResponsJson = gson.toJson(mLoginResp);
                    editor.putString(AppConstants.LOGIN_RESPONSE_JSON, loginResponsJson);
                    editor.putBoolean(AppConstants.LOGGED_IN_ATLEAST_ONCE, true);
                    editor.commit();
                    Utils.checkAndStartWebSocketService(LoginActivity.this);
                    StartMainActivity();
//                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, loginResponseBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                hideProgressHUDInLayout();
                hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
//                Log.d(LOG_TAG, "Failed for some reason");
                hideProgressHUDInLayout();
                hideSnackbar();
                handleRetrofitError(error);
            }
        });
    }

    private void StartMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void hideProgressHUDInLayout() {
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
        circularProgressBar_ll.setVisibility(View.GONE);
        loginButton.setEnabled(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                Utils.hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void snackBarRetryActionListener() {
        performLogin();
    }
}