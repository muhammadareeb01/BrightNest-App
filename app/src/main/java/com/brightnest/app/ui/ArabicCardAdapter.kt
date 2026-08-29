package com.brightnest.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.databinding.ItemArabicCardBinding

data class ArabicCardItem(
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String
)

class ArabicCardAdapter(
    private val items: List<ArabicCardItem>,
    // Passes (arabicText, transliterationText) so KalmasFragment decides which to speak
    private val onSpeak: ((arabic: String, translit: String) -> Unit)? = null
) : RecyclerView.Adapter<ArabicCardAdapter.VH>() {

    inner class VH(val b: ItemArabicCardBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemArabicCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.itemTitle.text = item.title
        holder.b.itemArabic.text = item.arabic
        holder.b.itemTranslit.text = item.transliteration
        holder.b.itemTranslation.text = item.translation

        holder.b.btnSpeak.setOnClickListener {
            onSpeak?.invoke(item.arabic, item.transliteration)
        }
    }
}
