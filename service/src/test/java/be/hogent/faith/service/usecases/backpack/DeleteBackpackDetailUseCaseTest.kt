package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class DeleteBackpackDetailUseCaseTest {
    private lateinit var deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>
    private val detailContainerRepo: IDetailContainerRepository<Backpack> = mockk(relaxed = true)
    private val fileStorageRepository: IFileStorageRepository = mockk(relaxed = true)

    private val detail = mockk<AudioDetail>()
    private lateinit var backpack: Backpack

    @Before
    fun setUp() {
        backpack = Backpack()
        backpack.addDetail(detail)

        every { detailContainerRepo.deleteDetail(detail) } returns Completable.complete()
        every { fileStorageRepository.deleteFiles(detail, backpack) } returns Completable.complete()

        deleteBackpackDetailUseCase =
            DeleteDetailsContainerDetailUseCase(
                detailContainerRepo,
                fileStorageRepository,
                mockk()
            )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `deleting a detail from a container removes its files`() {
        // Arrange
        val params = DeleteDetailsContainerDetailUseCase.Params(detail, backpack)

        // Act
        deleteBackpackDetailUseCase.buildUseCaseObservable(params)
            .test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { fileStorageRepository.deleteFiles(detail, backpack) }
    }

    @Test
    fun `deleting a detail from a container removes it from the database`() {
        // Arrange
        val params = DeleteDetailsContainerDetailUseCase.Params(detail, backpack)

        // Act
        deleteBackpackDetailUseCase.buildUseCaseObservable(params)
            .test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { detailContainerRepo.deleteDetail(detail) }
    }

    @Test
    fun `deleting a detail from a container removes it from the container`() {
        // Arrange
        val params = DeleteDetailsContainerDetailUseCase.Params(detail, backpack)

        // Act
        deleteBackpackDetailUseCase.buildUseCaseObservable(params)
            .test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertFalse(backpack.details.contains(detail))
    }
}