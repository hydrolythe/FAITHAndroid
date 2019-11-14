package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.event.SaveEmotionAvatarUseCase
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SaveEmotionAvatarUseCaseTest {
    private lateinit var saveEmotionAvatarUseCase: SaveEmotionAvatarUseCase
    private lateinit var observer: Scheduler
    private lateinit var storageRepository: StorageRepository
    private var bitmap = mockk<Bitmap>()
    private lateinit var event: Event

    @Before
    fun setUp() {
        observer = mockk()
        storageRepository = mockk(relaxed = true)
        saveEmotionAvatarUseCase = SaveEmotionAvatarUseCase(
            storageRepository,
            observer
        )
        event = EventFactory.makeEvent(nbrOfDetails = 0)
    }

    @Test
    fun saveBitMapUC_execute_saves() {
        every { storageRepository.saveEventEmotionAvatar(any(), any()) } returns Single.just(mockk())

        saveEmotionAvatarUseCase.buildUseCaseObservable(SaveEmotionAvatarUseCase.Params(bitmap, event))
            .test()
            .assertNoErrors()

        // The image should be stored in the repo
        verify {
            storageRepository.saveEventEmotionAvatar(bitmap, event)
            // The image should be added to the event
            assertNotNull(event.emotionAvatar)
        }
    }

    @Test
    fun saveBitMapUC_execute_failsOnRepoError() {
        every { storageRepository.saveEventEmotionAvatar(any(), any()) } returns Single.error(IOException())

        saveEmotionAvatarUseCase.buildUseCaseObservable(SaveEmotionAvatarUseCase.Params(bitmap, event))
            .test()
            .assertError(IOException::class.java)

        // The image shouldn't be added to the event
        assertNull(event.emotionAvatar)
    }
}