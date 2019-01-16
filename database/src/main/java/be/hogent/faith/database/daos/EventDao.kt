package be.hogent.faith.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import java.util.*

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eventEntity: EventEntity)

    @Transaction
    @Query("SELECT * FROM events")
    fun getAllEventsWithDetails(): LiveData<List<EventWithDetails>>

    @Transaction
    @Query("SELECT * FROM events WHERE uuid= :eventUuid")
    fun getEventWithDetails(eventUuid:UUID): LiveData<EventWithDetails>

    @Delete
    fun delete(eventEntity: EventEntity)
}