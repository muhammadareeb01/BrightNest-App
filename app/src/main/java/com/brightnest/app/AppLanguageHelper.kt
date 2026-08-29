package com.brightnest.app

import android.speech.tts.TextToSpeech
import com.brightnest.app.content.KidsContent
import java.util.Locale

object AppLanguageHelper {

    fun getLocale(langCode: String): Locale {
        return when (langCode.lowercase()) {
            "ur" -> Locale("ur", "PK")
            "ar" -> Locale("ar")
            "hi" -> Locale("hi", "IN")
            "fr" -> Locale.FRENCH
            "de" -> Locale.GERMAN
            "es" -> Locale("es")
            else -> Locale.US
        }
    }

    fun configureTts(tts: TextToSpeech?, langCode: String) {
        if (tts == null) return
        try {
            val locale = getLocale(langCode)
            tts.language = locale
        } catch (_: Exception) {}
    }

    fun getListenButtonText(langCode: String): String {
        return when (langCode.lowercase()) {
            "ur" -> "🔊 تلفظ سنیں"
            "ar" -> "🔊 استمع للنطق"
            "hi" -> "🔊 उच्चारण सुनें"
            "fr" -> "🔊 Écouter"
            "es" -> "🔊 Escuchar"
            else -> "🔊 Listen Pronunciation"
        }
    }

    fun getListenStoryButtonText(langCode: String): String {
        return when (langCode.lowercase()) {
            "ur" -> "🔊 کہانی سنیں"
            "ar" -> "🔊 استمع للقصة"
            "hi" -> "🔊 कहानी सुनें"
            "fr" -> "🔊 Écouter l'histoire"
            "es" -> "🔊 Escuchar historia"
            else -> "🔊 Listen Story"
        }
    }

    fun getStopListeningText(langCode: String): String {
        return when (langCode.lowercase()) {
            "ur" -> "⏹️ روکیں"
            "ar" -> "⏹️ إيقاف"
            "hi" -> "⏹️ रोकें"
            "fr" -> "⏹️ Arrêter"
            "es" -> "⏹️ Detener"
            else -> "⏹️ Stop"
        }
    }

    fun localizeKidsScreen(screen: KidsContent.Screen, langCode: String): KidsContent.Screen {
        val code = langCode.lowercase()
        if (code == "en") return screen

        val localizedTitle = when (screen.title) {
            "ABC" -> when (code) {
                "ur" -> "اے بی سی"
                "ar" -> "الحروف الإنجليزية"
                "hi" -> "अंग्रेजी वर्णमाला"
                else -> screen.title
            }
            "Animals" -> when (code) {
                "ur" -> "جانور"
                "ar" -> "الحيوانات"
                "hi" -> "जानवर"
                else -> screen.title
            }
            "Colors" -> when (code) {
                "ur" -> "رنگ"
                "ar" -> "الألوان"
                "hi" -> "रंग"
                else -> screen.title
            }
            "Shapes" -> when (code) {
                "ur" -> "شکلیں"
                "ar" -> "الأشكال"
                "hi" -> "आकृतियाँ"
                else -> screen.title
            }
            "Body Parts" -> when (code) {
                "ur" -> "جسم کے اعضاء"
                "ar" -> "أعضاء الجسم"
                "hi" -> "शरीर के अंग"
                else -> screen.title
            }
            "Science" -> when (code) {
                "ur" -> "سائنس"
                "ar" -> "العلوم"
                "hi" -> "विज्ञान"
                else -> screen.title
            }
            "Fruits" -> when (code) {
                "ur" -> "پھل اور سبزیاں"
                "ar" -> "الفواكه والخضروات"
                "hi" -> "फल और सब्जियाँ"
                else -> screen.title
            }
            "Numbers" -> when (code) {
                "ur" -> "گنتی"
                "ar" -> "الأرقام"
                "hi" -> "गिनती"
                else -> screen.title
            }
            else -> screen.title
        }

        val localizedItems = screen.items.map { item ->
            localizeItem(item, screen.variant, code)
        }

        return screen.copy(
            title = localizedTitle,
            items = localizedItems
        )
    }

    private fun localizeItem(item: KidsContent.Item, variant: KidsContent.Variant, langCode: String): KidsContent.Item {
        return when (variant) {
            KidsContent.Variant.LETTER -> {
                // ABC screen items (A, B, C...)
                when (langCode) {
                    "ur" -> {
                        val urduWord = getUrduAbcWord(item.label, item.detailText)
                        item.copy(
                            detailText = "$urduWord (${item.detailText})",
                            speakText = "${item.label}۔ $urduWord"
                        )
                    }
                    "ar" -> {
                        val arabicWord = getArabicAbcWord(item.label, item.detailText)
                        item.copy(
                            detailText = "$arabicWord (${item.detailText})",
                            speakText = arabicWord
                        )
                    }
                    "hi" -> {
                        val hindiWord = getHindiAbcWord(item.label, item.detailText)
                        item.copy(
                            detailText = "$hindiWord (${item.detailText})",
                            speakText = hindiWord
                        )
                    }
                    else -> item
                }
            }
            KidsContent.Variant.ICON -> {
                // Animals, Shapes, Body Parts, Science
                when (langCode) {
                    "ur" -> {
                        val urduName = localizeIconName(item.label, "ur")
                        val urduDetail = localizeIconDetail(item.detailText, "ur")
                        item.copy(
                            label = urduName,
                            detailText = if (urduDetail != item.detailText) urduDetail else "$urduName (${item.label})",
                            speakText = urduName
                        )
                    }
                    "ar" -> {
                        val arName = localizeIconName(item.label, "ar")
                        val arDetail = localizeIconDetail(item.detailText, "ar")
                        item.copy(
                            label = arName,
                            detailText = if (arDetail != item.detailText) arDetail else "$arName (${item.label})",
                            speakText = arName
                        )
                    }
                    "hi" -> {
                        val hiName = localizeIconName(item.label, "hi")
                        val hiDetail = localizeIconDetail(item.detailText, "hi")
                        item.copy(
                            label = hiName,
                            detailText = if (hiDetail != item.detailText) hiDetail else "$hiName (${item.label})",
                            speakText = hiName
                        )
                    }
                    else -> item
                }
            }
            KidsContent.Variant.COLOR -> {
                when (langCode) {
                    "ur" -> {
                        val urduColor = localizeColorName(item.label, "ur")
                        item.copy(
                            label = urduColor,
                            speakText = "$urduColor رنگ"
                        )
                    }
                    "ar" -> {
                        val arColor = localizeColorName(item.label, "ar")
                        item.copy(
                            label = arColor,
                            speakText = arColor
                        )
                    }
                    "hi" -> {
                        val hiColor = localizeColorName(item.label, "hi")
                        item.copy(
                            label = hiColor,
                            speakText = "$hiColor रंग"
                        )
                    }
                    else -> item
                }
            }
            KidsContent.Variant.FRUIT -> {
                when (langCode) {
                    "ur" -> {
                        val urduFruit = if (item.urdu.isNotEmpty()) item.urdu else localizeFruitName(item.label, "ur")
                        val urduDetail = localizeIconDetail(item.detailText, "ur")
                        item.copy(
                            label = urduFruit,
                            detailText = if (urduDetail != item.detailText) urduDetail else "${item.label} ($urduFruit)",
                            speakText = urduFruit
                        )
                    }
                    "ar" -> {
                        val arFruit = localizeFruitName(item.label, "ar")
                        val arDetail = localizeIconDetail(item.detailText, "ar")
                        item.copy(
                            label = arFruit,
                            detailText = if (arDetail != item.detailText) arDetail else "${item.label} ($arFruit)",
                            speakText = arFruit
                        )
                    }
                    "hi" -> {
                        val hiFruit = localizeFruitName(item.label, "hi")
                        val hiDetail = localizeIconDetail(item.detailText, "hi")
                        item.copy(
                            label = hiFruit,
                            detailText = if (hiDetail != item.detailText) hiDetail else "${item.label} ($hiFruit)",
                            speakText = hiFruit
                        )
                    }
                    else -> item
                }
            }
            KidsContent.Variant.NUMBER -> {
                when (langCode) {
                    "ur" -> {
                        val urNum = getUrduNumberWord(item.label)
                        item.copy(speakText = urNum)
                    }
                    "ar" -> {
                        val arNum = getArabicNumberWord(item.label)
                        item.copy(speakText = arNum)
                    }
                    "hi" -> {
                        val hiNum = getHindiNumberWord(item.label)
                        item.copy(speakText = hiNum)
                    }
                    else -> item
                }
            }
            else -> item
        }
    }

    private fun getUrduAbcWord(letter: String, word: String): String {
        return when (word.lowercase()) {
            "apple" -> "سیب"
            "bear" -> "ریچھ"
            "cat" -> "بلی"
            "dog" -> "کتا"
            "elephant" -> "ہاتھی"
            "fish" -> "مچھلی"
            "giraffe" -> "زرافہ"
            "hat" -> "ٹوپی"
            "ice cream" -> "آئس کریم"
            "jellyfish" -> "جیلی فش"
            "key" -> "چابی"
            "lion" -> "شیر"
            "moon" -> "چاند"
            "nest" -> "گھونسلا"
            "owl" -> "الو"
            "penguin" -> "پینگوئن"
            "queen" -> "ملکہ"
            "rabbit" -> "خرگوش"
            "sun" -> "سورج"
            "turtle" -> "کچھوا"
            "umbrella" -> "چھتری"
            "volcano" -> "آتش فشاں"
            "watermelon" -> "تربوز"
            "xylophone" -> "زائلو فون"
            "yarn" -> "اون"
            "zebra" -> "زیبرا"
            else -> word
        }
    }

