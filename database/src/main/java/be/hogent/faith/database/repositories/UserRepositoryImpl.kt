package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.UserDatabase
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Observable

open class UserRepositoryImpl(
    private val userMapper: UserMapper,
    private val userDatabase: UserDatabase
) : UserRepository {

    /**
     * deletes the user. item must be the authenticated user
     */
    override fun delete(item: User): Completable {
        return userDatabase.delete(userMapper.mapToEntity(item))
    }

    /**
     * registers a user. A new user has no events yet
     */
    override fun insert(item: User): Completable {
        return userDatabase.insert(userMapper.mapToEntity(item))
    }

    /**
     * gets the current user. This must be the uid of the authenticated user
     */
    override fun get(uid: String): Observable<User> {
        return userDatabase.get(uid).map { userMapper.mapFromEntity(it) }.toObservable()
    }
}