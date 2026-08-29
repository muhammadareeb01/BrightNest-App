package com.brightnest.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Stroke(val path: Path, val paint: Paint)

    private val strokes = mutableListOf<Stroke>()
    private var currentPath = Path()
    private var currentColor = Color.parseColor("#15795B")
    private var currentWidth = 14f

    private var lastX = 0f
    private var lastY = 0f

    private fun createPaint(c: Int, w: Float) = Paint().apply {
        color = c
        strokeWidth = w
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    fun setColor(color: Int) {
        currentColor = color
    }

    fun setStrokeWidth(width: Float) {
        currentWidth = width
    }

    fun undo() {
        if (strokes.isNotEmpty()) {
            strokes.removeAt(strokes.size - 1)
            invalidate()
        }
    }

    fun clear() {
        strokes.clear()
        currentPath = Path()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        for (s in strokes) {
            canvas.drawPath(s.path, s.paint)
        }
        if (!currentPath.isEmpty) {
            canvas.drawPath(currentPath, createPaint(currentColor, currentWidth))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(x, y)
                currentPath.lineTo(x + 0.1f, y + 0.1f)
                lastX = x
                lastY = y
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(x - lastX)
                val dy = Math.abs(y - lastY)
                if (dx >= 4 || dy >= 4) {
                    currentPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                    lastX = x
                    lastY = y
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                currentPath.lineTo(x, y)
                strokes.add(Stroke(currentPath, createPaint(currentColor, currentWidth)))
                currentPath = Path()
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                currentPath = Path()
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
