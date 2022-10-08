package com.smali.lvfashiyingdi;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadpackageparam) throws Throwable {
        if (loadpackageparam.packageName.equals("com.gonlan.iplaymtg")) {
            XposedBridge.log("hooking!!!");
            XposedHelpers.findAndHookMethod("com.gonlan.iplaymtg.common.MainActivity",
                    loadpackageparam.classLoader,
                    "t0",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Class clazz = param.thisObject.getClass();
                            Field field = clazz.getDeclaredField("Y");
                            field.setAccessible(true);
                            ImageView image = (ImageView) field.get(param.thisObject);
                            if (image != null) {
                                XposedBridge.log("make the shop icon disappear!!!");
                                image.setVisibility(View.GONE);
                            }
                            field = clazz.getDeclaredField("C");
                            field.setAccessible(true);
                            TextView text = (TextView) field.get(param.thisObject);
                            if (text != null){
                                XposedBridge.log("make the shop text disappear!!!");
                                text.setVisibility(View.GONE);
                            }
                        }
                    }
            );
        }
    }
}

