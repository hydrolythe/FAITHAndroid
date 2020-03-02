package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class SaveBackpackPhotoDetailUseCaseTest {
    private lateinit var saveBackpackPhotoDetailUseCase: SaveBackpackPhotoDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: IDummyStorageRepository = mockk(relaxed = true)

    private val detail = mockk<PhotoDetail>()

    @Before
    fun setUp() {
        saveBackpackPhotoDetailUseCase =
            SaveBackpackPhotoDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun savePhotoUC_savePhotoNormal_savedToStorage() {
        // Arrange
        every { repository.storeDetail(detail) } returns Completable.complete()
        val params = SaveBackpackPhotoDetailUseCase.Params(detail)

        // Act
        saveBackpackPhotoDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeDetail(detail) }
    }
}