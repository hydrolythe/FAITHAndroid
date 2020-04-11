package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.user.GetUserUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Flowable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID

class GetUserUseCaseTest {
    private lateinit var getUserUC: GetUserUseCase
    private val eventEncryptionService = mockk<IEventEncryptionService>()

    @Before
    fun setUp() {
        getUserUC = GetUserUseCase(
            userRepository = mockk(relaxed = true),
            eventRepository = mockk(relaxed = true),
            eventEncryptionService = eventEncryptionService,
            authManager = mockk(relaxed = true),
            observeScheduler = mockk()
        )
    }

    @Test
    fun getUserUC_execute_callsAuthManager() {
        getUserUC.buildUseCaseObservable(mockk())
        verify { mockk<IAuthManager>(relaxed = true).getLoggedInUserUUID() }
    }

    @Test
    fun getUserUC_execute_callsUserRepo() {
        getUserUC.buildUseCaseObservable(mockk())
        verify { mockk<IUserRepository>(relaxed = true).get(any()) }
    }

    @Test
    fun getUserUC_execute_callsEventRepo() {
        getUserUC.buildUseCaseObservable(mockk())
        verify { mockk<IEventRepository>(relaxed = true).getAll() }
    }

    @Test
    fun getUserUseCase_userPresent_returnsUserWithEvents() {
        val userUuidArg = slot<String>()
        val userUuid = DataFactory.randomUUID().toString()
        val events =
            listOf(
                EncryptedEvent(
                    dateTime = "encryptedDate",
                    title = "title",
                    emotionAvatar = mockk(),
                    notes = "notes",
                    uuid = mockk(),
                    details = mockk(),
                    encryptedDEK = mockk(),
                    encryptedStreamingDEK = mockk()
                )
            )
        val user = User("username", "avatar", UUID.randomUUID().toString())
        every { mockk<IAuthManager>(relaxed = true).getLoggedInUserUUID() } returns userUuid
        every { mockk<IUserRepository>(relaxed = true).get(capture(userUuidArg)) } returns Flowable.just(
            user
        )
        every { mockk<IEventRepository>(relaxed = true).getAll() } returns Flowable.just(
            events
        )

        val result = getUserUC.buildUseCaseObservable(mockk())

        result.test().assertValue { newUser ->
            newUser.username == "username"
            newUser.events.count() == events.count()
        }
        Assert.assertEquals(userUuid, userUuidArg.captured)
    }

    @Test
    fun getUserUseCase_noUserPresent_returnsNothing() {
        every { mockk<IAuthManager>(relaxed = true).getLoggedInUserUUID() } returns null

        val result = getUserUC.buildUseCaseObservable(mockk())

        result.test().assertError(RuntimeException::class.java)
    }
}