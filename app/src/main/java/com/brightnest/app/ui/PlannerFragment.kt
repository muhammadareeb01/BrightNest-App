package com.brightnest.app.ui

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.AdultStore
import com.brightnest.app.R
import com.brightnest.app.Task
import com.brightnest.app.databinding.FragmentAdultToolBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlannerFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var store: AdultStore
    private lateinit var listContainer: LinearLayout
    private lateinit var input: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        store = AdultStore(requireContext())
        binding.title.text = "Daily Planner"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        build()
    }

    private fun build() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val primaryFg = ContextCompat.getColor(ctx, R.color.on_primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val bg = ContextCompat.getColor(ctx, R.color.background)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val main = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        // date header
        val today = SimpleDateFormat("EEEE, MMMM d", Locale.US).format(Date())
        val dateHeader = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 16))
        }
        dateHeader.addView(TextView(ctx).apply { text = today; setTextColor(primary); textSize = 24f; setTypeface(typeface, Typeface.BOLD) })
        dateHeader.addView(TextView(ctx).apply { text = "What are your priorities today?"; setTextColor(muted); textSize = 16f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 4) } })
        main.addView(dateHeader)

        // list
        val scroll = ScrollView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        listContainer = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
        }
        scroll.addView(listContainer)
        main.addView(scroll)

        // input bar
        val inputBar = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            setBackgroundColor(card)
        }
        input = EditText(ctx).apply {
            hint = "Add a new task..."
            setHintTextColor(muted)
            setTextColor(fg)
            textSize = 16f
            setPadding(GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16), 0)
            background = GameUi.rounded(bg, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE
            layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 56), 1f).apply { marginEnd = GameUi.dp(ctx, 12) }
            setOnEditorActionListener { _, _, _ -> addTask(); true }
        }
        inputBar.addView(input)
        inputBar.addView(TextView(ctx).apply {
            text = "+"; textSize = 24f; setTextColor(primaryFg); gravity = Gravity.CENTER
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 56), GameUi.dp(ctx, 56))
            setOnClickListener { addTask() }
        })
        main.addView(inputBar)

        binding.toolRoot.addView(main)
        renderList()
    }

    private fun addTask() {
        val title = input.text.toString().trim()
        if (title.isEmpty()) return
        store.addTask(Task(System.currentTimeMillis().toString(), title, System.currentTimeMillis().toString(), false))
        input.setText("")
        renderList()
    }

    private fun renderList() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)
        listContainer.removeAllViews()

        val tasks = store.tasks
        if (tasks.isEmpty()) {
            val empty = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 40) }
            }
            empty.addView(TextView(ctx).apply { text = "✓"; textSize = 48f; setTextColor(muted); gravity = Gravity.CENTER })
            empty.addView(TextView(ctx).apply { text = "Your day is clear."; setTextColor(muted); textSize = 16f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 16) } })
            listContainer.addView(empty)
            return
        }

        tasks.forEach { task ->
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
                background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) }
            }
            row.addView(TextView(ctx).apply {
                text = if (task.done) "☑" else "☐"
                textSize = 22f
                setTextColor(if (task.done) primary else muted)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = GameUi.dp(ctx, 16) }
                setOnClickListener { store.toggleTask(task.id); renderList() }
            })
            row.addView(TextView(ctx).apply {
                text = task.title
                textSize = 16f
                setTextColor(if (task.done) muted else fg)
                if (task.done) paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            row.addView(TextView(ctx).apply {
                text = "🗑️"; textSize = 16f; setTextColor(destructive)
                setPadding(GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8))
                setOnClickListener { store.deleteTask(task.id); renderList() }
            })
            listContainer.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
