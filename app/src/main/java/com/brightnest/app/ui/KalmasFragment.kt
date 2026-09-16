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

data class KalmaDataDetail(
    val id: Int,
    val titleEn: String,
    val titleUr: String,
    val titleAr: String,
    val titleHi: String,
    val arabic: String,
    val transliteration: String,
    val translationEn: String,
    val translationUr: String,
    val translationAr: String,
    val translationHi: String
) {
    fun getLocalizedTitle(langCode: String): String = when (langCode.lowercase()) {
        "ur" -> titleUr
        "ar" -> titleAr
        "hi" -> titleHi
        else -> titleEn
    }

    fun getLocalizedTranslation(langCode: String): String = when (langCode.lowercase()) {
        "ur" -> translationUr
        "ar" -> translationAr
        "hi" -> translationHi
        else -> translationEn
    }

    fun getSpokenTitle(langCode: String): String = when (langCode.lowercase()) {
        "ur" -> titleUr
        "ar" -> titleAr
        "hi" -> titleHi
        else -> titleEn
    }
}

class KalmasFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var arabicAvailable = false
    private var speakJob: Job? = null

    private val kalmasList = listOf(
        KalmaDataDetail(
            id = 1,
            titleEn = "First Kalma — Tayyab",
            titleUr = "پہلا کلمہ طیب",
            titleAr = "الكلمة الأولى: الكلمة الطيبة",
            titleHi = "पहला कलमा: तय्यब",
            arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ مُحَمَّدٌ رَسُولُ اللَّهِ",
            transliteration = "La ilaha ill-Allahu Muhammadur Rasul-Allah",
            translationEn = "There is none worthy of worship except Allah, and Muhammad is the Messenger of Allah.",
            translationUr = "اللہ کے سوا کوئی عبادت کے لائق نہیں، محمد ﷺ اللہ کے رسول ہیں۔",
            translationAr = "لا إله إلا الله محمد رسول الله.",
            translationHi = "अल्लाह के सिवा कोई इबादत के लायक नहीं, मुहम्मद ﷺ अल्लाह के रसूल हैं।"
        ),
        KalmaDataDetail(
            id = 2,
            titleEn = "Second Kalma — Shahadat",
            titleUr = "دوسرا کلمہ شہادت",
            titleAr = "الكلمة الثانية: كلمة الشهادة",
            titleHi = "दूसरा कलमा: शहादत",
            arabic = "أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            transliteration = "Ashhadu an la ilaha ill-Allahu wahdahu la sharika lahu wa ashhadu anna Muhammadan abduhu wa rasuluhu",
            translationEn = "I bear witness that none is worthy of worship but Allah, He is Alone with no partner, and I bear witness that Muhammad is His servant and Messenger.",
            translationUr = "میں گواہی دیتا ہوں کہ اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے، اس کا کوئی شریک نہیں، اور میں گواہی دیتا ہوں کہ حضرت محمد ﷺ اس کے بندے اور رسول ہیں۔",
            translationAr = "أشهد أن لا إله إلا الله وحده لا شريك له وأشهد أن محمداً عبده ورسوله.",
            translationHi = "मैं गवाही देता हूँ कि अल्लाह के सिवा कोई माबूद नहीं, वह अकेला है, उसका कोई शरीक नहीं, और मैं गवाही देता हूँ कि मुहम्मद ﷺ उसके बन्दे और रसूल हैं।"
        ),
        KalmaDataDetail(
            id = 3,
            titleEn = "Third Kalma — Tamjeed",
            titleUr = "تیسرا کلمہ تمجید",
            titleAr = "الكلمة الثالثة: كلمة التمجيد",
            titleHi = "तीसरा कलमा: तमजीद",
            arabic = "سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ وَلَا إِلَٰهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            transliteration = "Subhan-Allahi wal-hamdu lillahi wa la ilaha ill-Allahu wa-Allahu akbar, wa la hawla wa la quwwata illa billahil-aliyyil-azeem",
            translationEn = "Glory be to Allah, and all praise is due to Allah, and there is none worthy of worship except Allah, and Allah is the Greatest. And there is no might nor power except with Allah, the Most High, the Most Supreme.",
            translationUr = "اللہ پاک ہے اور تمام تعریفیں اللہ کے لیے ہیں اور اللہ کے سوا کوئی معبود نہیں اور اللہ سب سے بڑا ہے۔ اور گناہوں سے بچنے کی طاقت اور نیکی کرنے کی قوت صرف اللہ کی طرف سے ہے جو بہت بلند اور عظمت والا ہے۔",
            translationAr = "سبحان الله والحمد لله ولا إله إلا الله والله أكبر، ولا حول ولا قوة إلا بالله العلي العظيم.",
            translationHi = "अल्लाह पाक है और सब तारीफें अल्लाह के लिए हैं और अल्लाह के सिवा कोई माबूद नहीं और अल्लाह सबसे बड़ा है। और बुराई से बचने और नेकी करने की ताक़त सिर्फ अल्लाह की तरफ से है जो बहुत बुलंद और अज़ीम है।"
        ),
        KalmaDataDetail(
            id = 4,
            titleEn = "Fourth Kalma — Tauheed",
            titleUr = "چوتھا کلمہ توحید",
            titleAr = "الكلمة الرابعة: كلمة التوحيد",
            titleHi = "चौथा कलमा: तौहीद",
            arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِي وَيُمِيتُ وَهُوَ حَيٌّ لَا يَمُوتُ أَبَدًا أَبَدًا، ذُو الْجَلَالِ وَالْإِكْرَامِ، بِيَدِهِ الْخَيْرُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha ill-Allahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamd, yuhyi wa yumitu wa huwa hayyun la yamutu abadan abada, dhul-jalali wal-ikram, bi-yadihil-khair, wa huwa ala kulli shai'in qadeer",
            translationEn = "There is none worthy of worship except Allah, He is Alone with no partner. To Him belongs the Kingdom and to Him belongs all praise. He gives life and causes death, and He is Living and will never die, ever and ever. Possessor of Majesty and Honor. In His hand is all goodness, and He has power over everything.",
            translationUr = "اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے، اس کا کوئی شریک نہیں۔ اسی کی بادشاہی ہے اور اسی کے لیے تمام تعریف ہے۔ وہی زندہ کرتا ہے اور وہی مارتا ہے، اور وہ ہمیشہ زندہ رہنے والا ہے جسے کبھی موت نہیں آئے گی۔ بڑی عظمت اور بزرگی والا ہے۔ اسی کے ہاتھ میں تمام بھلائی ہے اور وہ ہر چیز پر قادر ہے۔",
            translationAr = "لا إله إلا الله وحده لا شريك له، له الملك وله الحمد، يحيي ويميت وهو حي لا يموت أبداً أبداً، ذو الجلال والإكرام، بيده الخير، وهو على كل شيء قدير.",
            translationHi = "अल्लाह के सिवा कोई माबूद नहीं, वह अकेला है, उसका कोई शरीक नहीं। उसी की बादशाही है और उसी के लिए सब तारीफ है। वही ज़िंदा करता है और वही मारता है, और वह हमेशा ज़िंदा रहने वाला है जिसे कभी मौत नहीं आएगी। बड़ी बुज़ुर्गी और करम वाला है। उसी के हाथ में सारी भलाई है और वह हर चीज़ पर क़ादिर है।"
        ),
        KalmaDataDetail(
            id = 5,
            titleEn = "Fifth Kalma — Astaghfar",
            titleUr = "پانچواں کلمہ استغفار",
            titleAr = "الكلمة الخامسة: كلمة الاستغفار",
            titleHi = "पाँचवाँ कलमा: अस्तग़फ़ार",
            arabic = "أَسْتَغْفِرُ اللَّهَ رَبِّي مِنْ كُلِّ ذَنْبٍ أَذْنَبْتُهُ عَمَدًا أَوْ خَطَأً سِرًّا أَوْ عَلَانِيَةً وَأَتُوبُ إِلَيْهِ مِنَ الذَّنْبِ الَّذِي أَعْلَمُ وَمِنَ الذَّنْبِ الَّذِي لَا أَعْلَمُ، إِنَّكَ أَنْتَ عَلَّامُ الْغُيُوبِ وَسَتَّارُ الْعُيُوبِ وَغَفَّارُ الذُّنُوبِ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            transliteration = "Astaghfirullaha Rabbi min kulli dhambin adhnabtuhu amadan aw khata'an sirran aw alaniyatan wa atubu ilaihi minadh-dhambil-ladhi a'lamu wa minadh-dhambil-ladhi la a'lamu, innaka anta allamul-ghuyubi wa sattarul-uyubi wa ghaffarudh-dhunubi wa la hawla wa la quwwata illa billahil-aliyyil-azeem",
            translationEn = "I seek forgiveness from Allah, my Lord, for every sin I committed knowingly or mistakenly, secretly or openly, and I turn to Him in repentance from the sin that I know and from the sin that I do not know. Certainly You are the Knower of the unseen, the Concealer of faults, and the Forgiver of sins. And there is no might nor power except with Allah, the Most High, the Most Supreme.",
            translationUr = "میں اللہ سے اپنے تمام گناہوں کی بخشش مانگتا ہوں جو میں نے جان بوجھ کر کیے یا بھول کر، چھپ کر کیے یا کھلم کھلا، اور میں اس کی بارگاہ میں توبہ کرتا ہوں اس گناہ سے جسے میں جانتا ہوں اور اس گناہ سے جسے میں نہیں جانتا۔ بے شک تو غیبوں کا جاننے والا، عیبوں کو چھپانے والا اور گناہوں کو بخشنے والا ہے، اور گناہوں سے بچنے کی طاقت اور نیکی کی توفیق صرف اللہ کی مدد سے ہے جو بلند اور عظمت والا ہے۔",
            translationAr = "أستغفر الله ربي من كل ذنب أذنبته عمداً أو خطأً سراً أو علانيةً وأتوب إليه من الذنب الذي أعلم ومن الذنب الذي لا أعلم، إنك أنت علام الغيوب وستار العيوب وغفار الذنوب ولا حول ولا قوة إلا بالله العلي العظيم.",
            translationHi = "मैं अल्लाह से अपने तमाम गुनाहों की बख्शिश मांगता हूँ जो मैंने जानबूझकर किए या भूलकर, छुपकर किए या खुलेआम, और मैं उसकी बारगाह में तौबा करता हूँ उस गुनाह से जिसे मैं जानता हूँ और उस गुनाह से जिसे मैं नहीं जानता। बेशक तू ग़ैब को जानने वाला, ऐबों को छुपाने वाला और गुनाहों को बख्शने वाला है।"
        ),
        KalmaDataDetail(
            id = 6,
            titleEn = "Sixth Kalma — Radde Kufr",
            titleUr = "چھٹا کلمہ ردِ کفر",
            titleAr = "الكلمة السادسة: كلمة رد الكفر",
            titleHi = "छठा कलमा: रद्दे कुफ़्र",
            arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ أَنْ أُشْرِكَ بِكَ شَيْئًا وَأَنَا أَعْلَمُ بِهِ وَأَسْتَغْفِرُكَ لِمَا لَا أَعْلَمُ بِهِ تُبْتُ عَنْهُ وَتَبَرَّأْتُ مِنَ الْكُفْرِ وَالشِّرْكِ وَالْكِذْبِ وَالْغِيبَةِ وَالْبِدْعَةِ وَالنَّمِيمَةِ وَالْفَوَاحِشِ وَالْبُهْتَانِ وَالْمَعَاصِي كُلِّهَا وَأَسْلَمْتُ وَأَقُولُ لَا إِلَٰهَ إِلَّا اللَّهُ مُحَمَّدٌ رَسُولُ اللَّهِ",
            transliteration = "Allahumma inni a'udhu bika min an ushrika bika shai'an wa ana a'lamu bihi wa astaghfiruka lima la a'lamu bihi tubtu anhu wa tabarra'tu minal-kufri wash-shirki wal-kidhbi wal-gheebati wal-bid'ati wan-nameemati wal-fawahishi wal-buhtani wal-ma'asi kulliha wa aslamtu wa aqulu la ilaha ill-Allahu Muhammadur Rasul-Allah",
            translationEn = "O Allah, I seek refuge in You from associating anything with You while I know it, and I seek Your forgiveness for what I do not know. I repent from it and I dissociate myself from disbelief, polytheism, falsehood, backbiting, innovation, slander, lewdness, false accusation, and all sins whatsoever. I submit and I declare: There is none worthy of worship except Allah, and Muhammad is the Messenger of Allah.",
            translationUr = "اے اللہ! میں تیری پناہ مانگتا ہوں اس بات سے کہ میں کسی چیز کو تیرا شریک ٹھہراؤں جان بوجھ کر، اور میں تجھ سے بخشش مانگتا ہوں اس گناہ کی جس کو میں نہیں جانتا۔ میں نے اس سے توبہ کی اور میں بیزار ہوا کفر سے، شرک سے، جھوٹ سے، غیبت سے، بدعت سے، چغلی سے، بے حیائی سے، تہمت لگانے سے اور تمام گناہوں سے، اور میں اسلام لایا اور میں کہتا ہوں: اللہ کے سوا کوئی معبود نہیں، محمد ﷺ اللہ کے رسول ہیں۔",
            translationAr = "اللهم إني أعوذ بك من أن أشرك بك شيئاً وأنا أعلم به وأستغفرك لما لا أعلم به تبت عنه وتبرأت من الكفر والشرك والكذب والغيبة والبدعة والنميمة والفواحش والبهتان والمعاصي كلها وأسلمت وأقول لا إله إلا الله محمد رسول الله.",
            translationHi = "ऐ अल्लाह! मैं तेरी पनाह मांगता हूँ इस बात से कि मैं किसी चीज़ को तेरा शरीक ठहराऊँ जानबूझकर, और मैं तुझसे बख्शिश मांगता हूँ उस गुनाह की जिसे मैं नहीं जानता। मैंने उससे तौबा की और मैं बेज़ार हुआ कुफ़्र से, शिर्क से, झूठ से, ग़ीबत से اور تمام गुनाहों سے، اور میں اسلام لایا اور کہتا ہوں: اللہ کے سِوا کوئی معبود نہیں، محمّد ﷺ اللہ کے رسول ہیں۔"
        )
    )

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
        val langCode = try { Prefs(requireContext()).language.lowercase() } catch (e: Exception) { "en" }

        binding.headerTitle.text = "Six Kalmas"
        binding.headerSubTitle.text = "چھ کلمے"
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        AppLanguageHelper.localizeViewTree(binding.root, langCode)

        // Initialise TTS engine
        tts = TextToSpeech(requireContext(), this)

        viewLifecycleOwner.lifecycleScope.launch {
            val items = kalmasList.map {
                ArabicCardItem(
                    title = it.getLocalizedTitle(langCode),
                    arabic = it.arabic,
                    transliteration = it.transliteration,
                    translation = it.getLocalizedTranslation(langCode)
                )
            }
            _binding?.recycler?.adapter = ArabicCardAdapter(items) { item ->
                val index = items.indexOf(item)
                val detail = if (index in kalmasList.indices) kalmasList[index] else null
                if (detail != null) {
                    speakKalma(detail, langCode)
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

        tts?.setSpeechRate(0.75f)
        tts?.setPitch(0.92f)
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
     * Speaks the Kalma:
     * 1. Reads the Kalma Title in the selected language.
     * 2. Recites Ta'awwudh (Azubillah).
     * 3. Recites Tasmiyah (Bismillah).
     * 4. Recites the Kalma Arabic & transliteration.
     */
    private fun speakKalma(kalma: KalmaDataDetail, langCode: String) {
        if (!ttsReady) {
            Toast.makeText(context, "Please wait, audio is loading...", Toast.LENGTH_SHORT).show()
            return
        }

        speakJob?.cancel()
        tts?.stop()

        val spokenTitle = kalma.getSpokenTitle(langCode)
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

        speakJob = viewLifecycleOwner.lifecycleScope.launch {
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
                        titleTextToSpeak = kalma.titleEn
                    }
                }
                "ar" -> {
                    val arLoc = Locale("ar")
                    if (isLocaleAvailable(arLoc)) {
                        titleLocale = arLoc
                        titleTextToSpeak = spokenTitle
                    } else {
                        titleLocale = Locale.ENGLISH
                        titleTextToSpeak = kalma.titleEn
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
                        titleTextToSpeak = kalma.titleEn
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

            val titleUtteranceId = "kalma_title_${System.currentTimeMillis()}"
            awaitUtterance(titleTextToSpeak, titleUtteranceId)

            delay(400)

            if (arabicAvailable) {
                tts?.language = Locale("ar")
                tts?.setSpeechRate(0.75f)
                tts?.setPitch(0.92f)

                tts?.speak(azubillahAr, TextToSpeech.QUEUE_FLUSH, null, "azubillah")
                tts?.speak(bismillahAr, TextToSpeech.QUEUE_ADD, null, "bismillah")
                tts?.speak(kalma.arabic, TextToSpeech.QUEUE_ADD, null, "kalma")
            } else {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(0.78f)
                tts?.setPitch(0.95f)

                tts?.speak(azubillahEn, TextToSpeech.QUEUE_FLUSH, null, "azubillah")
                tts?.speak(bismillahEn, TextToSpeech.QUEUE_ADD, null, "bismillah")
                tts?.speak(kalma.transliteration, TextToSpeech.QUEUE_ADD, null, "kalma")
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