    private fun getArabicAbcWord(letter: String, word: String): String {
        return when (word.lowercase()) {
            "apple" -> "تفاحة"
            "bear" -> "دب"
            "cat" -> "قطة"
            "dog" -> "كلب"
            "elephant" -> "فيل"
            "fish" -> "سمكة"
            "giraffe" -> "زرافة"
            "hat" -> "قبعة"
            "ice cream" -> "مثلجات"
            "jellyfish" -> "قنديل البحر"
            "key" -> "مفتاح"
            "lion" -> "أسد"
            "moon" -> "قمر"
            "nest" -> "عش"
            "owl" -> "بومة"
            "penguin" -> "بطريق"
            "queen" -> "ملكة"
            "rabbit" -> "أرنب"
            "sun" -> "شمس"
            "turtle" -> "سلحفاة"
            "umbrella" -> "مظلة"
            "volcano" -> "بركان"
            "watermelon" -> "بطيخ"
            "xylophone" -> "اكسيليفون"
            "yarn" -> "خيط"
            "zebra" -> "حمار وحشي"
            else -> word
        }
    }

    private fun getHindiAbcWord(letter: String, word: String): String {
        return when (word.lowercase()) {
            "apple" -> "सेब"
            "bear" -> "भालू"
            "cat" -> "बिल्ली"
            "dog" -> "कुत्ता"
            "elephant" -> "हाथी"
            "fish" -> "मछली"
            "giraffe" -> "जिराफ़"
            "hat" -> "टोपी"
            "ice cream" -> "आइसक्रीम"
            "jellyfish" -> "जेलीफिश"
            "key" -> "चाबी"
            "lion" -> "शेर"
            "moon" -> "चाँद"
            "nest" -> "घोंसला"
            "owl" -> "उल्लू"
            "penguin" -> "पेंगुइन"
            "queen" -> "रानी"
            "rabbit" -> "खरगोश"
            "sun" -> "सूरज"
            "turtle" -> "कछुआ"
            "umbrella" -> "छतरी"
            "volcano" -> "ज्वालामुखी"
            "watermelon" -> "तरबूज"
            "xylophone" -> "ज़ायलोफोन"
            "yarn" -> "ऊन"
            "zebra" -> "ज़ेबरा"
            else -> word
        }
    }

