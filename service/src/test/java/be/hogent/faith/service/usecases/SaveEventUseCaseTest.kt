package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.event.SaveEventUseCase
import be.hogent.faith.service.usecases.util.EncryptedEventFactory
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveEventUseCaseTest {
    private val eventEncryptionService: IEventEncryptionService = mockk(relaxed = true)
    private val filesStorageRepository: IFileStorageRepository = mockk(relaxed = true)
    private val eventRepository: IEventRepository = mockk(relaxed = true)
    private lateinit var saveEventUseCase: SaveEventUseCase

    private lateinit var event: Event
    private lateinit var user: User

    private val encryptedEvent = EncryptedEventFactory.makeEvent()

    @Before
    fun setUp() {
        event = EventFactory.makeEvent(numberOfDetails = 2, hasEmotionAvatar = true)
        user = UserFactory.makeUser(numberOfEvents = 0)
        saveEventUseCase =
            SaveEventUseCase(
                eventEncryptionService,
                filesStorageRepository,
                eventRepository,
                mockk()
            )
        every { eventEncryptionService.encrypt(event) } returns Single.just(encryptedEvent)
        every { filesStorageRepository.saveEventFiles(encryptedEvent) } returns
                Single.just(encryptedEvent)
        every { eventRepository.insert(encryptedEvent) } returns Completable.complete()
    }

    @Test
    fun `After saving the event it should be in the user's list of events`() {
        val params = SaveEventUseCase.Params(event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)

        result.test()
            .dispose()

        assertTrue(user.events.contains(event))
    }

    @Test
    fun `When an error occurs in the EventRepository it returns an Error`() {
        // Arrange
        every { eventRepository.insert(any()) } returns Completable.error(RuntimeException())

        val params = SaveEventUseCase.Params(event, user)

        saveEventUseCase.buildUseCaseObservable(params)
            .test()
            .assertError(RuntimeException::class.java)
    }

    @Test
    fun `When an error occurs in the EventRepository the event is not added to the user's events`() {
        every { eventRepository.insert(any()) } returns Completable.error(RuntimeException())

        val params = SaveEventUseCase.Params(event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()

        assertFalse(user.events.contains(event))
    }
}