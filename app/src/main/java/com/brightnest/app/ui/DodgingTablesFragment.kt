package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
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
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentDodgingTablesBinding
import java.util.Locale

class DodgingTablesFragment : Fragment() {

    private var _binding: FragmentDodgingTablesBinding? = null
    private val binding get() = _binding!!

    private data class Q(val a: Int, val b: Int, val answer: Int, val options: List<Int>)

    private val palette = listOf(
        "#EF4444", "#F97316", "#F59E0B", "#84CC16", "#22C55E",
        "#06B6D4", "#3B82F6", "#8B5CF6", "#EC4899", "#14B8A6"
    )
    private val tables = (1..50).toList()
    private val roundSize = 10

    private var table: Int? = null            // null = none chosen
    private var mixed = false
    private var picking = false
    private var questions = listOf<Q>()
    private var index = 0
    private var score = 0
    private var picked: Int? = null
    private var finished = false
    private val chosen = mutableListOf<Int>()

    private val handler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDodgingTablesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                com.brightnest.app.AppLanguageHelper.configureTts(tts, com.brightnest.app.Prefs(requireContext()).language)
                tts?.setSpeechRate(0.9f)
            }
        }
        render()
    }

    private fun speak(t: String) {
        tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "dt")
    }

    private fun shuffleInts(arr: List<Int>): List<Int> = arr.shuffled()

    private fun makeOptions(answer: Int, a: Int, b: Int): List<Int> {
        val set = linkedSetOf(answer)
        val candidates = listOf(
            answer + 1, answer - 1, answer + a, maxOf(0, answer - a),
            a * (b + 1), a * maxOf(1, b - 1), answer + 2, answer + 10
        )
        for (c in shuffleInts(candidates)) {
            if (c > 0 && !set.contains(c)) set.add(c)
            if (set.size >= 4) break
        }
        while (set.size < 4) set.add(answer + set.size)
        return shuffleInts(set.toList())
    }

    private fun genMixed(pool: List<Int>): List<Q> {
        val t = if (pool.isNotEmpty()) pool else tables
        return (0 until roundSize).map {
            val a = t.random()
            val b = (1..10).random()
            Q(a, b, a * b, makeOptions(a * b, a, b))
        }
    }

    private fun genTable(tbl: Int): List<Q> = (1..10).map { b ->
        Q(tbl, b, tbl * b, makeOptions(tbl * b, tbl, b))
    }

    private fun start(tbl: Int?, isMixed: Boolean) {
        table = tbl
        mixed = isMixed
        picking = false
        questions = if (isMixed) genMixed(chosen) else genTable(tbl!!)
        index = 0; score = 0; picked = null; finished = false
        render()
    }

    private fun onAnswer(opt: Int) {
        if (picked != null) return
        val correct = questions[index].answer
        val ok = opt == correct
        picked = opt
        if (ok) { score += 1; speak("Correct") } else speak("Try again")
        render()
        handler.postDelayed({
            if (!isAdded) return@postDelayed
            if (index + 1 >= questions.size) { finished = true }
            else { index += 1; picked = null }
            render()
        }, 900)
    }

    private fun isPickerNone() = table == null && !mixed && !picking
    private fun isMixedSelect() = table == null && !mixed && picking

    private fun render() {
        when {
            finished -> renderResults()
            isMixedSelect() -> renderMixedSelect()
            table == null && !mixed -> renderPicker()
            else -> renderQuiz()
        }
    }

    private fun root(): LinearLayout {
        val ctx = requireContext()
        binding.dodgeRoot.removeAllViews()
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            binding.dodgeRoot.addView(this)
        }
    }

    private fun header(title: String, subtitle: String, onBack: () -> Unit): LinearLayout {
        val ctx = requireContext()
        val h = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
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
            text = title; textSize = 26f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(ctx, R.color.coral))
        })
        center.addView(TextView(ctx).apply {
            text = subtitle; textSize = 13f
            setTextColor(ContextCompat.getColor(ctx, R.color.muted_foreground))
        })
        h.addView(center)
        h.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40)) })
        return h
    }

    private fun tableGrid(ctx: android.content.Context, onTile: (Int) -> Unit, selected: ((Int) -> Boolean)? = null): LinearLayout {
        val grid = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL }
        val cols = 5
        val tileW = (resources.displayMetrics.widthPixels - GameUi.dp(ctx, 32) - GameUi.dp(ctx, 10) * cols) / cols
        var row: LinearLayout? = null
        tables.forEachIndexed { i, n ->
            if (i % cols == 0) {
                row = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
                grid.addView(row)
            }
            val color = Color.parseColor(palette[(n - 1) % palette.size])
            val sel = selected?.invoke(n) ?: false
            val tile = TextView(ctx).apply {
                text = n.toString(); textSize = 17f; setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
                setTextColor(if (sel) Color.WHITE else color)
                background = GameUi.rounded(if (sel) color else GameUi.withAlpha(color, 24), GameUi.dpf(ctx, 14), color, GameUi.dp(ctx, 2))
                layoutParams = LinearLayout.LayoutParams(tileW, tileW).apply {
                    setMargins(GameUi.dp(ctx, 5), GameUi.dp(ctx, 5), GameUi.dp(ctx, 5), GameUi.dp(ctx, 5))
                }
                isClickable = true; setOnClickListener { onTile(n) }
            }
            row?.addView(tile)
        }
        return grid
    }

    private fun renderPicker() {
        val ctx = requireContext()
        val col = root()
        col.addView(header("Dodging Tables", "ڈاجنگ ٹیبل") { findNavController().navigateUp() })
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val mutedFg = ContextCompat.getColor(ctx, R.color.muted_foreground)

        val scroll = ScrollView(ctx).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
        }
        content.addView(TextView(ctx).apply {
            text = "Pick a table to practice · مشق کے لیے پہاڑا چنیں"
            textSize = 14f; setTextColor(mutedFg); gravity = Gravity.CENTER
            setPadding(0, 0, 0, GameUi.dp(ctx, 16))
        })
        content.addView(LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 18), GameUi.dp(ctx, 18), GameUi.dp(ctx, 18), GameUi.dp(ctx, 18))
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) }
            isClickable = true; setOnClickListener { picking = true; render() }
            addView(TextView(ctx).apply { text = "Mixed · ملا جلا"; textSize = 22f; setTypeface(typeface, Typeface.BOLD); setTextColor(Color.WHITE) })
            addView(TextView(ctx).apply { text = "Choose which tables to mix"; textSize = 12f; setTextColor(Color.WHITE); alpha = 0.85f })
        })
        content.addView(tableGrid(ctx, { start(it, false) }))
        scroll.addView(content)
        col.addView(scroll)
    }

    private fun renderMixedSelect() {
        val ctx = requireContext()
        val col = root()
        col.addView(header("Mixed", "ملا جلا") { picking = false; render() })
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val mutedFg = ContextCompat.getColor(ctx, R.color.muted_foreground)

        val topRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16), GameUi.dp(ctx, 8))
        }
        topRow.addView(TextView(ctx).apply {
            text = "Tap tables to include · ٹیبل چنیں"; textSize = 13f; setTextColor(mutedFg)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        topRow.addView(TextView(ctx).apply {
            text = if (chosen.size == tables.size) "Clear all" else "Select all"
            textSize = 14f; setTypeface(typeface, Typeface.BOLD); setTextColor(primary)
            isClickable = true
            setOnClickListener {
                if (chosen.size == tables.size) chosen.clear() else { chosen.clear(); chosen.addAll(tables) }
                render()
            }
        })
        col.addView(topRow)

        val scroll = ScrollView(ctx).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 100))
        }
        content.addView(tableGrid(ctx, { n ->
            if (chosen.contains(n)) chosen.remove(n) else chosen.add(n)
            render()
        }, { chosen.contains(it) }))
        scroll.addView(content)
        col.addView(scroll)

        val enabled = chosen.isNotEmpty()
        col.addView(TextView(ctx).apply {
            text = "Start${if (enabled) " (${chosen.size})" else ""} · شروع"
            textSize = 20f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            setTextColor(if (enabled) Color.WHITE else mutedFg)
            setPadding(0, GameUi.dp(ctx, 18), 0, GameUi.dp(ctx, 18))
            background = GameUi.rounded(if (enabled) primary else ContextCompat.getColor(ctx, R.color.muted), GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            }
            isClickable = enabled
            setOnClickListener { if (enabled) start(null, true) }
        })
    }

    private fun renderResults() {
        val ctx = requireContext()
        val col = root()
        col.addView(header("Result", "نتیجہ") { resetToPicker() })
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)

        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        content.addView(TextView(ctx).apply {
            text = "$score/${questions.size}"; textSize = 36f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(primary); gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(primary, 32), GameUi.dpf(ctx, 60))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 120), GameUi.dp(ctx, 120))
        })
        val msg = when {
            score == questions.size -> "Perfect! شاباش"
            score >= questions.size / 2 -> "Good job! بہت اچھے"
            else -> "Keep practicing! مشق کرتے رہیں"
        }
        content.addView(TextView(ctx).apply {
            text = msg; textSize = 22f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(foreground); gravity = Gravity.CENTER
            setPadding(0, GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16))
        })
        content.addView(TextView(ctx).apply {
            text = "Play Again · دوبارہ"; textSize = 15f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE); gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 32), GameUi.dp(ctx, 14), GameUi.dp(ctx, 32), GameUi.dp(ctx, 14))
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 14))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 8) }
            isClickable = true; setOnClickListener { start(table, mixed) }
        })
        content.addView(TextView(ctx).apply {
            text = "Choose Table"; textSize = 15f; setTypeface(typeface, Typeface.BOLD)
            setTextColor(foreground); gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 32), GameUi.dp(ctx, 14), GameUi.dp(ctx, 32), GameUi.dp(ctx, 14))
            background = GameUi.rounded(Color.TRANSPARENT, GameUi.dpf(ctx, 14), ContextCompat.getColor(ctx, R.color.border), GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 12) }
            isClickable = true; setOnClickListener { resetToPicker() }
        })
        col.addView(content)
    }

    private fun resetToPicker() {
        table = null; mixed = false; picking = false; finished = false
        index = 0; score = 0; picked = null
        render()
    }

    private fun renderQuiz() {
        val ctx = requireContext()
        val col = root()
        val q = questions[index]
        val title = if (mixed) "Mixed" else "Table $table"
        col.addView(header(title, "Question ${index + 1} of ${questions.size}") { resetToPicker() })
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val mutedFg = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        // progress bar
        val track = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GameUi.rounded(border, GameUi.dpf(ctx, 3))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 6)).apply { bottomMargin = GameUi.dp(ctx, 24) }
        }
        val frac = index.toFloat() / questions.size
        track.addView(View(ctx).apply {
            setBackgroundColor(primary)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, frac)
        })
        track.addView(View(ctx).apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f - frac) })
        content.addView(track)

        content.addView(LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 32) }
            addView(TextView(ctx).apply { text = "${q.a} × ${q.b}"; textSize = 48f; setTypeface(typeface, Typeface.BOLD); setTextColor(foreground) })
            addView(TextView(ctx).apply { text = "= ?"; textSize = 48f; setTypeface(typeface, Typeface.BOLD); setTextColor(mutedFg) })
        })

        val cardColor = ContextCompat.getColor(ctx, R.color.card)
        for (opt in q.options) {
            val isPicked = picked == opt
            val isCorrect = opt == q.answer
            var bg = cardColor; var bd = border
            if (picked != null) {
                if (isCorrect) { bg = GameUi.withAlpha(Color.parseColor("#22C55E"), 34); bd = Color.parseColor("#22C55E") }
                else if (isPicked) { bg = GameUi.withAlpha(Color.parseColor("#EF4444"), 34); bd = Color.parseColor("#EF4444") }
            }
            content.addView(TextView(ctx).apply {
                text = opt.toString(); textSize = 22f; setTypeface(typeface, Typeface.BOLD)
                setTextColor(foreground); gravity = Gravity.CENTER
                setPadding(0, GameUi.dp(ctx, 18), 0, GameUi.dp(ctx, 18))
                background = GameUi.rounded(bg, GameUi.dpf(ctx, 16), bd, GameUi.dp(ctx, 2))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) }
                isClickable = true; setOnClickListener { onAnswer(opt) }
            })
        }
        col.addView(content)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop(); tts?.shutdown(); tts = null
    }
}
