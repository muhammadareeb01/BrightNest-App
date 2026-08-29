package com.brightnest.app.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/** Minimal flow layout: lays children left-to-right, wrapping to new rows, honoring margins. */
class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val widthLimit = maxWidth - paddingLeft - paddingRight
        var rowWidth = 0
        var rowHeight = 0
        var totalHeight = 0
        var maxRowWidth = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as MarginLayoutParams
            val cw = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val ch = child.measuredHeight + lp.topMargin + lp.bottomMargin
            if (rowWidth + cw > widthLimit && rowWidth > 0) {
                maxRowWidth = maxOf(maxRowWidth, rowWidth)
                totalHeight += rowHeight
                rowWidth = 0
                rowHeight = 0
            }
            rowWidth += cw
            rowHeight = maxOf(rowHeight, ch)
        }
        maxRowWidth = maxOf(maxRowWidth, rowWidth)
        totalHeight += rowHeight

        val resolvedWidth = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) maxWidth
        else maxRowWidth + paddingLeft + paddingRight
        setMeasuredDimension(resolvedWidth, totalHeight + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val widthLimit = width - paddingRight
        var x = paddingLeft
        var y = paddingTop
        var rowHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            val lp = child.layoutParams as MarginLayoutParams
            val cw = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val ch = child.measuredHeight + lp.topMargin + lp.bottomMargin
            if (x + cw > widthLimit && x > paddingLeft) {
                x = paddingLeft
                y += rowHeight
                rowHeight = 0
            }
            child.layout(x + lp.leftMargin, y + lp.topMargin,
                x + lp.leftMargin + child.measuredWidth, y + lp.topMargin + child.measuredHeight)
            x += cw
            rowHeight = maxOf(rowHeight, ch)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = MarginLayoutParams(context, attrs)
    override fun generateDefaultLayoutParams(): LayoutParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams =
        if (p != null) MarginLayoutParams(p) else generateDefaultLayoutParams()
    override fun checkLayoutParams(p: LayoutParams?): Boolean = p is MarginLayoutParams
}
