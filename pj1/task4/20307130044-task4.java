package com.smali.bilibili;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    public static boolean isForeground(Activity context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadpackageparam) throws Throwable {
        if (loadpackageparam.packageName.equals("tv.danmaku.bili")) {
            XposedBridge.log("hooking!!!");
            final Date[] starttime = {new Date()};
            XposedHelpers.findAndHookMethod("tv.danmaku.bili.MainActivityV2",
                    loadpackageparam.classLoader,
                    "onStart",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Activity now_activity = (Activity) param.thisObject;
                            XposedBridge.log("hook succeed!!!");
                            if (isForeground(now_activity)) {
                                Date endtime = new Date();
                                if (endtime.getTime() - starttime[0].getTime() > 30000) {
                                    XposedBridge.log("You haved watched at least 30s. GO TO WORK!!!!!!!!!");
                                    Toast.makeText(now_activity,"You haved watched at least 30s. GO TO WORK!!!!!!!!!",Toast.LENGTH_LONG).show();
                                }
                                else if (endtime.getTime() - starttime[0].getTime() > 20000) {
                                    XposedBridge.log("You haved watched at least 20s, please go to work!!!");
                                    Toast.makeText(now_activity,"You haved watched at least 20s, please go to work!!!",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    XposedBridge.log("Not 1000ms!!!");
                                }
                            }
                            else
                            {
                                starttime[0] = new Date();
                            }
                        }
                    }
            );
        }
    }
}

