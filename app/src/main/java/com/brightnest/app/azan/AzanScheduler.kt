package com.brightnest.app.azan

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.brightnest.app.Prefs
import java.util.Calendar
import java.util.Locale

object AzanScheduler {
    private const val TAG = "AzanScheduler"

    data class PrayerTime(val name: String, val timeString: String, val prayerId: Int)

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarms(context: Context) {
        val prefs = Prefs(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val prayers = listOf(
            PrayerTime("Fajr", prefs.prayerFajr, 1),
            PrayerTime("Dhuhr", prefs.prayerDhuhr, 2),
            PrayerTime("Asr", prefs.prayerAsr, 3),
            PrayerTime("Maghrib", prefs.prayerMaghrib, 4),
            PrayerTime("Isha", prefs.prayerIsha, 5)
        )

        for (prayer in prayers) {
            val calendar = parseTimeString(prayer.timeString) ?: continue
            
            // If the time has already passed today, schedule for tomorrow
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val intent = Intent(context, AzanReceiver::class.java).apply {
                putExtra("PRAYER_NAME", prayer.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayer.prayerId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        // Fallback if permission is not granted (though we requested it)
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Scheduled ${prayer.name} for ${calendar.time}")
            } catch (e: SecurityException) {
                Log.e(TAG, "Failed to schedule alarm", e)
            }
        }
    }

    private fun parseTimeString(timeString: String): Calendar? {
        val m = Regex("^(\\d{1,2}):(\\d{2})\\s*(AM|PM)?$", RegexOption.IGNORE_CASE).find(timeString.trim()) ?: return null
        var h = m.groupValues[1].toIntOrNull() ?: return null
        val min = m.groupValues[2].toIntOrNull() ?: return null
        val period = m.groupValues[3].uppercase(Locale.US)
        
        if (period == "PM" && h != 12) h += 12
        if (period == "AM" && h == 12) h = 0

        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}
