package com.brightnest.app.ui

import android.graphics.Color
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
import com.brightnest.app.Habit
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitsFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var store: AdultStore
    private lateinit var listContainer: LinearLayout

    private val palette = listOf("#FFB067", "#E88B7D", "#8E9D84", "#9C8BA7", "#3B82F6", "#F59E0B", "#10B981", "#8B5CF6")
    private var newHabitColor = palette[0]

    private data class Day(val iso: String, val label: String)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        store = AdultStore(requireContext())
        binding.title.text = "Habit Tracker"
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

    private fun last7Days(): List<Day> {
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFmt = SimpleDateFormat("EEE", Locale.US)
        val days = mutableListOf<Day>()
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            days.add(Day(isoFmt.format(cal.time), dayFmt.format(cal.time).take(1)))
        }
        return days
    }

    private fun renderList() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedC = ContextCompat.getColor(ctx, R.color.muted)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val primaryFg = ContextCompat.getColor(ctx, R.color.on_primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val bg = ContextCompat.getColor(ctx, R.color.background)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)
        listContainer.removeAllViews()

        val habits = store.habits
        if (habits.isEmpty()) {
            val empty = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 40) }
            }
            empty.addView(TextView(ctx).apply {
                text = "✓"; textSize = 28f; setTextColor(muted); gravity = Gravity.CENTER
                background = GameUi.rounded(mutedC, GameUi.dpf(ctx, 40))
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 80), GameUi.dp(ctx, 80))
            })
            empty.addView(TextView(ctx).apply { text = "No habits yet."; setTextColor(muted); textSize = 16f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 16) } })
            empty.addView(TextView(ctx).apply {
                text = "Add a Habit"; setTextColor(primaryFg); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
                setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 12), GameUi.dp(ctx, 24), GameUi.dp(ctx, 12))
                background = GameUi.rounded(primary, GameUi.dpf(ctx, 12))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 24) }
                setOnClickListener { showAddModal() }
            })
            listContainer.addView(empty)
            return
        }

        val days = last7Days()
        habits.forEach { habit ->
            val habitColor = parseColor(habit.color)
            val cardView = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
                background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) }
            }
            val headerRow = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) }
            }
            val nameWrap = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            nameWrap.addView(View(ctx).apply {
                background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(habitColor) }
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 12), GameUi.dp(ctx, 12)).apply { marginEnd = GameUi.dp(ctx, 8) }
            })
            nameWrap.addView(TextView(ctx).apply { text = habit.name; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD) })
            headerRow.addView(nameWrap)
            headerRow.addView(TextView(ctx).apply {
                text = "${habit.history.size} streak"; setTextColor(muted); textSize = 13f
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = GameUi.dp(ctx, 16) }
            })
            headerRow.addView(TextView(ctx).apply {
                text = "🗑️"; textSize = 14f; setTextColor(destructive)
                setOnClickListener { store.deleteHabit(habit.id); renderList() }
            })
            cardView.addView(headerRow)

            val grid = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            days.forEachIndexed { idx, d ->
                val isDone = habit.history.contains(d.iso)
                val dayCol = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                dayCol.addView(TextView(ctx).apply { text = d.label; setTextColor(muted); textSize = 10f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 8) } })
                dayCol.addView(TextView(ctx).apply {
                    text = if (isDone) "✓" else ""; setTextColor(Color.WHITE); textSize = 13f; gravity = Gravity.CENTER
                    background = GameUi.rounded(if (isDone) habitColor else bg, GameUi.dpf(ctx, 8), if (isDone) habitColor else border, GameUi.dp(ctx, 1))
                    layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 32), GameUi.dp(ctx, 32))
                    setOnClickListener { store.toggleHabit(habit.id, d.iso); renderList() }
                })
                grid.addView(dayCol)
            }
            cardView.addView(grid)
            listContainer.addView(cardView)
        }
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
        newHabitColor = palette[0]

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
        val modalHeader = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), 0)
        }
        modalHeader.addView(TextView(ctx).apply { text = "New Habit"; setTextColor(fg); textSize = 20f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        modalHeader.addView(TextView(ctx).apply { text = "✕"; textSize = 22f; setTextColor(fg); setOnClickListener { binding.toolRoot.removeView(overlay) } })
        sheet.addView(modalHeader)

        val body = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24), GameUi.dp(ctx, 24))
        }
        val nameInput = EditText(ctx).apply {
            hint = "E.g. Read 10 pages"; setHintTextColor(muted); setTextColor(fg); textSize = 16f
            background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            setPadding(GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16), 0)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 56))
        }
        body.addView(nameInput)
        body.addView(TextView(ctx).apply { text = "Color"; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 24); bottomMargin = GameUi.dp(ctx, 12) } })

        val swatches = mutableListOf<Pair<String, View>>()
        fun refreshSwatches() {
            swatches.forEach { (c, v) ->
                v.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL; setColor(parseColor(c))
                    if (newHabitColor == c) setStroke(GameUi.dp(ctx, 3), fg)
                }
            }
        }
        val wrap = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        var currentRow: LinearLayout? = null
        palette.forEachIndexed { idx, c ->
            if (idx % 4 == 0) {
                currentRow = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
                wrap.addView(currentRow)
            }
            val sw = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 40), GameUi.dp(ctx, 40)).apply { rightMargin = GameUi.dp(ctx, 16); bottomMargin = GameUi.dp(ctx, 16) }
                setOnClickListener { newHabitColor = c; refreshSwatches() }
            }
            swatches.add(c to sw)
            currentRow?.addView(sw)
        }
        refreshSwatches()
        body.addView(wrap)

        body.addView(TextView(ctx).apply {
            text = "Create Habit"; setTextColor(primaryFg); textSize = 16f; setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 16))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 56)).apply { topMargin = GameUi.dp(ctx, 32) }
            setOnClickListener {
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) return@setOnClickListener
                store.addHabit(Habit(System.currentTimeMillis().toString(), name, newHabitColor, emptyList()))
                binding.toolRoot.removeView(overlay)
                renderList()
            }
        })
        sheet.addView(body)
        overlay.addView(sheet)
        overlay.setOnClickListener { binding.toolRoot.removeView(overlay) }
        sheet.setOnClickListener { }
        binding.toolRoot.addView(overlay)
    }

    private fun parseColor(hex: String): Int = try { Color.parseColor(hex) } catch (e: Exception) { Color.GRAY }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
