package com.brightnest.app.ui

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.AdultStore
import com.brightnest.app.Goal
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding

class GoalsFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var store: AdultStore
    private lateinit var listContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        store = AdultStore(requireContext())
        binding.title.text = "Goals"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnAction.apply {
            text = "+"; setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            setOnClickListener { showAddModal() }
        }
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        listContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(requireContext(), 16), GameUi.dp(requireContext(), 16), GameUi.dp(requireContext(), 16), GameUi.dp(requireContext(), 48))
        }
        scroll.addView(listContainer)
        binding.toolRoot.addView(scroll)
        renderList()
    }

    private fun renderList() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedC = ContextCompat.getColor(ctx, R.color.muted)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val primaryFg = ContextCompat.getColor(ctx, R.color.on_primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)
        listContainer.removeAllViews()

        val goals = store.goals
        if (goals.isEmpty()) {
            val empty = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 40) }
            }
            empty.addView(TextView(ctx).apply {
                text = "🎯"; textSize = 28f; gravity = Gravity.CENTER
                background = GameUi.rounded(mutedC, GameUi.dpf(ctx, 40))
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 80), GameUi.dp(ctx, 80))
            })
            empty.addView(TextView(ctx).apply { text = "No goals set yet."; setTextColor(muted); textSize = 16f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 16) } })
            empty.addView(TextView(ctx).apply {
                text = "Set a Goal"; setTextColor(primaryFg); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
                setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 12), GameUi.dp(ctx, 24), GameUi.dp(ctx, 12))
                background = GameUi.rounded(primary, GameUi.dpf(ctx, 12))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 24) }
                setOnClickListener { showAddModal() }
            })
            listContainer.addView(empty)
            return
        }

        goals.forEach { goal ->
            val cardView = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20))
                background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) }
                setOnClickListener { showEditModal(goal) }
                isClickable = true
            }
            val headerRow = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 4) }
            }
            headerRow.addView(TextView(ctx).apply { text = goal.name; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
            headerRow.addView(TextView(ctx).apply { text = "${goal.progress}%"; setTextColor(primary); textSize = 16f; setTypeface(typeface, Typeface.BOLD) })
            cardView.addView(headerRow)
            cardView.addView(TextView(ctx).apply { text = "Target: ${goal.targetDate}"; setTextColor(muted); textSize = 13f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) } })
            cardView.addView(progressBar(ctx, goal.progress, primary, mutedC))
            listContainer.addView(cardView)
        }
    }

    private fun progressBar(ctx: android.content.Context, progress: Int, fill: Int, track: Int): View {
        val barBg = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GameUi.rounded(track, GameUi.dpf(ctx, 4))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 8))
            clipToOutline = true
        }
        val clamped = progress.coerceIn(0, 100)
        if (clamped > 0) {
            barBg.addView(View(ctx).apply {
                background = GameUi.rounded(fill, GameUi.dpf(ctx, 4))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, clamped.toFloat())
            })
        }
        if (clamped < 100) {
            barBg.addView(View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, (100 - clamped).toFloat())
            })
        }
        return barBg
    }

    private fun bottomSheet(ctx: android.content.Context, bg: Int): Pair<FrameLayout, LinearLayout> {
        val overlay = FrameLayout(ctx).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setBackgroundColor(0x80000000.toInt())
            isClickable = true
        }
        val sheet = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply { setColor(bg); cornerRadii = floatArrayOf(GameUi.dpf(ctx, 32), GameUi.dpf(ctx, 32), GameUi.dpf(ctx, 32), GameUi.dpf(ctx, 32), 0f, 0f, 0f, 0f) }
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
        }
        overlay.addView(sheet)
        overlay.setOnClickListener { binding.toolRoot.removeView(overlay) }
        sheet.setOnClickListener { }
        return overlay to sheet
    }

    private fun modalHeader(ctx: android.content.Context, titleText: String, fg: Int, onClose: () -> Unit): View {
        val header = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), 0)
        }
        header.addView(TextView(ctx).apply { text = titleText; setTextColor(fg); textSize = 20f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        header.addView(TextView(ctx).apply { text = "✕"; textSize = 22f; setTextColor(fg); setOnClickListener { onClose() } })
        return header
    }

    private fun showAddModal() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val primaryFg = ContextCompat.getColor(ctx, R.color.on_primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val bg = ContextCompat.getColor(ctx, R.color.background)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val (overlay, sheet) = bottomSheet(ctx, bg)
        sheet.addView(modalHeader(ctx, "New Goal", fg) { binding.toolRoot.removeView(overlay) })
        val body = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
        }
        val nameInput = EditText(ctx).apply {
            hint = "Goal Name (e.g. Read 12 books)"; setHintTextColor(muted); setTextColor(fg); textSize = 16f
            background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            setPadding(GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16), 0)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 56)).apply { bottomMargin = GameUi.dp(ctx, 16) }
        }
        val dateInput = EditText(ctx).apply {
            hint = "Target Date (e.g. Dec 2025)"; setHintTextColor(muted); setTextColor(fg); textSize = 16f
            background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            setPadding(GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16), 0)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 56))
        }
        body.addView(nameInput)
        body.addView(dateInput)
        body.addView(TextView(ctx).apply {
            text = "Create Goal"; setTextColor(primaryFg); textSize = 16f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 56)).apply { topMargin = GameUi.dp(ctx, 16) }
            setOnClickListener {
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) return@setOnClickListener
                val date = dateInput.text.toString().trim().ifEmpty { "Ongoing" }
                store.addGoal(Goal(System.currentTimeMillis().toString(), name, date, 0))
                binding.toolRoot.removeView(overlay)
                renderList()
            }
        })
        sheet.addView(body)
        binding.toolRoot.addView(overlay)
    }

    private fun showEditModal(goal: Goal) {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedC = ContextCompat.getColor(ctx, R.color.muted)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val primaryFg = ContextCompat.getColor(ctx, R.color.on_primary)
        val bg = ContextCompat.getColor(ctx, R.color.background)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)

        var editProgress = goal.progress
        val (overlay, sheet) = bottomSheet(ctx, bg)
        sheet.addView(modalHeader(ctx, "Update Progress", fg) { binding.toolRoot.removeView(overlay) })
        val body = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
        }
        body.addView(TextView(ctx).apply { text = goal.name; setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 8) } })
        body.addView(TextView(ctx).apply { text = "Target: ${goal.targetDate}"; setTextColor(muted); textSize = 16f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 32) } })
        val pctText = TextView(ctx).apply { text = "$editProgress%"; setTextColor(primary); textSize = 32f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) } }
        body.addView(pctText)

        // tappable track (10 segments)
        val trackFill = View(ctx).apply { background = GameUi.rounded(primary, GameUi.dpf(ctx, 20)) }
        val emptyFill = View(ctx)
        val trackBg = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GameUi.rounded(mutedC, GameUi.dpf(ctx, 20))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 40))
        }
        fun applyFill() {
            trackBg.removeAllViews()
            val clamped = editProgress.coerceIn(0, 100)
            if (clamped > 0) trackBg.addView(trackFill.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, clamped.toFloat()) })
            if (clamped < 100) trackBg.addView(emptyFill.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, (100 - clamped).toFloat()) })
        }
        applyFill()
        val tapRow = FrameLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 40))
        }
        tapRow.addView(trackBg)
        val segRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        for (i in 0 until 10) {
            segRow.addView(View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                setOnClickListener { editProgress = (i + 1) * 10; pctText.text = "$editProgress%"; applyFill() }
            })
        }
        tapRow.addView(segRow)
        body.addView(tapRow)
        body.addView(TextView(ctx).apply { text = "Tap track to adjust (increments of 10%)"; setTextColor(muted); textSize = 13f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 8); bottomMargin = GameUi.dp(ctx, 32) } })

        val btnRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        btnRow.addView(TextView(ctx).apply {
            text = "Delete"; setTextColor(destructive); textSize = 16f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(destructive, 0x20), GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 56), 1f).apply { marginEnd = GameUi.dp(ctx, 16) }
            setOnClickListener { store.deleteGoal(goal.id); binding.toolRoot.removeView(overlay); renderList() }
        })
        btnRow.addView(TextView(ctx).apply {
            text = "Save Progress"; setTextColor(primaryFg); textSize = 16f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 56), 2f)
            setOnClickListener { store.updateGoal(goal.id, editProgress); binding.toolRoot.removeView(overlay); renderList() }
        })
        body.addView(btnRow)
        sheet.addView(body)
        binding.toolRoot.addView(overlay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
