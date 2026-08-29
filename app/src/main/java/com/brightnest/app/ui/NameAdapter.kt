package com.brightnest.app.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.R
import com.brightnest.app.databinding.ItemNameBinding

data class NameItem(val n: Int, val ar: String, val tr: String, val en: String, val ur: String)

class NameAdapter(
    private var items: List<NameItem>,
    private val onClick: (NameItem) -> Unit
) : RecyclerView.Adapter<NameAdapter.VH>() {

    var playingIndex: Int = -1
        set(value) {
            val old = field
            field = value
            if (old != -1 && old < items.size) notifyItemChanged(old)
            if (value != -1 && value < items.size) notifyItemChanged(value)
        }

    fun getItems(): List<NameItem> = items

    inner class VH(val b: ItemNameBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val ctx = holder.b.root.context
        val isPlaying = position == playingIndex

        val primary = ContextCompat.getColor(ctx, R.color.primary)
        val d = ctx.resources.displayMetrics.density

        holder.b.numBadge.text = item.n.toString()
        holder.b.numBadge.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(primary)
        }
        holder.b.tr.text = item.tr
        holder.b.en.text = item.en
        holder.b.ur.text = item.ur
        holder.b.ar.text = item.ar

        if (isPlaying) {
            holder.b.root.background = GradientDrawable().apply {
                cornerRadius = 14 * d
                setColor(ContextCompat.getColor(ctx, R.color.secondary))
                setStroke((2 * d).toInt(), primary)
            }
        } else {
            holder.b.root.setBackgroundResource(R.drawable.card_rounded)
        }

        holder.b.root.setOnClickListener { onClick(item) }
    }

    fun submit(newItems: List<NameItem>) {
        items = newItems
        playingIndex = -1
        notifyDataSetChanged()
    }
}
