package com.brightnest.app.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightnest.app.databinding.FragmentAiBinding

class AiFragment : Fragment() {

    private var _binding: FragmentAiBinding? = null
    private val binding get() = _binding!!

    data class Msg(val content: String, val isUser: Boolean)

    private val messages = mutableListOf<Msg>()
    private lateinit var adapter: ChatAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var isTyping = false

    private val prompts = listOf(
        "Help with homework",
        "Tell me a story",
        "Quiz me on history",
        "Practice English with me"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatAdapter(messages)
        binding.chatList.layoutManager = LinearLayoutManager(requireContext())
        binding.chatList.adapter = adapter

        buildPromptChips()
        updateState()

        binding.btnClear.setOnClickListener {
            messages.clear()
            adapter.notifyDataSetChanged()
            setFollowUps(emptyList())
            updateState()
        }
        binding.btnSend.setOnClickListener {
            send(binding.input.text?.toString().orEmpty())
        }
    }

    private fun buildPromptChips() {
        binding.promptsGrid.removeAllViews()
        prompts.forEach { p ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = p
                isClickable = true
                setOnClickListener { send(p) }
            }
            binding.promptsGrid.addView(chip)
        }
    }

    private fun updateState() {
        val empty = messages.isEmpty()
        binding.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
        binding.chatList.visibility = if (empty) View.GONE else View.VISIBLE
    }

    private fun setFollowUps(items: List<String>) {
        binding.followUps.removeAllViews()
        if (items.isEmpty() || messages.isEmpty()) {
            binding.followUps.visibility = View.GONE
            return
        }
        items.forEach { f ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = f
                isClickable = true
                setOnClickListener { send(f) }
            }
            binding.followUps.addView(chip)
        }
        binding.followUps.visibility = View.VISIBLE
    }

    private fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || isTyping) return

        val history = messages.map { Assistant.ChatTurn(if (it.isUser) "user" else "assistant", it.content) }

        messages.add(Msg(trimmed, true))
        binding.input.setText("")
        setFollowUps(emptyList())
        updateState()
        adapter.notifyItemInserted(messages.size - 1)
        binding.chatList.scrollToPosition(messages.size - 1)

        isTyping = true
        binding.typingIndicator.visibility = View.VISIBLE

        Thread {
            var onlineReply: String? = null
            try {
                onlineReply = Assistant.fetchAIReply(trimmed, history)
            } catch (_: Exception) {
                onlineReply = null
            }
            val offline = if (onlineReply == null) Assistant.getOfflineReply(trimmed) else null
            handler.post {
                isTyping = false
                if (_binding == null) return@post
                binding.typingIndicator.visibility = View.GONE
                if (onlineReply != null) {
                    messages.add(Msg(onlineReply, false))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.chatList.scrollToPosition(messages.size - 1)
                    setFollowUps(emptyList())
                } else if (offline != null) {
                    messages.add(Msg(offline.text, false))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.chatList.scrollToPosition(messages.size - 1)
                    setFollowUps(offline.followUps)
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        isTyping = false
        _binding = null
    }
}
