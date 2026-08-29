package com.brightnest.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.databinding.ItemSimpleRowBinding

data class SimpleRow(val emoji: String, val title: String, val subtitle: String)

class SimpleRowAdapter(
    private val items: List<SimpleRow>,
    private val onClick: ((SimpleRow) -> Unit)? = null
) : RecyclerView.Adapter<SimpleRowAdapter.VH>() {

    inner class VH(val b: ItemSimpleRowBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemSimpleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.rowEmoji.text = item.emoji
        holder.b.rowTitle.text = item.title
        holder.b.rowSubtitle.text = item.subtitle
        holder.b.root.setOnClickListener { onClick?.invoke(item) }
    }
}
