package be.hogent.faith.database.user

import be.hogent.faith.domain.models.User
import be.hogent.faith.service.repositories.IUserRepository
import io.reactivex.Completable
import io.reactivex.Flowable

open class UserRepository(
    private val userMapper: UserMapper,
    private val userDatabase: FirebaseUserDatabase
) : IUserRepository {

    /**
     * deletes the user. item must be the authenticated user
     */
    override fun delete(user: User): Completable {
        return userDatabase.delete(userMapper.mapToEntity(user))
    }

    /**
     * Initialize a user.
     */
    override fun initialiseUser(user: User): Completable {
        return userDatabase.initialiseUser(userMapper.mapToEntity(user))
    }

    /**
     * gets the current user. This must be the uid of the authenticated user
     */
    override fun get(uid: String): Flowable<User> {
        return userDatabase.get(uid).map { userMapper.mapFromEntity(it) }
    }
}