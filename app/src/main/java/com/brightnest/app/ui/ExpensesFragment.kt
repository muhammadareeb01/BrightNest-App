package com.brightnest.app.ui

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
import com.brightnest.app.Expense
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpensesFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var store: AdultStore

    private var type = "expense"
    private lateinit var categoryInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var expenseTotalText: TextView
    private lateinit var incomeTotalText: TextView
    private lateinit var listContainer: LinearLayout
    private lateinit var expenseToggle: TextView
    private lateinit var incomeToggle: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        store = AdultStore(requireContext())
        binding.title.text = "Expenses"
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
        val border = ContextCompat.getColor(ctx, R.color.border)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)
        val sage = ContextCompat.getColor(ctx, R.color.sage)

        val main = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        // summary row
        val summaryRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
        }
        val expCard = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            background = GameUi.rounded(GameUi.withAlpha(destructive, 0x10), GameUi.dpf(ctx, 20))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = GameUi.dp(ctx, 8) }
        }
        expCard.addView(TextView(ctx).apply { text = "Expenses"; setTextColor(destructive); textSize = 13f })
        expenseTotalText = TextView(ctx).apply { setTextColor(destructive); textSize = 24f; setTypeface(typeface, Typeface.BOLD) }
        expCard.addView(expenseTotalText)
        val incCard = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            background = GameUi.rounded(GameUi.withAlpha(sage, 0x10), GameUi.dpf(ctx, 20))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = GameUi.dp(ctx, 8) }
        }
        incCard.addView(TextView(ctx).apply { text = "Income"; setTextColor(sage); textSize = 13f })
        incomeTotalText = TextView(ctx).apply { setTextColor(sage); textSize = 24f; setTypeface(typeface, Typeface.BOLD) }
        incCard.addView(incomeTotalText)
        summaryRow.addView(expCard)
        summaryRow.addView(incCard)
        main.addView(summaryRow)

        // input section
        val inputSection = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            background = GameUi.rounded(card, GameUi.dpf(ctx, 12), border, GameUi.dp(ctx, 1))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                marginStart = GameUi.dp(ctx, 16); marginEnd = GameUi.dp(ctx, 16); bottomMargin = GameUi.dp(ctx, 4)
            }
        }
        val typeToggle = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GameUi.rounded(GameUi.withAlpha(android.graphics.Color.BLACK, 0x0D), GameUi.dpf(ctx, 8))
            setPadding(GameUi.dp(ctx, 4), GameUi.dp(ctx, 4), GameUi.dp(ctx, 4), GameUi.dp(ctx, 4))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 12) }
        }
        expenseToggle = TextView(ctx).apply {
            text = "Expense"; textSize = 13f; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { type = "expense"; updateToggle() }
        }
        incomeToggle = TextView(ctx).apply {
            text = "Income"; textSize = 13f; gravity = Gravity.CENTER
            setPadding(GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { type = "income"; updateToggle() }
        }
        typeToggle.addView(expenseToggle)
        typeToggle.addView(incomeToggle)
        inputSection.addView(typeToggle)

        val inputRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        categoryInput = EditText(ctx).apply {
            hint = "Category"; setHintTextColor(muted); setTextColor(fg); textSize = 14f
            background = GameUi.rounded(0x00000000, GameUi.dpf(ctx, 8), border, GameUi.dp(ctx, 1))
            setPadding(GameUi.dp(ctx, 12), 0, GameUi.dp(ctx, 12), 0)
            inputType = InputType.TYPE_CLASS_TEXT
            layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 44), 2f).apply { marginEnd = GameUi.dp(ctx, 8) }
        }
        amountInput = EditText(ctx).apply {
            hint = "0.00"; setHintTextColor(muted); setTextColor(fg); textSize = 14f
            background = GameUi.rounded(0x00000000, GameUi.dpf(ctx, 8), border, GameUi.dp(ctx, 1))
            setPadding(GameUi.dp(ctx, 12), 0, GameUi.dp(ctx, 12), 0)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            layoutParams = LinearLayout.LayoutParams(0, GameUi.dp(ctx, 44), 1f).apply { marginEnd = GameUi.dp(ctx, 8) }
        }
        inputRow.addView(categoryInput)
        inputRow.addView(amountInput)
        inputRow.addView(TextView(ctx).apply {
            text = "+"; textSize = 20f; setTextColor(primaryFg); gravity = Gravity.CENTER
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 8))
            layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 44), GameUi.dp(ctx, 44))
            setOnClickListener { addExpense() }
        })
        inputSection.addView(inputRow)
        main.addView(inputSection)

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

        binding.toolRoot.addView(main)
        updateToggle()
        renderList()
    }

    private fun updateToggle() {
        val ctx = requireContext()
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)
        val sage = ContextCompat.getColor(ctx, R.color.sage)
        if (type == "expense") {
            expenseToggle.background = GameUi.rounded(destructive, GameUi.dpf(ctx, 6))
            expenseToggle.setTextColor(android.graphics.Color.WHITE)
            incomeToggle.background = null
            incomeToggle.setTextColor(muted)
        } else {
            incomeToggle.background = GameUi.rounded(sage, GameUi.dpf(ctx, 6))
            incomeToggle.setTextColor(android.graphics.Color.WHITE)
            expenseToggle.background = null
            expenseToggle.setTextColor(muted)
        }
    }

    private fun addExpense() {
        val cat = categoryInput.text.toString().trim()
        val amtStr = amountInput.text.toString().trim()
        if (cat.isEmpty() || amtStr.isEmpty()) return
        val amt = amtStr.toDoubleOrNull() ?: return
        store.addExpense(Expense(System.currentTimeMillis().toString(), type, cat, amt, System.currentTimeMillis().toString(), ""))
        categoryInput.setText(""); amountInput.setText("")
        renderList()
    }

    private fun renderList() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)
        val sage = ContextCompat.getColor(ctx, R.color.sage)

        val expenses = store.expenses
        val totalExpense = expenses.filter { it.type == "expense" }.sumOf { it.amount }
        val totalIncome = expenses.filter { it.type == "income" }.sumOf { it.amount }
        expenseTotalText.text = "$%.2f".format(totalExpense)
        incomeTotalText.text = "$%.2f".format(totalIncome)

        listContainer.removeAllViews()
        expenses.forEach { e ->
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(0, GameUi.dp(ctx, 12), 0, GameUi.dp(ctx, 12))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            val divider = View(ctx).apply { setBackgroundColor(border) }
            val infoCol = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            infoCol.addView(TextView(ctx).apply { text = e.category; setTextColor(fg); textSize = 16f; setTypeface(typeface, Typeface.BOLD) })
            infoCol.addView(TextView(ctx).apply { text = displayDate(e.date); setTextColor(muted); textSize = 13f })
            row.addView(infoCol)
            row.addView(TextView(ctx).apply {
                text = (if (e.type == "expense") "-" else "+") + "$%.2f".format(e.amount)
                setTextColor(if (e.type == "expense") destructive else sage); textSize = 16f; setTypeface(typeface, Typeface.BOLD)
            })
            row.addView(TextView(ctx).apply {
                text = "🗑️"; textSize = 14f; setTextColor(muted)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = GameUi.dp(ctx, 16) }
                setOnClickListener { store.deleteExpense(e.id); renderList() }
            })
            listContainer.addView(row)
            listContainer.addView(View(ctx).apply {
                setBackgroundColor(border)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 1))
            })
        }
    }

    private fun displayDate(stored: String): String {
        return try {
            SimpleDateFormat("M/d/yyyy", Locale.US).format(Date(stored.toLong()))
        } catch (e: Exception) { stored }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
