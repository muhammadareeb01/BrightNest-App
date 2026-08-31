package com.brightnest.app.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentSubscriptionBinding

class SubscriptionFragment : Fragment() {

    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs

    private data class Tier(
        val id: String,
        val name: String,
        val tagline: String,
        val monthly: Double,
        val yearly: Double,
        val features: List<String>,
        val highlight: Boolean,
        val icon: String
    )

    private val tiers = listOf(
        Tier("free", "Free", "Get started, no strings attached.", 0.0, 0.0,
            listOf("Basic kids games", "Daily planner", "Water tracker"), false, "☀️"),
        Tier("premium", "Premium", "Unlock the full BrightNest experience.", 4.99, 49.99,
            listOf("All kids educational content", "Advanced adult tools", "Cloud sync across devices", "Ad-free experience"), true, "⭐"),
        Tier("family", "Family", "Everything, for the whole household.", 9.99, 99.99,
            listOf("Up to 5 family profiles", "Parental controls & reports", "All premium features", "Priority support"), false, "👪")
    )

    private var billing = "monthly"
    private var selected = "premium"
    private var modalOpen = false
    private var justUnlocked = false

    private var isDark = false
    private var fg = 0
    private var muted = 0
    private var primary = 0
    private var primaryFg = 0
    private var bg = 0
    private var glassBg = 0
    private var glassBorder = 0

    private lateinit var backCallback: OnBackPressedCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()
        prefs = Prefs(ctx)
        isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        fg = ContextCompat.getColor(ctx, R.color.foreground)
        muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        primary = ContextCompat.getColor(ctx, R.color.primary)
        primaryFg = ContextCompat.getColor(ctx, R.color.on_primary)
        bg = ContextCompat.getColor(ctx, R.color.background)
        glassBg = if (isDark) Color.argb(26, 255, 255, 255) else Color.argb(140, 255, 255, 255)
        glassBorder = if (isDark) Color.argb(46, 255, 255, 255) else Color.argb(179, 255, 255, 255)

