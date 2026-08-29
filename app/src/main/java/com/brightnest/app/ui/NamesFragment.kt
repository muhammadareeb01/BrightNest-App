package com.brightnest.app.ui

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentNamesBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class NamesFragment : Fragment() {

    private var _binding: FragmentNamesBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private lateinit var adapter: NameAdapter
    private var selected: NameItem? = null
    private var playAllJob: Job? = null
    private var isPlayingAll = false

    companion object {
        val allNames = listOf(
            NameItem(1, "ٱلرَّحْمَٰنُ", "Ar-Rahman", "The Most Compassionate", "نہایت مہربان"),
            NameItem(2, "ٱلرَّحِيمُ", "Ar-Raheem", "The Most Merciful", "بہت رحم کرنے والا"),
            NameItem(3, "ٱلْمَلِكُ", "Al-Malik", "The King", "بادشاہ"),
            NameItem(4, "ٱلْقُدُّوسُ", "Al-Quddus", "The Most Holy", "نہایت پاک"),
            NameItem(5, "ٱلسَّلَامُ", "As-Salam", "The Source of Peace", "سلامتی والا"),
            NameItem(6, "ٱلْمُؤْمِنُ", "Al-Mu'min", "The Guarantor of Faith", "امن دینے والا"),
            NameItem(7, "ٱلْمُهَيْمِنُ", "Al-Muhaymin", "The Guardian", "نگہبان"),
            NameItem(8, "ٱلْعَزِيزُ", "Al-'Aziz", "The Almighty", "غالب"),
            NameItem(9, "ٱلْجَبَّارُ", "Al-Jabbar", "The Compeller", "زبردست"),
            NameItem(10, "ٱلْمُتَكَبِّرُ", "Al-Mutakabbir", "The Supreme", "بڑائی والا"),
            NameItem(11, "ٱلْخَالِقُ", "Al-Khaliq", "The Creator", "پیدا کرنے والا"),
            NameItem(12, "ٱلْبَارِئُ", "Al-Bari'", "The Maker", "بنانے والا"),
            NameItem(13, "ٱلْمُصَوِّرُ", "Al-Musawwir", "The Fashioner", "صورت بنانے والا"),
            NameItem(14, "ٱلْغَفَّارُ", "Al-Ghaffar", "The Ever-Forgiving", "بہت بخشنے والا"),
            NameItem(15, "ٱلْقَهَّارُ", "Al-Qahhar", "The Subduer", "قہر والا"),
            NameItem(16, "ٱلْوَهَّابُ", "Al-Wahhab", "The Bestower", "بہت عطا کرنے والا"),
            NameItem(17, "ٱلرَّزَّاقُ", "Ar-Razzaq", "The Provider", "رزق دینے والا"),
            NameItem(18, "ٱلْفَتَّاحُ", "Al-Fattah", "The Opener", "کھولنے والا"),
            NameItem(19, "ٱلْعَلِيمُ", "Al-'Aleem", "The All-Knowing", "سب کچھ جاننے والا"),
            NameItem(20, "ٱلْقَابِضُ", "Al-Qabid", "The Withholder", "روکنے والا"),
            NameItem(21, "ٱلْبَاسِطُ", "Al-Basit", "The Extender", "کشادگی دینے والا"),
            NameItem(22, "ٱلْخَافِضُ", "Al-Khafid", "The Abaser", "پست کرنے والا"),
            NameItem(23, "ٱلرَّافِعُ", "Ar-Rafi'", "The Exalter", "بلند کرنے والا"),
            NameItem(24, "ٱلْمُعِزُّ", "Al-Mu'izz", "The Honorer", "عزت دینے والا"),
            NameItem(25, "ٱلْمُذِلُّ", "Al-Muzil", "The Humiliator", "ذلت دینے والا"),
            NameItem(26, "ٱلسَّمِيعُ", "As-Samee'", "The All-Hearing", "سب سننے والا"),
            NameItem(27, "ٱلْبَصِيرُ", "Al-Baseer", "The All-Seeing", "سب دیکھنے والا"),
            NameItem(28, "ٱلْحَكَمُ", "Al-Hakam", "The Judge", "فیصلہ کرنے والا"),
            NameItem(29, "ٱلْعَدْلُ", "Al-'Adl", "The Just", "انصاف والا"),
            NameItem(30, "ٱللَّطِيفُ", "Al-Lateef", "The Subtle One", "باریک بین"),
            NameItem(31, "ٱلْخَبِيرُ", "Al-Khabeer", "The All-Aware", "خبر رکھنے والا"),
            NameItem(32, "ٱلْحَلِيمُ", "Al-Haleem", "The Forbearing", "بردبار"),
            NameItem(33, "ٱلْعَظِيمُ", "Al-'Azeem", "The Magnificent", "عظمت والا"),
            NameItem(34, "ٱلْغَفُورُ", "Al-Ghafoor", "The Forgiving", "بخشنے والا"),
            NameItem(35, "ٱلشَّكُورُ", "Ash-Shakoor", "The Appreciative", "قدردان"),
            NameItem(36, "ٱلْعَلِيُّ", "Al-'Aliyy", "The Highest", "سب سے بلند"),
            NameItem(37, "ٱلْكَبِيرُ", "Al-Kabeer", "The Greatest", "سب سے بڑا"),
            NameItem(38, "ٱلْحَفِيظُ", "Al-Hafeez", "The Preserver", "حفاظت کرنے والا"),
            NameItem(39, "ٱلْمُقِيتُ", "Al-Muqeet", "The Sustainer", "روزی رساں"),
            NameItem(40, "ٱلْحَسِيبُ", "Al-Haseeb", "The Reckoner", "حساب لینے والا"),
            NameItem(41, "ٱلْجَلِيلُ", "Al-Jaleel", "The Majestic", "بزرگی والا"),
            NameItem(42, "ٱلْكَرِيمُ", "Al-Kareem", "The Generous", "کرم کرنے والا"),
            NameItem(43, "ٱلرَّقِيبُ", "Ar-Raqeeb", "The Watchful", "نگہبان"),
            NameItem(44, "ٱلْمُجِيبُ", "Al-Mujeeb", "The Responsive One", "دعا قبول کرنے والا"),
            NameItem(45, "ٱلْوَاسِعُ", "Al-Wasi'", "The All-Encompassing", "وسعت والا"),
            NameItem(46, "ٱلْحَكِيمُ", "Al-Hakeem", "The Wise", "حکمت والا"),
            NameItem(47, "ٱلْوَدُودُ", "Al-Wadood", "The Loving One", "محبت کرنے والا"),
            NameItem(48, "ٱلْمَجِيدُ", "Al-Majeed", "The Glorious One", "بزرگی والا"),
            NameItem(49, "ٱلْبَاعِثُ", "Al-Ba'ith", "The Resurrecter", "اٹھانے والا"),
            NameItem(50, "ٱلشَّهِيدُ", "Ash-Shaheed", "The Witness", "گواہ"),
            NameItem(51, "ٱلْحَقُّ", "Al-Haqq", "The Truth", "برحق"),
            NameItem(52, "ٱلْوَكِيلُ", "Al-Wakeel", "The Trustee", "کارساز"),
            NameItem(53, "ٱلْقَوِيُّ", "Al-Qawiyy", "The Strong One", "طاقتور"),
            NameItem(54, "ٱلْمَتِينُ", "Al-Mateen", "The Firm One", "مضبوط"),
            NameItem(55, "ٱلْوَلِيُّ", "Al-Waliyy", "The Protecting Friend", "حمایتی"),
            NameItem(56, "ٱلْحَمِيدُ", "Al-Hameed", "The Praiseworthy", "تعریف والا"),
            NameItem(57, "ٱلْمُحْصِيُ", "Al-Muhsee", "The Counter", "شمار کرنے والا"),
            NameItem(58, "ٱلْمُبْدِئُ", "Al-Mubdi'", "The Originator", "پہلی بار پیدا کرنے والا"),
            NameItem(59, "ٱلْمُعِيدُ", "Al-Mu'eed", "The Restorer", "دوبارہ پیدا کرنے والا"),
            NameItem(60, "ٱلْمُحْيِي", "Al-Muhyi", "The Giver of Life", "زندگی دینے والا"),
            NameItem(61, "ٱلْمُمِيتُ", "Al-Mumeet", "The Taker of Life", "موت دینے والا"),
            NameItem(62, "ٱلْحَيُّ", "Al-Hayy", "The Ever-Living", "ہمیشہ زندہ رہنے والا"),
            NameItem(63, "ٱلْقَيُّومُ", "Al-Qayyuum", "The Self-Existing", "سب کو تھامنے والا"),
            NameItem(64, "ٱلْوَاجِدُ", "Al-Wajid", "The Finder", "پانے والا"),
            NameItem(65, "ٱلْمَاجِدُ", "Al-Majid", "The Noble", "بزرگی والا"),
            NameItem(66, "ٱلْوَاحِدُ", "Al-Wahid", "The Unique One", "ایک"),
            NameItem(67, "ٱلْأَحَد", "Al-Ahad", "The One", "اکیلا"),
            NameItem(68, "ٱلصَّمَدُ", "As-Samad", "The Eternal", "بے نیاز"),
            NameItem(69, "ٱلْقَادِرُ", "Al-Qadir", "The Able", "قدرت والا"),
            NameItem(70, "ٱلْمُقْتَدِرُ", "Al-Muqtadir", "The Powerful", "کامل قدرت والا"),
            NameItem(71, "ٱلْمُقَدِّمُ", "Al-Muqaddim", "The Expediter", "آگے کرنے والا"),
            NameItem(72, "ٱلْمُؤَخِّرُ", "Al-Mu'akhkhir", "The Delayer", "پیچھے کرنے والا"),
            NameItem(73, "ٱلْأَوَّلُ", "Al-Awwal", "The First", "سب سے پہلے"),
            NameItem(74, "ٱلْآخِرُ", "Al-Akhir", "The Last", "سب کے بعد"),
            NameItem(75, "ٱلظَّاهِرُ", "Az-Zahir", "The Manifest", "ظاہر"),
            NameItem(76, "ٱلْبَاطِنُ", "Al-Batin", "The Hidden", "پوشیدہ"),
            NameItem(77, "ٱلْوَالِي", "Al-Wali", "The Governor", "مالک ومختار"),
            NameItem(78, "ٱلْمُتَعَالِي", "Al-Muta'ali", "The Most High", "عالی شان"),
            NameItem(79, "ٱلْبَرُّ", "Al-Barr", "The Source of Goodness", "احسان کرنے والا"),
            NameItem(80, "ٱلتَّوَّابُ", "At-Tawwab", "The Acceptor of Repentance", "توبہ قبول کرنے والا"),
            NameItem(81, "ٱلْمُنْتَقِمُ", "Al-Muntaqim", "The Avenger", "انتقام لینے والا"),
            NameItem(82, "ٱلْعَفُوُّ", "Al-'Afuww", "The Pardoner", "معاف کرنے والا"),
            NameItem(83, "ٱلرَّءُوفُ", "Ar-Ra'oof", "The Compassionate", "نرم دل"),
            NameItem(84, "مَالِكُ ٱلْمُلْكِ", "Malik-ul-Mulk", "The Owner of all Sovereignty", "ملک کا مالک"),
            NameItem(85, "ذُو ٱلْجَلَالِ وَٱلْإِكْرَامِ", "Dhul-Jalali wal-Ikram", "Lord of Majesty and Bounty", "عزت اور جلال والا"),
            NameItem(86, "ٱلْمُقْسِطُ", "Al-Muqsit", "The Equitable One", "انصاف کرنے والا"),
            NameItem(87, "ٱلْجَامِعُ", "Al-Jami'", "The Gatherer", "اکٹھا کرنے والا"),
            NameItem(88, "ٱلْغَنِيُّ", "Al-Ghaniyy", "The Self-Sufficient", "بے نیاز"),
            NameItem(89, "ٱلْمُغْنِي", "Al-Mughni", "The Enricher", "غنی کرنے والا"),
            NameItem(90, "ٱلْمَانِعُ", "Al-Mani'", "The Withholder", "روکنے والا"),
            NameItem(91, "ٱلضَّارُّ", "Ad-Darr", "The Distresser", "نقصان دینے والا"),
            NameItem(92, "ٱلنَّافِعُ", "An-Nafi'", "The Benefactor", "نفع دینے والا"),
            NameItem(93, "ٱلنُّورُ", "An-Noor", "The Light", "روشنی"),
            NameItem(94, "ٱلْهَادِي", "Al-Hadi", "The Guide", "ہدایت دینے والا"),
            NameItem(95, "ٱلْبَدِيعُ", "Al-Badee'", "The Originator", "بے مثل پیدا کرنے والا"),
            NameItem(96, "ٱلْبَاقِي", "Al-Baqi", "The Everlasting", "ہمیشہ باقی"),
            NameItem(97, "ٱلْوَارِثُ", "Al-Warith", "The Inheritor", "وارث"),
            NameItem(98, "ٱلرَّشِيدُ", "Ar-Rasheed", "The Guide to Right", "ہدایت کا مالک"),
            NameItem(99, "ٱلصَّبُورُ", "As-Saboor", "The Most Patient", "بہت صبر والا")
        )
    }

    private val names = allNames

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                tts?.language = Locale("ar")
                tts?.setSpeechRate(0.85f)
            }
        }

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnHeaderPlayAll.setOnClickListener { togglePlayAll() }
        binding.btnPlayAll.setOnClickListener { togglePlayAll() }

        adapter = NameAdapter(names) { openName(it) }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString().orEmpty()
                binding.clear.visibility = if (q.isNotEmpty()) View.VISIBLE else View.GONE
                filter(q)
            }
        })
        binding.clear.setOnClickListener { binding.search.setText("") }

        binding.overlay.setOnClickListener { closeOverlay() }
        binding.modalCard.setOnClickListener { /* swallow */ }
        binding.mClose.setOnClickListener { closeOverlay() }
        binding.mListen.setOnClickListener { selected?.let { speak(it.ar) } }
    }

    private fun togglePlayAll() {
        if (isPlayingAll) {
            stopPlayAll()
        } else {
            startPlayAll()
        }
    }

    private fun startPlayAll() {
        if (!ttsReady) return
        stopPlayAll()
        isPlayingAll = true
        updatePlayAllUi()

        playAllJob = viewLifecycleOwner.lifecycleScope.launch {
            val currentList = adapter.getItems()
            for (i in currentList.indices) {
                if (!isPlayingAll) break
                val item = currentList[i]
                adapter.playingIndex = i
                binding.recycler.smoothScrollToPosition(i)

                speakNameAndWait(item.ar)
                delay(1300) // ~1.3 to 1.5 second gap between names
            }
            stopPlayAll()
        }
    }

    private fun stopPlayAll() {
        playAllJob?.cancel()
        playAllJob = null
        isPlayingAll = false
        tts?.stop()
        if (_binding != null) {
            adapter.playingIndex = -1
            updatePlayAllUi()
        }
    }

    private fun updatePlayAllUi() {
        val b = _binding ?: return
        if (isPlayingAll) {
            b.btnHeaderPlayAll.text = "⏹️"
            b.tvPlayAllIcon.text = "⏹️"
            b.tvPlayAllText.text = "Stop Playing • تلاوت روکیں"
        } else {
            b.btnHeaderPlayAll.text = "▶️"
            b.tvPlayAllIcon.text = "▶️"
            b.tvPlayAllText.text = "Play All 99 Names • تمام ٩٩ نام سنیں"
        }
    }

    private suspend fun speakNameAndWait(text: String) = suspendCancellableCoroutine<Unit> { cont ->
        if (!ttsReady || _binding == null) {
            cont.resume(Unit)
            return@suspendCancellableCoroutine
        }
        val utteranceId = "name_utterance_${System.currentTimeMillis()}"
        tts?.language = Locale("ar")
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

    private fun filter(query: String) {
        stopPlayAll()
        val q = query.trim()
        if (q.isEmpty()) { adapter.submit(names); return }
        val lower = q.lowercase(Locale.getDefault())
        adapter.submit(names.filter {
            it.tr.lowercase(Locale.getDefault()).contains(lower) ||
                it.en.lowercase(Locale.getDefault()).contains(lower) ||
                it.ar.contains(q) || it.ur.contains(q)
        })
    }

    private fun openName(name: NameItem) {
        stopPlayAll()
        selected = name
        binding.mNum.text = name.n.toString()
        binding.mNum.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ContextCompat.getColor(requireContext(), R.color.primary))
        }
        binding.mAr.text = name.ar
        binding.mTr.text = name.tr
        binding.mEn.text = name.en
        binding.mUr.text = name.ur
        binding.overlay.visibility = View.VISIBLE
    }

    private fun closeOverlay() {
        tts?.stop()
        selected = null
        binding.overlay.visibility = View.GONE
    }

    private fun speak(t: String) {
        if (ttsReady) {
            tts?.stop()
            tts?.language = Locale("ar")
            tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "bn")
        }
    }

    override fun onPause() {
        super.onPause()
        stopPlayAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopPlayAll()
        tts?.stop()
        tts?.shutdown()
        tts = null
        _binding = null
    }
}
