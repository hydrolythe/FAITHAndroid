package com.divyanshu.draw.widget

import android.content.Context
import android.view.MotionEvent
import com.divyanshu.draw.widget.tools.CanvasAction
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DrawViewTest {

    private val context: Context = mockk()

    private lateinit var subject: DrawView

    @Before
    fun setUp() {
        subject = DrawView(context, mockk(relaxed = true))
    }

    @Test
    fun drawView_startsWithEmptyActions() {
        // Assert
        assert(subject.canvasActions.isEmpty())
    }

    @Test
    fun drawView_addDrawingAction_isAdded() {
        // Act
        subject.addDrawingAction(mockk())

        // Assert
        assertEquals(1, subject.canvasActions.size)
    }

    @Test
    fun drawView_addDrawingAction_isInvalidated() {
        // Arrange
        val viewSpy = spyk(DrawView(context, mockk()))

        // Act
        viewSpy.addDrawingAction(mockk())

        // Assert
        verify {
            viewSpy.invalidate()
        }
    }

    @Test
    fun drawView_undo_isInvalidated() {
        // Arrange
        val viewSpy = spyk(DrawView(context, mockk()))
        viewSpy.addDrawingAction(mockk())

        // Act
        viewSpy.undo()

        // Assert
        verify {
            viewSpy.invalidate()
        }
    }

    @Test
    fun drawView_undo_removesLastAction() {
        // Arrange
        subject.addDrawingAction(mockk())

        // Act
        subject.undo()

        // Assert
        assert(subject.canvasActions.isEmpty())
    }

    @Test
    fun drawView_drawLine_addedToActions() {
        // Arrange
        val downEvent = mockk<MotionEvent>(relaxed = true)
        every { downEvent.action } returns MotionEvent.ACTION_DOWN
        val moveEvent = mockk<MotionEvent>(relaxed = true)
        every { moveEvent.action } returns MotionEvent.ACTION_MOVE
        val upEvent = mockk<MotionEvent>(relaxed = true)
        every { upEvent.action } returns MotionEvent.ACTION_UP

        // Act
        subject.onTouchEvent(downEvent)
        subject.onTouchEvent(moveEvent)
        subject.onTouchEvent(upEvent)

        // Assert
        assertEquals(1, subject.canvasActions.size)
    }

    @Test
    fun drawView_drawLine_canBeUndone() {
        // Arrange
        val downEvent = mockk<MotionEvent>(relaxed = true)
        every { downEvent.action } returns MotionEvent.ACTION_DOWN
        val moveEvent = mockk<MotionEvent>(relaxed = true)
        every { moveEvent.action } returns MotionEvent.ACTION_MOVE
        val upEvent = mockk<MotionEvent>(relaxed = true)
        every { upEvent.action } returns MotionEvent.ACTION_UP

        subject.onTouchEvent(downEvent)
        subject.onTouchEvent(moveEvent)
        subject.onTouchEvent(upEvent)

        // Act
        subject.undo()

        // Assert
        assert(subject.canvasActions.isEmpty())
    }

    @Test
    fun drawView_redo_undoesLastAction() {
        // Arrange
        val action = mockk<CanvasAction>()
        subject.addDrawingAction(action)
        subject.undo()

        // Act
        subject.redo()

        // Assert
        assertEquals(action, subject.canvasActions.first())
    }

    @Test
    fun drawView_clear_clearsAllActions() {
        // Arrange
        val action = mockk<CanvasAction>()
        subject.addDrawingAction(action)

        // Act
        subject.clearCanvas()

        // Assert
        assert(subject.canvasActions.isEmpty())
    }

    @Test
    fun drawView_clear_canBeUndone() {
        // Arrange
        val action = mockk<CanvasAction>()
        subject.addDrawingAction(action)
        subject.clearCanvas()

        // Act
        subject.undo()

        // Assert
        assertEquals(action, subject.canvasActions.first())
    }
}