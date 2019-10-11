package com.divyanshu.draw.widget.tools.drawing

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.divyanshu.draw.widget.Action
import com.divyanshu.draw.widget.Line
import com.divyanshu.draw.widget.Move
import com.divyanshu.draw.widget.Quad
import com.divyanshu.draw.widget.tools.CanvasAction
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.*

// TODO: remove redundant paintOptions
class PathAction(private val paint: Paint) : Path(), Serializable,
    CanvasAction {

    init {
        paint.apply {
            // Same for every path
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val actions = LinkedList<Action>()

    override fun drawOn(canvas: Canvas) {
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