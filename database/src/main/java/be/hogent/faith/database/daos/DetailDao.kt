package be.hogent.faith.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.Function3
import java.util.UUID

@Dao
abstract class DetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAudio(audio: AudioDetailEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPicture(picture: PictureDetailEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertText(text: TextDetailEntity): Completable

    @Transaction
    open fun insert(detailEntity: DetailEntity): Completable {
        return when (detailEntity) {
            is AudioDetailEntity -> insertAudio(detailEntity)
            is TextDetailEntity -> insertText(detailEntity)
            is PictureDetailEntity -> insertPicture(detailEntity)
            else -> Completable.error(ClassNotFoundException("Encountered Unknown DetailEntity subclass"))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(detailEntities: List<DetailEntity>): Completable

    @Query("SELECT * FROM audioDetails WHERE eventUuid= :eventUuid")
    abstract fun getAudioDetailsForEvent(eventUuid: UUID): Flowable<List<AudioDetailEntity>>

    @Query("SELECT * FROM pictureDetails WHERE eventUuid= :eventUuid")
    abstract fun getPictureDetailsForEvent(eventUuid: UUID): Flowable<List<PictureDetailEntity>>

    @Query("SELECT * FROM textDetails WHERE eventUuid= :eventUuid")
    abstract fun getTextDetailsForEvent(eventUuid: UUID): Flowable<List<TextDetailEntity>>

    @Transaction
    open fun getDetailsForEvent(eventUuid: UUID): Flowable<List<DetailEntity>> {
        return Flowable.combineLatest(
            getAudioDetailsForEvent(eventUuid),
            getPictureDetailsForEvent(eventUuid),
            getTextDetailsForEvent(eventUuid),
            Function3<List<AudioDetailEntity>, List<PictureDetailEntity>, List<TextDetailEntity>, List<DetailEntity>> { audio, pictures, texts ->
                val resultingList = mutableListOf<DetailEntity>()
                resultingList.addAll(audio)
                resultingList.addAll(pictures)
                resultingList.addAll(texts)
                resultingList
            })
    }
}