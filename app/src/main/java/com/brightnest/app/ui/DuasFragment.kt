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

import androidx.navigation.fragment.findNavController
import com.brightnest.app.data.SeedData

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
        binding.headerTitle.text = "Daily Duas"
        binding.headerSubTitle.text = "مسنون دعائیں"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        // Initialise TTS engine
        tts = TextToSpeech(requireContext(), this)

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            val rawList = try { db.duaDao().all() } catch (e: Exception) { emptyList() }
            val sourceList = if (rawList.isNotEmpty()) rawList else SeedData.duas
            // Deduplicate items to ensure no repeating duas occur
            val items = sourceList.distinctBy { it.title.trim().lowercase() }.map {
                ArabicCardItem("${it.title}  •  ${it.category}", it.arabic, it.transliteration, it.translation)
            }
            _binding?.recycler?.adapter = ArabicCardAdapter(items) { item ->
                speakDua(item)
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

    /**
     * Speaks the Dua:
     * 1. Reads what the Dua is for (title/purpose).
     * 2. Recites Ta'awwudh (Azubillah).
     * 3. Recites Tasmiyah (Bismillah).
     * 4. Recites the authentic Dua.
     */
    private fun speakDua(item: ArabicCardItem) {
        if (!ttsReady) {
            Toast.makeText(context, "Please wait, audio is loading...", Toast.LENGTH_SHORT).show()
            return
        }

        val cleanTitle = item.title.substringBefore("•").trim()
        Toast.makeText(context, "🔊 $cleanTitle — تَعَوُّذْ اور تَسْمِیَہ کے ساتھ", Toast.LENGTH_SHORT).show()

        tts?.stop()

        val azubillahAr = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ"
        val bismillahAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        val azubillahEn = "A'oodhu billahi minash-shaytanir-rajeem."
        val bismillahEn = "Bismillahir-Rahmanir-Raheem."

        // Check if the dua itself already starts with Bismillah (e.g. Before Eating: Bismillahi wa ala barakatillah)
        val startsWithBismillah = item.arabic.trim().startsWith("بِسْمِ") ||
                item.transliteration.trim().startsWith("Bismillahi", ignoreCase = true)

        val textToSpeak = if (arabicAvailable) {
            if (startsWithBismillah) {
                "$cleanTitle . $azubillahAr . ${item.arabic}"
            } else {
                "$cleanTitle . $azubillahAr . $bismillahAr . ${item.arabic}"
            }
        } else {
            if (startsWithBismillah) {
                "$cleanTitle. $azubillahEn ${item.transliteration}"
            } else {
                "$cleanTitle. $azubillahEn $bismillahEn ${item.transliteration}"
            }
        }

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
