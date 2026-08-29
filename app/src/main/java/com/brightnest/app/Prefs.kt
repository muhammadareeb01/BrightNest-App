package com.brightnest.app

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("brightnest_prefs", Context.MODE_PRIVATE)

    var onboarded: Boolean
        get() = sp.getBoolean("onboarded", false)
        set(value) = sp.edit().putBoolean("onboarded", value).apply()

    var loggedIn: Boolean
        get() = sp.getBoolean("logged_in", false)
        set(value) = sp.edit().putBoolean("logged_in", value).apply()

    var userName: String
        get() = sp.getString("user_name", "") ?: ""
        set(value) = sp.edit().putString("user_name", value).apply()

    var userEmail: String
        get() = sp.getString("user_email", "") ?: ""
        set(value) = sp.edit().putString("user_email", value).apply()

    var language: String
        get() = sp.getString("language", "en") ?: "en"
        set(value) = sp.edit().putString("language", value).apply()

    // "kids" or "adult"
    var mode: String
        get() = sp.getString("mode", "kids") ?: "kids"
        set(value) = sp.edit().putString("mode", value).apply()

    var darkMode: Boolean
        get() = sp.getBoolean("dark_mode", false)
        set(value) = sp.edit().putBoolean("dark_mode", value).apply()

    var notifications: Boolean
        get() = sp.getBoolean("notifications", true)
        set(value) = sp.edit().putBoolean("notifications", value).apply()

    var coins: Int
        get() = sp.getInt("coins", 0)
        set(value) = sp.edit().putInt("coins", value).apply()

    var stars: Int
        get() = sp.getInt("stars", 0)
        set(value) = sp.edit().putInt("stars", value).apply()

    var streak: Int
        get() = sp.getInt("streak", 0)
        set(value) = sp.edit().putInt("streak", value).apply()

    // "free" | "premium" | "family"
    var premiumTier: String
        get() = sp.getString("premium_tier", "free") ?: "free"
        set(value) = sp.edit().putString("premium_tier", value).apply()

    val isPremium: Boolean get() = premiumTier != "free"

    var parentPin: String
        get() = sp.getString("parent_pin", "") ?: ""
        set(value) = sp.edit().putString("parent_pin", value).apply()

    var lockExit: Boolean
        get() = sp.getBoolean("lock_exit", false)
        set(value) = sp.edit().putBoolean("lock_exit", value).apply()

    var lockSettings: Boolean
        get() = sp.getBoolean("lock_settings", false)
        set(value) = sp.edit().putBoolean("lock_settings", value).apply()

    var prayerFajr: String
        get() = sp.getString("prayer_fajr", "05:30") ?: "05:30"
        set(value) = sp.edit().putString("prayer_fajr", value).apply()

    var prayerDhuhr: String
        get() = sp.getString("prayer_dhuhr", "13:15") ?: "13:15"
        set(value) = sp.edit().putString("prayer_dhuhr", value).apply()

    var prayerAsr: String
        get() = sp.getString("prayer_asr", "16:45") ?: "16:45"
        set(value) = sp.edit().putString("prayer_asr", value).apply()

    var prayerMaghrib: String
        get() = sp.getString("prayer_maghrib", "18:20") ?: "18:20"
        set(value) = sp.edit().putString("prayer_maghrib", value).apply()

    var prayerIsha: String
        get() = sp.getString("prayer_isha", "19:50") ?: "19:50"
        set(value) = sp.edit().putString("prayer_isha", value).apply()

    var badges: Set<String>
        get() = sp.getStringSet("badges", emptySet()) ?: emptySet()
        set(value) = sp.edit().putStringSet("badges", value).apply()

    var alarmsJson: String
        get() = sp.getString("alarms_json", "[]") ?: "[]"
        set(value) = sp.edit().putString("alarms_json", value).apply()

    var clockOffsetMs: Long
        get() = sp.getLong("clock_offset_ms", 0L)
        set(value) = sp.edit().putLong("clock_offset_ms", value).apply()

    fun addCoins(n: Int) { coins += n }

    fun addStars(n: Int) { stars += n }

    fun unlockBadge(id: String) {
        if (!badges.contains(id)) badges = badges + id
    }

    fun logout() {
        loggedIn = false
        userName = ""
        userEmail = ""
    }
}
