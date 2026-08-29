package com.brightnest.app.azan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AzanReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "AzanReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "onReceive: action=$action")

        if (action == Intent.ACTION_BOOT_COMPLETED || 
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED) {
            // Reschedule alarms on device boot or time change
            AzanScheduler.scheduleAlarms(context)
        } else {
            // It's a regular alarm trigger for a prayer
            val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "Prayer"
            
            val serviceIntent = Intent(context, AzanService::class.java).apply {
                putExtra("PRAYER_NAME", prayerName)
            }
            
            // Start the foreground service to play the Azan
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            
            // Schedule the alarm for tomorrow
            AzanScheduler.scheduleAlarms(context)
        }
    }
}
