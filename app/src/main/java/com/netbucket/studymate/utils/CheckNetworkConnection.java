package com.netbucket.studymate.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

public class CheckNetworkConnection extends AsyncTask<Void, Void, Boolean> {

    private OnConnectionCallback mOnConnectionCallback;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    public CheckNetworkConnection(Context context, OnConnectionCallback onConnectionCallback) {
        super();
        this.mOnConnectionCallback = onConnectionCallback;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (mContext == null)
            return false;
        return new NetworkInfoUtility(mContext).isConnectedToInternet();
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);

        if (b) {
            mOnConnectionCallback.onConnectionSuccess();
        } else {
            String message = "No Internet Connection";
            if (mContext == null)
                message = "Context is null";
            mOnConnectionCallback.onConnectionFail(message);
        }
    }

    public interface OnConnectionCallback {
        void onConnectionSuccess();

        void onConnectionFail(String message);
    }
}

