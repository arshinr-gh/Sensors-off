package com.yourpackage.sensorsmod

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XC_MethodHook

class SensorHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.android.systemui") {
            try {
                val sensorManagerClass = XposedHelpers.findClass(
                    "android.hardware.SensorManager", 
                    lpparam.classLoader
                )

                XposedHelpers.findAndHookMethod(
                    sensorManagerClass, "setSensorEnabled",
                    Int::class.java, Boolean::class.java, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val sensorType = param.args[0] as Int
                            if (sensorType == SENSOR_MICROPHONE) {
                                param.args[1] = true // Always enable mic
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                XposedHelpers.log("SensorHook: Error - ${e.message}")
            }
        }
    }

    companion object {
        private const val SENSOR_MICROPHONE = 1 // Replace with correct sensor ID if needed
    }
}
