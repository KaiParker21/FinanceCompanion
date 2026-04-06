package com.skye.financecompanion.data.service

import android.content.Context
import androidx.core.app.NotificationManagerCompat

fun Context.hasMagicTrackingPermission(): Boolean {
    return NotificationManagerCompat
        .getEnabledListenerPackages(this)
        .contains(this.packageName)
}