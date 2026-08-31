package com.brightnest.app.content

/**
 * Static reference data for the kids "card grid" learning screens.
 * Faithful 1:1 port of the Expo screens (abc, animals, colors, shapes,
 * body-parts, science, fruits). MaterialCommunityIcons are mapped to emojis.
 */
object KidsContent {

    val PALETTE = listOf(
        "#EF4444", "#F97316", "#F59E0B", "#EAB308",
        "#84CC16", "#22C55E", "#10B981", "#14B8A6",
        "#06B6D4", "#0EA5E9", "#3B82F6", "#6366F1",
        "#8B5CF6", "#A855F7", "#D946EF", "#EC4899",
        "#F43F5E", "#E11D48", "#DB2777", "#7C3AED"
    )

    enum class Variant { LETTER, ICON, COLOR, FRUIT, ALPHABET, NUMBER }

    data class Item(
        val label: String,
        val urdu: String = "",
        val emoji: String = "",
        val colorHex: String = "",
        val detailText: String = "",
        val speakText: String = "",
        val category: String = ""
    )

    data class Screen(
        val title: String,
        val urdu: String,
        val variant: Variant,
        val columns: Int,
        val headerColor: String,
        val items: List<Item>
    )

    private fun paletted(items: List<Item>): List<Item> =
        items.mapIndexed { i, it ->
            if (it.colorHex.isEmpty()) it.copy(colorHex = PALETTE[i % PALETTE.size]) else it
        }

    private val abc = paletted(
        listOf(
            Item("A", emoji = "🍎", detailText = "Apple", speakText = "A is for Apple"),
            Item("B", emoji = "🐻", detailText = "Bear", speakText = "B is for Bear"),
            Item("C", emoji = "🐱", detailText = "Cat", speakText = "C is for Cat"),
            Item("D", emoji = "🐶", detailText = "Dog", speakText = "D is for Dog"),
            Item("E", emoji = "🐘", detailText = "Elephant", speakText = "E is for Elephant"),
            Item("F", emoji = "🐟", detailText = "Fish", speakText = "F is for Fish"),
            Item("G", emoji = "🦒", detailText = "Giraffe", speakText = "G is for Giraffe"),
            Item("H", emoji = "🎩", detailText = "Hat", speakText = "H is for Hat"),
            Item("I", emoji = "🍦", detailText = "Ice Cream", speakText = "I is for Ice Cream"),
            Item("J", emoji = "🪼", detailText = "Jellyfish", speakText = "J is for Jellyfish"),
            Item("K", emoji = "🔑", detailText = "Key", speakText = "K is for Key"),
            Item("L", emoji = "🦁", detailText = "Lion", speakText = "L is for Lion"),
            Item("M", emoji = "🌙", detailText = "Moon", speakText = "M is for Moon"),
            Item("N", emoji = "🪺", detailText = "Nest", speakText = "N is for Nest"),
            Item("O", emoji = "🦉", detailText = "Owl", speakText = "O is for Owl"),
            Item("P", emoji = "🐧", detailText = "Penguin", speakText = "P is for Penguin"),
            Item("Q", emoji = "👑", detailText = "Queen", speakText = "Q is for Queen"),
            Item("R", emoji = "🐰", detailText = "Rabbit", speakText = "R is for Rabbit"),
            Item("S", emoji = "☀️", detailText = "Sun", speakText = "S is for Sun"),
            Item("T", emoji = "🐢", detailText = "Turtle", speakText = "T is for Turtle"),
            Item("U", emoji = "☂️", detailText = "Umbrella", speakText = "U is for Umbrella"),
            Item("V", emoji = "🌋", detailText = "Volcano", speakText = "V is for Volcano"),
            Item("W", emoji = "🍉", detailText = "Watermelon", speakText = "W is for Watermelon"),
            Item("X", emoji = "🎵", detailText = "Xylophone", speakText = "X is for Xylophone"),
            Item("Y", emoji = "🧶", detailText = "Yarn", speakText = "Y is for Yarn"),
            Item("Z", emoji = "🦓", detailText = "Zebra", speakText = "Z is for Zebra")
        )
    )

    private fun animal(name: String, emoji: String, sound: String) =
        Item(name, emoji = emoji, detailText = sound, speakText = "$name. $sound")

