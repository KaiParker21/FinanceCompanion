package com.skye.financecompanion.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.skye.financecompanion.MainActivity // Replace with your actual MainActivity

class PaymentNotificationService : NotificationListenerService() {

    // Target specific UPI/Wallet apps
    private val paymentApps = listOf(
        "com.google.android.apps.nbu.paisa.user", // GPay
        "net.one97.paytm",                        // Paytm
        "com.phonepe.app",                        // PhonePe
        "com.samsung.android.spay"                // Samsung Pay
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName

        // 1. Only process notifications from our target apps
        if (paymentApps.contains(packageName)) {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title") ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""

            // 2. Simple Heuristic Check: Did they pay money?
            // (You will need to refine these keywords based on actual app notifications)
            val isPayment = text.contains("Paid", ignoreCase = true) ||
                    text.contains("Sent", ignoreCase = true) ||
                    title.contains("Paid", ignoreCase = true)

            if (isPayment) {
                // 3. Extract the amount using Regex (e.g., looks for ₹ followed by numbers)
                val amountRegex = Regex("(?i)(?:rs\\.?|inr|₹)\\s*([0-9,]+(?:\\.[0-9]+)?)")
                val matchResult = amountRegex.find(text) ?: amountRegex.find(title)

                val amountString = matchResult?.groups?.get(1)?.value

                if (amountString != null) {
                    // Clean the string (remove commas) and convert to Double
                    val amount = amountString.replace(",", "").toDoubleOrNull()
                    if (amount != null) {
                        fireQuickAddNotification(amount, packageName)
                    }
                }
            }
        }
    }

    private fun fireQuickAddNotification(amount: Double, sourceApp: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "finance_companion_quick_add"

        // Create Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Quick Add Transactions",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // The intent that opens your app when they tap the notification
        // You can pass the amount as an Intent Extra so your app pre-fills the UI!
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
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
            .setContentTitle("Add ₹$amount to Finance Companion?")
            .setContentText("We detected a payment via $appName.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Not needed for our use case, but must be implemented if you want to track dismissed notifications
    }
}