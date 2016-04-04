package com.twsyt.merchant.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.twsyt.merchant.R;
import com.twsyt.merchant.Util.AppConstants;
import com.twsyt.merchant.Util.Utils;
import com.twsyt.merchant.model.BaseResponse;
import com.twsyt.merchant.model.Login;
import com.twsyt.merchant.model.LoginResponse;
import com.twsyt.merchant.service.HttpService;
import com.twsyt.merchant.service.WebSocketService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    private String LOG_TAG = LoginActivity.this.getClass().getSimpleName();

    String userId = "";
    String password = "";
    EditText et_userId;
    EditText et_password;
    private LoginResponse mLoginResp;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_userId = (EditText) findViewById(R.id.tv_userId);
        et_password = (EditText) findViewById(R.id.tv_password);
        TextView loginButton = (TextView) findViewById(R.id.bLogin);
        sharedPrefs = this.getApplicationContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        if (sharedPrefs.getBoolean(AppConstants.LOGGED_IN_ATLEAST_ONCE, false)) {
            Utils.startMyService(WebSocketService.class, LoginActivity.this);
            StartMainActivity();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
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
                    /*int role = mLoginResp.getLoginResponse().getRole();
                    String channelName = "";
                    switch (role) {
                        case AppConstants.ROLE_ADMIN:
                            String email = "";
                            if (mLoginResp.getLoginResponse().getProfile() != null) {
                                email = mLoginResp.getLoginResponse().getProfile().getEmail();
                            }
                            channelName = email.replace("@", "").replace(".", "");
                            break;

                        case AppConstants.ROLE_MERCHANT:
                            channelName = mLoginResp.getLoginResponse().get_id();
                            break;

                        default:
                            channelName = "";
                    }*/

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    Gson gson = new Gson();
                    String loginResponsJson = gson.toJson(mLoginResp);
                    editor.putString(AppConstants.LOGIN_RESPONSE_JSON, loginResponsJson);
                    editor.putBoolean(AppConstants.LOGGED_IN_ATLEAST_ONCE, true);
                    editor.commit();
                    Utils.startMyService(WebSocketService.class, LoginActivity.this);
                    StartMainActivity();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, "Failed for some reason");
            }
        });
    }

    private void StartMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
