package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class SaveBitmapUseCaseTest {
    private lateinit var saveBitmapUseCase: SaveBitmapUseCase
    private lateinit var observer: Scheduler
    private lateinit var storageRepository: StorageRepository
    private var bitmap = mockk<Bitmap>()
    private var event = mockk<Event>()

    @Before
    fun setUp() {
        observer = mockk()
        storageRepository = mockk(relaxed = true)
        saveBitmapUseCase = SaveBitmapUseCase(storageRepository, observer)
    }

    @Test
    fun saveBitMapUC_execute_saves() {
        every { storageRepository.storeBitmap(bitmap, event) } returns Single.just(mockk<File>())

        saveBitmapUseCase.execute(SaveBitmapUseCase.SaveBitmapParams(bitmap, event))
            .test()
            .assertNoErrors()

        // The image should be stored in the repo
        verify { storageRepository.storeBitmap(bitmap, event) }
        // The image should be added to the event
        verify { event.addDetail(any()) }
    }

    @Test
    fun saveBitMapUC_execute_failsOnRepoError() {
        every { storageRepository.storeBitmap(mockk(), mockk()) } returns Single.error(IOException())

        saveBitmapUseCase.execute(SaveBitmapUseCase.SaveBitmapParams(bitmap, event))
            .test()
            .assertError(IOException::class.java)

        // The image shouldn't be added to the event
        verify(exactly = 0) { event.addDetail(any()) }
    }
}