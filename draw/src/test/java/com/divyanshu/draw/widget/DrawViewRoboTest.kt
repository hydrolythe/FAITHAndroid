package com.divyanshu.draw.widget

import androidx.test.platform.app.InstrumentationRegistry
import com.divyanshu.draw.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DrawViewRoboTest {

    private lateinit var subject: DrawView
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val attributeSet = Robolectric.buildAttributeSet().build()

    @Before
    fun setUp() {
        subject = DrawView(context, attributeSet)
    }

    @Test
    fun drawView_addDrawable_addedToActions() {
        // Act
        subject.addDrawable(R.drawable.circle_black, 0, 0)

        // Assert
        assertEquals(1, subject.canvasActions.size)
    }

    @Test
    fun drawView_addDrawable_canBeUndone() {
        // Arrange
        subject.addDrawable(R.drawable.circle_black, 0, 0)

        // Act
        subject.undo()

        // Assert
        assert(subject.canvasActions.isEmpty())
    }

    @Test
    fun drawView_pickDrawingTool_multipleTimes_OK() {
        // Act
        subject.pickDrawingTool()
        subject.pickDrawingTool()
    }

    @Test
    fun drawView_changeToolImmediately_noActionsAdded() {
        // Act
        subject.pickDrawingTool()
        subject.pickTextTool()
        subject.pickEraserTool()

        // Assert
        assert(subject.canvasActions.isEmpty())
    }

    @Test
    fun drawView_pickEraserTool_multipleTimes_OK() {
        // Act
        subject.pickEraserTool()
        subject.pickEraserTool()
    }

    @Test
    fun drawView_pickTextTool_multipleTimes_OK() {
        // Act
        subject.pickTextTool()
        subject.pickTextTool()
    }
}