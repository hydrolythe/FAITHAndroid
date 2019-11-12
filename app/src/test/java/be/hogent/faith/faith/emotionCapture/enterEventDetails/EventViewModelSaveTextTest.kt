package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.service.usecases.textDetail.CreateTextDetailUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.Called
import io.mockk.called
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

class EventViewModelSaveTextTest : KoinTest {
    private lateinit var viewModel: EventViewModel
    private val saveTextUseCase = mockk<CreateTextDetailUseCase>(relaxed = true)

    private val eventTitle = DataFactory.randomString()
    private val eventNotes = DataFactory.randomString()
    private val eventDateTime = DataFactory.randomDateTime()

    private val text = "Hello World!"

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private val event
        get() = viewModel.event.value!!

    @Before
    fun setUp() {
        startKoin { modules(appModule) }

        viewModel = EventViewModel(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            saveTextUseCase
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
    fun enterTextVM_saveText_callsUseCase() {
        val params = slot<CreateTextDetailUseCase.Params>()

        viewModel.saveText(text)
        verify { saveTextUseCase.execute(capture(params), any()) }

        assertEquals(text, params.captured.text)
        assertEquals(event, params.captured.event)
    }

    @Test
    fun enterTextVM_saveText_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val params = slot<CreateTextDetailUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveText(text)
        verify { saveTextUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun enterTextViewModel_saveText_notifiesWhenSaveFails() {
        // Arrange
        val params = slot<CreateTextDetailUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveText(text)
        verify { saveTextUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}