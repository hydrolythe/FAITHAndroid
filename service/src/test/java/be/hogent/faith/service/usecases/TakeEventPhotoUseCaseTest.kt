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
import java.io.File
import java.io.IOException

class TakeEventPhotoUseCaseTest {
    private val observer = mockk<Scheduler>()
    private val tempRecordingFile = mockk<File>()
    private val photoName = "TestName"
    private val storageRepository = mockk<StorageRepository>(relaxed = true)

    private lateinit var event: Event
    private lateinit var takeEventPhotoUseCase: TakeEventPhotoUseCase

    @Before
    fun setUp() {
        event = Event()
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
        assertTrue(resultingDetail.detailType == DetailType.PICTURE)
        // TODO: add check for name
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