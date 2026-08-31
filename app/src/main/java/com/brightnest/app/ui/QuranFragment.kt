package com.brightnest.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightnest.app.databinding.FragmentListBinding

import androidx.lifecycle.lifecycleScope
import com.brightnest.app.BrightNestApp
import kotlinx.coroutines.launch

import androidx.navigation.fragment.findNavController

class QuranFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerTitle.text = "Quran Lessons"
        binding.headerSubTitle.text = "قرآن پاک کی تعلیم"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        val sections = listOf(
            SimpleRow("🔤", "Arabic Alphabet", "Learn the 28 letters with sounds"),
            SimpleRow("📗", "Noorani Qaida", "Step-by-step reading basics"),
            SimpleRow("🕋", "Surah Al-Fatihah", "The Opening — 7 verses"),
            SimpleRow("🌟", "Surah Al-Ikhlas", "Sincerity — 4 verses"),
            SimpleRow("🌅", "Surah Al-Falaq", "The Daybreak — 5 verses"),
            SimpleRow("👥", "Surah An-Nas", "Mankind — 6 verses"),
            SimpleRow("📿", "Ayat al-Kursi", "The Throne Verse"),
            SimpleRow("🔊", "Tajweed Basics", "Rules of recitation")
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            val cached = try { db.quranLessonDao().all() } catch (e: Exception) { emptyList() }
            val list = if (cached.isNotEmpty()) {
                cached.map { SimpleRow(it.icon, it.title, it.subtitle) }
            } else sections

            if (_binding != null) {
                binding.recycler.adapter = SimpleRowAdapter(list) {
                    Toast.makeText(requireContext(), "${it.title} — lessons coming soon", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
