package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.usecases.audioDetail.SaveEventAudioDetailUseCase
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SaveEventAudioDetailUseCaseTest {
    private lateinit var saveEventAudioDetailUseCase: SaveEventAudioDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: StorageRepository = mockk(relaxed = true)

    private val tempStorageFile = DataFactory.randomFile()
    private val recordingName = DataFactory.randomString()
    private lateinit var event: Event

    @Before
    fun setUp() {
        saveEventAudioDetailUseCase = SaveEventAudioDetailUseCase(
            repository,
            scheduler
        )
        event = EventFactory.makeEvent(nbrOfDetails = 0)
    }

    @Test
    fun saveAudioUC_saveAudioNormal_savedToStorage() {
        // Arrange
        every { repository.saveEventAudio(any(), any()) } returns Single.just(mockk())

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(
            SaveEventAudioDetailUseCase.Params(tempStorageFile, event)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.saveEventAudio(any(), any()) }
    }

    @Test
    fun saveAudioUC_saveAudioNormal_addedToEvent() {
        // Arrange
        every { repository.saveEventAudio(any(), any()) } returns Single.just(mockk())

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(
            SaveEventAudioDetailUseCase.Params(tempStorageFile, event)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertTrue(event.details.isNotEmpty())

        val resultingDetail = event.details.first()
        assertTrue(resultingDetail is AudioDetail)
    }

    @Test
    fun saveAudioUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every { repository.saveEventAudio(any(), any()) } returns Single.error(IOException())

        // Act
        saveEventAudioDetailUseCase.buildUseCaseObservable(
            SaveEventAudioDetailUseCase.Params(tempStorageFile, event)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}