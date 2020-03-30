package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.IAuthManager
import be.hogent.faith.domain.repository.SignInException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class LoginUserUseCaseTest {
    private lateinit var loginUserUseCase: LoginUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var authManager: IAuthManager

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        authManager = mockk(relaxed = true)
        loginUserUseCase = LoginUserUseCase(authManager, scheduler)
    }

    @Test
    fun loginUserUC_existingUser_IsLoggedIn() {
        // Arrange
        val emailArg = slot<String>()
        val passwordArg = slot<String>()
        every {
            authManager.signIn(
                capture(emailArg),
                capture(passwordArg)
            )
        } returns Maybe.just("xxxx")

        val params = LoginUserUseCase.Params("username", "testPassword")

        // Act
        val result = loginUserUseCase.buildUseCaseMaybe(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertValue { uuid ->
                uuid == "xxxx"
            }

        Assert.assertEquals(params.username + "@faith.be", emailArg.captured)
        Assert.assertEquals(params.password, passwordArg.captured)
    }

    @Test
    fun createUserUC_nonExistingUser_IsNotLoggedIn() {
        val emailArg = slot<String>()
        val passwordArg = slot<String>()
        every { authManager.signIn(capture(emailArg), capture(passwordArg)) } returns Maybe.error(
            SignInException(mockk())
        )

        // Act
        val params = LoginUserUseCase.Params("username", "testPassword")

        // Act
        val result = loginUserUseCase.buildUseCaseMaybe(params)

        result.test()
            .assertError(SignInException::class.java)
            .assertNoValues()

        // Assert
        Assert.assertEquals(params.username + "@faith.be", emailArg.captured)
        Assert.assertEquals(params.password, passwordArg.captured)
    }
}