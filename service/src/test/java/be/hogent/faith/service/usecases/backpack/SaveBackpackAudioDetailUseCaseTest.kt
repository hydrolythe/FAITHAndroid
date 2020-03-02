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

class SaveBackpackAudioDetailUseCaseTest {
    private lateinit var saveBackpackAudioDetailUseCase: SaveBackpackAudioDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: IDummyStorageRepository = mockk(relaxed = true)

    private val detail = mockk<AudioDetail>()

    @Before
    fun setUp() {
        saveBackpackAudioDetailUseCase =
            SaveBackpackAudioDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveAudioUC_saveAudioNormal_savedToStorage() {
        // Arrange
        every { repository.storeDetail(detail) } returns Completable.complete()
        val params = SaveBackpackAudioDetailUseCase.Params(detail)

        // Act
        saveBackpackAudioDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeDetail(detail) }
    }
}