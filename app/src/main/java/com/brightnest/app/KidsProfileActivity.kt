package com.brightnest.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brightnest.app.databinding.ActivityKidsProfileBinding

class KidsProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKidsProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKidsProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = Prefs(this)
        
        // If they somehow have already logged in, skip this
        if (prefs.loggedIn && prefs.userName.isNotEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.btnStart.setOnClickListener {
            val name = binding.inputName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a nickname.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.userName = name
            prefs.loggedIn = true
            prefs.onboarded = true
            prefs.mode = "kids" // Ensure kids mode

            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
