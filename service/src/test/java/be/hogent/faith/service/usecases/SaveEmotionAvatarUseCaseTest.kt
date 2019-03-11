package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.IOException

class SaveEmotionAvatarUseCaseTest {
    private lateinit var saveEmotionAvatarUseCase: SaveEmotionAvatarUseCase
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
        saveEmotionAvatarUseCase = SaveEmotionAvatarUseCase(storageRepository, observer)
    }

    @Test
    fun saveBitMapUC_execute_saves() {
        every { storageRepository.storeBitmap(any(), any(), any()) } returns Single.just(mockk<File>())

        saveEmotionAvatarUseCase.buildUseCaseObservable(SaveEmotionAvatarUseCase.Params(bitmap, event))
            .test()
            .assertNoErrors()

        // The image should be stored in the repo
        verify { storageRepository.storeBitmap(bitmap, event, any()) }
        // The image should be added to the event
        assertNotNull(event.emotionAvatar)
    }

    @Test
    fun saveBitMapUC_execute_failsOnRepoError() {
        every { storageRepository.storeBitmap(any(), any(), any()) } returns Single.error(IOException())

        saveEmotionAvatarUseCase.buildUseCaseObservable(SaveEmotionAvatarUseCase.Params(bitmap, event))
            .test()
            .assertError(IOException::class.java)

        // The image shouldn't be added to the event
        assertNull(event.emotionAvatar)
    }
}