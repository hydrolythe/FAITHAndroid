package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class SaveBackpackDrawingDetailUseCaseTest {
    private lateinit var saveBackpackDrawingDetailUseCase: SaveBackpackDrawingDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: IDummyStorageRepository = mockk(relaxed = true)

    private val detail = mockk<DrawingDetail>()

    @Before
    fun setUp() {
        saveBackpackDrawingDetailUseCase =
            SaveBackpackDrawingDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveDrawingUC_saveDrawingNormal_savedToStorage() {
        // Arrange
        every { repository.storeDetail(detail) } returns Completable.complete()
        val params = SaveBackpackDrawingDetailUseCase.Params(detail)

        // Act
        saveBackpackDrawingDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeDetail(detail) }
    }
}