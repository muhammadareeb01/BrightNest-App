package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.AppLanguageHelper
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentVowelDetailBinding

class VowelDetailFragment : Fragment() {

    private var _binding: FragmentVowelDetailBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVowelDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))

        val letterArg = arguments?.getString("letter") ?: "A"
        val vowel = VowelsFragment.allVowels.find { it.letter.equals(letterArg, ignoreCase = true) }
            ?: VowelsFragment.allVowels.first()

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

        val colorInt = runCatching { Color.parseColor(vowel.color) }.getOrDefault(Color.RED)
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        // Header title
        val titleText = when (langCode) {
            "ur" -> "حرف ${vowel.letter}"
            "ar" -> "الحرف ${vowel.letter}"
            "hi" -> "स्वर ${vowel.letter}"
            else -> "Vowel ${vowel.letter}"
        }
        binding.headerTitle.text = titleText
        binding.headerTitle.setTextColor(colorInt)

        // Top Square Box with Letter
        binding.letterText.text = vowel.letter
        binding.letterText.setTextColor(colorInt)
        binding.letterCardBox.background = GradientDrawable().apply {
            cornerRadius = dp(28).toFloat()
            setColor(ColorUtils.setAlphaComponent(colorInt, 0x1A))
            setStroke(dp(3), colorInt)
        }

        // Mic/Listen button next to the square box (exact circle 48dp like bottom word cards)
        binding.btnListenLetter.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorInt)
        }

        // Speak only the letter when clicking the top box or mic button
        val letterClickAction = View.OnClickListener { speakLetter(vowel.letter) }
        binding.letterCardBox.setOnClickListener(letterClickAction)
        binding.btnListenLetter.setOnClickListener(letterClickAction)

        // Section title above word cards
        val wTitle = when (langCode) {
            "ur" -> "${vowel.letter} سے شروع ہونے والے الفاظ"
            "ar" -> "كلمات تبدأ بحرف ${vowel.letter}"
            "hi" -> "${vowel.letter} से शुरू होने वाले शब्द"
            else -> "Words with ${vowel.letter}"
        }
        binding.wordsTitle.text = wTitle

        // Populate bottom word cards
        binding.wordsContainer.removeAllViews()
        vowel.words.forEach { w ->
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
                background = GradientDrawable().apply {
                    cornerRadius = dp(22).toFloat()
                    setColor(ColorUtils.setAlphaComponent(colorInt, 0x12))
                    setStroke(dp(2), ColorUtils.setAlphaComponent(colorInt, 0x40))
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(14) }
            }

            // Emoji icon
            val emojiView = TextView(ctx).apply {
                text = w.emoji
                textSize = 42f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dp(64), dp(64)).apply { marginEnd = dp(14) }
            }
            card.addView(emojiView)

            // Middle text column (English word + localized translation)
            val textCol = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            textCol.addView(TextView(ctx).apply {
                text = w.word
                setTextColor(fg)
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
            })
            val loc = w.getLocalized(langCode)
            if (loc.isNotEmpty() && (langCode != "en" || loc != w.word)) {
                textCol.addView(TextView(ctx).apply {
                    text = loc
                    setTextColor(muted)
                    textSize = 16f
                    setTypeface(typeface, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = dp(4) }
                })
            }
            card.addView(textCol)

            // Mic / Speaker button on each card
            val micBtn = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(colorInt)
                }
                layoutParams = LinearLayout.LayoutParams(dp(48), dp(48)).apply { marginStart = dp(8) }
            }
            micBtn.addView(TextView(ctx).apply {
                text = "🔊"
                textSize = 20f
                gravity = Gravity.CENTER
            })
            card.addView(micBtn)

            // Clicking word card or its mic button speaks just this word
            val wordClickAction = View.OnClickListener { speakWord(w, vowel.letter, langCode) }
            card.setOnClickListener(wordClickAction)
            micBtn.setOnClickListener(wordClickAction)

            binding.wordsContainer.addView(card)
        }
    }

    private fun speakLetter(letter: String) {
        if (!ttsReady) return
        tts?.speak(letter, TextToSpeech.QUEUE_FLUSH, null, "vowel_letter")
    }

    private fun speakWord(w: VowelsFragment.Word, letter: String, langCode: String) {
        if (!ttsReady) return
        val loc = w.getLocalized(langCode)
        val textToSpeak = when (langCode) {
            "ur" -> "$letter is for ${w.word}۔ $loc"
            "ar" -> "$letter is for ${w.word}. $loc"
            "hi" -> "$letter is for ${w.word}. $loc"
            else -> "$letter is for ${w.word}"
        }
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "vowel_word_${w.word}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        tts = null
        _binding = null
    }
}
