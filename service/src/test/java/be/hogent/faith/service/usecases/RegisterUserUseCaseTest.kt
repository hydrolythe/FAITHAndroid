package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.UserCollisionException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class RegisterUserUseCaseTest {
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var authManager: AuthManager

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        authManager = mockk(relaxed = true)
        registerUserUseCase = RegisterUserUseCase(authManager, scheduler)
    }

    @Test
    fun registerUserUC_nonExistingUser_Succeeds() {
        // Arrange
        val emailArg = slot<String>()
        val passwordArg = slot<String>()
        every {
            authManager.register(
                capture(emailArg),
                capture(passwordArg)
            )
        } returns Maybe.just("xxxx")

        val params = RegisterUserUseCase.Params("username", "testPassword")

        // Act
        val result = registerUserUseCase.buildUseCaseMaybe(params)

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
    fun registerUserUC_existingUser_Fails() {
        val emailArg = slot<String>()
        val passwordArg = slot<String>()
        every { authManager.register(capture(emailArg), capture(passwordArg)) } returns Maybe.error(
            UserCollisionException(Exception("error"))
        )

        // Act
        val params = RegisterUserUseCase.Params("username", "testPassword")

        // Act
        val result = registerUserUseCase.buildUseCaseMaybe(params)

        result.test()
            .assertError(UserCollisionException::class.java)
            .assertNoValues()

        // Assert
        Assert.assertEquals(params.username + "@faith.be", emailArg.captured)
        Assert.assertEquals(params.password, passwordArg.captured)
    }
}