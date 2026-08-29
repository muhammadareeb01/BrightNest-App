package com.brightnest.app.data

import com.brightnest.app.Prefs
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

data class AlarmItem(
    val id: String,
    val hour: Int,
    val minute: Int,
    val label: String,
    val enabled: Boolean
)

object AlarmStore {

    fun load(prefs: Prefs): MutableList<AlarmItem> {
        val out = mutableListOf<AlarmItem>()
        try {
            val arr = JSONArray(prefs.alarmsJson)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    AlarmItem(
                        id = o.getString("id"),
                        hour = o.getInt("hour"),
                        minute = o.getInt("minute"),
                        label = o.optString("label", ""),
                        enabled = o.optBoolean("enabled", true)
                    )
                )
            }
        } catch (_: Exception) {
        }
        out.sortWith(compareBy({ it.hour }, { it.minute }))
        return out
    }

    fun save(prefs: Prefs, alarms: List<AlarmItem>) {
        val sorted = alarms.sortedWith(compareBy({ it.hour }, { it.minute }))
        val arr = JSONArray()
        for (a in sorted) {
            arr.put(JSONObject().apply {
                put("id", a.id)
                put("hour", a.hour)
                put("minute", a.minute)
                put("label", a.label)
                put("enabled", a.enabled)
            })
        }
        prefs.alarmsJson = arr.toString()
    }

    fun now(prefs: Prefs): Calendar =
        Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() + prefs.clockOffsetMs }

    fun to12h(hour: Int, minute: Int): String {
        val ampm = if (hour >= 12) "PM" else "AM"
        val h = if (hour % 12 == 0) 12 else hour % 12
        return "$h:${minute.toString().padStart(2, '0')} $ampm"
    }

    fun setManualTime(prefs: Prefs, hour: Int, minute: Int) {
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        prefs.clockOffsetMs = target.timeInMillis - System.currentTimeMillis()
    }

    fun clearManual(prefs: Prefs) {
        prefs.clockOffsetMs = 0L
    }
}
