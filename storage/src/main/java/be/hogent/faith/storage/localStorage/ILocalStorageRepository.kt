package be.hogent.faith.storage.localStorage

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Single

interface ILocalStorageRepository {
    /**
     * moves the files from tempory storage to local storage
     */
    fun saveEvent(event: Event): Single<Event>

    fun isFilePresent(detail: Detail): Boolean
    fun isEmotionAvatarPresent(event: Event): Boolean
}