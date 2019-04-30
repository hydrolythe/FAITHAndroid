package com.divyanshu.draw.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.LinkedList

// TODO: remove redundant paintOptions
class MyPath(val paintOptions: PaintOptions = PaintOptions()) : Path(), Serializable, DrawingAction {

    private val paint = Paint().also {
        // Same for every path
        it.style = Paint.Style.STROKE
        it.strokeJoin = Paint.Join.ROUND
        it.strokeCap = Paint.Cap.ROUND
    }
    private val actions = LinkedList<Action>()

    override fun drawOn(canvas: Canvas) {
        val paint = paint.also {
            it.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
            it.strokeWidth = paintOptions.strokeWidth
        }
        canvas.drawPath(this, paint)
    }

    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()

        val copiedActions = actions.map { it }
        copiedActions.forEach {
            it.perform(this)
        }
    }

    override fun reset() {
        actions.clear()
        super.reset()
    }

    override fun moveTo(x: Float, y: Float) {
        actions.add(Move(x, y))
        super.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        actions.add(Line(x, y))
        super.lineTo(x, y)
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        actions.add(Quad(x1, y1, x2, y2))
        super.quadTo(x1, y1, x2, y2)
    }
}