        // gradient background
        val grad = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            if (isDark) intArrayOf(Color.parseColor("#1B1230"), Color.parseColor("#2A1A4A"), Color.parseColor("#050B14"))
            else intArrayOf(Color.parseColor("#FFE3C2"), Color.parseColor("#FFD0E1"), Color.parseColor("#E6E0FF"))
        )
        binding.subRoot.background = grad

        backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                modalOpen = false
                justUnlocked = false
                render()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        render()
    }

    private val savingsPct: Int
        get() {
            val t = tiers.firstOrNull { it.highlight } ?: tiers.firstOrNull { it.monthly > 0 } ?: return 0
            if (t.monthly == 0.0) return 0
            return Math.round((1 - t.yearly / (t.monthly * 12)) * 100).toInt()
        }

    private fun formatPrice(t: Tier): Pair<String, String> {
        if (t.monthly == 0.0) return "$0" to "forever"
        if (billing == "monthly") return "$${String.format("%.2f", t.monthly)}" to "/mo"
        val perMonth = String.format("%.2f", t.yearly / 12)
        return "$$perMonth" to "/mo, billed yearly"
    }

    private fun render() {
        val ctx = requireContext()
        binding.subRoot.removeAllViews()

        // halos
        binding.subRoot.addView(halo(ctx, if (isDark) "#7B5CFF" else "#FFB37A", 0.35f, Gravity.TOP or Gravity.END, -100, -120, 0, 0))
        binding.subRoot.addView(halo(ctx, if (isDark) "#FF6BCB" else "#B79CFF", 0.25f, Gravity.BOTTOM or Gravity.START, 0, 0, -120, -140))

        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }

        // header
        val header = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(8))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        header.addView(TextView(ctx).apply {
            text = "←"; setTextColor(fg); textSize = 20f; gravity = Gravity.CENTER
            background = GameUi.rounded(glassBg, dpf(20), glassBorder, dp(1))
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
            setOnClickListener { findNavController().navigateUp() }
        })
        header.addView(TextView(ctx).apply {
            text = "Upgrade"; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)) })
        col.addView(header)

        val scroll = ScrollView(ctx).apply {
            isVerticalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(56))
        }

        // hero badge
        val badgeRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) }
        }
        val badge = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(6), dp(12), dp(6))
            background = GameUi.rounded(glassBg, dpf(999), glassBorder, dp(1))
        }
        badge.addView(TextView(ctx).apply { text = "⚡"; textSize = 12f; setTextColor(primary) })
        badge.addView(TextView(ctx).apply { text = "Limited launch pricing"; setTextColor(fg); textSize = 13f; setTypeface(typeface, Typeface.BOLD); setPadding(dp(6), 0, 0, 0) })
        badgeRow.addView(badge)
        content.addView(badgeRow)

        content.addView(TextView(ctx).apply {
            text = "Unlock BrightNest"; setTextColor(fg); textSize = 30f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(8) }
        })
        content.addView(TextView(ctx).apply {
            text = "A premium space for your family's learning, routines and reflection."
            setTextColor(muted); textSize = 15f; gravity = Gravity.CENTER
            setPadding(dp(24), 0, dp(24), 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(24) }
        })

        content.addView(buildBillingToggle(ctx))

        tiers.forEach { content.addView(buildTierCard(ctx, it)) }

        content.addView(buildAssurance(ctx))
        content.addView(TextView(ctx).apply {
            text = "Prices shown in USD. Subscriptions renew automatically until cancelled."
            setTextColor(muted); textSize = 12f; gravity = Gravity.CENTER
            setPadding(dp(24), 0, dp(24), 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) }
        })

        scroll.addView(content)
        col.addView(scroll)
        binding.subRoot.addView(col)

        if (modalOpen) binding.subRoot.addView(buildModal(ctx))
        if (this::backCallback.isInitialized) backCallback.isEnabled = modalOpen
    }

    private fun halo(ctx: Context, color: String, alpha: Float, gravity: Int, ml: Int, mt: Int, mr: Int, mb: Int): View =
        View(ctx).apply {
            background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor(color)) }
            this.alpha = alpha
            layoutParams = FrameLayout.LayoutParams(dp(320), dp(320), gravity).apply {
                leftMargin = dp(ml); topMargin = dp(mt); rightMargin = dp(mr); bottomMargin = dp(mb)
            }
        }

    private fun buildBillingToggle(ctx: Context): View {
        val toggle = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            background = GameUi.rounded(glassBg, dpf(999), glassBorder, dp(1))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL; bottomMargin = dp(24)
            }
        }
        listOf("monthly", "yearly").forEach { b ->
            val active = billing == b
            val pill = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(18), dp(10), dp(18), dp(10))
                if (active) background = GameUi.rounded(primary, dpf(999))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    if (b == "yearly") marginStart = dp(4)
                }
                setOnClickListener { billing = b; render() }
            }
            pill.addView(TextView(ctx).apply {
                text = if (b == "monthly") "Monthly" else "Yearly"
                setTextColor(if (active) primaryFg else fg); textSize = 13f; setTypeface(typeface, Typeface.BOLD)
            })
            if (b == "yearly" && savingsPct > 0) {
                pill.addView(TextView(ctx).apply {
                    text = "SAVE $savingsPct%"; textSize = 10f; setTypeface(typeface, Typeface.BOLD)
                    setTextColor(if (active) primaryFg else primary)
                    setPadding(dp(6), dp(2), dp(6), dp(2))
                    background = GameUi.rounded(if (active) Color.argb(64, 255, 255, 255) else GameUi.withAlpha(primary, 0x22), dpf(999))
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = dp(8) }
                })
            }
            toggle.addView(pill)
        }
        return toggle
    }

    private fun buildTierCard(ctx: Context, tier: Tier): View {
        val isSelected = selected == tier.id
        val isCurrent = tier.id == prefs.premiumTier
        val cardRoot = FrameLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(16) }
            setOnClickListener { selected = tier.id; render() }
        }
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = GameUi.rounded(glassBg, dpf(24), if (isSelected) primary else glassBorder, if (isSelected) dp(2) else dp(1))
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        }

        // top row
        val topRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, if (tier.highlight) dp(90) else 0, 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(16) }
        }
        topRow.addView(TextView(ctx).apply {
            text = tier.icon; textSize = 18f; gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(primary, 0x1A), dpf(12))
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)).apply { marginEnd = dp(12) }
        })
        val nameCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val nameRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        nameRow.addView(TextView(ctx).apply { text = tier.name; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD) })
        if (isCurrent) {
            nameRow.addView(TextView(ctx).apply {
                text = "CURRENT"; setTextColor(fg); textSize = 10f; setTypeface(typeface, Typeface.BOLD)
                setPadding(dp(8), dp(3), dp(8), dp(3))
                background = GameUi.rounded(GameUi.withAlpha(fg, 0x12), dpf(999), GameUi.withAlpha(fg, 0x20), dp(1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = dp(8) }
            })
        }
        nameCol.addView(nameRow)
        nameCol.addView(TextView(ctx).apply { text = tier.tagline; setTextColor(muted); textSize = 12f; setPadding(0, dp(2), 0, 0) })
        topRow.addView(nameCol)
        card.addView(topRow)

        // price
        val (price, period) = formatPrice(tier)
        val priceRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.BOTTOM
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(16) }
        }
        priceRow.addView(TextView(ctx).apply { text = price; setTextColor(fg); textSize = 36f; setTypeface(typeface, Typeface.BOLD) })
        priceRow.addView(TextView(ctx).apply { text = period; setTextColor(muted); textSize = 15f; setPadding(dp(6), 0, 0, dp(4)) })
        card.addView(priceRow)

        card.addView(View(ctx).apply {
            setBackgroundColor(GameUi.withAlpha(fg, 0x12))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)).apply { bottomMargin = dp(16) }
        })

        tier.features.forEach { f ->
            val frow = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) }
            }
            frow.addView(TextView(ctx).apply {
                text = "✓"; setTextColor(primary); textSize = 12f; gravity = Gravity.CENTER
                background = GameUi.rounded(GameUi.withAlpha(primary, 0x1F), dpf(11))
                layoutParams = LinearLayout.LayoutParams(dp(22), dp(22)).apply { marginEnd = dp(12) }
            })
            frow.addView(TextView(ctx).apply { text = f; setTextColor(fg); textSize = 15f; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
            card.addView(frow)
        }
        // spacer below features (gap 20 - last feature already 12)
        card.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(8)) })

        // CTA
        card.addView(buildCta(ctx, tier, isCurrent))
        cardRoot.addView(card)

        if (tier.highlight) {
            val popular = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(10), dp(5), dp(10), dp(5))
                background = GameUi.rounded(primary, dpf(999))
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP or Gravity.END).apply {
                    topMargin = dp(14); rightMargin = dp(14)
                }
            }
            popular.addView(TextView(ctx).apply { text = "⭐"; textSize = 11f; setTextColor(primaryFg) })
            popular.addView(TextView(ctx).apply { text = "MOST POPULAR"; setTextColor(primaryFg); textSize = 10f; setTypeface(typeface, Typeface.BOLD); setPadding(dp(4), 0, 0, 0) })
            cardRoot.addView(popular)
        }
        return cardRoot
    }

    private fun buildCta(ctx: Context, tier: Tier, isCurrent: Boolean): View {
        val cta = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER
            setPadding(0, dp(14), 0, dp(14))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        when {
            isCurrent -> {
                cta.background = GameUi.rounded(GameUi.withAlpha(fg, 0x10), dpf(14), GameUi.withAlpha(fg, 0x18), dp(1))
                cta.addView(TextView(ctx).apply { text = "✓"; setTextColor(muted); textSize = 15f; setPadding(0, 0, dp(8), 0) })
                cta.addView(TextView(ctx).apply { text = "Your current plan"; setTextColor(muted); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
            }
            tier.id == "free" -> {
                cta.background = GameUi.rounded(Color.TRANSPARENT, dpf(14), GameUi.withAlpha(fg, 0x30), dp(2))
                cta.addView(TextView(ctx).apply { text = "Switch to Free"; setTextColor(fg); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
                cta.setOnClickListener { prefs.premiumTier = "free"; render() }
            }
            else -> {
                cta.background = GameUi.rounded(Color.TRANSPARENT, dpf(14), muted, dp(2))
                cta.addView(TextView(ctx).apply { text = "Coming Soon"; setTextColor(muted); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
                cta.setOnClickListener { android.widget.Toast.makeText(ctx, "Coming Soon!", android.widget.Toast.LENGTH_SHORT).show() }
            }
        }
        return cta
    }

    private fun buildAssurance(ctx: Context): View {
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(14), 0, dp(14))
            background = GameUi.rounded(glassBg, dpf(20), glassBorder, dp(1))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(20) }
        }
        fun item(icon: String, label: String): View = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            addView(TextView(ctx).apply { text = icon; textSize = 16f; setTextColor(primary); gravity = Gravity.CENTER })
            addView(TextView(ctx).apply { text = label; setTextColor(fg); textSize = 12f; gravity = Gravity.CENTER; setPadding(0, dp(4), 0, 0) })
        }
        fun divider(): View = View(ctx).apply {
            setBackgroundColor(GameUi.withAlpha(fg, 0x15))
            layoutParams = LinearLayout.LayoutParams(dp(1), dp(28))
        }
        row.addView(item("🛡️", "Secure"))
        row.addView(divider())
        row.addView(item("🔄", "Cancel anytime"))
        row.addView(divider())
        row.addView(item("❤️", "Family-first"))
        return row
    }

    private fun buildModal(ctx: Context): View {
        val tier = tiers.firstOrNull { it.id == selected } ?: tiers[1]
        val overlay = FrameLayout(ctx).apply {
            setBackgroundColor(Color.argb(128, 0, 0, 0))
            isClickable = true
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setOnClickListener { modalOpen = false; justUnlocked = false; render() }
        }
        val sheet = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(bg)
                cornerRadii = floatArrayOf(dpf(32), dpf(32), dpf(32), dpf(32), 0f, 0f, 0f, 0f)
            }
            isClickable = true
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
        }
        // handle
        sheet.addView(LinearLayout(ctx).apply {
            gravity = Gravity.CENTER; setPadding(0, dp(10), 0, 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(View(ctx).apply {
                background = GameUi.rounded(GameUi.withAlpha(fg, 0x30), dpf(2))
                layoutParams = LinearLayout.LayoutParams(dp(40), dp(4))
            })
        })
        // close
        sheet.addView(LinearLayout(ctx).apply {
            gravity = Gravity.END; setPadding(dp(16), dp(8), dp(16), 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(TextView(ctx).apply {
                text = "✕"; setTextColor(fg); textSize = 18f; gravity = Gravity.CENTER
                background = GameUi.rounded(GameUi.withAlpha(fg, 0x10), dpf(18))
                layoutParams = LinearLayout.LayoutParams(dp(36), dp(36))
                setOnClickListener { modalOpen = false; justUnlocked = false; render() }
            })
        })

        val body = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), 0, dp(24), dp(32))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        body.addView(TextView(ctx).apply {
            text = if (justUnlocked) "✅" else tier.icon; textSize = 28f; gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(primary, 0x1A), dpf(36))
            layoutParams = LinearLayout.LayoutParams(dp(72), dp(72)).apply { bottomMargin = dp(20) }
        })
        if (justUnlocked) {
            body.addView(TextView(ctx).apply { text = "You're all set"; setTextColor(fg); textSize = 22f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) } })
            body.addView(TextView(ctx).apply { text = "${tier.name} is now unlocked on this device. Enjoy the full BrightNest experience."; setTextColor(muted); textSize = 15f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(24) } })
            body.addView(primaryButton(ctx, "Done", null) { modalOpen = false; justUnlocked = false; render() })
        } else {
            body.addView(TextView(ctx).apply { text = "Unlock ${tier.name}"; setTextColor(fg); textSize = 22f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) } })
            body.addView(TextView(ctx).apply { text = "This unlocks all ${tier.name} features on this device. No payment is taken."; setTextColor(muted); textSize = 15f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(20) } })
            val (price, period) = formatPrice(tier)
            val summary = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(14), dp(16), dp(14))
                background = GameUi.rounded(GameUi.withAlpha(fg, 0x0A), dpf(14), GameUi.withAlpha(fg, 0x14), dp(1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(16) }
            }
            summary.addView(TextView(ctx).apply { text = tier.name; setTextColor(fg); textSize = 15f; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
            summary.addView(TextView(ctx).apply { text = "$price$period"; setTextColor(fg); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
            body.addView(summary)
            body.addView(primaryButton(ctx, "Unlock now", "🔓") { prefs.premiumTier = selected; justUnlocked = true; render() })
        }
        sheet.addView(body)
        overlay.addView(sheet)
        return overlay
    }

    private fun primaryButton(ctx: Context, label: String, icon: String?, onClick: () -> Unit): View {
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER
            background = GameUi.rounded(primary, dpf(14))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52))
            if (icon != null) addView(TextView(ctx).apply { text = icon; setTextColor(primaryFg); textSize = 16f; setPadding(0, 0, dp(8), 0) })
            addView(TextView(ctx).apply { text = label; setTextColor(primaryFg); textSize = 15f; setTypeface(typeface, Typeface.BOLD) })
            setOnClickListener { onClick() }
        }
    }

    private fun dp(v: Int) = GameUi.dp(requireContext(), v)
    private fun dpf(v: Int) = GameUi.dpf(requireContext(), v)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
