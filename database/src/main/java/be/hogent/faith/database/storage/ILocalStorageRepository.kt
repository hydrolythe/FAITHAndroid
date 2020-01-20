package be.hogent.faith.database.storage

import be.hogent.faith.database.encryption.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable

// TODO: maybe split in multiple interfaces depending on what is needed?
// e.g. an encryptedStorageRepository and an "isXAvailableChecker"
interface ILocalStorageRepository {

    /**
     * Saves all files belonging to an event to local storage
     */
    fun saveEvent(event: EncryptedEventEntity): Completable

    /**
     * checks if file is present in localStorage
     */
    fun isFilePresent(detail: Detail): Boolean

    /**
     * checks if emotion avatar is present in localStorage
     */
    fun isEmotionAvatarPresent(event: Event): Boolean
}