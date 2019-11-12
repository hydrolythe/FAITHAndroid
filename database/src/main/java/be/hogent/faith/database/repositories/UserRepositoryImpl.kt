package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseUserRepository
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Flowable

open class UserRepositoryImpl(
    private val userMapper: UserMapper,
    private val firebaseUserRepository: FirebaseUserRepository
) : UserRepository {

    /**
     * deletes the user. item must be the authenticated user
     */
    override fun delete(item: User): Completable {
        return firebaseUserRepository.delete(userMapper.mapToEntity(item))
    }

    /**
     * registers a user. A new user has no events yet
     */
    override fun insert(item: User): Completable {
        return firebaseUserRepository.insert(userMapper.mapToEntity(item))
    }

    /**
     * gets the current user. This must be the uuid of the authenticated user
     */
    override fun get(uuid: String): Flowable<User> {
        return firebaseUserRepository.get(uuid).map { userMapper.mapFromEntity(it) }
    }

    override fun getAll(): Flowable<List<User>> {
        TODO("not implemented") // not possible because no admin user defined in Firebase
    }

    /**
     * Add events to the user
     */
    private fun addEventsToUser(user: User, events: List<Event>): User {
        events.forEach { user.addEvent(it) }
        return user
    }
}