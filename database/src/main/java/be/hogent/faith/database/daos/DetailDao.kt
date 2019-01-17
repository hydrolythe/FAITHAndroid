package be.hogent.faith.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.hogent.faith.database.models.DetailEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

@Dao
interface DetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(detailEntity: DetailEntity)

    @Query("SELECT * FROM details WHERE eventUuid= :eventUuid")
    fun getDetailsForEvent(eventUuid: UUID): Flowable<List<DetailEntity>>
}