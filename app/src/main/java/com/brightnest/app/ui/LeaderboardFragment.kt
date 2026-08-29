package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.Typeface
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
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private data class Player(val name: String, val coins: Int)
    private data class Medal(val color: Int, val emoji: String)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val ctx = requireContext()
        val prefs = Prefs(ctx)
        val mutedFg = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)

        val friends = listOf(
            Player("Aisha", 480), Player("Bilal", 425), Player("Fatima", 390),
            Player("Hamza", 310), Player("Maryam", 280), Player("Yusuf", 240),
            Player("Zaynab", 200), Player("Omar", 165),
        )
        val all = (friends + Player("You", prefs.coins)).sortedByDescending { it.coins }

        fun medal(rank: Int): Medal = when (rank) {
            0 -> Medal(Color.parseColor("#F59E0B"), "🏆")
            1 -> Medal(Color.parseColor("#9CA3AF"), "🥈")
            2 -> Medal(Color.parseColor("#CD7F32"), "🥉")
            else -> Medal(mutedFg, "•")
        }

        all.forEachIndexed { i, p ->
            val m = medal(i)
            val isYou = p.name == "You"
            val rowBg = if (isYou) GameUi.withAlpha(primary, 32) else GameUi.withAlpha(m.color, 21)
            val rowBorder = if (isYou) primary else m.color

            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(GameUi.dp(ctx, 14), GameUi.dp(ctx, 14), GameUi.dp(ctx, 14), GameUi.dp(ctx, 14))
                background = GameUi.rounded(rowBg, GameUi.dpf(ctx, 16), rowBorder, GameUi.dp(ctx, 2))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = GameUi.dp(ctx, 10) }
            }

            val rankCircle = TextView(ctx).apply {
                text = (i + 1).toString()
                setTextColor(Color.WHITE)
                textSize = 18f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
                background = GameUi.rounded(m.color, GameUi.dpf(ctx, 22))
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 44), GameUi.dp(ctx, 44))
            }
            row.addView(rankCircle)

            row.addView(TextView(ctx).apply {
                text = m.emoji
                textSize = 26f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    marginStart = GameUi.dp(ctx, 14)
                }
            })

            row.addView(TextView(ctx).apply {
                text = p.name + if (isYou) " (You)" else ""
                textSize = 16f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(if (isYou) primary else foreground)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = GameUi.dp(ctx, 14)
                }
            })

            row.addView(TextView(ctx).apply {
                text = "⭐ ${p.coins}"
                textSize = 15f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.parseColor("#F59E0B"))
            })

            binding.lbList.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
