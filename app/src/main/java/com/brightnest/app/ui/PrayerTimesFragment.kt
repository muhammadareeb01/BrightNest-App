package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.azan.AzanScheduler
import com.brightnest.app.databinding.FragmentPrayerTimesBinding
import java.util.Date
import java.util.Locale

class PrayerTimesFragment : Fragment() {

    private var _binding: FragmentPrayerTimesBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs

    private data class Meta(val key: String, val en: String, val ar: String, val ur: String, val icon: String, val color: String)

    private val metas = listOf(
        Meta("fajr", "Fajr", "الفجر", "فجر", "🌅", "#7C9CC4"),
        Meta("dhuhr", "Dhuhr", "الظهر", "ظہر", "☀️", "#F4B860"),
        Meta("asr", "Asr", "العصر", "عصر", "⛅", "#E89C5C"),
        Meta("maghrib", "Maghrib", "المغرب", "مغرب", "🌇", "#D87C5A"),
        Meta("isha", "Isha", "العشاء", "عشاء", "🌙", "#6A6CA8")
    )

    private var editing: String? = null

    private fun getTime(key: String): String = when (key) {
        "fajr" -> prefs.prayerFajr
        "dhuhr" -> prefs.prayerDhuhr
        "asr" -> prefs.prayerAsr
        "maghrib" -> prefs.prayerMaghrib
        else -> prefs.prayerIsha
    }

    private fun setTime(key: String, value: String) { when (key) {
        "fajr" -> prefs.prayerFajr = value
        "dhuhr" -> prefs.prayerDhuhr = value
        "asr" -> prefs.prayerAsr = value
        "maghrib" -> prefs.prayerMaghrib = value
        else -> prefs.prayerIsha = value
    } }

    private fun toMinutes(t: String): Int? {
        val m = Regex("^(\\d{1,2}):(\\d{2})\\s*(AM|PM)?$", RegexOption.IGNORE_CASE).find(t.trim()) ?: return null
        var h = m.groupValues[1].toIntOrNull() ?: return null
        val min = m.groupValues[2].toIntOrNull() ?: return null
        val period = m.groupValues[3].uppercase(Locale.US)
        if (period == "PM" && h != 12) h += 12
        if (period == "AM" && h == 12) h = 0
        return h * 60 + min
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPrayerTimesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        prefs = Prefs(requireContext())
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        rebuild()

        // Initially schedule alarms just in case
        AzanScheduler.scheduleAlarms(requireContext())
    }

    private fun rebuild() {
        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val onPrimary = ContextCompat.getColor(ctx, R.color.on_primary)
        val cardColor = ContextCompat.getColor(ctx, R.color.card)

        binding.container.removeAllViews()

        val now = Date()
        val cal = java.util.Calendar.getInstance().apply { time = now }
        val nowMin = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
        val today = java.text.SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now)

        // next prayer
        var nextKey: String? = null
        var nextMins = Int.MAX_VALUE
        for (m in metas) {
            val t = toMinutes(getTime(m.key)) ?: continue
            if (t > nowMin && t < nextMins) { nextMins = t; nextKey = m.key }
        }
        if (nextKey == null) {
            val first = metas.firstOrNull { toMinutes(getTime(it.key)) != null }
            if (first != null) { nextKey = first.key; nextMins = (toMinutes(getTime(first.key)) ?: 0) + 24 * 60 }
        }
        val minsUntil = if (nextKey != null) maxOf(0, nextMins - nowMin) else 0
        val hUntil = minsUntil / 60
        val mUntil = minsUntil % 60

