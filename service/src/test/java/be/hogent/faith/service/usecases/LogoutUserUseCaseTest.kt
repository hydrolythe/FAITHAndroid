package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.IAuthManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class LogoutUserUseCaseTest {
    private lateinit var logoutUserUseCase: LogoutUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var authManager: IAuthManager

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        authManager = mockk(relaxed = true)
        logoutUserUseCase = LogoutUserUseCase(authManager, scheduler)
    }

    @Test
    fun LogoutUserUC_Succeeds() {
        // Arrange
        every { authManager.signOut() } returns Completable.complete()

        // Act
        val result = logoutUserUseCase.buildUseCaseObservable(null)

        // Assert
        result.test()
            .assertNoErrors()
    }
}