    private val animals = paletted(
        listOf(
            animal("Lion", "🦁", "Roar"),
            animal("Tiger", "🐯", "Roar"),
            animal("Leopard", "🐆", "Snarl"),
            animal("Cheetah", "🐆", "Chirp"),
            animal("Jaguar", "🐆", "Growl"),
            animal("Lynx", "🐱", "Yowl"),
            animal("Elephant", "🐘", "Trumpet"),
            animal("Giraffe", "🦒", "Tall and quiet"),
            animal("Zebra", "🦓", "Bray"),
            animal("Hippo", "🦛", "Grunt"),
            animal("Rhino", "🦏", "Snort"),
            animal("Monkey", "🐵", "Ooh ooh ah ah"),
            animal("Gorilla", "🦍", "Beat chest"),
            animal("Chimp", "🐒", "Hoot"),
            animal("Rabbit", "🐰", "Hop hop"),
            animal("Squirrel", "🐿️", "Chatter"),
            animal("Hedgehog", "🦔", "Snuffle"),
            animal("Dog", "🐶", "Woof woof"),
            animal("Puppy", "🐕", "Yip yip"),
            animal("Cat", "🐱", "Meow"),
            animal("Kitten", "🐈", "Mew"),
            animal("Cow", "🐮", "Moo"),
            animal("Sheep", "🐑", "Baa"),
            animal("Horse", "🐴", "Neigh"),
            animal("Pony", "🐴", "Whinny"),
            animal("Bird", "🐦", "Tweet tweet"),
            animal("Parrot", "🦜", "Squawk"),
            animal("Eagle", "🦅", "Screech"),
            animal("Peacock", "🦚", "Call"),
            animal("Camel", "🐫", "Hummm"),
            animal("Deer", "🦌", "Quiet"),
            animal("Moose", "🫎", "Bellow"),
            animal("Bear", "🐻", "Growl"),
            animal("Polar Bear", "🐻‍❄️", "Roar"),
            animal("Wolf", "🐺", "Howl"),
            animal("Fox", "🦊", "Yip"),
            animal("Panda", "🐼", "Munch"),
            animal("Koala", "🐨", "Snore"),
            animal("Kangaroo", "🦘", "Thump"),
            animal("Penguin", "🐧", "Squawk"),
            animal("Owl", "🦉", "Hoo hoo"),
            animal("Duck", "🦆", "Quack"),
            animal("Goose", "🦢", "Honk"),
            animal("Swan", "🦢", "Trumpet"),
            animal("Chicken", "🐔", "Cluck"),
            animal("Rooster", "🐓", "Cock-a-doodle-doo"),
            animal("Turkey", "🦃", "Gobble"),
            animal("Pig", "🐷", "Oink"),
            animal("Goat", "🐐", "Bleat"),
            animal("Donkey", "🫏", "Hee-haw"),
            animal("Fish", "🐟", "Blub blub"),
            animal("Shark", "🦈", "Splash"),
            animal("Dolphin", "🐬", "Click click"),
            animal("Whale", "🐳", "Whoosh"),
            animal("Octopus", "🐙", "Squish"),
            animal("Crab", "🦀", "Snip snip"),
            animal("Seahorse", "🐠", "Quiet"),
            animal("Starfish", "⭐", "Silent"),
            animal("Turtle", "🐢", "Slow and steady"),
            animal("Tortoise", "🐢", "Quiet"),
            animal("Frog", "🐸", "Ribbit"),
            animal("Toad", "🐸", "Croak"),
            animal("Snake", "🐍", "Hissss"),
            animal("Lizard", "🦎", "Quiet"),
            animal("Crocodile", "🐊", "Snap"),
            animal("Alligator", "🐊", "Bellow"),
            animal("Bee", "🐝", "Buzz buzz"),
            animal("Butterfly", "🦋", "Flutter"),
            animal("Dragonfly", "🦋", "Whirr"),
            animal("Spider", "🕷️", "Quiet"),
            animal("Ant", "🐜", "Tiny"),
            animal("Snail", "🐌", "Slow and quiet"),
            animal("Ladybug", "🐞", "Tiny"),
            animal("Jellyfish", "🪼", "Float"),
            animal("Bat", "🦇", "Squeak"),
            animal("Mouse", "🐭", "Squeak")
        )
    )

