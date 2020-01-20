package be.hogent.faith.storage.localStorage

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Single

interface ILocalStorageRepository {

    /**
     * Saves all files belonging to an event to local storage
     */
    fun saveEvent(event: Event): Single<Event>

    /**
     * checks if file is present in localStorage
     */
    fun isFilePresent(detail: Detail): Boolean

    /**
     * checks if emotion avatar is present in localStorage
     */
    fun isEmotionAvatarPresent(event: Event): Boolean
}