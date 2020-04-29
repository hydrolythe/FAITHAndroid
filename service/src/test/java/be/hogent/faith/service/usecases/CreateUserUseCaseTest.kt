package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.user.CreateUserUseCase
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.Executor

class CreateUserUseCaseTest {
    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: IUserRepository
    private val backpackRepository: IDetailContainerRepository<Backpack> = mockk()
    private val cinemaRepository: IDetailContainerRepository<Cinema> = mockk()
    private val backpackEncryptionService: IDetailContainerEncryptionService<Backpack> = mockk()
    private val cinemaEncryptionService: IDetailContainerEncryptionService<Backpack> = mockk()

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        repository = mockk(relaxed = true)
        createUserUseCase =
            CreateUserUseCase(
                repository,
                backpackRepository,
                cinemaRepository,
                backpackEncryptionService,
                cinemaEncryptionService,
                scheduler
            )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun createUserUC_normal_newUserReturned() {
        // Arrange
        val uuid = UUID.randomUUID().toString()
        val userArg = slot<User>()
        every { repository.insert(capture(userArg)) } returns Completable.complete()
        every { backpackRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { backpackEncryptionService.createContainer() } returns Single.just(mockk())
        every { cinemaRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { cinemaEncryptionService.createContainer() } returns Single.just(mockk())

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
        every { backpackRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { backpackEncryptionService.createContainer() } returns Single.just(mockk())
        every { cinemaRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { cinemaEncryptionService.createContainer() } returns Single.just(mockk())

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
        every { backpackRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { backpackEncryptionService.createContainer() } returns Single.just(mockk())
        every { cinemaRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { cinemaEncryptionService.createContainer() } returns Single.just(mockk())

        val params = CreateUserUseCase.Params("username", "testPassword", "avatarName")

        // Act
        createUserUseCase.buildUseCaseSingle(params).test()
            .assertError(RuntimeException::class.java)
    }
}