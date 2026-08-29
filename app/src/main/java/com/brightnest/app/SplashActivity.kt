package com.brightnest.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.brightnest.app.data.SeedData
import com.brightnest.app.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in))
        binding.appName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        binding.tagline.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        lifecycleScope.launch {
            seedIfNeeded()
            delay(1700)
            val prefs = Prefs(this@SplashActivity)
            val next = when {
                !prefs.onboarded -> OnboardingActivity::class.java
                !prefs.loggedIn -> AuthActivity::class.java
                else -> MainActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, next))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private suspend fun seedIfNeeded() {
        val db = (application as BrightNestApp).database
        com.brightnest.app.data.FirestoreRepository.syncRemoteContentToLocalDb(db)
    }
}