    private fun localizeIconName(en: String, lang: String): String {
        val mapUr = mapOf(
            "Lion" to "شیر", "Tiger" to "چیتا", "Leopard" to "تیندوا", "Cheetah" to "چیتا", "Jaguar" to "جیگوار", "Lynx" to "جنگلی بلی",
            "Elephant" to "ہاتھی", "Giraffe" to "زرافہ", "Zebra" to "زیبرا", "Hippo" to "دریا گھوڑا", "Rhino" to "گینڈا",
            "Monkey" to "بندر", "Gorilla" to "گوریلا", "Chimp" to "چمپینزی", "Rabbit" to "خرگوش", "Squirrel" to "گلہری", "Hedgehog" to "خارپشت",
            "Dog" to "کتا", "Puppy" to "پلا", "Cat" to "بلی", "Kitten" to "بلونگڑا", "Cow" to "گائے", "Sheep" to "بھیڑ",
            "Horse" to "گھوڑا", "Pony" to "ٹٹو", "Bird" to "پرندہ", "Parrot" to "طوطا", "Eagle" to "عقاب", "Peacock" to "مور",
            "Camel" to "اونٹ", "Deer" to "ہرن", "Moose" to "بارہ سنگھا", "Bear" to "ریچھ", "Polar Bear" to "قطبی ریچھ",
            "Wolf" to "بھیڑیا", "Fox" to "لومڑی", "Panda" to "پانڈا", "Koala" to "کوآلا", "Kangaroo" to "کینگرو",
            "Penguin" to "پینگوئن", "Owl" to "الو", "Duck" to "بطخ", "Goose" to "راج ہنس", "Swan" to "ہنس",
            "Chicken" to "مرغی", "Rooster" to "مرغا", "Turkey" to "ٹرکی", "Pig" to "سور", "Goat" to "بکری", "Donkey" to "گدھا",
            "Fish" to "مچھلی", "Shark" to "شارک", "Dolphin" to "ڈولفن", "Whale" to "وہیل", "Octopus" to "آکٹوپس",
            "Crab" to "کیکڑا", "Seahorse" to "سمندری گھوڑا", "Starfish" to "اسٹار فش", "Turtle" to "کچھوا", "Tortoise" to "خاکی کچھوا",
            "Frog" to "مینڈک", "Toad" to "بڑا مینڈک", "Snake" to "سانپ", "Lizard" to "چھپکلی", "Crocodile" to "مگرمچھ", "Alligator" to "گھڑیال",
            "Bee" to "شہد کی مکھی", "Butterfly" to "تتلی", "Dragonfly" to "ڈریگن فلائی", "Spider" to "مکڑی", "Ant" to "چیونٹی",
            "Snail" to "گھونگھا", "Ladybug" to "لیڈی بگ", "Jellyfish" to "جیلی فش", "Bat" to "چمگادڑ", "Mouse" to "چوہا",
            // Science items
            "Sun" to "سورج", "Moon" to "چاند", "Stars" to "ستارے", "Earth" to "زمین", "Cloud" to "بادل",
            "Rain" to "بارش", "Snow" to "برف", "Wind" to "ہوا", "Water" to "پانی", "Fire" to "آگ",
            "Tree" to "درخت", "Flower" to "پھول", "Magnet" to "مقناطیس", "Battery" to "بیٹری", "Light" to "روشنی",
            "Sound" to "آواز", "Rainbow" to "قوس قزح", "Volcano" to "آتش فشاں", "Ocean" to "سمندر", "Mountain" to "پہاڑ",
            // Shapes
            "Circle" to "دائرہ", "Square" to "مربع", "Triangle" to "تکون", "Rectangle" to "مستطیل", "Star" to "ستارہ",
            "Heart" to "دل", "Diamond" to "ہیرا", "Oval" to "بیضوی", "Pentagon" to "مخمس", "Hexagon" to "مسدس",
            "Octagon" to "مثمن", "Crescent" to "ہلال", "Plus" to "جمع", "Arrow" to "تیر", "Cube" to "مکعب", "Sphere" to "کرہ",
            // Body Parts
            "Head" to "سر", "Eye" to "آنکھ", "Ear" to "کان", "Nose" to "ناک", "Mouth" to "منہ",
            "Teeth" to "دانت", "Tongue" to "زبان", "Hair" to "بال", "Neck" to "گردن", "Shoulder" to "کندھا",
            "Arm" to "بازو", "Hand" to "ہاتھ", "Finger" to "انگلی", "Chest" to "سینہ", "Stomach" to "پیٹ",
            "Back" to "کمر", "Leg" to "ٹانگ", "Knee" to "گھٹنا", "Foot" to "پاؤں", "Toe" to "پاؤں کی انگلی",
            "Brain" to "دماغ", "Lungs" to "پھیپھڑے", "Skin" to "جلد"
        )
        val mapAr = mapOf(
            "Lion" to "أسد", "Tiger" to "نمر", "Leopard" to "فهد", "Cheetah" to "فهد صياد", "Jaguar" to "جاغوار", "Lynx" to "وشق",
            "Elephant" to "فيل", "Giraffe" to "زرافة", "Zebra" to "حمار وحشي", "Hippo" to "فرس النهر", "Rhino" to "وحيد القرن",
            "Monkey" to "قرد", "Gorilla" to "غوريلا", "Chimp" to "شمبانزي", "Rabbit" to "أرنب", "Squirrel" to "سنجاب", "Hedgehog" to "قنفذ",
            "Dog" to "كلب", "Puppy" to "جرو", "Cat" to "قطة", "Kitten" to "هرة", "Cow" to "بقرة", "Sheep" to "خروف",
            "Horse" to "حصان", "Pony" to "مهر", "Bird" to "طائر", "Parrot" to "ببغاء", "Eagle" to "نسر", "Peacock" to "طاووس",
            "Camel" to "جمل", "Deer" to "غزال", "Moose" to "موس", "Bear" to "دب", "Polar Bear" to "دب قطبي",
            "Wolf" to "ذئب", "Fox" to "ثعلب", "Panda" to "باندا", "Koala" to "كوالا", "Kangaroo" to "كنغر",
            "Penguin" to "بطريق", "Owl" to "بومة", "Duck" to "بطة", "Goose" to "أوزة", "Swan" to "بجعة",
            "Chicken" to "دجاجة", "Rooster" to "ديك", "Turkey" to "ديك رومي", "Pig" to "خنزير", "Goat" to "ماعز", "Donkey" to "حمار",
            "Fish" to "سمكة", "Shark" to "قرش", "Dolphin" to "دلفين", "Whale" to "حوت", "Octopus" to "أخطبوط",
            "Crab" to "سرطان البحر", "Seahorse" to "فرس البحر", "Starfish" to "نجمة البحر", "Turtle" to "سلحفاة", "Tortoise" to "سلحفاة برية",
            "Frog" to "ضفدع", "Toad" to "علجوم", "Snake" to "ثعبان", "Lizard" to "سحلية", "Crocodile" to "تمساح", "Alligator" to "قاطور",
            "Bee" to "نحلة", "Butterfly" to "فراشة", "Dragonfly" to "يعسوب", "Spider" to "عنكبوت", "Ant" to "نملة",
            "Snail" to "حلزون", "Ladybug" to "دعسوقة", "Jellyfish" to "قنديل البحر", "Bat" to "خفاش", "Mouse" to "فأر",
            // Science items
            "Sun" to "شمس", "Moon" to "قمر", "Stars" to "نجوم", "Earth" to "أرض", "Cloud" to "سحابة",
            "Rain" to "مطر", "Snow" to "ثلج", "Wind" to "رياح", "Water" to "ماء", "Fire" to "نار",
            "Tree" to "شجرة", "Flower" to "زهرة", "Magnet" to "مغناطيس", "Battery" to "بطارية", "Light" to "ضوء",
            "Sound" to "صوت", "Rainbow" to "قوس قزح", "Volcano" to "بركان", "Ocean" to "محيط", "Mountain" to "جبل",
            // Shapes
            "Circle" to "دائرة", "Square" to "مربع", "Triangle" to "مثلث", "Rectangle" to "مستطيل", "Star" to "نجمة",
            "Heart" to "قلب", "Diamond" to "ماسة", "Oval" to "بيضاوي", "Pentagon" to "خماسي", "Hexagon" to "سداسي",
            "Octagon" to "ثماني", "Crescent" to "هلال", "Plus" to "زائد", "Arrow" to "سهم", "Cube" to "مكعب", "Sphere" to "كرة",
            // Body Parts
            "Head" to "رأس", "Eye" to "عين", "Ear" to "أذن", "Nose" to "أنف", "Mouth" to "فم",
            "Teeth" to "أسنان", "Tongue" to "لسان", "Hair" to "شعر", "Neck" to "رقبة", "Shoulder" to "كتف",
            "Arm" to "ذراع", "Hand" to "يد", "Finger" to "إصبع", "Chest" to "صدر", "Stomach" to "معدة",
            "Back" to "ظهر", "Leg" to "ساق", "Knee" to "ركبة", "Foot" to "قدم", "Toe" to "إصبع القدم",
            "Brain" to "دماغ", "Lungs" to "رئتين", "Skin" to "جلد"
        )
        val mapHi = mapOf(
            "Lion" to "शेर", "Tiger" to "बाघ", "Leopard" to "तेंदुआ", "Cheetah" to "चीता", "Jaguar" to "जगुआर", "Lynx" to "लिंक्स",
            "Elephant" to "हाथी", "Giraffe" to "जिराफ़", "Zebra" to "ज़ेबरा", "Hippo" to "दरियाई घोड़ा", "Rhino" to "गैंडा",
            "Monkey" to "बंदर", "Gorilla" to "गोरिल्ला", "Chimp" to "चिंपैंजी", "Rabbit" to "खरगोश", "Squirrel" to "गिलहरी", "Hedgehog" to "साही",
            "Dog" to "कुत्ता", "Puppy" to "पिल्ला", "Cat" to "बिल्ली", "Kitten" to "बिल्ली का बच्चा", "Cow" to "गाय", "Sheep" to "भेड़",
            "Horse" to "घोड़ा", "Pony" to "टट्टू", "Bird" to "पक्षी", "Parrot" to "तोता", "Eagle" to "चील", "Peacock" to "मोर",
            "Camel" to "ऊँट", "Deer" to "हिरण", "Moose" to "मूस", "Bear" to "भालू", "Polar Bear" to "ध्रुवीय भालू",
            "Wolf" to "भेड़िया", "Fox" to "लोमड़ी", "Panda" to "पांडा", "Koala" to "कोआला", "Kangaroo" to "कंगारू",
            "Penguin" to "पेंगुइन", "Owl" to "उल्लू", "Duck" to "बत्तख", "Goose" to "हंस", "Swan" to "राजहंस",
            "Chicken" to "मुर्गी", "Rooster" to "मुर्गा", "Turkey" to "टर्की", "Pig" to "सुअर", "Goat" to "बकरी", "Donkey" to "गधा",
            "Fish" to "मछली", "Shark" to "शार्क", "Dolphin" to "डॉल्फ़िन", "Whale" to "व्हेल", "Octopus" to "ऑक्टोपस",
            "Crab" to "केकड़ा", "Seahorse" to "समुद्री घोड़ा", "Starfish" to "तारामछली", "Turtle" to "कछुआ", "Tortoise" to "कछुआ",
            "Frog" to "मेंढक", "Toad" to "टोड", "Snake" to "साँप", "Lizard" to "छिपकली", "Crocodile" to "मगरमच्छ", "Alligator" to "घड़ियाल",
            "Bee" to "मधुमक्खी", "Butterfly" to "तितली", "Dragonfly" to "व्याधपतंग", "Spider" to "मकड़ी", "Ant" to "चींटी",
            "Snail" to "घोंघा", "Ladybug" to "लेडीबग", "Jellyfish" to "जेलीफिश", "Bat" to "चमगादड़", "Mouse" to "चूहा",
            // Science items
            "Sun" to "सूरज", "Moon" to "चाँद", "Stars" to "तारे", "Earth" to "पृथ्वी", "Cloud" to "बादल",
            "Rain" to "बारिश", "Snow" to "बर्फ", "Wind" to "हवा", "Water" to "पानी", "Fire" to "आग",
            "Tree" to "पेड़", "Flower" to "फूल", "Magnet" to "चुंबक", "Battery" to "बैटरी", "Light" to "प्रकाश",
            "Sound" to "ध्वनि", "Rainbow" to "इंद्रधनुष", "Volcano" to "ज्वालामुखी", "Ocean" to "महासागर", "Mountain" to "पर्वत",
            // Shapes
            "Circle" to "वृत्त", "Square" to "वर्ग", "Triangle" to "त्रिकोण", "Rectangle" to "आयत", "Star" to "तारा",
            "Heart" to "दिल", "Diamond" to "हीरा", "Oval" to "अंडाकार", "Pentagon" to "पंचभुज", "Hexagon" to "षट्कोण",
            "Octagon" to "अष्टभुज", "Crescent" to "अर्धचंद्र", "Plus" to "प्लस", "Arrow" to "तीर", "Cube" to "घन", "Sphere" to "गोला",
            // Body Parts
            "Head" to "सिर", "Eye" to "आँख", "Ear" to "कान", "Nose" to "नाक", "Mouth" to "मुँह",
            "Teeth" to "दाँत", "Tongue" to "जीभ", "Hair" to "बाल", "Neck" to "गर्दन", "Shoulder" to "कंधा",
            "Arm" to "बाँह", "Hand" to "हाथ", "Finger" to "उँगली", "Chest" to "छाती", "Stomach" to "पेट",
            "Back" to "पीठ", "Leg" to "टाँग", "Knee" to "घुटना", "Foot" to "पैर", "Toe" to "पैर की उँगली",
            "Brain" to "मस्तिष्क", "Lungs" to "फेफड़े", "Skin" to "त्वचा"
        )
        return when (lang) {
            "ur" -> mapUr[en] ?: en
            "ar" -> mapAr[en] ?: en
            "hi" -> mapHi[en] ?: en
            else -> en
        }
    }

