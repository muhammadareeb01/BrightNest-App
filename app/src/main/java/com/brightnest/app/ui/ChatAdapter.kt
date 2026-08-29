package com.brightnest.app.ui

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.R
import com.brightnest.app.databinding.ItemChatBubbleBinding

class ChatAdapter(
    private val items: List<AiFragment.Msg>
) : RecyclerView.Adapter<ChatAdapter.VH>() {

    inner class VH(val b: ItemChatBubbleBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemChatBubbleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val ctx = holder.itemView.context
        holder.b.bubbleText.text = item.content
        val lp = holder.b.bubble.layoutParams as FrameLayout.LayoutParams
        if (item.isUser) {
            lp.gravity = Gravity.END
            holder.b.bubble.setCardBackgroundColor(ctx.getColor(R.color.primary))
            holder.b.bubbleText.setTextColor(ctx.getColor(R.color.on_primary))
        } else {
            lp.gravity = Gravity.START
            holder.b.bubble.setCardBackgroundColor(ctx.getColor(R.color.card))
            holder.b.bubbleText.setTextColor(ctx.getColor(R.color.foreground))
        }
        holder.b.bubble.layoutParams = lp
    }
}
