package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class SaveEventPhotoDetailUseCaseTest {
    private val observer = mockk<Scheduler>()
    private val tempRecordingFile = mockk<File>()
    private val storageRepository = mockk<StorageRepository>(relaxed = true)

    private lateinit var event: Event
    private lateinit var saveEventPhotoDetailUseCase: SaveEventPhotoDetailUseCase

    @Before
    fun setUp() {
        event = EventFactory.makeEvent(nbrOfDetails = 0)
        saveEventPhotoDetailUseCase = SaveEventPhotoDetailUseCase(storageRepository, observer)
    }

    @Test
    fun takePhotoUC_executeNormally_savedToStorage() {
        // Arrange
        every {
            storageRepository.saveEventPhoto(tempRecordingFile, event)
        } returns Single.just(mockk())

        // Act
        saveEventPhotoDetailUseCase.buildUseCaseSingle(
            SaveEventPhotoDetailUseCase.Params(tempRecordingFile, event)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { storageRepository.saveEventPhoto(tempRecordingFile, event) }
    }

    @Test
    fun takePhotoUC_executeNormally_addedToEventAndReturnsDetail() {
        // Arrange
        every {
            storageRepository.saveEventPhoto(tempRecordingFile, event)
        } returns Single.just(mockk())

        // Act
        val result = saveEventPhotoDetailUseCase.buildUseCaseSingle(
            SaveEventPhotoDetailUseCase.Params(tempRecordingFile, event)
        )

        result.test()
            .assertNoErrors()
            .assertValue { newDetail -> newDetail is PhotoDetail }

        // Assert
        assertTrue(event.details.isNotEmpty())

        val resultingDetail = event.details.first()
        assertTrue(resultingDetail is PhotoDetail)
    }

    @Test
    fun takePhotoUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every {
            storageRepository.saveEventPhoto(tempRecordingFile, event)
        } returns Single.error(IOException())

        // Act
        saveEventPhotoDetailUseCase.buildUseCaseSingle(
            SaveEventPhotoDetailUseCase.Params(tempRecordingFile, event)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}