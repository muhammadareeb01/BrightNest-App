package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
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
import com.brightnest.app.databinding.FragmentPuzzleBinding

class PuzzleFragment : Fragment() {

    private var _binding: FragmentPuzzleBinding? = null
    private val binding get() = _binding!!

    private var tiles = IntArray(9)
    private var won = false
    private var moves = 0
    private val tileViews = mutableListOf<TextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPuzzleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnReset.setOnClickListener { initGame() }
        initGame()
    }

    private fun initGame() {
        val t = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
        var empty = 8
        var lastEmpty = -1
        repeat(120) {
            val r = empty / 3
            val c = empty % 3
            val neighbors = mutableListOf<Int>()
            if (r > 0) neighbors.add(empty - 3)
            if (r < 2) neighbors.add(empty + 3)
            if (c > 0) neighbors.add(empty - 1)
            if (c < 2) neighbors.add(empty + 1)
            val choices = neighbors.filter { it != lastEmpty }
            val pick = choices.random()
            val tmp = t[empty]; t[empty] = t[pick]; t[pick] = tmp
            lastEmpty = empty
            empty = pick
        }
        tiles = t
        won = false
        moves = 0
        binding.movesText.text = "Moves: 0"
        renderBoard()
    }

    private fun renderBoard() {
        val ctx = requireContext()
        binding.gameFrame.removeAllViews()
        tileViews.clear()

        val boardSize = resources.displayMetrics.widthPixels - GameUi.dp(ctx, 64)
        val tileSize = boardSize / 3

        val wrap = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        if (won) {
            val coral = ContextCompat.getColor(ctx, R.color.coral)
            wrap.addView(LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 20), GameUi.dp(ctx, 24), GameUi.dp(ctx, 20))
                background = GameUi.rounded(GameUi.withAlpha(coral, 32), GameUi.dpf(ctx, 24))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = GameUi.dp(ctx, 24) }
                addView(TextView(ctx).apply {
                    text = "Puzzle Solved!"; textSize = 22f
                    setTypeface(typeface, Typeface.BOLD); setTextColor(coral)
                })
                addView(TextView(ctx).apply {
                    text = "+20 Coins"; textSize = 15f
                    setTypeface(typeface, Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(ctx, R.color.foreground))
                    setPadding(0, GameUi.dp(ctx, 8), 0, 0)
                })
            })
        }

        val board = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = GameUi.rounded(
                ContextCompat.getColor(ctx, R.color.card), GameUi.dpf(ctx, 16),
                ContextCompat.getColor(ctx, R.color.border), GameUi.dp(ctx, 4)
            )
            layoutParams = LinearLayout.LayoutParams(boardSize, boardSize)
        }

        val coral = ContextCompat.getColor(ctx, R.color.coral)
        val bgColor = ContextCompat.getColor(ctx, R.color.background)
        var row: LinearLayout? = null
        for (idx in 0 until 9) {
            if (idx % 3 == 0) {
                row = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
                board.addView(row)
            }
            val tile = tiles[idx]
            val tv = TextView(ctx).apply {
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(tileSize, tileSize)
                if (tile != 0) {
                    text = tile.toString()
                    textSize = 32f
                    setTypeface(typeface, Typeface.BOLD)
                    setTextColor(Color.WHITE)
                    background = GameUi.rounded(coral, 0f, bgColor, GameUi.dp(ctx, 2))
                }
                isClickable = true
                setOnClickListener { moveTile(idx) }
            }
            tileViews.add(tv)
            row?.addView(tv)
        }
        wrap.addView(board)
        binding.gameFrame.addView(wrap)
    }

    private fun moveTile(index: Int) {
        if (won) return
        val emptyIndex = tiles.indexOf(0)
        val row = index / 3; val emptyRow = emptyIndex / 3
        val col = index % 3; val emptyCol = emptyIndex % 3
        val adjacent = (Math.abs(row - emptyRow) == 1 && col == emptyCol) ||
                (Math.abs(col - emptyCol) == 1 && row == emptyRow)
        if (!adjacent) return

        val tmp = tiles[index]; tiles[index] = tiles[emptyIndex]; tiles[emptyIndex] = tmp
        moves += 1
        binding.movesText.text = "Moves: $moves"

        val solved = (0 until 8).all { tiles[it] == it + 1 } && tiles[8] == 0
        if (solved) {
            won = true
            Prefs(requireContext()).addCoins(20)
        }
        renderBoard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
