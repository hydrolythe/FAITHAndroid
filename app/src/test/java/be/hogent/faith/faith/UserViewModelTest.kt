package be.hogent.faith.faith

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.usecases.user.GetUserUseCase
import be.hogent.faith.service.usecases.event.SaveEventUseCase
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserViewModelTest {
    private lateinit var userViewModel: UserViewModel
    private val saveEventUseCase = mockk<SaveEventUseCase>(relaxed = true)
    private val getUserUseCase = mockk<GetUserUseCase>(relaxed = true)
    private val eventTitle = DataFactory.randomString()
    private val event = EventFactory.makeEvent()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        userViewModel = UserViewModel(saveEventUseCase, getUserUseCase)
        userViewModel.setUser(mockk(relaxed = true))
    }

    @Test
    fun userViewModel_saveEvent_nullTitle_noUseCaseCall_errorMessage() {
        // Arrange
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        userViewModel.titleErrorMessage.observeForever(errorMessageObserver)
        event.title = null
        // Act
        userViewModel.saveEvent(event)

        // Assert
        verify { saveEventUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun userViewModel_saveEvent_CallUseCaseWithCorrectParams() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()

        // Act
        userViewModel.saveEvent(event)
        verify { saveEventUseCase.execute(capture(params), any()) }

        // Assert
        assertEquals(event, params.captured.event)
    }

    @Test
    fun userViewModel_saveEvent_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        userViewModel.eventSavedState.observeForever(successObserver)

        // Act
        userViewModel.saveEvent(event)
        verify { saveEventUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        verify(atLeast = 2) { successObserver.onChanged(any()) }
        assertEquals(
            ResourceState.SUCCESS,
            userViewModel.eventSavedState.value?.status
        )
    }

    @Test
    fun userViewModel_saveEvent_callUseCaseAndNotifiesFailure() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        userViewModel.eventSavedState.observeForever(successObserver)

        // Act
        userViewModel.saveEvent(event)
        verify { saveEventUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify(atLeast = 2) { successObserver.onChanged(any()) }
        assertEquals(
            ResourceState.ERROR,
            userViewModel.eventSavedState.value?.status
        )
    }

    @Test
    fun userViewModel_getUser_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val observer = slot<DisposableSubscriber<User>>()
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        userViewModel.getLoggedInUserState.observeForever(successObserver)

        // Act
        userViewModel.getLoggedInUser()

        verify { getUserUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onNext(mockk(relaxed = true))

        // Assert
        assertEquals(
            ResourceState.SUCCESS,
            userViewModel.getLoggedInUserState.value?.status
        )
        verify { successObserver.onChanged(any()) }
    }

    @Test
    fun userViewModel_getUser_callUseCaseAndNotifiesFailure() {
        val observer = slot<DisposableSubscriber<User>>()
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        userViewModel.getLoggedInUserState.observeForever(successObserver)

        // Act
        userViewModel.getLoggedInUser()

        verify { getUserUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        assertEquals(
            ResourceState.ERROR,
            userViewModel.getLoggedInUserState.value?.status
        )
        verify { successObserver.onChanged(any()) }
    }
}