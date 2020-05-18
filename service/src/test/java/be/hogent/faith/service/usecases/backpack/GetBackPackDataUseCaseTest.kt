package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.service.encryption.EncryptedDetailsContainer
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.util.EncryptedDetailFactory
import be.hogent.faith.util.factory.DetailFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import be.hogent.faith.service.repositories.IDetailContainerRepository as IDetailContainerRepository1

class GetBackPackDataUseCaseTest {

    private val containerEncryptionService = mockk<IDetailContainerEncryptionService<Backpack>>()
    private val containerRepository = mockk<IDetailContainerRepository1<Backpack>>()
    private lateinit var getBackPackDataUseCase: GetDetailsContainerDataUseCase<Backpack>

    @Before
    fun setUp() {
        getBackPackDataUseCase = GetDetailsContainerDataUseCase<Backpack>(
            containerRepository,
            containerEncryptionService,
            observer = mockk(),
            subscriber = Schedulers.trampoline()
        )
    }

    @Test
    fun `returns unencrypted versions of all details in the repo`() {
        // Arrange
        every { containerRepository.getAll() } returns Flowable.just(
            listOf(
                EncryptedDetailFactory.makeRandomDetail(),
                EncryptedDetailFactory.makeRandomDetail()
            )
        )
        every { containerRepository.getEncryptedContainer() } returns Single.just(
            EncryptedDetailsContainer("", "")
        )
        every {
            containerEncryptionService.decryptData(any(), any())
        } returns Single.defer { Single.just(DetailFactory.makeRandomDetail()) }

        // Act + Assert
        getBackPackDataUseCase.buildUseCaseObservable(GetDetailsContainerDataUseCase.Params())
            .test()
            .assertValue {
                it.size == 2
            }
            .assertComplete()
            .assertNoErrors()
    }
}