package com.brightnest.app.ui

import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding

abstract class TopicGuideFragment : Fragment() {

    data class Topic(val title: String, val preview: String, val content: String)

    abstract val screenTitle: String
    abstract val accentColorRes: Int
    abstract val heroIcon: String
    abstract val heroTitle: String
    abstract val heroSubtitle: String
    abstract val cardIcon: String
    abstract val modalIcon: String
    abstract val topics: List<Topic>

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        binding.title.text = screenTitle
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        build()
    }

    private fun build() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val accent = ContextCompat.getColor(ctx, accentColorRes)
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

        val hero = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(GameUi.dp(ctx, 32), GameUi.dp(ctx, 32), GameUi.dp(ctx, 32), GameUi.dp(ctx, 32))
            background = GameUi.rounded(GameUi.withAlpha(accent, 0x15), GameUi.dpf(ctx, 24))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 24) }
        }
        hero.addView(TextView(ctx).apply { text = heroIcon; textSize = 36f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) } })
        hero.addView(TextView(ctx).apply { text = heroTitle; setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER })
        hero.addView(TextView(ctx).apply { text = heroSubtitle; setTextColor(muted); textSize = 16f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 8) } })
        col.addView(hero)

        // 2-column grid
        var i = 0
        while (i < topics.size) {
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) }
            }
            for (c in 0 until 2) {
                if (i < topics.size) {
                    row.addView(topicCard(ctx, topics[i], fg, muted, accent, card, border, c == 0))
                } else {
                    row.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(0, 1, 1f) })
                }
                i++
            }
            col.addView(row)
        }

        scroll.addView(col)
        binding.toolRoot.addView(scroll)
    }

    private fun topicCard(ctx: android.content.Context, topic: Topic, fg: Int, muted: Int, accent: Int, card: Int, border: Int, first: Boolean): View {
        val cardView = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                marginStart = if (first) 0 else GameUi.dp(ctx, 8)
                marginEnd = if (first) GameUi.dp(ctx, 8) else 0
            }
            setOnClickListener { showModal(topic) }
            isClickable = true
        }
        cardView.addView(TextView(ctx).apply {
            text = cardIcon; textSize = 20f; gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(accent, 0x15), GameUi.dpf(ctx, 20))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40))
        })
        cardView.addView(TextView(ctx).apply { text = topic.title; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 16); bottomMargin = GameUi.dp(ctx, 4) } })
        cardView.addView(TextView(ctx).apply { text = topic.preview; setTextColor(muted); textSize = 13f })
        return cardView
    }

    private fun showModal(topic: Topic) {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val accent = ContextCompat.getColor(ctx, accentColorRes)
        val bg = ContextCompat.getColor(ctx, R.color.background)

        val overlay = FrameLayout(ctx).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setBackgroundColor(0x80000000.toInt())
            isClickable = true
        }
        val sheet = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawableTop(bg, GameUi.dpf(ctx, 32))
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (resources.displayMetrics.heightPixels * 0.8f).toInt(), Gravity.BOTTOM)
        }
        val modalHeader = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
        }
        modalHeader.addView(TextView(ctx).apply {
            text = "✕"; textSize = 20f; setTextColor(fg); gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(ContextCompat.getColor(ctx, R.color.black), 0x0D), GameUi.dpf(ctx, 20))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40))
            setOnClickListener { binding.toolRoot.removeView(overlay) }
        })
        sheet.addView(modalHeader)

        val bodyScroll = ScrollView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        val bodyCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
        }
        bodyCol.addView(TextView(ctx).apply {
            text = modalIcon; textSize = 28f; gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(accent, 0x15), GameUi.dpf(ctx, 32))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 64), GameUi.dp(ctx, 64)).apply { bottomMargin = GameUi.dp(ctx, 24) }
        })
        bodyCol.addView(TextView(ctx).apply { text = topic.title; setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) } })
        bodyCol.addView(TextView(ctx).apply { text = topic.content; setTextColor(fg); textSize = 16f; setLineSpacing(GameUi.dpf(ctx, 4), 1f) })
        bodyScroll.addView(bodyCol)
        sheet.addView(bodyScroll)

        overlay.addView(sheet)
        overlay.setOnClickListener { binding.toolRoot.removeView(overlay) }
        binding.toolRoot.addView(overlay)
    }

    private fun GradientDrawableTop(color: Int, radius: Float): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(color)
            cornerRadii = floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
