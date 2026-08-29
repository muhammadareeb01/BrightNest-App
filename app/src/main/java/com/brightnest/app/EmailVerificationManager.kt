package com.brightnest.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.random.Random

object EmailVerificationManager {

    private const val CHANNEL_ID = "brightnest_email_channel"
    private const val CHANNEL_NAME = "Email Verification"

    var currentCode: String = ""
        private set

    var pendingEmail: String = ""
        private set

    /**
     * Generates a fresh 4-digit code every time (1000..9999)
     */
    fun generateNewCode(): String {
        val code = Random.nextInt(1000, 10000).toString()
        currentCode = code
        return code
    }

    /**
     * Sends the 4-digit code via simulated email notification so user can see it
     */
    fun sendVerificationEmail(context: Context, email: String): String {
        pendingEmail = email
        val code = generateNewCode()

        showEmailNotification(context, email, code)
        return code
    }

    /**
     * Verify if user entered code matches currentCode
     */
    fun verifyCode(enteredCode: String): Boolean {
        return enteredCode.trim() == currentCode
    }

    private fun showEmailNotification(context: Context, email: String, code: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Sends 4-digit email verification codes"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("📧 BrightNest Email Verification")
            .setContentText("Your 4-digit verification code for $email is: $code")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Hello Parent,\n\nYour BrightNest 4-digit email verification code is:\n\n👉  $code  👈\n\nPlease enter this code in the app to verify your email.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(9991, notification)
    }
}
