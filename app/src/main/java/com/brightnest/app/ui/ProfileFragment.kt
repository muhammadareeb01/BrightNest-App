package com.brightnest.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.AuthActivity
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentProfileBinding
import com.google.android.material.chip.Chip

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val wa = "923205340788"
    private val email = "id83002@gmail.com"

    private val languages = listOf(
        "en" to "English",
        "ur" to "اردو",
        "ar" to "العربية",
        "hi" to "हिन्दी"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = Prefs(requireContext())

        val name = prefs.userName.ifEmpty { "User" }
        binding.name.text = name
        binding.email.text = prefs.userEmail.ifEmpty { "No email" }
        binding.avatar.text = name.firstOrNull()?.uppercase() ?: "?"

        buildLangChips(prefs)

        binding.switchAdult.isChecked = prefs.mode == "adult"
        binding.switchAdult.setOnClickListener {
            val wantAdult = binding.switchAdult.isChecked
            if (wantAdult) {
                // User wants to turn ON Adult Mode -> Require PIN
                PinDialogHelper.show(requireContext(), prefs, onSuccess = {
                    binding.switchAdult.isChecked = true
                    prefs.mode = "adult"
                    requireActivity().recreate()
                }, onCancel = {
                    binding.switchAdult.isChecked = false
                })
            } else {
                // User wants to turn OFF Adult Mode (switch to Kids Mode)
                binding.switchAdult.isChecked = false
                prefs.mode = "kids"
                requireActivity().recreate()
            }
        }

        binding.rowSettings.setOnClickListener {
            PinDialogHelper.show(requireContext(), prefs, onSuccess = {
                findNavController().navigate(R.id.settingsFragment)
            })
        }
        binding.rowParent.setOnClickListener {
            PinDialogHelper.show(requireContext(), prefs, onSuccess = {
                findNavController().navigate(R.id.parentControlFragment)
            })
        }
        binding.rowSubscription.setOnClickListener {
            PinDialogHelper.show(requireContext(), prefs, onSuccess = {
                findNavController().navigate(R.id.subscriptionFragment)
            })
        }

        binding.rowAcademyWhatsapp.setOnClickListener { openWhatsApp() }
        binding.rowAcademyCall.setOnClickListener { openDial() }
        binding.rowAcademyEmail.setOnClickListener { openEmail() }
        binding.rowSupportWhatsapp.setOnClickListener { openWhatsApp() }
        binding.rowSupportCall.setOnClickListener { openDial() }
        binding.rowSupportEmail.setOnClickListener { openEmail() }

        binding.btnSignOut.setOnClickListener {
            prefs.logout()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
        com.brightnest.app.AppLanguageHelper.localizeViewTree(binding.root, prefs.language)
    }

    private fun buildLangChips(prefs: Prefs) {
        binding.langChips.removeAllViews()
        languages.forEach { (code, label) ->
            val chip = Chip(requireContext()).apply {
                text = label
                isCheckable = true
                isChecked = prefs.language == code
                setOnClickListener {
                    prefs.language = code
                    buildLangChips(prefs)
                    requireActivity().recreate()
                }
            }
            binding.langChips.addView(chip)
        }
    }

    private fun safeStart(intent: Intent) {
        try { startActivity(intent) } catch (_: Exception) {}
    }

    private fun openWhatsApp() {
        safeStart(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$wa")))
    }

    private fun openDial() {
        safeStart(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+$wa")))
    }

    private fun openEmail() {
        safeStart(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
