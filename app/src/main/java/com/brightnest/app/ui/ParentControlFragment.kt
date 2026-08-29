package com.brightnest.app.ui

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding

class ParentControlFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs

    private var storedPin = ""
    private var unlocked = false
    private var entryPin = ""
    private var entryError = false
    private var pin = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        prefs = Prefs(requireContext())
        storedPin = prefs.parentPin
        unlocked = storedPin.isEmpty()
        binding.title.text = "Parent Panel"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnAction.text = ""
        render()
    }

    private fun render() {
        binding.toolRoot.removeAllViews()
        if (!unlocked) buildGate() else buildPanel()
    }

    // ---- Unlock gate ----
    private fun buildGate() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)

        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        col.addView(TextView(ctx).apply {
            text = "🔒"; textSize = 32f; gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(primary, 0x15), GameUi.dpf(ctx, 32))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 64), GameUi.dp(ctx, 64)).apply { bottomMargin = GameUi.dp(ctx, 16) }
        })
        col.addView(TextView(ctx).apply {
            text = "Enter Parent PIN"; setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 8) }
        })
        col.addView(TextView(ctx).apply {
            text = if (entryError) "Incorrect PIN. Try again." else "Enter your 4-digit PIN to continue."
            setTextColor(if (entryError) destructive else muted); textSize = 16f; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 32) }
        })
        col.addView(numPad(entryPin, { handleEntryKey(it) }, { if (entryPin.isNotEmpty()) { entryPin = entryPin.dropLast(1); render() } }))
        binding.toolRoot.addView(col)
    }

    private fun handleEntryKey(num: String) {
        if (entryPin.length >= 4) return
        entryPin += num
        if (entryPin.length == 4) {
            if (entryPin == storedPin) {
                unlocked = true; entryPin = ""; entryError = false
            } else {
                entryError = true
                Handler(Looper.getMainLooper()).postDelayed({
                    if (_binding != null) { entryPin = ""; render() }
                }, 400)
            }
        }
        render()
    }

    // ---- Panel ----
    private fun buildPanel() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedC = ContextCompat.getColor(ctx, R.color.muted)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val coral = ContextCompat.getColor(ctx, R.color.coral)
        val sage = ContextCompat.getColor(ctx, R.color.sage)
        val lavender = ContextCompat.getColor(ctx, R.color.lavender)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)

        val scroll = ScrollView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 48))
        }

        content.addView(sectionLabel(ctx, "Kids Activity", fg, GameUi.dp(ctx, 12), 0))

        data class Stat(val icon: String, val label: String, val value: Int, val color: Int)
        val stats = listOf(
            Stat("🪙", "Coins", prefs.coins, primary),
            Stat("⭐", "Stars", prefs.stars, coral),
            Stat("⚡", "Streak", prefs.streak, sage),
            Stat("🏅", "Badges", prefs.badges.size, lavender)
        )
        var row: LinearLayout? = null
        stats.forEachIndexed { idx, s ->
            if (idx % 2 == 0) {
                row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) }
                }
                content.addView(row)
            }
            val statCard = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
                background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    if (idx % 2 == 0) marginEnd = GameUi.dp(ctx, 6) else marginStart = GameUi.dp(ctx, 6)
                }
            }
            statCard.addView(TextView(ctx).apply {
                text = s.icon; textSize = 18f; gravity = Gravity.CENTER
                background = GameUi.rounded(GameUi.withAlpha(s.color, 0x20), GameUi.dpf(ctx, 20))
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40)).apply { bottomMargin = GameUi.dp(ctx, 8) }
            })
            statCard.addView(TextView(ctx).apply { text = s.value.toString(); setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD) })
            statCard.addView(TextView(ctx).apply { text = s.label; setTextColor(muted); textSize = 13f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 4) } })
            row?.addView(statCard)
        }

        content.addView(sectionLabel(ctx, "Restrictions", fg, GameUi.dp(ctx, 12), GameUi.dp(ctx, 24)))

        val restrictCard = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        restrictCard.addView(restrictionRow(ctx, "🔒", "Lock Kids Mode Exit", "Require PIN to switch back to Adult mode", prefs.lockExit, true, border) { v -> prefs.lockExit = v })
        restrictCard.addView(View(ctx).apply { setBackgroundColor(border); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 1)) })
        restrictCard.addView(restrictionRow(ctx, "⚙️", "Lock Profile Settings", "Require PIN to edit profile or subscription", prefs.lockSettings, false, border) { v -> prefs.lockSettings = v })
        content.addView(restrictCard)

        content.addView(sectionLabel(ctx, if (storedPin.isNotEmpty()) "Change PIN" else "Set Parent PIN", fg, GameUi.dp(ctx, 4), GameUi.dp(ctx, 24)))
        content.addView(TextView(ctx).apply {
            text = if (storedPin.isNotEmpty()) "Enter a new 4-digit PIN to change it." else "Enter a 4-digit PIN to lock kids features."
            setTextColor(muted); textSize = 13f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 20) }
        })

        val pinSection = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        pinSection.addView(numPad(pin, { handleKeyPress(it) }, { if (pin.isNotEmpty()) { pin = pin.dropLast(1); render() } }))
        if (storedPin.isNotEmpty()) {
            pinSection.addView(TextView(ctx).apply {
                text = "Clear PIN"; setTextColor(destructive); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
                setPadding(GameUi.dp(ctx, 12), GameUi.dp(ctx, 12), GameUi.dp(ctx, 12), GameUi.dp(ctx, 12))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 24) }
                setOnClickListener { prefs.parentPin = ""; storedPin = ""; pin = ""; render() }
            })
        }
        content.addView(pinSection)

        scroll.addView(content)
        binding.toolRoot.addView(scroll)
    }

    private fun handleKeyPress(num: String) {
        if (pin.length < 4) {
            pin += num
            if (pin.length == 4) {
                prefs.parentPin = pin
                storedPin = pin
                pin = ""
            }
            render()
        }
    }

    private fun sectionLabel(ctx: android.content.Context, text: String, fg: Int, bottom: Int, top: Int): TextView =
        TextView(ctx).apply {
            this.text = text; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = top; bottomMargin = bottom }
        }

    private fun restrictionRow(
        ctx: android.content.Context, icon: String, title: String, sub: String,
        value: Boolean, ignored: Boolean, border: Int, onChange: (Boolean) -> Unit
    ): View {
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedC = ContextCompat.getColor(ctx, R.color.muted)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        row.addView(TextView(ctx).apply { text = icon; textSize = 18f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = GameUi.dp(ctx, 12) } })
        val textCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = GameUi.dp(ctx, 16) }
        }
        textCol.addView(TextView(ctx).apply { text = title; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD) })
        textCol.addView(TextView(ctx).apply { text = sub; setTextColor(muted); textSize = 13f })
        row.addView(textCol)
        row.addView(SwitchCompat(ctx).apply {
            isChecked = value
            trackTintList = android.content.res.ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(primary, mutedC)
            )
            setOnCheckedChangeListener { _, checked -> onChange(checked) }
        })
        return row
    }

    private fun numPad(value: String, onKey: (String) -> Unit, onBackspace: () -> Unit): View {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val wrap = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        // dots
        val dots = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 32) }
        }
        for (i in 0 until 4) {
            val filled = value.length > i
            dots.addView(View(ctx).apply {
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(if (filled) primary else card)
                    setStroke(GameUi.dp(ctx, 1), if (filled) primary else border)
                }
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20)).apply { if (i > 0) marginStart = GameUi.dp(ctx, 16) }
            })
        }
        wrap.addView(dots)

        val keypad = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 280), LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        fun keyView(label: String, onClick: (() -> Unit)?): TextView = TextView(ctx).apply {
            text = label; textSize = 24f; setTextColor(fg); gravity = Gravity.CENTER
            background = GameUi.rounded(card, GameUi.dpf(ctx, 32), border, GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 64), 1f)
            if (onClick != null) setOnClickListener { onClick() }
        }
        fun spacer(): View = View(ctx).apply { layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 64), 1f) }
        fun makeRow(builder: (LinearLayout) -> Unit): LinearLayout = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) }
            builder(this)
        }
        val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"))
        rows.forEach { r ->
            keypad.addView(makeRow { rowLayout ->
                r.forEachIndexed { i, num ->
                    val k = keyView(num) { onKey(num) }
                    (k.layoutParams as LinearLayout.LayoutParams).apply { if (i > 0) marginStart = GameUi.dp(ctx, 12) }
                    rowLayout.addView(k)
                }
            })
        }
        keypad.addView(makeRow { rowLayout ->
            rowLayout.addView(spacer())
            val zero = keyView("0") { onKey("0") }
            (zero.layoutParams as LinearLayout.LayoutParams).marginStart = GameUi.dp(ctx, 12)
            rowLayout.addView(zero)
            val del = keyView("⌫") { onBackspace() }
            (del.layoutParams as LinearLayout.LayoutParams).marginStart = GameUi.dp(ctx, 12)
            rowLayout.addView(del)
        })
        wrap.addView(keypad)
        return wrap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
