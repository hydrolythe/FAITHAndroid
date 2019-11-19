package be.hogent.faith.storage

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class StorageRepository(
    private val localStorage: IStorage,
    private val remoteStorage: IStorage
) : IStorage {
    override fun saveEvent(event: Event): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getFile(detail: Detail): Single<File> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getEmotionAvatar(event: Event): Single<File> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}