package com.brightnest.app.ui

import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding
import java.util.Locale

class EnglishFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null

    private data class Phrase(val text: String, val meaning: String)

    private val phrases = listOf(
        Phrase("Good morning", "Used to greet someone in the morning"),
        Phrase("How are you?", "A common polite greeting"),
        Phrase("Thank you very much", "Expressing gratitude"),
        Phrase("I would like a coffee please", "Ordering a drink"),
        Phrase("Where is the nearest...", "Asking for directions"),
        Phrase("Could you help me?", "Asking for assistance"),
        Phrase("Nice to meet you", "Greeting someone new"),
        Phrase("I am from...", "Stating your origin"),
        Phrase("What time is it?", "Asking for the time"),
        Phrase("Have a good day", "A polite farewell")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        binding.title.text = "English Phrases"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.8f)
            }
        }
        build()
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "en")
    }

    private fun build() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val scroll = ScrollView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            isFillViewport = true
        }
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 48))
        }

        // hero
        val hero = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(GameUi.dp(ctx, 32), GameUi.dp(ctx, 32), GameUi.dp(ctx, 32), GameUi.dp(ctx, 32))
            background = GameUi.rounded(GameUi.withAlpha(primary, 0x15), GameUi.dpf(ctx, 24))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 24) }
        }
        hero.addView(TextView(ctx).apply { text = "💬"; textSize = 36f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) } })
        hero.addView(TextView(ctx).apply { text = "Everyday English"; setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER })
        hero.addView(TextView(ctx).apply { text = "Listen and practice common phrases"; setTextColor(muted); textSize = 16f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 8) } })
        col.addView(hero)

        phrases.forEach { p ->
            val cardView = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
                background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) }
            }
            val textCol = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = GameUi.dp(ctx, 16) }
            }
            textCol.addView(TextView(ctx).apply { text = p.text; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 4) } })
            textCol.addView(TextView(ctx).apply { text = p.meaning; setTextColor(muted); textSize = 13f })
            cardView.addView(textCol)
            val play = TextView(ctx).apply {
                text = "🔊"; textSize = 18f; gravity = Gravity.CENTER
                background = GameUi.rounded(GameUi.withAlpha(primary, 0x20), GameUi.dpf(ctx, 24))
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 48), GameUi.dp(ctx, 48))
                setOnClickListener { speak(p.text) }
            }
            cardView.addView(play)
            col.addView(cardView)
        }

        scroll.addView(col)
        binding.toolRoot.addView(scroll)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop(); tts?.shutdown(); tts = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
