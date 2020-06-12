package be.hogent.faith.service.usecases.util

import be.hogent.faith.domain.models.goals.ActionStatus
import be.hogent.faith.domain.models.goals.GoalColor
import be.hogent.faith.domain.models.goals.ReachGoalWay
import be.hogent.faith.service.encryption.EncryptedAction
import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.service.encryption.EncryptedSubGoal
import be.hogent.faith.util.factory.DataFactory

object EncryptedGoalFactory {
    fun makeGoal(numberOfSubGoals: Int = 5): EncryptedGoal {
        return EncryptedGoal(
            dateTime = DataFactory.randomDateTime().toString(),
            description = DataFactory.randomString(),
            isCompleted = DataFactory.randomBoolean(),
            uuid = DataFactory.randomUUID().toString(),
            encryptedDEK = DataFactory.randomString(),
            currentPositionAvatar = DataFactory.randomInt(0, 9),
            goalColor = GoalColor.GREEN.name,
            reachGoalWay = ReachGoalWay.Elevator.name,
            subgoals = makeSubGoalList(numberOfSubGoals)
        )
    }

    fun makeGoalList(count: Int = 5): List<EncryptedGoal> {
        val events = mutableListOf<EncryptedGoal>()
        repeat(count) {
            events.add(makeGoal())
        }
        return events
    }

    fun makeSubGoalList(count: Int = 5): List<EncryptedSubGoal> {
        val subgoals = mutableListOf<EncryptedSubGoal>()
        repeat(count) {
            subgoals.add(
                EncryptedSubGoal(
                    it, DataFactory.randomString(10), makeActionList(5)
                )
            )
        }
        return subgoals
    }

    fun makeActionList(count: Int = 5): List<EncryptedAction> {
        val actions = mutableListOf<EncryptedAction>()
        repeat(count) {
            actions.add(EncryptedAction(DataFactory.randomString(10), ActionStatus.ACTIVE.name))
        }
        return actions
    }
}