package com.netbucket.studymate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

public class NetworkInfoUtility {

    private final Context mContext;
    private boolean mWifiEnable = false;
    private boolean mMobileNetworkAvailable = false;

    public NetworkInfoUtility(Context context) {
        this.mContext = context;
    }

    public boolean getWifiEnable() {
        return mWifiEnable;
    }

    public void setIsWifiEnable(boolean isWifiEnable) {
        this.mWifiEnable = isWifiEnable;
    }

    public boolean getMobileNetworkAvailable() {
        return mMobileNetworkAvailable;
    }

    public void setIsMobileNetworkAvailable(boolean isMobileNetworkAvailable) {
        this.mMobileNetworkAvailable = isMobileNetworkAvailable;
    }

    public boolean isConnectedToInternet() {
        boolean isNetworkAvailable = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        setIsWifiEnable(Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).isConnected());
        setIsMobileNetworkAvailable(Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).isConnected());

        if (getWifiEnable() || getMobileNetworkAvailable()) {
            /*Sometime wifi is connected but service provider never connected to internet
            so cross check one more time*/
            if (isOnline()) {
                isNetworkAvailable = true;
            }
        }
        return isNetworkAvailable;
    }

    public boolean isOnline() {
        /*Just to check Time delay*/
        long t = Calendar.getInstance().getTimeInMillis();

        Runtime runtime = Runtime.getRuntime();
        try {
            /*Pinging to Google server*/
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            long t2 = Calendar.getInstance().getTimeInMillis();
            Log.i("Network check time", (t2 - t) + "ms");
        }
        return false;
    }
}
