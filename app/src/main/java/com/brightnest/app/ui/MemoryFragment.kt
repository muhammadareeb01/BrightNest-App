package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentMemoryBinding

class MemoryFragment : Fragment() {

    private var _binding: FragmentMemoryBinding? = null
    private val binding get() = _binding!!

    private data class Card(val id: Int, val emoji: String, var isFlipped: Boolean, var isMatched: Boolean)

    private val icons = listOf("🐱", "🐶", "🐘", "🐟", "🐦", "🐄", "🐴", "🐰")
    private lateinit var cards: MutableList<Card>
    private val flipped = mutableListOf<Int>()
    private var moves = 0
    private var locked = false
    private var won = false
    private val handler = Handler(Looper.getMainLooper())
    private val cardViews = mutableListOf<TextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMemoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnReset.setOnClickListener { resetGame() }
        resetGame()
    }

    private fun buildDeck(): MutableList<Card> {
        val deck = mutableListOf<Card>()
        icons.forEachIndexed { i, ic ->
            deck.add(Card(i * 2, ic, false, false))
            deck.add(Card(i * 2 + 1, ic, false, false))
        }
        for (i in deck.size - 1 downTo 1) {
            val j = (0..i).random()
            val tmp = deck[i]; deck[i] = deck[j]; deck[j] = tmp
        }
        return deck
    }

    private fun resetGame() {
        handler.removeCallbacksAndMessages(null)
        cards = buildDeck()
        flipped.clear()
        moves = 0
        locked = false
        won = false
        binding.movesText.text = "Moves: 0"
        renderGrid()
    }

    private fun renderGrid() {
        val ctx = requireContext() ?: return
        binding.gameFrame.removeAllViews()
        cardViews.clear()

        val cols = 4
        val totalGap = GameUi.dp(ctx, 8) * (cols - 1)
        val frameW = resources.displayMetrics.widthPixels - GameUi.dp(ctx, 64)
        val size = (frameW - totalGap) / cols

        val grid = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        var row: LinearLayout? = null
        cards.forEachIndexed { idx, _ ->
            if (idx % cols == 0) {
                row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                }
                grid.addView(row)
            }
            val tv = TextView(ctx).apply {
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    val g = GameUi.dp(ctx, 4)
                    setMargins(g, g, g, g)
                }
                isClickable = true
                isFocusable = true
                setOnClickListener { onCardPress(idx) }
            }
            cardViews.add(tv)
            row?.addView(tv)
            paintCard(idx)
        }
        binding.gameFrame.addView(grid)
    }

    private fun paintCard(idx: Int) {
        val ctx = context ?: return
        val card = cards[idx]
        val tv = cardViews[idx]
        val lavender = ContextCompat.getColor(ctx, R.color.lavender)
        val cardBg = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val sage = ContextCompat.getColor(ctx, R.color.sage)
        val faceUp = card.isFlipped || card.isMatched
        val bg = if (faceUp) cardBg else lavender
        val strokeColor = if (card.isMatched) sage else border
        tv.background = GameUi.rounded(bg, GameUi.dpf(ctx, 12), strokeColor, GameUi.dp(ctx, 2))
        if (faceUp) {
            tv.text = card.emoji
            tv.textSize = 30f
        } else {
            tv.text = "?"
            tv.textSize = 26f
            tv.setTextColor(Color.WHITE)
        }
    }

    private fun onCardPress(index: Int) {
        if (locked || won) return
        val card = cards[index]
        if (card.isFlipped || card.isMatched) return

        card.isFlipped = true
        paintCard(index)
        flipped.add(index)

        if (flipped.size == 2) {
            locked = true
            moves += 1
            binding.movesText.text = "Moves: $moves"
            val i1 = flipped[0]; val i2 = flipped[1]
            if (cards[i1].emoji == cards[i2].emoji) {
                handler.postDelayed({
                    cards[i1].isMatched = true; cards[i2].isMatched = true
                    paintCard(i1); paintCard(i2)
                    flipped.clear(); locked = false
                    if (cards.all { it.isMatched }) handleWin()
                }, 450)
            } else {
                handler.postDelayed({
                    cards[i1].isFlipped = false; cards[i2].isFlipped = false
                    paintCard(i1); paintCard(i2)
                    flipped.clear(); locked = false
                }, 900)
            }
        }
    }

    private fun handleWin() {
        won = true
        val ctx = requireContext()
        val prefs = Prefs(ctx)
        prefs.addCoins(10)
        prefs.addStars(1)

        val lavender = ContextCompat.getColor(ctx, R.color.lavender)
        val overlay = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(GameUi.withAlpha(ContextCompat.getColor(ctx, R.color.background), 242))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        overlay.addView(TextView(ctx).apply { text = "🌟"; textSize = 90f; gravity = Gravity.CENTER })
        overlay.addView(TextView(ctx).apply {
            text = "You Won!"; textSize = 30f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(ctx, R.color.foreground))
            setPadding(0, GameUi.dp(ctx, 16), 0, 0)
        })
        overlay.addView(TextView(ctx).apply {
            text = "+10 Coins & 1 Star"; textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(lavender)
            setPadding(0, GameUi.dp(ctx, 8), 0, 0)
        })
        overlay.addView(TextView(ctx).apply {
            text = "Play Again"; textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 32), GameUi.dp(ctx, 14), GameUi.dp(ctx, 32), GameUi.dp(ctx, 14))
            background = GameUi.rounded(lavender, GameUi.dpf(ctx, 100))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = GameUi.dp(ctx, 24) }
            isClickable = true
            setOnClickListener { resetGame() }
        })
        binding.gameFrame.addView(overlay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
