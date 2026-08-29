package com.brightnest.app.ui

import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.AppLanguageHelper
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentStoriesBinding

import androidx.lifecycle.lifecycleScope
import com.brightnest.app.BrightNestApp
import kotlinx.coroutines.launch

class StoriesFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var isSpeaking = false
    private var currentStoryText: String = ""
    private var btnDetailListenStory: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lang = Prefs(requireContext()).language
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnDetailBack.text = "✕"
        binding.btnDetailBack.setOnClickListener { closeDetail() }
        binding.detailTitle.text = AppLanguageHelper.localizeUiText("Stories", lang)
        binding.btnSpeaker.setOnClickListener { toggleStorySpeech() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.detailRoot.visibility == View.VISIBLE) {
                    closeDetail()
                } else {
                    isEnabled = false
                    findNavController().navigateUp()
                }
            }
        })

        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                AppLanguageHelper.configureTts(tts, Prefs(requireContext()).language)
                tts?.setSpeechRate(0.85f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        activity?.runOnUiThread {
                            isSpeaking = true
                            updateSpeakerUi()
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        activity?.runOnUiThread {
                            isSpeaking = false
                            updateSpeakerUi()
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        activity?.runOnUiThread {
                            isSpeaking = false
                            updateSpeakerUi()
                        }
                    }
                })
            }
        }

        binding.storyRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            val cached = try { db.storyDao().all() } catch (e: Exception) { emptyList() }
            val list = if (cached.isNotEmpty() && cached.all { it.contentHi.isNotEmpty() && it.contentAr.isNotEmpty() }) {
                cached.map { s ->
                    Story(
                        id = s.id, emoji = s.emoji,
                        titleEn = s.titleEn, titleUr = s.titleUr, titleAr = s.titleAr, titleHi = s.titleHi,
                        taglineEn = s.taglineEn, taglineUr = s.taglineUr, taglineAr = s.taglineAr, taglineHi = s.taglineHi,
                        lessonEn = s.lessonEn, lessonUr = s.lessonUr, lessonAr = s.lessonAr, lessonHi = s.lessonHi,
                        contentEn = s.contentEn, contentUr = s.contentUr, contentAr = s.contentAr, contentHi = s.contentHi
                    )
                }
            } else {
                val updatedEntities = StoriesData.stories.map { s ->
                    com.brightnest.app.data.StoryEntity(
                        id = s.id, emoji = s.emoji,
                        titleEn = s.titleEn, titleUr = s.titleUr, titleAr = s.titleAr, titleHi = s.titleHi,
                        taglineEn = s.taglineEn, taglineUr = s.taglineUr, taglineAr = s.taglineAr, taglineHi = s.taglineHi,
                        lessonEn = s.lessonEn, lessonUr = s.lessonUr, lessonAr = s.lessonAr, lessonHi = s.lessonHi,
                        contentEn = s.contentEn, contentUr = s.contentUr, contentAr = s.contentAr, contentHi = s.contentHi
                    )
                }
                try { db.storyDao().insertAll(updatedEntities) } catch (_: Exception) {}
                StoriesData.stories
            }
            if (_binding != null) {
                binding.storyRecycler.adapter = StoryAdapter(list) { openDetail(it) }
            }
        }
    }

    private fun toggleStorySpeech() {
        if (!ttsReady || currentStoryText.isEmpty()) return
        if (isSpeaking) {
            tts?.stop()
            isSpeaking = false
            updateSpeakerUi()
        } else {
            val lang = Prefs(requireContext()).language
            AppLanguageHelper.configureTts(tts, lang)
            val params = Bundle()
            tts?.speak(currentStoryText, TextToSpeech.QUEUE_FLUSH, params, "story_utterance_${System.currentTimeMillis()}")
            isSpeaking = true
            updateSpeakerUi()
        }
    }

    private fun updateSpeakerUi() {
        val b = _binding ?: return
        val lang = Prefs(requireContext()).language
        if (isSpeaking) {
            b.btnSpeaker.text = "⏹️"
            btnDetailListenStory?.text = AppLanguageHelper.getStopListeningText(lang)
        } else {
            b.btnSpeaker.text = "🔊"
            btnDetailListenStory?.text = AppLanguageHelper.getListenStoryButtonText(lang)
        }
    }

    private fun openDetail(story: Story) {
        buildDetail(story)
        binding.listRoot.visibility = View.GONE
        binding.detailRoot.visibility = View.VISIBLE
    }

    private fun closeDetail() {
        tts?.stop()
        isSpeaking = false
        updateSpeakerUi()
        binding.detailRoot.visibility = View.GONE
        binding.listRoot.visibility = View.VISIBLE
    }

    private fun buildDetail(story: Story) {
        val ctx = requireContext()
        val lang = Prefs(ctx).language.lowercase()
        val isRtl = lang == "ur" || lang == "ar"
        val foreground = ContextCompat.getColor(ctx, R.color.foreground)
        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val border = ContextCompat.getColor(ctx, R.color.border)
        val container = binding.detailContent
        container.removeAllViews()

        // Cover
        container.addView(LinearLayout(ctx).apply {
            gravity = Gravity.CENTER
            background = GameUi.rounded(GameUi.withAlpha(primary, 18), GameUi.dpf(ctx, 24))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 180)
            ).apply { bottomMargin = GameUi.dp(ctx, 24) }
            addView(TextView(ctx).apply { text = story.emoji; textSize = 72f; gravity = Gravity.CENTER })
        })

        val locTitle = story.getLocalizedTitle(lang)
        val locTagline = story.getLocalizedTagline(lang)
        val locContent = story.getLocalizedContent(lang)
        val locLesson = story.getLocalizedLesson(lang)

        val lessonHeader = when (lang) {
            "ur" -> "سبق"
            "ar" -> "الدرس"
            "hi" -> "सीख"
            else -> "Lesson"
        }

        currentStoryText = "$locTitle. $locTagline. $locContent. $lessonHeader: $locLesson"

        container.addView(TextView(ctx).apply {
            text = locTitle; textSize = 28f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(foreground)
            gravity = if (isRtl) Gravity.END else Gravity.START
            if (isRtl) textDirection = View.TEXT_DIRECTION_RTL
            setPadding(0, 0, 0, GameUi.dp(ctx, 6))
        })

        container.addView(TextView(ctx).apply {
            text = locTagline; textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(primary)
            gravity = if (isRtl) Gravity.END else Gravity.START
            if (isRtl) textDirection = View.TEXT_DIRECTION_RTL
            setPadding(0, 0, 0, GameUi.dp(ctx, 12))
        })

        // Listen Story button
        val listenBtn = TextView(ctx).apply {
            text = if (isSpeaking) AppLanguageHelper.getStopListeningText(lang) else AppLanguageHelper.getListenStoryButtonText(lang)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(ctx, R.color.on_primary))
            gravity = Gravity.CENTER
            background = GameUi.rounded(primary, GameUi.dpf(ctx, 16))
            setPadding(GameUi.dp(ctx, 18), GameUi.dp(ctx, 10), GameUi.dp(ctx, 18), GameUi.dp(ctx, 10))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = if (isRtl) Gravity.END else Gravity.START
                bottomMargin = GameUi.dp(ctx, 20)
            }
            setOnClickListener { toggleStorySpeech() }
        }
        btnDetailListenStory = listenBtn
        container.addView(listenBtn)

        val paras = locContent.split("\n\n")
        for (i in paras.indices) {
            val textPara = paras[i]
            if (textPara.isNotBlank()) {
                val block = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = GameUi.dp(ctx, 20) }
                }
                block.addView(TextView(ctx).apply {
                    text = textPara; textSize = if (isRtl) 19f else 17f
                    setTextColor(foreground)
                    gravity = if (isRtl) Gravity.END else Gravity.START
                    if (isRtl) textDirection = View.TEXT_DIRECTION_RTL
                    setLineSpacing(GameUi.dpf(ctx, if (isRtl) 6 else 4), 1f)
                    setPadding(0, 0, 0, GameUi.dp(ctx, 10))
                })
                if (i < paras.size - 1) {
                    block.addView(View(ctx).apply {
                        setBackgroundColor(border)
                        alpha = 0.5f
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, GameUi.dp(ctx, 1)
                        ).apply { topMargin = GameUi.dp(ctx, 16) }
                    })
                }
                container.addView(block)
            }
        }

        // Lesson box
        val lessonBox = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16), GameUi.dp(ctx, 16))
            background = GameUi.rounded(
                GameUi.withAlpha(primary, 16), GameUi.dpf(ctx, 16),
                GameUi.withAlpha(primary, 48), GameUi.dp(ctx, 1)
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = GameUi.dp(ctx, 16) }
        }
        lessonBox.addView(TextView(ctx).apply {
            text = lessonHeader; textSize = 16f
            setTypeface(typeface, Typeface.BOLD); setTextColor(primary)
            gravity = if (isRtl) Gravity.END else Gravity.START
            if (isRtl) textDirection = View.TEXT_DIRECTION_RTL
            setPadding(0, 0, 0, GameUi.dp(ctx, 6))
        })
        lessonBox.addView(TextView(ctx).apply {
            text = locLesson; textSize = 17f
            setTextColor(foreground)
            gravity = if (isRtl) Gravity.END else Gravity.START
            if (isRtl) textDirection = View.TEXT_DIRECTION_RTL
            setLineSpacing(GameUi.dpf(ctx, 5), 1f)
        })
        container.addView(lessonBox)

        updateSpeakerUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        tts = null
        btnDetailListenStory = null
        _binding = null
    }

    private class StoryAdapter(
        val items: List<Story>,
        val onClick: (Story) -> Unit
    ) : RecyclerView.Adapter<StoryAdapter.VH>() {

        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val card: LinearLayout = v.findViewById(R.id.storyCard)
            val cover: View = v.findViewById(R.id.storyCover)
            val icon: TextView = v.findViewById(R.id.storyIcon)
            val titleEn: TextView = v.findViewById(R.id.storyTitle)
            val titleUr: TextView = v.findViewById(R.id.storyTitleUr)
            val taglineEn: TextView = v.findViewById(R.id.storyTagline)
            val taglineUr: TextView = v.findViewById(R.id.storyTaglineUr)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
            return VH(v)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val ctx = holder.itemView.context
            val story = items[position]
            val lang = com.brightnest.app.Prefs(ctx).language
            val isRtl = lang.lowercase() == "ur" || lang.lowercase() == "ar"
            val primary = ContextCompat.getColor(ctx, R.color.primary)
            val cardBg = ContextCompat.getColor(ctx, R.color.card)
            val border = ContextCompat.getColor(ctx, R.color.border)
            holder.card.background = GameUi.rounded(cardBg, GameUi.dpf(ctx, 22), border, GameUi.dp(ctx, 1))
            holder.card.clipToOutline = true
            holder.cover.setBackgroundColor(GameUi.withAlpha(primary, 21))
            holder.icon.text = story.emoji
            holder.titleEn.text = story.getLocalizedTitle(lang)
            holder.titleEn.gravity = if (isRtl) Gravity.END else Gravity.START
            if (isRtl) holder.titleEn.textDirection = View.TEXT_DIRECTION_RTL
            holder.titleUr.visibility = View.GONE
            holder.taglineEn.text = story.getLocalizedTagline(lang)
            holder.taglineEn.gravity = if (isRtl) Gravity.END else Gravity.START
            if (isRtl) holder.taglineEn.textDirection = View.TEXT_DIRECTION_RTL
            holder.taglineUr.visibility = View.GONE
            holder.card.setOnClickListener { onClick(story) }
        }
    }
}
