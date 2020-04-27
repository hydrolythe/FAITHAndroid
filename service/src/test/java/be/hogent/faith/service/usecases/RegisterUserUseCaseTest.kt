package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.UserCollisionException
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.user.RegisterUserUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.Executor

class RegisterUserUseCaseTest {
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var authManager: IAuthManager
    private lateinit var userRepository: IUserRepository

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        authManager = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        registerUserUseCase =
            RegisterUserUseCase(
                authManager,
                userRepository,
                scheduler
            )
    }

    @Test
    fun registerUserUC_nonExistingUser_Succeeds() {
        // Arrange
        val emailArg = slot<String>()
        val passwordArg = slot<String>()
        val userArg = slot<User>()

        val uid = UUID.randomUUID().toString()

        every {
            authManager.register(
                capture(emailArg),
                capture(passwordArg)
            )
        } returns Maybe.just(uid)

        every { userRepository.insert(capture(userArg)) } returns Completable.complete()

        val params = RegisterUserUseCase.Params("username", "testPassword", "avatarName")

        // Act
        val result = registerUserUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertComplete()

        Assert.assertEquals(params.username + "@faith.be", emailArg.captured)
        Assert.assertEquals(params.password, passwordArg.captured)

        Assert.assertEquals(params.avatar, userArg.captured.avatarName)
        Assert.assertEquals(uid, userArg.captured.uuid)
        Assert.assertEquals(params.username, userArg.captured.username)
    }

    @Test
    fun registerUserUC_existingUser_Fails() {
        val emailArg = slot<String>()
        val passwordArg = slot<String>()
        val userArg = slot<User>()

        every { authManager.register(capture(emailArg), capture(passwordArg)) } returns Maybe.error(
            UserCollisionException(Exception("error"))
        )
        every { userRepository.insert(capture(userArg)) } returns Completable.complete()
        // Act
        val params = RegisterUserUseCase.Params("username", "testPassword", "avatarName")

        // Act
        val result = registerUserUseCase.buildUseCaseObservable(params)

        result.test()
            .assertError(UserCollisionException::class.java)
            .assertNotComplete()

        // Assert
        Assert.assertEquals(params.username + "@faith.be", emailArg.captured)
        Assert.assertEquals(params.password, passwordArg.captured)
        verify(exactly = 0) { userRepository.insert(allAny()) }
    }
}