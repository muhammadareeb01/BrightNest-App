package com.brightnest.app.ui

import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.brightnest.app.Prefs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

object PinDialogHelper {

    fun show(context: Context, prefs: Prefs, onSuccess: () -> Unit, onCancel: (() -> Unit)? = null) {
        val storedPin = prefs.parentPin

        if (!prefs.isAdult && storedPin.isBlank()) {
            val input = EditText(context).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                hint = "Answer"
            }
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(60, 20, 60, 0)
                addView(input)
            }
            val num1 = (5..12).random()
            val num2 = (5..12).random()
            val ans = num1 * num2

            MaterialAlertDialogBuilder(context)
                .setTitle("Grown-Ups Only!")
                .setMessage("Ask your parents for help.\n\nWhat is $num1 x $num2?")
                .setView(container)
                .setCancelable(false)
                .setPositiveButton("Verify") { d, _ ->
                    if (input.text.toString().trim() == ans.toString()) {
                        d.dismiss()
                        val intent = android.content.Intent(context, com.brightnest.app.AuthActivity::class.java)
                        intent.putExtra("force_login", true)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Oops, that's not right!", Toast.LENGTH_SHORT).show()
                        onCancel?.invoke()
                    }
                }
                .setNegativeButton("Cancel") { d, _ ->
                    d.dismiss()
                    onCancel?.invoke()
                }
                .show()
            return
        }

        if (storedPin.isBlank()) {
            val input = EditText(context).apply {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                hint = "Create 4-Digit Parent PIN"
            }
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(60, 20, 60, 0)
                addView(input)
            }
            MaterialAlertDialogBuilder(context)
                .setTitle("🔒 Set Parent Security PIN")
                .setMessage("Please create a 4-digit Parent PIN to access Adult Mode and parent controls.")
                .setView(container)
                .setCancelable(false)
                .setPositiveButton("Save PIN") { d, _ ->
                    val entered = input.text.toString().trim()
                    if (entered.length == 4 && entered.all { it.isDigit() }) {
                        prefs.parentPin = entered
                        d.dismiss()
                        onSuccess()
                    } else {
                        Toast.makeText(context, "PIN must be exactly 4 digits.", Toast.LENGTH_SHORT).show()
                        onCancel?.invoke()
                    }
                }
                .setNegativeButton("Cancel") { d, _ ->
                    d.dismiss()
                    onCancel?.invoke()
                }
                .show()
            return
        }

        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "Enter 4-Digit Parent PIN"
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 20, 60, 0)
            addView(input)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("🔒 Parent Security Gate")
            .setMessage("Please enter your 4-digit Parent PIN to continue.")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("Verify") { d, _ ->
                val entered = input.text.toString().trim()
                if (entered == storedPin) {
                    d.dismiss()
                    onSuccess()
                } else {
                    Toast.makeText(context, "Incorrect PIN.", Toast.LENGTH_SHORT).show()
                    onCancel?.invoke()
                }
            }
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
                onCancel?.invoke()
            }
            .setNeutralButton("Forgot PIN?") { d, _ ->
                d.dismiss()
                val email = prefs.userEmail.ifEmpty { FirebaseAuth.getInstance().currentUser?.email ?: "" }
                if (email.isNotBlank() && email.contains("@")) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            Toast.makeText(context, "PIN recovery email sent to $email", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Recovery email sent to $email", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(context, "Please check your registered parent email.", Toast.LENGTH_LONG).show()
                }
                onCancel?.invoke()
            }
            .show()
    }
}
