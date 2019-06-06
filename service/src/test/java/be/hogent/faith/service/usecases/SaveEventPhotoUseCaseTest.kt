package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class SaveEventPhotoUseCaseTest {
    private val observer = mockk<Scheduler>()
    private val tempRecordingFile = mockk<File>()
    private val photoName = DataFactory.randomString()
    private val storageRepository = mockk<StorageRepository>(relaxed = true)

    private lateinit var event: Event
    private lateinit var saveEventPhotoUseCase: SaveEventPhotoUseCase

    @Before
    fun setUp() {
        event = EventFactory.makeEvent(nbrOfDetails = 0)
        saveEventPhotoUseCase = SaveEventPhotoUseCase(storageRepository, observer)
    }

    @Test
    fun takePhotoUC_executeNormally_savedToStorage() {
        // Arrange
        every {
            storageRepository.saveEventPhoto(tempRecordingFile, event)
        } returns Single.just(mockk())

        // Act
        saveEventPhotoUseCase.buildUseCaseSingle(
            SaveEventPhotoUseCase.Params(tempRecordingFile, event, photoName)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { storageRepository.saveEventPhoto(tempRecordingFile, event) }
    }

    @Test
    fun takelPhotoUC_executeNormally_addedToEventAndReturnsDetail() {
        // Arrange
        every {
            storageRepository.saveEventPhoto(tempRecordingFile, event)
        } returns Single.just(mockk())

        // Act
        val result = saveEventPhotoUseCase.buildUseCaseSingle(
            SaveEventPhotoUseCase.Params(tempRecordingFile, event, photoName)
        )

        result.test()
            .assertNoErrors()
            .assertValue{newDetail -> newDetail is PictureDetail}
            .assertValue{newDetail -> newDetail.name == photoName }

           // Assert
        assertTrue(event.details.isNotEmpty())

        val resultingDetail = event.details.first()
        assertTrue(resultingDetail is PictureDetail)
        assertEquals(resultingDetail.name, photoName)
    }

    @Test
    fun takePhotoUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every {
            storageRepository.saveEventPhoto(tempRecordingFile, event)
        } returns Single.error(IOException())

        // Act
        saveEventPhotoUseCase.buildUseCaseSingle(
            SaveEventPhotoUseCase.Params(tempRecordingFile, event, photoName)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}