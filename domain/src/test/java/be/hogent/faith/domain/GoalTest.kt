package be.hogent.faith.domain

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.SubGoal
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GoalTest {

    private lateinit var goal : Goal
    private var subGoalPos1 = SubGoal("1")
    private var subGoalPos2 = SubGoal("2")
    private var subGoalPos3 = SubGoal("3")
    private var subGoalPos4 = SubGoal("4")
    private var subGoalPos5 = SubGoal("5")

    @Before
    fun setUp() {
        goal = Goal(description = "desc")
    }

    @Test
    fun `subgoals are correctly added to goal`() {
        goal.addSubGoalToGoal(subGoalPos1)
        goal.addSubGoalToGoal(subGoalPos2)
        goal.addSubGoalToGoal(subGoalPos3)
        goal.addSubGoalToGoal(subGoalPos4)
        goal.addSubGoalToGoal(subGoalPos5)

        Assert.assertEquals(5, goal.subGoals.size)
    }

    @Test
    fun `change position of subgoal`() {
        goal.addSubGoalToGoal(subGoalPos1)
        goal.addSubGoalToGoal(subGoalPos2)
        goal.addSubGoalToGoal(subGoalPos3)
        goal.addSubGoalToGoal(subGoalPos4)
        goal.addSubGoalToGoal(subGoalPos5)

       goal.changeSubGoalPosition(subGoalPos3, 0)

        Assert.assertEquals(goal.subGoals.elementAt(0), subGoalPos3)
        Assert.assertEquals(goal.subGoals.elementAt(1), subGoalPos1)
        Assert.assertEquals(goal.subGoals.elementAt(2), subGoalPos2)
        Assert.assertEquals(goal.subGoals.elementAt(3), subGoalPos4)
        Assert.assertEquals(goal.subGoals.elementAt(4), subGoalPos5)

        Assert.assertEquals(0, subGoalPos3.currentPosition)
        Assert.assertEquals(1, subGoalPos1.currentPosition)
        Assert.assertEquals(2, subGoalPos2.currentPosition)
        Assert.assertEquals(3, subGoalPos4.currentPosition)
        Assert.assertEquals(4, subGoalPos5.currentPosition)
    }
}