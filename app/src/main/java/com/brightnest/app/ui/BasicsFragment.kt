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
import com.brightnest.app.AppLanguageHelper
import com.brightnest.app.Prefs
import java.util.Locale

class BasicsFragment : Fragment() {

    private var _binding: FragmentContentListBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    data class Item(val en: String, val ur: String)
    data class Section(val title: String, val ur: String, val emoji: String, val color: String, val chips: Boolean, val items: List<Item>)

    companion object {
        val allSections = listOf(
            Section("Days of the Week", "ہفتے کے دن", "📅", "#22C55E", true, listOf(
                Item("Sunday", "اتوار"), Item("Monday", "پیر"), Item("Tuesday", "منگل"),
                Item("Wednesday", "بدھ"), Item("Thursday", "جمعرات"), Item("Friday", "جمعہ"), Item("Saturday", "ہفتہ")
            )),
            Section("Months", "مہینے", "🗓️", "#3B82F6", true, listOf(
                Item("January", "جنوری"), Item("February", "فروری"), Item("March", "مارچ"), Item("April", "اپریل"),
                Item("May", "مئی"), Item("June", "جون"), Item("July", "جولائی"), Item("August", "اگست"),
                Item("September", "ستمبر"), Item("October", "اکتوبر"), Item("November", "نومبر"), Item("December", "دسمبر")
            )),
            Section("Seasons", "موسم", "⛅", "#F59E0B", true, listOf(
                Item("Spring", "بہار"), Item("Summer", "گرمی"), Item("Autumn", "خزاں"), Item("Winter", "سردی")
            )),
            Section("Good Habits", "اچھی عادتیں", "🫶", "#EC4899", false, listOf(
                Item("Always tell the truth.", "ہمیشہ سچ بولو۔"),
                Item("Say please and thank you.", "براہِ کرم اور شکریہ کہو۔"),
                Item("Wash your hands before eating.", "کھانے سے پہلے ہاتھ دھوؤ۔"),
                Item("Respect your elders.", "بڑوں کا احترام کرو۔"),
                Item("Keep yourself clean.", "اپنے آپ کو صاف رکھو۔")
            ))
        )
    }

    private val sections = allSections

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        val langCode = Prefs(requireContext()).language
        tts = TextToSpeech(requireContext().applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                ttsReady = true
                AppLanguageHelper.configureTts(tts, langCode)
                tts?.setSpeechRate(0.85f)
            }
        }

        binding.title.text = "Basics"
        binding.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.coral))
        binding.subTitle.text = "بنیادی باتیں"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        sections.forEach { s ->
            val c = Color.parseColor(s.color)
            val header = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    .apply { topMargin = dp(14); bottomMargin = dp(10) }
            }
            header.addView(TextView(ctx).apply {
                text = s.emoji; textSize = 16f; gravity = Gravity.CENTER
                background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(c) }
                layoutParams = LinearLayout.LayoutParams(dp(32), dp(32)).apply { marginEnd = dp(8) }
            })
            header.addView(TextView(ctx).apply {
                text = s.title; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            header.addView(TextView(ctx).apply { text = s.ur; setTextColor(muted); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
            binding.container.addView(header)

            if (s.chips) {
                val flow = FlowLayout(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
                s.items.forEach { it2 ->
                    val chip = LinearLayout(ctx).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER
                        setPadding(dp(14), dp(10), dp(14), dp(10))
                        background = GradientDrawable().apply {
                            cornerRadius = dp(14).toFloat()
                            setColor(ColorUtils.setAlphaComponent(c, 0x14))
                            setStroke(dp(2), c)
                        }
                        setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("en", it2.en)
                                putString("ur", it2.ur)
                                putString("color", s.color)
                                putString("title", s.title)
                            }
                            findNavController().navigate(R.id.basicDetailFragment, bundle)
                        }
                    }
                    chip.addView(TextView(ctx).apply { text = it2.en; setTextColor(fg); textSize = 13f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER })
                    chip.addView(TextView(ctx).apply { text = it2.ur; setTextColor(c); textSize = 12f; gravity = Gravity.CENTER })
                    flow.addView(chip)
                }
                binding.container.addView(flow)
            } else {
                s.items.forEach { it2 ->
                    val card = LinearLayout(ctx).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(dp(16), dp(14), dp(16), dp(14))
                        background = GradientDrawable().apply {
                            cornerRadius = dp(14).toFloat()
                            setColor(ColorUtils.setAlphaComponent(c, 0x14))
                            setStroke(dp(2), c)
                        }
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            .apply { bottomMargin = dp(8) }
                        setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("en", it2.en)
                                putString("ur", it2.ur)
                                putString("color", s.color)
                                putString("title", s.title)
                            }
                            findNavController().navigate(R.id.basicDetailFragment, bundle)
                        }
                    }
                    card.addView(TextView(ctx).apply { text = it2.en; setTextColor(fg); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
                    card.addView(TextView(ctx).apply { text = it2.ur; setTextColor(muted); textSize = 13f })
                    binding.container.addView(card)
                }
            }
        }

        binding.container.addView(TextView(ctx).apply {
            text = "Tap any item to hear it · سننے کے لیے دبائیں"
            setTextColor(muted); textSize = 12f; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) }
        })
    }

    private fun speak(t: String) { if (ttsReady) tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "bn") }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown(); tts = null
        _binding = null
    }
}
