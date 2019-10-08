package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
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
import java.lang.RuntimeException
import java.util.concurrent.Executor

class GetUserUseCaseTest {
    private lateinit var getUserUC: GetUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: UserRepository
    private lateinit var authManager: AuthManager

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        repository = mockk(relaxed = true)
        authManager =mockk(relaxed=true)
        getUserUC = GetUserUseCase(repository, authManager, scheduler)
    }

    @Test
    fun getUserUC_execute_callsAuthManager() {
        getUserUC.buildUseCaseObservable(null)
        verify { authManager.getLoggedInUser() }
    }
    @Test
    fun getUserUC_execute_callsRepo() {
        //val params = GetUserUseCase.Params(DataFactory.randomUUID())
        getUserUC.buildUseCaseObservable(null)
        verify { repository.get(any()) }
    }

    @Test
    fun getUserUseCase_userPresent_returnsUser() {
        val userUuidArg = slot<String>()
        val userUuid = DataFactory.randomUUID().toString()
        every {authManager.getLoggedInUser()} returns userUuid
        val user = mockk<User>()
        every { repository.get(capture(userUuidArg)) } returns Flowable
            .just(user)
        val result = getUserUC.buildUseCaseObservable(null)
        result.test().assertValues(user)
        Assert.assertEquals(userUuid , userUuidArg.captured)
    }

    @Test
    fun getUserUseCase_noUserPresent_returnsNothing() {
        val userUuidArg = slot<String>()
        val userUuid = DataFactory.randomUUID().toString()
        every {authManager.getLoggedInUser()} returns null
        val result = getUserUC.buildUseCaseObservable(null)
        result.test().assertError(RuntimeException::class.java)
    }
}