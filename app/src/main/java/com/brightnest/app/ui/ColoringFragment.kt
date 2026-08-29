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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentColoringBinding

class ColoringFragment : Fragment() {

    sealed class ColorAction {
        data class Fill(val regionId: String, val prevColor: String?) : ColorAction()
        data class StrokeAction(val stroke: ColoringView.Stroke) : ColorAction()
    }

    private var _binding: FragmentColoringBinding? = null
    private val binding get() = _binding!!

    private var category = "All"
    private var pictureId = ColoringData.pictures[0].id
    private var color = ColoringData.palette[2]
    private var currentMode = ColoringView.Mode.FILL

    private val fills = HashMap<String, HashMap<String, String>>()
    private val strokesMap = HashMap<String, ArrayList<ColoringView.Stroke>>()
    private val history = HashMap<String, ArrayList<ColorAction>>()
    private val awarded = HashMap<String, Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentColoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnUndo.setOnClickListener { handleUndo() }
        binding.btnReset.setOnClickListener { handleReset() }

        binding.btnModeFill.setOnClickListener { setMode(ColoringView.Mode.FILL) }
        binding.btnModePen.setOnClickListener { setMode(ColoringView.Mode.PEN) }

        binding.coloringView.onRegionTap = { handleFill(it) }
        binding.coloringView.onStrokeFinished = { handleStroke(it) }
        binding.coloringView.setCurrentColor(Color.parseColor(color))

