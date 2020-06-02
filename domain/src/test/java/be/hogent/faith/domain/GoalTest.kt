package be.hogent.faith.domain

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.SubGoal
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GoalTest {

    private lateinit var goal: Goal
    private var subGoalPos1 = SubGoal("1")
    private var subGoalPos2 = SubGoal("2")
    private var subGoalPos3 = SubGoal("3")
    private var subGoalPos4 = SubGoal("4")
    private var subGoalPos5 = SubGoal("5")

    @Before
    fun setUp() {
        goal = Goal(color = 0)
    }

    @Test
    fun `subgoals added and assigned to correct position`() {

        goal.addSubGoalToGoal(subGoalPos1, 0)
        goal.addSubGoalToGoal(subGoalPos2, 2)
        goal.addSubGoalToGoal(subGoalPos3, 6)
        goal.addSubGoalToGoal(subGoalPos4, 8)
        goal.addSubGoalToGoal(subGoalPos5, 9)

        Assert.assertEquals(goal.subGoals.elementAt(6), subGoalPos3)
        Assert.assertEquals(goal.subGoals.elementAt(0), subGoalPos1)
        Assert.assertEquals(goal.subGoals.elementAt(2), subGoalPos2)
        Assert.assertEquals(goal.subGoals.elementAt(8), subGoalPos4)
        Assert.assertEquals(goal.subGoals.elementAt(9), subGoalPos5)
    }

    @Test
    fun `change index subgoal`() {
        goal.addSubGoalToGoal(subGoalPos1, 0)
        goal.addSubGoalToGoal(subGoalPos2, 2)
        goal.addSubGoalToGoal(subGoalPos3, 6)
        goal.addSubGoalToGoal(subGoalPos4, 8)
        goal.addSubGoalToGoal(subGoalPos5, 9)

        goal.changeIndexSubGoal(subGoalPos3, 2)

        Assert.assertEquals(goal.subGoals.elementAt(2), subGoalPos3)
        Assert.assertEquals(goal.subGoals.elementAt(3), subGoalPos2)
    }
}