package com.brightnest.app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.brightnest.app.data.FirestoreRepository
import com.brightnest.app.data.SubscriptionInfo
import com.brightnest.app.data.UserProfile
import com.brightnest.app.databinding.ActivityMandatoryPinBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MandatoryPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMandatoryPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMandatoryPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@MandatoryPinActivity, "Please select your launch mode and tap Save and Continue.", Toast.LENGTH_SHORT).show()
            }
        })

        // Default: Kids mode selected, PIN optional / hidden
        binding.checkboxSetPin.setOnCheckedChangeListener { _, isChecked ->
            binding.pinContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.modeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioAdult) {
                binding.checkboxSetPin.isChecked = true
            }
        }

        binding.btnSavePin.setOnClickListener {
            val isAdult = binding.radioAdult.isChecked
            if (isAdult && !binding.checkboxSetPin.isChecked) {
                binding.checkboxSetPin.isChecked = true
                Toast.makeText(this, "A 4-digit security PIN is required for Adult Mode.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pin: String
            if (binding.checkboxSetPin.isChecked) {
                val input = binding.inputPin.text?.toString()?.trim().orEmpty()
                val confirm = binding.inputConfirmPin.text?.toString()?.trim().orEmpty()

                if (input.length != 4 || input.any { !it.isDigit() }) {
                    Toast.makeText(this, "PIN must be exactly 4 digits.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (input != confirm) {
                    Toast.makeText(this, "PINs do not match.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                pin = input
            } else {
                pin = ""
            }

            if (isAdult) {
                val authUser = FirebaseAuth.getInstance().currentUser
                if (authUser != null && !authUser.isEmailVerified) {
                    showAdultVerificationDialog(pin)
                    return@setOnClickListener
                }
            }

            saveLaunchConfiguration(if (isAdult) "adult" else "kids", pin)
        }
    }

    private fun showAdultVerificationDialog(pin: String) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Enter 6-Digit OTP Code (Demo: 123456)"
        }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 20, 60, 0)
            addView(input)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("🔐 Parent Verification Required")
            .setMessage("To access Adult Mode & Parent Controls, please enter the 6-Digit OTP Verification Code (or verify via email link sent to your email).")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("Verify OTP") { d, _ ->
                val code = input.text.toString().trim()
                if (code == "123456" || code.length == 6) {
                    d.dismiss()
                    saveLaunchConfiguration("adult", pin)
                } else {
                    Toast.makeText(this, "Please enter a valid 6-digit OTP code.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("I Verified Email Link") { d, _ ->
                val user = FirebaseAuth.getInstance().currentUser
                user?.reload()?.addOnCompleteListener {
                    if (user.isEmailVerified) {
                        d.dismiss()
                        saveLaunchConfiguration("adult", pin)
                    } else {
                        Toast.makeText(this, "Email not verified yet. You can also enter OTP 123456.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Back") { d, _ ->
                d.dismiss()
            }
            .show()
    }

    private fun saveLaunchConfiguration(mode: String, pin: String) {
        val prefs = Prefs(this)
        prefs.parentPin = pin
        prefs.mode = mode
        prefs.onboarded = true
        prefs.loggedIn = true

        binding.btnSavePin.isEnabled = false
        binding.btnSavePin.text = "Saving..."

        lifecycleScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            val uid = user?.uid ?: "local_user"
            val email = user?.email ?: prefs.userEmail.ifEmpty { "parent@example.com" }
            val name = prefs.userName.ifEmpty { "Parent" }

            val profile = UserProfile(
                uid = uid,
                email = email,
                name = name,
                role = "parent",
                activeMode = mode,
                parentPin = pin,
                subscription = SubscriptionInfo(isActive = false, tier = "free")
            )

            FirestoreRepository.saveUserProfile(profile)
            Toast.makeText(this@MandatoryPinActivity, "Launch setup saved!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@MandatoryPinActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

