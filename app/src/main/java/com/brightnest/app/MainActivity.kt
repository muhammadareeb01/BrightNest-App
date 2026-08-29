package com.brightnest.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.lifecycle.lifecycleScope
import com.brightnest.app.azan.AzanScheduler
import com.brightnest.app.data.AlarmStore
import com.brightnest.app.data.FirestoreRepository
import com.brightnest.app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        AzanScheduler.scheduleAlarms(this)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var ringDialog: AlertDialog? = null
    private var firedMinuteKey = ""
    private val firedTags = mutableSetOf<String>()
    private var snoozeHour = -1
    private var snoozeMinute = -1
    private var snoozeLabel = ""

    private val alarmTick = object : Runnable {
        override fun run() {
            checkAlarms()
            handler.postDelayed(this, 1000)
        }
    }

    private val buzz = object : Runnable {
        override fun run() {
            vibrate()
            handler.postDelayed(this, 1800)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHost.navController)

        requestNotificationPermissionAndScheduleAlarms()

        tts = TextToSpeech(applicationContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) tts?.setSpeechRate(0.9f)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = (application as BrightNestApp).database
                com.brightnest.app.data.FirestoreRepository.syncRemoteContentToLocalDb(db)
                val prefs = Prefs(this@MainActivity)
                val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val profile = com.brightnest.app.data.UserProfile(
                        uid = user.uid,
                        email = user.email ?: prefs.userEmail,
                        name = prefs.userName.ifEmpty { "Parent" },
                        role = "parent",
                        activeMode = prefs.mode,
                        parentPin = prefs.parentPin
                    )
                    com.brightnest.app.data.FirestoreRepository.saveUserProfile(profile)
                }
            } catch (e: Exception) {
                android.util.Log.e("BrightNestSync", "Sync error", e)
            }
        }
    }

    private fun requestNotificationPermissionAndScheduleAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                AzanScheduler.scheduleAlarms(this)
            }
        } else {
            AzanScheduler.scheduleAlarms(this)
        }
    }

    override fun onResume() {
        super.onResume()
        val isAdult = Prefs(this).mode == "adult"
        binding.bottomNav.menu.findItem(R.id.rewardsFragment)
            .setTitle(if (isAdult) R.string.tab_spiritual else R.string.tab_rewards)
        com.brightnest.app.AppLanguageHelper.localizeBottomNav(binding.bottomNav.menu, Prefs(this).language, isAdult)
    }

    override fun onStart() {
        super.onStart()
        handler.post(alarmTick)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(alarmTick)
        ringDialog?.dismiss()
        stopRing()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
        tts = null
    }

    private fun checkAlarms() {
        val prefs = Prefs(this)
        val now = AlarmStore.now(prefs)
        val hh = now.get(Calendar.HOUR_OF_DAY)
        val mm = now.get(Calendar.MINUTE)
        val key = "${now.get(Calendar.YEAR)}-${now.get(Calendar.MONTH)}-" +
            "${now.get(Calendar.DAY_OF_MONTH)}-$hh-$mm"
        if (firedMinuteKey != key) {
            firedMinuteKey = key
            firedTags.clear()
        }
        if (ringDialog?.isShowing == true) return

        if (snoozeHour == hh && snoozeMinute == mm && !firedTags.contains("snooze")) {
            firedTags.add("snooze")
            val label = snoozeLabel
            snoozeHour = -1
            snoozeMinute = -1
            snoozeLabel = ""
            ring(hh, mm, label)
            return
        }

        val match = AlarmStore.load(prefs)
            .firstOrNull { it.enabled && it.hour == hh && it.minute == mm && !firedTags.contains(it.id) }
        if (match != null) {
            firedTags.add(match.id)
            ring(match.hour, match.minute, match.label)
        }
    }

    private fun ring(hour: Int, minute: Int, label: String) {
        if (ttsReady) {
            val text = if (label.isNotEmpty()) "Alarm. $label" else "Alarm"
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "bn_alarm")
        }
        handler.post(buzz)
        val msg = AlarmStore.to12h(hour, minute) + if (label.isNotEmpty()) "\n$label" else ""
        ringDialog = AlertDialog.Builder(this)
            .setTitle("⏰ Alarm")
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("Dismiss") { d, _ ->
                stopRing()
                d.dismiss()
            }
            .setNegativeButton("Snooze 5 min") { d, _ ->
                val t = AlarmStore.now(Prefs(this))
                t.add(Calendar.MINUTE, 5)
                snoozeHour = t.get(Calendar.HOUR_OF_DAY)
                snoozeMinute = t.get(Calendar.MINUTE)
                snoozeLabel = if (label.isNotEmpty()) "$label (snooze)" else "Snooze"
                stopRing()
                d.dismiss()
            }
            .show()
    }

    private fun stopRing() {
        handler.removeCallbacks(buzz)
        ringDialog = null
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(400)
        }
    }

    override fun onPause() {
        super.onPause()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (uid.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                FirestoreRepository.backupAllLocalProgressToCloud(
                    uid = uid,
                    prefs = Prefs(this@MainActivity),
                    store = AdultStore(this@MainActivity)
                )
            }
        }
    }
}
