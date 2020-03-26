package be.hogent.faith.service.usecases.detail.audioDetail

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import be.hogent.faith.storage.localStorage.ITemporaryStorage
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
    private lateinit var saveEventAudioDetailUseCase: SaveEventDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: ITemporaryStorage = mockk(relaxed = true)

    private val event = Event()
    private val audioDetail = mockk<AudioDetail>()

    @Before
    fun setUp() {
        saveEventAudioDetailUseCase =
            SaveEventDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveAudioUC_saveAudioNormal_savedToStorage() {
        // Arrange
        every {
            repository.storeDetailWithContainer(
                audioDetail,
                event
            )
        } returns Completable.complete()
        val params = SaveEventDetailUseCase.Params(audioDetail, event)

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeDetailWithContainer(audioDetail, event) }
    }

    @Test
    fun saveAudioUC_saveAudioNormal_addedToEvent() {
        // Arrange
        every {
            repository.storeDetailWithContainer(
                audioDetail,
                event
            )
        } returns Completable.complete()
        val params = SaveEventDetailUseCase.Params(audioDetail, event)

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
            repository.storeDetailWithContainer(
                audioDetail,
                event
            )
        } returns Completable.error(mockk<IOException>())
        val params = SaveEventDetailUseCase.Params(audioDetail, event)

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(params).test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}