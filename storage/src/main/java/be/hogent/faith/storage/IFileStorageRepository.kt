package be.hogent.faith.storage

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.domain.models.Event
import io.reactivex.Completable
import io.reactivex.Single

interface IFileStorageRepository {
    /**
     * Saves the event in storage.
     *
     * Returns the event after it is done so calls can be chained.
     */
    fun saveEvent(encryptedEvent: EncryptedEvent): Single<EncryptedEvent>

    /**
     * Saves all files of an [event] in localStorage : emotion avatar and all detail files
     * If not locally available, it will download it from firebase and save locally.
     */
    fun downloadEventFiles(event: Event): Completable
}