    private fun localizeIconDetail(en: String, lang: String): String {
        val mapUr = mapOf(
            "Roar" to "دھاڑ", "Snarl" to "غراہٹ", "Chirp" to "چہچہاہٹ", "Growl" to "غراہٹ", "Yowl" to "چیخ",
            "Trumpet" to "چنگھاڑ", "Tall and quiet" to "لمبا اور خاموش", "Bray" to "رینکنا", "Grunt" to "غراہٹ", "Snort" to "پھنکارنا",
            "Ooh ooh ah ah" to "اوہ اوہ آہ آہ", "Beat chest" to "سینہ پیٹنا", "Hoot" to "ہوٹ", "Hop hop" to "کودنا", "Chatter" to "چٹخنا",
            "Snuffle" to "سونگھنا", "Woof woof" to "بھونکنا", "Yip yip" to "ہلکی چیخ", "Meow" to "میاؤں", "Mew" to "میو",
            "Moo" to "رنبھانا", "Baa" to "میں میں", "Neigh" to "ہنہنانا", "Whinny" to "ہنہنانا", "Tweet tweet" to "چوں چوں",
            "Squawk" to "چیخنا", "Screech" to "تیز چیخ", "Call" to "پکار", "Hummm" to "گنگنانا", "Quiet" to "خاموش",
            "Bellow" to "ڈکارنا", "Howl" to "بھیڑئیے کی چیخ", "Yip" to "چیخ", "Munch" to "چبانا", "Snore" to "خراٹے",
            "Thump" to "دھمک", "Hoo hoo" to "ہو ہو", "Quack" to "کویک کویک", "Honk" to "ہانک", "Cluck" to "کڑکڑانا",
            "Cock-a-doodle-doo" to "ککڑوں کوں", "Gobble" to "گوبل", "Oink" to "اوئینک", "Bleat" to "میامیانا", "Hee-haw" to "ڈھینچوں ڈھینچوں",
            "Blub blub" to "بلب بلب", "Splash" to "چھپاک", "Click click" to "کلک کلک", "Whoosh" to "غراتی ہوا", "Squish" to "سکیوئش",
            "Snip snip" to "سنپ سنپ", "Silent" to "خاموش", "Slow and steady" to "آہستہ اور مستقل", "Ribbit" to "ٹر ٹر", "Croak" to "ٹرانا",
            "Hissss" to "پھنکار", "Snap" to "جھپٹنا", "Buzz buzz" to "بھنبھناہٹ", "Flutter" to "پھڑپھڑاہٹ", "Whirr" to "گنگناہٹ",
            "Tiny" to "چھوٹا", "Slow and quiet" to "آہستہ اور خاموش", "Float" to "تیرنا", "Squeak" to "چیخنا",
            // Science details
            "The Sun gives us light and heat." to "سورج ہمیں روشنی اور حرارت دیتا ہے۔",
            "The Moon shines at night." to "چاند رات کو چمکتا ہے۔",
            "Stars are giant balls of fire far away." to "ستارے بہت دور آگ کے بڑے گولے ہیں۔",
            "We all live on planet Earth." to "ہم سب زمین پر رہتے ہیں۔",
            "Clouds bring us rain." to "بادل ہمارے لیے بارش لاتے ہیں۔",
            "Rain helps plants grow." to "بارش پودوں کو اگنے میں مدد دیتی ہے۔",
            "Snow is frozen water." to "برف جما ہوا پانی ہے۔",
            "Wind is moving air." to "ہوا چلتی ہوئی گیس ہے۔",
            "Water is needed by all living things." to "پانی تمام جانداروں کے لیے ضروری ہے۔",
            "Fire is hot. Stay safe!" to "آگ گرم ہوتی ہے۔ محفوظ رہیں!",
            "Trees give us fresh air." to "درخت ہمیں تازہ ہوا دیتے ہیں۔",
            "Flowers attract bees and butterflies." to "پھول مکھیوں اور تتلیوں کو راغب کرتے ہیں۔",
            "Magnets pull iron objects." to "مقناطیس لوہے کی چیزوں کو کھینچتے ہیں۔",
            "Batteries store energy." to "بیٹریاں توانائی ذخیرہ کرتی ہیں۔",
            "Light helps us see." to "روشنی ہمیں دیکھنے میں مدد کرتی ہے۔",
            "Sound is made by vibrations." to "آواز تھرتھراہٹ سے پیدا ہوتی ہے۔",
            "A rainbow has seven colors." to "قوس قزح میں سات رنگ ہوتے ہیں۔",
            "A volcano shoots hot lava." to "آتش فشاں گرم لاوا پھینکتا ہے۔",
            "Oceans are full of salt water." to "سمندر نمکین پانی سے بھرے ہوتے ہیں۔",
            "Mountains are very tall hills." to "پہاڑ بہت اونچی چوٹیاں ہوتے ہیں۔",
            // Fruit facts
            "An apple a day keeps the doctor away!" to "روزانہ ایک سیب ڈاکٹر کو دور رکھتا ہے!",
            "Bananas grow upside-down on trees." to "کیلے درختوں پر الٹے اگتے ہیں۔",
            "Oranges are full of Vitamin C." to "نارنگی وٹامن سی سے بھرپور ہوتی ہے۔",
            "Grapes grow in big bunches called clusters." to "انگور بڑے گچھوں میں اگتے ہیں۔",
            "Watermelon is 92% water!" to "تربوز میں 92 فیصد پانی ہوتا ہے!",
            "The only fruit with seeds on the outside." to "وہ واحد پھل جس کے بیج باہر ہوتے ہیں۔",
            "Cherries come in pairs on one stem." to "چیری ایک ٹہنی پر جوڑے کی شکل میں آتی ہے۔",
            "Pineapples take 2 years to grow!" to "انناس کو اگنے میں 2 سال لگتے ہیں!",
            "Pears float because they have air inside." to "ناشپاتی پانی پر تیرتی ہے کیونکہ اس کے اندر ہوا ہوتی ہے۔",
            "Mango is the king of fruits in Pakistan!" to "آم پاکستان میں پھلوں کا بادشاہ ہے!",
            "Lemons are very sour but very healthy." to "لیموں بہت کھٹا مگر صحت کے لیے بہترین ہوتا ہے۔",
            "Peaches have fuzzy soft skin." to "آڑو کی جلد نرم اور ریشمی ہوتی ہے۔",
            "Dried plums become prunes." to "خشک آلوبخارا پرون کہلاتا ہے۔",
            "Coconuts can float across oceans!" to "ناریل سمندروں میں تیر کر جا سکتے ہیں!",
            "One pomegranate can have 600 seeds!" to "ایک انار میں 600 دانے ہو سکتے ہیں!",
            "Kiwi has more Vitamin C than orange." to "کیوی میں نارنگی سے زیادہ وٹامن سی ہوتا ہے۔",
            "Papayas grow on trees, not bushes." to "پپیتا درخت پر اگتا ہے، جھاڑی پر نہیں۔",
            "Guava skin is edible and healthy!" to "امرود کا چھلکا کھانے کے قابل اور صحت بخش ہوتا ہے!",
            "Blueberries are great for your brain." to "بلوبیری آپ کے دماغ کے لیے بہترین ہے۔",
            "Prophet Muhammad ﷺ loved eating dates." to "نبی کریم ﷺ کو کھجور کھانا پسند تھا۔",
            "Figs are mentioned in the Holy Quran." to "انجیر کا ذکر قرآن مجید میں آیا ہے۔",
            "Olives are blessed in the Quran." to "زیتون قرآن میں بابرکت پھل ہے۔",
            "Avocado is actually a fruit, not a veggie!" to "ایوکاڈو اصل میں پھل ہے، سبزی نہیں!",
            "Carrots help you see better in the dark." to "گاجر اندھیرے میں بہتر دیکھنے میں مدد کرتی ہے۔",
            "Broccoli is a tiny green tree!" to "بروکلی ایک چھوٹا ہرا درخت ہے!",
            "Cabbage has layers like a book." to "گوبھی میں کتاب کی طرح پرتیں ہوتی ہیں۔",
            "Lettuce is mostly water and very fresh." to "سلاد کے پتے زیادہ تر پانی اور تازہ ہوتے ہیں۔",
            "Tomatoes are actually fruits, but we cook them like veggies." to "ٹماٹر اصل میں پھل ہے مگر ہم اسے سبزی کی طرح پکاتے ہیں۔",
            "Potatoes grow underground." to "آلو زمین کے نیچے اگتے ہیں۔",
            "Corn has silky threads at the top." to "مکئی کے اوپر ریشمی بال ہوتے ہیں۔",
            "Peas hide inside green pods." to "مٹر ہری پھلیوں کے اندر چھپے ہوتے ہیں۔",
            "Cucumbers stay cool inside in summer." to "کھیرا گرمیوں میں اندر سے ٹھنڈا رہتا ہے۔",
            "Pumpkins can grow bigger than you!" to "کدو آپ سے بھی بڑا ہو سکتا ہے!",
            "Eggplant is purple and shiny outside." to "بینگن باہر سے جامنی اور چمکدار ہوتا ہے۔",
            "Bell peppers come in red, yellow & green!" to "شملہ مرچ لال، پیلی اور ہری ہوتی ہے!",
            "Chillies make food spicy and hot." to "مرچیں کھانے کو چٹپٹا اور تیز بناتی ہیں۔",
            "Onions can make you cry while cutting." to "پیاز کاٹتے وقت آنکھوں میں آنسو آ سکتے ہیں۔",
            "Garlic is great for fighting germs!" to "لہسن جراثیم سے لڑنے کے لیے بہترین ہے!",
            "Mushrooms are not plants — they're fungi!" to "کھمبی پودا نہیں بلکہ فنجائی ہے!",
            "Spinach makes you strong like Popeye." to "پالک آپ کو پاپائے کی طرح مضبوط بناتی ہے۔",
            "Radishes are crunchy and a bit spicy." to "مولی کرکرے اور تھوڑی تیز ہوتی ہے۔"
        )
        val mapAr = mapOf(
            "Roar" to "زئير", "Snarl" to "زمجرة", "Chirp" to "زقزقة", "Growl" to "هدير", "Yowl" to "عواء",
            "Trumpet" to "صياح", "Tall and quiet" to "طويل وهادئ", "Bray" to "نهيق", "Grunt" to "شخير", "Snort" to "نفير",
            "Ooh ooh ah ah" to "أوه أوه آه آه", "Beat chest" to "ضرب الصدر", "Hoot" to "صياح البوم", "Hop hop" to "قفز", "Chatter" to "ثرثرة",
            "Snuffle" to "تشمم", "Woof woof" to "نباح", "Yip yip" to "نباح خفيف", "Meow" to "مواء", "Mew" to "مواء صغير",
            "Moo" to "خوار", "Baa" to "ثغاء", "Neigh" to "صهيل", "Whinny" to "صهيل خفيف", "Tweet tweet" to "تغريد",
            "Squawk" to "صراخ", "Screech" to "صيحة حادة", "Call" to "نداء", "Hummm" to "همهمة", "Quiet" to "هادئ",
            "Bellow" to "خوار بصوت عالٍ", "Howl" to "عواء الذئب", "Yip" to "نباح", "Munch" to "مضغ", "Snore" to "شخير",
            "Thump" to "ضربة", "Hoo hoo" to "هو هو", "Quack" to "بطبطة", "Honk" to "صياح الإوز", "Cluck" to "قنطرة",
            "Cock-a-doodle-doo" to "صياح الديك", "Gobble" to "صياح الديك الرومي", "Oink" to "شخير الخنزير", "Bleat" to "ثغاء الماعز", "Hee-haw" to "نهيق الحمار",
            "Blub blub" to "بلب بلب", "Splash" to "رش الماء", "Click click" to "نقرة", "Whoosh" to "أزيز", "Squish" to "سحق",
            "Snip snip" to "قص قص", "Silent" to "صامت", "Slow and steady" to "بطيء وثابت", "Ribbit" to "نقيق", "Croak" to "نقيق الضفدع",
            "Hissss" to "فخاخ", "Snap" to "قضم حاد", "Buzz buzz" to "طنين", "Flutter" to "رفرفة", "Whirr" to "أزيز الأجنحة",
            "Tiny" to "صغير جداً", "Slow and quiet" to "بطيء وهادئ", "Float" to "طفو", "Squeak" to "صرير",
            // Science details
            "The Sun gives us light and heat." to "الشمس تعطينا الضوء والحرارة.",
            "The Moon shines at night." to "القمر يضيء في الليل.",
            "Stars are giant balls of fire far away." to "النجوم كرات نارية عملاقة بعيدة.",
            "We all live on planet Earth." to "نحن جميعاً نعيش على كوكب الأرض.",
            "Clouds bring us rain." to "الغيوم تجلب لنا المطر.",
            "Rain helps plants grow." to "المطر يساعد النباتات على النمو.",
            "Snow is frozen water." to "الثلج هو ماء متجمد.",
            "Wind is moving air." to "الرياح هي هواء متحرك.",
            "Water is needed by all living things." to "الماء ضروري لجميع الكائنات الحية.",
            "Fire is hot. Stay safe!" to "النار حارة. ابقَ آمناً!",
            "Trees give us fresh air." to "الأشجار تعطينا هواءً نقياً.",
            "Flowers attract bees and butterflies." to "الأزهار تجذب النحل والفراشات.",
            "Magnets pull iron objects." to "المغناطيس يجذب الأشياء الحديدية.",
            "Batteries store energy." to "البلطاريات تخزن الطاقة.",
            "Light helps us see." to "الضوء يساعدنا على الرؤية.",
            "Sound is made by vibrations." to "الصوت ينتج عن الاهتزازات.",
            "A rainbow has seven colors." to "قوس قزح يحتوي على سبعة ألوان.",
            "A volcano shoots hot lava." to "البركان يطلق حمماً ساخنة.",
            "Oceans are full of salt water." to "المحيطات مليئة بالماء المالح.",
            "Mountains are very tall hills." to "الجبال تلال عالية جداً."
        )
        val mapHi = mapOf(
            "Roar" to "दहाड़", "Snarl" to "गुर्राहट", "Chirp" to "चहचहाहट", "Growl" to "गुर्राहट", "Yowl" to "चीख",
            "Trumpet" to "चिंघाड़", "Tall and quiet" to "लंबा और शांत", "Bray" to "रेंकना", "Grunt" to "घुरघुराहट", "Snort" to "फूँकार",
            "Ooh ooh ah ah" to "ऊ ऊ आ आ", "Beat chest" to "छाती पीटना", "Hoot" to "हूट", "Hop hop" to "कूदना", "Chatter" to "चटखना",
            "Snuffle" to "सूँघना", "Woof woof" to "भौंकना", "Yip yip" to "हल्की चीख", "Meow" to "म्याऊँ", "Mew" to "म्यू",
            "Moo" to "रंभाना", "Baa" to "मिमियाना", "Neigh" to "हिनहिनाना", "Whinny" to "हिनहिनाना", "Tweet tweet" to "चीं चीं",
            "Squawk" to "चीखना", "Screech" to "तेज़ चीख", "Call" to "पुकार", "Hummm" to "गुनगुनाना", "Quiet" to "शांत",
            "Bellow" to "डकारना", "Howl" to "भेड़िये की चीख", "Yip" to "चीख", "Munch" to "चबाना", "Snore" to "खर्राटे",
            "Thump" to "धमक", "Hoo hoo" to "हू हू", "Quack" to "क्वैक क्वैक", "Honk" to "हाँक", "Cluck" to "कुड़कुड़ाना",
            "Cock-a-doodle-doo" to "कुकड़ूँ कूँ", "Gobble" to "गोबल", "Oink" to "ओइंक", "Bleat" to "मिमियाना", "Hee-haw" to "ढेंचूँ ढेंचूँ",
            "Blub blub" to "ब्लब ब्लब", "Splash" to "छपाक", "Click click" to "क्लिक क्लिक", "Whoosh" to "सरसराहट", "Squish" to "स्क्विश",
            "Snip snip" to "स्निप स्निप", "Silent" to "मौन", "Slow and steady" to "धीमा और निरंतर", "Ribbit" to "टर्र टर्र", "Croak" to "टर्राना",
            "Hissss" to "फूँकार", "Snap" to "झपटना", "Buzz buzz" to "भिनभिनाहट", "Flutter" to "फड़फड़ाहट", "Whirr" to "गुनगुनाहट",
            "Tiny" to "छोटा", "Slow and quiet" to "धीमा और शांत", "Float" to "तैरना", "Squeak" to "चीखना",
            // Science details
            "The Sun gives us light and heat." to "सूर्य हमें प्रकाश और गर्मी देता है।",
            "The Moon shines at night." to "चाँद रात में चमकता है।",
            "Stars are giant balls of fire far away." to "तारे बहुत दूर आग के विशाल गोले हैं।",
            "We all live on planet Earth." to "हम सभी पृथ्वी ग्रह पर रहते हैं।",
            "Clouds bring us rain." to "बादल हमारे लिए बारिश लाते हैं।",
            "Rain helps plants grow." to "बारिश पौधों को बढ़ने में मदद करती है।",
            "Snow is frozen water." to "बर्फ जमा हुआ पानी है।",
            "Wind is moving air." to "हवा चलती हुई गैस है।",
            "Water is needed by all living things." to "पानी सभी जीवित चीजों के लिए आवश्यक है।",
            "Fire is hot. Stay safe!" to "आग गर्म होती है। सुरक्षित रहें!",
            "Trees give us fresh air." to "पेड़ हमें ताज़ा हवा देते हैं।",
            "Flowers attract bees and butterflies." to "फूल मधुमक्खियों और तितलियों को आकर्षित करते हैं।",
            "Magnets pull iron objects." to "चुंबक लोहे की वस्तुओं को खींचते हैं।",
            "Batteries store energy." to "बैटरी ऊर्जा संग्रहीत करती है।",
            "Light helps us see." to "प्रकाश हमें देखने में मदद करता है।",
            "Sound is made by vibrations." to "ध्वनि कंपन से उत्पन्न होती है।",
            "A rainbow has seven colors." to "इंद्रधनुष में सात रंग होते हैं।",
            "A volcano shoots hot lava." to "ज्वालामुखी गर्म लावा उगलता है।",
            "Oceans are full of salt water." to "महासागर खारे पानी से भरे होते हैं।",
            "Mountains are very tall hills." to "पर्वत बहुत ऊँची पहाड़ियां होते हैं।"
        )
        return when (lang) {
            "ur" -> mapUr[en] ?: en
            "ar" -> mapAr[en] ?: en
            "hi" -> mapHi[en] ?: en
            else -> en
        }
    }

