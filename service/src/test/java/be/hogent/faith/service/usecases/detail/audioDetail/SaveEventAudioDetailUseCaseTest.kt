package be.hogent.faith.service.usecases.detail.audioDetail

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.usecases.event.SaveEventAudioDetailUseCase
import be.hogent.faith.storage.localstorage.ITemporaryStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SaveEventAudioDetailUseCaseTest {
    private lateinit var saveEventAudioDetailUseCase: SaveEventAudioDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: ITemporaryStorageRepository = mockk(relaxed = true)

    private val event = Event()
    private val audioDetail = mockk<AudioDetail>()

    @Before
    fun setUp() {
        saveEventAudioDetailUseCase =
            SaveEventAudioDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveAudioUC_saveAudioNormal_savedToStorage() {
        // Arrange
        every {
            repository.storeDetailWithEvent(
                audioDetail,
                event
            )
        } returns Completable.complete()
        val params = SaveEventAudioDetailUseCase.Params(audioDetail, event)

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeDetailWithEvent(audioDetail, event) }
    }

    @Test
    fun saveAudioUC_saveAudioNormal_addedToEvent() {
        // Arrange
        every {
            repository.storeDetailWithEvent(
                audioDetail,
                event
            )
        } returns Completable.complete()
        val params = SaveEventAudioDetailUseCase.Params(audioDetail, event)

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertTrue(event.details.isNotEmpty())

        val eventDetail = event.details.first()
        assertEquals(eventDetail, audioDetail)
    }

    @Test
    fun saveAudioUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every {
            repository.storeDetailWithEvent(
                audioDetail,
                event
            )
        } returns Completable.error(mockk<IOException>())
        val params = SaveEventAudioDetailUseCase.Params(audioDetail, event)

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(params).test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}