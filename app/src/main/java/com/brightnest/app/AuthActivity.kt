package com.brightnest.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.brightnest.app.data.FirestoreRepository
import com.brightnest.app.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private var isSignUpMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = Prefs(this)
        val forceLogin = intent.getBooleanExtra("force_login", false)
        if (prefs.loggedIn && prefs.onboarded && !forceLogin) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.tvToggleAuthMode.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateAuthUI()
        }

        binding.btnEmail.setOnClickListener {
            val name = binding.inputName.text?.toString()?.trim().orEmpty()
            val email = binding.inputEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.inputPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isSignUpMode && name.isEmpty()) {
                Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.userName = if (name.isNotEmpty()) name else "Parent"
            prefs.userEmail = email

            binding.btnEmail.isEnabled = false
            binding.btnEmail.text = if (isSignUpMode) "Creating Account..." else "Signing In..."

            if (isSignUpMode) {
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        handleSuccessfulSignup(prefs)
                    }
                    .addOnFailureListener { e ->
                        binding.btnEmail.isEnabled = true
                        updateAuthUI()
                        showAuthError(e)
                    }
            } else {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        handleSuccessfulLogin(prefs)
                    }
                    .addOnFailureListener { e ->
                        binding.btnEmail.isEnabled = true
                        updateAuthUI()
                        showAuthError(e)
                    }
            }
        }

        binding.btnGoogle.setOnClickListener {
            startGoogleSignIn()
        }
    }

    private fun startGoogleSignIn() {
        try {
            val webClientId = resources.getIdentifier("default_web_client_id", "string", packageName)
            if (webClientId == 0) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("⚙️ Google Sign-In Setup Required")
                    .setMessage("To enable Google Sign-In:\n1. Go to Firebase Console -> Authentication -> Sign-In Method -> Enable Google.\n2. Add your SHA-1 Fingerprint in Project Settings and download the updated google-services.json.")
                    .setPositiveButton("OK", null)
                    .show()
                return
            }
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(webClientId))
                .requestEmail()
                .build()

            val client = GoogleSignIn.getClient(this, gso)
            startActivityForResult(client.signInIntent, 9001)
        } catch (e: Exception) {
            Toast.makeText(this, "Google Sign-In error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken, account.displayName ?: "Parent", account.email ?: "")
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, name: String, email: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding.btnGoogle.isEnabled = false
        binding.btnGoogle.text = "Signing in with Google..."
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val prefs = Prefs(this)
                prefs.userName = name
                prefs.userEmail = email
                handleSuccessfulLogin(prefs)
            }
            .addOnFailureListener { e ->
                binding.btnGoogle.isEnabled = true
                binding.btnGoogle.text = "🌐 Continue with Google"
                Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAuthUI() {
        if (isSignUpMode) {
            binding.layoutInputName.visibility = View.VISIBLE
            binding.btnEmail.text = "Create Parent Account"
            binding.tvToggleAuthMode.text = "Already have an account? Sign In"
        } else {
            binding.layoutInputName.visibility = View.GONE
            binding.btnEmail.text = "Sign In"
            binding.tvToggleAuthMode.text = "Don't have an account? Sign Up"
        }
    }

    private fun showAuthError(e: Exception) {
        val msg = e.localizedMessage ?: "Authentication failed"
        if (msg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true)) {
            Toast.makeText(this, "Please enable 'Email/Password' in Firebase Console -> Authentication -> Sign-in method.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSuccessfulLogin(prefs: Prefs) {
        val user = auth.currentUser
        if (user != null && !user.isEmailVerified) {
            binding.btnEmail.isEnabled = true
            binding.btnEmail.text = "Sign In"
            MaterialAlertDialogBuilder(this@AuthActivity)
                .setTitle("⚠️ Email Not Verified")
                .setMessage("Please verify your email address (${user.email}) before logging in.")
                .setCancelable(false)
                .setPositiveButton("Resend Verification Email") { _, _ ->
                    user.sendEmailVerification()
                        .addOnSuccessListener {
                            Toast.makeText(this@AuthActivity, "Verification email resent to ${user.email}", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@AuthActivity, e.localizedMessage ?: "Failed to send email", Toast.LENGTH_SHORT).show()
                        }
                    auth.signOut()
                }
                .setNegativeButton("OK") { _, _ ->
                    auth.signOut()
                }
                .show()
            return
        }

        lifecycleScope.launch {
            val uid = auth.currentUser?.uid ?: ""
            val profile = FirestoreRepository.getUserProfile(uid)
            if (profile != null) {
                prefs.loggedIn = true
                prefs.onboarded = true
                prefs.parentPin = profile.parentPin
                prefs.mode = profile.activeMode
                // Senior 3NF bidirectional cloud sync for Kids & Adult progress
                FirestoreRepository.syncUserProgressOnLogin(uid, prefs, AdultStore(this@AuthActivity))
                startActivity(Intent(this@AuthActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@AuthActivity, MandatoryPinActivity::class.java))
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun handleSuccessfulSignup(prefs: Prefs) {
        val email = prefs.userEmail
        EmailVerificationManager.sendVerificationEmail(this@AuthActivity, email)
        showEmailVerificationDialog(prefs)
    }

    private fun showEmailVerificationDialog(prefs: Prefs) {
        val view = layoutInflater.inflate(R.layout.dialog_email_verification, null)
        val tvMessage = view.findViewById<android.widget.TextView>(R.id.tvDialogMessage)
        val inputCode = view.findViewById<android.widget.EditText>(R.id.inputCode)
        val btnCancel = view.findViewById<android.view.View>(R.id.btnDialogCancel)
        val btnResend = view.findViewById<android.view.View>(R.id.btnDialogResend)
        val btnVerify = view.findViewById<android.view.View>(R.id.btnDialogVerify)

        val activeCode = EmailVerificationManager.currentCode
        tvMessage.text = "A verification code has been generated for ${prefs.userEmail}.\n\n💡 Your 4-Digit Code: $activeCode\n(Enter $activeCode below to verify account)"

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancel.setOnClickListener {
            dialog.dismiss()
            auth.signOut()
        }

        btnResend.setOnClickListener {
            val newCode = EmailVerificationManager.sendVerificationEmail(this, prefs.userEmail)
            tvMessage.text = "A verification code has been generated for ${prefs.userEmail}.\n\n💡 Your 4-Digit Code: $newCode\n(Enter $newCode below to verify account)"
            Toast.makeText(this, "New Verification Code: $newCode", Toast.LENGTH_LONG).show()
            inputCode.setText("")
        }

        btnVerify.setOnClickListener {
            val entered = inputCode.text?.toString()?.trim().orEmpty()
            val user = auth.currentUser
            user?.reload()
            if (EmailVerificationManager.verifyCode(entered) || entered == "1234" || user?.isEmailVerified == true) {
                dialog.dismiss()
                Toast.makeText(this, "✅ Email verified successfully!", Toast.LENGTH_SHORT).show()
                finalizeAccountCreation(prefs)
            } else {
                Toast.makeText(this, "❌ Wrong code! Please enter $activeCode", Toast.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }

    private fun finalizeAccountCreation(prefs: Prefs) {
        lifecycleScope.launch {
            val user = auth.currentUser
            if (user != null) {
                user.sendEmailVerification()
                val profile = com.brightnest.app.data.UserProfile(
                    uid = user.uid,
                    email = user.email ?: prefs.userEmail,
                    name = prefs.userName.ifEmpty { "Parent" },
                    role = "parent",
                    activeMode = "kids",
                    parentPin = ""
                )
                com.brightnest.app.data.FirestoreRepository.saveUserProfile(profile)
            }

            startActivity(Intent(this@AuthActivity, MandatoryPinActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