        // hero card
        val hero = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = GradientDrawable().apply { cornerRadius = dp(18).toFloat(); setColor(primary) }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        hero.addView(TextView(ctx).apply { text = today; setTextColor(onPrimary); textSize = 12f; alpha = 0.85f })
        hero.addView(TextView(ctx).apply {
            text = "Next Prayer • اگلی نماز"; setTextColor(onPrimary); textSize = 13f; alpha = 0.85f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) }
        })
        val nextMeta = metas.firstOrNull { it.key == nextKey }
        if (nextMeta != null) {
            hero.addView(TextView(ctx).apply {
                text = "${nextMeta.en}  ${nextMeta.ar}"; setTextColor(onPrimary); textSize = 30f; setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) }
            })
            val until = (if (hUntil > 0) "${hUntil}h " else "") + "${mUntil}m"
            hero.addView(TextView(ctx).apply {
                text = "${getTime(nextMeta.key)} • in $until"; setTextColor(onPrimary); textSize = 16f; alpha = 0.95f
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) }
            })
        } else {
            hero.addView(TextView(ctx).apply {
                text = "Set your times below"; setTextColor(onPrimary)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) }
            })
        }
        binding.container.addView(hero)

        binding.container.addView(TextView(ctx).apply {
            text = "TODAY'S SCHEDULE • آج کا شیڈول"; setTextColor(muted); textSize = 12f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(20); bottomMargin = dp(8); marginStart = dp(4) }
        })

        metas.forEach { meta ->
            val isNext = nextKey == meta.key
            val time = getTime(meta.key)
            val tMins = toMinutes(time)
            val past = tMins != null && tMins < nowMin && !isNext
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(14), dp(14), dp(14), dp(14))
                background = GradientDrawable().apply {
                    cornerRadius = dp(14).toFloat(); setColor(cardColor)
                    if (isNext) setStroke(dp(2), primary)
                }
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(10) }
            }
            row.addView(TextView(ctx).apply {
                text = meta.icon; textSize = 22f; gravity = Gravity.CENTER
                background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor(meta.color)) }
                layoutParams = LinearLayout.LayoutParams(dp(42), dp(42))
            })
            val col = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = dp(12) }
            }
            val nameRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            nameRow.addView(TextView(ctx).apply { text = meta.en; setTextColor(fg); setTypeface(typeface, Typeface.BOLD); if (past) alpha = 0.55f })
            nameRow.addView(TextView(ctx).apply { text = meta.ar; setTextColor(muted); textSize = 15f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = dp(8) } })
            if (isNext) {
                nameRow.addView(TextView(ctx).apply {
                    text = "NEXT"; setTextColor(onPrimary); textSize = 10f; setTypeface(typeface, Typeface.BOLD)
                    setPadding(dp(6), dp(2), dp(6), dp(2))
                    background = GradientDrawable().apply { cornerRadius = dp(6).toFloat(); setColor(primary) }
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = dp(8) }
                })
            }
            col.addView(nameRow)
            col.addView(TextView(ctx).apply { text = meta.ur; setTextColor(muted); textSize = 12f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) } })
            row.addView(col)

            if (editing == meta.key) {
                val input = EditText(ctx).apply {
                    setText(time); setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
                    gravity = Gravity.CENTER; hint = "5:30 AM"; setHintTextColor(muted)
                    inputType = InputType.TYPE_CLASS_TEXT
                    setPadding(dp(10), dp(6), dp(10), dp(6))
                    background = GradientDrawable().apply { cornerRadius = dp(8).toFloat(); setStroke(dp(2), primary) }
                    layoutParams = LinearLayout.LayoutParams(dp(92), LinearLayout.LayoutParams.WRAP_CONTENT)
                }
                val check = TextView(ctx).apply {
                    text = "✓"; setTextColor(primary); textSize = 22f
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = dp(6) }
                    setOnClickListener {
                        val draft = input.text.toString()
                        if (toMinutes(draft) != null) {
                            setTime(meta.key, draft)
                            // Reschedule alarms when time changes
                            AzanScheduler.scheduleAlarms(requireContext())
                        }
                        editing = null; rebuild()
                    }
                }
                row.addView(input); row.addView(check)
                input.requestFocus()
            } else {
                val timeRow = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                    setOnClickListener { editing = meta.key; rebuild() }
                }
                timeRow.addView(TextView(ctx).apply { text = time; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD); if (past) alpha = 0.55f })
                timeRow.addView(TextView(ctx).apply { text = "✎"; setTextColor(muted); textSize = 14f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = dp(6) } })
                row.addView(timeRow)
            }
            binding.container.addView(row)
        }

        val tip = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = GradientDrawable().apply { cornerRadius = dp(14).toFloat(); setColor(cardColor) }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(18) }
        }
        tip.addView(TextView(ctx).apply { text = "ℹ  Tip • نصیحت"; setTextColor(primary); setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(6) } })
        tip.addView(TextView(ctx).apply { text = "Tap any time to edit. Use the format like \"5:30 AM\" or \"13:45\"."; setTextColor(muted); textSize = 12f })
        tip.addView(TextView(ctx).apply { text = "کسی بھی وقت کو ترمیم کرنے کے لیے اس پر ٹیپ کریں۔"; setTextColor(muted); textSize = 12f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) } })
        binding.container.addView(tip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
