package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.reactivex.Completable
import io.reactivex.Single

class TestStorageRepository : IStorageRepository {
    override fun saveEvent(event: Event): Single<Event> {
        return Single.just(EventFactory.makeEvent())
    }

    override fun getEvent(event: Event): Completable {
        return Completable.complete()
    }


}