    private val colors = listOf(
        Item("Red", colorHex = "#EF4444", speakText = "Red"),
        Item("Orange", colorHex = "#F97316", speakText = "Orange"),
        Item("Yellow", colorHex = "#FACC15", speakText = "Yellow"),
        Item("Green", colorHex = "#22C55E", speakText = "Green"),
        Item("Blue", colorHex = "#3B82F6", speakText = "Blue"),
        Item("Purple", colorHex = "#A855F7", speakText = "Purple"),
        Item("Pink", colorHex = "#EC4899", speakText = "Pink"),
        Item("Brown", colorHex = "#92400E", speakText = "Brown"),
        Item("Black", colorHex = "#1F2937", speakText = "Black"),
        Item("White", colorHex = "#F5F5F4", speakText = "White"),
        Item("Gray", colorHex = "#9CA3AF", speakText = "Gray"),
        Item("Cyan", colorHex = "#06B6D4", speakText = "Cyan"),
        Item("Lime", colorHex = "#84CC16", speakText = "Lime"),
        Item("Teal", colorHex = "#14B8A6", speakText = "Teal"),
        Item("Indigo", colorHex = "#6366F1", speakText = "Indigo"),
        Item("Magenta", colorHex = "#D946EF", speakText = "Magenta"),
        Item("Gold", colorHex = "#EAB308", speakText = "Gold"),
        Item("Silver", colorHex = "#D1D5DB", speakText = "Silver"),
        Item("Maroon", colorHex = "#7F1D1D", speakText = "Maroon"),
        Item("Navy", colorHex = "#1E3A8A", speakText = "Navy")
    )

    private val shapes = paletted(
        listOf(
            Item("Circle", urdu = "دائرہ", emoji = "🔴", detailText = "A round 2D shape with no straight sides or corners.", speakText = "Circle. Round shape with no corners."),
            Item("Square", urdu = "مربع / چوکور", emoji = "🟦", detailText = "A shape with 4 equal sides and 4 equal right angles.", speakText = "Square. 4 equal sides."),
            Item("Triangle", urdu = "مثلث / تکون", emoji = "🔺", detailText = "A shape with 3 straight sides and 3 sharp corners.", speakText = "Triangle. 3 sides."),
            Item("Rectangle", urdu = "مستطیل", emoji = "🟧", detailText = "A 4-sided shape with equal opposite sides and 4 right angles.", speakText = "Rectangle. 4 sides with opposite sides equal."),
            Item("Star", urdu = "ستارہ", emoji = "⭐", detailText = "A beautiful twinkling 5-pointed geometric star.", speakText = "Star. 5 points."),
            Item("Heart", urdu = "دل", emoji = "❤️", detailText = "A classic heart shape symbol of care and love.", speakText = "Heart shape."),
            Item("Diamond", urdu = "ہیرا / لوزینج", emoji = "💎", detailText = "A rhombus shape with 4 slanted sides like a gem.", speakText = "Diamond shape."),
            Item("Oval", urdu = "بیضوی", emoji = "🥚", detailText = "An elongated rounded curve shape like an egg.", speakText = "Oval. Egg shape."),
            Item("Pentagon", urdu = "مخمس (5 کونے)", emoji = "🛑", detailText = "A polygon shape with 5 straight sides and 5 angles.", speakText = "Pentagon. 5 sides."),
            Item("Hexagon", urdu = "مسدس (6 کونے)", emoji = "⬢", detailText = "A polygon shape with 6 straight sides like a honeycomb.", speakText = "Hexagon. 6 sides."),
            Item("Octagon", urdu = "مثمن (8 کونے)", emoji = "🛑", detailText = "An 8-sided polygon commonly used for Stop signs.", speakText = "Octagon. 8 sides."),
            Item("Crescent", urdu = "ہلال / چاند", emoji = "🌙", detailText = "A curved moon shape with tapering sharp ends.", speakText = "Crescent moon."),
            Item("Plus", urdu = "جمع / پلس", emoji = "➕", detailText = "A cross symbol with 4 perpendicular equal arms.", speakText = "Plus cross shape."),
            Item("Arrow", urdu = "تیر کا نشان", emoji = "⬆️", detailText = "A directional pointer with a shaft and triangle head.", speakText = "Arrow shape."),
            Item("Cube", urdu = "مکعب (تھری ڈی ڈبہ)", emoji = "🎲", detailText = "A 3D solid box with 6 identical square faces.", speakText = "Cube. 3D solid with 6 square faces."),
            Item("Sphere", urdu = "کرہ (گیند)", emoji = "🔮", detailText = "A perfectly round 3D solid shape like a ball.", speakText = "Sphere. 3D round ball."),
            Item("Cylinder", urdu = "استوانہ (سلنڈر)", emoji = "🛢️", detailText = "A 3D solid with 2 circular bases and a curved tube.", speakText = "Cylinder shape."),
            Item("Cone", urdu = "مخروط (کون)", emoji = "🍦", detailText = "A 3D shape that tapers smoothly from a circular base to a point.", speakText = "Cone shape.")
        )
    )

