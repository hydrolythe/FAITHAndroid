package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.goals.Goal

object GoalFactory {
    fun makeGoal(numberOfSubGoals: Int = 5): Goal {
        val goal = Goal(color = 1)
        goal.description = DataFactory.randomString(10)
        return goal
    }

    fun makeGoalsList(count: Int = 5): List<Goal> {
        val goals = mutableListOf<Goal>()
        do{
            var counter = count
            goals.add(count, makeGoal())
            counter--
        }while (counter >= 0)
        return goals
    }
}
