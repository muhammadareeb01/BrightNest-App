package com.brightnest.app.ui

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.AdultStore
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding

class TasbeehFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var store: AdultStore
    private lateinit var countText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        store = AdultStore(requireContext())
        binding.title.text = "Tasbeeh"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnAction.apply {
            text = "↺"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.foreground))
            setOnClickListener {
                store.resetTasbeeh()
                countText.text = store.tasbeehCount.toString()
            }
        }
        build()
    }

    private fun build() {
        val ctx = requireContext()
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        val area = FrameLayout(ctx).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            isClickable = true
            setOnClickListener {
                store.incrementTasbeeh()
                countText.text = store.tasbeehCount.toString()
            }
        }
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        val circle = FrameLayout(ctx).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(0x00000000)
                setStroke(GameUi.dp(ctx, 8), primary)
            }
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 200), GameUi.dp(ctx, 200))
        }
        countText = TextView(ctx).apply {
            text = store.tasbeehCount.toString()
            setTextColor(primary)
            textSize = 64f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        }
        circle.addView(countText)
        col.addView(circle)
        col.addView(TextView(ctx).apply {
            text = "Tap anywhere to count"
            setTextColor(muted)
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 32) }
        })
        area.addView(col)
        binding.toolRoot.addView(area)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
