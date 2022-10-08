package com.smali.dongqiudi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.dongqiudi.news")) {
            XposedBridge.log("hooking!!");
            ClassLoader classLoader = loadPackageParam.classLoader;
            final String main_activity_Name = "com.dongqiudi.news.MainActivity";
            XposedHelpers.findAndHookMethod("android.app.Activity", classLoader, "onStart", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Activity now_activity = (Activity) param.thisObject;
                            Intent launch_intent, ads_intent;
                            PackageManager packageManager = now_activity.getPackageManager();
                            launch_intent = packageManager.getLaunchIntentForPackage(now_activity.getPackageName());
                            ads_intent = now_activity.getIntent();
                            if (launch_intent != null && ads_intent != null)
                                if (launch_intent.getComponent().flattenToString().equals(ads_intent.getComponent().flattenToString())) {
                                    Intent main_intent = new Intent();
                                    main_intent.setClassName(now_activity, main_activity_Name);
                                    now_activity.finish();
                                    now_activity.startActivity(main_intent);
                                    XposedBridge.log(("succeed!!"));
                                }
                            else
                                {
                                    XposedBridge.log("No intent!");
                                }
                        }
                    }
            );
        }
    }
}