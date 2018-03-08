package com.arrow.saywhatisay.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private List<Activity> mActivityList = new LinkedList<Activity>();
    private SharedPreferences sharedPreferences;

    public synchronized static MyApplication getInstance() {
        if (null == mInstance) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    public void onCreate() {
        super.onCreate();
        mInstance = this;
//        sharedPreferences = getSharedPreferences("cuthair", 0);

    }

}
