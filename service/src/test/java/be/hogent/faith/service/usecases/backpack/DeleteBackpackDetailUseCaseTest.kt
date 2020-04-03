package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class DeleteBackpackDetailUseCaseTest {
    private lateinit var deleteBackpackDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>
    private val scheduler: Scheduler = mockk()
    private val repository: IDetailContainerRepository<Backpack> = mockk(relaxed = true)

    private val detail = mockk<AudioDetail>()

    @Before
    fun setUp() {
        deleteBackpackDetailUseCase =
            DeleteDetailsContainerDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun deleteDetailUC_deleteDetailFromStorage() {
        // Arrange
        every { repository.deleteDetail(detail) } returns Completable.complete()
        val params = DeleteDetailsContainerDetailUseCase.Params(detail)

        // Act
        deleteBackpackDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.deleteDetail(detail) }
    }
}