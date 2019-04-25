package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventDetailsViewModelTest {
    private lateinit var viewModel: EventDetailsViewModel
    private val saveEmotionAvatarUseCase = mockk<SaveEmotionAvatarUseCase>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EventDetailsViewModel(saveEmotionAvatarUseCase, mockk())
    }

    @Test
    fun eventDetailsVM_updateEvent_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Event>>(relaxed = true)
        viewModel.event.observeForever(observer)

        // Act
        viewModel.updateEvent()

        // Assert
        verify { observer.onChanged(any()) }
    }

    @Test
    fun eventDetailsVM_saveImage_callsUseCase() {
        // Arrange
        val params = slot<SaveEmotionAvatarUseCase.Params>()
        every { saveEmotionAvatarUseCase.execute(capture(params)) } returns Completable.complete()

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { saveEmotionAvatarUseCase.execute(any()) }
        Assert.assertEquals(viewModel.event.value, params.captured.event)
    }

    @Test
    fun eventDetailsVM_saveImage_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        every { saveEmotionAvatarUseCase.execute(any()) } returns Completable.complete()
        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun eventDetailsVM_saveImage_notifiesWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        val errorMessage = "Something failed"

        every { saveEmotionAvatarUseCase.execute(any()) } returns Completable.error(RuntimeException(errorMessage))

        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { failedObserver.onChanged(errorMessage) }
        verify { successObserver wasNot Called }
    }
}