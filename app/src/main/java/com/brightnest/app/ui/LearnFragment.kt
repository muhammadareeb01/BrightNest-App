package com.brightnest.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.brightnest.app.AppLanguageHelper
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentGridBinding

class LearnFragment : Fragment() {

    private var _binding: FragmentGridBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))

        val adult = Prefs(requireContext()).mode == "adult"
        if (adult) {
            binding.header.text = "Productivity & Tools"
            binding.subHeader.visibility = View.GONE
            binding.recycler.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.recycler.adapter = MenuAdapter(adultTools()) { navigateTo(it) }
        } else {
            val lang = Prefs(requireContext()).language
            when (lang.lowercase()) {
                "ur" -> {
                    binding.header.text = "آؤ سیکھیں!"
                    binding.subHeader.text = "Let's Learn!"
                }
                "ar" -> {
                    binding.header.text = "هيا نتعلم!"
                    binding.subHeader.text = "Let's Learn!"
                }
                "hi" -> {
                    binding.header.text = "चलो सीखें!"
                    binding.subHeader.text = "Let's Learn!"
                }
                else -> {
                    binding.header.text = "Let's Learn!"
                    binding.subHeader.text = "آؤ سیکھیں!"
                }
            }
            binding.subHeader.visibility = View.VISIBLE
            binding.recycler.layoutManager = GridLayoutManager(requireContext(), 4)
            val localizedList = kidsCategories(lang).map { AppLanguageHelper.localizeMenuItem(it, lang) }
            binding.recycler.adapter = MenuAdapter(localizedList) { navigateTo(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        val adult = Prefs(requireContext()).mode == "adult"
        if (!adult && _binding != null) {
            val lang = Prefs(requireContext()).language
            val localizedList = kidsCategories(lang).map { AppLanguageHelper.localizeMenuItem(it, lang) }
            binding.recycler.adapter = MenuAdapter(localizedList) { navigateTo(it) }
        }
    }

    private fun navigateTo(item: MenuItem) {
        val args = Bundle().apply { item.argTitle?.let { putString("title", it) } }
        findNavController().navigate(item.destId, args)
    }

    private fun kidsCategories(langCode: String): List<MenuItem> {
        val coral = R.color.coral; val sage = R.color.sage
        val lav = R.color.lavender; val pri = R.color.primary
        val cg = R.id.cardGridFragment
        val code = langCode.lowercase()

        val alphabetItem = when (code) {
            "ur" -> MenuItem("Urdu", "اردو حروفِ تہجی", "✍️", lav, cg, "Urdu")
            "ar" -> MenuItem("Arabic", "الحروف العربية", "📜", pri, cg, "Arabic")
            else -> MenuItem("ABC", "اے بی سی", "🔤", coral, cg, "ABC")
        }

        return listOf(
            alphabetItem,
            MenuItem("Numbers", "گنتی", "🔢", sage, cg, "Numbers"),
            MenuItem("Multiplication Tables", "پہاڑے", "✖️", coral, R.id.tablesFragment),
            MenuItem("Dodging Tables", "ڈاجنگ", "🔀", lav, R.id.dodgingTablesFragment),
            MenuItem("Tenses", "ٹینس", "⏱️", pri, R.id.tensesFragment),
            MenuItem("Vowels", "حروفِ علت", "🔡", sage, R.id.vowelsFragment),
            MenuItem("Basics", "بنیادی باتیں", "💡", coral, R.id.basicsFragment),
            MenuItem("Colors", "رنگ", "🎨", coral, cg, "Colors"),
            MenuItem("Shapes", "شکلیں", "🔷", sage, cg, "Shapes"),
            MenuItem("Body Parts", "اعضاء", "🧍", lav, cg, "Body Parts"),
            MenuItem("Animals", "جانور", "🦁", pri, cg, "Animals"),
            MenuItem("Fruits", "پھل", "🍎", coral, cg, "Fruits"),
            MenuItem("Science", "سائنس", "🔬", sage, cg, "Science"),
            MenuItem("Poems", "نظمیں", "🎵", lav, R.id.poemsFragment),
            MenuItem("Stories", "کہانیاں", "📚", pri, R.id.storiesFragment),
            MenuItem("GK Quiz", "معلوماتِ عامہ", "❓", coral, R.id.quizFragment),
            MenuItem("Drawing", "ڈرائنگ", "✏️", sage, R.id.drawingFragment),
            MenuItem("Mini Games", "کھیل", "🎮", lav, R.id.gamesFragment),
            MenuItem("Quran", "قرآن", "📖", pri, R.id.quranFragment),
            MenuItem("6 Kalmas", "چھ کلمے", "☪️", coral, R.id.kalmasFragment),
            MenuItem("Daily Duas", "روزانہ دعائیں", "🤲", sage, R.id.duasFragment),
            MenuItem("Namaz", "نماز", "🕌", lav, R.id.namazFragment),
            MenuItem("Wudu", "وضو", "💧", pri, R.id.wuduFragment),
            MenuItem("99 Names", "٩٩ نام", "✨", coral, R.id.namesFragment),
            MenuItem("Prayer Times", "اوقاتِ نماز", "🕐", sage, R.id.prayerTimesFragment),
            MenuItem("Leaderboard", "اسکور بورڈ", "🏆", lav, R.id.leaderboardFragment)
        )
    }

    private fun adultTools(): List<MenuItem> {
        val pri = R.color.primary
        return listOf(
            MenuItem("Daily Planner", "", "📅", pri, R.id.plannerFragment),
            MenuItem("Notes", "", "📝", pri, R.id.notesFragment),
            MenuItem("Expenses", "", "💲", pri, R.id.expensesFragment),
            MenuItem("Habit Tracker", "", "✅", pri, R.id.habitsFragment),
            MenuItem("Goals", "", "🎯", pri, R.id.goalsFragment),
            MenuItem("Tasbeeh", "", "📿", pri, R.id.tasbeehFragment),
            MenuItem("Water", "", "💧", pri, R.id.waterFragment),
            MenuItem("English", "", "💬", pri, R.id.englishFragment),
            MenuItem("Freelancing", "", "💼", pri, R.id.freelanceFragment),
            MenuItem("Business", "", "📈", pri, R.id.businessFragment)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
