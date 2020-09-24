package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.User

object UserFactory {
    fun makeUser(numberOfEvents: Int = 5, numberOfGoals: Int = 4): User {
        val user = User(
            username = DataFactory.randomString(),
            avatarName = "jongen_gamer_bl",
            uuid = DataFactory.randomUUID().toString()
        )
        repeat(numberOfEvents) {
            user.addEvent(EventFactory.makeEvent())
        }
        repeat(numberOfGoals) {
            user.setGoals(GoalFactory.makeGoalsList(numberOfGoals))
        }
        return user
    }
}