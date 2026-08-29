package com.brightnest.app.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentGamesBinding

class GamesFragment : Fragment() {

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!

    private data class GameItem(val title: String, val emoji: String, val colorRes: Int, val destId: Int)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val games = listOf(
            GameItem("Drawing", "✏️", R.color.coral, R.id.drawingFragment),
            GameItem("Coloring", "🎨", R.color.sage, R.id.coloringFragment),
            GameItem("Memory Match", "🃏", R.color.lavender, R.id.memoryFragment),
            GameItem("Math Quiz", "🧮", R.color.primary, R.id.mathFragment),
            GameItem("Slide Puzzle", "🧩", R.color.coral, R.id.puzzleFragment),
            GameItem("Knowledge Quiz", "💡", R.color.sage, R.id.quizFragment),
        )

        val ctx = requireContext()
        val cardColor = ContextCompat.getColor(ctx, R.color.card)
        val borderColor = ContextCompat.getColor(ctx, R.color.border)

        for (g in games) {
            val gameColor = ContextCompat.getColor(ctx, g.colorRes)
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20))
                background = GameUi.rounded(cardColor, GameUi.dpf(ctx, 20), borderColor, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = GameUi.dp(ctx, 16) }
                isClickable = true
                isFocusable = true
                setOnClickListener { findNavController().navigate(g.destId) }
            }

            val iconBox = TextView(ctx).apply {
                text = g.emoji
                textSize = 26f
                gravity = Gravity.CENTER
                background = GameUi.rounded(GameUi.withAlpha(gameColor, 32), GameUi.dpf(ctx, 32))
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 64), GameUi.dp(ctx, 64))
            }
            card.addView(iconBox)

            val mid = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = GameUi.dp(ctx, 16)
                }
            }
            mid.addView(TextView(ctx).apply {
                text = g.title
                textSize = 17f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(ctx, R.color.foreground))
            })
            mid.addView(TextView(ctx).apply {
                text = "Play & earn coins"
                textSize = 12f
                setTextColor(ContextCompat.getColor(ctx, R.color.muted_foreground))
                setPadding(0, GameUi.dp(ctx, 4), 0, 0)
            })
            card.addView(mid)

            card.addView(TextView(ctx).apply {
                text = "▶"
                textSize = 26f
                setTextColor(gameColor)
            })

            binding.gamesList.addView(card)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
