package com.brightnest.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
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
import com.brightnest.app.AdultStore
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding

class WaterFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var store: AdultStore

    private val blue = Color.parseColor("#3B82F6")
    private lateinit var ring: RingView
    private lateinit var countText: TextView
    private lateinit var subTitle: TextView
    private lateinit var targetLabel: TextView
    private val chipViews = mutableListOf<Pair<Int, TextView>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        store = AdultStore(requireContext())
        binding.title.text = "Water Tracker"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        build()
    }

    private fun refresh() {
        ring.percentage = pct()
        ring.invalidate()
        countText.text = store.waterGlasses.toString()
        subTitle.text = "Daily target: ${store.waterTarget} glasses"
        targetLabel.text = "/ ${store.waterTarget} glasses"
        updateChips()
    }

    private fun pct(): Float =
        minOf((store.waterGlasses.toFloat() / maxOf(store.waterTarget, 1)) * 100f, 100f)

    private fun updateChips() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)
        chipViews.forEach { (t, tv) ->
            val sel = store.waterTarget == t
            tv.background = GameUi.rounded(if (sel) blue else card, GameUi.dpf(ctx, 12), if (sel) blue else border, GameUi.dp(ctx, 1))
            tv.setTextColor(if (sel) Color.WHITE else fg)
        }
    }

    private fun build() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedC = ContextCompat.getColor(ctx, R.color.muted)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val scroll = ScrollView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            isFillViewport = true
        }
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 48))
        }

        col.addView(TextView(ctx).apply {
            text = "Stay Hydrated"; setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 8) }
        })
        subTitle = TextView(ctx).apply {
            text = "Daily target: ${store.waterTarget} glasses"; setTextColor(muted); textSize = 16f; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 48) }
        }
        col.addView(subTitle)

        // Ring
        val ringBox = FrameLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 300), GameUi.dp(ctx, 300)).apply { bottomMargin = GameUi.dp(ctx, 48) }
        }
        ring = RingView(ctx, mutedC, blue, GameUi.dpf(ctx, 24)).apply {
            percentage = pct()
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        ringBox.addView(ring)
        val ringContent = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        }
        ringContent.addView(TextView(ctx).apply { text = "💧"; textSize = 36f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 8) } })
        countText = TextView(ctx).apply { text = store.waterGlasses.toString(); setTextColor(fg); textSize = 48f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER }
        ringContent.addView(countText)
        targetLabel = TextView(ctx).apply { text = "/ ${store.waterTarget} glasses"; setTextColor(muted); textSize = 16f; gravity = Gravity.CENTER }
        ringContent.addView(targetLabel)
        ringBox.addView(ringContent)
        col.addView(ringBox)

        // Controls
        val controls = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 48) }
        }
        controls.addView(TextView(ctx).apply {
            text = "−"; textSize = 28f; setTextColor(fg); gravity = Gravity.CENTER
            background = GameUi.rounded(card, GameUi.dpf(ctx, 32), border, GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 64), GameUi.dp(ctx, 64)).apply { marginEnd = GameUi.dp(ctx, 32) }
            setOnClickListener { store.removeWater(); refresh() }
        })
        controls.addView(TextView(ctx).apply {
            text = "+"; textSize = 36f; setTextColor(Color.WHITE); gravity = Gravity.CENTER
            background = GameUi.rounded(blue, GameUi.dpf(ctx, 44))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 88), GameUi.dp(ctx, 88))
            setOnClickListener { store.addWater(); refresh() }
        })
        col.addView(controls)

        // Target section
        val targetSection = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
            background = GameUi.rounded(GameUi.withAlpha(Color.BLACK, 0x08), GameUi.dpf(ctx, 24))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        targetSection.addView(TextView(ctx).apply { text = "Adjust Target"; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) } })
        val targetRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        chipViews.clear()
        listOf(6, 8, 10, 12).forEachIndexed { idx, t ->
            val chip = TextView(ctx).apply {
                text = t.toString(); textSize = 16f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 60), GameUi.dp(ctx, 48)).apply {
                    if (idx > 0) marginStart = GameUi.dp(ctx, 12)
                }
                setOnClickListener { store.waterTarget = t; refresh() }
            }
            chipViews.add(t to chip)
            targetRow.addView(chip)
        }
        targetSection.addView(targetRow)
        col.addView(targetSection)
        updateChips()

        scroll.addView(col)
        binding.toolRoot.addView(scroll)
    }

    private class RingView(
        ctx: Context,
        private val trackColor: Int,
        private val progressColor: Int,
        private val strokeW: Float
    ) : View(ctx) {
        var percentage: Float = 0f
        private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = strokeW; color = trackColor
        }
        private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = strokeW; color = progressColor; strokeCap = Paint.Cap.ROUND
        }
        private val rect = RectF()

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val pad = strokeW / 2f
            rect.set(pad, pad, width - pad, height - pad)
            canvas.drawArc(rect, 0f, 360f, false, trackPaint)
            canvas.drawArc(rect, -90f, 360f * (percentage / 100f), false, progressPaint)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
