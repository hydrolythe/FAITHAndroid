package be.hogent.faith.database.factory

import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.domain.models.User

object UserFactory {
    fun makeUser(numberOfEvents: Int = 5): User {
        val user = User(
            DataFactory.randomUID()
        )
        repeat(numberOfEvents) {
            user.addEvent(EventFactory.makeEvent())
        }
        return user
    }

    fun makeUserEntity(): UserEntity {
        return UserEntity(
            DataFactory.randomUID()
        )
    }
}