    private fun localizeColorName(en: String, lang: String): String {
        val mapUr = mapOf(
            "Red" to "لال", "Orange" to "نارنجی", "Yellow" to "پیلا", "Green" to "ہرا",
            "Blue" to "نیلا", "Purple" to "جامنی", "Pink" to "گلابی", "Brown" to "بھورا",
            "Black" to "کالا", "White" to "سفید", "Gray" to "سرمئی", "Cyan" to "فیروزی",
            "Lime" to "ہلکا ہرا", "Teal" to "سبز نیلا", "Indigo" to "گہرا نیلا", "Magenta" to "ارغوانی",
            "Gold" to "سنہرا", "Silver" to "چاندی", "Maroon" to "قرمزی", "Navy" to "گہرا نیلا"
        )
        val mapAr = mapOf(
            "Red" to "أحمر", "Orange" to "برتقالي", "Yellow" to "أصفر", "Green" to "أخضر",
            "Blue" to "أزرق", "Purple" to "أرجواني", "Pink" to "وردي", "Brown" to "بني",
            "Black" to "أسود", "White" to "أبيض", "Gray" to "رمادي", "Cyan" to "سماوي",
            "Lime" to "أخضر فاتح", "Teal" to "فيروزي", "Indigo" to "نيلي", "Magenta" to "أرجواني فاتح",
            "Gold" to "ذهبي", "Silver" to "فضي", "Maroon" to "عنابي", "Navy" to "كحلي"
        )
        val mapHi = mapOf(
            "Red" to "लाल", "Orange" to "नारंगी", "Yellow" to "पीला", "Green" to "हरा",
            "Blue" to "नीला", "Purple" to "बैंगनी", "Pink" to "गुलाबी", "Brown" to "भूरा",
            "Black" to "काला", "White" to "सफ़ेद", "Gray" to "ग्रे", "Cyan" to "फ़िरोज़ी",
            "Lime" to "हल्का हरा", "Teal" to "नील हरित", "Indigo" to "गहरा नीला", "Magenta" to "गहरा गुलाबी",
            "Gold" to "सुनहरा", "Silver" to "चाँदी", "Maroon" to "मरून", "Navy" to "गहरा नीला"
        )
        return when (lang) {
            "ur" -> mapUr[en] ?: en
            "ar" -> mapAr[en] ?: en
            "hi" -> mapHi[en] ?: en
            else -> en
        }
    }

