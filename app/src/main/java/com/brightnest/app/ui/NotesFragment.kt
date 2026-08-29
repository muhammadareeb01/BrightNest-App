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
import com.brightnest.app.Note
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentAdultToolBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesFragment : Fragment() {

    private var _binding: FragmentAdultToolBinding? = null
    private val binding get() = _binding!!
    private lateinit var store: AdultStore

    // editingId: null = list, "" = new note, otherwise = note id
    private var editingId: String? = null
    private var titleVal = ""
    private var bodyVal = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdultToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        store = AdultStore(requireContext())
        render()
    }

    private fun render() {
        val ctx = requireContext()
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        binding.toolRoot.removeAllViews()

        if (editingId != null) {
            binding.title.text = if (editingId!!.isNotEmpty()) "Edit Note" else "New Note"
            binding.btnBack.text = "✕"
            binding.btnBack.setOnClickListener { editingId = null; render() }
            binding.btnAction.apply {
                text = "Save"; textSize = 16f; setTypeface(typeface, Typeface.BOLD); setTextColor(primary)
                setOnClickListener { saveNote() }
            }
            buildEditor()
        } else {
            binding.title.text = "Notes"
            binding.btnBack.text = "←"
            binding.btnBack.setOnClickListener { findNavController().navigateUp() }
            binding.btnAction.apply { text = ""; setOnClickListener(null) }
            buildList()
        }
    }

    private fun buildList() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val mutedC = ContextCompat.getColor(ctx, R.color.muted)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val primaryFg = ContextCompat.getColor(ctx, R.color.on_primary)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val destructive = ContextCompat.getColor(ctx, R.color.destructive)

        val scroll = ScrollView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 100))
        }

        val notes = store.notes
        if (notes.isEmpty()) {
            val empty = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48), GameUi.dp(ctx, 48))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 40) }
            }
            empty.addView(TextView(ctx).apply {
                text = "📝"; textSize = 28f; gravity = Gravity.CENTER
                background = GameUi.rounded(mutedC, GameUi.dpf(ctx, 40))
                layoutParams = LinearLayout.LayoutParams(GameUi.dp(ctx, 80), GameUi.dp(ctx, 80))
            })
            empty.addView(TextView(ctx).apply { text = "No notes yet. Tap the button to start."; setTextColor(muted); textSize = 16f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 16) } })
            col.addView(empty)
        } else {
            notes.forEach { note ->
                val cardView = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20))
                    background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) }
                    setOnClickListener { openEditor(note) }
                    isClickable = true
                }
                val textCol = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                textCol.addView(TextView(ctx).apply { text = note.title.ifEmpty { "Untitled Note" }; setTextColor(fg); textSize = 18f; setTypeface(typeface, Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 8) } })
                textCol.addView(TextView(ctx).apply { text = note.body.ifEmpty { "No content..." }; setTextColor(muted); textSize = 13f; maxLines = 3; ellipsize = android.text.TextUtils.TruncateAt.END })
                textCol.addView(TextView(ctx).apply { text = displayDate(note.updatedAt); setTextColor(muted); textSize = 13f; alpha = 0.6f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = GameUi.dp(ctx, 12) } })
                cardView.addView(textCol)
                cardView.addView(TextView(ctx).apply {
                    text = "🗑️"; textSize = 18f; setTextColor(destructive)
                    setPadding(GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8), GameUi.dp(ctx, 8))
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginStart = GameUi.dp(ctx, 12) }
                    setOnClickListener { store.deleteNote(note.id); render() }
                })
                col.addView(cardView)
            }
        }
        scroll.addView(col)

        val container = android.widget.FrameLayout(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        container.addView(scroll)
        // FAB
        container.addView(TextView(ctx).apply {
            text = "✎"; textSize = 24f; setTextColor(primaryFg); gravity = Gravity.CENTER
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 30))
            layoutParams = android.widget.FrameLayout.LayoutParams(GameUi.dp(ctx, 60), GameUi.dp(ctx, 60), Gravity.BOTTOM or Gravity.END).apply {
                rightMargin = GameUi.dp(ctx, 24); bottomMargin = GameUi.dp(ctx, 24)
            }
            setOnClickListener { editingId = ""; titleVal = ""; bodyVal = ""; render() }
        })
        binding.toolRoot.addView(container)
    }

    private fun buildEditor() {
        val ctx = requireContext()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)
        val card = ContextCompat.getColor(ctx, R.color.card)
        val border = ContextCompat.getColor(ctx, R.color.border)

        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val titleInput = EditText(ctx).apply {
            hint = "Note Title"; setHintTextColor(muted); setTextColor(fg); textSize = 24f; setTypeface(typeface, Typeface.BOLD)
            setText(titleVal)
            background = null
            setPadding(0, GameUi.dp(ctx, 16), 0, GameUi.dp(ctx, 16))
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = GameUi.dp(ctx, 16) }
            addTextChangedListener(simpleWatcher { titleVal = it })
        }
        // bottom border line under title
        val titleWrap = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        titleWrap.addView(titleInput)
        titleWrap.addView(View(ctx).apply { setBackgroundColor(border); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 1)) })
        col.addView(titleWrap)

        col.addView(EditText(ctx).apply {
            hint = "Start typing..."; setHintTextColor(muted); setTextColor(fg); textSize = 16f
            setText(bodyVal)
            gravity = Gravity.TOP
            background = GameUi.rounded(card, GameUi.dpf(ctx, 16), border, GameUi.dp(ctx, 1))
            setPadding(GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20), GameUi.dp(ctx, 20))
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f).apply { topMargin = GameUi.dp(ctx, 16) }
            addTextChangedListener(simpleWatcher { bodyVal = it })
        })
        binding.toolRoot.addView(col)
    }

    private fun openEditor(note: Note) {
        editingId = note.id; titleVal = note.title; bodyVal = note.body; render()
    }

    private fun saveNote() {
        if (titleVal.trim().isEmpty() && bodyVal.trim().isEmpty()) return
        val now = System.currentTimeMillis().toString()
        val id = editingId
        if (!id.isNullOrEmpty()) {
            store.updateNote(id, titleVal, bodyVal, now)
        } else {
            store.addNote(Note(System.currentTimeMillis().toString(), titleVal, bodyVal, now))
        }
        editingId = null; titleVal = ""; bodyVal = ""; render()
    }

    private fun displayDate(stored: String): String {
        return try {
            SimpleDateFormat("M/d/yyyy", Locale.US).format(Date(stored.toLong()))
        } catch (e: Exception) { stored }
    }

    private fun simpleWatcher(onChange: (String) -> Unit) = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: android.text.Editable?) { onChange(s?.toString() ?: "") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
