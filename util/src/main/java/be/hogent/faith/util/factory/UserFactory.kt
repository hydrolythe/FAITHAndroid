package be.hogent.faith.database.factory

import be.hogent.faith.domain.models.User
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory

object UserFactory {
    fun makeUser(numberOfEvents: Int = 5): User {
        val user = User(DataFactory.randomString(), DataFactory.randomString(), DataFactory.randomUUID())
        repeat(numberOfEvents) {
            user.addEvent(EventFactory.makeEvent())
        }
        return user
    }
}