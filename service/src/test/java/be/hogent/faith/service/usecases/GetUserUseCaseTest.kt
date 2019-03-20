package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Scheduler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.Executor

class GetUserUseCaseTest {
    private lateinit var getUserUC: GetUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        repository = mockk(relaxed = true)
        getUserUC = GetUserUseCase(repository, scheduler)
    }

    @Test
    fun getUserUC_execute_callsRepo() {
        val params = GetUserUseCase.Params(UUID.randomUUID())
        getUserUC.buildUseCaseObservable(params)
        verify { repository.get(any()) }
    }

    @Test
    fun getUserUseCase_userUuidIsPassedToRepo() {
        val userUuidArg = slot<UUID>()
        val userUuid = UUID.randomUUID()
        val params = GetUserUseCase.Params(userUuid)
        every { repository.get(capture(userUuidArg)) } returns Flowable
            .just(createUser())
        getUserUC.buildUseCaseObservable(params)
        Assert.assertEquals(params.userUuid, userUuidArg.captured)
    }

    @Test
    fun getUserUseCase_userPresent_returnsUser() {
        val userUuidArg = slot<UUID>()
        val userUuid = UUID.randomUUID()
        val params = GetUserUseCase.Params(userUuid)
        val user = createUser()
        every { repository.get(capture(userUuidArg)) } returns Flowable
            .just(user)
        val result = getUserUC.buildUseCaseObservable(params)
        result.test().assertValues(user)
    }

    @Test
    fun getUserUseCase_noUserPresent_returnsNothing() {
        val userUuid = UUID.randomUUID()
        val params = GetUserUseCase.Params(userUuid)
        every { repository.get(any()) } returns Flowable.empty()
        val result = getUserUC.buildUseCaseObservable(params)
        result.test().assertNoValues()
    }

    private fun createUser(): User {
        return User("username", "avatar", UUID.randomUUID())
    }
}