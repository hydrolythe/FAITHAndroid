package be.hogent.faith.service.repositories

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.EncryptedEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import java.util.UUID

interface IEventRepository {

    fun update(encryptedEvent: EncryptedEvent): Completable

    /**
     * Saves the given [event] to the events of the given [user]
     */
    fun insert(encryptedEvent: EncryptedEvent): Completable

    fun get(uuid: UUID): Flowable<EncryptedEvent>

    /**
     * Deletes the  given [event]. The [user] is required to check that the event belongs to
     * the currently logged in user.
     */
    fun delete(event: Event, user: User): Completable

    /**
     * Returns the data of all events associated with the currently logged-in user.
     * This does no ensure that the files for these events are available!
     * Use the [be.hogent.faith.service.usecases.event.MakeEventFilesAvailableUseCase] to ensure this.
     */
    fun getAll(): Flowable<List<EncryptedEvent>>
}