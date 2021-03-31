package com.example.maptest;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class CommonUtil {

    // Vibrate for milliseconds
    public static void vibrateDevice(Context context, long milliseconds) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                ((Vibrator) context.getSystemService(context.VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                ((Vibrator) context.getSystemService(context.VIBRATOR_SERVICE)).vibrate(milliseconds);
            }
        } catch (Exception e) { }
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }

}
