package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class DeleteBackpackDetailUseCaseTest {
    private lateinit var deleteBackpackDetailUseCase: DeleteBackpackDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: IDummyStorageRepository = mockk(relaxed = true)

    private val detail = mockk<AudioDetail>()

    @Before
    fun setUp() {
        deleteBackpackDetailUseCase =
            DeleteBackpackDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun deleteDetailUC_deleteDetailFromStorage() {
        // Arrange
        every { repository.deleteDetail(detail) } returns Completable.complete()
        val params = DeleteBackpackDetailUseCase.Params(detail)

        // Act
        deleteBackpackDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.deleteDetail(detail) }
    }
}