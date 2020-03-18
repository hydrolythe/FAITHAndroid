package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.IEventRepository
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.util.factory.DataFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Scheduler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID
import java.util.concurrent.Executor

class GetUserUseCaseTest {
    private lateinit var getUserUC: GetUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var userRepository: UserRepository
    private lateinit var eventRepository: IEventRepository
    private lateinit var authManager: AuthManager

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        userRepository = mockk(relaxed = true)
        eventRepository = mockk(relaxed = true)
        authManager = mockk(relaxed = true)
        getUserUC = GetUserUseCase(userRepository, eventRepository, authManager, scheduler)
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
        val events = listOf(Event(LocalDateTime.now(), "title", mockk<File>(), "notes"))
        val user = User("username", "avatar", UUID.randomUUID().toString())
        every { authManager.getLoggedInUserUUID() } returns userUuid
        every { userRepository.get(capture(userUuidArg)) } returns Flowable
            .just(user)
        every { eventRepository.getAll() } returns Flowable
            .just(events)

        val result = getUserUC.buildUseCaseObservable(mockk())

        result.test().assertValue { newUser ->
            newUser.username == "username"
            newUser.events.count() == events.count()
        }
        Assert.assertEquals(userUuid, userUuidArg.captured)
    }

    @Test
    fun getUserUseCase_noUserPresent_returnsNothing() {
        every { authManager.getLoggedInUserUUID() } returns null

        val result = getUserUC.buildUseCaseObservable(mockk())

        result.test().assertError(RuntimeException::class.java)
    }
}