package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.AppLanguageHelper
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentBasicDetailBinding

class BasicDetailFragment : Fragment() {

    private var _binding: FragmentBasicDetailBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBasicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))

        val enArg = arguments?.getString("en") ?: ""
        val urArg = arguments?.getString("ur") ?: ""
        val colorArg = arguments?.getString("color") ?: "#22C55E"
        val titleArg = arguments?.getString("title") ?: "Basics"

        val langCode = Prefs(requireContext()).language.lowercase()

        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                AppLanguageHelper.configureTts(tts, langCode)
                tts?.setSpeechRate(0.85f)
            }
        }

        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()

        val colorInt = runCatching { Color.parseColor(colorArg) }.getOrDefault(Color.RED)
        val fg = ContextCompat.getColor(ctx, R.color.foreground)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        // Header Title
        binding.headerTitle.text = titleArg
        binding.headerTitle.setTextColor(colorInt)

        // Contents
        binding.textEnglish.text = enArg
        binding.textEnglish.setTextColor(fg)
        binding.textUrdu.text = urArg
        binding.textUrdu.setTextColor(colorInt)

        // Card Box Background styling
        binding.cardBox.background = GradientDrawable().apply {
            cornerRadius = dp(28).toFloat()
            setColor(ColorUtils.setAlphaComponent(colorInt, 0x1A))
            setStroke(dp(3), colorInt)
        }

        // Listen Button background styling
        binding.btnListen.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorInt)
        }

        val playSoundAction = View.OnClickListener {
            if (ttsReady) {
                val textToSpeak = if (langCode == "ur") urArg else enArg
                tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "basic_sound")
            }
        }

        binding.cardBox.setOnClickListener(playSoundAction)
        binding.btnListen.setOnClickListener(playSoundAction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        tts = null
        _binding = null
    }
}
