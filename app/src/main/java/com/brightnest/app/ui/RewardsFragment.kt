package com.brightnest.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentRewardsBinding

class RewardsFragment : Fragment() {

    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    data class Badge(
        val id: String,          // key stored in Prefs.badges
        val title: String,
        val urdu: String,
        val emoji: String,
        val description: String,
        val howTo: String
    )

    private val badges = listOf(
        Badge("first_letter",  "First Letter",   "پہلا حرف",        "🔤",
              "You learned your first alphabet letter!",
              "Tap any card in the ABC section."),
        Badge("math_10",       "10 Math Wins",   "١٠ ریاضی جیت",    "➕",
              "A math champion in the making!",
              "Answer 10 math quiz questions correctly."),
        Badge("streak_7",      "7-Day Streak",   "٧ دن مسلسل",      "⚡",
              "Consistency is power — masha'Allah!",
              "Open the app and complete an activity 7 days in a row."),
        Badge("first_surah",   "First Surah",    "پہلی سورۃ",        "📖",
              "You read your first Surah from the Quran.",
              "Tap Surah Al-Fatiha in the Quran section."),
        Badge("animal_master", "Animal Master",  "جانوروں کا ماہر", "🐾",
              "You know your animals!",
              "Tap and learn 8 different animals."),
        Badge("artist",        "Artist",         "فنکار",            "🎨",
              "A future Picasso!",
              "Use the Drawing section 3 times.")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = Prefs(requireContext())
        val adult = prefs.mode == "adult"

        val content = layoutInflater.inflate(
            if (adult) R.layout.rewards_adult else R.layout.rewards_kids,
            binding.container,
            false
        )
        binding.container.addView(content)

        if (adult) {
            content.findViewById<View>(R.id.prayersCard).setOnClickListener {
                findNavController().navigate(R.id.prayerTimesFragment)
            }
            content.findViewById<View>(R.id.namesCard).setOnClickListener {
                findNavController().navigate(R.id.namesFragment)
            }
        } else {
            content.findViewById<TextView>(R.id.heroStars).text = prefs.stars.toString()
            content.findViewById<TextView>(R.id.heroCoins).text = prefs.coins.toString()
            val grid = content.findViewById<GridLayout>(R.id.badgesGrid)
            populateBadges(grid, prefs)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh badge grid whenever we come back (e.g. after earning a badge elsewhere)
        val prefs = Prefs(requireContext())
        if (prefs.mode != "adult" && _binding != null) {
            val grid = binding.container.findViewWithTag<GridLayout>("badgesGrid")
                ?: binding.container.getChildAt(0)?.findViewById(R.id.badgesGrid)
                ?: return
            populateBadges(grid, prefs)
        }
    }

    private fun populateBadges(grid: GridLayout, prefs: Prefs) {
        grid.removeAllViews()
        val unlockedIds = prefs.badges
        val primary = ContextCompat.getColor(requireContext(), R.color.primary)
        val muted   = ContextCompat.getColor(requireContext(), R.color.muted)

        badges.forEach { badge ->
            val unlocked = unlockedIds.contains(badge.id)
            val item = layoutInflater.inflate(R.layout.item_badge, grid, false)

            item.findViewById<TextView>(R.id.badgeEmoji).text = badge.emoji
            item.findViewById<TextView>(R.id.badgeTitle).text = badge.title
            item.findViewById<TextView>(R.id.badgeUrdu).text  = badge.urdu

            val circle = item.findViewById<ImageView>(R.id.badgeCircle)
            val lock   = item.findViewById<TextView>(R.id.badgeLock)

            if (unlocked) {
                circle.setColorFilter(primary)
                lock.visibility = View.GONE
                item.alpha = 1f
            } else {
                circle.setColorFilter(muted)
                lock.visibility = View.VISIBLE
                item.alpha = 0.55f
            }

            item.setOnClickListener { showBadge(badge, unlocked) }

            val lp = GridLayout.LayoutParams().apply {
                width  = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
            }
            grid.addView(item, lp)
        }
    }

    private fun showBadge(badge: Badge, unlocked: Boolean) {
        val status = if (unlocked) "✅  UNLOCKED — Masha'Allah!" else "🔒  LOCKED"
        val msg    = "${badge.description}\n\nHOW TO EARN\n${badge.howTo}\n\nStatus: $status"
        AlertDialog.Builder(requireContext())
            .setTitle("${badge.emoji}  ${badge.title}  ·  ${badge.urdu}")
            .setMessage(msg)
            .setPositiveButton("Got it!") { d, _ -> d.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

