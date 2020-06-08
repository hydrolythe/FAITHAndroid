package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.goals.SubGoal

object SubGoalFactory {

    fun makeSubGoal(numberOfActions: Int = 5): SubGoal {
        val subgoal = SubGoal(
            description = DataFactory.randomString()
        )
        repeat(numberOfActions) {
            subgoal.addAction(ActionFactory.makeAction())
        }
        return subgoal
    }


}
