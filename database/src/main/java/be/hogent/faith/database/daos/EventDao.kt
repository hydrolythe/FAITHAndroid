package be.hogent.faith.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eventEntity: EventEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(eventEntities: List<EventEntity>): Completable

    @Transaction
    @Query("SELECT * FROM events WHERE userUuid= :userUuid order by dateTime DESC")
    fun getAllEventsWithDetails(userUuid: UUID): Flowable<List<EventWithDetails>>

    @Transaction
    @Query("SELECT * FROM events WHERE uuid= :eventUuid")
    fun getEventWithDetails(eventUuid: UUID): Flowable<EventWithDetails>

    @Delete
    fun delete(eventEntity: EventEntity): Completable
}