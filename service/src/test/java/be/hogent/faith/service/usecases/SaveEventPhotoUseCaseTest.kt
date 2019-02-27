package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.IOException

class SaveEventPhotoUseCaseTest {
    private lateinit var saveEventPhotoUseCase: SaveEventPhotoUseCase
    private lateinit var observer: Scheduler
    private lateinit var storageRepository: StorageRepository
    private var bitmap = mockk<Bitmap>()
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
        saveEventPhotoUseCase = spyk(SaveEventPhotoUseCase(storageRepository, observer), recordPrivateCalls = true)
    }

    @Test
    fun saveEventPhoto_execute_saves() {
        every { storageRepository.storeBitmap(any(), any(), any()) } returns Single.just(mockk<File>())
        // ABP requires timezone date that we don't have when running unit tests
        every { saveEventPhotoUseCase["createSaveFileName"]() } returns "27_2_2019_13_05_222"

        saveEventPhotoUseCase.buildUseCaseObservable((SaveEventPhotoUseCase.Params(bitmap, event)))
            .test()
            .assertNoErrors()
            .assertComplete()

        // Must be stored in repo
        verify { storageRepository.storeBitmap(bitmap, event, any()) }
        // Must be added to details
        assertEquals(1, event.details.size)
        // Detail must be correct type
        assertEquals(DetailType.PICTURE, event.details.first().detailType)
        // File of detail must be set
        assertNotNull(event.details.first().file)
    }

    @Test
    fun saveEventPhoto_execute_failsOnRepoError() {
        every { storageRepository.storeBitmap(any(), any(), any()) } returns Single.error(IOException())
        every { saveEventPhotoUseCase["createSaveFileName"]() } returns "27_2_2019_13_05_222"

        saveEventPhotoUseCase.buildUseCaseObservable((SaveEventPhotoUseCase.Params(bitmap, event)))
            .test()
            .assertError(IOException::class.java)
            .assertNotComplete()

        // Details must still be empty (nothing could be added)
        assert(event.details.isEmpty())
    }
}