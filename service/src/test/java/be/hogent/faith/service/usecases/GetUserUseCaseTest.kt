package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.user.GetUserUseCase
import be.hogent.faith.service.usecases.util.EncryptedEventFactory
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID

class GetUserUseCaseTest {
    private lateinit var getUserUC: GetUserUseCase
    private val eventEncryptionService = mockk<IEventEncryptionService>()
    private val userRepository = mockk<IUserRepository>(relaxed = true)
    private val eventRepository = mockk<IEventRepository>(relaxed = true)
    private val authManager = mockk<IAuthManager>(relaxed = true)

    @Before
    fun setUp() {
        getUserUC = GetUserUseCase(
            userRepository,
            eventRepository,
            eventEncryptionService,
            authManager,
            mockk<Scheduler>(),
            Schedulers.trampoline()
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getUserUC_execute_callsAuthManager() {
        getUserUC.buildUseCaseObservable(mockk())
        verify { authManager.getLoggedInUserUUID() }
    }

    @Test
    fun getUserUC_execute_callsUserRepo() {
        getUserUC.buildUseCaseObservable(mockk())
        verify { userRepository.get(any()) }
    }

    @Test
    fun getUserUC_execute_callsEventRepo() {
        getUserUC.buildUseCaseObservable(mockk())
        verify { eventRepository.getAll() }
    }

    @Test
    fun getUserUseCase_userPresent_returnsUserWithEvents() {
        val userUuidArg = slot<String>()
        val userUuid = DataFactory.randomUUID().toString()
        val encryptedEvents = EncryptedEventFactory.makeEventList(5)
        val user = User("username", "avatar", UUID.randomUUID().toString())

        every { authManager.getLoggedInUserUUID() } returns userUuid
        every { userRepository.get(capture(userUuidArg)) } returns Flowable.just(user)
        every { eventRepository.getAll() } returns Flowable.just(encryptedEvents)
        every { eventEncryptionService.decryptData(any()) } returns Single.defer { // Defer to ensure a new event is made with each call
            Single.just(EventFactory.makeEvent())
        }

        getUserUC.buildUseCaseObservable(mockk())
            .test()
            .assertValue { newUser ->
                newUser.username == "username"
                newUser.events.count() == encryptedEvents.count()
            }
        Assert.assertEquals(userUuid, userUuidArg.captured)
    }

    @Test
    fun getUserUseCase_noUserPresent_returnsNothing() {
        every { authManager.getLoggedInUserUUID() } returns null

        getUserUC.buildUseCaseObservable(mockk())
            .test()
            .assertError(RuntimeException::class.java)
    }
}