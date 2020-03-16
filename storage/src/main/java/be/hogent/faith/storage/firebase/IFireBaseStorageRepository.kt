package be.hogent.faith.storage.firebase

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

interface IFireBaseStorageRepository {
    fun saveEvent(event: Event): Single<Event>
    /**
    * Gets a detail's file from firestorage and stores it into the path provided for it by the
    * pathProvider.
    */
    fun makeFileLocallyAvailable(detail: Detail): Completable

    fun makeEmotionAvatarLocallyAvailable(event: Event): Completable

    fun saveBackpackDetail(detail: Detail): Single<Detail>
}