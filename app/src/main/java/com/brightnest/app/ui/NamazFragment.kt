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
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentNamazBinding
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import com.brightnest.app.BrightNestApp
import kotlinx.coroutines.launch

class NamazFragment : Fragment() {

    private var _binding: FragmentNamazBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private data class Breakdown(val label: String, val value: String)
    private data class Prayer(
        val name: String, val arabic: String, val urdu: String, val time: String, val timeUrdu: String,
        val icon: String, val about: String, val aboutUrdu: String, val breakdown: List<Breakdown>
    )
    private data class Step(val step: String, val stepUrdu: String, val desc: String, val descUrdu: String)

    private val palette = listOf("#F97316", "#EAB308", "#84CC16", "#A855F7", "#3B82F6", "#10B981")

    private val prayers = listOf(
        Prayer("Fajr", "الفجر", "فجر کی نماز", "Before sunrise (Dawn)", "طلوعِ آفتاب سے پہلے", "🌅",
            "Fajr is the first prayer of the day. It is offered before sunrise when the sky begins to lighten.",
            "فجر دن کی پہلی نماز ہے۔ یہ طلوعِ آفتاب سے پہلے ادا کی جاتی ہے جب آسمان روشن ہونے لگتا ہے۔",
            listOf(Breakdown("Sunnah", "2 Rakats"), Breakdown("Farz", "2 Rakats"))),
        Prayer("Dhuhr", "الظهر", "ظہر کی نماز", "After midday (Noon)", "زوالِ آفتاب کے بعد", "☀️",
            "Dhuhr is the noon prayer, offered after the sun has passed its highest point.",
            "ظہر دوپہر کی نماز ہے، جو سورج کے بلندی سے ڈھلنے کے بعد ادا کی جاتی ہے۔",
            listOf(Breakdown("Sunnah (before)", "4 Rakats"), Breakdown("Farz", "4 Rakats"), Breakdown("Sunnah (after)", "2 Rakats"), Breakdown("Nafl", "2 Rakats"))),
        Prayer("Asr", "العصر", "عصر کی نماز", "Late afternoon", "سہ پہر کے وقت", "⛅",
            "Asr is the afternoon prayer, offered when the shadow of an object becomes equal to its length.",
            "عصر سہ پہر کی نماز ہے، جو اس وقت ادا کی جاتی ہے جب کسی چیز کا سایہ اس کے برابر ہو جائے۔",
            listOf(Breakdown("Sunnah", "4 Rakats"), Breakdown("Farz", "4 Rakats"))),
        Prayer("Maghrib", "المغرب", "مغرب کی نماز", "Right after sunset", "غروبِ آفتاب کے فوراً بعد", "🌇",
            "Maghrib is the evening prayer, offered just after the sun sets below the horizon.",
            "مغرب شام کی نماز ہے، جو سورج کے غروب ہونے کے فوراً بعد ادا کی جاتی ہے۔",
            listOf(Breakdown("Farz", "3 Rakats"), Breakdown("Sunnah", "2 Rakats"), Breakdown("Nafl", "2 Rakats"))),
        Prayer("Isha", "العشاء", "عشاء کی نماز", "At night", "رات کے وقت", "🌙",
            "Isha is the night prayer, the last prayer of the day, offered after the twilight has disappeared.",
            "عشاء رات کی نماز ہے، یہ دن کی آخری نماز ہے، جو شفق کے غائب ہونے کے بعد ادا کی جاتی ہے۔",
            listOf(Breakdown("Sunnah (before)", "4 Rakats"), Breakdown("Farz", "4 Rakats"), Breakdown("Sunnah (after)", "2 Rakats"), Breakdown("Nafl", "2 Rakats"), Breakdown("Witr", "3 Rakats"), Breakdown("Nafl", "2 Rakats"))),
        Prayer("Jummah", "الجمعة", "جمعہ کی نماز", "Friday, at Dhuhr time", "جمعہ کے دن، ظہر کے وقت", "🕌",
            "Jummah is the special Friday congregational prayer that replaces Dhuhr. It includes a Khutbah (sermon) followed by 2 Farz Rakats prayed in jamaat.",
            "جمعہ جمعہ کے دن کی خاص با جماعت نماز ہے جو ظہر کی جگہ پڑھی جاتی ہے۔ اس میں خطبہ ہوتا ہے، پھر 2 فرض رکعتیں جماعت کے ساتھ ادا کی جاتی ہیں۔",
            listOf(Breakdown("Sunnah (before)", "4 Rakats"), Breakdown("Khutbah", "Sermon"), Breakdown("Farz (with Imam)", "2 Rakats"), Breakdown("Sunnah (after)", "4 Rakats"), Breakdown("Sunnah", "2 Rakats"), Breakdown("Nafl", "2 Rakats")))
    )

