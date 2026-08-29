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
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentContentListBinding
import java.util.Locale

class VowelsFragment : Fragment() {

    private var _binding: FragmentContentListBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    data class Word(
        val word: String,
        val ur: String,
        val emoji: String,
        val ar: String = "",
        val hi: String = ""
    ) {
        fun getLocalized(langCode: String): String {
            return when (langCode.lowercase()) {
                "ur" -> if (ur.isNotEmpty()) ur else word
                "ar" -> if (ar.isNotEmpty()) ar else word
                "hi" -> if (hi.isNotEmpty()) hi else word
                else -> if (ur.isNotEmpty()) ur else word
            }
        }
    }
    data class Vowel(val letter: String, val color: String, val words: List<Word>)

    companion object {
        val allVowels = listOf(
            Vowel("A", "#EF4444", listOf(
                Word("Apple", "سیب", "🍎", "تفاحة", "सेब"),
                Word("Ant", "چیونٹی", "🐜", "نملة", "चींटी"),
                Word("Arrow", "تیر", "⬆️", "سهم", "तीर")
            )),
            Vowel("E", "#F59E0B", listOf(
                Word("Egg", "انڈا", "🥚", "بيضة", "अंडा"),
                Word("Elephant", "ہاتھی", "🐘", "فيل", "हाथी"),
                Word("Eagle", "عقاب", "🦅", "نسر", "चील")
            )),
            Vowel("I", "#22C55E", listOf(
                Word("Ice", "برف", "❄️", "جليد", "बर्फ"),
                Word("Igloo", "برفانی گھر", "🛖", "كوخ الجليد", "इग्लू"),
                Word("Ink", "سیاہی", "🖋️", "حبر", "स्याही")
            )),
            Vowel("O", "#3B82F6", listOf(
                Word("Orange", "مالٹا", "🍊", "برتقال", "संतरा"),
                Word("Owl", "اُلّو", "🦉", "بومة", "उल्लू"),
                Word("Ostrich", "شترمرغ", "🦤", "نعامة", "शुतुरमुर्ग")
            )),
            Vowel("U", "#8B5CF6", listOf(
                Word("Umbrella", "چھتری", "☂️", "مظلة", "छतरी"),
                Word("Up", "اوپر", "⬆️", "فوق", "ऊपर"),
                Word("Urn", "مٹکا", "🏺", "جرة", "कलश")
            ))
        )
    }

    private val vowels = allVowels

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        val langCode = com.brightnest.app.Prefs(requireContext()).language.lowercase()

        tts = TextToSpeech(requireContext().applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                ttsReady = true
                com.brightnest.app.AppLanguageHelper.configureTts(tts, langCode)
                tts?.setSpeechRate(0.8f)
            }
        }

        binding.title.text = "Vowels"
        binding.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.coral))
        val subText = when (langCode) {
            "ur" -> "حروفِ علت · A E I O U"
            "ar" -> "حروف العلة · A E I O U"
            "hi" -> "स्वर (Vowels) · A E I O U"
            else -> "Vowel Letters · A E I O U"
        }
        binding.subTitle.text = subText
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        vowels.forEach { v ->
            val c = Color.parseColor(v.color)
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
                background = GradientDrawable().apply {
                    cornerRadius = dp(20).toFloat()
                    setColor(ColorUtils.setAlphaComponent(c, 0x14))
                    setStroke(dp(2), c)
                }
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    .apply { bottomMargin = dp(14) }
                setOnClickListener {
                    val bundle = Bundle().apply { putString("letter", v.letter) }
                    findNavController().navigate(R.id.vowelDetailFragment, bundle)
                }
            }

            val circle = TextView(ctx).apply {
                text = v.letter
                setTextColor(Color.WHITE)
                textSize = 40f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
                background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(c) }
                layoutParams = LinearLayout.LayoutParams(dp(72), dp(72)).apply { marginEnd = dp(16) }
            }
            card.addView(circle)

            val col = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            v.words.forEach { w ->
                val row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        .apply { topMargin = dp(2); bottomMargin = dp(2) }
                }
                row.addView(TextView(ctx).apply {
                    text = w.emoji; textSize = 18f
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(8) }
                })
                row.addView(TextView(ctx).apply {
                    text = w.word; setTextColor(fg); setTypeface(typeface, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                })
                row.addView(TextView(ctx).apply {
                    text = w.getLocalized(langCode)
                    setTextColor(muted)
                })
                col.addView(row)
            }
            card.addView(col)
            binding.container.addView(card)
        }

        val hintText = when (langCode) {
            "ur" -> "تفصیلات اور آواز کے لیے کارڈ دبائیں"
            "ar" -> "اضغط على البطاقة للتفاصيل والاستماع"
            "hi" -> "विवरण और ध्वनि के लिए कार्ड दबाएं"
            else -> "Tap a card to open details & sounds"
        }
        binding.container.addView(TextView(ctx).apply {
            text = hintText
            setTextColor(muted); textSize = 13f; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) }
        })
    }

    private fun speak(t: String) { if (ttsReady) tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "bn") }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown(); tts = null
        _binding = null
    }
}
