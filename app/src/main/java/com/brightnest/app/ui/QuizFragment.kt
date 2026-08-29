package com.brightnest.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.brightnest.app.BrightNestApp
import com.brightnest.app.R
import com.brightnest.app.data.QuizQuestion
import com.brightnest.app.databinding.FragmentQuizBinding
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private var questions: List<QuizQuestion> = emptyList()
    private var index = 0
    private var score = 0
    private var answered = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnEasy.setOnClickListener { startQuiz("easy") }
        binding.btnMedium.setOnClickListener { startQuiz("medium") }
        binding.btnHard.setOnClickListener { startQuiz("hard") }
        binding.btnNextQ.setOnClickListener { nextQuestion() }
        binding.btnPlayAgain.setOnClickListener { showDifficulty() }
    }

    private fun startQuiz(level: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as BrightNestApp).database
            questions = db.quizDao().byDifficulty(level, 10)
            index = 0; score = 0
            val b = _binding ?: return@launch
            if (questions.isEmpty()) { showDifficulty(); return@launch }
            b.difficultyView.visibility = View.GONE
            b.resultView.visibility = View.GONE
            b.questionView.visibility = View.VISIBLE
            renderQuestion()
        }
    }

    private fun renderQuestion() {
        answered = false
        val rawQ = questions[index]
        val lang = com.brightnest.app.Prefs(requireContext()).language
        val q = com.brightnest.app.AppLanguageHelper.localizeQuizQuestion(rawQ, lang)
        val progressText = when (lang) {
            "ur" -> "سوال ${index + 1}/${questions.size}"
            "ar" -> "السؤال ${index + 1}/${questions.size}"
            "hi" -> "प्रश्न ${index + 1}/${questions.size}"
            else -> "Question ${index + 1}/${questions.size}"
        }
        binding.progressLabel.text = progressText
        binding.questionText.text = q.question
        binding.btnNextQ.visibility = View.INVISIBLE
        binding.optionsContainer.removeAllViews()

        val options = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
        options.forEachIndexed { i, text ->
            val btn = Button(requireContext()).apply {
                this.text = text
                isAllCaps = false
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.ink))
                setBackgroundResource(R.drawable.card_rounded)
                val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(56))
                lp.bottomMargin = dp(12)
                layoutParams = lp
                setOnClickListener { onAnswer(i, this) }
            }
            binding.optionsContainer.addView(btn)
        }
    }

    private fun onAnswer(chosen: Int, btn: Button) {
        if (answered) return
        answered = true
        val correct = questions[index].correctIndex
        if (chosen == correct) {
            score++
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_primary))
            btn.setTextColor(Color.WHITE)
        } else {
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_red))
            btn.setTextColor(Color.WHITE)
            val correctBtn = binding.optionsContainer.getChildAt(correct) as Button
            correctBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_primary))
            correctBtn.setTextColor(Color.WHITE)
        }
        binding.btnNextQ.visibility = View.VISIBLE
        val lang = com.brightnest.app.Prefs(requireContext()).language
        if (index == questions.size - 1) {
            binding.btnNextQ.text = when (lang) {
                "ur" -> "ختم کریں"
                "ar" -> "إنهاء"
                "hi" -> "समाप्त"
                else -> "Finish"
            }
        } else {
            binding.btnNextQ.text = when (lang) {
                "ur" -> "اگلا"
                "ar" -> "التالي"
                "hi" -> "अगला"
                else -> getString(R.string.next)
            }
        }
    }

    private fun nextQuestion() {
        if (index < questions.size - 1) {
            index++
            renderQuestion()
        } else {
            showResult()
        }
    }

    private fun showResult() {
        binding.questionView.visibility = View.GONE
        binding.resultView.visibility = View.VISIBLE
        val lang = com.brightnest.app.Prefs(requireContext()).language
        binding.resultText.text = when (lang) {
            "ur" -> "آپ کا اسکور: $score/${questions.size}"
            "ar" -> "تيجتك: $score/${questions.size}"
            "hi" -> "आपका स्कोर: $score/${questions.size}"
            else -> "You scored $score/${questions.size}"
        }
        val ratio = score.toDouble() / questions.size
        binding.resultEmoji.text = when {
            ratio >= 0.8 -> "🏆"
            ratio >= 0.5 -> "🎉"
            else -> "💪"
        }
    }

    private fun showDifficulty() {
        binding.resultView.visibility = View.GONE
        binding.questionView.visibility = View.GONE
        binding.difficultyView.visibility = View.VISIBLE
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
