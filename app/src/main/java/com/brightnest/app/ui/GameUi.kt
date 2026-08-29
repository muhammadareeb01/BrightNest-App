package com.brightnest.app.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

object GameUi {
    fun dp(ctx: Context, v: Int) = (v * ctx.resources.displayMetrics.density).toInt()

    fun dpf(ctx: Context, v: Int) = v * ctx.resources.displayMetrics.density

    fun rounded(color: Int, radiusPx: Float, strokeColor: Int? = null, strokePx: Int = 0): GradientDrawable =
        GradientDrawable().apply {
            cornerRadius = radiusPx
            setColor(color)
            if (strokeColor != null) setStroke(strokePx, strokeColor)
        }

    fun withAlpha(color: Int, alpha: Int) =
        Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
}
