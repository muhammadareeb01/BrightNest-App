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

class DuasFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var arabicAvailable = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
            val rawList = db.duaDao().all()
            // Deduplicate items to ensure no repeating duas occur
            val items = rawList.distinctBy { it.title.trim().lowercase() }.map {
                ArabicCardItem("${it.title}  •  ${it.category}", it.arabic, it.transliteration, it.translation)
            }
            _binding?.recycler?.adapter = ArabicCardAdapter(items) { arabicText, translitText ->
                speakDua(arabicText, translitText)
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

    private fun speakDua(arabicText: String, translitText: String) {
        if (!ttsReady) {
            Toast.makeText(context, "Please wait, TTS is loading...", Toast.LENGTH_SHORT).show()
            return
        }

        val textToSpeak = if (arabicAvailable) arabicText else translitText
        tts?.stop()
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "dua_tts")
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
