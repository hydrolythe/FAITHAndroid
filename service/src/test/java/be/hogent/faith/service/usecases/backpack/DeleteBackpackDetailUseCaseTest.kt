package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Before
import org.junit.Test

class DeleteBackpackDetailUseCaseTest {
    private lateinit var deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>
    private val detailContainerRepo: IDetailContainerRepository<Backpack> = mockk(relaxed = true)
    private val fileStorageRepository: IFileStorageRepository = mockk(relaxed = true)

    private val detail = mockk<AudioDetail>()
    private val backpack = mockk<Backpack>()

    @Before
    fun setUp() {
        deleteBackpackDetailUseCase =
            DeleteDetailsContainerDetailUseCase(
                detailContainerRepo,
                fileStorageRepository,
                mockk()
            )
    }

    @Test
    fun deleteDetailUC_deleteDetailFromStorage() {
        // Arrange
        every { detailContainerRepo.deleteDetail(detail) } returns Completable.complete()
        val params = DeleteDetailsContainerDetailUseCase.Params(detail, backpack)

        // Act
        deleteBackpackDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { detailContainerRepo.deleteDetail(detail) }
    }
}