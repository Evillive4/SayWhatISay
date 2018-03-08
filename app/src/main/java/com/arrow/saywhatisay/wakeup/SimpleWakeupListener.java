package com.arrow.saywhatisay.wakeup;

import android.content.Intent;
import android.util.Log;

import com.arrow.saywhatisay.MainActivity;
import com.arrow.saywhatisay.application.MyApplication;
import com.arrow.saywhatisay.util.Logger;

public class SimpleWakeupListener implements IWakeupListener {

    private static final String TAG = "SimpleWakeupListener";

    @Override
    public void onSuccess(String word, WakeUpResult result) {
        Log.i(TAG,"onSuccess 唤醒成功，唤醒词：" + "word");
        Intent intent = new Intent();
        intent.setAction(MainActivity.class.getSimpleName());
        intent.putExtra("type", "101");
        intent.putExtra("word", word);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    @Override
    public void onStop() {
        Log.i(TAG,"onStop 唤醒词识别结束");
        Intent intent = new Intent();
        intent.setAction(MainActivity.class.getSimpleName());
        intent.putExtra("type", "102");
        intent.putExtra("word", "结束");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    @Override
    public void onError(int errorCode, String errorMessge, WakeUpResult result) {
        Log.i(TAG,"onError 唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.getOrigalJson());
        Intent intent = new Intent();
        intent.setAction(MainActivity.class.getSimpleName());
        intent.putExtra("type", "103");
        intent.putExtra("word", "Error");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    @Override
    public void onASrAudio(byte[] data, int offset, int length) {
        Logger.error(TAG, "audio data： " + data.length);
    }

}
