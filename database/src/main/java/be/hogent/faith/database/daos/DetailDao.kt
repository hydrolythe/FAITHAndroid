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
internal interface DetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(detailEntity: DetailEntity):Completable

    @Query("SELECT * FROM details WHERE eventUuid= :eventUuid")
    fun getDetailsForEvent(eventUuid: UUID): Flowable<List<DetailEntity>>
}