package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.goals.Goal

object GoalFactory {
    fun makeGoal(
        numberOfSubGoals: Int = 5,
        numberOfActionsPerSubgoal: Int = 3,
        onlyActive: Boolean = true
    ): Goal {
        val goal = Goal(
            dateTime = DataFactory.randomDateTime(),
            description = DataFactory.randomString(),
            uuid = DataFactory.randomUUID(),
            isCompleted = if (onlyActive) false else DataFactory.randomBoolean()
        )
        repeat(numberOfSubGoals) {
            repeat(numberOfActionsPerSubgoal) {
                // TODO
            }
        }
        return goal
    }

    fun makeGoalList(count: Int = 5, onlyActive: Boolean = true): List<Goal> {
        val goals = mutableListOf<Goal>()
        repeat(count) {
            goals.add(makeGoal(onlyActive = onlyActive))
        }
        return goals
    }
}