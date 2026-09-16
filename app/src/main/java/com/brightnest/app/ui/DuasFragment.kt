package com.brightnest.app.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightnest.app.AppLanguageHelper
import com.brightnest.app.BrightNestApp
import com.brightnest.app.Prefs
import com.brightnest.app.data.SeedData
import com.brightnest.app.databinding.FragmentListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class DuasFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var arabicAvailable = false
    private var speakJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val langCode = try { Prefs(requireContext()).language.lowercase() } catch (e: Exception) { "en" }

        binding.headerTitle.text = "Daily Duas"
        binding.headerSubTitle.text = "مسنون دعائیں"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        AppLanguageHelper.localizeViewTree(binding.root, langCode)

        // Initialise TTS engine
        tts = TextToSpeech(requireContext(), this)

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            try {
                // Ensure room db is synced with complete authentic seed duas
                db.duaDao().insertAll(SeedData.duas)
            } catch (_: Exception) {}

            // Load multilingual duas without category dot (• Daily)
            val duasList = DuasData.duas
            val items = duasList.map { d ->
                ArabicCardItem(
                    title = d.getLocalizedTitle(langCode),
                    arabic = d.arabic,
                    transliteration = d.transliteration,
                    translation = d.getLocalizedTranslation(langCode)
                )
            }

            _binding?.recycler?.adapter = ArabicCardAdapter(items) { item ->
                val index = items.indexOf(item)
                val detail = if (index in duasList.indices) duasList[index] else null
                if (detail != null) {
                    speakDua(detail, langCode)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            return
        }

        val arabicLocale = Locale("ar")
        val arabicResult = tts?.isLanguageAvailable(arabicLocale)
        arabicAvailable = arabicResult != TextToSpeech.LANG_MISSING_DATA &&
                arabicResult != TextToSpeech.LANG_NOT_SUPPORTED &&
                arabicResult != null

        if (arabicAvailable) {
            tts?.setLanguage(arabicLocale)
        } else {
            tts?.setLanguage(Locale.ENGLISH)
        }

        tts?.setSpeechRate(0.78f)
        tts?.setPitch(0.95f)
        ttsReady = true
    }

    private fun isLocaleAvailable(loc: Locale): Boolean {
        val result = tts?.isLanguageAvailable(loc)
        return result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED &&
                result != null
    }

    private suspend fun awaitUtterance(text: String, utteranceId: String) = suspendCancellableCoroutine<Unit> { cont ->
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}
            override fun onDone(id: String?) {
                if (id == utteranceId && cont.isActive) {
                    cont.resume(Unit)
                }
            }
            @Deprecated("Deprecated in Java")
            override fun onError(id: String?) {
                if (id == utteranceId && cont.isActive) {
                    cont.resume(Unit)
                }
            }
        })
        val res = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        if (res != TextToSpeech.SUCCESS && cont.isActive) {
            cont.resume(Unit)
        }
        cont.invokeOnCancellation {
            tts?.stop()
        }
    }

    /**
     * Speaks the Dua:
     * 1. Reads what the Dua is for in the user's selected language (Urdu, English, Arabic, Hindi).
     * 2. Recites Ta'awwudh (Azubillah).
     * 3. Recites Tasmiyah (Bismillah - if not starting with Bismillah).
     * 4. Recites the authentic Dua in Arabic (or transliteration if Arabic voice is unavailable).
     */
    private fun speakDua(dua: DuaDetail, langCode: String) {
        if (!ttsReady) {
            Toast.makeText(context, "Please wait, audio is loading...", Toast.LENGTH_SHORT).show()
            return
        }

        speakJob?.cancel()
        tts?.stop()

        val spokenTitle = dua.getSpokenTitle(langCode)
        val toastMsg = when (langCode) {
            "ur" -> "🔊 $spokenTitle — تَعَوُّذْ اور تَسْمِیَہ کے ساتھ"
            "ar" -> "🔊 $spokenTitle — مع التعوذ والتسمية"
            "hi" -> "🔊 $spokenTitle — ताअव्वुज़ और तस्मिया के साथ"
            else -> "🔊 $spokenTitle — with Ta'awwudh & Bismillah"
        }
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()

        val azubillahAr = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ"
        val bismillahAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        val azubillahEn = "A'oodhu billahi minash-shaytanir-rajeem."
        val bismillahEn = "Bismillahir-Rahmanir-Raheem."

        val startsWithBismillah = dua.arabic.trim().startsWith("بِسْمِ") ||
                dua.transliteration.trim().startsWith("Bismillahi", ignoreCase = true)

        speakJob = viewLifecycleOwner.lifecycleScope.launch {
            // Step 1: Announce which Dua it is in the active language
            val titleLocale: Locale
            val titleTextToSpeak: String

            when (langCode) {
                "ur" -> {
                    val urPk = Locale("ur", "PK")
                    val urGeneric = Locale("ur")
                    if (isLocaleAvailable(urPk)) {
                        titleLocale = urPk
                        titleTextToSpeak = spokenTitle
                    } else if (isLocaleAvailable(urGeneric)) {
                        titleLocale = urGeneric
                        titleTextToSpeak = spokenTitle
                    } else {
                        titleLocale = Locale.ENGLISH
                        titleTextToSpeak = dua.titleEn
                    }
                }
                "ar" -> {
                    val arLoc = Locale("ar")
                    if (isLocaleAvailable(arLoc)) {
                        titleLocale = arLoc
                        titleTextToSpeak = spokenTitle
                    } else {
                        titleLocale = Locale.ENGLISH
                        titleTextToSpeak = dua.titleEn
                    }
                }
                "hi" -> {
                    val hiIn = Locale("hi", "IN")
                    val hiGeneric = Locale("hi")
                    if (isLocaleAvailable(hiIn)) {
                        titleLocale = hiIn
                        titleTextToSpeak = spokenTitle
                    } else if (isLocaleAvailable(hiGeneric)) {
                        titleLocale = hiGeneric
                        titleTextToSpeak = spokenTitle
                    } else {
                        titleLocale = Locale.ENGLISH
                        titleTextToSpeak = dua.titleEn
                    }
                }
                else -> {
                    titleLocale = Locale.ENGLISH
                    titleTextToSpeak = spokenTitle
                }
            }

            tts?.language = titleLocale
            tts?.setSpeechRate(0.85f)
            tts?.setPitch(1.0f)

            val titleUtteranceId = "dua_title_${System.currentTimeMillis()}"
            awaitUtterance(titleTextToSpeak, titleUtteranceId)

            // Short respectful pause between title announcement and recitation
            delay(400)

            // Step 2: Recite Ta'awwudh, Tasmiyah, and the authentic Dua
            if (arabicAvailable) {
                tts?.language = Locale("ar")
                tts?.setSpeechRate(0.78f)
                tts?.setPitch(0.95f)

                tts?.speak(azubillahAr, TextToSpeech.QUEUE_FLUSH, null, "azubillah")
                if (!startsWithBismillah) {
                    tts?.speak(bismillahAr, TextToSpeech.QUEUE_ADD, null, "bismillah")
                }
                tts?.speak(dua.arabic, TextToSpeech.QUEUE_ADD, null, "dua")
            } else {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(0.82f)
                tts?.setPitch(0.95f)

                tts?.speak(azubillahEn, TextToSpeech.QUEUE_FLUSH, null, "azubillah")
                if (!startsWithBismillah) {
                    tts?.speak(bismillahEn, TextToSpeech.QUEUE_ADD, null, "bismillah")
                }
                tts?.speak(dua.transliteration, TextToSpeech.QUEUE_ADD, null, "dua")
            }
        }
    }

    override fun onDestroyView() {
        speakJob?.cancel()
        speakJob = null
        tts?.stop()
        tts?.shutdown()
        tts = null
        ttsReady = false
        arabicAvailable = false
        super.onDestroyView()
        _binding = null
    }
}