    private fun localizeFruitName(en: String, lang: String): String {
        val mapUr = mapOf(
            "Apple" to "سیب", "Banana" to "کیلا", "Orange" to "نارنگی", "Grapes" to "انگور", "Watermelon" to "تربوز",
            "Strawberry" to "اسٹرابیری", "Cherries" to "چیری", "Pineapple" to "انناس", "Pear" to "ناشپاتی", "Mango" to "آم",
            "Lemon" to "لیموں", "Peach" to "آڑو", "Plum" to "آلوبخارا", "Coconut" to "ناریل", "Pomegranate" to "انار",
            "Kiwi" to "کیوی", "Papaya" to "پپیتا", "Guava" to "امرود", "Blueberry" to "بلوبیری", "Dates" to "کھجور",
            "Fig" to "انجیر", "Olive" to "زیتون", "Avocado" to "ایوکاڈو", "Carrot" to "گاجر", "Broccoli" to "بروکلی",
            "Cabbage" to "گوبھی", "Lettuce" to "سلاد", "Tomato" to "ٹماٹر", "Potato" to "آلو", "Corn" to "مکئی",
            "Peas" to "مٹر", "Cucumber" to "کھیرا", "Pumpkin" to "کدو", "Eggplant" to "بینگن", "Bell Pepper" to "شملہ مرچ",
            "Chili" to "مرچ", "Onion" to "پیاز", "Garlic" to "لہسن", "Mushroom" to "کھمبی", "Spinach" to "پالک", "Radish" to "مولی"
        )
        val mapAr = mapOf(
            "Apple" to "تفاحة", "Banana" to "موز", "Orange" to "برتقال", "Grapes" to "عنب", "Watermelon" to "بطيخ",
            "Strawberry" to "فراولة", "Cherries" to "كرز", "Pineapple" to "أناناس", "Pear" to "كمثرى", "Mango" to "مانجو",
            "Lemon" to "ليمون", "Peach" to "خوخ", "Plum" to "برقوق", "Coconut" to "جوز الهند", "Pomegranate" to "رمان",
            "Kiwi" to "كيوي", "Papaya" to "بابايا", "Guava" to "جوافة", "Blueberry" to "توت أزرق", "Dates" to "تمر",
            "Fig" to "تين", "Olive" to "زيتون", "Avocado" to "أفوكادو", "Carrot" to "جزر", "Broccoli" to "بروكلي",
            "Cabbage" to "ملفوف", "Lettuce" to "خس", "Tomato" to "طماطم", "Potato" to "بطاطس", "Corn" to "ذرة",
            "Peas" to "بازلاء", "Cucumber" to "خيار", "Pumpkin" to "يقطين", "Eggplant" to "باذنجان", "Bell Pepper" to "فلفل حلو",
            "Chili" to "فلفل حار", "Onion" to "بصل", "Garlic" to "ثوم", "Mushroom" to "فطر", "Spinach" to "سبانخ", "Radish" to "فجل"
        )
        val mapHi = mapOf(
            "Apple" to "सेब", "Banana" to "केला", "Orange" to "संतरा", "Grapes" to "अंगूर", "Watermelon" to "तरबूज",
            "Strawberry" to "स्ट्रॉबेरी", "Cherries" to "चेरी", "Pineapple" to "अनानास", "Pear" to "नाशपाती", "Mango" to "आम",
            "Lemon" to "नींबू", "Peach" to "आड़ू", "Plum" to "आलूबुखारा", "Coconut" to "नारियल", "Pomegranate" to "अनार",
            "Kiwi" to "कीवी", "Papaya" to "पपीता", "Guava" to "अमरूद", "Blueberry" to "ब्लूबेरी", "Dates" to "खजूर",
            "Fig" to "अंजीर", "Olive" to "जैतून", "Avocado" to "एवोकैडो", "Carrot" to "गाजर", "Broccoli" to "ब्रोकोली",
            "Cabbage" to "पत्तागोभी", "Lettuce" to "सलाद पत्ता", "Tomato" to "टमाटर", "Potato" to "आलू", "Corn" to "मक्का",
            "Peas" to "मटर", "Cucumber" to "खीरा", "Pumpkin" to "कद्दू", "Eggplant" to "बैंगन", "Bell Pepper" to "शिमला मिर्च",
            "Chili" to "मिर्च", "Onion" to "प्याज", "Garlic" to "लहसुन", "Mushroom" to "मशरूम", "Spinach" to "पालक", "Radish" to "मूली"
        )
        return when (lang) {
            "ur" -> mapUr[en] ?: en
            "ar" -> mapAr[en] ?: en
            "hi" -> mapHi[en] ?: en
            else -> en
        }
    }

    fun localizeQuizQuestion(q: com.brightnest.app.data.QuizQuestion, lang: String): com.brightnest.app.data.QuizQuestion {
        if (lang == "en") return q
        val urQuestions = mapOf(
            "How many days are in a week?" to ("ایک ہفتے میں کتنے دن ہوتے ہیں؟" to listOf("5", "6", "7", "8")),
            "What color is the sky on a clear day?" to ("صاف دن میں آسمان کا رنگ کیا ہوتا ہے؟" to listOf("ہرا", "نیلا", "لال", "پیلا")),
            "How many pillars of Islam are there?" to ("اسلام کے کتنے ارکان ہیں؟" to listOf("3", "4", "5", "6")),
            "Which animal says 'meow'?" to ("کون سا جانور 'میاؤں' کرتا ہے؟" to listOf("کتا", "بلی", "گائے", "بطخ")),
            "How many surahs are in the Quran?" to ("قرآن مجید میں کتنی سورتیں ہیں؟" to listOf("114", "110", "120", "100")),
            "What is 9 x 6?" to ("9 × 6 کا جواب کیا ہے؟" to listOf("54", "56", "63", "45")),
            "Which planet is closest to the Sun?" to ("کون سا سیارہ سورج کے سب سے قریب ہے؟" to listOf("زہرہ", "زمین", "عطارد", "مریخ")),
            "In which city is the Kaaba located?" to ("خانہ کعبہ کس شہر میں واقع ہے؟" to listOf("مدینہ", "مکہ", "قاہرہ", "استنبول")),
            "How many verses (ayat) are in Surah Al-Baqarah?" to ("سورۃ البقرہ میں کتنی آیات ہیں؟" to listOf("286", "200", "255", "300")),
            "What is the square root of 144?" to ("144 کا جزر کیا ہے؟" to listOf("11", "12", "13", "14")),
            "Which prophet is known as 'Kalimullah'?" to ("کس پیغمبر کو 'کلیم اللہ' کہا جاتا ہے؟" to listOf("حضرت عیسیٰ (AS)", "حضرت ابراہیم (AS)", "حضرت موسیٰ (AS)", "حضرت نوح (AS)")),
            "What year (CE) did the Hijra take place?" to ("ہجرتِ مدینہ عیسوی سال (CE) کے کس سن میں ہوئی؟" to listOf("610", "622", "630", "570"))
        )
        val arQuestions = mapOf(
            "How many days are in a week?" to ("كم عدد أيام الأسبوع؟" to listOf("5", "6", "7", "8")),
            "What color is the sky on a clear day?" to ("ما لون السماء في يوم صافٍ؟" to listOf("أخضر", "أزرق", "أحمر", "أصفر")),
            "How many pillars of Islam are there?" to ("كم عدد أركان الإسلام؟" to listOf("3", "4", "5", "6")),
            "Which animal says 'meow'?" to ("أي حيوان يقول 'مياو'؟" to listOf("كلب", "قطة", "بقرة", "بطة")),
            "How many surahs are in the Quran?" to ("كم عدد سور القرآن الكريم؟" to listOf("114", "110", "120", "100")),
            "What is 9 x 6?" to ("ما حاصل ضرب 9 × 6؟" to listOf("54", "56", "63", "45")),
            "Which planet is closest to the Sun?" to ("أي كوكب هو الأقرب إلى الشمس؟" to listOf("الزهرة", "الأرض", "عطارد", "المريخ")),
            "In which city is the Kaaba located?" to ("في أي مدينة تقع الكعبة المشرفة؟" to listOf("المدينة المنورة", "مكة المكرمة", "القاهرة", "إسطنبول")),
            "How many verses (ayat) are in Surah Al-Baqarah?" to ("كم عدد آيات سورة البقرة؟" to listOf("286", "200", "255", "300")),
            "What is the square root of 144?" to ("ما الجذر التربيعي للعدد 144؟" to listOf("11", "12", "13", "14")),
            "Which prophet is known as 'Kalimullah'?" to ("أي نبي يُعرف بلقب 'كليم الله'؟" to listOf("عيسى (AS)", "إبراهيم (AS)", "موسى (AS)", "نوح (AS)")),
            "What year (CE) did the Hijra take place?" to ("في أي سنة ميلادية (CE) حدثت الهجرة النبوية؟" to listOf("610", "622", "630", "570"))
        )
        val hiQuestions = mapOf(
            "How many days are in a week?" to ("एक सप्ताह में कितने दिन होते हैं?" to listOf("5", "6", "7", "8")),
            "What color is the sky on a clear day?" to ("साफ दिन में आसमान का रंग क्या होता है?" to listOf("हरा", "नीला", "लाल", "पीला")),
            "How many pillars of Islam are there?" to ("इस्लाम के कितने स्तंभ (अरकान) हैं?" to listOf("3", "4", "5", "6")),
            "Which animal says 'meow'?" to ("कौन सा जानवर 'म्याऊँ' करता है?" to listOf("कुत्ता", "बिल्ली", "गाय", "बत्तख")),
            "How many surahs are in the Quran?" to ("पवित्र कुरान में कितनी सूरतें हैं?" to listOf("114", "110", "120", "100")),
            "What is 9 x 6?" to ("9 × 6 का उत्तर क्या है?" to listOf("54", "56", "63", "45")),
            "Which planet is closest to the Sun?" to ("कौन सा ग्रह सूर्य के सबसे निकट है?" to listOf("शुक्र", "पृथ्वी", "बुध", "मंगल")),
            "In which city is the Kaaba located?" to ("काबा किस शहर में स्थित है?" to listOf("मदीना", "मक्का", "काहिरा", "इस्तांबुल")),
            "How many verses (ayat) are in Surah Al-Baqarah?" to ("सूरह अल-बक़रा में कितनी आयतें हैं?" to listOf("286", "200", "255", "300")),
            "What is the square root of 144?" to ("144 का वर्गमूल क्या है?" to listOf("11", "12", "13", "14")),
            "Which prophet is known as 'Kalimullah'?" to ("किस पैगंबर को 'कलीमुल्लाह' के नाम से जाना जाता है?" to listOf("ईसा (AS)", "इब्राहीम (AS)", "मूसा (AS)", "नूह (AS)")),
            "What year (CE) did the Hijra take place?" to ("हिजरा किस ईसवी वर्ष (CE) में हुआ था?" to listOf("610", "622", "630", "570"))
        )
        val entry = when (lang) {
            "ur" -> urQuestions[q.question]
            "ar" -> arQuestions[q.question]
            "hi" -> hiQuestions[q.question]
            else -> null
        }
        return if (entry != null) {
            val (qText, opts) = entry
            q.copy(
                question = qText,
                optionA = opts.getOrElse(0) { q.optionA },
                optionB = opts.getOrElse(1) { q.optionB },
                optionC = opts.getOrElse(2) { q.optionC },
                optionD = opts.getOrElse(3) { q.optionD }
            )
        } else q
    }

