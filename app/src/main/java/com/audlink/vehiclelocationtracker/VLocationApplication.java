package com.audlink.vehiclelocationtracker;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

/**
 * Created by sridhar on 3/2/2017.
 */

public class VLocationApplication extends Application implements InterNetReceiver.ConnectivityReceiverListener  {
    private static Context appContext;
    private static VLocationApplication mInstance;
    boolean isConnected;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        appContext = getApplicationContext();
        setAppContext(mInstance);
        isConnected = InterNetReceiver.isConnected();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mInstance=this;
    }
    public static Context getAppContext() {
        return appContext;
    }
    public static synchronized VLocationApplication getInstance() {
        return mInstance;
    }
    private static void setAppContext(Context appContext) {
        VLocationApplication.appContext = appContext;
    }


    public void setConnectivityListener(InterNetReceiver.ConnectivityReceiverListener listener) {
        InterNetReceiver.connectivityReceiverListener = listener;
    }
    public void onNetworkConnectionChanged(boolean Connected) {
        isConnected=Connected;
    }


}
