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
import androidx.lifecycle.lifecycleScope
import com.brightnest.app.BrightNestApp
import kotlinx.coroutines.launch

class WuduFragment : Fragment() {

    private var _binding: FragmentContentListBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private data class Step(val n: Int, val name: String, val nameUrdu: String, val desc: String, val descUrdu: String, val icon: String)

    private val palette = listOf("#06B6D4", "#0EA5E9", "#3B82F6", "#6366F1", "#8B5CF6", "#A855F7", "#10B981", "#14B8A6", "#22C55E")

    private val steps = listOf(
        Step(1, "Niyyah", "نیّت", "Make intention in your heart to perform Wudu.", "دل میں وضو کرنے کی نیّت کریں۔", "❤️"),
        Step(2, "Bismillah", "بسم اللہ", "Say Bismillah before starting.", "وضو شروع کرنے سے پہلے بسم اللہ پڑھیں۔", "☪️"),
        Step(3, "Hands", "ہاتھ دھونا", "Wash both hands up to the wrists, three times.", "دونوں ہاتھ کلائیوں تک تین بار دھوئیں۔", "✋"),
        Step(4, "Mouth", "کلی کرنا", "Rinse the mouth with water three times (Misvak if possible).", "تین بار منہ میں پانی لے کر کلی کریں (ہو سکے تو مسواک کریں)۔", "🦷"),
        Step(5, "Nose", "ناک میں پانی", "Sniff water into the nose and blow out, three times.", "تین بار ناک میں پانی چڑھائیں اور صاف کریں۔", "💨"),
        Step(6, "Face", "چہرہ دھونا", "Wash the whole face three times — forehead to chin, ear to ear.", "پورا چہرہ تین بار دھوئیں — پیشانی سے ٹھوڑی تک، کان سے کان تک۔", "😊"),
        Step(7, "Arms", "بازو دھونا", "Wash both arms up to and including the elbows three times.", "دونوں بازو کہنیوں سمیت تین بار دھوئیں۔", "💪"),
        Step(8, "Head (Masah)", "سر کا مسح", "Wipe wet hands over the head once (Masah).", "گیلے ہاتھوں سے سر کا ایک بار مسح کریں۔", "💆"),
        Step(9, "Ears", "کانوں کا مسح", "Wipe the inside and outside of the ears once.", "کانوں کے اندر اور باہر ایک بار مسح کریں۔", "👂"),
        Step(10, "Feet", "پاؤں دھونا", "Wash both feet up to and including the ankles three times.", "دونوں پاؤں ٹخنوں سمیت تین بار دھوئیں۔", "👣"),
        Step(11, "Dua", "دعا", "After Wudu recite the Shahadah and the Wudu Dua.", "وضو کے بعد کلمہ شہادت اور وضو کی دعا پڑھیں۔", "🤲")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        tts = TextToSpeech(requireContext().applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                ttsReady = true
                com.brightnest.app.AppLanguageHelper.configureTts(tts, com.brightnest.app.Prefs(requireContext()).language)
                tts?.setSpeechRate(0.85f)
            }
        }

        binding.title.text = "Wudu"
        binding.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        binding.subTitle.text = "وضو"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val intro = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                setColor(card)
                setStroke(dp(1), border)
            }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(12) }
        }
        intro.addView(TextView(ctx).apply {
            text = "Wudu is the washing we do before Salah to be clean and pure."
            setTextColor(fg); gravity = Gravity.CENTER
        })
        intro.addView(TextView(ctx).apply {
            text = "وضو وہ پاکیزگی ہے جو نماز سے پہلے کی جاتی ہے تاکہ ہم صاف ستھرے ہو کر اللہ کے سامنے کھڑے ہوں۔"
            setTextColor(fg); gravity = Gravity.CENTER; textSize = 15f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) }
        })
        binding.container.addView(intro)

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            val cached = try { db.wuduStepDao().all() } catch (e: Exception) { emptyList() }
            val renderSteps = if (cached.isNotEmpty()) {
                cached.map { w -> Step(w.stepNumber, w.titleEn, w.titleUr, w.descEn, w.descUr, w.icon) }
            } else steps

            if (_binding == null) return@launch
            renderSteps.forEachIndexed { i, s ->
                val c = Color.parseColor(palette[i % palette.size])
                val row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(16), dp(16), dp(16), dp(16))
                    background = GradientDrawable().apply {
                        cornerRadius = dp(16).toFloat()
                        setColor(ColorUtils.setAlphaComponent(c, 0x20))
                        setStroke(dp(2), c)
                    }
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        .apply { bottomMargin = dp(12) }
                    setOnClickListener { 
                        val isUrdu = com.brightnest.app.Prefs(ctx).language == "ur"
                        if (isUrdu) {
                            speak("سٹیپ ${s.n}. ${s.nameUrdu}. ${s.descUrdu}")
                        } else {
                            speak("Step ${s.n}. ${s.name}. ${s.desc}")
                        }
                    }
                }

                row.addView(TextView(ctx).apply {
                    text = s.n.toString(); setTextColor(Color.WHITE); textSize = 18f
                    setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
                    background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(c) }
                    layoutParams = LinearLayout.LayoutParams(dp(44), dp(44)).apply { marginEnd = dp(12) }
                })
                row.addView(TextView(ctx).apply {
                    text = s.icon; textSize = 26f
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(12) }
                })

                val col = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                val nameRow = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
                nameRow.addView(TextView(ctx).apply {
                    text = s.name; setTextColor(c); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                })
                nameRow.addView(TextView(ctx).apply {
                    text = s.nameUrdu; setTextColor(c); textSize = 15f; setTypeface(typeface, Typeface.BOLD)
                })
                col.addView(nameRow)
                col.addView(TextView(ctx).apply {
                    text = s.desc; setTextColor(fg); textSize = 13f
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) }
                })
                col.addView(TextView(ctx).apply {
                    text = s.descUrdu; setTextColor(fg); textSize = 14f; gravity = Gravity.END
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) }
                })
                row.addView(col)
                binding.container.addView(row)
            }
        }
    }

    private fun speak(t: String) { if (ttsReady) { tts?.stop(); tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "bn") } }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown(); tts = null
        _binding = null
    }
}
