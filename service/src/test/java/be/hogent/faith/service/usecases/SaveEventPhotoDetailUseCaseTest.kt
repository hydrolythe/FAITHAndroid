package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.event.SaveEventPhotoDetailUseCase
import be.hogent.faith.storage.StorageRepository
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

class SaveEventPhotoDetailUseCaseTest {
    private val observer = mockk<Scheduler>()
    private val storageRepository = mockk<ITemporaryStorage>(relaxed = true)

    private val event = Event()
    private val photoDetail = mockk<PhotoDetail>()
    private lateinit var saveEventPhotoDetailUseCase: SaveEventPhotoDetailUseCase

    @Before
    fun setUp() {
        saveEventPhotoDetailUseCase =
            SaveEventPhotoDetailUseCase(
                storageRepository,
                observer
            )
    }

    @Test
    fun saveEventPhotoUC_executeNormally_savedToStorage() {
        // Arrange
        every {
            storageRepository.storeDetailWithEvent(photoDetail, event)
        } returns Completable.complete()
        val params = SaveEventPhotoDetailUseCase.Params(photoDetail, event)

        // Act
        saveEventPhotoDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { storageRepository.storeDetailWithEvent(photoDetail, event) }
    }

    @Test
    fun saveEventPhotoUC_executeNormally_addedToEvent() {
        // Arrange
        every {
            storageRepository.storeDetailWithEvent(photoDetail, event)
        } returns Completable.complete()
        val params = SaveEventPhotoDetailUseCase.Params(photoDetail, event)

        // Act
        val result = saveEventPhotoDetailUseCase.buildUseCaseObservable(params)
        result.test()

        // Assert
        assertTrue(event.details.isNotEmpty())

        val eventDetail = event.details.first()
        assertEquals(eventDetail, photoDetail)
    }

    @Test
    fun saveEventPhotoUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every {
            storageRepository.storeDetailWithEvent(photoDetail, event)
        } returns Completable.error(mockk<IOException>())
        val params = SaveEventPhotoDetailUseCase.Params(photoDetail, event)

        // Act
        val result = saveEventPhotoDetailUseCase.buildUseCaseObservable(params)
        result.test()
            .assertError(IOException::class.java)

        // Assert
        assertTrue(event.details.isEmpty())
    }
}