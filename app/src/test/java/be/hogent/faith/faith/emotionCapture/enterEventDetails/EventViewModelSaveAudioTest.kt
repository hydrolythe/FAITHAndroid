package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.testModule
import be.hogent.faith.service.usecases.SaveEventAudioUseCase
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

class EventViewModelSaveAudioTest : KoinTest {
    private lateinit var viewModel: EventViewModel
    private val saveAudioUseCase = mockk<SaveEventAudioUseCase>(relaxed = true)

    private val eventTitle = DataFactory.randomString()
    private val eventNotes = DataFactory.randomString()
    private val eventDateTime = DataFactory.randomDateTime()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private val event
        get() = viewModel.event.value!!

    @Before
    fun setUp() {
        startKoin { modules(listOf(appModule, testModule)) }
        viewModel = EventViewModel(
            mockk(),
            mockk(),
            saveAudioUseCase,
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
    fun eventViewModel_saveAudio_callsUseCase() {
        // Arrange
        val params = slot<SaveEventAudioUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        // Act
        viewModel.saveAudio()

        verify { saveAudioUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        assertEquals(event, params.captured.event)
    }

    @Test
    fun eventViewModel_saveAudio_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableCompletableObserver>()

        viewModel.recordingSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveAudio()
        verify { saveAudioUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun eventViewModel_saveAudio_notifiesWithErrorMessageWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableCompletableObserver>()

        viewModel.recordingSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveAudio()
        verify { saveAudioUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { failedObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}