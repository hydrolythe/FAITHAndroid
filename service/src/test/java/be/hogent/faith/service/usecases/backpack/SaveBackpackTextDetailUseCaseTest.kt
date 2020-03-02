package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class SaveBackpackTextDetailUseCaseTest {

    private lateinit var saveBackpackTextDetailUseCase: SaveBackpackTextDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: IDummyStorageRepository = mockk(relaxed = true)

    private val detail = mockk<TextDetail>()

    @Before
    fun setUp() {
        saveBackpackTextDetailUseCase =
            SaveBackpackTextDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveTextUC_saveTextNormal_savedToStorage() {
        // Arrange
        every { repository.storeDetail(detail) } returns Completable.complete()
        val params = SaveBackpackTextDetailUseCase.Params(detail)

        // Act
        saveBackpackTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeDetail(detail) }
    }
}