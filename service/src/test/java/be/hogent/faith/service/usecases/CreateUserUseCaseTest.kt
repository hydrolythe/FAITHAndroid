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
        val userArg = slot<User>()
        every { repository.insert(capture(userArg)) } returns Completable.complete()

        val params = CreateUserUseCase.Params("username", "avatarName")

        // Act
        val result = createUserUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertValue { newUser ->
                newUser.username == "username"
            }

        Assert.assertEquals(params.username, userArg.captured.username)
        // TODO: add Avatar test
//        Assert.assertEquals(params.avatarName, userArg.captured.avatarName)
    }

    @Test
    fun createUserUC_normal_userIsPassedToRepo() {
        // Arrange
        val userArg = slot<User>()
        every { repository.insert(capture(userArg)) } returns Completable.complete()

        val params = CreateUserUseCase.Params("username", "avatarName")

        // Act
        val result = createUserUseCase.buildUseCaseObservable(params)

        // Assert
        Assert.assertEquals(params.username, userArg.captured.username)
        // TODO: add Avatar test
//        Assert.assertEquals(params.avatarName, userArg.captured.avatarName)
    }
}