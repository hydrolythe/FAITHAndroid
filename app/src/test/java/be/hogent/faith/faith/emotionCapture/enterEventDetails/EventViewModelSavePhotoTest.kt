package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.service.usecases.photoDetail.SaveEventPhotoDetailUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.Called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableSingleObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class EventViewModelSavePhotoTest : KoinTest {
    private lateinit var viewModel: EventViewModel
    private val savePhotoUseCase = mockk<SaveEventPhotoDetailUseCase>(relaxed = true)

    private val eventTitle = DataFactory.randomString()
    private val eventNotes = DataFactory.randomString()
    private val eventDateTime = DataFactory.randomDateTime()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private val event
        get() = viewModel.event.value!!

    @Before
    fun setUp() {
        startKoin { modules(appModule) }
        viewModel = EventViewModel(
            mockk(),
            savePhotoUseCase,
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
    fun eventViewModel_savePhoto_usesCorrectParams() {
        // Arrange
        val observer = slot<DisposableSingleObserver<Detail>>()
        val params = slot<SaveEventPhotoDetailUseCase.Params>()

        // Act
        viewModel.savePhoto(mockk())
        verify { savePhotoUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onSuccess(mockk())

        // Assert
        assertEquals(event, params.captured.event)
    }

    @Test
    fun eventViewModel_savePhoto_notifiesWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Detail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableSingleObserver<Detail>>()

        viewModel.photoDetailSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.savePhoto(mockk())
        verify { savePhotoUseCase.execute(any(), capture(observer)) }
        observer.captured.onError(RuntimeException())

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }

    @Test
    fun eventViewModel_savePhoto_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Detail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableSingleObserver<Detail>>()

        viewModel.photoDetailSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.savePhoto(mockk())
        verify { savePhotoUseCase.execute(any(), capture(observer)) }
        observer.captured.onSuccess(mockk())

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorObserver wasNot Called }
    }
}