package com.brightnest.app.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.BrightNestApp
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs

    private var langPickerOpen = false

    private var legalOverlay: View? = null
    private lateinit var backCallback: OnBackPressedCallback

    private var fg = 0
    private var muted = 0
    private var primary = 0
    private var border = 0
    private var card = 0
    private var bg = 0

    private val languages = listOf(
        "en" to ("English" to "English"),
        "ur" to ("اردو" to "Urdu"),
        "ar" to ("العربية" to "Arabic"),
        "hi" to ("हिन्दी" to "Hindi"),
        "fr" to ("Français" to "French"),
        "de" to ("Deutsch" to "German"),
        "es" to ("Español" to "Spanish"),
        "zh" to ("中文" to "Chinese"),
        "ja" to ("日本語" to "Japanese"),
        "ko" to ("한국어" to "Korean"),
        "tr" to ("Türkçe" to "Turkish"),
        "ru" to ("Русский" to "Russian"),
        "it" to ("Italiano" to "Italian"),
        "pt" to ("Português" to "Portuguese"),
        "bn" to ("বাংলা" to "Bengali")
    )

    private val privacyText = """BrightNest Privacy Policy

Your privacy matters to us. BrightNest stores all your data locally on your device using secure on-device storage. We do not collect, transmit, or share your personal information with third parties.

What we store on your device:
• Your name, avatar, and account preferences
• Your progress, streaks, coins, and stars
• Your notes, tasks, habits, and goals
• Your language and theme preferences

What we never collect:
• We do not use trackers or analytics
• We do not send your data to external servers
• We do not sell your information

You can clear your data anytime by deleting and reinstalling the app, or by signing out."""

    private val termsText = """BrightNest Terms of Service

By using BrightNest, you agree to the following terms:

1. BrightNest is a personal learning and lifestyle companion intended for Muslim families. Use it in accordance with your local laws and Islamic ethics.

2. All Islamic content (prayers, names of Allah, Quran references, duas) is provided for educational purposes. Always consult qualified scholars for religious guidance.

3. Content shown in BrightNest is for personal use only. Do not redistribute screenshots or content commercially without permission.

4. The app is provided "as is" without warranty. Prayer times and Hijri dates are approximate — verify with your local masjid.

5. Parental supervision is required for children using the Kids mode. Use Parent Controls to manage access.

6. We reserve the right to update these terms; continued use after updates constitutes acceptance.

Thank you for using BrightNest. May Allah accept your efforts."""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()
        prefs = Prefs(ctx)
        fg = ContextCompat.getColor(ctx, R.color.foreground)
        muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        primary = ContextCompat.getColor(ctx, R.color.primary)
        border = ContextCompat.getColor(ctx, R.color.border)
        card = ContextCompat.getColor(ctx, R.color.card)
        bg = ContextCompat.getColor(ctx, R.color.background)
        binding.settingsRoot.setBackgroundColor(ContextCompat.getColor(ctx, R.color.background))

        backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() { dismissLegal() }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        render()
    }

    private fun render() {
        val ctx = requireContext()
        binding.settingsRoot.removeAllViews()

        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }

        // header
        val header = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        header.addView(TextView(ctx).apply {
            text = "←"; setTextColor(fg); textSize = 22f; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
            setOnClickListener { if (!findNavController().navigateUp()) findNavController().popBackStack() }
        })
        header.addView(TextView(ctx).apply {
            text = "Settings"; setTextColor(fg); textSize = 20f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)) })
        val headerWrap = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            addView(header)
            addView(View(ctx).apply { setBackgroundColor(border); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)) })
        }
        col.addView(headerWrap)

        val scroll = ScrollView(ctx).apply {
            isVerticalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(48))
        }

        // Preferences section
        content.addView(sectionLabel(ctx, "PREFERENCES"))
        val prefCard = cardContainer(ctx)
        prefCard.addView(switchRow(ctx, "🌙", "Dark Mode", prefs.darkMode) { v ->
            prefs.darkMode = v
            BrightNestApp.applyNightMode(v)
            requireActivity().recreate()
        })
        prefCard.addView(divider(ctx))
        prefCard.addView(switchRow(ctx, "🔔", "Notifications", prefs.notifications) { v -> prefs.notifications = v })
        prefCard.addView(divider(ctx))
        prefCard.addView(languageRow(ctx))
        if (langPickerOpen) prefCard.addView(languageList(ctx))
        content.addView(prefCard)
        content.addView(sectionSpacer(ctx))

        // Reminders section
        content.addView(sectionLabel(ctx, "REMINDERS"))
        val remCard = cardContainer(ctx)
        remCard.addView(navRow(ctx, "💧", "Water Reminders") { findNavController().navigate(R.id.waterFragment) })
        remCard.addView(divider(ctx))
        remCard.addView(navRow(ctx, "🕐", "Prayer Times") { findNavController().navigate(R.id.prayerTimesFragment) })
        content.addView(remCard)
        content.addView(sectionSpacer(ctx))

        // About section
        content.addView(sectionLabel(ctx, "ABOUT"))
        val aboutCard = cardContainer(ctx)
        aboutCard.addView(infoRow(ctx, "Version", "1.0.0"))
        aboutCard.addView(divider(ctx))
        aboutCard.addView(navRow(ctx, null, "Privacy Policy") { showLegal("Privacy Policy", privacyText) })
        aboutCard.addView(divider(ctx))
        aboutCard.addView(navRow(ctx, null, "Terms of Service") { showLegal("Terms of Service", termsText) })
        content.addView(aboutCard)

        scroll.addView(content)
        col.addView(scroll)
        binding.settingsRoot.addView(col)
        com.brightnest.app.AppLanguageHelper.localizeViewTree(binding.settingsRoot, prefs.language)
    }

    private fun sectionLabel(ctx: Context, text: String): View = TextView(ctx).apply {
        this.text = text; setTextColor(muted); textSize = 12f
        setPadding(dp(16), 0, 0, dp(8))
    }

    private fun sectionSpacer(ctx: Context): View = View(ctx).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(24))
    }

    private fun cardContainer(ctx: Context): LinearLayout = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
        background = GameUi.rounded(card, dpf(16), border, dp(1))
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun divider(ctx: Context): View = View(ctx).apply {
        setBackgroundColor(border)
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1))
    }

    private fun rowBase(ctx: Context): LinearLayout = LinearLayout(ctx).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(16))
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun leftLabel(ctx: Context, icon: String?, label: String): View {
        val left = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        if (icon != null) left.addView(TextView(ctx).apply { text = icon; setTextColor(fg); textSize = 18f; setPadding(0, 0, dp(12), 0) })
        left.addView(TextView(ctx).apply { text = label; setTextColor(fg); textSize = 16f })
        return left
    }

    private fun switchRow(ctx: Context, icon: String, label: String, value: Boolean, onChange: (Boolean) -> Unit): View {
        val row = rowBase(ctx)
        row.addView(leftLabel(ctx, icon, label))
        row.addView(SwitchCompat(ctx).apply {
            isChecked = value
            setOnCheckedChangeListener { _, checked -> onChange(checked) }
        })
        return row
    }

    private fun navRow(ctx: Context, icon: String?, label: String, onClick: () -> Unit): View {
        val row = rowBase(ctx)
        row.addView(leftLabel(ctx, icon, label))
        row.addView(TextView(ctx).apply { text = "›"; setTextColor(muted); textSize = 22f })
        row.setOnClickListener { onClick() }
        return row
    }

    private fun infoRow(ctx: Context, label: String, value: String): View {
        val row = rowBase(ctx)
        row.addView(TextView(ctx).apply {
            text = label; setTextColor(fg); textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        row.addView(TextView(ctx).apply { text = value; setTextColor(muted); textSize = 13f })
        return row
    }

    private fun languageRow(ctx: Context): View {
        val row = rowBase(ctx)
        row.addView(leftLabel(ctx, "🌐", "Language"))
        val right = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val current = languages.firstOrNull { it.first == prefs.language } ?: languages[0]
        right.addView(TextView(ctx).apply { text = current.second.first; setTextColor(muted); textSize = 13f; setPadding(0, 0, dp(6), 0) })
        right.addView(TextView(ctx).apply { text = if (langPickerOpen) "⌃" else "⌄"; setTextColor(muted); textSize = 16f })
        row.addView(right)
        row.setOnClickListener { langPickerOpen = !langPickerOpen; render() }
        return row
    }

    private fun languageList(ctx: Context): View {
        val wrap = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(6), 0, dp(6))
        }
        wrap.addView(View(ctx).apply {
            setBackgroundColor(border)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)).apply { bottomMargin = dp(6) }
        })
        wrap.addView(TextView(ctx).apply {
            text = "Choose Language"; setTextColor(muted); textSize = 13f
            setPadding(dp(16), dp(8), dp(16), dp(8))
        })
        languages.forEach { (code, labels) ->
            val selected = code == prefs.language
            val item = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(12), dp(16), dp(12))
                if (selected) setBackgroundColor(GameUi.withAlpha(primary, 0x15))
                setOnClickListener { prefs.language = code; langPickerOpen = false; render(); requireActivity().recreate() }
            }
            val textCol = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            textCol.addView(TextView(ctx).apply { text = labels.first; setTextColor(if (selected) primary else fg); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
            textCol.addView(TextView(ctx).apply { text = labels.second; setTextColor(muted); textSize = 13f })
            item.addView(textCol)
            if (selected) item.addView(TextView(ctx).apply { text = "✓"; setTextColor(primary); textSize = 18f })
            wrap.addView(item)
        }
        return wrap
    }

    private fun dismissLegal() {
        legalOverlay?.let { binding.settingsRoot.removeView(it) }
        legalOverlay = null
        if (this::backCallback.isInitialized) backCallback.isEnabled = false
    }

    private fun showLegal(title: String, body: String) {
        val ctx = requireContext()
        val overlay = FrameLayout(ctx).apply {
            setBackgroundColor(bg)
            isClickable = true
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        val colL = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL }
        val head = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        head.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)) })
        head.addView(TextView(ctx).apply {
            text = title; setTextColor(fg); textSize = 20f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        head.addView(TextView(ctx).apply {
            text = "✕"; setTextColor(fg); textSize = 20f; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
            setOnClickListener { dismissLegal() }
        })
        colL.addView(head)
        colL.addView(View(ctx).apply { setBackgroundColor(border); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)) })
        val scroll = ScrollView(ctx)
        scroll.addView(TextView(ctx).apply {
            text = body; setTextColor(fg); textSize = 15f; setLineSpacing(dp(6).toFloat(), 1f)
            setPadding(dp(20), dp(20), dp(20), dp(48))
        })
        colL.addView(scroll)
        overlay.addView(colL)
        legalOverlay?.let { binding.settingsRoot.removeView(it) }
        legalOverlay = overlay
        binding.settingsRoot.addView(overlay)
        if (this::backCallback.isInitialized) backCallback.isEnabled = true
    }

    private fun dp(v: Int) = GameUi.dp(requireContext(), v)
    private fun dpf(v: Int) = GameUi.dpf(requireContext(), v)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
