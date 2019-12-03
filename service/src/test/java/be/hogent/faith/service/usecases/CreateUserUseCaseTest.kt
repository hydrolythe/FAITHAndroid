package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.Executor

class CreateUserUseCaseTest {
    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        repository = mockk(relaxed = true)
        createUserUseCase = CreateUserUseCase(repository, scheduler)
    }

    @Test
    fun createUserUC_normal_newUserReturned() {
        // Arrange
        val uuid = UUID.randomUUID().toString()
        val userArg = slot<User>()
        every { repository.insert(capture(userArg)) } returns Completable.complete()

        val params = CreateUserUseCase.Params("username", "avatarName", uuid)

        // Act
        val result = createUserUseCase.buildUseCaseSingle(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertValue { newUser ->
                newUser.username == "username"
                newUser.avatarName == "avatarName"
                newUser.uuid == uuid
            }
    }

    @Test
    fun createUserUC_normal_userIsPassedToRepo() {
        // Arrange
        val userArg = slot<User>()
        every { repository.insert(capture(userArg)) } returns Completable.complete()

        val params = CreateUserUseCase.Params("username", "testPassword", "avatarName")

        // Act
        createUserUseCase.buildUseCaseSingle(params).test()

        // Assert
        Assert.assertEquals(params.username, userArg.captured.username)
        Assert.assertEquals(params.uuid, userArg.captured.uuid)
        Assert.assertEquals(params.avatarName, userArg.captured.avatarName)
    }

    @Test
    fun createUserUC_userNotAuthenticated_Fails() {
        // Arrange
        val userArg = slot<User>()
        every { repository.insert(capture(userArg)) } returns Completable.error(RuntimeException())

        val params = CreateUserUseCase.Params("username", "testPassword", "avatarName")

        // Act
        createUserUseCase.buildUseCaseSingle(params).test()
            .assertError(RuntimeException::class.java)
    }
}