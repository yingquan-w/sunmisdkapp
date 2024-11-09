package com.example.sunmisdk;

import android.app.Application;
import android.util.Log;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
// import com.sunmi.pay.hardware.wrapper.SunmiPayKernel;
import sunmi.paylib.SunmiPayKernel;

public class MyApplication extends Application {
    public static MyApplication app;
    public ReadCardOptV2 readCardOptV2;
    private SunmiPayKernel payKernel;
    private boolean isPaySDKConnected = false;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        bindPaySDKService();
    }

    /**
     * Bind and initialize the PaySDK service
     */
    private void bindPaySDKService() {
        payKernel = SunmiPayKernel.getInstance();
        payKernel.initPaySDK(this, new SunmiPayKernel.ConnectCallback() {
            @Override
            public void onConnectPaySDK() {
                Log.d("MyApplication", "PaySDK connected successfully");
                isPaySDKConnected = true;
                // Initialize readCardOptV2
                readCardOptV2 = payKernel.mReadCardOptV2;
            }

            @Override
            public void onDisconnectPaySDK() {
                Log.d("MyApplication", "PaySDK disconnected");
                isPaySDKConnected = false;
                readCardOptV2 = null;
            }
        });
    }

    public boolean isPaySDKConnected() {
        return isPaySDKConnected;
    }
}