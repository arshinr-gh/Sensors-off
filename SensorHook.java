package com.sensorsoff;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;

public class SensorHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.android.systemui")) {
            try {
                Class<?> sensorManagerClass = XposedHelpers.findClass(
                        "android.hardware.SensorManager",
                        lpparam.classLoader
                );

                XposedHelpers.findAndHookMethod(
                        sensorManagerClass,
                        "registerListener",
                        int.class,
                        boolean.class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                int sensorType = (int) param.args[0];
                                if (sensorType == 9 || sensorType == 10) { // Gyroscope/Orientation
                                    param.args[1] = true; // Keep mic enabled
                                }
                            }
                        }
                );
            } catch (Exception e) {
                XposedHelpers.log("SensorHook Error: " + e.getMessage());
            }
        }
    }
}
