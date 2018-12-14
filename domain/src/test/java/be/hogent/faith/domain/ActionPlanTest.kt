package be.hogent.faith.domain

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ActionPlanTest {

    private lateinit var actionPlan: ActionPlan

    @Before
    fun setUp() {
        actionPlan = ActionPlan()
    }

    @Test
    fun `ActionPlan constructor starts with empty list of goals`() {
        assert(actionPlan.goals.isEmpty())
    }

    @Test
    fun `ActionPlan addGoal correctly adds the goal`() {
        val goal = mockk<Goal>()

        actionPlan.addGoal(goal)

        assertEquals(1, actionPlan.goals.size)
        assertEquals(goal, actionPlan.goals.first())
    }

    @Test
    fun `ActionPlan getGoals returns all goals`() {
        for (i in 1..10) {
            actionPlan.addGoal(mockk())
        }

        assertEquals(10, actionPlan.goals.size)
    }
}