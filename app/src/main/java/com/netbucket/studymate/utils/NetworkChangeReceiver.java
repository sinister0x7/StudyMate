package com.netbucket.studymate.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (checkInternet(context)) {
            Toast.makeText(context, "Network Available Do operations", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkInternet(Context context) {
        return new NetworkInfoUtility(context).isConnectedToInternet();
    }
}