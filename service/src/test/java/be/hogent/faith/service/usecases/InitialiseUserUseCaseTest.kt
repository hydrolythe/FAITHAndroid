package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.TreasureChest
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.ContainerType
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IUserRepository
import be.hogent.faith.service.usecases.user.InitialiseUserUseCase
import be.hogent.faith.util.factory.UserFactory
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class InitialiseUserUseCaseTest {
    private lateinit var userUseCase: InitialiseUserUseCase
    private lateinit var scheduler: Scheduler
    private val userRepository: IUserRepository = mockk()
    private val authManager: IAuthManager = mockk()

    private val backpackRepository: IDetailContainerRepository<Backpack> = mockk()
    private val cinemaRepository: IDetailContainerRepository<Cinema> = mockk()
    private val treasureChestRepository: IDetailContainerRepository<TreasureChest> = mockk()

    private val backpackEncryptionService: IDetailContainerEncryptionService<Backpack> = mockk()
    private val cinemaEncryptionService: IDetailContainerEncryptionService<Cinema> = mockk()
    private val treasureChestEncryptionService: IDetailContainerEncryptionService<TreasureChest> = mockk()

    private val user: User = UserFactory.makeUser()

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
        every { treasureChestRepository.saveEncryptedContainer(any()) } returns Completable.complete()
        every { treasureChestEncryptionService.createContainer(ContainerType.TREASURECHEST) } returns Single.just(
            mockk()
        )
        scheduler = mockk()
        userUseCase =
            InitialiseUserUseCase(
                userRepository,
                backpackRepository,
                cinemaRepository,
                treasureChestRepository,
                backpackEncryptionService,
                cinemaEncryptionService,
                treasureChestEncryptionService,
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
        val userArg = slot<User>()

        every { userRepository.initialiseUser(capture(userArg)) } returns Completable.complete()

        val params = InitialiseUserUseCase.Params(user)

        // Act
        val result = userUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertComplete()

        Assert.assertEquals(params.user.username, userArg.captured.username)

        Assert.assertEquals(params.user.avatarName, userArg.captured.avatarName)
        Assert.assertEquals(params.user.username, userArg.captured.username)
    }

    @Test
    fun createUserUC_normal_userIsPassedToRepo() {
        // Arrange
        val userArg = slot<User>()
        every { userRepository.initialiseUser(capture(userArg)) } returns Completable.complete()
        every { authManager.register(any(), any()) } returns Maybe.just("uuid")

        val params = InitialiseUserUseCase.Params(user)

        // Act
        userUseCase.buildUseCaseObservable(params)
            .test()

        // Assert
        Assert.assertEquals(params.user.username, userArg.captured.username)
        Assert.assertEquals(params.user.avatarName, userArg.captured.avatarName)
    }
}