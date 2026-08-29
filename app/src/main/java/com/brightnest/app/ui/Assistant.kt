package com.brightnest.app.ui

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

object Assistant {

    data class Reply(val text: String, val followUps: List<String> = emptyList())

    data class ChatTurn(val role: String, val content: String)

    // Mirrors EXPO_PUBLIC_DOMAIN in lib/assistant.ts. Unset by default → no API base,
    // so fetchAIReply throws and the caller falls back to the offline engine.
    private const val DOMAIN = ""
    private val apiBase: String
        get() = if (DOMAIN.isNotBlank()) "https://" + DOMAIN.replace(Regex("^https?://"), "") else ""

    /** Throws on any failure (incl. no configured API base) so caller falls back to offline. */
    fun fetchAIReply(message: String, history: List<ChatTurn>): String {
        val base = apiBase
        if (base.isEmpty()) throw IllegalStateException("No API base URL configured")
        val url = URL("$base/api/ai/chat")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 30000
            readTimeout = 30000
            setRequestProperty("Content-Type", "application/json")
        }
        try {
            val body = JSONObject().apply {
                put("message", message)
                put("history", JSONArray().apply {
                    history.takeLast(10).forEach { put(JSONObject().put("role", it.role).put("content", it.content)) }
                })
            }
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = conn.responseCode
            if (code !in 200..299) throw IllegalStateException("AI request failed ($code)")
            val text = conn.inputStream.bufferedReader().use(BufferedReader::readText)
            val reply = JSONObject(text).optString("reply", "")
            if (reply.isBlank()) throw IllegalStateException("Empty AI reply")
            return reply
        } finally {
            conn.disconnect()
        }
    }

    private fun <T> pick(arr: List<T>): T = arr[(Math.random() * arr.size).toInt().coerceIn(0, arr.size - 1)]

    private val STORIES = listOf(
        "Once upon a time, a small bird learned to share her seeds with hungry friends — and they all became a flock that flew together forever.",
        "A kind boy named Yusuf found a lost kitten and gave it warm milk. The kitten purred and never left his side.",
        "In a quiet village, a girl planted one date seed every morning. Years later, the whole hill was full of date trees feeding everyone.",
        "Two brothers argued over the last piece of bread, but then split it in half and shared with a stranger. That night their mother baked them ten loaves."
    )

    private val HOMEWORK_TIPS = listOf(
        "Break the work into small 10-minute chunks with short breaks in between — your brain learns better that way.",
        "Read the question twice, underline the key words, then answer. This single habit boosts test scores a lot.",
        "Teach what you learned to someone else (even a stuffed animal!). If you can explain it, you understand it.",
        "Do the hardest subject first while your mind is fresh, easier ones at the end."
    )

    private val ENGLISH_PRACTICE = listOf(
        "Let's practice! Tell me three things you did today, in full sentences. I'll help you make them better.",
        "Try this: describe your favourite food using five words — colour, taste, smell, texture, and feeling.",
        "Pick an object near you and describe it without naming it. I'll try to guess what it is!",
        "Make a sentence using these three words: 'happy', 'because', 'sunny'."
    )

    private val HISTORY_FACTS = listOf(
        "The Great Wall of China is over 21,000 km long — it's the longest structure ever built by humans.",
        "Prophet Muhammad ﷺ was born in the year 570 CE in the city of Makkah.",
        "Egypt's pyramids of Giza are about 4,500 years old and were originally covered in shiny white limestone.",
        "The first university in the world was founded in 859 CE in Fez, Morocco, by a Muslim woman named Fatima al-Fihri.",
        "Muslim scholars in Baghdad's House of Wisdom (~800 CE) translated and preserved most of ancient Greek science."
    )

    private val ISLAMIC = listOf(
        "Prayer (Salah) is performed 5 times a day: Fajr, Dhuhr, Asr, Maghrib, and Isha. Each one is a beautiful reset for the heart.",
        "Allah ﷻ has 99 beautiful names. One of them is Ar-Rahman — The Most Merciful.",
        "The Holy Quran has 114 surahs (chapters) and was revealed over 23 years.",
        "Charity (sadaqah) doesn't have to be money — a smile, kind words, or helping someone are also sadaqah."
    )

    private val MATH_HELP = listOf(
        "For addition: line up the numbers by place value (ones under ones, tens under tens), then add column by column from right to left.",
        "Multiplication trick: any number × 9, the digits of the answer add up to 9. (e.g. 9×7=63, 6+3=9).",
        "To check division: multiply your answer by the divisor. You should get the original number back.",
        "When you see a word problem, circle the question, underline the numbers, then write the operation (+, −, ×, ÷) you'll use."
    )

    private val QUIZ_QUESTIONS = listOf(
        "Quiz time! What is 7 × 8?",
        "Quiz time! Which planet is the largest in our solar system?",
        "Quiz time! What is the capital of Pakistan?",
        "Quiz time! Which animal is called the king of the jungle?",
        "Quiz time! How many sides does a hexagon have?",
        "Quiz time! What gas do plants breathe in that humans breathe out?"
    )

    private val GREETINGS = listOf(
        "Assalamu Alaikum! I'm BrightNest's assistant. How can I help today?",
        "Hello! I'm here to help with homework, stories, quizzes, or anything you'd like to learn.",
        "Hi there! Ask me about prayers, school, history, or just tell me what you're curious about."
    )

    private val ENCOURAGE = listOf(
        "Great question! Let's think about it together.",
        "That's a wonderful thing to wonder about.",
        "I love that you're asking — curiosity is a superpower."
    )

    private fun calc(input: String): String? {
        val m = Regex("(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*x/×÷])\\s*(-?\\d+(?:\\.\\d+)?)", RegexOption.IGNORE_CASE).find(input)
            ?: return null
        val a = m.groupValues[1].toDouble()
        val b = m.groupValues[3].toDouble()
        val op = m.groupValues[2].replace("x", "*").replace("×", "*").replace("÷", "/")
        val r: Double = when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> {
                if (b == 0.0) return "Dividing by zero isn't allowed in math — it's like trying to share cookies with zero friends!"
                a / b
            }
            else -> return null
        }
        val rStr = if (r == Math.floor(r) && !r.isInfinite()) r.toLong().toString() else String.format("%.2f", r)
        val aStr = if (a == Math.floor(a)) a.toLong().toString() else a.toString()
        val bStr = if (b == Math.floor(b)) b.toLong().toString() else b.toString()
        return "$aStr ${m.groupValues[2]} $bStr = $rStr"
    }

    private data class Rule(val match: Regex, val reply: (String) -> Reply)

    private val RULES = listOf(
        Rule(Regex("^(hi|hello|hey|salam|assalam|salaam)\\b", RegexOption.IGNORE_CASE)) {
            Reply(pick(GREETINGS), listOf("Help with homework", "Tell me a story", "Quiz me", "Teach me something"))
        },
        Rule(Regex("\\b(story|stories|tale|kahani)\\b", RegexOption.IGNORE_CASE)) {
            Reply(pick(STORIES), listOf("Another story", "Make it about animals", "What's the lesson?"))
        },
        Rule(Regex("\\b(homework|study|studies|exam|test|study tips?)\\b", RegexOption.IGNORE_CASE)) {
            Reply(pick(HOMEWORK_TIPS), listOf("Another tip", "Help me focus", "How to memorize?"))
        },
        Rule(Regex("\\b(english|grammar|spelling|writing|practice)\\b", RegexOption.IGNORE_CASE)) {
            Reply(pick(ENGLISH_PRACTICE), listOf("Easier please", "Another one", "Check my sentence"))
        },
        Rule(Regex("\\b(history|past|ancient|historical|civilization)\\b", RegexOption.IGNORE_CASE)) {
            Reply(pick(HISTORY_FACTS), listOf("Tell me more", "Another fact", "About Muslim history"))
        },
        Rule(Regex("\\b(quran|islam|allah|prophet|muhammad|salah|prayer|namaz|dua|sunnah)\\b", RegexOption.IGNORE_CASE)) {
            Reply(pick(ISLAMIC), listOf("Tell me more", "About prayer times", "About the Prophet ﷺ"))
        },
        Rule(Regex("\\b(math|maths|sum|add|subtract|multiply|divide|calculation)\\b", RegexOption.IGNORE_CASE)) { input ->
            val c = calc(input)
            if (c != null) Reply(c, listOf("Another sum", "Math tip", "Quiz me"))
            else Reply(pick(MATH_HELP), listOf("Practice math", "Another tip", "Hard sum please"))
        },
        Rule(Regex("\\b(quiz|test me|ask me|question)\\b", RegexOption.IGNORE_CASE)) {
            Reply(pick(QUIZ_QUESTIONS), listOf("Another question", "Easier please", "Harder please"))
        },
        Rule(Regex("^\\s*[\\d+\\-*x/×÷.\\s()]+\\s*[?=]?\\s*$")) { input ->
            val c = calc(input)
            if (c != null) Reply(c, listOf("Another sum", "Quiz me"))
            else Reply("I couldn't compute that — try something like '12 + 7'.")
        },
        Rule(Regex("\\b(thank|shukriya|jazak)", RegexOption.IGNORE_CASE)) {
            Reply("You're very welcome! I'm always here when you need me.")
        },
        Rule(Regex("\\b(what (?:is|are) you|who are you|tell me about yourself)\\b", RegexOption.IGNORE_CASE)) {
            Reply("I'm BrightNest's friendly learning assistant! I can help with homework, stories, math, English, Islamic knowledge, and quizzes. Just ask anything!", listOf("Help with homework", "Tell me a story", "Quiz me"))
        }
    )

    fun getOfflineReply(input: String): Reply {
        val text = input.trim()
        if (text.isEmpty()) return Reply("Type something and I'll do my best to help!")

        for (rule in RULES) {
            if (rule.match.containsMatchIn(text)) return rule.reply(text)
        }

        val c = calc(text)
        if (c != null) return Reply(c, listOf("Another sum", "Quiz me"))

        val isQuestion = Regex("[?]|^(what|why|how|when|where|who|can|do|does|is|are|will|should)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)
        if (isQuestion) {
            val snippet = if (text.length > 60) text.substring(0, 60) + "..." else text
            return Reply(
                "${pick(ENCOURAGE)} You asked about \"$snippet\". I'm an offline helper, so I work best on topics like homework, stories, math, English, history, prayers, and quizzes. Try asking one of those, or pick a quick prompt below.",
                listOf("Help with homework", "Tell me a story", "Quiz me", "Math practice")
            )
        }

        if (text.lowercase().length < 12) {
            return Reply(
                "Tell me a little more about \"$text\" — what do you want to know or do?",
                listOf("Explain it simply", "Quiz me on it", "Tell me a fun fact")
            )
        }

        val snippet = if (text.length > 80) text.substring(0, 80) + "..." else text
        return Reply(
            "Got it — \"$snippet\". I can help you turn this into a story, a quiz question, or a study summary. What would you like?",
            listOf("Make it a story", "Make a quiz", "Summarize it", "Explain it simply")
        )
    }
}
