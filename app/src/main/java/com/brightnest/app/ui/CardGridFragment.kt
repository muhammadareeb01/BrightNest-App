package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.content.KidsContent
import com.brightnest.app.databinding.FragmentCardGridBinding
import com.brightnest.app.AppLanguageHelper
import java.util.Locale

class CardGridFragment : Fragment() {

    private var _binding: FragmentCardGridBinding? = null
    private val binding get() = _binding!!

    private var screen: KidsContent.Screen? = null
    private var adapter: CardGridAdapter? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var currentTab = "fruit"
    private lateinit var backCallback: OnBackPressedCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCardGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))

        val sRaw = KidsContent.byTitle(arguments?.getString("title"))
        if (sRaw == null) {
            findNavController().navigateUp()
            return
        }
        val langCode = Prefs(requireContext()).language
        val s = AppLanguageHelper.localizeKidsScreen(sRaw, langCode)
        screen = s

        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                AppLanguageHelper.configureTts(tts, langCode)
            }
        }

        binding.title.text = s.title
        val subText = when (langCode.lowercase()) {
            "ur" -> sRaw.urdu
            "ar" -> AppLanguageHelper.getArabicSubtitle(sRaw.title)
            "hi" -> AppLanguageHelper.getHindiSubtitle(sRaw.title)
            else -> ""
        }
        if (subText.isNotEmpty()) {
            binding.subTitle.visibility = View.VISIBLE
            binding.subTitle.text = subText
        } else {
            binding.subTitle.visibility = View.GONE
        }
        val headerColorRes = when (s.headerColor) {
            "sage" -> R.color.sage
            "lavender" -> R.color.lavender
            "primary" -> R.color.primary
            else -> R.color.coral
        }
        binding.title.setTextColor(ContextCompat.getColor(requireContext(), headerColorRes))
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.detailClose.setOnClickListener { hideDetail() }

        adapter = CardGridAdapter(s.variant, displayItems(), ::onItemClick)
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), s.columns)
        binding.recycler.adapter = adapter

        if (s.variant == KidsContent.Variant.FRUIT) {
            binding.tabs.visibility = View.VISIBLE
            binding.tabFruit.setOnClickListener { setTab("fruit") }
            binding.tabVeg.setOnClickListener { setTab("veg") }
            updateTabs()
        }

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.detailRoot.visibility == View.VISIBLE) {
                    hideDetail()
                } else {
                    isEnabled = false
                    findNavController().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
    }

    private fun displayItems(): List<KidsContent.Item> {
        val s = screen ?: return emptyList()
        return if (s.variant == KidsContent.Variant.FRUIT) s.items.filter { it.category == currentTab } else s.items
    }

    private fun setTab(tab: String) {
        currentTab = tab
        adapter?.submit(displayItems())
        binding.recycler.scrollToPosition(0)
        updateTabs()
    }

    private fun updateTabs() {
        styleTab(binding.tabFruit, binding.tabFruitLabel, binding.tabFruitUrdu, currentTab == "fruit")
        styleTab(binding.tabVeg, binding.tabVegLabel, binding.tabVegUrdu, currentTab == "veg")
    }

    private fun styleTab(container: View, label: android.widget.TextView, urdu: android.widget.TextView, active: Boolean) {
        if (active) {
            container.background = GradientDrawable().apply {
                cornerRadius = 999f
                setColor(ContextCompat.getColor(requireContext(), R.color.primary))
            }
            val onPri = ContextCompat.getColor(requireContext(), R.color.on_primary)
            label.setTextColor(onPri)
            urdu.setTextColor(onPri)
        } else {
            container.background = null
            label.setTextColor(ContextCompat.getColor(requireContext(), R.color.foreground))
            urdu.setTextColor(ContextCompat.getColor(requireContext(), R.color.muted_foreground))
        }
    }

    private fun onItemClick(item: KidsContent.Item) {
        Prefs(requireContext()).addCoins(1)
        showDetail(item)
    }

    private fun speak(text: String) {
        if (ttsReady && text.isNotEmpty()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "bn")
        }
    }

    private fun showDetail(item: KidsContent.Item) {
        val s = screen ?: return
        val c = runCatching { Color.parseColor(item.colorHex) }.getOrDefault(Color.GRAY)

        val isColor = s.variant == KidsContent.Variant.COLOR
        binding.detailRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background))
        binding.detailHeaderTitle.text = s.title

        val onBg = ContextCompat.getColor(requireContext(), R.color.foreground)
        binding.detailClose.setTextColor(onBg)
        binding.detailName.setTextColor(onBg)

        // reset
        binding.detailLetter.visibility = View.GONE
        binding.detailLetter.textSize = 58f
        binding.detailEmoji.visibility = View.GONE
        binding.detailEmoji.textSize = 96f
        binding.detailColorCircle.visibility = View.GONE
        binding.detailDots.visibility = View.GONE
        binding.detailDots.removeAllViews()
        binding.detailName.visibility = View.VISIBLE
        binding.detailUrdu.visibility = View.GONE
        binding.detailText.visibility = View.GONE

        when (s.variant) {
            KidsContent.Variant.LETTER -> {
                binding.detailLetter.visibility = View.VISIBLE
                binding.detailLetter.textSize = 58f
                binding.detailLetter.text = item.label
                binding.detailLetter.setTextColor(c)
                binding.detailEmoji.visibility = View.VISIBLE
                binding.detailEmoji.textSize = 90f
                binding.detailEmoji.text = item.emoji
                binding.detailName.text = item.detailText
            }
            KidsContent.Variant.COLOR -> {
                binding.detailColorCircle.visibility = View.VISIBLE
                binding.detailColorCircle.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(c)
                    setStroke((4 * resources.displayMetrics.density).toInt(), ColorUtils.setAlphaComponent(Color.WHITE, 0x80))
                }
                binding.detailName.text = item.label
            }
            KidsContent.Variant.FRUIT -> {
                binding.detailEmoji.visibility = View.VISIBLE
                binding.detailEmoji.textSize = 96f
                binding.detailEmoji.text = item.emoji
                binding.detailName.text = item.label
                val langCode = Prefs(requireContext()).language.lowercase()
                if (langCode == "ur" && item.urdu.isNotEmpty() && item.urdu != item.label) {
                    binding.detailUrdu.visibility = View.VISIBLE
                    binding.detailUrdu.text = item.urdu
                } else {
                    binding.detailUrdu.visibility = View.GONE
                }
                binding.detailText.visibility = View.VISIBLE
                binding.detailText.text = item.detailText
            }
            KidsContent.Variant.ICON -> {
                binding.detailEmoji.visibility = View.VISIBLE
                binding.detailEmoji.textSize = 96f
                binding.detailEmoji.text = item.emoji
                binding.detailName.text = item.label
                if (item.urdu.isNotEmpty() && item.urdu != item.label) {
                    binding.detailUrdu.visibility = View.VISIBLE
                    binding.detailUrdu.text = item.urdu
                } else {
                    binding.detailUrdu.visibility = View.GONE
                }
                if (item.detailText.isNotEmpty()) {
                    binding.detailText.visibility = View.VISIBLE
                    binding.detailText.text = item.detailText
                }
            }
            KidsContent.Variant.ALPHABET -> {
                binding.detailLetter.visibility = View.VISIBLE
                binding.detailLetter.textSize = 100f
                binding.detailLetter.text = item.label
                binding.detailLetter.setTextColor(c)
                binding.detailName.text = item.detailText
            }
            KidsContent.Variant.NUMBER -> {
                val n = item.label.toIntOrNull() ?: 0
                binding.detailLetter.visibility = View.VISIBLE
                binding.detailLetter.textSize = if (n >= 100) 80f else 100f
                binding.detailLetter.text = item.label
                binding.detailLetter.setTextColor(c)
                binding.detailName.visibility = View.GONE
                buildDots(n, c)
            }
        }

        binding.detailListen.text = AppLanguageHelper.getListenButtonText(Prefs(requireContext()).language)
        binding.detailListen.setOnClickListener { speak(item.speakText) }

        binding.gridRoot.visibility = View.GONE
        binding.detailRoot.visibility = View.VISIBLE
    }

    private fun buildDots(n: Int, c: Int) {
        binding.detailDots.removeAllViews()
        if (n <= 0) {
            binding.detailDots.visibility = View.GONE
            return
        }
        val d = resources.displayMetrics.density
        val sizeDp = if (n > 50) 10 else if (n > 20) 16 else 24
        val gapDp = if (n > 50) 4 else if (n > 20) 8 else 12
        val sizePx = (sizeDp * d).toInt()
        val gapPx = (gapDp * d / 2).toInt()
        for (i in 0 until n) {
            val dot = View(requireContext())
            val lp = GridLayout.LayoutParams()
            lp.width = sizePx
            lp.height = sizePx
            lp.setMargins(gapPx, gapPx, gapPx, gapPx)
            dot.layoutParams = lp
            dot.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(c)
            }
            binding.detailDots.addView(dot)
        }
        binding.detailDots.visibility = View.VISIBLE
    }

    private fun hideDetail() {
        tts?.stop()
        binding.detailRoot.visibility = View.GONE
        binding.gridRoot.visibility = View.VISIBLE
    }

    private fun isLight(c: Int) = ColorUtils.calculateLuminance(c) > 0.6

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        tts = null
        adapter = null
        _binding = null
    }
}
