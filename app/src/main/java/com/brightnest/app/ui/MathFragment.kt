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
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentMathBinding
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MathFragment : Fragment() {

    private var _binding: FragmentMathBinding? = null
    private val binding get() = _binding!!

    private data class Diff(val key: String, val label: String, val urdu: String, val maxN: Int, val ops: List<String>, val color: String, val time: Int)

    private val diffs = listOf(
        Diff("easy", "Easy", "آسان", 10, listOf("+", "−"), "#22C55E", 15),
        Diff("medium", "Medium", "درمیانہ", 20, listOf("+", "−", "×"), "#F59E0B", 12),
        Diff("hard", "Hard", "مشکل", 12, listOf("+", "−", "×", "÷"), "#EF4444", 10),
    )

    private var difficulty: Diff? = null
    private var num1 = 0
    private var num2 = 0
    private var op = "+"
    private var options = listOf<Int>()
    private var score = 0
    private var streak = 0
    private var bestStreak = 0
    private var lives = 3
    private var timeLeft = 15
    private var feedback: String? = null
    private var pickedIdx: Int? = null
    private var gameOver = false
    private var questionNum = 0
    private var totalWins = 0

    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    // gameplay refs
    private var timerFill: View? = null
    private var timerEmpty: View? = null
    private var timerText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMathBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showPicker()
    }

    private fun correctAnswer(): Int = when (op) {
        "+" -> num1 + num2
        "−" -> num1 - num2
        "×" -> num1 * num2
        else -> num1 / num2
    }

    private fun generateProblem() {
        val cfg = difficulty ?: return
        val o = cfg.ops.random()
        var n1: Int; var n2: Int; var ans: Int
        when (o) {
            "+" -> { n1 = 1 + (0 until cfg.maxN).random(); n2 = 1 + (0 until cfg.maxN).random(); ans = n1 + n2 }
            "−" -> { n1 = 1 + (0 until cfg.maxN).random(); n2 = 1 + (0 until cfg.maxN).random(); if (n1 < n2) { val t = n1; n1 = n2; n2 = t }; ans = n1 - n2 }
            "×" -> { val m = min(cfg.maxN, 12); n1 = 1 + (0 until m).random(); n2 = 1 + (0 until m).random(); ans = n1 * n2 }
            else -> { val m = min(cfg.maxN, 12); ans = 1 + (0 until m).random(); n2 = 1 + (0 until m).random(); n1 = ans * n2 }
        }
        num1 = n1; num2 = n2; op = o
        val opts = linkedSetOf(ans)
        while (opts.size < 4) {
            val delta = (0 until 9).random() - 4
            val w = ans + (if (delta == 0) 1 else delta)
            if (w >= 0) opts.add(w)
        }
        options = opts.shuffled()
        feedback = null
        pickedIdx = null
        timeLeft = cfg.time
        questionNum += 1
        showGameplay()
        startTimer()
    }

    private fun startTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        val r = object : Runnable {
            override fun run() {
                if (difficulty == null || gameOver || feedback != null) return
                if (timeLeft <= 0) { handleWrong(true); return }
                timeLeft -= 1
                updateTimer()
                handler.postDelayed(this, 1000)
            }
        }
        timerRunnable = r
        handler.postDelayed(r, 1000)
        updateTimer()
    }

    private fun updateTimer() {
        val cfg = difficulty ?: return
        val ctx = context ?: return
        val pct = timeLeft.toFloat() / cfg.time
        val col = when {
            timeLeft <= 3 -> Color.parseColor("#EF4444")
            timeLeft <= 6 -> Color.parseColor("#F59E0B")
            else -> ContextCompat.getColor(ctx, R.color.primary)
        }
        timerFill?.let {
            val lp = it.layoutParams as LinearLayout.LayoutParams
            lp.weight = max(0f, pct)
            it.layoutParams = lp
            it.setBackgroundColor(col)
        }
        timerEmpty?.let {
            val lp = it.layoutParams as LinearLayout.LayoutParams
            lp.weight = max(0f, 1f - pct)
            it.layoutParams = lp
        }
        timerText?.apply { text = "${timeLeft}s"; setTextColor(col) }
    }

    private fun handleCorrect() {
        val cfg = difficulty ?: return
        val prefs = Prefs(requireContext())
        val newStreak = streak + 1
        val bonus = min(newStreak, 5)
        prefs.addCoins(2 + bonus)
        if (newStreak > 0 && newStreak % 5 == 0) prefs.addStars(1)
        score += 10 + bonus * 2
        streak = newStreak
        bestStreak = max(bestStreak, newStreak)
        totalWins += 1
        if (totalWins >= 10) {
            val isNew = prefs.unlockBadge("math_10")
            if (isNew) {
                val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            com.brightnest.app.data.FirestoreRepository.saveKidsProgress(
                                uid,
                                com.brightnest.app.data.KidsProgress(
                                    coins = prefs.coins,
                                    stars = prefs.stars,
                                    streak = prefs.streak,
                                    badges = prefs.badges.toList()
                                )
                            )
                        } catch (e: Exception) {
                            android.util.Log.w("MathFragment", "Badge sync failed", e)
                        }
                    }
                }
            }
        }
        feedback = "correct"
        showGameplay()
        handler.postDelayed({ if (isAdded) generateProblem() }, 700)
    }

    private fun handleWrong(timedOut: Boolean = false) {
        streak = 0
        feedback = "wrong"
        lives -= 1
        showGameplay()
        if (lives <= 0) {
            handler.postDelayed({ if (isAdded) { gameOver = true; showGameOver() } }, 900)
        } else {
            handler.postDelayed({ if (isAdded) generateProblem() }, if (timedOut) 900 else 1100)
        }
    }

    private fun handleAnswer(value: Int, idx: Int) {
        if (feedback != null) return
        pickedIdx = idx
        timerRunnable?.let { handler.removeCallbacks(it) }
        if (value == correctAnswer()) handleCorrect() else handleWrong()
    }

    private fun restart() {
        score = 0; streak = 0; bestStreak = 0; lives = 3
        gameOver = false; questionNum = 0; totalWins = 0
        feedback = null; pickedIdx = null
        generateProblem()
    }

    private fun header(title: String, urdu: String?, onBack: () -> Unit, rightView: View? = null): LinearLayout {
        val ctx = requireContext()
        val h = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
        }
        h.addView(TextView(ctx).apply {
            text = "←"; textSize = 24f; gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(ctx, R.color.foreground))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40))
            isClickable = true; setOnClickListener { onBack() }
        })
        val center = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        center.addView(TextView(ctx).apply {
            text = title; textSize = 22f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(ctx, R.color.primary))
        })
        if (urdu != null) center.addView(TextView(ctx).apply {
            text = urdu; textSize = 13f
            setTextColor(ContextCompat.getColor(ctx, R.color.muted_foreground))
        })
        h.addView(center)
        if (rightView != null) h.addView(rightView)
        else h.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40)) })
        return h
    }

    private fun rootColumn(): LinearLayout {
        val ctx = requireContext()
        binding.mathRoot.removeAllViews()
        timerFill = null; timerEmpty = null; timerText = null
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            binding.mathRoot.addView(this)
        }
    }

    private fun showPicker() {
        val ctx = requireContext()
        val col = rootColumn()
        col.addView(header("Math Quiz", "ریاضی کوئز", { findNavController().navigateUp() }))

        val scroll = ScrollView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 40))
        }
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val mutedFg = ContextCompat.getColor(ctx, R.color.muted_foreground)

        content.addView(LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20))
            background = GameUi.rounded(GameUi.withAlpha(primary, 18), GameUi.dpf(ctx, 20), GameUi.withAlpha(primary, 48), GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 20) }
            addView(TextView(ctx).apply { text = "⚡"; textSize = 28f })
            addView(TextView(ctx).apply {
                text = "Choose your level"; textSize = 22f
                setTypeface(typeface, Typeface.BOLD); setTextColor(foreground)
                gravity = Gravity.CENTER; setPadding(0, GameUi.dp(ctx, 12), 0, 0)
            })
            addView(TextView(ctx).apply {
                text = "Answer fast for bigger streaks and bonus coins!"; textSize = 12f
                setTextColor(mutedFg); gravity = Gravity.CENTER; setPadding(0, GameUi.dp(ctx, 6), 0, 0)
            })
        })

        for (d in diffs) {
            val dColor = Color.parseColor(d.color)
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
                background = GameUi.rounded(ContextCompat.getColor(ctx, R.color.card), GameUi.dpf(ctx, 18), dColor, GameUi.dp(ctx, 2))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) }
                isClickable = true
                setOnClickListener { difficulty = d; restart() }
            }
            card.addView(TextView(ctx).apply {
                text = d.ops.joinToString(" "); setTextColor(Color.WHITE); textSize = 16f
                setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 14), GameUi.dp(ctx, 10), GameUi.dp(ctx, 14), GameUi.dp(ctx, 10))
                background = GameUi.rounded(dColor, GameUi.dpf(ctx, 12))
                minWidth = GameUi.dp(ctx, 70)
            })
            val mid = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = GameUi.dp(ctx, 16) }
            }
            mid.addView(TextView(ctx).apply { text = d.label; textSize = 17f; setTypeface(typeface, Typeface.BOLD); setTextColor(foreground) })
            mid.addView(TextView(ctx).apply { text = d.urdu; textSize = 13f; setTextColor(mutedFg) })
            mid.addView(TextView(ctx).apply { text = "up to ${d.maxN}   •   ${d.time}s"; textSize = 12f; setTextColor(mutedFg); setPadding(0, GameUi.dp(ctx, 6), 0, 0) })
            card.addView(mid)
            card.addView(TextView(ctx).apply { text = "›"; textSize = 22f; setTextColor(dColor) })
            content.addView(card)
        }

        content.addView(LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            background = GameUi.rounded(ContextCompat.getColor(ctx, R.color.muted), GameUi.dpf(ctx, 14))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 16) }
            addView(TextView(ctx).apply { text = "How to play"; setTypeface(typeface, Typeface.BOLD); setTextColor(foreground); setPadding(0, 0, 0, GameUi.dp(ctx, 8)) })
            addView(TextView(ctx).apply {
                text = "• +2 coins per correct answer, bonus for streaks\n• +1 star every 5-in-a-row\n• Beware: 3 lives, and the clock is ticking!"
                textSize = 13f; setTextColor(mutedFg); setLineSpacing(GameUi.dpf(ctx, 4), 1f)
            })
        })

        scroll.addView(content)
        col.addView(scroll)
    }

    private fun showGameOver() {
        val ctx = requireContext()
        val col = rootColumn()
        col.addView(header("Game Over", null, { difficulty = null; showPicker() }))
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val mutedFg = ContextCompat.getColor(ctx, R.color.muted_foreground)

        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        content.addView(TextView(ctx).apply {
            text = "🏅"; textSize = 56f; gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(primary, 21), GameUi.dpf(ctx, 60))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 120), GameUi.dp(ctx, 120))
        })
        content.addView(TextView(ctx).apply {
            text = "Masha'Allah!"; textSize = 28f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(foreground); gravity = Gravity.CENTER; setPadding(0, GameUi.dp(ctx, 24), 0, 0)
        })
        content.addView(TextView(ctx).apply {
            text = "ماشاءاللہ"; textSize = 14f; setTextColor(mutedFg); gravity = Gravity.CENTER; setPadding(0, GameUi.dp(ctx, 6), 0, 0)
        })

        val statsGrid = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 28) }
        }
        fun stat(label: String, value: String): LinearLayout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 14), GameUi.dp(ctx, 14), GameUi.dp(ctx, 14), GameUi.dp(ctx, 14))
            background = GameUi.rounded(ContextCompat.getColor(ctx, R.color.card), GameUi.dpf(ctx, 14), ContextCompat.getColor(ctx, R.color.border), GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = GameUi.dp(ctx, 10) }
            addView(TextView(ctx).apply { text = label; textSize = 12f; setTextColor(mutedFg) })
            addView(TextView(ctx).apply { text = value; textSize = 22f; setTypeface(typeface, Typeface.BOLD); setTextColor(primary) })
        }
        statsGrid.addView(stat("SCORE", score.toString()))
        statsGrid.addView(stat("BEST STREAK", bestStreak.toString()))
        statsGrid.addView(stat("QUESTIONS", questionNum.toString()).apply {
            (layoutParams as LinearLayout.LayoutParams).marginEnd = 0
        })
        content.addView(statsGrid)

        content.addView(TextView(ctx).apply {
            text = "↻  Play Again"; textSize = 16f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE); gravity = Gravity.CENTER
            setPadding(0, GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16))
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 32) }
            isClickable = true; setOnClickListener { restart() }
        })
        content.addView(TextView(ctx).apply {
            text = "Change Difficulty"; textSize = 16f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(foreground); gravity = Gravity.CENTER
            setPadding(0, GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16))
            background = GameUi.rounded(Color.TRANSPARENT, GameUi.dpf(ctx, 16), ContextCompat.getColor(ctx, R.color.border), GameUi.dp(ctx, 2))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 12) }
            isClickable = true; setOnClickListener { difficulty = null; showPicker() }
        })
        col.addView(content)
    }

    private fun showGameplay() {
        val cfg = difficulty ?: return
        val ctx = requireContext()
        val col = rootColumn()
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val mutedFg = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedCol = ContextCompat.getColor(ctx, R.color.muted)

        // header: back, hearts, score pill
        val hearts = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        for (i in 0 until 3) hearts.addView(TextView(ctx).apply {
            text = if (i < lives) "❤️" else "🤍"; textSize = 16f
            setPadding(GameUi.dp(ctx, 3), 0, GameUi.dp(ctx, 3), 0)
        })
        val scorePill = TextView(ctx).apply {
            text = "⭐ $score"; textSize = 14f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(foreground)
            setPadding(GameUi.dp(ctx, 14), GameUi.dp(ctx, 6), GameUi.dp(ctx, 14), GameUi.dp(ctx, 6))
            background = GameUi.rounded(GameUi.withAlpha(Color.BLACK, 13), GameUi.dpf(ctx, 20))
        }
        val h = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
        }
        h.addView(TextView(ctx).apply {
            text = "←"; textSize = 24f; gravity = Gravity.CENTER
            setTextColor(foreground)
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40))
            isClickable = true; setOnClickListener { difficulty = null; showPicker() }
        })
        h.addView(hearts, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { gravity = Gravity.CENTER })
        h.addView(scorePill)
        col.addView(h)

        // streak bar
        val streakBar = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 12), GameUi.dp(ctx, 8), GameUi.dp(ctx, 12), GameUi.dp(ctx, 8))
            background = GameUi.rounded(ContextCompat.getColor(ctx, R.color.card), GameUi.dpf(ctx, 12), ContextCompat.getColor(ctx, R.color.border), GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(GameUi.dp(ctx, 16), GameUi.dp(ctx, 10), GameUi.dp(ctx, 16), 0)
            }
        }
        streakBar.addView(TextView(ctx).apply {
            text = "Streak: $streak" + if (streak >= 3) "  🔥 HOT" else ""
            textSize = 13f; setTypeface(typeface, Typeface.BOLD); setTextColor(foreground)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        streakBar.addView(TextView(ctx).apply { text = "Q $questionNum"; textSize = 12f; setTextColor(mutedFg) })
        col.addView(streakBar)

        // timer
        val timerWrap = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(GameUi.dp(ctx, 16), GameUi.dp(ctx, 10), GameUi.dp(ctx, 16), 0)
            }
        }
        val track = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GameUi.rounded(mutedCol, GameUi.dpf(ctx, 4))
            layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 8), 1f)
        }
        val fill = View(ctx).apply {
            setBackgroundColor(primary)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, timeLeft.toFloat() / cfg.time)
        }
        val emptySpace = View(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, max(0f, 1f - timeLeft.toFloat() / cfg.time))
        }
        track.addView(fill); track.addView(emptySpace)
        timerFill = fill
        timerEmpty = emptySpace
        timerWrap.addView(track)
        val tText = TextView(ctx).apply {
            text = "${timeLeft}s"; textSize = 12f; setTypeface(typeface, Typeface.BOLD); setTextColor(primary)
            setPadding(GameUi.dp(ctx, 10), 0, 0, 0); minWidth = GameUi.dp(ctx, 28)
        }
        timerText = tText
        timerWrap.addView(tText)
        col.addView(timerWrap)

        // content: problem + options
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 40))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val probBg = when (feedback) {
            "correct" -> GameUi.withAlpha(Color.parseColor("#22C55E"), 32)
            "wrong" -> GameUi.withAlpha(Color.parseColor("#EF4444"), 32)
            else -> GameUi.withAlpha(primary, 21)
        }
        val probBorder = when (feedback) {
            "correct" -> Color.parseColor("#22C55E")
            "wrong" -> Color.parseColor("#EF4444")
            else -> primary
        }
        val probFg = when (feedback) {
            "correct" -> Color.parseColor("#15803D")
            "wrong" -> Color.parseColor("#B91C1C")
            else -> primary
        }
        val probBox = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(0, GameUi.dp(ctx, 32), 0, GameUi.dp(ctx, 32))
            background = GameUi.rounded(probBg, GameUi.dpf(ctx, 28), probBorder, GameUi.dp(ctx, 2))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 32) }
        }
        probBox.addView(TextView(ctx).apply { text = "$num1 $op $num2"; textSize = 56f; setTypeface(typeface, Typeface.BOLD); setTextColor(probFg) })
        probBox.addView(TextView(ctx).apply { text = "= ?"; textSize = 36f; setTextColor(mutedFg) })
        content.addView(probBox)

        val grid = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) }
        var row: LinearLayout? = null
        options.forEachIndexed { i, opt ->
            if (i % 2 == 0) {
                row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 14) }
                }
                grid.addView(row)
            }
            val isPicked = pickedIdx == i
            val isAnswer = feedback != null && opt == correctAnswer()
            val bg = when {
                isAnswer -> Color.parseColor("#22C55E")
                isPicked && feedback == "wrong" -> Color.parseColor("#EF4444")
                else -> ContextCompat.getColor(ctx, R.color.card)
            }
            val fg = if (isAnswer || (isPicked && feedback == "wrong")) Color.WHITE else foreground
            val border = when {
                isAnswer -> Color.parseColor("#22C55E")
                isPicked && feedback == "wrong" -> Color.parseColor("#EF4444")
                else -> ContextCompat.getColor(ctx, R.color.border)
            }
            val btn = TextView(ctx).apply {
                text = opt.toString(); textSize = 32f; setTypeface(typeface, Typeface.BOLD)
                setTextColor(fg); gravity = Gravity.CENTER
                background = GameUi.rounded(bg, GameUi.dpf(ctx, 20), border, GameUi.dp(ctx, 2))
                layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 90), 1f).apply {
                    if (i % 2 == 0) marginEnd = GameUi.dp(ctx, 14)
                }
                isClickable = feedback == null
                setOnClickListener { handleAnswer(opt, i) }
            }
            row?.addView(btn)
        }
        content.addView(grid)
        col.addView(content)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
