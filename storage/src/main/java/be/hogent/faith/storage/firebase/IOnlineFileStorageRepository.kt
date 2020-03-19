package be.hogent.faith.storage.firebase

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable

interface IOnlineFileStorageRepository {

    fun saveEvent(encryptedEvent: EncryptedEvent): Completable

    /**
     * Ensures a detail's file is locally available.
     */
    fun downloadDetail(detail: Detail, event: Event): Completable

    /**
     * Ensures the emotion avatar for the given [event] is locally available.
     */
    fun downloadEmotionAvatar(event: Event): Completable
}