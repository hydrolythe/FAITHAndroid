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

class TakeEventPhotoUseCaseTest {
    private val observer = mockk<Scheduler>()
    private val tempRecordingFile = mockk<File>()
    private val photoName = DataFactory.randomString()
    private val storageRepository = mockk<StorageRepository>(relaxed = true)

    private lateinit var event: Event
    private lateinit var takeEventPhotoUseCase: TakeEventPhotoUseCase

    @Before
    fun setUp() {
        event = EventFactory.makeEvent(nbrOfDetails = 0)
        takeEventPhotoUseCase = TakeEventPhotoUseCase(storageRepository, observer)
    }

    @Test
    fun takePhotoUC_executeNormally_savedToStorage() {
        // Arrange
        every {
            storageRepository.movePhotoFromTempStorage(tempRecordingFile, event, photoName)
        } returns Single.just(mockk())

        // Act
        takeEventPhotoUseCase.buildUseCaseObservable(
            TakeEventPhotoUseCase.Params(tempRecordingFile, event, photoName)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { storageRepository.movePhotoFromTempStorage(any(), event, any()) }
    }

    @Test
    fun takePhotoUC_executeNormally_addedToEvent() {
        // Arrange
        every {
            storageRepository.movePhotoFromTempStorage(tempRecordingFile, event, photoName)
        } returns Single.just(mockk())

        // Act
        takeEventPhotoUseCase.buildUseCaseObservable(
            TakeEventPhotoUseCase.Params(tempRecordingFile, event, photoName)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertTrue(event.details.isNotEmpty())

        val resultingDetail = event.details.first()
        assertTrue(resultingDetail is PictureDetail)
        assertEquals(resultingDetail.name, photoName)
        // TODO: add check for recordingName
    }

    @Test
    fun takePhotoUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every {
            storageRepository.movePhotoFromTempStorage(tempRecordingFile, event, photoName)
        } returns Single.error(IOException())

        // Act
        takeEventPhotoUseCase.buildUseCaseObservable(
            TakeEventPhotoUseCase.Params(tempRecordingFile, event, photoName)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}