package com.example.androidmatchinggame.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.androidmatchinggame.data.Connection

class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint()
    private val lines: MutableList<Connection> = mutableListOf()
    private var currentLine: Connection? = null

    init {
        paint.color = 0xFF0000FF.toInt()
        paint.strokeWidth = 10f
    }

    fun addLine(startX: Float, startY: Float, endX: Float, endY: Float) {
        lines.add(Connection(startX, startY, endX, endY))
        invalidate()
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
        currentLine = Connection(connection.startX, connection.startY, connection.endX, connection.endY)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (line in lines) {
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
        }

        currentLine?.let {
            canvas.drawLine(it.startX, it.startY, it.endX, it.endY, paint)
        }
    }

}

