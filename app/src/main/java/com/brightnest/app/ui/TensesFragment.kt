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

class TensesFragment : Fragment() {

    private var _binding: FragmentContentListBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    data class Ex(val en: String, val ur: String)
    data class Tense(val name: String, val ur: String, val formula: String, val examples: List<Ex>)
    data class Group(val title: String, val ur: String, val color: String, val tenses: List<Tense>)

    companion object {
        val allGroups = listOf(
            Group("Present", "حال", "#22C55E", listOf(
                Tense("Present Simple", "فعل حال سادہ", "Subject + base verb (+s/es)", listOf(
                    Ex("I eat an apple.", "میں سیب کھاتا ہوں۔"),
                    Ex("She plays cricket.", "وہ کرکٹ کھیلتی ہے۔"),
                    Ex("They go to school every day.", "وہ روزانہ اسکول جاتے ہیں۔"),
                    Ex("The sun rises in the east.", "سورج مشرق سے نکلتا ہے۔")
                )),
                Tense("Present Continuous", "حال جاری", "Subject + am/is/are + verb-ing", listOf(
                    Ex("I am eating an apple.", "میں سیب کھا رہا ہوں۔"),
                    Ex("They are playing football.", "وہ فٹ بال کھیل رہے ہیں۔"),
                    Ex("She is reading an interesting book.", "وہ ایک دلچسپ کتاب پڑھ رہی ہے۔"),
                    Ex("It is raining outside.", "باہر بارش ہو رہی ہے۔")
                )),
                Tense("Present Perfect", "حال کامل", "Subject + has/have + past participle (3rd form)", listOf(
                    Ex("I have eaten an apple.", "میں سیب کھا چکا ہوں۔"),
                    Ex("She has gone home.", "وہ گھر جا چکی ہے۔"),
                    Ex("We have completed our homework.", "ہم نے اپنا ہوم ورک مکمل کر لیا ہے۔"),
                    Ex("They have won the match.", "وہ میچ جیت چکے ہیں۔")
                )),
                Tense("Present Perfect Continuous", "حال کامل جاری", "Subject + has/have been + verb-ing + since/for", listOf(
                    Ex("It has been raining since morning.", "صبح سے بارش ہو رہی ہے۔"),
                    Ex("He has been studying for two hours.", "وہ دو گھنٹے سے پڑھ رہا ہے۔"),
                    Ex("They have been playing since 4 PM.", "وہ شام چار بجے سے کھیل رہے ہیں۔")
                ))
            )),
            Group("Past", "ماضی", "#F59E0B", listOf(
                Tense("Past Simple", "فعل ماضی سادہ", "Subject + past verb (2nd form)", listOf(
                    Ex("I ate an apple.", "میں نے سیب کھایا۔"),
                    Ex("She played cricket.", "اس نے کرکٹ کھیلی۔"),
                    Ex("We visited the museum yesterday.", "ہم نے کل عجائب گھر کی سیر کی۔"),
                    Ex("He wrote a beautiful poem.", "اس نے ایک خوبصورت نظم لکھی۔")
                )),
                Tense("Past Continuous", "ماضی جاری", "Subject + was/were + verb-ing", listOf(
                    Ex("I was eating an apple.", "میں سیب کھا رہا تھا۔"),
                    Ex("They were playing in the park.", "وہ پارک میں کھیل رہے تھے۔"),
                    Ex("She was cooking delicious dinner.", "وہ مزیدار کھانا پکا رہی تھی۔"),
                    Ex("It was raining all night.", "ساری رات بارش ہو رہی تھی۔")
                )),
                Tense("Past Perfect", "ماضی کامل", "Subject + had + past participle (3rd form)", listOf(
                    Ex("I had eaten an apple.", "میں سیب کھا چکا تھا۔"),
                    Ex("She had gone home before I arrived.", "میرے آنے سے پہلے وہ گھر جا چکی تھی۔"),
                    Ex("The train had already left.", "ٹرین پہلے ہی روانہ ہو چکی تھی۔")
                )),
                Tense("Past Perfect Continuous", "ماضی کامل جاری", "Subject + had been + verb-ing + since/for", listOf(
                    Ex("They had been working since morning.", "وہ صبح سے کام کر رہے تھے۔"),
                    Ex("She had been waiting for an hour.", "وہ ایک گھنٹے سے انتظار کر رہی تھی۔")
                ))
            )),
            Group("Future", "مستقبل", "#3B82F6", listOf(
                Tense("Future Simple", "فعل مستقبل سادہ", "Subject + will + base verb", listOf(
                    Ex("I will eat an apple.", "میں سیب کھاؤں گا۔"),
                    Ex("She will play cricket.", "وہ کرکٹ کھیلے گی۔"),
                    Ex("We will visit our grandparents tomorrow.", "ہم کل اپنے دادا دادی سے ملنے جائیں گے۔"),
                    Ex("They will pass the examination.", "وہ امتحان پاس کر لیں گے۔")
                )),
                Tense("Future Continuous", "مستقبل جاری", "Subject + will be + verb-ing", listOf(
                    Ex("I will be eating an apple.", "میں سیب کھا رہا ہوں گا۔"),
                    Ex("They will be playing tomorrow evening.", "وہ کل شام کھیل رہے ہوں گے۔"),
                    Ex("She will be traveling to Lahore.", "وہ لاہور کا سفر کر رہی ہوگی۔")
                )),
                Tense("Future Perfect", "مستقبل کامل", "Subject + will have + past participle (3rd form)", listOf(
                    Ex("I will have eaten an apple.", "میں سیب کھا چکا ہوں گا۔"),
                    Ex("She will have gone home by evening.", "وہ شام تک گھر جا چکی ہوگی۔"),
                    Ex("We will have finished this project by tomorrow.", "ہم کل تک یہ پروجیکٹ مکمل کر چکے ہوں گے۔")
                )),
                Tense("Future Perfect Continuous", "مستقبل کامل جاری", "Subject + will have been + verb-ing + since/for", listOf(
                    Ex("By next year, I will have been studying here for three years.", "اگلے سال تک، میں یہاں تین سال سے پڑھ رہا ہوں گا۔")
                ))
            ))
        )
    }

