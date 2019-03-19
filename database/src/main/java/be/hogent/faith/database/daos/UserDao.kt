package be.hogent.faith.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.hogent.faith.database.models.UserEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userEntity: UserEntity): Completable

    @Transaction
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flowable<List<UserEntity>>

    @Transaction
    @Query("SELECT * FROM users WHERE uuid= :userUuid")
    fun getUser(userUuid: UUID): Flowable<UserEntity>

    @Delete
    fun delete(userEntity: UserEntity): Completable
}