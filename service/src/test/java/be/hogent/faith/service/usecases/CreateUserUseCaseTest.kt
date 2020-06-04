package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.ContainerType
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.repositories.UserCollisionException
import be.hogent.faith.service.usecases.user.CreateUserUseCase
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID

class CreateUserUseCaseTest {
    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var scheduler: Scheduler
    private val userRepository: IUserRepository = mockk()
    private val authManager: IAuthManager = mockk()
    private val backpackRepository: IDetailContainerRepository<Backpack> = mockk()
    private val cinemaRepository: IDetailContainerRepository<Cinema> = mockk()
    private val backpackEncryptionService: IDetailContainerEncryptionService<Backpack> = mockk()
    private val cinemaEncryptionService: IDetailContainerEncryptionService<Backpack> = mockk()

    @Before
    fun setUp() {
        every { backpackRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { backpackEncryptionService.createContainer(ContainerType.BACKPACK) } returns Single.just(
            mockk()
        )
        every { cinemaRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { cinemaEncryptionService.createContainer(ContainerType.CINEMA) } returns Single.just(
            mockk()
        )
        scheduler = mockk()
        createUserUseCase =
            CreateUserUseCase(
                authManager,
                userRepository,
                backpackRepository,
                cinemaRepository,
                backpackEncryptionService,
                cinemaEncryptionService,
                scheduler,
                Schedulers.trampoline()
            )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun registerUserUC_nonExistingUser_Succeeds() {
        // Arrange
        val emailArg = slot<String>()
        val passwordArg = slot<String>()
        val userArg = slot<User>()

        val uid = UUID.randomUUID().toString()

        every {
            authManager.register(capture(emailArg), capture(passwordArg))
        } returns Maybe.just(uid)

        every { userRepository.insert(capture(userArg)) } returns Completable.complete()

        val params = CreateUserUseCase.Params("username", "avatarName", "testPassword")

        // Act
        val result = createUserUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertComplete()

        Assert.assertEquals(params.username + "@faith.be", emailArg.captured)
        Assert.assertEquals(params.password, passwordArg.captured)

        Assert.assertEquals(params.avatarName, userArg.captured.avatarName)
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
        val params = CreateUserUseCase.Params("username", "testPassword", "avatarName")

        // Act
        val result = createUserUseCase.buildUseCaseObservable(params)

        result.test()
            .assertError(UserCollisionException::class.java)
            .assertNotComplete()

        // Assert
        Assert.assertEquals(params.username + "@faith.be", emailArg.captured)
        Assert.assertEquals(params.password, passwordArg.captured)
        verify(exactly = 0) { userRepository.insert(allAny()) }
    }

    @Test
    fun createUserUC_normal_userIsPassedToRepo() {
        // Arrange
        val userArg = slot<User>()
        every { userRepository.insert(capture(userArg)) } returns Completable.complete()
        every { authManager.register(any(), any()) } returns Maybe.just("uuid")

        val params = CreateUserUseCase.Params("username", "avatarName", "testPassword")

        // Act
        createUserUseCase.buildUseCaseObservable(params)
            .test()

        // Assert
        Assert.assertEquals(params.username, userArg.captured.username)
        Assert.assertEquals(params.avatarName, userArg.captured.avatarName)
    }
}