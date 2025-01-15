package com.android.securesms.service.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.addAll

/**
 * PermissionActivity class handles app permissions
 * @since v1.0.0
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun requestAllPermissions() : Array<String> {
        val permissions = mutableListOf<String>()

        if (!areReadPermissionGranted())
            permissions.add(AppPermission.READ_SMS.permission)

        if (!areReceivePermissionGranted())
            permissions.add(AppPermission.RECEIVE_SMS.permission)

        return permissions.toTypedArray()
    }

    fun checkAllPermissions(): Boolean {
        return areReadPermissionGranted() && areReceivePermissionGranted()
    }

    fun areReadPermissionGranted(): Boolean {
        return AppPermission.READ_SMS.isGranted()
    }

    fun areReceivePermissionGranted(): Boolean {
        return AppPermission.RECEIVE_SMS.isGranted()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun notificationPermission() : String {
        return AppPermission.NOTIFICATION.permission
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun areNotificationPermissionGranted() : Boolean {
        return AppPermission.NOTIFICATION.isGranted()
    }

    private fun AppPermission.isGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            this.permission
        ) == PERMISSION_GRANTED
    }

}

enum class AppPermission(val permission: String) {
    READ_SMS(Manifest.permission.READ_SMS),
    RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    NOTIFICATION(Manifest.permission.POST_NOTIFICATIONS)
}