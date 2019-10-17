package be.hogent.faith.faith

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.service.usecases.SaveEventUseCase
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserViewModelTest {
    private lateinit var userViewModel: UserViewModel
    private val saveEventUseCase = mockk<SaveEventUseCase>(relaxed = true)
    private val eventTitle = DataFactory.randomString()
    private val event = EventFactory.makeEvent()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        userViewModel = UserViewModel(saveEventUseCase)
        userViewModel.setUser(mockk(relaxed = true))
    }

    @Test
    fun userViewModel_saveEvent_nullTitle_noUseCaseCall_errorMessage() {
        // Arrange
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        userViewModel.titleErrorMessage.observeForever(errorMessageObserver)

        // Act
        userViewModel.saveEvent(null, mockk())

        // Assert
        verify { saveEventUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun userViewModel_saveEvent_CallUseCaseWithCorrectParams() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()

        // Act
        userViewModel.saveEvent(eventTitle, event)
        verify { saveEventUseCase.execute(capture(params), any()) }

        // Assert
        assertEquals(params.captured.eventTitle, eventTitle)
        assertEquals(event, params.captured.event)
    }

    @Test
    fun userViewModel_saveEvent_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        userViewModel.errorMessage.observeForever(errorObserver)
        userViewModel.eventSavedSuccessFully.observeForever(successObserver)

        // Act
        userViewModel.saveEvent(eventTitle, event)
        verify { saveEventUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun userViewModel_saveEvent_callUseCaseAndNotifiesFailure() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        userViewModel.errorMessage.observeForever(errorObserver)
        userViewModel.eventSavedSuccessFully.observeForever(successObserver)

        // Act
        userViewModel.saveEvent(eventTitle, event)
        verify { saveEventUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot called }
    }
}