    private val bodyParts = paletted(
        listOf(
            Item("Head", emoji = "🧑", speakText = "Head"),
            Item("Eye", emoji = "👁️", speakText = "Eye"),
            Item("Ear", emoji = "👂", speakText = "Ear"),
            Item("Nose", emoji = "👃", speakText = "Nose"),
            Item("Mouth", emoji = "👄", speakText = "Mouth"),
            Item("Teeth", emoji = "🦷", speakText = "Teeth"),
            Item("Tongue", emoji = "👅", speakText = "Tongue"),
            Item("Hair", emoji = "💇", speakText = "Hair"),
            Item("Neck", emoji = "🧣", speakText = "Neck"),
            Item("Shoulder", emoji = "🦾", speakText = "Shoulder"),
            Item("Arm", emoji = "💪", speakText = "Arm"),
            Item("Hand", emoji = "✋", speakText = "Hand"),
            Item("Finger", emoji = "👆", speakText = "Finger"),
            Item("Chest", emoji = "🫁", speakText = "Chest"),
            Item("Stomach", emoji = "🍽️", speakText = "Stomach"),
            Item("Heart", emoji = "❤️", speakText = "Heart"),
            Item("Back", emoji = "🦴", speakText = "Back"),
            Item("Leg", emoji = "🦵", speakText = "Leg"),
            Item("Knee", emoji = "🦵", speakText = "Knee"),
            Item("Foot", emoji = "🦶", speakText = "Foot"),
            Item("Toe", emoji = "🦶", speakText = "Toe"),
            Item("Brain", emoji = "🧠", speakText = "Brain"),
            Item("Lungs", emoji = "🫁", speakText = "Lungs"),
            Item("Skin", emoji = "🖐️", speakText = "Skin")
        )
    )

    private fun fact(name: String, emoji: String, f: String) =
        Item(name, emoji = emoji, detailText = f, speakText = f)

    private val science = paletted(
        listOf(
            fact("Sun", "☀️", "The Sun gives us light and heat."),
            fact("Moon", "🌙", "The Moon shines at night."),
            fact("Stars", "⭐", "Stars are giant balls of fire far away."),
            fact("Earth", "🌍", "We all live on planet Earth."),
            fact("Cloud", "☁️", "Clouds bring us rain."),
            fact("Rain", "🌧️", "Rain helps plants grow."),
            fact("Snow", "❄️", "Snow is frozen water."),
            fact("Wind", "💨", "Wind is moving air."),
            fact("Water", "💧", "Water is needed by all living things."),
            fact("Fire", "🔥", "Fire is hot. Stay safe!"),
            fact("Tree", "🌳", "Trees give us fresh air."),
            fact("Flower", "🌸", "Flowers attract bees and butterflies."),
            fact("Magnet", "🧲", "Magnets pull iron objects."),
            fact("Battery", "🔋", "Batteries store energy."),
            fact("Light", "💡", "Light helps us see."),
            fact("Sound", "🔊", "Sound is made by vibrations."),
            fact("Rainbow", "🌈", "A rainbow has seven colors."),
            fact("Volcano", "🌋", "A volcano shoots hot lava."),
            fact("Ocean", "🌊", "Oceans are full of salt water."),
            fact("Mountain", "⛰️", "Mountains are very tall hills.")
        )
    )

    private fun food(name: String, urdu: String, emoji: String, color: String, cat: String, f: String) =
        Item(name, urdu = urdu, emoji = emoji, colorHex = color, detailText = f, speakText = name, category = cat)

