package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.GoalColor

object GoalFactory {
    fun makeGoal(
        numberOfSubGoals: Int = 5,
        numberOfActionsPerSubgoal: Int = 3
    ): Goal {
        val goal = Goal(
            GoalColor.GREEN).also {
            it.description = DataFactory.randomString() }
        repeat(numberOfSubGoals) {
            goal.addSubGoal(
                SubGoalFactory.makeSubGoal(numberOfActionsPerSubgoal),
                DataFactory.randomInt(0, numberOfSubGoals)
            )
        }
        return goal
    }

    fun makeGoalsList(count: Int = 5): List<Goal> {
        val goals = mutableListOf<Goal>()
        repeat(count) {
            goals.add(makeGoal())
        }
        return goals
    }
}
