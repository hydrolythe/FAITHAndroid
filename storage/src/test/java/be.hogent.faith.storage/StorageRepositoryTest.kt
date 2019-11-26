package be.hogent.faith.storage

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.IFireBaseStorageRepository
import be.hogent.faith.storage.localStorage.ILocalStorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test

class StorageRepositoryTest {


    private val localStorage = mockk<ILocalStorageRepository>()
    private val remoteStorage = mockk<IFireBaseStorageRepository>()
    private val storageRepository = StorageRepository(localStorage, remoteStorage)

    @Test
    fun saveEvent_savesFilesinLocalAndRemoteStorageAndReturnsEvent() {
        val eventArg = slot<Event>()
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true )
        every { localStorage.saveEvent(capture(eventArg))} returns Single.just(
                event)
        every { remoteStorage.saveEvent(capture(eventArg))} returns Single.just(event)
        val result = storageRepository.saveEvent(event).test()
        Assert.assertEquals(event, eventArg.captured)
        verify(exactly = 1) { localStorage.saveEvent(event) }
        verify(exactly = 1) { remoteStorage.saveEvent(event) }
        result.assertValue(event)
    }

    @Test
    fun saveEvent_whenSavingInLocalStorageFails_returnsErrorAndDoesntSaveInRemoteStorage() {
        val eventArg = slot<Event>()
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true )
        val error = mockk<Throwable>()
        every { localStorage.saveEvent(capture(eventArg))} returns Single.error(error)
        every { remoteStorage.saveEvent(capture(eventArg))} returns Single.just(event)
        val result = storageRepository.saveEvent(event).test()

        verify(exactly = 1) { localStorage.saveEvent(event) }
        verify(exactly = 0) { remoteStorage.saveEvent(event) }
        result.assertError(error)
    }

    @Test
    fun getEvent_downloadsNothingIfAllFilesAlreadyInLocalStorage() {
        val eventArg = slot<Event>()
        val detailArg = slot<Detail>()
        val event = EventFactory.makeEvent(nbrOfDetails = 1, hasEmotionAvatar = true )
        every { localStorage.isEmotionAvatarPresent(capture(eventArg))} returns true
        every { localStorage.isFilePresent(capture(detailArg))} returns true
        every { remoteStorage.getEmotionAvatar(any())} returns Completable.complete()
        every { remoteStorage.getFile(any())} returns Completable.complete()
        val result = storageRepository.getEvent(event).test()
        Assert.assertEquals(event, eventArg.captured)
        Assert.assertEquals(event.details[0], detailArg.captured)
        verify(exactly = 1) { localStorage.isEmotionAvatarPresent(event)}
        verify(exactly = 1) { localStorage.isFilePresent(event.details[0])}
        verify(exactly = 0) { remoteStorage.getEmotionAvatar(any())}
        verify(exactly = 0) { remoteStorage.getFile(any()) }
        result.assertComplete()
    }

    @Test
    fun getEvent_downloadsFilesIfNotInLocalStorage() {
        val eventArg = slot<Event>()
        val detailArg = slot<Detail>()
        val event = EventFactory.makeEvent(nbrOfDetails = 1, hasEmotionAvatar = true )
        every { localStorage.isEmotionAvatarPresent(capture(eventArg))} returns false
        every { localStorage.isFilePresent(capture(detailArg))} returns false
        every { remoteStorage.getEmotionAvatar(any())} returns Completable.complete()
        every { remoteStorage.getFile(any())} returns Completable.complete()
        val result = storageRepository.getEvent(event).test()
        Assert.assertEquals(event, eventArg.captured)
        Assert.assertEquals(event.details[0], detailArg.captured)
        verify(exactly = 1) { localStorage.isEmotionAvatarPresent(event)}
        verify(exactly = 1) { localStorage.isFilePresent(event.details[0])}
        verify(exactly = 1) { remoteStorage.getEmotionAvatar(event)}
        verify(exactly = 1) { remoteStorage.getFile(event.details[0]) }
        result.assertComplete()
    }

}
