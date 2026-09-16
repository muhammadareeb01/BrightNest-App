package com.brightnest.app.ui

data class DuaDetail(
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
    val translationHi: String,
    val category: String
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

object DuasData {
    val duas: List<DuaDetail> = listOf(
        DuaDetail(
            id = 1,
            titleEn = "Dua Before Eating",
            titleUr = "کھانا کھانے سے پہلے کی دعا",
            titleAr = "دعاء قبل الطعام",
            titleHi = "खाना खाने से पहले की दुआ",
            arabic = "بِسْمِ اللَّهِ وَعَلَىٰ بَرَكَةِ اللَّهِ",
            transliteration = "Bismillahi wa 'ala barakatillah",
            translationEn = "In the name of Allah and with the blessings of Allah.",
            translationUr = "اللہ کے نام کے ساتھ اور اللہ کی برکت پر شروع کرتا ہوں۔",
            translationAr = "بسم الله وعلى بركة الله نبتدئ طعامنا.",
            translationHi = "अल्लाह के नाम के साथ और अल्लाह की बरकत पर शुरू करता हूँ।",
            category = "Daily"
        ),
        DuaDetail(
            id = 2,
            titleEn = "Dua After Eating",
            titleUr = "کھانا کھانے کے بعد کی دعا",
            titleAr = "دعاء بعد الفراغ من الطعام",
            titleHi = "खाना खाने के बाद की दुआ",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
            transliteration = "Alhamdulillahil-ladhi at'amana wa saqana wa ja'alana Muslimeen",
            translationEn = "All praise is due to Allah Who gave us food and drink, and made us Muslims.",
            translationUr = "تمام تعریفیں اللہ کے لیے ہیں جس نے ہمیں کھلایا، پلایا اور مسلمان بنایا۔",
            translationAr = "الحمد لله الذي أطعمنا وسقانا وجعلنا من المسلمين.",
            translationHi = "तमाम तारीफें अल्लाह के लिए हैं जिसने हमें खिलाया, पिलाया और मुसलमान बनाया।",
            category = "Daily"
        ),
        DuaDetail(
            id = 3,
            titleEn = "Dua When Forgetting Before Eating",
            titleUr = "کھانا کھانے کی دعا بھول جانے پر",
            titleAr = "دعاء من نسي التسمية أول الطعام",
            titleHi = "खाना खाने की दुआ भूल जाने पर",
            arabic = "بِسْمِ اللَّهِ أَوَّلَهُ وَآخِرَهُ",
            transliteration = "Bismillahi awwalahu wa akhirahu",
            translationEn = "In the name of Allah at its beginning and at its end.",
            translationUr = "اللہ کے نام کے ساتھ اس کے اول اور آخر میں شروع کرتا ہوں۔",
            translationAr = "بسم الله في أوله وآخره.",
            translationHi = "अल्लाह के नाम के साथ इसके शुरू और आखिर में।",
            category = "Daily"
        ),
        DuaDetail(
            id = 4,
            titleEn = "Dua Before Sleeping",
            titleUr = "سوتے وقت کی دعا",
            titleAr = "دعاء النوم",
            titleHi = "सोते वक्त की दुआ",
            arabic = "اللَّهُمَّ بِاسْمِكَ أَمُوتُ وَأَحْيَا",
            transliteration = "Allahumma bismika amootu wa ahya",
            translationEn = "O Allah, with Your name I die (sleep) and I live (wake).",
            translationUr = "اے اللہ! تیرے ہی نام کے ساتھ میں مرتا ہوں اور جیتا ہوں۔",
            translationAr = "اللهم باسمك أموت وأحيا.",
            translationHi = "ऐ अल्लाह! तेरे नाम के साथ मैं मरता (सोता) हूँ और जीता (जागता) हूँ।",
            category = "Daily"
        ),
        DuaDetail(
            id = 5,
            titleEn = "Dua Upon Waking Up",
            titleUr = "نیند سے بیدار ہونے کی دعا",
            titleAr = "دعاء الاستيقاظ من النوم",
            titleHi = "सो कर उठने की दुआ",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Alhamdulillahil-ladhi ahyana ba'da ma amatana wa ilaihin-nushoor",
            translationEn = "All praise is due to Allah Who brought us to life after causing us to die, and unto Him is the resurrection.",
            translationUr = "تمام تعریفیں اللہ کے لیے ہیں جس نے ہمیں مارنے کے بعد زندگی بخشی اور اسی کی طرف لوٹ کر جانا ہے۔",
            translationAr = "الحمد لله الذي أحيانا بعد ما أماتنا وإليه النشور.",
            translationHi = "सब तारीफें अल्लाह के लिए हैं जिसने हमें मौत के बाद ज़िन्दगी दी और उसी की तरफ उठना है।",
            category = "Daily"
        ),
        DuaDetail(
            id = 6,
            titleEn = "Dua For Entering The Mosque",
            titleUr = "مسجد میں داخل ہونے کی دعا",
            titleAr = "دعاء دخول المسجد",
            titleHi = "मस्जिद में दाखिल होने की दुआ",
            arabic = "بِسْمِ اللَّهِ ، وَالصَّلَاةُ وَالسَّلَامُ عَلَىٰ رَسُولِ اللَّهِ ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            transliteration = "Bismillahi was-salatu was-salamu 'ala Rasulillah, Allahummaf-tah lee abwaba rahmatik",
            translationEn = "In the name of Allah, and peace and blessings be upon the Messenger of Allah. O Allah, open for me the gates of Your mercy.",
            translationUr = "اللہ کے نام کے ساتھ، اور درود و سلام ہو رسول اللہ پر۔ اے اللہ! میرے لیے اپنی رحمت کے دروازے کھول دے۔",
            translationAr = "بسم الله، والصلاة والسلام على رسول الله، اللهم افتح لي أبواب رحمتك.",
            translationHi = "अल्लाह के नाम के साथ, और दुरूद व सलाम हो अल्लाह के रसूल पर। ऐ अल्लाह! मेरे लिए अपनी रहमत के दरवाज़े खोल दे।",
            category = "Mosque"
        ),
        DuaDetail(
            id = 7,
            titleEn = "Dua For Leaving The Mosque",
            titleUr = "مسجد سے نکلنے کی دعا",
            titleAr = "دعاء الخروج من المسجد",
            titleHi = "मस्जिद से निकलने की दुआ",
            arabic = "بِسْمِ اللَّهِ ، وَالصَّلَاةُ وَالسَّلَامُ عَلَىٰ رَسُولِ اللَّهِ ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
            transliteration = "Bismillahi was-salatu was-salamu 'ala Rasulillah, Allahumma innee as-aluka min fadlik",
            translationEn = "In the name of Allah, and peace and blessings be upon the Messenger of Allah. O Allah, I ask You from Your bounty.",
            translationUr = "اللہ کے نام کے ساتھ، اور درود و سلام ہو رسول اللہ پر۔ اے اللہ! میں تجھ سے تیرے فضل کا سوال کرتا ہوں۔",
            translationAr = "بسم الله، والصلاة والسلام على رسول الله، اللهم إني أسألك من فضلك.",
            translationHi = "अल्लाह के नाम के साथ, और दुरूद व सलाम हो अल्लाह के रसूल पर। ऐ अल्लाह! मैं तुझसे तेरे फ़ज़ल का सवाल करता हूँ।",
            category = "Mosque"
        ),
        DuaDetail(
            id = 8,
            titleEn = "Dua For Entering Home",
            titleUr = "گھر میں داخل ہونے کی دعا",
            titleAr = "دعاء دخول المنزل",
            titleHi = "घर में दाखिल होने की दुआ",
            arabic = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَىٰ رَبِّنَا تَوَكَّلْنَا",
            transliteration = "Bismillahi walajna, wa bismillahi kharajna, wa 'ala Rabbina tawakkalna",
            translationEn = "In the name of Allah we enter, and in the name of Allah we leave, and upon our Lord we rely.",
            translationUr = "اللہ کے نام کے ساتھ ہم داخل ہوئے اور اسی کے نام سے نکلے اور اپنے رب پر ہم نے بھروسہ کیا۔",
            translationAr = "بسم الله ولجنا، وبسم الله خرجنا، وعلى ربنا توكلنا.",
            translationHi = "अल्लाह के नाम के साथ हम दाखिल हुए और उसी के नाम से निकले और अपने रब पर हमने भरोसा किया।",
            category = "Home"
        ),
        DuaDetail(
            id = 9,
            titleEn = "Dua For Leaving Home",
            titleUr = "گھر سے نکلتے وقت کی دعا",
            titleAr = "دعاء الخروج من المنزل",
            titleHi = "घर से निकलते वक्त की दुआ",
            arabic = "بِسْمِ اللَّهِ ، تَوَكَّلْتُ عَلَى اللَّهِ ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Bismillahi, tawakkaltu 'alallahi, la hawla wa la quwwata illa billah",
            translationEn = "In the name of Allah, I place my trust in Allah. There is no power nor might except with Allah.",
            translationUr = "اللہ کے نام کے ساتھ، میں نے اللہ پر بھروسہ کیا، برائی سے بچنے اور نیکی کرنے کی طاقت صرف اللہ کی طرف سے ہے۔",
            translationAr = "بسم الله، توكلت على الله، ولا حول ولا قوة إلا بالله.",
            translationHi = "अल्लाह के नाम के साथ, मैंने अल्लाह पर भरोसा किया। बुराई से बचने और नेकी करने की ताक़त सिर्फ अल्लाह की तरफ से है।",
            category = "Home"
        ),
        DuaDetail(
            id = 10,
            titleEn = "Dua For Seeking Knowledge",
            titleUr = "علم میں اضافے کی دعا",
            titleAr = "دعاء طلب العلم النافع",
            titleHi = "इल्म में इज़ाफे की दुआ",
            arabic = "رَبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidnee 'ilma",
            translationEn = "My Lord, increase me in knowledge.",
            translationUr = "اے میرے پروردگار! میرے علم میں اضافہ فرما۔",
            translationAr = "رب زدني علماً نافعاً.",
            translationHi = "ऐ मेरे रब! मेरे इल्म में इज़ाफ़ा फरमा।",
            category = "Learning"
        ),
        DuaDetail(
            id = 11,
            titleEn = "Dua For Entering The Restroom",
            titleUr = "بیت الخلاء میں داخل ہونے کی دعا",
            titleAr = "دعاء دخول الخلاء",
            titleHi = "शौचालय में दाखिल होने की दुआ",
            arabic = "بِسْمِ اللَّهِ ، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
            transliteration = "Bismillahi, Allahumma innee a'oodhu bika minal-khubuthi wal-khaba'ith",
            translationEn = "In the name of Allah. O Allah, I seek refuge in You from all wicked impurities (evil male and female jinn).",
            translationUr = "اللہ کے نام کے ساتھ۔ اے اللہ! میں ناپاک جنات اور شیطانوں سے تیری پناہ مانگتا ہوں۔",
            translationAr = "بسم الله، اللهم إني أعوذ بك من الخبث والخبائث.",
            translationHi = "अल्लाह के नाम के साथ। ऐ अल्लाह! मैं नापाक जिन्नात और शैतानों से तेरी पनाह मांगता हूँ।",
            category = "Daily"
        ),
        DuaDetail(
            id = 12,
            titleEn = "Dua For Leaving The Restroom",
            titleUr = "بیت الخلاء سے نکلنے کی دعا",
            titleAr = "دعاء الخروج من الخلاء",
            titleHi = "शौचालय से निकलने की दुआ",
            arabic = "غُفْرَانَكَ ، الْحَمْدُ لِلَّهِ الَّذِي أَذْهَبَ عَنِّي الْأَذَىٰ وَعَافَانِي",
            transliteration = "Ghufranaka, Alhamdulillahil-ladhee adh-haba 'annil-adha wa 'aafanee",
            translationEn = "I seek Your forgiveness. All praise is due to Allah Who removed from me harm and granted me health.",
            translationUr = "اے اللہ! میں تیری بخشش چاہتا ہوں۔ تمام تعریفیں اللہ کے لیے ہیں جس نے مجھ سے تکلیف دہ چیز دور کی اور مجھے عافیت دی۔",
            translationAr = "غفرانك، الحمد لله الذي أذهب عني الأذى وعافاني.",
            translationHi = "ऐ अल्लाह! मैं तेरी बख्शिश चाहता हूँ। तमाम तारीफें अल्लाह के लिए हैं जिसने मुझसे तकलीफदेह चीज़ दूर की और मुझे आफियत दी।",
            category = "Daily"
        ),
        DuaDetail(
            id = 13,
            titleEn = "Dua When Looking In The Mirror",
            titleUr = "آئینہ دیکھنے کی دعا",
            titleAr = "دعاء النظر في المرآة",
            titleHi = "आईना देखने की दुआ",
            arabic = "اللَّهُمَّ أَنْتَ حَسَّنْتَ خَلْقِي فَحَسِّنْ خُلُقِي",
            transliteration = "Allahumma anta hassanta khalqee fahassin khuluqee",
            translationEn = "O Allah, just as You made my appearance beautiful, make my character beautiful too.",
            translationUr = "اے اللہ! جیسے تو نے میری صورت اچھی بنائی، میرا اخلاق بھی اچھا کر دے۔",
            translationAr = "اللهم كما حسنت خَلقي فحسّن خُلقي.",
            translationHi = "ऐ अल्लाह! जैसे तूने मेरी सूरत अच्छी बनाई, मेरा अख़लाक़ भी अच्छा कर दे।",
            category = "Daily"
        ),
        DuaDetail(
            id = 14,
            titleEn = "Dua For Parents",
            titleUr = "والدین کے لیے دعا",
            titleAr = "دعاء للوالدين",
            titleHi = "माता-पिता (वालिदैन) के लिए दुआ",
            arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-hamhuma kama rabbayani sagheera",
            translationEn = "My Lord, have mercy upon them both as they raised and nourished me when I was small.",
            translationUr = "اے میرے رب! ان دونوں پر رحم فرما جیسا کہ انہوں نے مجھے بچپن میں پیار و شفقت سے پالا تھا۔",
            translationAr = "رب ارحمهما كما ربياني صغيراً.",
            translationHi = "ऐ मेरे रब! उन दोनों पर रहम फरमा जैसा कि उन्होंने मुझे बचपन में पाला था।",
            category = "Family"
        ),
        DuaDetail(
            id = 15,
            titleEn = "Travel Dua (Boarding A Vehicle)",
            titleUr = "سفر اور سواری کی دعا",
            titleAr = "دعاء ركوب الدابة والسفر",
            titleHi = "सफ़र और सवारी की दुआ",
            arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
            transliteration = "Subhanal-ladhee sakh-khara lana hadha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon",
            translationEn = "Glory to Him Who has subjected this to us, and we could never have achieved this by ourselves. And indeed to our Lord we will return.",
            translationUr = "پاک ہے وہ ذات جس نے اس سواری کو ہمارے قابو میں کر دیا ورنہ ہم اسے قابو کرنے والے نہ تھے، اور بے شک ہم اپنے رب کی طرف لوٹ کر جانے والے ہیں۔",
            translationAr = "سبحان الذي سخر لنا هذا وما كنا له مقرنين، وإنا إلى ربنا لمنقلبون.",
            translationHi = "पाक है वह ज़ात जिसने इस सवारी को हमारे काबू में कर दिया वरना हम इसे काबू करने वाले न थे, और बेशक हम अपने रब की तरफ लौटने वाले हैं।",
            category = "Travel"
        ),
        DuaDetail(
            id = 16,
            titleEn = "Dua When It Rains",
            titleUr = "بارش کے وقت کی دعا",
            titleAr = "دعاء نزول المطر",
            titleHi = "बारिश के वक्त की दुआ",
            arabic = "اللَّهُمَّ صَيِّبًا نَافِعًا",
            transliteration = "Allahumma sayyiban nafi'a",
            translationEn = "O Allah, make it a beneficial and abundant rain.",
            translationUr = "اے اللہ! اس بارش کو خوب برسنے والی اور نفع بخش بنا۔",
            translationAr = "اللهم اجعله صيباً نافعاً مباركاً.",
            translationHi = "ऐ अल्लाह! इस बारिश को नफ़ाबख़्श और बरकत वाली बना।",
            category = "Daily"
        ),
        DuaDetail(
            id = 17,
            titleEn = "Dua When Sneezing",
            titleUr = "چھینک آنے کی دعا",
            titleAr = "دعاء العاطس",
            titleHi = "छींक आने की दुआ",
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah (Hearer: Yarhamukallah. Sneezer: Yahdeekumullahu wa yuslihu baalakum)",
            translationEn = "All praise is due to Allah. Hearer says: Yarhamukallah (May Allah have mercy on you). Sneezer replies: Yahdeekumullahu wa yuslihu baalakum (May Allah guide you and set your affairs right).",
            translationUr = "تمام تعریفیں اللہ کے لیے ہیں۔ سننے والا کہے: يَرْحَمُكَ اللَّهُ (اللہ تم پر رحم فرمائے)۔ پھر چھینکنے والا کہے: يَهْدِيكُمُ اللَّهُ وَيُصْلِحُ بَالَكُمْ (اللہ تمہیں ہدایت دے اور تمہارا حال درست رکھے)۔",
            translationAr = "الحمد لله. ويقول له السامع: يرحمك الله، فيرد العاطس: يهديكم الله ويصلح بالكم.",
            translationHi = "सब तारीफें अल्लाह के लिए हैं। सुनने वाला कहे: यरहमुकल्लाह (अल्लाह तुम पर रहम करे)। छींकने वाला कहे: यहदीकुमुल्लाह व युस्लिहु बालकुम।",
            category = "Daily"
        ),
        DuaDetail(
            id = 18,
            titleEn = "Dua For Protection From Harm",
            titleUr = "ہر آفت اور نقصان سے حفاظت کی دعا",
            titleAr = "دعاء الحفظ والوقاية من كل سوء",
            titleHi = "हर आफ़त और नुकसान से हिफ़ाज़त की दुआ",
            arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillahi-lladhee la yadurru ma'asmihee shay'un fil-ardi wa la fis-samaa'i wa Huwas-Samee'ul-'Aleem",
            translationEn = "In the Name of Allah, with Whose Name nothing on earth nor in heaven can cause harm, and He is the All-Hearing, the All-Knowing.",
            translationUr = "اللہ کے نام کے ساتھ جس کے نام کی برکت سے زمین اور آسمان کی کوئی چیز نقصان نہیں پہنچا سکتی، اور وہی سب کچھ سننے والا اور جاننے والا ہے۔",
            translationAr = "بسم الله الذي لا يضر مع اسمه شيء في الأرض ولا في السماء وهو السميع العليم.",
            translationHi = "अल्लाह के नाम के साथ जिसकी बरकत से ज़मीन और आसमान की कोई चीज़ नुकसान नहीं पहुँचा सकती, और वही सुनने और जानने वाला है।",
            category = "Protection"
        ),
        DuaDetail(
            id = 19,
            titleEn = "Dua-e-Qunoot (Witr Prayer)",
            titleUr = "دعائے قنوت (نمازِ وتر)",
            titleAr = "دعاء القنوت في صلاة الوتر",
            titleHi = "दुआ-ए-क़ुनूत (नमाज़-ए-वित्र)",
            arabic = "اللَّهُمَّ إِنَّا نَسْتَعِينُكَ وَنَسْتَغْفِرُكَ وَنُؤْمِنُ بِكَ وَنَتَوَكَّلُ عَلَيْكَ وَنُثْنِي عَلَيْكَ الْخَيْرَ ، وَنَشْكُرُكَ وَلَا نَكْفُرُكَ ، وَنَخْلَعُ وَنَتْرُكُ مَنْ يَفْجُرُكَ ، اللَّهُمَّ إِيَّاكَ نَعْبُدُ وَلَكَ نُصَلِّي وَنَسْجُدُ ، وَإِلَيْكَ نَسْعَىٰ وَنَحْفِدُ ، وَنَرْجُو رَحْمَتَكَ وَنَخْشَىٰ عَذَابَكَ ، إِنَّ عَذَابَكَ بِالْكُفَّارِ مُلْحَقٌ",
            transliteration = "Allahumma inna nasta'eenuka wa nastaghfiruka wa nu'minu bika wa natawakkalu 'alaika wa nuthnee 'alaikal-khair, wa nashkuruka wa la nakfuruka, wa nakhla'u wa natruku mai-yafjuruk. Allahumma iyyaka na'budu wa laka nusallee wa nasjudu, wa ilaika nas'aa wa nahfidu, wa narjoo rahmataka wa nakhshaa 'adhabaka, inna 'adhabaka bil-kuffari mulhaq.",
            translationEn = "O Allah, we seek Your help and ask Your forgiveness, and we believe in You and rely upon You, and we praise You in the best manner, and we thank You and are not ungrateful to You, and we forsake whoever disobeys You. O Allah, You alone do we worship, and unto You do we pray and prostrate, and towards You do we strive and hasten to serve, and we hope for Your mercy and fear Your punishment. Truly, Your punishment will overtake the disbelievers.",
            translationUr = "اے اللہ! ہم تجھ سے مدد چاہتے ہیں اور تجھ سے بخشش مانگتے ہیں اور تجھ پر ایمان لاتے ہیں اور تجھ پر بھروسہ کرتے ہیں اور تیری بہت اچھی تعریف کرتے ہیں اور تیرا شکر ادا کرتے ہیں اور تیری ناشکری نہیں کرتے اور چھوڑتے ہیں اس شخص کو جو تیری نافرمانی کرے۔ اے اللہ! ہم تیری ہی عبادت کرتے ہیں اور تیرے ہی لیے نماز پڑھتے ہیں اور سجدہ کرتے ہیں اور تیری ہی طرف دوڑتے اور حاضر ہوتے ہیں اور تیری رحمت کے امیدوار ہیں اور تیرے عذاب سے ڈرتے ہیں، بے شک تیرا عذاب کافروں کو پہنچنے والا ہے۔",
            translationAr = "اللهم إنا نستعينك ونستغفرك ونؤمن بك ونتوكل عليك ونثني عليك الخير، ونشكرك ولا نكفرك، ونخلع ونترك من يفجرك. اللهم إياك نعبد ولك نصلي ونسجد، وإليك نسعى ونحفد، نرجو رحمتك ونخشى عذابك إن عذابك بالكفار ملحق.",
            translationHi = "ऐ अल्लाह! हम तुझसे मदद चाहते हैं और तुझसे बख्शिश मांगते हैं और तुझपर ईमान लाते हैं और तुझपर भरोसा करते हैं और तेरी बेहतरीन तारीफ करते हैं। हम तेरा शुक्र अदा करते हैं और नाशुक्र नहीं होते। हम उस शख्स को छोड़ते हैं जो तेरी नाफरमानी करे। ऐ अल्लाह! हम तेरी ही इबादत करते हैं, तेरे लिए ही नमाज़ पढ़ते और सजदा करते हैं, और तेरी ही तरफ दौड़ते हैं। हम तेरी रहमत की उम्मीद रखते हैं और तेरे अज़ाब से डरते हैं, बेशक तेरा अज़ाब काफिरों को पहुँचने वाला है।",
            category = "Prayer"
        ),
        DuaDetail(
            id = 20,
            titleEn = "Ayat-ul-Kursi (The Throne Verse)",
            titleUr = "آیت الکرسی (سورۃ البقرہ: 255)",
            titleAr = "آية الكرسي (سورة البقرة: 255)",
            titleHi = "आयतुल कुर्सी (सूरह अल-बक़रा: 255)",
            arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyoom, la ta'khudhuhu sinatuw-wa la nawm, lahu ma fis-samawati wa ma fil-ard, man dhal-ladhee yashfa'u 'indahu illa bi-idhnih, ya'lamu ma baina aideehim wa ma khalfahum, wa la yuheetoona bi-shay'im-min 'ilmihee illa bima shaa'a, wasi'a kursiyyuhus-samawati wal-ard, wa la ya'ooduhu hifzuhuma, wa Huwal-'Aliyyul-'Azeem.",
            translationEn = "Allah! There is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            translationUr = "اللہ! اس کے سوا کوئی معبود نہیں، وہ ہمیشہ زندہ اور سب کا سنبھالنے والا ہے۔ نہ اسے اونگھ آتی ہے نہ نیند۔ جو کچھ آسمانوں میں ہے اور جو کچھ زمین میں ہے سب اسی کا ہے۔ کون ہے جو اس کی اجازت کے بغیر اس کے حضور سفارش کر سکے؟ وہ جانتا ہے جو کچھ ان کے آگے ہے اور جو کچھ ان کے پیچھے ہے، اور وہ اس کے علم میں سے کسی چیز کا احاطہ نہیں کر سکتے مگر جتنا وہ چاہے۔ اس کی کرسی آسمانوں اور زمین پر محیط ہے، اور ان دونوں کی حفاظت اسے نہیں تھکاتی، اور وہ بہت بلند، بہت عظمت والا ہے۔",
            translationAr = "الله لا إله إلا هو الحي القيوم، لا تأخذه سنة ولا نوم، له ما في السماوات وما في الأرض، من ذا الذي يشفع عنده إلا بإذنه، يعلم ما بين أيديهم وما خلفهم، ولا يحيطون بشيء من علمه إلا بما شاء، وسع كرسيه السماوات والأرض، ولا يؤوده حفظهما، وهو العلي العظيم.",
            translationHi = "अल्लाह! उसके सिवा कोई माबूद नहीं, वह हमेशा ज़िंदा और सबका संभालने वाला है। न उसे ऊँघ आती है न नींद। जो कुछ आसमानों में है और जो कुछ ज़मीन में है सब उसी का है। कौन है जो उसकी इजाज़त के बिना सिफारिश कर सके? वह जानता है जो कुछ उनके आगे है और जो कुछ उनके पीछे है, और वे उसके इल्म में से किसी चीज़ का अहाता नहीं कर सकते मगर जितना वह चाहे। उसकी कुर्सी आसमानों और ज़मीन पर फैली हुई है, और इन दोनों की हिफाज़त उसे थकाती नहीं, और वह बहुत बुलंद, बहुत बड़ा है।",
            category = "Protection"
        )
    )
}
