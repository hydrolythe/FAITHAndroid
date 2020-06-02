package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.SkyscraperType

object GoalFactory {
    fun makeGoal(skyscraperType: SkyscraperType, numberOfSubGoals: Int = 5): Goal {
        val goal = Goal(
            skyscraperType = skyscraperType
        )
        goal.description = DataFactory.randomString(10)
        return goal
    }

    fun makeGoalsList(count: Int = 5): List<Goal> {
        val types = SkyscraperType.values()
        val goals = mutableListOf<Goal>()
        repeat(count) {
            makeGoal(types.get(it))
        }
        return goals
    }
}
