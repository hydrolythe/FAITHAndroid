package be.hogent.faith.storage

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.IOnlineStorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test

class StorageRepositoryTest {

    private val localStorage = mockk<be.hogent.faith.database.storage.ILocalStorageRepository>()
    private val remoteStorage = mockk<IOnlineStorageRepository>()
    private val storageRepository = StorageRepository(localStorage, remoteStorage, mockk())

    @Test
    fun saveEvent_eventCorrectlyPassedToLocalStorage() {
        val eventArg = slot<Event>()
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)
        every { localStorage.saveEvent(capture(eventArg)) } returns Single.just(
            event)
        every { remoteStorage.saveEvent(any()) } returns Single.just(event)
        val result = storageRepository.saveEvent(event).test()
        Assert.assertEquals(event, eventArg.captured)
        result.assertValue(event)
    }

    @Test
    fun saveEvent_eventCorrectlyPassedToRemoteStorage() {
        val eventArg = slot<Event>()
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)
        every { localStorage.saveEvent(any()) } returns Single.just(
            event)
        every { remoteStorage.saveEvent(capture(eventArg)) } returns Single.just(event)
        val result = storageRepository.saveEvent(event).test()
        Assert.assertEquals(event, eventArg.captured)
        result.assertValue(event)
    }

    @Test
    fun saveEvent_savesFilesinLocalAndRemoteStorageAndReturnsEvent() {
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)
        every { localStorage.saveEvent(any()) } returns Single.just(
                event)
        every { remoteStorage.saveEvent(any()) } returns Single.just(event)
        val result = storageRepository.saveEvent(event).test()
        verify(exactly = 1) { localStorage.saveEvent(event) }
        verify(exactly = 1) { remoteStorage.saveEvent(event) }
        result.assertValue(event)
    }

    @Test
    fun saveEvent_whenSavingInLocalStorageFails_returnsErrorAndDoesntSaveInRemoteStorage() {
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)
        every { localStorage.saveEvent(any()) } returns Single.error(RuntimeException())
        every { remoteStorage.saveEvent(any()) } returns Single.just(event)
        val result = storageRepository.saveEvent(event).test()

        verify(exactly = 1) { localStorage.saveEvent(event) }
        verify { remoteStorage.saveEvent(any()) wasNot called }

        result.assertError(RuntimeException::class.java)
    }

    @Test
    fun getEvent_passesEventAndDetailCorrectlyToLocalAndRemoteStorage() {
        val eventArg = slot<Event>()
        val detailArg = slot<Detail>()
        val event = EventFactory.makeEvent(nbrOfDetails = 1, hasEmotionAvatar = true)
        every { localStorage.isEmotionAvatarPresent(capture(eventArg)) } returns true
        every { localStorage.isFilePresent(capture(detailArg)) } returns true
        every { remoteStorage.downloadEmotionAvatar(capture(eventArg)) } returns Completable.complete()
        every { remoteStorage.downloadDetail(capture(detailArg)) } returns Completable.complete()
        val result = storageRepository.downloadEventFiles(event).test()
        Assert.assertEquals(event, eventArg.captured)
        Assert.assertEquals(event.details[0], detailArg.captured)
        result.assertComplete()
    }

    @Test
    fun getEvent_downloadsNothingIfAllFilesAlreadyInLocalStorage() {
        val event = EventFactory.makeEvent(nbrOfDetails = 1, hasEmotionAvatar = true)
        every { localStorage.isEmotionAvatarPresent(any()) } returns true
        every { localStorage.isFilePresent(any()) } returns true
        every { remoteStorage.downloadEmotionAvatar(any()) } returns Completable.complete()
        every { remoteStorage.downloadDetail(any()) } returns Completable.complete()
        val result = storageRepository.downloadEventFiles(event).test()

        verify(exactly = 1) { localStorage.isEmotionAvatarPresent(event) }
        verify(exactly = 1) { localStorage.isFilePresent(event.details[0]) }
        verify { remoteStorage.downloadEmotionAvatar(any()) wasNot called }
        verify { remoteStorage.downloadDetail(any()) wasNot called }
        result.assertComplete()
    }

    @Test
    fun getEvent_downloadsFilesIfNotInLocalStorage() {
        val event = EventFactory.makeEvent(nbrOfDetails = 1, hasEmotionAvatar = true)
        every { localStorage.isEmotionAvatarPresent(any()) } returns false
        every { localStorage.isFilePresent(any()) } returns false
        every { remoteStorage.downloadEmotionAvatar(any()) } returns Completable.complete()
        every { remoteStorage.downloadDetail(any()) } returns Completable.complete()
        val result = storageRepository.downloadEventFiles(event).test()
        verify(exactly = 1) { localStorage.isEmotionAvatarPresent(event) }
        verify(exactly = 1) { localStorage.isFilePresent(event.details[0]) }
        verify(exactly = 1) { remoteStorage.downloadEmotionAvatar(event) }
        verify(exactly = 1) { remoteStorage.downloadDetail(event.details[0]) }
        result.assertComplete()
    }
}
