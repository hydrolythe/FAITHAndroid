package be.hogent.faith.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.hogent.faith.database.models.DetailEntity
import java.util.*

@Dao
interface DetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(detailEntity: DetailEntity)

    @Query("SELECT * FROM details WHERE eventUuid= :eventUuid")
    fun getDetailsForEvent(eventUuid: UUID) : LiveData<List<DetailEntity>>

}