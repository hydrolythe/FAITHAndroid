package be.hogent.faith.database.repositories

import android.util.Log
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.daos.UserDao
import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.mappers.EventWithDetailsMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import java.util.UUID

private const val TAG = "UserRepositoryImpl"

open class UserRepositoryImpl(
    // Passing the database is required to run transactions across multiple DAO's.
    // This is required to insert an event together with all its details.
    private val database: EntityDatabase,
    private val userMapper: UserMapper,
    private val eventWithDetailsMapper: EventWithDetailsMapper
) : UserRepository {
    // Although the DAO's could be injected, that would mean we inject both the
    // database and the DAO's, while the latter are made from the former, and are properties of it.
    // It's cleaner to just inject the database and request the daos during construction.
    private val userDao: UserDao = database.userDao()
    private val eventDao: EventDao = database.eventDao()
    private val detailDao: DetailDao = database.detailDao()

    override fun delete(item: User): Completable {
        return userDao.delete(userMapper.mapToEntity(item))
    }

    // een gebruiker heeft bij creatie nog geen events
    override fun insert(item: User): Completable {
        return try {
            userDao.insert(userMapper.mapToEntity(item))
            Completable.complete()
        } catch (e: Exception) {
            Completable.error(e)
        }
    }

    override fun get(uuid: UUID): Flowable<User> {
        val user = userDao.getUser(uuid).map { userMapper.mapFromEntity(it) }
            .doOnNext { Log.d(TAG, "uuid of user fetched from database ${it.uuid.toString()}") }
        val eventsWithDetails =
            eventDao.getAllEventsWithDetails(uuid).map { eventWithDetailsMapper.mapFromEntities(it) }
                .doOnNext { Log.d(TAG, "Nbr of events for user with ${uuid} fetched ${it.size.toString()}") }
        return Flowable.combineLatest(user, eventsWithDetails, BiFunction { u, e -> addEventsToUser(u, e) })
    }

    override fun getAll(): Flowable<List<User>> {
        val users = userDao.getAllUsers()
        return users.map { userMapper.mapFromEntities(it) }
    }

    private fun addEventsToUser(user: User, events: List<Event>): User {
        events.forEach { user.addEvent(it) }
        Log.d(TAG, user.events.size.toString())
        return user
    }
}