package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Maybe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveEventUseCaseTest {
    private lateinit var saveEventUseCase: SaveEventUseCase
    private lateinit var eventRepository: EventRepository
    private lateinit var storageRepository: StorageRepository

    private lateinit var event: Event
    private lateinit var user: User

    private val eventTitle = "title"

    @Before
    fun setUp() {
        event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)
        user = spyk(
            User(
                DataFactory.randomString(),
                DataFactory.randomString(),
                DataFactory.randomUUID().toString()
            )
        )
        eventRepository = mockk(relaxed = true)
        storageRepository = mockk(relaxed = true)
        saveEventUseCase = SaveEventUseCase(eventRepository, storageRepository, mockk(relaxed = true))
    }

    @Test
    fun execute_normal_eventCorrectlyPassedToRepo() {
        val eventArg = slot<Event>()
        val userArg = slot<User>()
        every { eventRepository.insert(capture(eventArg), capture(userArg)) } returns Maybe.just(event)
        every { storageRepository.deleteFile(any()) } returns true

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()
            .dispose()

        assertEquals(params.event, eventArg.captured)
        assertEquals(params.user, userArg.captured)
    }

    @Test
    fun execute_normal_useCaseCompletes() {
        every { eventRepository.insert(any(), any()) } returns Maybe.just(event)
        every { storageRepository.deleteFile(any()) } returns true

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)

        result.test()
            .assertComplete()
            .dispose()
    }

    @Test
    fun execute_normal_eventIsAddedToUser() {
        every { eventRepository.insert(any(), any()) } returns Maybe.just(event)
        every { storageRepository.deleteFile(any()) } returns true

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()
            .assertNoErrors()
            .assertComplete()

        assertTrue(user.events.isNotEmpty())
    }

    @Test
    fun execute_normal_deletesLocalFiles() {
        every { eventRepository.insert(any(), any()) } returns Maybe.just(event)
        every { storageRepository.deleteFile(any()) } returns true

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()
            .assertNoErrors()
            .assertComplete()

        verify(exactly = 3) { storageRepository.deleteFile(any()) }
    }

    @Test
    fun execute_repoFails_showsError() {
        every { eventRepository.insert(any(), any()) } returns Maybe.error(RuntimeException())
        every { storageRepository.deleteFile(any()) } returns true

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)

        result.test()
            .assertError(RuntimeException::class.java)
            .assertNoValues()
            .dispose()
    }

    @Test
    fun execute_addEventToUserFails_notSavedToRepoAndNoLocalFilesDeleted() {
        every { user.addEvent(any()) } throws RuntimeException()
        every { storageRepository.deleteFile(any()) } returns true

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)

        result.test()
            .assertError(RuntimeException::class.java)
            .assertNoValues()
            .dispose()

        verify { eventRepository.insert(any(), any()) wasNot called }
        verify(exactly = 0) { storageRepository.deleteFile(any()) }
    }
}