        updateModeUi()
        buildCategories()
        buildPalette()
        selectPicture(pictureId)
    }

    private fun setMode(mode: ColoringView.Mode) {
        currentMode = mode
        binding.coloringView.setMode(mode)
        updateModeUi()
    }

    private fun updateModeUi() {
        val ctx = context ?: return
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)

        if (currentMode == ColoringView.Mode.FILL) {
            binding.btnModeFill.setTextColor(Color.WHITE)
            binding.btnModeFill.background = GameUi.rounded(primary, GameUi.dpf(ctx, 12), primary, 0)

            binding.btnModePen.setTextColor(foreground)
            binding.btnModePen.setBackgroundColor(Color.TRANSPARENT)
        } else {
            binding.btnModePen.setTextColor(Color.WHITE)
            binding.btnModePen.background = GameUi.rounded(primary, GameUi.dpf(ctx, 12), primary, 0)

            binding.btnModeFill.setTextColor(foreground)
            binding.btnModeFill.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun picture(): ColorPicture = ColoringData.pictures.first { it.id == pictureId }
    private fun pictureFills(): HashMap<String, String> = fills.getOrPut(pictureId) { HashMap() }
    private fun pictureStrokes(): ArrayList<ColoringView.Stroke> = strokesMap.getOrPut(pictureId) { ArrayList() }
    private fun pictureHistory(): ArrayList<ColorAction> = history.getOrPut(pictureId) { ArrayList() }

    private fun visiblePictures(): List<ColorPicture> =
        if (category == "All") ColoringData.pictures else ColoringData.pictures.filter { it.category == category }

    private fun buildCategories() {
        val ctx = requireContext()
        binding.categoryRow.removeAllViews()
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val border = ContextCompat.getColor(ctx, R.color.border)
        for (c in ColoringData.categories) {
            val active = c == category
            binding.categoryRow.addView(TextView(ctx).apply {
                text = c; textSize = 13f; setTypeface(typeface, Typeface.BOLD)
                setTextColor(if (active) Color.WHITE else foreground)
                gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 14), GameUi.dp(ctx, 8), GameUi.dp(ctx, 14), GameUi.dp(ctx, 8))
                background = GameUi.rounded(if (active) primary else Color.TRANSPARENT, GameUi.dpf(ctx, 100), if (active) primary else border, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = GameUi.dp(ctx, 8) }
                isClickable = true
                setOnClickListener {
                    category = c
                    val vis = visiblePictures()
                    if (vis.none { it.id == pictureId } && vis.isNotEmpty()) pictureId = vis[0].id
                    buildCategories(); buildPictures(); selectPicture(pictureId)
                }
            })
        }
        buildPictures()
    }

    private fun buildPictures() {
        val ctx = requireContext()
        binding.pictureRow.removeAllViews()
        val sage = ContextCompat.getColor(ctx, R.color.sage)
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val cardBg = ContextCompat.getColor(ctx, R.color.card)
        for (p in visiblePictures()) {
            val active = p.id == pictureId
            val done = awarded[p.id] == true
            binding.pictureRow.addView(TextView(ctx).apply {
                text = (if (done) "✓ " else "") + p.name
                textSize = 13f; setTypeface(typeface, Typeface.BOLD)
                setTextColor(if (active) Color.WHITE else foreground)
                gravity = Gravity.CENTER
                setPadding(GameUi.dp(ctx, 14), GameUi.dp(ctx, 8), GameUi.dp(ctx, 14), GameUi.dp(ctx, 8))
                background = GameUi.rounded(if (active) sage else cardBg, GameUi.dpf(ctx, 100), if (active) sage else border, GameUi.dp(ctx, 1))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = GameUi.dp(ctx, 8) }
                isClickable = true
                setOnClickListener { selectPicture(p.id) }
            })
        }
    }

    private fun buildPalette() {
        val ctx = requireContext()
        binding.paletteRow.removeAllViews()
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        for (c in ColoringData.palette) {
            val selected = color == c
            binding.paletteRow.addView(View(ctx).apply {
                val sz = if (selected) GameUi.dp(ctx, 44) else GameUi.dp(ctx, 38)
                background = GameUi.rounded(
                    Color.parseColor(c), GameUi.dpf(ctx, if (selected) 22 else 19),
                    if (selected) foreground else Color.parseColor("#26000000"),
                    GameUi.dp(ctx, if (selected) 3 else 1)
                )
                layoutParams = LinearLayout.LayoutParams(sz, sz).apply {
                    marginEnd = GameUi.dp(ctx, 10)
                    gravity = Gravity.CENTER_VERTICAL
                }
                isClickable = true
                setOnClickListener {
                    color = c
                    binding.coloringView.setCurrentColor(Color.parseColor(c))
                    buildPalette()
                }
            })
        }
    }

    private fun selectPicture(id: String) {
        pictureId = id
        binding.coloringView.setPicture(picture())
        binding.coloringView.setFills(pictureFills())
        binding.coloringView.setStrokes(pictureStrokes())
        buildPictures()
        updateSubtitle()
    }

    private fun updateSubtitle() {
        val pic = picture()
        val filled = pic.regions.count { pictureFills()[it.id] != null }
        binding.coloringSub.text = "$filled/${pic.regions.size} · ${ColoringData.pictures.size} designs"
    }

    private fun handleFill(regionId: String) {
        pictureHistory().add(ColorAction.Fill(regionId, pictureFills()[regionId]))
        pictureFills()[regionId] = color
        binding.coloringView.setFills(pictureFills())
        updateSubtitle()
        checkAward()
    }

    private fun handleStroke(stroke: ColoringView.Stroke) {
        pictureHistory().add(ColorAction.StrokeAction(stroke))
        pictureStrokes().add(stroke)
    }

    private fun handleUndo() {
        val h = pictureHistory()
        if (h.isEmpty()) return
        val last = h.removeAt(h.size - 1)
        when (last) {
            is ColorAction.Fill -> {
                if (last.prevColor == null) {
                    pictureFills().remove(last.regionId)
                } else {
                    pictureFills()[last.regionId] = last.prevColor
                }
                binding.coloringView.setFills(pictureFills())
                updateSubtitle()
            }
            is ColorAction.StrokeAction -> {
                val stList = pictureStrokes()
                if (stList.isNotEmpty()) {
                    stList.removeAt(stList.size - 1)
                    binding.coloringView.setStrokes(stList)
                }
            }
        }
    }

    private fun handleReset() {
        pictureFills().clear()
        pictureStrokes().clear()
        pictureHistory().clear()
        awarded[pictureId] = false
        binding.coloringView.setFills(pictureFills())
        binding.coloringView.clearStrokes()
        buildPictures()
        updateSubtitle()
    }

    private fun checkAward() {
        val pic = picture()
        val filled = pic.regions.count { pictureFills()[it.id] != null }
        if (filled == pic.regions.size && awarded[pictureId] != true) {
            awarded[pictureId] = true
            Prefs(requireContext()).addCoins(5)
            buildPictures()
            Toast.makeText(requireContext(), "+5 coins · Great job!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
