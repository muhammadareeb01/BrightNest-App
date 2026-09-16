package com.brightnest.app.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())
    private var clockText: TextView? = null
    private var tvNextPrayerName: TextView? = null
    private var tvNextPrayerTime: TextView? = null
    private var prefsRef: Prefs? = null
    private val timeFmt = SimpleDateFormat("h:mm:ss a", Locale.getDefault())

    private val tick = object : Runnable {
        override fun run() {
            val prefs = prefsRef ?: return
            val offset = prefs.clockOffsetMs
            clockText?.text = "🕐 " + timeFmt.format(Date(System.currentTimeMillis() + offset))
            updateNextPrayerInfo(prefs)
            handler.postDelayed(this, 1000)
        }
    }

    private fun toMinutes(t: String): Int? {
        val m = Regex("^(\\d{1,2}):(\\d{2})\\s*(AM|PM)?$", RegexOption.IGNORE_CASE).find(t.trim()) ?: return null
        var h = m.groupValues[1].toIntOrNull() ?: return null
        val min = m.groupValues[2].toIntOrNull() ?: return null
        val period = m.groupValues[3].uppercase(Locale.US)
        if (period == "PM" && h != 12) h += 12
        if (period == "AM" && h == 12) h = 0
        return h * 60 + min
    }

    private fun updateNextPrayerInfo(prefs: Prefs) {
        if (tvNextPrayerName == null || tvNextPrayerTime == null) return
        val offset = prefs.clockOffsetMs
        val now = Date(System.currentTimeMillis() + offset)
        val cal = java.util.Calendar.getInstance().apply { time = now }
        val nowMin = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)

        val prayers = listOf(
            Pair("Fajr", prefs.prayerFajr),
            Pair("Dhuhr", prefs.prayerDhuhr),
            Pair("Asr", prefs.prayerAsr),
            Pair("Maghrib", prefs.prayerMaghrib),
            Pair("Isha", prefs.prayerIsha)
        )

        var nextName: String? = null
        var nextTimeStr: String? = null
        var nextMins = Int.MAX_VALUE

        for ((name, timeStr) in prayers) {
            val t = toMinutes(timeStr) ?: continue
            if (t > nowMin && t < nextMins) {
                nextMins = t
                nextName = name
                nextTimeStr = timeStr
            }
        }

        if (nextName == null) {
            val first = prayers.firstOrNull { toMinutes(it.second) != null }
            if (first != null) {
                nextName = first.first
                nextTimeStr = first.second
            }
        }

        if (nextName != null && nextTimeStr != null) {
            tvNextPrayerName?.text = nextName
            tvNextPrayerTime?.text = nextTimeStr
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = Prefs(requireContext())
        val adult = prefs.mode == "adult"

        updateTabs(adult)
        binding.tabKids.setOnClickListener { switchMode(prefs, "kids") }
        binding.tabAdult.setOnClickListener { switchMode(prefs, "adult") }

        val content = layoutInflater.inflate(
            if (adult) R.layout.home_adult else R.layout.home_kids,
            binding.contentContainer,
            false
        )
        binding.contentContainer.addView(content)

        val name = prefs.userName.ifEmpty { "Friend" }
        val greeting = content.findViewById<TextView>(R.id.tvGreeting)
        clockText = content.findViewById(R.id.clockText)
        prefsRef = prefs

        val clockPill = content.findViewById<View>(R.id.clockPill)
        val openPrayer = View.OnClickListener {
            findNavController().navigate(R.id.prayerTimesFragment)
        }
        clockPill?.setOnClickListener(openPrayer)
        clockText?.setOnClickListener(openPrayer)

        if (adult) {
            greeting.text = "Good morning, $name"
            tvNextPrayerName = content.findViewById(R.id.tvNextPrayerName)
            tvNextPrayerTime = content.findViewById(R.id.tvNextPrayerTime)
            content.findViewById<View>(R.id.cardNextPrayer)?.setOnClickListener(openPrayer)
            updateNextPrayerInfo(prefs)
        } else {
            greeting.text = "Assalamu Alaikum, $name!"
            content.findViewById<TextView>(R.id.statStreak).text = prefs.streak.toString()
            content.findViewById<TextView>(R.id.statStars).text = prefs.stars.toString()
            content.findViewById<TextView>(R.id.statCoins).text = prefs.coins.toString()
            content.findViewById<View>(R.id.storyCard).setOnClickListener {
                findNavController().navigate(R.id.storiesFragment)
            }
        }
        com.brightnest.app.AppLanguageHelper.localizeViewTree(binding.root, prefs.language)
    }

    private fun switchMode(prefs: Prefs, mode: String) {
        if (prefs.mode == mode) return
        if (mode == "adult") {
            PinDialogHelper.show(requireContext(), prefs, onSuccess = {
                prefs.mode = "adult"
                requireActivity().recreate()
            })
            return
        }
        prefs.mode = mode
        requireActivity().recreate()
    }

    private fun updateTabs(adult: Boolean) {
        val primary = ContextCompat.getColor(requireContext(), R.color.primary)
        val muted = ContextCompat.getColor(requireContext(), R.color.muted_foreground)
        if (adult) {
            binding.tabAdult.setBackgroundResource(R.drawable.mode_indicator)
            binding.tabAdult.setTextColor(primary)
            binding.tabKids.background = null
            binding.tabKids.setTextColor(muted)
        } else {
            binding.tabKids.setBackgroundResource(R.drawable.mode_indicator)
            binding.tabKids.setTextColor(primary)
            binding.tabAdult.background = null
            binding.tabAdult.setTextColor(muted)
        }
        _binding?.let {
            val prefs = Prefs(requireContext())
            com.brightnest.app.AppLanguageHelper.localizeViewTree(it.root, prefs.language)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(tick)

        // Daily streak tracking & badge
        val prefs = prefsRef ?: return
        if (prefs.mode != "adult") {
            val newStreak = prefs.checkAndUpdateStreak()
            // Refresh streak display if kids home is visible
            view?.let { v ->
                val statStreak = v.findViewById<TextView>(R.id.statStreak)
                statStreak?.text = newStreak.toString()
            }
            // Unlock streak_7 badge
            if (newStreak >= 7) {
                val isNew = prefs.unlockBadge("streak_7")
                if (isNew) {
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                com.brightnest.app.data.FirestoreRepository.saveKidsProgress(
                                    uid,
                                    com.brightnest.app.data.KidsProgress(
                                        coins = prefs.coins,
                                        stars = prefs.stars,
                                        streak = prefs.streak,
                                        badges = prefs.badges.toList()
                                    )
                                )
                            } catch (e: Exception) {
                                android.util.Log.w("HomeFragment", "Streak badge sync failed", e)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(tick)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(tick)
        clockText = null
        tvNextPrayerName = null
        tvNextPrayerTime = null
        _binding = null
    }
}
