package be.hogent.faith.storage.online

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import io.reactivex.Completable

interface IOnlineFileStorageRepository {

    fun saveEventFiles(encryptedEvent: EncryptedEvent): Completable

    /**
     * Ensures an [event]'s [detail]'s file is locally available.
     */
    fun downloadDetail(detail: Detail, event: Event): Completable

    /**
     * Ensures a [container]'s [detail]'s file is locally available.
     */
    fun downloadDetail(detail: Detail, container: DetailsContainer): Completable

    /**
     * Ensures the emotion avatar for the given [event] is locally available.
     */
    fun downloadEmotionAvatar(event: Event): Completable

    fun saveDetailFiles(encryptedDetail: EncryptedDetail, container: DetailsContainer): Completable
}