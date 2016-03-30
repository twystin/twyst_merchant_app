package com.twsyt.merchant.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;

import org.json.JSONObject;

import java.net.URI;

public class WebSocketService extends IntentService implements FayeListener {

    public final String TAG = this.getClass().getSimpleName();

    FayeClient mClient;

    public WebSocketService() {
        super("WebSocketService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Starting Web Socket");

//        try {

//            String baseUrl = Preferences.getString(Preferences.KEY_FAYE_HOST, DebugActivity.PROD_FAYE_HOST);
//        String baseUrl = "http://twyst.in/faye/";
        String baseUrl = "http://staging.twyst.in/faye/";

//        URI uri = URI.create(String.format("wss://%s:443/events", baseUrl));
        URI uri = URI.create(String.format("%s", baseUrl));
//        String channel = String.format("/%s", "56879bf4af76ee153f804dd3");
        String channel = String.format("/%s", "dktwystin");
        Log.d(TAG,"URI : " + uri + "Channel : " + channel);
//            String channel = String.format("/%s/**", User.getCurrentUser().getUserId());

        JSONObject ext = new JSONObject();
//            ext.put("authToken", User.getCurrentUser().getAuthorizationToken());

//            mClient = new FayeClient(uri, channel);
        mClient = new FayeClient(new Handler(Looper.getMainLooper()), uri, channel);
        mClient.setFayeListener(this);
        mClient.connectToServer(ext);


//        } catch (JSONException ex) {}
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void connectedToServer() {
        Log.i(TAG, "Connected to Server");
        mClient.subscribe();
    }

    @Override
    public void disconnectedFromServer() {
        Log.i(TAG, "Disonnected from Server");
    }

    @Override
    public void subscribedToChannel(String subscription) {
        Log.i(TAG, String.format("Subscribed to channel %s on Faye", subscription));
    }

    @Override
    public void subscriptionFailedWithError(String error) {
        Log.i(TAG, String.format("Subscription failed with error: %s", error));
    }

    @Override
    public void messageReceived(JSONObject json) {
        Log.i(TAG, String.format("Received message %s", json.toString()));
    }
}
