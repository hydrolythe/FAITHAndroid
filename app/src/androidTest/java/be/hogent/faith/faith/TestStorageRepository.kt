package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class TestStorageRepository : IStorageRepository {
    override fun saveEvent(event: Event): Single<Event> {
        return Single.just(EventFactory.makeEvent())
    }

    override fun getEvent(event: Event): Completable {
        return Completable.complete()
    }

    override fun getFile(detail: Detail): Single<File> {
        return Single.just(DataFactory.randomFile())
    }
}