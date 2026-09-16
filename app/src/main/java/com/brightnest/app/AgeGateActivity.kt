package com.brightnest.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brightnest.app.databinding.ActivityAgeGateBinding
import java.util.Calendar

class AgeGateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgeGateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeGateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            val yearStr = binding.inputYear.text?.toString()?.trim().orEmpty()
            if (yearStr.length != 4 || yearStr.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid 4-digit birth year.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val birthYear = yearStr.toInt()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val age = currentYear - birthYear

            if (age < 0 || age > 120) {
                Toast.makeText(this, "Please enter a valid birth year.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = Prefs(this)
            prefs.ageVerified = true
            
            if (age >= 18) {
                prefs.isAdult = true
                startActivity(Intent(this, OnboardingActivity::class.java))
            } else {
                prefs.isAdult = false
                prefs.mode = "kids" // Set mode to kids immediately
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
