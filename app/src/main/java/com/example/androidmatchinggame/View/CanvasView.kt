package com.example.androidmatchinggame.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.androidmatchinggame.data.Connection

class CanvasView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private val paint: Paint = Paint()
    private val lines: MutableList<Connection> = mutableListOf()
    private var currentLine: Connection? = null
    private var startPoint: PointF? = null
    private var endPoint: PointF? = null
    private var isDrawing = false

    init {
        paint.color = 0xFF0000FF.toInt()
        paint.strokeWidth = 10f
        paint.isAntiAlias = true
    }

    fun startDrawing(connection: Connection) {
       // currentLine = Connection(startX, startY, startX, startY)
        currentLine = connection
        invalidate()
    }
    fun addLine(startX: Float, startY: Float, endX: Float, endY: Float) {
        if (isPointInBounds(startX, startY) && isPointInBounds(endX, endY)) {
            lines.add(Connection(startX, startY, endX, endY))
            invalidate()
        }
    }
    fun clearLines() {
        lines.clear()
        invalidate()
    }

    fun clearCurrentLine() {
        currentLine = null
        invalidate()
    }


    fun updateCurrentLine(connection: Connection) {
        currentLine?.let {
            currentLine = Connection(it.startX, it.startY, connection.endX, connection.endY)
            invalidate()
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (line in lines) {
            if (isPointInBounds(line.startX, line.startY) &&
                isPointInBounds(line.endX, line.endY)
            ) {
                paint.color = 0xFF00FF00.toInt()
                canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
            }
        }

        currentLine?.let {
            paint.color = 0xFF0000FF.toInt()
            canvas.drawLine(it.startX, it.startY, it.endX, it.endY, paint)
        }
        startPoint?.let { start ->
            endPoint?.let { end ->
                canvas.drawLine(start.x, start.y, end.x, end.y, paint)
            }
        }

    }
    private fun isPointInBounds(x: Float, y: Float): Boolean {
        return x >= 0 && x <= width && y >= 0 && y <= height
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startPoint = PointF(event.x, event.y)
                endPoint = PointF()
                isDrawing = true
            }

            MotionEvent.ACTION_MOVE -> {
                endPoint!!.x = event.x
                endPoint!!.y = event.y
              invalidate()
            }

            MotionEvent.ACTION_UP -> {
                endPoint!!.x = event.x
                endPoint!!.y = event.y
                isDrawing = false
                addLine(startPoint!!.x, startPoint!!.y, endPoint!!.x, endPoint!!.y)
                invalidate()
            }

            else -> {

            }
        }
        return true
    }


}

