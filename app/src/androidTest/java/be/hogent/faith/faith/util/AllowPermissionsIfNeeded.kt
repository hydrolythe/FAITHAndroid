package be.hogent.faith.faith.util

import android.os.Build
import android.util.Log
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector

fun allowPermissionsIfNeeded(device: UiDevice) {
    if (Build.VERSION.SDK_INT >= 23) {
        var allowPermissions = device.findObject(UiSelector().text("ALLOW"))
        if (!allowPermissions.exists()) {
            allowPermissions = device.findObject(UiSelector().text("TOESTAAN"))
        }
        if (allowPermissions.exists()) {
            try {
                allowPermissions.click()
            } catch (e: UiObjectNotFoundException) {
                Log.e("Permissions:", "There is no permissions dialog to interact with ")
            }
        }
    }
}