    private fun getUrduNumberWord(numStr: String): String {
        val n = numStr.toIntOrNull() ?: return numStr
        val map = mapOf(
            1 to "ایک", 2 to "دو", 3 to "تین", 4 to "چار", 5 to "پانچ",
            6 to "چھ", 7 to "سات", 8 to "آٹھ", 9 to "نو", 10 to "دس",
            11 to "گیارہ", 12 to "بارہ", 13 to "تیرہ", 14 to "چودہ", 15 to "پندرہ",
            16 to "سولہ", 17 to "سترہ", 18 to "اٹھارہ", 19 to "انیس", 20 to "بیس"
        )
        return map[n] ?: numStr
    }

    private fun getArabicNumberWord(numStr: String): String {
        val n = numStr.toIntOrNull() ?: return numStr
        val map = mapOf(
            1 to "واحد", 2 to "اثنان", 3 to "ثلاثة", 4 to "أربعة", 5 to "خمسة",
            6 to "ستة", 7 to "سبعة", 8 to "ثمانية", 9 to "تسعة", 10 to "عشرة"
        )
        return map[n] ?: numStr
    }

    private fun getHindiNumberWord(numStr: String): String {
        val n = numStr.toIntOrNull() ?: return numStr
        val map = mapOf(
            1 to "एक", 2 to "दो", 3 to "तीन", 4 to "चार", 5 to "पाँच",
            6 to "छह", 7 to "सात", 8 to "आठ", 9 to "नौ", 10 to "दस"
        )
        return map[n] ?: numStr
    }

    fun getArabicSubtitle(title: String): String {
        return when (title) {
            "ABC" -> "الحروف الإنجليزية"
            "Animals" -> "الحيوانات"
            "Colors" -> "الألوان"
            "Shapes" -> "الأشكال"
            "Body Parts" -> "أعضاء الجسم"
            "Science" -> "العلوم"
            "Fruits" -> "الفواكه والخضروات"
            "Numbers" -> "الأرقام"
            "Urdu" -> "الحروف الأردية"
            "Arabic" -> "الحروف العربية"
            else -> ""
        }
    }

    fun getHindiSubtitle(title: String): String {
        return when (title) {
            "ABC" -> "अंग्रेजी वर्णमाला"
            "Animals" -> "जानवर"
            "Colors" -> "रंग"
            "Shapes" -> "आकृतियाँ"
            "Body Parts" -> "शरीर के अंग"
            "Science" -> "विज्ञान"
            "Fruits" -> "फल और सब्जियाँ"
            "Numbers" -> "गिनती"
            "Urdu" -> "उर्दू वर्णमाला"
            "Arabic" -> "अरबी वर्णमाला"
            else -> ""
        }
    }

    fun localizeMenuItem(item: com.brightnest.app.ui.MenuItem, langCode: String): com.brightnest.app.ui.MenuItem {
        val code = langCode.lowercase()
        if (code == "en") return item
        val arTitles = mapOf(
            "ABC" to "الحروف الإنجليزية", "Numbers" to "الأرقام", "Multiplication Tables" to "جداول الضرب",
            "Dodging Tables" to "جداول عشوائية", "Tenses" to "الأزمنة", "Vowels" to "حروف العلة",
            "Basics" to "الأساسيات", "Urdu" to "الأردية", "Arabic" to "العربية", "Colors" to "الألوان",
            "Shapes" to "الأشكال", "Body Parts" to "أعضاء الجسم", "Animals" to "الحيوانات", "Fruits" to "الفواكه",
            "Science" to "العلوم", "Poems" to "الأناشيد", "Stories" to "القصص", "GK Quiz" to "مسابقة ثقافية",
            "Drawing" to "الرسم", "Mini Games" to "ألعاب ميني", "Quran" to "القرآن الكريم", "6 Kalmas" to "الكلمات الست",
            "Daily Duas" to "الأدعية اليومية", "Namaz" to "الصلاة", "Wudu" to "الوضوء", "99 Names" to "أسماء الله الحسنى",
            "Prayer Times" to "مواقيت الصلاة", "Leaderboard" to "لائحة المتصدرين"
        )
        val hiTitles = mapOf(
            "ABC" to "एबीसी", "Numbers" to "गिनती", "Multiplication Tables" to "पहाड़े",
            "Dodging Tables" to "डॉजिंग पहाड़े", "Tenses" to "काल (Tenses)", "Vowels" to "स्वर",
            "Basics" to "बुनियादी बातें", "Urdu" to "उर्दू", "Arabic" to "अरबी", "Colors" to "रंग",
            "Shapes" to "आकृतियाँ", "Body Parts" to "शरीर के अंग", "Animals" to "जानवर", "Fruits" to "फल",
            "Science" to "विज्ञान", "Poems" to "कविताएँ", "Stories" to "कहानियाँ", "GK Quiz" to "सामान्य ज्ञान",
            "Drawing" to "ड्राइंग", "Mini Games" to "मिनी गेम्स", "Quran" to "क़ुरआन", "6 Kalmas" to "छह कलमे",
            "Daily Duas" to "दैनिक दुआएं", "Namaz" to "नमाज़", "Wudu" to "वज़ू", "99 Names" to "अल्लाह के 99 नाम",
            "Prayer Times" to "नमाज़ का समय", "Leaderboard" to "स्कोरबोर्ड"
        )
        return when (code) {
            "ur" -> {
                val urTitle = if (item.urdu.isNotEmpty()) item.urdu else item.title
                item.copy(title = urTitle, urdu = item.title)
            }
            "ar" -> {
                val arTitle = arTitles[item.title] ?: item.title
                item.copy(title = arTitle, urdu = item.title)
            }
            "hi" -> {
                val hiTitle = hiTitles[item.title] ?: item.title
                item.copy(title = hiTitle, urdu = item.title)
            }
            else -> item
        }
    }

