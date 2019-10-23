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
     * deletes the currentUser
     */
    override fun delete(item: User): Completable {
        return firebaseUserRepository.delete()
    }

    // een gebruiker heeft bij creatie nog geen events
    override fun insert(item: User): Completable {
        return firebaseUserRepository.insert(userMapper.mapToEntity(item))
    }

    override fun get(uuid: String): Flowable<User> {
        return firebaseUserRepository.get().map { userMapper.mapFromEntity(it) }
    }

    override fun getAll(): Flowable<List<User>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    private fun addEventsToUser(user: User, events: List<Event>): User {
        events.forEach { user.addEvent(it) }
        return user
    }
}