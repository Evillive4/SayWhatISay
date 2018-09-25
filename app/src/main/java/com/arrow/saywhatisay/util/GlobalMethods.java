package com.arrow.saywhatisay.util;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.arrow.saywhatisay.application.MyApplication;
import com.arrow.saywhatisay.receiver.AdminManageReceiver;

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
            Log.e(TAG, "Unlock");
            screenOff(context);
//            wl.setReferenceCounted(false);
//            wl.release();
        }
    }

    public static void screenOff(Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) MyApplication.getInstance().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, AdminManageReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
            getDeviceManager(adminReceiver, context);
        }
    }

    //查找是否已经激活设备管理器
    public static void isAdminActive(Context context) {
        //TODO 第一次打开时处理
        DevicePolicyManager policyManager = (DevicePolicyManager) MyApplication.getInstance().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, AdminManageReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (!admin) {
            getDeviceManager(adminReceiver, context);
        }
    }

    private static void getDeviceManager(ComponentName adminReceiver, Context context) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "activity device");
        context.startActivity(intent);
    }

    public static void lock(Context context) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");


        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //锁屏
        kl.reenableKeyguard();
        //释放wakeLock，关灯
        wl.release();
    }

}
