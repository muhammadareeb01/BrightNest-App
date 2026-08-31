package com.brightnest.app.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.R
import com.brightnest.app.content.KidsContent
import com.brightnest.app.databinding.ItemCardGridBinding

class CardGridAdapter(
    private val variant: KidsContent.Variant,
    private var items: List<KidsContent.Item>,
    private val onClick: (KidsContent.Item) -> Unit
) : RecyclerView.Adapter<CardGridAdapter.VH>() {

    inner class VH(val b: ItemCardGridBinding) : RecyclerView.ViewHolder(b.root)

    fun submit(newItems: List<KidsContent.Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCardGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val ctx = holder.b.root.context
        val density = ctx.resources.displayMetrics.density
        val radius = 16f * density
        val c = runCatching { Color.parseColor(item.colorHex) }.getOrDefault(Color.GRAY)

        val bg = GradientDrawable().apply { cornerRadius = radius }

        when (variant) {
            KidsContent.Variant.COLOR -> {
                bg.setColor(c)
                bg.setStroke((2 * density).toInt(), c)
                holder.b.itemMain.visibility = View.GONE
                holder.b.itemLabel.text = item.label
                holder.b.itemLabel.setTextColor(if (isLight(c)) Color.BLACK else Color.WHITE)
                holder.b.itemUrdu.visibility = View.GONE
            }
            KidsContent.Variant.LETTER -> {
                bg.setColor(ColorUtils.setAlphaComponent(c, 0x20))
                bg.setStroke((2 * density).toInt(), c)
                holder.b.itemMain.visibility = View.VISIBLE
                holder.b.itemMain.text = item.label
                holder.b.itemMain.textSize = 32f
                holder.b.itemMain.setTextColor(c)
                holder.b.itemLabel.visibility = View.GONE
                holder.b.itemUrdu.visibility = View.GONE
            }
            KidsContent.Variant.FRUIT -> {
                bg.setColor(ColorUtils.setAlphaComponent(c, 0x18))
                bg.setStroke((int(1.5f, density)), ColorUtils.setAlphaComponent(c, 0x55))
                holder.b.itemMain.visibility = View.VISIBLE
                holder.b.itemMain.text = item.emoji
                holder.b.itemMain.textSize = 30f
                holder.b.itemLabel.visibility = View.VISIBLE
                holder.b.itemLabel.text = item.label
                holder.b.itemUrdu.visibility = View.VISIBLE
                holder.b.itemUrdu.text = item.urdu
            }
            KidsContent.Variant.ICON -> {
                bg.setColor(ColorUtils.setAlphaComponent(c, 0x20))
                bg.setStroke((2 * density).toInt(), c)
                holder.b.itemMain.visibility = View.VISIBLE
                holder.b.itemMain.text = item.emoji
                holder.b.itemMain.textSize = 36f
                holder.b.itemLabel.visibility = View.VISIBLE
                holder.b.itemLabel.text = item.label
                holder.b.itemLabel.setTextColor(c)
                if (item.urdu.isNotEmpty() && item.urdu != item.label) {
                    holder.b.itemUrdu.visibility = View.VISIBLE
                    holder.b.itemUrdu.text = item.urdu
                } else {
                    holder.b.itemUrdu.visibility = View.GONE
                }
            }
            KidsContent.Variant.ALPHABET -> {
                bg.setColor(ColorUtils.setAlphaComponent(c, 0x20))
                bg.setStroke((2 * density).toInt(), c)
                holder.b.itemMain.visibility = View.VISIBLE
                holder.b.itemMain.text = item.label
                holder.b.itemMain.textSize = 36f
                holder.b.itemMain.setTextColor(c)
                holder.b.itemLabel.visibility = View.VISIBLE
                holder.b.itemLabel.text = item.detailText
                holder.b.itemLabel.textSize = 10f
                holder.b.itemLabel.setTextColor(ContextCompat.getColor(ctx, R.color.muted_foreground))
                holder.b.itemUrdu.visibility = View.GONE
            }
            KidsContent.Variant.NUMBER -> {
                bg.setColor(ColorUtils.setAlphaComponent(c, 0x20))
                bg.setStroke((2 * density).toInt(), c)
                holder.b.itemMain.visibility = View.VISIBLE
                holder.b.itemMain.text = item.label
                holder.b.itemMain.textSize = when {
                    item.label.length >= 3 -> 26f
                    item.label.length == 2 -> 32f
                    else -> 40f
                }
                holder.b.itemMain.setTextColor(c)
                holder.b.itemLabel.visibility = View.GONE
                holder.b.itemUrdu.visibility = View.GONE
            }
        }

        holder.b.root.background = bg
        holder.b.root.setOnClickListener { onClick(item) }
    }

    private fun int(v: Float, d: Float) = (v * d).toInt()

    private fun isLight(c: Int) = ColorUtils.calculateLuminance(c) > 0.6
}
