package be.hogent.faith.domain

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GoalTest {

    private lateinit var goal: Goal

    @Before
    fun setUp() {
        goal = Goal("testDescription")
    }

    @Test
    fun `Goal constructor correctly sets attributes`() {
        assertEquals(goal.description, "testDescription")
    }
}