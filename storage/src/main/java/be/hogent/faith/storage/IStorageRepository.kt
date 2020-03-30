package be.hogent.faith.storage

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface IStorageRepository {
    /**
     * Saves the event in storage.
     *
     * Returns the event after it is done so calls can be chained.
     */
    fun saveEvent(event: Event): Single<Event>

    /**
     * Saves all files of an [event] in localStorage : emotion avatar and all detail files
     * If not locally available, it will download it from firebase and save locally.
     */
    fun getEvent(event: Event): Completable

    fun getFile(detail: Detail): Single<File>

    fun saveDetailFileForContainer(detailsContainer: DetailsContainer, detail: Detail): Single<Detail>

    fun deleteDetail(detail: Detail, event: Event): Completable
}