    private val groups = allGroups

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        
        // Initialise TTS with English locale for crisp English sentence pronunciation
        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.ENGLISH)
                }
                tts?.setSpeechRate(0.85f)
            }
        }

        binding.title.text = "English Tenses"
        binding.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.coral))
        binding.subTitle.text = "انگریزی ٹینس"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        groups.forEach { g ->
            val c = Color.parseColor(g.color)
            val header = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    .apply { topMargin = dp(14); bottomMargin = dp(10) }
            }
            header.addView(View(ctx).apply {
                background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(c) }
                layoutParams = LinearLayout.LayoutParams(dp(12), dp(12)).apply { marginEnd = dp(8) }
            })
            header.addView(TextView(ctx).apply {
                text = g.title; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            header.addView(TextView(ctx).apply { text = g.ur; setTextColor(muted); textSize = 16f; setTypeface(typeface, Typeface.BOLD) })
            binding.container.addView(header)

            g.tenses.forEach { tn ->
                val card = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(16), dp(16), dp(16), dp(16))
                    background = GradientDrawable().apply {
                        cornerRadius = dp(18).toFloat()
                        setColor(ColorUtils.setAlphaComponent(c, 0x14))
                        setStroke(dp(2), c)
                    }
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        .apply { bottomMargin = dp(14) }
                    isClickable = true
                    isFocusable = true
                    // Clicking card opens dedicated detail screen for this tense
                    setOnClickListener {
                        val bundle = Bundle().apply {
                            putString("tenseName", tn.name)
                            putString("groupName", g.title)
                        }
                        findNavController().navigate(R.id.tenseDetailFragment, bundle)
                    }
                }
                
                // Name Row with Tense Title and Arrow/Detail indicator
                val nameRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                nameRow.addView(TextView(ctx).apply {
                    text = tn.name; setTextColor(fg); textSize = 17f; setTypeface(typeface, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                })
                nameRow.addView(TextView(ctx).apply {
                    text = tn.ur
                    setTextColor(c)
                    textSize = 15f
                    setTypeface(typeface, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        marginEnd = dp(8)
                    }
                })
                nameRow.addView(TextView(ctx).apply {
                    text = "➔"
                    setTextColor(c)
                    textSize = 15f
                    setTypeface(typeface, Typeface.BOLD)
                })
                card.addView(nameRow)

                // Formula Badge
                card.addView(TextView(ctx).apply {
                    text = tn.formula; setTextColor(fg); textSize = 13f; setTypeface(typeface, Typeface.BOLD)
                    setPadding(dp(10), dp(6), dp(10), dp(6))
                    background = GradientDrawable().apply { cornerRadius = dp(8).toFloat(); setColor(ColorUtils.setAlphaComponent(c, 0x20)) }
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8); bottomMargin = dp(8) }
                })

                // Preview first 2 examples with interactive individual audio playback
                tn.examples.take(2).forEach { ex ->
                    val exRow = LinearLayout(ctx).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            topMargin = dp(6)
                        }
                    }

                    val textBlock = LinearLayout(ctx).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    textBlock.addView(TextView(ctx).apply {
                        text = "• ${ex.en}"; setTextColor(fg); textSize = 14f; setTypeface(typeface, Typeface.BOLD)
                    })
                    textBlock.addView(TextView(ctx).apply {
                        text = ex.ur; setTextColor(muted); textSize = 13f
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { leftMargin = dp(12) }
                    })
                    exRow.addView(textBlock)

                    val audioBtn = TextView(ctx).apply {
                        text = "🔊"
                        textSize = 15f
                        setPadding(dp(6), dp(6), dp(6), dp(6))
                        isClickable = true
                        isFocusable = true
                        setOnClickListener { speak(ex.en) }
                    }
                    exRow.addView(audioBtn)

                    card.addView(exRow)
                }

                // "View all examples" hint
                card.addView(TextView(ctx).apply {
                    text = "Tap to practice & listen to all sentences →"
                    setTextColor(c)
                    textSize = 12f
                    setTypeface(typeface, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = dp(8)
                    }
                })

                binding.container.addView(card)
            }
        }
    }

    private fun speak(t: String) {
        if (ttsReady) {
            tts?.stop()
            tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "tense_sentence")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown(); tts = null
        _binding = null
    }
}
