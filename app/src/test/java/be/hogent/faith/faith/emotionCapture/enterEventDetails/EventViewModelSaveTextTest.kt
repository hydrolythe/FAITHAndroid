package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.textDetail.SaveEventTextDetailUseCase
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
import org.koin.core.context.stopKoin

class EventViewModelSaveTextTest {
    private lateinit var viewModel: EventViewModel
    private val saveTextUseCase = mockk<SaveEventTextDetailUseCase>(relaxed = true)

    private val eventTitle = DataFactory.randomString()
    private val eventNotes = DataFactory.randomString()
    private val eventDateTime = DataFactory.randomDateTime()

    private val detail = mockk<TextDetail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private val event
        get() = viewModel.event.value!!

    @Before
    fun setUp() {
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

    @Test
    fun enterTextVM_saveText_callsUseCase() {
        val params = slot<SaveEventTextDetailUseCase.Params>()

        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.detail)
        assertEquals(event, params.captured.event)
    }

    @Test
    fun enterTextVM_saveText_notifiesUseCaseSuccess() {
        // Arrange
        val observer = slot<DisposableCompletableObserver>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(any(), capture(observer)) }
        observer.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_text_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun enterTextViewModel_saveText_notifiesUseCaseFailure() {
        // Arrange
        val observer = slot<DisposableCompletableObserver>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(any(), capture(observer)) }
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}