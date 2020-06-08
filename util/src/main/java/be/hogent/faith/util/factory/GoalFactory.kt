package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.goals.ColorGoals
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.MAX_AMOUNT_OF_SUBGOALS
import be.hogent.faith.domain.models.goals.ReachGoalWay

object GoalFactory {
    fun makeGoal(
        numberOfSubGoals: Int = 5,
        numberOfActionsPerSubgoal: Int = 3

    ): Goal {
        val goal = Goal(
            dateTime = DataFactory.randomDateTime(),
            description = DataFactory.randomString(),
            uuid = DataFactory.randomUUID(),
            isCompleted = DataFactory.randomBoolean(),
            currentPositionAvatar = DataFactory.randomInt(0, 10),
            color = ColorGoals.red,
            chosenReachGoalWay = ReachGoalWay.Elevator
        )
        repeat(numberOfSubGoals) {
            goal.addSubGoal(
                SubGoalFactory.makeSubGoal(numberOfActionsPerSubgoal),
                DataFactory.randomInt(0, MAX_AMOUNT_OF_SUBGOALS - 1)
            )
        }
        return goal
    }

    fun makeGoalList(count: Int = 5): List<Goal> {
        val goals = mutableListOf<Goal>()
        repeat(count) {
            goals.add(makeGoal())
        }
        return goals
    }
}