    private val fruits = listOf(
        food("Apple", "سیب", "🍎", "#EF4444", "fruit", "An apple a day keeps the doctor away!"),
        food("Banana", "کیلا", "🍌", "#FACC15", "fruit", "Bananas grow upside-down on trees."),
        food("Orange", "نارنگی", "🍊", "#F97316", "fruit", "Oranges are full of Vitamin C."),
        food("Grapes", "انگور", "🍇", "#7C3AED", "fruit", "Grapes grow in big bunches called clusters."),
        food("Watermelon", "تربوز", "🍉", "#22C55E", "fruit", "Watermelon is 92% water!"),
        food("Strawberry", "اسٹرابیری", "🍓", "#E11D48", "fruit", "The only fruit with seeds on the outside."),
        food("Cherries", "چیری", "🍒", "#BE123C", "fruit", "Cherries come in pairs on one stem."),
        food("Pineapple", "انناس", "🍍", "#EAB308", "fruit", "Pineapples take 2 years to grow!"),
        food("Pear", "ناشپاتی", "🍐", "#84CC16", "fruit", "Pears float because they have air inside."),
        food("Mango", "آم", "🥭", "#F59E0B", "fruit", "Mango is the king of fruits in Pakistan!"),
        food("Lemon", "لیموں", "🍋", "#FDE047", "fruit", "Lemons are very sour but very healthy."),
        food("Peach", "آڑو", "🍑", "#FB923C", "fruit", "Peaches have fuzzy soft skin."),
        food("Plum", "آلوبخارا", "🟣", "#9333EA", "fruit", "Dried plums become prunes."),
        food("Coconut", "ناریل", "🥥", "#A8A29E", "fruit", "Coconuts can float across oceans!"),
        food("Pomegranate", "انار", "🔴", "#B91C1C", "fruit", "One pomegranate can have 600 seeds!"),
        food("Kiwi", "کیوی", "🥝", "#7C5E2A", "fruit", "Kiwi has more Vitamin C than orange."),
        food("Papaya", "پپیتا", "🟠", "#F97316", "fruit", "Papayas grow on trees, not bushes."),
        food("Guava", "امرود", "🟢", "#EC4899", "fruit", "Guava skin is edible and healthy!"),
        food("Blueberry", "بلوبیری", "🫐", "#3B82F6", "fruit", "Blueberries are great for your brain."),
        food("Dates", "کھجور", "🟤", "#92400E", "fruit", "Prophet Muhammad ﷺ loved eating dates."),
        food("Fig", "انجیر", "🟣", "#7C3AED", "fruit", "Figs are mentioned in the Holy Quran."),
        food("Olive", "زیتون", "🫒", "#4D7C0F", "fruit", "Olives are blessed in the Quran."),
        food("Avocado", "ایوکاڈو", "🥑", "#65A30D", "fruit", "Avocado is actually a fruit, not a veggie!"),
        food("Carrot", "گاجر", "🥕", "#F97316", "veg", "Carrots help you see better in the dark."),
        food("Broccoli", "بروکلی", "🥦", "#15803D", "veg", "Broccoli is a tiny green tree!"),
        food("Cabbage", "گوبھی", "🥬", "#84CC16", "veg", "Cabbage has layers like a book."),
        food("Lettuce", "سلاد", "🥬", "#22C55E", "veg", "Lettuce is mostly water and very fresh."),
        food("Tomato", "ٹماٹر", "🍅", "#DC2626", "veg", "Tomatoes are actually fruits, but we cook them like veggies."),
        food("Potato", "آلو", "🥔", "#A16207", "veg", "Potatoes grow underground."),
        food("Corn", "مکئی", "🌽", "#FACC15", "veg", "Corn has silky threads at the top."),
        food("Peas", "مٹر", "🟢", "#16A34A", "veg", "Peas hide inside green pods."),
        food("Cucumber", "کھیرا", "🥒", "#22C55E", "veg", "Cucumbers stay cool inside in summer."),
        food("Pumpkin", "کدو", "🎃", "#EA580C", "veg", "Pumpkins can grow bigger than you!"),
        food("Eggplant", "بینگن", "🍆", "#6B21A8", "veg", "Eggplant is purple and shiny outside."),
        food("Bell Pepper", "شملہ مرچ", "🫑", "#DC2626", "veg", "Bell peppers come in red, yellow & green!"),
        food("Chili", "مرچ", "🌶️", "#DC2626", "veg", "Chillies make food spicy and hot."),
        food("Onion", "پیاز", "🧅", "#C084FC", "veg", "Onions can make you cry while cutting."),
        food("Garlic", "لہسن", "🧄", "#F4ECD8", "veg", "Garlic is great for fighting germs!"),
        food("Mushroom", "کھمبی", "🍄", "#A16207", "veg", "Mushrooms are not plants — they're fungi!"),
        food("Spinach", "پالک", "🥬", "#15803D", "veg", "Spinach makes you strong like Popeye."),
        food("Radish", "مولی", "🔴", "#E11D48", "veg", "Radishes are crunchy and a bit spicy.")
    )

