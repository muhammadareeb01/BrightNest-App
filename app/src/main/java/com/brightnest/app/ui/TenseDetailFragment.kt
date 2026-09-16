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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentTenseDetailBinding
import java.util.Locale

class TenseDetailFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentTenseDetailBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTenseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))

        // Initialise Text-To-Speech
        tts = TextToSpeech(requireContext().applicationContext, this)

        val tenseNameArg = arguments?.getString("tenseName") ?: "Present Simple"
        val groupNameArg = arguments?.getString("groupName") ?: "Present"

        val langCode = com.brightnest.app.Prefs(requireContext()).language.lowercase()
        val allGroups = TensesLocalization.getLocalizedGroups(langCode)
        val group = allGroups.find { it.title.equals(groupNameArg, ignoreCase = true) }
            ?: allGroups.first()

        val tense = group.tenses.find { it.name.equals(tenseNameArg, ignoreCase = true) }
            ?: group.tenses.first()

        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()

        val colorInt = runCatching { Color.parseColor(group.color) }.getOrDefault(Color.parseColor("#22C55E"))
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        // Top Header
        binding.headerTitle.text = tense.name
        binding.headerTitle.setTextColor(colorInt)
        binding.headerUrdu.text = tense.ur

        // Formula Card Styling
        binding.formulaCard.background = GradientDrawable().apply {
            cornerRadius = dp(20).toFloat()
            setColor(ColorUtils.setAlphaComponent(colorInt, 0x14))
            setStroke(dp(2), colorInt)
        }

        val groupNameLocal = if (group.ur.isNotBlank()) group.ur else group.title
        binding.badgeCategory.text = "${group.title} Tense • زمانہ $groupNameLocal"
        binding.badgeCategory.setTextColor(colorInt)
        binding.badgeCategory.background = GradientDrawable().apply {
            cornerRadius = dp(12).toFloat()
            setColor(ColorUtils.setAlphaComponent(colorInt, 0x22))
        }

        binding.textTenseUrdu.text = tense.ur
        binding.textTenseUrdu.setTextColor(colorInt)

        binding.textTenseName.text = tense.name
        binding.textTenseName.setTextColor(fg)

        binding.formulaBox.background = GradientDrawable().apply {
            cornerRadius = dp(12).toFloat()
            setColor(ColorUtils.setAlphaComponent(colorInt, 0x20))
        }
        binding.textFormula.text = tense.formula
        binding.textFormula.setTextColor(fg)

        // Populate Sentence Cards
        binding.sentencesContainer.removeAllViews()

        tense.examples.forEachIndexed { index, ex ->
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
                background = GradientDrawable().apply {
                    cornerRadius = dp(18).toFloat()
                    setColor(ColorUtils.setAlphaComponent(colorInt, 0x10))
                    setStroke(dp(1), ColorUtils.setAlphaComponent(colorInt, 0x50))
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(12) }
                isClickable = true
                isFocusable = true
            }

            // Left Number Badge (1, 2, 3...)
            val numBadge = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(ColorUtils.setAlphaComponent(colorInt, 0x25))
                }
                layoutParams = LinearLayout.LayoutParams(dp(36), dp(36)).apply { marginEnd = dp(12) }
            }
            numBadge.addView(TextView(ctx).apply {
                text = "${index + 1}"
                setTextColor(colorInt)
                textSize = 15f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
            })
            card.addView(numBadge)

            // Middle Column: English Sentence + Urdu Translation
            val textCol = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvEnglish = TextView(ctx).apply {
                text = ex.en
                setTextColor(fg)
                textSize = 16f
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            textCol.addView(tvEnglish)

            val tvUrdu = TextView(ctx).apply {
                text = ex.ur
                setTextColor(muted)
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp(4)
                }
            }
            textCol.addView(tvUrdu)

            card.addView(textCol)

            // Right Speaker Button 🔊
            val micBtn = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(colorInt)
                }
                layoutParams = LinearLayout.LayoutParams(dp(42), dp(42)).apply { marginStart = dp(8) }
                isClickable = true
                isFocusable = true
            }
            micBtn.addView(TextView(ctx).apply {
                text = "🔊"
                textSize = 18f
                gravity = Gravity.CENTER
            })
            card.addView(micBtn)

            // Action: Speak this exact sentence when clicking card or speaker icon
            val sentenceClickAction = View.OnClickListener {
                speakSentence(ex)
            }
            card.setOnClickListener(sentenceClickAction)
            micBtn.setOnClickListener(sentenceClickAction)

            binding.sentencesContainer.addView(card)
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            return
        }

        // Tenses sentences are in English, so configure English locale
        val result = tts?.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.ENGLISH)
        }

        // Natural, clear pace for learners
        tts?.setSpeechRate(0.85f)
        tts?.setPitch(1.0f)
        ttsReady = true
    }

    private fun speakSentence(ex: TensesFragment.Ex) {
        if (!ttsReady) {
            Toast.makeText(context, "Please wait, TTS is loading...", Toast.LENGTH_SHORT).show()
            return
        }
        tts?.stop()
        
        // 1. Speak English part
        tts?.language = Locale.US
        tts?.speak(ex.en, TextToSpeech.QUEUE_FLUSH, null, "tense_sentence_en")
        
        // 2. Speak translated part if it's not English mode and text is not empty
        val langCode = com.brightnest.app.Prefs(requireContext()).language.lowercase()
        if (langCode != "en" && ex.ur.isNotBlank()) {
            com.brightnest.app.AppLanguageHelper.configureTts(tts, langCode)
            tts?.speak(ex.ur, TextToSpeech.QUEUE_ADD, null, "tense_sentence_loc")
        }
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ttsReady = false
        super.onDestroyView()
        _binding = null
    }
}
