package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.event.SaveEmotionAvatarUseCase
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class SaveEmotionAvatarUseCaseTest {
    private lateinit var saveEmotionAvatarUseCase: SaveEmotionAvatarUseCase
    private lateinit var observer: Scheduler
    private lateinit var storageRepository: ITemporaryFileStorageRepository
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
        event = EventFactory.makeEvent(numberOfDetails = 0)
    }

    @Test
    fun saveBitMapUC_execute_saves() {
        val avatarFile = File("path")
        every { storageRepository.storeBitmap(any()) } returns Single.just(avatarFile)

        saveEmotionAvatarUseCase.buildUseCaseObservable(
            SaveEmotionAvatarUseCase.Params(bitmap, event)
        )
            .test()
            .assertNoErrors()

        // The image should be stored in the repo
        verify {
            storageRepository.storeBitmap(bitmap)
            // The image should be added to the event
            assertEquals(avatarFile, event.emotionAvatar)
        }
    }

    @Test
    fun saveBitMapUC_execute_failsOnRepoError() {
        every { storageRepository.storeBitmap(any()) } returns Single.error(
            IOException()
        )

        saveEmotionAvatarUseCase.buildUseCaseObservable(
            SaveEmotionAvatarUseCase.Params(
                bitmap,
                event
            )
        )
            .test()
            .assertError(IOException::class.java)

        // The image shouldn't be added to the event
        assertNull(event.emotionAvatar)
    }
}