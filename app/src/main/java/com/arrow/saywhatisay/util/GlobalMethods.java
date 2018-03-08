package com.arrow.saywhatisay.util;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.arrow.saywhatisay.application.MyApplication;

public class GlobalMethods {

    public static final String TAG = "GlobalMethods";

    /**
     * 唤醒并解锁屏幕
     *
     * @param context
     */
    public static void Unlock(Context context, boolean isLock) {
        //屏锁管理器
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean screen = pm.isScreenOn();
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        if (!screen && isLock) {//如果灭屏
            //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            //点亮屏幕
            wl.acquire();
            //释放
            wl.release();
        } else if (screen && !isLock) {
            //关屏
            Log.i(TAG,"Unlock");
            screenOff();
//            wl.setReferenceCounted(false);
//            wl.release();
        }
    }

    public static void screenOff() {
        DevicePolicyManager policyManager = (DevicePolicyManager) MyApplication.getInstance().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(MyApplication.getInstance(), MyApplication.getInstance().getPackageName());
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {

        }
    }

}
