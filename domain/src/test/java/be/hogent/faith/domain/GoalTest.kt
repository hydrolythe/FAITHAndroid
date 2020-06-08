package be.hogent.faith.domain

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.GoalColor
import be.hogent.faith.domain.models.goals.SubGoal
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GoalTest {

    private lateinit var goal: Goal
    private var subGoal1 = SubGoal("1")
    private var subGoal2 = SubGoal("2")
    private var subGoal3 = SubGoal("3")
    private var subGoal4 = SubGoal("4")
    private var subGoal5 = SubGoal("5")
    private var subGoal6 = SubGoal("6")
    private var subGoal7 = SubGoal("7")
    private var subGoal8 = SubGoal("8")
    private var subGoal9 = SubGoal("9")
    private var subGoal10 = SubGoal("10")

    @Before
    fun setUp() {
        goal = Goal(goalColor = GoalColor.GREEN)
    }

    @Test
    fun `should assign new subgoals to the given floor`() {
        goal.addSubGoal(subGoal1, 0)
        goal.addSubGoal(subGoal2, 2)

        Assert.assertEquals(goal.subGoals[0], subGoal1)
        Assert.assertEquals(goal.subGoals[2], subGoal2)
    }

    @Test
    fun `should move goal to given subfloor when that floor is empty`() {
        goal.addSubGoal(subGoal1, 0)
        goal.addSubGoal(subGoal2, 2)
        goal.addSubGoal(subGoal3, 3)
        goal.addSubGoal(subGoal4, 8)
        goal.addSubGoal(subGoal5, 9)

        goal.changeFloorSubGoal(subGoal3, 1)

        Assert.assertEquals(goal.subGoals[1], subGoal3)
    }

    // subgoal : 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9 - 10 --> 1 - 2 - 4 - 5 - 6 - 7 - 8 - 3 - 9 - 10
    @Test
    fun `when placing a subgoal an a floor that is not empty, they should switch places`() {
        goal.addSubGoal(subGoal1, 0)
        goal.addSubGoal(subGoal2, 1)
        goal.addSubGoal(subGoal3, 2)

        goal.changeFloorSubGoal(subGoal2, 2)

        Assert.assertEquals(goal.subGoals[0], subGoal1)
        Assert.assertEquals(goal.subGoals[2], subGoal2)
        Assert.assertEquals(goal.subGoals[1], subGoal3)
    }

    @Test(expected = RuntimeException::class)
    fun shouldNotAllowSubGoalOnNonExistingFloor() {
        repeat(11) { goal.addSubGoal(mockk(), 11) }
    }
}