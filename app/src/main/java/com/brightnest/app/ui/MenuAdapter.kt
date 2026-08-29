package com.brightnest.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.databinding.ItemMenuTileBinding

class MenuAdapter(
    private val items: List<MenuItem>,
    private val onClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.VH>() {

    inner class VH(val b: ItemMenuTileBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemMenuTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tileTitle.text = item.title
        holder.b.tileUrdu.text = item.urdu
        holder.b.tileEmoji.text = item.emoji
        holder.b.tileCircle.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, item.colorRes)
        )
        holder.b.root.setOnClickListener { onClick(item) }
    }
}
