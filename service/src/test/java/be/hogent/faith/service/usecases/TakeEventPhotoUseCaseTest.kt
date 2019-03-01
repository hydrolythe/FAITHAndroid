package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.interfaces.PhotoTaker
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File

class TakeEventPhotoUseCaseTest {
    private lateinit var takeEventPhotoUseCase: TakeEventPhotoUseCase
    private lateinit var observer: Scheduler
    private lateinit var storageRepository: StorageRepository
    private var bitmap = mockk<Bitmap>()
    private var photoTaker = mockk<PhotoTaker>()
    // Give own dateTime because AndroidThreeTen requires context to init timezones
    private var event = Event(
        dateTime = LocalDateTime.of(2019, 2, 19, 16, 58)
    )

    @Before
    fun setUp() {
        observer = mockk()
        storageRepository = mockk(relaxed = true)
        // spyk used to mix mock and real object.
        // We need the mock to mock the private call to createSaveFileName
        takeEventPhotoUseCase = spyk(TakeEventPhotoUseCase(storageRepository, observer), recordPrivateCalls = true)
    }

    @Test
    fun saveEventPhoto_execute_saves() {
        every { storageRepository.storeBitmap(any(), any(), any()) } returns Single.just(mockk<File>())
        // ABP requires timezone date that we don't have when running unit tests
        every { photoTaker.takePhoto(any()) } returns Completable.complete()
        every { storageRepository["createSaveFileName"]() } returns "saveFile"

        takeEventPhotoUseCase.buildUseCaseObservable((TakeEventPhotoUseCase.Params(photoTaker, event)))
            .test()
            .assertNoErrors()
            .assertComplete()

        // Must be added to details
        assertEquals(1, event.details.size)
        // Detail must be correct type
        assertEquals(DetailType.PICTURE, event.details.first().detailType)
        // File of detail must be set
        assertNotNull(event.details.first().file)
    }

    @Test
    fun saveEventPhoto_execute_failsOnCameraError() {
        every { photoTaker.takePhoto(any()) } returns Completable.error(RuntimeException())
        every { storageRepository["createSaveFileName"]() } returns "saveFileName"

        takeEventPhotoUseCase.buildUseCaseObservable((TakeEventPhotoUseCase.Params(photoTaker, event)))
            .test()
            .assertError(RuntimeException::class.java)
            .assertNotComplete()

        // Details must still be empty (nothing could be added)
        assert(event.details.isEmpty())
    }
}