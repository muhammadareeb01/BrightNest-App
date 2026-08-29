package com.brightnest.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.brightnest.app.data.BrightNestDatabase

class BrightNestApp : Application() {

    val database: BrightNestDatabase by lazy { BrightNestDatabase.get(this) }

    companion object {
        lateinit var instance: BrightNestApp
            private set

        fun applyNightMode(dark: Boolean) {
            AppCompatDelegate.setDefaultNightMode(
                if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        applyNightMode(Prefs(this).darkMode)
        try {
            com.brightnest.app.data.FirestoreRepository.seedCloudFirestoreDirectly()
        } catch (e: Exception) {
            android.util.Log.e("BrightNestApp", "Error seeding Firestore", e)
        }
    }
}