    private val steps = listOf(
        Step("1. Niyyah", "۱۔ نیّت", "Make intention in your heart for the prayer.", "دل میں نماز کی نیّت کریں۔"),
        Step("2. Takbir", "۲۔ تکبیرِ تحریمہ", "Raise hands and say Allahu Akbar.", "دونوں ہاتھ کانوں تک اٹھا کر اللہ اکبر کہیں۔"),
        Step("3. Qiyam", "۳۔ قیام", "Stand and recite Surah Al-Fatiha, then another Surah.", "کھڑے ہو کر سورۃ الفاتحہ پڑھیں، پھر کوئی اور سورت ملائیں۔"),
        Step("4. Ruku", "۴۔ رکوع", "Bow down and say Subhana Rabbiyal Azeem three times.", "جھک کر تین بار سبحان ربی العظیم کہیں۔"),
        Step("5. Qauma", "۵۔ قومہ", "Stand up straight and say Sami Allahu liman hamidah, Rabbana lakal hamd.", "سیدھے کھڑے ہو کر سمع اللہ لمن حمدہ، ربنا لک الحمد کہیں۔"),
        Step("6. Sujood", "۶۔ سجدہ", "Prostrate and say Subhana Rabbiyal A'la three times. Do two sujood in each rakat.", "سجدے میں جا کر تین بار سبحان ربی الاعلیٰ کہیں۔ ہر رکعت میں دو سجدے کریں۔"),
        Step("7. Tashahhud", "۷۔ تشہد", "Sit and recite At-tahiyyatu lillahi...", "بیٹھ کر التحیات للہ پڑھیں۔"),
        Step("8. Durood & Dua", "۸۔ درود و دعا", "Recite Durood Ibrahim and then a Dua.", "درود ابراہیمی پڑھیں اور پھر دعا مانگیں۔"),
        Step("9. Salaam", "۹۔ سلام", "Turn head right then left saying Assalamu Alaykum wa Rahmatullah.", "پہلے دائیں طرف، پھر بائیں طرف منہ پھیر کر السلام علیکم ورحمۃ اللہ کہیں۔")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNamazBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        tts = TextToSpeech(requireContext().applicationContext) { if (it == TextToSpeech.SUCCESS) { ttsReady = true; tts?.language = Locale.US; tts?.setSpeechRate(0.85f) } }

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.detailClose.setOnClickListener { hideDetail() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.detailRoot.visibility == View.VISIBLE) hideDetail()
                else { isEnabled = false; findNavController().navigateUp() }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            val cachedPrayers = try { db.namazPrayerDao().all() } catch (e: Exception) { emptyList() }
            val cachedSteps = try { db.namazStepDao().all() } catch (e: Exception) { emptyList() }

            val pList = if (cachedPrayers.isNotEmpty()) {
                cachedPrayers.map { pe ->
                    val bList = try {
                        val trimmed = pe.breakdownJson.trim().removePrefix("[").removeSuffix("]")
                        if (trimmed.isEmpty()) emptyList()
                        else {
                            trimmed.split("},{").map { obj ->
                                val clean = obj.trim('{', '}')
                                val parts = clean.split(",")
                                var label = ""
                                var valStr = ""
                                parts.forEach { part ->
                                    val kv = part.split(":")
                                    if (kv.size == 2) {
                                        if (kv[0].contains("label")) label = kv[1].trim('"', ' ')
                                        if (kv[0].contains("value")) valStr = kv[1].trim('"', ' ')
                                    }
                                }
                                Breakdown(label, valStr)
                            }
                        }
                    } catch (e: Exception) { emptyList() }
                    Prayer(pe.name, pe.arabic, pe.urdu, pe.time, pe.timeUrdu, pe.icon, pe.about, pe.aboutUrdu, bList)
                }
            } else prayers

            val sList = if (cachedSteps.isNotEmpty()) {
                cachedSteps.map { se -> Step(se.step, se.stepUrdu, se.desc, se.descUrdu) }
            } else steps

            if (_binding != null) buildList(pList, sList)
        }
    }

    private fun buildList(pList: List<Prayer> = prayers, sList: List<Step> = steps) {
        if (_binding == null) return
        binding.container.removeAllViews()
        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        binding.container.addView(TextView(ctx).apply {
            text = "5 Daily Prayers"; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) }
        })

        pList.forEachIndexed { i, p ->
            val c = Color.parseColor(palette[i % palette.size])
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
                background = GradientDrawable().apply {
                    cornerRadius = dp(16).toFloat()
                    setColor(ColorUtils.setAlphaComponent(c, 0x20))
                    setStroke(dp(2), c)
                }
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) }
                setOnClickListener { openPrayer(p, c) }
            }
            card.addView(TextView(ctx).apply {
                text = p.icon; textSize = 36f
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(16) }
            })
            val col = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            col.addView(TextView(ctx).apply { text = p.name; setTextColor(c); textSize = 18f; setTypeface(typeface, Typeface.BOLD) })
            col.addView(TextView(ctx).apply { text = p.urdu; setTextColor(fg); textSize = 14f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) } })
            col.addView(TextView(ctx).apply { text = p.time; setTextColor(muted); textSize = 12f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) } })
            card.addView(col)
            card.addView(TextView(ctx).apply { text = "›"; setTextColor(c); textSize = 26f })
            binding.container.addView(card)
        }

        binding.container.addView(TextView(ctx).apply {
            text = "How to Pray"; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16); bottomMargin = dp(12) }
        })

        sList.forEachIndexed { i, s ->
            val c = Color.parseColor(palette[i % palette.size])
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
                background = GradientDrawable().apply {
                    cornerRadius = dp(16).toFloat()
                    setColor(ColorUtils.setAlphaComponent(c, 0x20))
                    setStroke(dp(2), c)
                }
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) }
                setOnClickListener { 
                    if (ttsReady) {
                        val langCode = com.brightnest.app.Prefs(requireContext()).language.lowercase()
                        com.brightnest.app.AppLanguageHelper.configureTts(tts, langCode)
                        val textToSpeak = if (langCode == "ur") "${s.stepUrdu}۔ ${s.descUrdu}" else "${s.step}. ${s.desc}"
                        tts?.stop()
                        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "bn")
                    }
                }
            }
            val head = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            head.addView(TextView(ctx).apply {
                text = s.step; setTextColor(c); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            head.addView(TextView(ctx).apply { text = s.stepUrdu; setTextColor(c); textSize = 16f; setTypeface(typeface, Typeface.BOLD) })
            card.addView(head)
            card.addView(TextView(ctx).apply { text = s.desc; setTextColor(fg); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) } })
            card.addView(TextView(ctx).apply { text = s.descUrdu; setTextColor(fg); textSize = 15f; gravity = Gravity.END; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) } })
            binding.container.addView(card)
        }
    }

    private fun openPrayer(p: Prayer, c: Int) {
        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val white80 = ColorUtils.setAlphaComponent(Color.WHITE, 0x80)

        binding.listRoot.visibility = View.GONE
        binding.detailRoot.setBackgroundColor(ContextCompat.getColor(ctx, R.color.background))
        binding.detailTitle.text = p.name
        binding.detailTitle.setTextColor(c)

        val box = binding.detailContainer
        box.removeAllViews()

        box.addView(TextView(ctx).apply { text = p.icon; textSize = 96f })
        box.addView(TextView(ctx).apply {
            text = p.arabic; setTextColor(c); textSize = 48f; setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) }
        })
        box.addView(TextView(ctx).apply {
            text = p.urdu; setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) }
        })
        box.addView(TextView(ctx).apply {
            text = p.time; setTextColor(muted); textSize = 16f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) }
        })
        box.addView(TextView(ctx).apply {
            text = p.timeUrdu; setTextColor(muted); textSize = 15f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) }
        })

        val aboutCard = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = GradientDrawable().apply { cornerRadius = dp(16).toFloat(); setColor(white80) }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(24) }
        }
        aboutCard.addView(TextView(ctx).apply { text = p.about; setTextColor(fg); gravity = Gravity.CENTER; textSize = 15f })
        aboutCard.addView(View(ctx).apply {
            setBackgroundColor(ColorUtils.setAlphaComponent(c, 0x40))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)).apply { topMargin = dp(12); bottomMargin = dp(12) }
        })
        aboutCard.addView(TextView(ctx).apply { text = p.aboutUrdu; setTextColor(fg); gravity = Gravity.CENTER; textSize = 16f })
        box.addView(aboutCard)

        box.addView(TextView(ctx).apply {
            text = "Rakat Breakdown"; setTextColor(c); textSize = 18f; setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(28) }
        })

        p.breakdown.forEach { b ->
            val r = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(14), dp(14), dp(14), dp(14))
                background = GradientDrawable().apply { cornerRadius = dp(12).toFloat(); setColor(white80); setStroke(dp(1), c) }
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) }
            }
            r.addView(TextView(ctx).apply { text = b.label; setTextColor(fg); setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
            r.addView(TextView(ctx).apply { text = b.value; setTextColor(c); setTypeface(typeface, Typeface.BOLD) })
            box.addView(r)
        }
        val total = p.breakdown.sumOf { it.value.takeWhile { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }
        val totalRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = GradientDrawable().apply { cornerRadius = dp(12).toFloat(); setColor(c) }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) }
        }
        totalRow.addView(TextView(ctx).apply { text = "Total Rakats"; setTextColor(Color.WHITE); setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        totalRow.addView(TextView(ctx).apply { text = total.toString(); setTextColor(Color.WHITE); setTypeface(typeface, Typeface.BOLD) })
        box.addView(totalRow)

        val listen = TextView(ctx).apply {
            text = "🔊  Listen"; setTextColor(Color.WHITE); setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(dp(32), dp(14), dp(32), dp(14))
            background = GradientDrawable().apply { cornerRadius = dp(28).toFloat(); setColor(c) }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(28) }
            setOnClickListener { 
                if (ttsReady) {
                    val langCode = com.brightnest.app.Prefs(requireContext()).language.lowercase()
                    com.brightnest.app.AppLanguageHelper.configureTts(tts, langCode)
                    val textToSpeak = if (langCode == "ur") "${p.urdu}۔ ${p.aboutUrdu}" else "${p.name}. ${p.about}"
                    tts?.stop()
                    tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "bn")
                }
            }
        }
        box.addView(listen)
        binding.detailRoot.visibility = View.VISIBLE
        binding.detailContainer.scrollTo(0, 0)
    }

    private fun hideDetail() {
        tts?.stop()
        binding.detailRoot.visibility = View.GONE
        binding.listRoot.visibility = View.VISIBLE
    }

    private fun speak(t: String) { if (ttsReady) { tts?.stop(); tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "bn") } }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown(); tts = null
        _binding = null
    }
}
