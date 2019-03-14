package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.IOException

class SaveAudioRecordingUseCaseTest {
    private lateinit var saveAudioRecordingUseCase: SaveAudioRecordingUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: StorageRepository = mockk(relaxed = true)

    private val tempStorageFile = File("cacheDir/tempAudioRecording.3gp")
    private val eventName = "TestName"
    private lateinit var event: Event

    @Before
    fun setUp() {
        saveAudioRecordingUseCase = SaveAudioRecordingUseCase(repository, scheduler)
        event = Event(
            dateTime = LocalDateTime.of(2019, 2, 19, 16, 58)
        )
    }

    @Test
    fun saveAudioUC_saveAudioNormal_savedToStorage() {
        // Arrange
        every { repository.storeAudioRecording(tempStorageFile, event) } returns Single.just(mockk<File>())

        // Act
        saveAudioRecordingUseCase.buildUseCaseObservable(
            SaveAudioRecordingUseCase.SaveAudioRecordingParams(tempStorageFile, event)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeAudioRecording(any(), event) }
    }

    @Test
    fun saveAudioUC_saveAudioNormal_addedToEvent() {
        // Arrange
        every { repository.storeAudioRecording(tempStorageFile, event) } returns Single.just(mockk<File>())

        // Act
        saveAudioRecordingUseCase.buildUseCaseObservable(
            SaveAudioRecordingUseCase.SaveAudioRecordingParams(tempStorageFile, event, eventName)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertTrue(event.details.isNotEmpty())

        val resultingDetail = event.details.first()
        assertTrue(resultingDetail.detailType == DetailType.AUDIO)
        // TODO: also check name once it is added to the Detail class
    }

    @Test
    fun saveAudioUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every { repository.storeAudioRecording(tempStorageFile, event) } returns Single.error(IOException())

        // Act
        saveAudioRecordingUseCase.buildUseCaseObservable(
            SaveAudioRecordingUseCase.SaveAudioRecordingParams(tempStorageFile, event, eventName)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}