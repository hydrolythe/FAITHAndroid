package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseUserRepository
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

open class UserRepositoryImpl(
    // Passing the database is required to run transactions across multiple DAO's.
    // This is required to insert an event together with all its details.
    private val userMapper: UserMapper,
    private val firebaseUserRepository: FirebaseUserRepository
) : UserRepository {

    override fun delete(item: User): Completable {
        TODO("not implemented")
    }

    // een gebruiker heeft bij creatie nog geen events
    override fun insert(item: User): Completable {
        return firebaseUserRepository.insert(userMapper.mapToEntity(item))
    }

    override fun get(uuid: String): Flowable<User> {
        return firebaseUserRepository.get(uuid).map { userMapper.mapFromEntity(it) }
        /*
        val user = userDao.getUser(uuid).map { userMapper.mapFromEntity(it) }
            .doOnNext { Log.d(TAG, "uuid of user fetched from database ${it.uuid}") }
        val eventsWithDetails =
            eventDao.getAllEventsWithDetails(uuid)
                .map { eventWithDetailsMapper.mapFromEntities(it) }
                .doOnNext { Log.d(TAG, "Nbr of events for user with $uuid fetched ${it.size}") }
        return Flowable.combineLatest(
            user,
            eventsWithDetails,
            BiFunction { u, e -> addEventsToUser(u, e) })
         */
    }

    override fun isUsernameUnique(username: String): Single<Boolean> = firebaseUserRepository.isUsernameUnique(username)

    override fun getAll(): Flowable<List<User>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    private fun addEventsToUser(user: User, events: List<Event>): User {
        events.forEach { user.addEvent(it) }
        return user
    }
}