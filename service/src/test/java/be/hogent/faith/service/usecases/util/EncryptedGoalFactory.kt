package be.hogent.faith.service.usecases.util

import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.util.factory.DataFactory

object EncryptedGoalFactory {
    fun makeGoal(numberOfSubGoals: Int = 5): EncryptedGoal {
        val goal = EncryptedGoal(
            dateTime = DataFactory.randomDateTime().toString(),
            description = DataFactory.randomString(),
            isCompleted = DataFactory.randomBoolean(),
            uuid = DataFactory.randomUUID(),
            encryptedStreamingDEK = DataFactory.randomString(),
            encryptedDEK = DataFactory.randomString()
        )
        // TODO create subgoals and actions
        return goal
    }

    fun makeGoalList(count: Int = 5): List<EncryptedGoal> {
        val events = mutableListOf<EncryptedGoal>()
        repeat(count) {
            events.add(makeGoal())
        }
        return events
    }
}