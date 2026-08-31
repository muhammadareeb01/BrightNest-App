package com.brightnest.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.brightnest.app.data.BrightNestDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        try {
            applyNightMode(Prefs(this).darkMode)
        } catch (e: Throwable) {
            android.util.Log.e("BrightNestApp", "Night mode error", e)
        }

        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                com.brightnest.app.data.FirestoreRepository.seedLocalRoomDbFast(database)
                com.brightnest.app.data.FirestoreRepository.seedCloudFirestoreDirectly()
            } catch (t: Throwable) {
                android.util.Log.e("BrightNestApp", "Error background seeding", t)
            }
        }
    }
}