    fun localizeUiText(text: String, langCode: String): String {
        val code = langCode.lowercase()
        if (code == "en") return text
        val urMap = mapOf(
            "Good morning" to "صبح بخیر",
            "Assalamu Alaikum" to "السلام علیکم",
            "Kids" to "بچے",
            "Adult" to "بڑے / سرپرست",
            "Days Streak" to "دن کا سلسلہ",
            "Stars" to "ستارے",
            "Coins" to "سکے",
            "Featured Story" to "خاص کہانی",
            "Read about trust and patience..." to "صبر اور یقین کی کہانی پڑھیں...",
            "Next Prayer" to "اگلی نماز",
            "Today's Tasks" to "آج کے کام",
            "Remaining" to "باقی",
            "Alarm" to "الارم",
            "🕌 Prayer Times" to "🕌 اوقاتِ نماز",
            "✎ Edit" to "✎ ترمیم",
            "Spiritual" to "روحانیت",
            "Rewards" to "انعامات",
            "Adult Mode" to "بڑوں کا موڈ",
            "🌙  Adult Mode" to "🌙  بڑوں کا موڈ",
            "Settings" to "سیٹنگز",
            "Parent Controls" to "سرپرست کنٹرولز",
            "PREFERENCES" to "ترجیحات",
            "Dark Mode" to "ڈارک موڈ",
            "Notifications" to "نوٹیفیکیشنز",
            "Language" to "زبان",
            "Choose Language" to "زبان منتخب کریں",
            "REMINDERS" to "یاددہانی",
            "Water Reminders" to "پانی کی یاددہانی",
            "Prayer Times" to "نماز کے اوقات",
            "ABOUT" to "ہمارے بارے میں",
            "Version" to "ورژن",
            "Privacy Policy" to "پرائیویسی پالیسی",
            "Terms of Service" to "شرائط و ضوابط",
            "Sign Out" to "لاگ آؤٹ",
            "Learn" to "سیکھنا",
            "Home" to "ہوم",
            "Profile" to "پروفائل",
            "Let's Learn!" to "آؤ سیکھیں!",
            "Productivity & Tools" to "پروڈکٹیوٹی اور ٹولز",
            "ABC" to "اے بی سی",
            "Numbers" to "گنتی",
            "Multiplication Tables" to "پہاڑے",
            "Dodging Tables" to "ڈاجنگ",
            "Tenses" to "ٹینس",
            "Vowels" to "حروفِ علت",
            "Basics" to "بنیادی باتیں",
            "Colors" to "رنگ",
            "Shapes" to "شکلیں",
            "Body Parts" to "اعضاء",
            "Animals" to "جانور",
            "Fruits" to "پھل",
            "Science" to "سائنس",
            "Poems" to "نظمیں",
            "Stories" to "کہانیاں",
            "GK Quiz" to "معلوماتِ عامہ",
            "Drawing" to "ڈرائنگ",
            "Mini Games" to "کھیل",
            "Quran" to "قرآن",
            "6 Kalmas" to "چھ کلمے",
            "Daily Duas" to "روزانہ دعائیں",
            "Namaz" to "نماز",
            "Wudu" to "وضو",
            "99 Names" to "٩٩ نام",
            "Leaderboard" to "اسکور بورڈ",
            "Lesson" to "سبق",
            "Listen Pronunciation" to "تلفظ سنیں"
        )
        val arMap = mapOf(
            "Good morning" to "صباح الخير",
            "Assalamu Alaikum" to "السلام عليكم",
            "Kids" to "الأطفال",
            "Adult" to "البالغين",
            "Days Streak" to "أيام متتالية",
            "Stars" to "النجوم",
            "Coins" to "العملات",
            "Featured Story" to "قصة مميزة",
            "Read about trust and patience..." to "اقرأ عن الصبر والتوكل...",
            "Next Prayer" to "الصلاة القادمة",
            "Today's Tasks" to "مهام اليوم",
            "Remaining" to "المتبقي",
            "Alarm" to "تنبيه",
            "Spiritual" to "الروحانيات",
            "Rewards" to "المكافآت",
            "Adult Mode" to "وضع البالغين",
            "🌙  Adult Mode" to "🌙  وضع البالغين",
            "Settings" to "الإعدادات",
            "Parent Controls" to "الرقابة الأبوية",
            "PREFERENCES" to "التفضيلات",
            "Dark Mode" to "الوضع الداكن",
            "Notifications" to "الإشعارات",
            "Language" to "اللغة",
            "Choose Language" to "اختر اللغة",
            "REMINDERS" to "التذكيرات",
            "Water Reminders" to "تذكير شرب الماء",
            "Prayer Times" to "مواقيت الصلاة",
            "ABOUT" to "حول التطبيق",
            "Version" to "الإصدار",
            "Privacy Policy" to "سياسة الخصوصية",
            "Terms of Service" to "شروط الخدمة",
            "Sign Out" to "تسجيل الخروج",
            "Learn" to "التعلم",
            "Home" to "الرئيسية",
            "Profile" to "الملف الشخصي",
            "Let's Learn!" to "هيا نتعلم!",
            "Productivity & Tools" to "الأدوات والإنتاجية",
            "ABC" to "الحروف الإنجليزية",
            "Numbers" to "الأرقام",
            "Multiplication Tables" to "جداول الضرب",
            "Dodging Tables" to "تمارين الجداول",
            "Tenses" to "الأزمنة",
            "Vowels" to "حروف العلة",
            "Basics" to "الأساسيات",
            "Colors" to "الألوان",
            "Shapes" to "الأشكال",
            "Body Parts" to "أعضاء الجسم",
            "Animals" to "الحيوانات",
            "Fruits" to "الفواكه",
            "Science" to "العلوم",
            "Poems" to "الأناشيد",
            "Stories" to "القصص",
            "GK Quiz" to "أسئلة عامة",
            "Drawing" to "الرسم",
            "Mini Games" to "ألعاب صغيرة",
            "Quran" to "القرآن",
            "6 Kalmas" to "الكلمات الست",
            "Daily Duas" to "أدعية يومية",
            "Namaz" to "الصلاة",
            "Wudu" to "الوضوء",
            "99 Names" to "أسماء الله الحسنى",
            "Leaderboard" to "لوحة المتصدرين",
            "Lesson" to "الدرس",
            "Listen Pronunciation" to "استمع للنطق"
        )
        val hiMap = mapOf(
            "Good morning" to "सुप्रभात",
            "Assalamu Alaikum" to "अस्सलाम वालेकुम",
            "Kids" to "बच्चे",
            "Adult" to "वयस्क",
            "Days Streak" to "दिनों की श्रृंखला",
            "Stars" to "सितारे",
            "Coins" to "सिक्के",
            "Featured Story" to "विशेष कहानी",
            "Read about trust and patience..." to "विश्वास और धैर्य के बारे में पढ़ें...",
            "Next Prayer" to "अगली नमाज़",
            "Today's Tasks" to "आज के कार्य",
            "Remaining" to "शेष",
            "Alarm" to "अलार्म",
            "Spiritual" to "आध्यात्मिक",
            "Rewards" to "पुरस्कार",
            "Adult Mode" to "वयस्क मोड",
            "🌙  Adult Mode" to "🌙  वयस्क मोड",
            "Settings" to "सेटिंग्स",
            "Parent Controls" to "अभिभावक नियंत्रण",
            "PREFERENCES" to "प्राथमिकताएं",
            "Dark Mode" to "डार्क मोड",
            "Notifications" to "सूचनाएं",
            "Language" to "भाषा",
            "Choose Language" to "भाषा चुनें",
            "REMINDERS" to "रिमाइंडर",
            "Water Reminders" to "पानी का रिमाइंडर",
            "Prayer Times" to "नमाज़ का समय",
            "ABOUT" to "हमारे बारे में",
            "Version" to "संस्करण",
            "Privacy Policy" to "गोपनीयता नीति",
            "Terms of Service" to "सेवा की शर्तें",
            "Sign Out" to "साइन आउट",
            "Learn" to "सीखें",
            "Home" to "होम",
            "Profile" to "प्रोफ़ाइल",
            "Let's Learn!" to "चलो सीखें!",
            "Productivity & Tools" to "उत्पादकता और उपकरण",
            "ABC" to "अंग्रेजी वर्णमाला",
            "Numbers" to "गिनती",
            "Multiplication Tables" to "पहाड़े",
            "Dodging Tables" to "डॉजिंग पहाड़े",
            "Tenses" to "काल",
            "Vowels" to "स्वर",
            "Basics" to "बुनियादी बातें",
            "Colors" to "रंग",
            "Shapes" to "आकृतियाँ",
            "Body Parts" to "शरीर के अंग",
            "Animals" to "जानवर",
            "Fruits" to "फल",
            "Science" to "विज्ञान",
            "Poems" to "कविताएँ",
            "Stories" to "कहानियाँ",
            "GK Quiz" to "सामान्य ज्ञान",
            "Drawing" to "ड्राइंग",
            "Mini Games" to "मिनी गेम्स",
            "Quran" to "क़ुरआन",
            "6 Kalmas" to "छह कलमे",
            "Daily Duas" to "दैनिक दुआएं",
            "Namaz" to "नमाज़",
            "Wudu" to "वज़ू",
            "99 Names" to "अल्लाह के 99 नाम",
            "Leaderboard" to "स्कोरबोर्ड",
            "Lesson" to "सीख",
            "Listen Pronunciation" to "उच्चारण सुनें"
        )
        val map = when (code) {
            "ur" -> urMap
            "ar" -> arMap
            "hi" -> hiMap
            else -> emptyMap()
        }
        map[text]?.let { return it }
        if (text.startsWith("Good morning")) {
            val namePart = text.removePrefix("Good morning").trim()
            return "${map["Good morning"] ?: "Good morning"}$namePart"
        }
        if (text.startsWith("Assalamu Alaikum")) {
            val namePart = text.removePrefix("Assalamu Alaikum").trim()
            return "${map["Assalamu Alaikum"] ?: "Assalamu Alaikum"}$namePart"
        }
        return text
    }

    fun localizeViewTree(view: android.view.View?, langCode: String) {
        if (view == null || langCode.lowercase() == "en") return
        if (view is android.widget.TextView) {
            val original = view.text?.toString() ?: ""
            if (original.isNotEmpty()) {
                val loc = localizeUiText(original, langCode)
                if (loc != original) {
                    view.text = loc
                }
            }
        }
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                localizeViewTree(view.getChildAt(i), langCode)
            }
        }
    }

    fun localizeBottomNav(menu: android.view.Menu, langCode: String, isAdult: Boolean) {
        if (langCode.lowercase() == "en") return
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val titleStr = item.title?.toString() ?: continue
            item.title = localizeUiText(titleStr, langCode)
        }
    }
}
