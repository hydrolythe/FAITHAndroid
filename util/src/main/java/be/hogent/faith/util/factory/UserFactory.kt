package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.User

object UserFactory {
    fun makeUser(numberOfEvents: Int = 5): User {
        val user = User(
            username = DataFactory.randomString(),
            // TODO: add avatar
            avatarName = DataFactory.randomString(),
            uuid = DataFactory.randomUUID()
        )
        repeat(numberOfEvents) {
            user.addEvent(EventFactory.makeEvent())
        }
        return user
    }
}