package be.hogent.faith.storage.local

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail

/**
 * Allows access to the devices temporary storage (i.e. context.cachedir)
 */
interface ITemporaryFileStorageRepository {

    /**
     * Checks if the file(s) belonging to the [event] are in temporary storage.
     */
    fun isFilePresent(detail: Detail, event: Event): Boolean

    /**
     * Checks if the file(s) belonging to the [container] are in temporary storage.
     */
    fun isFilePresent(detail: Detail, container: DetailsContainer): Boolean

    /**st
     * Checks if the emotion avatar of the [event] is in temporary storage.
     */
    fun isEmotionAvatarPresent(event: Event): Boolean
}