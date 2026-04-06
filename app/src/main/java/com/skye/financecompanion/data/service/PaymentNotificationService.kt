package com.skye.financecompanion.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.skye.financecompanion.MainActivity

class PaymentNotificationService : NotificationListenerService() {

    private val paymentApps = listOf(
        "com.google.android.apps.nbu.paisa.user",
        "net.one97.paytm",
        "com.phonepe.app",
        "com.samsung.android.spay"
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName

        if (paymentApps.contains(packageName)) {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title") ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""

            val isPayment = text.contains("Paid", ignoreCase = true) ||
                    text.contains("Sent", ignoreCase = true) ||
                    title.contains("Paid", ignoreCase = true)

            if (isPayment) {
                val amountRegex = Regex("(?i)(?:rs\\.?|inr|₹)\\s*([0-9,]+(?:\\.[0-9]+)?)")
                val matchResult = amountRegex.find(text) ?: amountRegex.find(title)

                val amountString = matchResult?.groups?.get(1)?.value

                if (amountString != null) {
                    val amount = amountString.replace(",", "").toDoubleOrNull()
                    if (amount != null) {
                        fireQuickAddNotification(amount, packageName)
                    }
                }
            }
        }
    }

    private fun fireQuickAddNotification(amount: Double, sourceApp: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "finance_companion_quick_add"

        val channel = NotificationChannel(
            channelId,
            "Quick Add Transactions",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("QUICK_ADD_AMOUNT", amount)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val appName = when(sourceApp) {
            "com.google.android.apps.nbu.paisa.user" -> "GPay"
            "net.one97.paytm" -> "Paytm"
            "com.phonepe.app" -> "PhonePe"
            else -> "Mobile Wallet"
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Add ₹$amount to Finance Companion?")
            .setContentText("We detected a payment via $appName.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // For future updates if needed
    }
}