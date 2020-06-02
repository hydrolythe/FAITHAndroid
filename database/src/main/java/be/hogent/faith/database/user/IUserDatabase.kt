package be.hogent.faith.database.user

import io.reactivex.Completable
import io.reactivex.Flowable

interface IUserDatabase {
    /**
     * get User for currentUser, observe changes
     */
    fun get(uid: String): Flowable<UserEntity>

    /**
     * Insert user in Firestorage in users/uuid
     */
    fun initialiseUser(item: UserEntity): Completable

    /**
     * delete the user corresponding the currentUser in firestore
     */
    fun delete(item: UserEntity): Completable
}