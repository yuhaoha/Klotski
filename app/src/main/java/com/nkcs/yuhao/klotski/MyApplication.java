package com.nkcs.yuhao.klotski;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取context
        mContext = getApplicationContext();
    }

    //获取context对象
    public static Context getContext() {
        return mContext;
    }

}