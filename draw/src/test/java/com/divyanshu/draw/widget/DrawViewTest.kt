package com.divyanshu.draw.widget

import android.content.Context
import android.content.res.Resources
import android.view.MotionEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class DrawViewTest {

    private val context: Context = mockk()

    @Test
    fun drawView_startsWithEmptyActions() {
        // Act
        val view = DrawView(context, mockk())

        // Assert
        assert(view.drawingActions.isEmpty())
    }

    @Test
    fun drawView_addDrawingAction_isAdded() {
        // Arrange
        val view = DrawView(context, mockk())

        // Act
        view.addDrawingAction(mockk())

        // Assert
        assertEquals(1, view.drawingActions.size)
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
        val view = DrawView(context, mockk())
        view.addDrawingAction(mockk())

        // Act
        view.undo()

        // Assert
        assert(view.drawingActions.isEmpty())
    }

    @Test
    fun drawView_addDrawable_addedToActions() {
        // Arrange
        setupContextWithResources()
        val view = spyk(DrawView(context, mockk()))
        every { view.context } returns context
        val fakeResourceID = 0

        // Act
        view.addDrawable(fakeResourceID, 0, 0)

        // Assert
        assertEquals(1, view.drawingActions.size)
    }

    @Test
    fun drawView_addDrawable_canBeUndone() {
        // Arrange
        setupContextWithResources()
        val view = spyk(DrawView(context, mockk()))
        every { view.context } returns context
        val fakeResourceID = 0
        view.addDrawable(fakeResourceID, 0, 0)

        // Act
        view.undo()

        // Assert
        assert(view.drawingActions.isEmpty())
    }

    @Test
    fun drawView_drawLine_addedToActions() {
        // Arrange
        val view = DrawView(context, mockk())

        val downEvent = mockk<MotionEvent>(relaxed = true)
        every { downEvent.action } returns MotionEvent.ACTION_DOWN
        val moveEvent = mockk<MotionEvent>(relaxed = true)
        every { moveEvent.action } returns MotionEvent.ACTION_MOVE
        val upEvent = mockk<MotionEvent>(relaxed = true)
        every { upEvent.action } returns MotionEvent.ACTION_UP

        // Act
        view.onTouchEvent(downEvent)
        view.onTouchEvent(moveEvent)
        view.onTouchEvent(upEvent)

        // Assert
        assertEquals(1, view.drawingActions.size)
    }

    @Test
    fun drawView_drawLine_canBeUndone() {
        // Arrange
        val view = DrawView(context, mockk())

        val downEvent = mockk<MotionEvent>(relaxed = true)
        every { downEvent.action } returns MotionEvent.ACTION_DOWN
        val moveEvent = mockk<MotionEvent>(relaxed = true)
        every { moveEvent.action } returns MotionEvent.ACTION_MOVE
        val upEvent = mockk<MotionEvent>(relaxed = true)
        every { upEvent.action } returns MotionEvent.ACTION_UP

        view.onTouchEvent(downEvent)
        view.onTouchEvent(moveEvent)
        view.onTouchEvent(upEvent)

        // Act
        view.undo()

        // Assert
        assert(view.drawingActions.isEmpty())
    }

    @Test
    fun drawView_redo_undoesLastAction() {
        // Arrange
        val view = DrawView(context, mockk())
        val action = mockk<DrawingAction>()
        view.addDrawingAction(action)
        view.undo()

        // Act
        view.redo()

        // Assert
        assertEquals(action, view.drawingActions.first())
    }

    @Test
    fun drawView_clear_clearsAllActions() {
        // Arrange
        val view = DrawView(context, mockk())
        val action = mockk<DrawingAction>()
        view.addDrawingAction(action)

        // Act
        view.clearCanvas()

        // Assert
        assert(view.drawingActions.isEmpty())
    }

    @Test
    fun drawView_clear_canBeUndone() {
        // Arrange
        val view = DrawView(context, mockk())
        val action = mockk<DrawingAction>()
        view.addDrawingAction(action)
        view.clearCanvas()

        // Act
        view.undo()

        // Assert
        assertEquals(action, view.drawingActions.first())
    }

    private fun setupContextWithResources() {
        val resources = mockk<Resources>()
        every { resources.getDrawable(any()) } returns mockk(relaxed = true)
        every { context.resources } returns resources
    }
}