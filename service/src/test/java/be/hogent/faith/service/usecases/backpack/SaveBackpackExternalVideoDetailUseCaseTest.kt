package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class SaveBackpackExternalVideoDetailUseCaseTest {
    private lateinit var saveBackpackExternalVideoDetailUseCase: SaveBackpackExternalVideoDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: IDummyStorageRepository = mockk(relaxed = true)

    private val detail = mockk<ExternalVideoDetail>()

    @Before
    fun setUp() {
        saveBackpackExternalVideoDetailUseCase =
                SaveBackpackExternalVideoDetailUseCase(
                        repository,
                        scheduler
                )
    }

    @Test
    fun savePhotoUC_savePhotoNormal_savedToStorage() {
        // Arrange
        every { repository.storeDetail(detail) } returns Completable.complete()
        val params = SaveBackpackExternalVideoDetailUseCase.Params(detail)

        // Act
        saveBackpackExternalVideoDetailUseCase.buildUseCaseObservable(params).test()
                .assertNoErrors()
                .assertComplete()

        // Assert
        verify { repository.storeDetail(detail) }
    }
}