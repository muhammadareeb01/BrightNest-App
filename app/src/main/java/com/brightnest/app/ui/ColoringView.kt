package com.brightnest.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.PathParser

class ColoringView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    enum class Mode { FILL, PEN }

    data class Stroke(val path: Path, val color: Int, val width: Float)

    private var mode: Mode = Mode.FILL
    private var currentColor: Int = Color.parseColor("#EF4444")
    private var brushWidth: Float = 22f

    private var picture: ColorPicture? = null
    private var fills: Map<String, String> = emptyMap()
    private val strokes = mutableListOf<Stroke>()
    private var currentPath = Path()

    var onRegionTap: ((String) -> Unit)? = null
    var onStrokeFinished: ((Stroke) -> Unit)? = null

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val penPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private var scale = 1f
    private var offsetX = 0f
    private var offsetY = 0f

    fun setMode(newMode: Mode) {
        mode = newMode
    }

    fun getMode(): Mode = mode

    fun setCurrentColor(color: Int) {
        currentColor = color
    }

    fun setBrushWidth(width: Float) {
        brushWidth = width
    }

    fun setPicture(p: ColorPicture) {
        picture = p
        invalidate()
    }

    fun setFills(f: Map<String, String>) {
        fills = f
        invalidate()
    }

    fun setStrokes(s: List<Stroke>) {
        strokes.clear()
        strokes.addAll(s)
        invalidate()
    }

    fun getStrokes(): List<Stroke> = strokes

    fun addStroke(stroke: Stroke) {
        strokes.add(stroke)
        invalidate()
    }

    fun removeLastStroke(): Stroke? {
        if (strokes.isNotEmpty()) {
            val last = strokes.removeAt(strokes.size - 1)
            invalidate()
            return last
        }
        return null
    }

    fun clearStrokes() {
        strokes.clear()
        currentPath = Path()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pic = picture ?: return
        val size = minOf(width, height).toFloat()
        if (size <= 0f) return
        scale = size / 300f
        offsetX = (width - size) / 2f
        offsetY = (height - size) / 2f

        // 1. Draw base picture filled regions
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.scale(scale, scale)

        for (region in pic.regions) {
            canvas.save()
            canvas.translate(region.left.toFloat(), region.top.toFloat())
            val regionColor = fills[region.id] ?: "#FFFFFF"
            for (prim in region.prims) {
                canvas.save()
                applyTransform(canvas, prim.attrs["transform"])
                drawPrim(canvas, prim, regionColor, drawOutline = true)
                canvas.restore()
            }
            canvas.restore()
        }
        canvas.restore()

        // 2. Draw freehand brush strokes
        for (s in strokes) {
            penPaint.color = s.color
            penPaint.strokeWidth = s.width
            canvas.drawPath(s.path, penPaint)
        }

        // 3. Draw active ongoing stroke
        if (!currentPath.isEmpty) {
            penPaint.color = currentColor
            penPaint.strokeWidth = brushWidth
            canvas.drawPath(currentPath, penPaint)
        }

        // 4. Re-draw picture outlines on top if pen brush strokes exist so outlines stay crisp
        if (strokes.isNotEmpty() || !currentPath.isEmpty) {
            canvas.save()
            canvas.translate(offsetX, offsetY)
            canvas.scale(scale, scale)
            for (region in pic.regions) {
                canvas.save()
                canvas.translate(region.left.toFloat(), region.top.toFloat())
                for (prim in region.prims) {
                    canvas.save()
                    applyTransform(canvas, prim.attrs["transform"])
                    drawOutlineOnly(canvas, prim)
                    canvas.restore()
                }
                canvas.restore()
            }
            canvas.restore()
        }
    }

    private fun drawOutlineOnly(canvas: Canvas, prim: ColorPrim) {
        val a = prim.attrs
        val strokeStr = a["stroke"]
        val strokeWidth = a["strokeWidth"]?.toFloatOrNull() ?: 0f
        if (strokeStr != null && strokeStr != "none" && strokeWidth > 0f) {
            if (prim.type == "Line") {
                strokePaint.color = parseColor(strokeStr, Color.BLACK)
                strokePaint.strokeWidth = strokeWidth
                canvas.drawLine(
                    a["x1"]?.toFloatOrNull() ?: 0f, a["y1"]?.toFloatOrNull() ?: 0f,
                    a["x2"]?.toFloatOrNull() ?: 0f, a["y2"]?.toFloatOrNull() ?: 0f, strokePaint
                )
                return
            }
            val path = buildPath(prim) ?: return
            if (a["fillRule"] == "evenodd") path.fillType = Path.FillType.EVEN_ODD
            strokePaint.color = parseColor(strokeStr, Color.BLACK)
            strokePaint.strokeWidth = strokeWidth
            canvas.drawPath(path, strokePaint)
        }
    }

    private fun applyTransform(canvas: Canvas, transform: String?) {
        if (transform.isNullOrBlank()) return
        val m = Regex("rotate\\(([^)]+)\\)").find(transform) ?: return
        val parts = m.groupValues[1].trim().split(Regex("[ ,]+")).mapNotNull { it.toFloatOrNull() }
        when (parts.size) {
            1 -> canvas.rotate(parts[0])
            3 -> canvas.rotate(parts[0], parts[1], parts[2])
        }
    }

    private fun parseColor(v: String?, fallback: Int): Int {
        if (v == null || v == "none") return fallback
        return try { Color.parseColor(v) } catch (e: Exception) { fallback }
    }

    private fun drawPrim(canvas: Canvas, prim: ColorPrim, regionColor: String, drawOutline: Boolean = true) {
        val a = prim.attrs
        val fillRaw = a["fill"]
        val fillStr = if (fillRaw == "__FILL__") regionColor else fillRaw
        val hasFill = fillStr != null && fillStr != "none"
        val strokeStr = a["stroke"]
        val hasStroke = strokeStr != null && strokeStr != "none"
        val strokeWidth = a["strokeWidth"]?.toFloatOrNull() ?: 0f

        if (prim.type == "Line") {
            if (hasStroke && drawOutline) {
                strokePaint.color = parseColor(strokeStr, Color.BLACK)
                strokePaint.strokeWidth = strokeWidth
                canvas.drawLine(
                    a["x1"]?.toFloatOrNull() ?: 0f, a["y1"]?.toFloatOrNull() ?: 0f,
                    a["x2"]?.toFloatOrNull() ?: 0f, a["y2"]?.toFloatOrNull() ?: 0f, strokePaint
                )
            }
            return
        }

        val path = buildPath(prim) ?: return
        if (a["fillRule"] == "evenodd") path.fillType = Path.FillType.EVEN_ODD

        if (hasFill) {
            fillPaint.color = parseColor(fillStr, Color.WHITE)
            canvas.drawPath(path, fillPaint)
        }
        if (hasStroke && strokeWidth > 0f && drawOutline) {
            strokePaint.color = parseColor(strokeStr, Color.BLACK)
            strokePaint.strokeWidth = strokeWidth
            canvas.drawPath(path, strokePaint)
        }
    }

    private fun buildPath(prim: ColorPrim): Path? {
        val a = prim.attrs
        return when (prim.type) {
            "Path" -> {
                val d = a["d"] ?: return null
                try { PathParser.createPathFromPathData(d) } catch (e: Exception) { null }
            }
            "Circle" -> Path().apply {
                addCircle(a["cx"]?.toFloatOrNull() ?: 0f, a["cy"]?.toFloatOrNull() ?: 0f, a["r"]?.toFloatOrNull() ?: 0f, Path.Direction.CW)
            }
            "Ellipse" -> Path().apply {
                val cx = a["cx"]?.toFloatOrNull() ?: 0f; val cy = a["cy"]?.toFloatOrNull() ?: 0f
                val rx = a["rx"]?.toFloatOrNull() ?: 0f; val ry = a["ry"]?.toFloatOrNull() ?: rx
                addOval(cx - rx, cy - ry, cx + rx, cy + ry, Path.Direction.CW)
            }
            "Rect" -> Path().apply {
                val x = a["x"]?.toFloatOrNull() ?: 0f; val y = a["y"]?.toFloatOrNull() ?: 0f
                val w = a["width"]?.toFloatOrNull() ?: 0f; val h = a["height"]?.toFloatOrNull() ?: 0f
                val rx = a["rx"]?.toFloatOrNull() ?: 0f; val ry = a["ry"]?.toFloatOrNull() ?: rx
                if (rx > 0f || ry > 0f) addRoundRect(x, y, x + w, y + h, rx, ry, Path.Direction.CW)
                else addRect(x, y, x + w, y + h, Path.Direction.CW)
            }
            "Polygon" -> {
                val pts = (a["points"] ?: return null).trim().split(Regex("[ ,]+")).mapNotNull { it.toFloatOrNull() }
                if (pts.size < 4) return null
                Path().apply {
                    moveTo(pts[0], pts[1])
                    var i = 2
                    while (i + 1 < pts.size) { lineTo(pts[i], pts[i + 1]); i += 2 }
                    close()
                }
            }
            else -> null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mode == Mode.FILL) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val pic = picture ?: return false
                val lx = (event.x - offsetX) / scale
                val ly = (event.y - offsetY) / scale

                // Find all matching regions and pick the most specific (smallest area)
                val candidates = pic.regions.filter { r ->
                    lx >= r.left && lx <= r.left + r.width &&
                    ly >= r.top && ly <= r.top + r.height
                }
                val best = candidates.minByOrNull { it.width * it.height }
                if (best != null) {
                    onRegionTap?.invoke(best.id)
                    return true
                }
            }
            return true
        } else {
            // PEN / BRUSH MODE
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currentPath = Path()
                    currentPath.moveTo(x, y)
                    currentPath.lineTo(x + 0.1f, y + 0.1f)
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    currentPath.lineTo(x, y)
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    currentPath.lineTo(x, y)
                    val s = Stroke(currentPath, currentColor, brushWidth)
                    strokes.add(s)
                    onStrokeFinished?.invoke(s)
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
        }
        return super.onTouchEvent(event)
    }
}
