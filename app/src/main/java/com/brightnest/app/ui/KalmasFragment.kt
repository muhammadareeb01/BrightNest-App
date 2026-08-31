package com.brightnest.app.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightnest.app.BrightNestApp
import com.brightnest.app.databinding.FragmentListBinding
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.brightnest.app.data.SeedData
import java.util.Locale

class KalmasFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var arabicAvailable = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerTitle.text = "Six Kalmas"
        binding.headerSubTitle.text = "چھ کلمے"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        // Initialise TTS engine
        tts = TextToSpeech(requireContext(), this)

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            val raw = try { db.kalmaDao().all() } catch (e: Exception) { emptyList() }
            val list = if (raw.isNotEmpty()) raw else SeedData.kalmas
            val items = list.map {
                ArabicCardItem(it.title, it.arabic, it.transliteration, it.translation)
            }
            _binding?.recycler?.adapter = ArabicCardAdapter(items) { item ->
                speakKalma(item)
            }
        }
    }

    /** Called when TTS engine finishes initialising */
    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(context, "Text-to-Speech initialisation failed", Toast.LENGTH_SHORT).show()
            return
        }

        // Try Arabic locale first — Google TTS Arabic voice sounds very natural
        val arabicLocale = Locale("ar")
        val arabicResult = tts?.isLanguageAvailable(arabicLocale)
        arabicAvailable = arabicResult != TextToSpeech.LANG_MISSING_DATA &&
                arabicResult != TextToSpeech.LANG_NOT_SUPPORTED &&
                arabicResult != null

        if (arabicAvailable) {
            tts?.setLanguage(arabicLocale)
        } else {
            // Fallback to English for transliteration
            tts?.setLanguage(Locale.ENGLISH)
        }

        // Slower speed for clear, dignified recitation — 0.75 feels natural
        tts?.setSpeechRate(0.75f)

        // Slightly lower pitch — sounds more authoritative and less robotic
        tts?.setPitch(0.92f)

        ttsReady = true
    }

    /**
     * Speaks the Kalma:
     * 1. Reads the Kalma Title.
     * 2. Recites Ta'awwudh (Azubillah).
     * 3. Recites Tasmiyah (Bismillah).
     * 4. Recites the Kalma Arabic & transliteration.
     */
    private fun speakKalma(item: ArabicCardItem) {
        if (!ttsReady) {
            Toast.makeText(context, "Please wait, audio is loading...", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "🔊 ${item.title} — تَعَوُّذْ اور تَسْمِیَہ کے ساتھ", Toast.LENGTH_SHORT).show()

        val azubillahAr = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ"
        val bismillahAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        val azubillahEn = "A'oodhu billahi minash-shaytanir-rajeem."
        val bismillahEn = "Bismillahir-Rahmanir-Raheem."

        val textToSpeak = if (arabicAvailable) {
            "${item.title} . $azubillahAr . $bismillahAr . ${item.arabic}"
        } else {
            "${item.title}. $azubillahEn $bismillahEn ${item.transliteration}"
        }

        tts?.stop()
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "kalma_tts")
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ttsReady = false
        arabicAvailable = false
        super.onDestroyView()
        _binding = null
    }
}
