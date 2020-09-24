package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.ContainerType
import be.hogent.faith.service.encryption.EncryptedDetailsContainer
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.util.EncryptedDetailFactory
import be.hogent.faith.util.factory.DetailFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class SaveBackpackDetailUseCaseTest {

    private lateinit var saveBackpackTextDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>
    private val scheduler: Scheduler = mockk()
    private val containerRepository = mockk<IDetailContainerRepository<Backpack>>()
    private val containerEncryptionService =
        mockk<IDetailContainerEncryptionService<Backpack>>()
    private val storageRepository = mockk<IFileStorageRepository>()

    private val user: User = mockk(relaxed = true)

    private val detail = DetailFactory.makeRandomDetail()

    @Before
    fun setUp() {
        saveBackpackTextDetailUseCase =
            SaveDetailsContainerDetailUseCase(
                containerRepository,
                containerEncryptionService,
                storageRepository,
                scheduler,
                Schedulers.trampoline()
            )
    }

    @Test
    fun `when saving a detail to the backpack it is saved to storage`() {
        // Arrange
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)

        val encryptedContainer =
            EncryptedDetailsContainer(ContainerType.BACKPACK, "encryptedDEK", "encryptedSDEK")
        every { containerRepository.getEncryptedContainer() } returns Single.just(encryptedContainer)

        val encryptedDetail = EncryptedDetailFactory.makeRandomDetail()
        every {
            containerEncryptionService.encrypt(detail, encryptedContainer)
        } returns Single.just(encryptedDetail)

        every {
            storageRepository.saveDetailFileWithContainer(encryptedDetail, user.backpack)
        } returns Single.just(encryptedDetail)
        every {
            containerRepository.insertDetail(encryptedDetail, user)
        } returns Completable.complete()

        // Act
        saveBackpackTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()

        // Assert
        verify { storageRepository.saveDetailFileWithContainer(any(), user.backpack) }
    }
}