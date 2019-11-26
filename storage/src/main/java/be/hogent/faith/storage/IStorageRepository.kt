package be.hogent.faith.storage

import be.hogent.faith.domain.models.Event
import io.reactivex.Completable
import io.reactivex.Single

interface IStorageRepository {
    /**
     * saves all files of an [event] in Firebase: emotion avatar and all detail files
     */
    fun saveEvent(event: Event): Single<Event>

    /**
     * Saves all files of an [event] in localStorage : emotion avatar and all detail files
     * If not locally available, it will download it from firebase and save locally.
     */
    fun getEvent(event: Event): Completable
}