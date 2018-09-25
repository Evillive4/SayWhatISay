package com.arrow.saywhatisay.wakeup;

import android.content.Intent;
import android.os.Handler;

import com.arrow.saywhatisay.activity.MainActivity;
import com.arrow.saywhatisay.application.MyApplication;
import com.arrow.saywhatisay.util.IStatus;
import com.arrow.saywhatisay.util.Logger;

public class RecogWakeupListener extends SimpleWakeupListener implements IStatus {

    private static final String TAG = "RecogWakeupListener";

    private Handler handler;

    public RecogWakeupListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onSuccess(String word, WakeUpResult result) {
        super.onSuccess(word, result);
        Logger.info(TAG, "onSuccess");
        handler.sendMessage(handler.obtainMessage(STATUS_WAKEUP_SUCCESS));
        Intent intent = new Intent();
        intent.setAction(MainActivity.class.getSimpleName());
        intent.putExtra("type", "101");
        intent.putExtra("word", word);
        MyApplication.getInstance().sendBroadcast(intent);

    }
}
