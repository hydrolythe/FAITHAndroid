package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.Called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class EventViewModelSaveEmotionAvatarTest : KoinTest {
    private lateinit var viewModel: EventViewModel
    private val saveEmotionAvatarUseCase = mockk<SaveEmotionAvatarUseCase>(relaxed = true)

    private val eventTitle = DataFactory.randomString()
    private val eventNotes = DataFactory.randomString()
    private val eventDateTime = DataFactory.randomDateTime()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        startKoin { modules(appModule) }
        viewModel = EventViewModel(
            saveEmotionAvatarUseCase,
            mockk(),
            mockk(),
            mockk(),
            mockk()
        )

        // We have to add an observer so event changes when title/notes/date are given a new value
        viewModel.event.observeForever(mockk(relaxed = true))

        viewModel.eventTitle.value = eventTitle
        viewModel.eventNotes.value = eventNotes
        viewModel.eventDate.value = eventDateTime
    }

    @After
    fun takeDown() {
        stopKoin()
    }

    @Test
    fun eventViewModel_saveEmotionAvatar_callsUseCase() {
        // Arrange
        val params = slot<SaveEmotionAvatarUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        // Act
        viewModel.saveEmotionAvatarImage(mockk())
        verify { saveEmotionAvatarUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        assertEquals(viewModel.event.value, params.captured.event)
    }

    @Test
    fun eventViewModel_saveEmotionAvatar_notifiesWhenSaveCompletes() {
        // Arrange
        val observer = slot<DisposableCompletableObserver>()
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        val failedObserver = mockk<Observer<Int>>(relaxed = true)

        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveEmotionAvatarImage(mockk())
        verify { saveEmotionAvatarUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun eventViewModel_saveEmotionAvatar_notifiesWhenSaveFails() {
        // Arrange
        val observer = slot<DisposableCompletableObserver>()
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        val failedObserver = mockk<Observer<Int>>(relaxed = true)

        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveEmotionAvatarImage(mockk())
        verify { saveEmotionAvatarUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { failedObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}