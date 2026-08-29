package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentPoemsBinding
import com.brightnest.app.AppLanguageHelper
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PoemsFragment : Fragment() {

    private var _binding: FragmentPoemsBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private data class Poem(val title: String, val color: String, val lines: List<String>)

    private val palette = listOf(
        "#EF4444", "#F97316", "#F59E0B", "#EAB308", "#84CC16", "#22C55E", "#10B981", "#14B8A6",
        "#06B6D4", "#0EA5E9", "#3B82F6", "#6366F1", "#8B5CF6", "#A855F7", "#D946EF", "#EC4899"
    )

    private val poems: List<Poem> = listOf(
        "Twinkle Twinkle" to listOf(
            "Twinkle, twinkle, little star,", "How I wonder what you are!",
            "Up above the world so high,", "Like a diamond in the sky.",
            "When the blazing sun is gone,", "When he nothing shines upon,",
            "Then you show your little light,", "Twinkle, twinkle, all the night.",
            "Then the traveler in the dark", "Thanks you for your tiny spark,",
            "How could he see where to go,", "If you did not twinkle so?",
            "In the dark blue sky you keep,", "Often through my curtains peep",
            "For you never shut your eye,", "Till the sun is in the sky.",
            "As your bright and tiny spark", "Lights the traveler in the dark,",
            "Though I know not what you are,", "Twinkle, twinkle, little star."
        ),
        "Baa Baa Black Sheep" to listOf(
            "Baa, baa, black sheep,", "Have you any wool?",
            "Yes sir, yes sir,", "Three bags full.",
            "One for the master,", "And one for the dame,",
            "And one for the little boy", "Who lives down the lane.",
            "Baa, baa, black sheep,", "Have you any wool?",
            "Yes sir, yes sir,", "Three bags full."
        ),
        "Humpty Dumpty" to listOf(
            "Humpty Dumpty sat on a wall,", "Humpty Dumpty had a great fall;",
            "All the king's horses and all the king's men,", "Couldn't put Humpty together again.",
            "Humpty Dumpty counted to ten,", "Humpty Dumpty got up again!",
            "All the king's horses and all the king's men,", "Smiled to see Humpty happy again."
        ),
        "Jack and Jill" to listOf(
            "Jack and Jill went up the hill,", "To fetch a pail of water;",
            "Jack fell down and broke his crown,", "And Jill came tumbling after.",
            "Up Jack got, and home did trot,", "As fast as he could caper,",
            "To old Dame Dob, who patched his knob", "With vinegar and brown paper.",
            "Jill came in and she did grin", "To see Jack's paper plaster;",
            "Mother gave her a big tight hug", "And wished them well thereafter."
        ),
        "Mary Had a Little Lamb" to listOf(
            "Mary had a little lamb,", "Its fleece was white as snow;",
            "And everywhere that Mary went,", "The lamb was sure to go.",
            "It followed her to school one day,", "Which was against the rule;",
            "It made the children laugh and play,", "To see a lamb at school.",
            "And so the teacher turned it out,", "But still it lingered near,",
            "And waited patiently about", "Till Mary did appear.",
            "\"Why does the lamb love Mary so?\"", "The eager children cry;",
            "\"Why, Mary loves the lamb, you know,\"", "The teacher did reply."
        ),
        "Old MacDonald" to listOf(
            "Old MacDonald had a farm,", "E-I-E-I-O!",
            "And on that farm he had a cow,", "E-I-E-I-O!",
            "With a moo-moo here and a moo-moo there,",
            "Here a moo, there a moo, everywhere a moo-moo,",
            "Old MacDonald had a farm,", "E-I-E-I-O!",
            "And on that farm he had a duck,", "E-I-E-I-O!",
            "With a quack-quack here and a quack-quack there,",
            "Here a quack, there a quack, everywhere a quack-quack,",
            "Old MacDonald had a farm,", "E-I-E-I-O!",
            "And on that farm he had a sheep,", "E-I-E-I-O!",
            "With a baa-baa here and a baa-baa there,",
            "Here a baa, there a baa, everywhere a baa-baa,",
            "Old MacDonald had a farm,", "E-I-E-I-O!"
        ),
        "Rain Rain Go Away" to listOf(
            "Rain, rain, go away,", "Come again another day;",
            "Little Johnny wants to play,", "Rain, rain, go away.",
            "Rain, rain, go away,", "Come again another day;",
            "All the children want to play,", "Rain, rain, go away.",
            "Rain, rain, go to Spain,", "Never show your face again!",
            "Sunshine, sunshine, come today,", "Bright and warm in every way."
        ),
        "Row Your Boat" to listOf(
            "Row, row, row your boat,", "Gently down the stream;",
            "Merrily, merrily, merrily, merrily,", "Life is but a dream.",
            "Row, row, row your boat,", "Sneak along the shore;",
            "If you see a crocodile,", "Don't forget to roar!",
            "Row, row, row your boat,", "Gently down the river;",
            "If you see a polar bear,", "Don't forget to shiver!",
            "Row, row, row your boat,", "Gently to the sea;",
            "If you see a friendly fish,", "Wave happily with glee!"
        ),
        "Itsy Bitsy Spider" to listOf(
            "The itsy bitsy spider,", "Climbed up the water spout;",
            "Down came the rain,", "And washed the spider out.",
            "Out came the sun,", "And dried up all the rain;",
            "And the itsy bitsy spider,", "Climbed up the spout again.",
            "The itsy bitsy spider,", "Climbed up the kitchen wall;",
            "Swoosh went the fan,", "And made the spider fall.",
            "Off went the fan,", "No longer did it blow;",
            "So the itsy bitsy spider,", "Back up the wall did go."
        ),
        "Wheels on the Bus" to listOf(
            "The wheels on the bus go round and round,", "Round and round, round and round;",
            "The wheels on the bus go round and round,", "All through the town.",
            "The wipers on the bus go swish, swish, swish,", "Swish, swish, swish, swish, swish, swish;",
            "The wipers on the bus go swish, swish, swish,", "All through the town.",
            "The horn on the bus goes beep, beep, beep,", "Beep, beep, beep, beep, beep, beep;",
            "The horn on the bus goes beep, beep, beep,", "All through the town.",
            "The doors on the bus go open and shut,", "Open and shut, open and shut;",
            "The doors on the bus go open and shut,", "All through the town.",
            "The babies on the bus go waa, waa, waa,", "Waa, waa, waa, waa, waa, waa;",
            "The babies on the bus go waa, waa, waa,", "All through the town.",
            "The parents on the bus go shh, shh, shh,", "Shh, shh, shh, shh, shh, shh;",
            "The parents on the bus go shh, shh, shh,", "All through the town."
        )
    ).mapIndexed { i, (title, lines) -> Poem(title, palette[i % palette.size], lines) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPoemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        tts = TextToSpeech(requireContext().applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                ttsReady = true
                AppLanguageHelper.configureTts(tts, Prefs(requireContext()).language)
                tts?.setSpeechRate(0.85f)
            }
        }

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.detailClose.setOnClickListener { hideDetail() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.detailRoot.visibility == View.VISIBLE) hideDetail()
                else { isEnabled = false; findNavController().navigateUp() }
            }
        })

        val ctx = requireContext()
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val muted = ContextCompat.getColor(ctx, R.color.muted_foreground)

        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as com.brightnest.app.BrightNestApp).database
            val cached = try { db.poemDao().all() } catch (e: Exception) { emptyList() }
            val isFull = cached.isNotEmpty() && cached.all { p ->
                val linesCount = try {
                    val trimmed = p.linesJson.trim().removePrefix("[").removeSuffix("]")
                    trimmed.split("\",\"").size
                } catch (e: Exception) { 0 }
                linesCount >= 6
            }

            val list = if (isFull) {
                cached.mapNotNull { pEnt ->
                    val lines = try {
                        val trimmed = pEnt.linesJson.trim().removePrefix("[").removeSuffix("]")
                        if (trimmed.isEmpty()) emptyList()
                        else trimmed.split("\",\"").map { it.trim('"').replace("\\\"", "\"") }
                    } catch (e: Exception) { emptyList() }
                    if (lines.isNotEmpty()) Poem(pEnt.title, pEnt.color, lines) else null
                }
            } else {
                try {
                    db.poemDao().insertAll(com.brightnest.app.data.SeedData.poems)
                } catch (_: Exception) {}
                poems
            }
            val renderList = if (list.isNotEmpty()) list else poems
            if (_binding == null) return@launch
            binding.container.removeAllViews()
            renderList.forEach { p ->
                val c = try { Color.parseColor(p.color) } catch (e: Exception) { Color.parseColor("#EF4444") }
                val card = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(16), dp(16), dp(16), dp(16))
                    background = GradientDrawable().apply {
                        cornerRadius = dp(16).toFloat()
                        setColor(ColorUtils.setAlphaComponent(c, 0x20))
                        setStroke(dp(2), c)
                    }
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        .apply { bottomMargin = dp(12) }
                    setOnClickListener { handleSelect(p) }
                }
                card.addView(TextView(ctx).apply {
                    text = "🎵"; textSize = 28f; setTextColor(c)
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(16) }
                })
                val col = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                col.addView(TextView(ctx).apply { text = p.title; setTextColor(c); textSize = 16f; setTypeface(typeface, Typeface.BOLD) })
                col.addView(TextView(ctx).apply { text = p.lines.firstOrNull() ?: ""; setTextColor(muted); textSize = 13f; maxLines = 1 })
                card.addView(col)
                card.addView(TextView(ctx).apply { text = "▶️"; textSize = 24f; setTextColor(c) })
                binding.container.addView(card)
            }
        }
    }

    private fun handleSelect(p: Poem) {
        val ctx = requireContext()
        val c = Color.parseColor(p.color)
        val d = resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()
        val fg = ContextCompat.getColor(ctx, R.color.foreground)

        binding.listRoot.visibility = View.GONE
        binding.detailRoot.setBackgroundColor(ContextCompat.getColor(ctx, R.color.background))
        binding.detailTitle.text = p.title
        binding.detailTitle.setTextColor(c)
        binding.detailNote.setTextColor(c)
        binding.detailLines.removeAllViews()
        p.lines.forEach { line ->
            binding.detailLines.addView(TextView(ctx).apply {
                text = line; setTextColor(fg); textSize = 20f; gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    .apply { topMargin = dp(8); bottomMargin = dp(8) }
            })
        }
        binding.detailListen.background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(c) }
        binding.detailListen.setOnClickListener { speak(p.lines.joinToString(" ")) }

        Prefs(ctx).addCoins(1)
        binding.detailRoot.visibility = View.VISIBLE
    }

    private fun hideDetail() {
        tts?.stop()
        binding.detailRoot.visibility = View.GONE
        binding.listRoot.visibility = View.VISIBLE
    }

    private fun speak(t: String) {
        if (ttsReady) { tts?.stop(); tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "bn") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop(); tts?.shutdown(); tts = null
        _binding = null
    }
}
