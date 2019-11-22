package be.hogent.faith.storage

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface IStorageRepository {
    fun saveEvent(event: Event): Completable

    /**
     *  Gets the file in which a detail is saved.
     *  If not locally available, it will first download it and save locally.
     */
    fun getFile(detail: Detail): Single<File>

    /**
     * Gets the file in which an [event]'s emotion avatar is saved.
     *  If not locally available, it will first download it and save locally.
     */
    fun getEmotionAvatar(event: Event): Single<File>

    fun deleteFile(file: File) : Completable
}