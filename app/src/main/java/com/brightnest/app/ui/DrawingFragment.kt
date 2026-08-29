package com.brightnest.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentDrawingBinding

class DrawingFragment : Fragment() {

    private var _binding: FragmentDrawingBinding? = null
    private val binding get() = _binding!!

    private var selectedColor = "#15795B"
    private var selectedSize = 16f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDrawingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dv = binding.drawingView

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnUndo.setOnClickListener { dv.undo() }
        binding.btnClear.setOnClickListener { dv.clear() }

        binding.btnSizeSmall.setOnClickListener { setBrushSize(8f, 1) }
        binding.btnSizeMedium.setOnClickListener { setBrushSize(16f, 2) }
        binding.btnSizeLarge.setOnClickListener { setBrushSize(28f, 3) }

        buildPalette()
        setBrushSize(selectedSize, 2)
        dv.setColor(Color.parseColor(selectedColor))
    }

    private fun setBrushSize(size: Float, index: Int) {
        selectedSize = size
        binding.drawingView.setStrokeWidth(size)

        val ctx = context ?: return
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)

        val pills = listOf(binding.btnSizeSmall, binding.btnSizeMedium, binding.btnSizeLarge)
        for (i in pills.indices) {
            val p = pills[i]
            val active = (i + 1) == index
            if (active) {
                p.setTextColor(Color.WHITE)
                p.background = GameUi.rounded(primary, GameUi.dpf(ctx, 16), primary, 0)
            } else {
                p.setTextColor(foreground)
                p.background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            }
        }
    }

    private fun buildPalette() {
        val ctx = context ?: return
        binding.paletteRow.removeAllViews()
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)

        for (c in ColoringData.palette) {
            val selected = selectedColor == c
            binding.paletteRow.addView(View(ctx).apply {
                val sz = if (selected) GameUi.dp(ctx, 42) else GameUi.dp(ctx, 36)
                background = GameUi.rounded(
                    Color.parseColor(c), GameUi.dpf(ctx, if (selected) 21 else 18),
                    if (selected) foreground else Color.parseColor("#26000000"),
                    GameUi.dp(ctx, if (selected) 3 else 1)
                )
                layoutParams = LinearLayout.LayoutParams(sz, sz).apply {
                    marginEnd = GameUi.dp(ctx, 10)
                    gravity = Gravity.CENTER_VERTICAL
                }
                isClickable = true
                setOnClickListener {
                    selectedColor = c
                    binding.drawingView.setColor(Color.parseColor(c))
                    buildPalette()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