    private fun alpha(letter: String, rom: String) =
        Item(label = letter, detailText = rom, speakText = rom)

    private val urduLetters = paletted(
        listOf(
            alpha("ا", "Alif"), alpha("ب", "Bay"), alpha("پ", "Pay"), alpha("ت", "Tay"),
            alpha("ٹ", "Ttay"), alpha("ث", "Say"), alpha("ج", "Jeem"), alpha("چ", "Chay"),
            alpha("ح", "Baṛi Hay"), alpha("خ", "Khay"), alpha("د", "Daal"), alpha("ڈ", "Ddaal"),
            alpha("ذ", "Zaal"), alpha("ر", "Ray"), alpha("ڑ", "Rray"), alpha("ز", "Zay"),
            alpha("ژ", "Zhay"), alpha("س", "Seen"), alpha("ش", "Sheen"), alpha("ص", "Suad"),
            alpha("ض", "Zuad"), alpha("ط", "Toay"), alpha("ظ", "Zoay"), alpha("ع", "Ain"),
            alpha("غ", "Ghain"), alpha("ف", "Fay"), alpha("ق", "Qaaf"), alpha("ک", "Kaaf"),
            alpha("گ", "Gaaf"), alpha("ل", "Laam"), alpha("م", "Meem"), alpha("ن", "Noon"),
            alpha("ں", "Noon Ghunnah"), alpha("و", "Wao"), alpha("ہ", "Choṭi Hay"),
            alpha("ھ", "Do-Chashmi Hay"), alpha("ء", "Hamza"), alpha("ی", "Yay"), alpha("ے", "Bari Yay")
        )
    )

    private val arabicLetters = paletted(
        listOf(
            alpha("ا", "Alif"), alpha("ب", "Ba"), alpha("ت", "Ta"), alpha("ث", "Tha"),
            alpha("ج", "Jeem"), alpha("ح", "Haa"), alpha("خ", "Kha"), alpha("د", "Daal"),
            alpha("ذ", "Dhaal"), alpha("ر", "Ra"), alpha("ز", "Zay"), alpha("س", "Seen"),
            alpha("ش", "Sheen"), alpha("ص", "Saad"), alpha("ض", "Daad"), alpha("ط", "Taa"),
            alpha("ظ", "Zaa"), alpha("ع", "Ain"), alpha("غ", "Ghain"), alpha("ف", "Fa"),
            alpha("ق", "Qaaf"), alpha("ك", "Kaaf"), alpha("ل", "Laam"), alpha("م", "Meem"),
            alpha("ن", "Noon"), alpha("ه", "Ha"), alpha("و", "Waw"), alpha("ي", "Ya")
        )
    )

    private val numbers = (1..100).map { n ->
        Item(label = n.toString(), colorHex = PALETTE[(n - 1) % PALETTE.size], speakText = n.toString())
    }

    val screens = listOf(
        Screen("ABC", "اے بی سی", Variant.LETTER, 4, "coral", abc),
        Screen("Animals", "جانور", Variant.ICON, 4, "coral", animals),
        Screen("Colors", "رنگ", Variant.COLOR, 4, "coral", colors),
        Screen("Shapes", "شکلیں", Variant.ICON, 3, "coral", shapes),
        Screen("Body Parts", "اعضاء", Variant.ICON, 4, "coral", bodyParts),
        Screen("Science", "سائنس", Variant.ICON, 4, "coral", science),
        Screen("Fruits", "پھل و سبزیاں", Variant.FRUIT, 3, "sage", fruits),
        Screen("Numbers", "گنتی", Variant.NUMBER, 4, "sage", numbers),
        Screen("Urdu", "اردو", Variant.ALPHABET, 4, "lavender", urduLetters),
        Screen("Arabic", "عربی", Variant.ALPHABET, 4, "primary", arabicLetters)
    )

    fun byTitle(title: String?): Screen? = screens.firstOrNull { it.title == title }
}
