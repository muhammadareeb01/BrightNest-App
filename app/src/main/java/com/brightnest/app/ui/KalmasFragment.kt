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
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        // Initialise TTS engine
        tts = TextToSpeech(requireContext(), this)

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            val items = db.kalmaDao().all().map {
                ArabicCardItem(it.title, it.arabic, it.transliteration, it.translation)
            }
            _binding?.recycler?.adapter = ArabicCardAdapter(items) { arabicText, translitText ->
                speakKalma(arabicText, translitText)
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
     * Speaks the Arabic text if Arabic TTS is available (natural Arabic voice),
     * otherwise falls back to the English transliteration.
     * Before speaking any Kalma, it recites Ta'awwudh (Azubillah) first, then Tasmiyah (Bismillah), then the Kalma.
     */
    private fun speakKalma(arabicText: String, translitText: String) {
        if (!ttsReady) {
            Toast.makeText(context, "Please wait, TTS is loading...", Toast.LENGTH_SHORT).show()
            return
        }

        val textToSpeak = if (arabicAvailable) {
            "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ . بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ . $arabicText"
        } else {
            "A'udhu billahi min ash-shaytanir-rajim. Bismillahir-Rahmanir-Rahim. $translitText"
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
