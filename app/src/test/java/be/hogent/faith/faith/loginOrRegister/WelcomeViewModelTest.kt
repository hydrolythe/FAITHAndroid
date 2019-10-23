package be.hogent.faith.faith.loginOrRegister

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.service.usecases.LoginUserUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableMaybeObserver
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class WelcomeViewModelTest {
    private lateinit var welcomeViewModel: WelcomeViewModel
    private val loginUserUseCase = mockk<LoginUserUseCase>(relaxed = true)
    private val username = DataFactory.randomString()
    private val password = DataFactory.randomString()
    private val uuid = UUID.randomUUID().toString()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        welcomeViewModel = WelcomeViewModel(loginUserUseCase)
    }

    @Test
    fun welcomeViewModel_login_nullUsername_noUseCaseCall_errorMessage() {
        // Arrange
        welcomeViewModel.password.postValue(password)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        welcomeViewModel.errorMessage.observeForever(errorMessageObserver)

        // Act
        welcomeViewModel.loginButtonClicked()

        // Assert
        verify { loginUserUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun welcomeViewModel_login_nullPassword_noUseCaseCall_errorMessage() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        welcomeViewModel.errorMessage.observeForever(errorMessageObserver)

        // Act
        welcomeViewModel.loginButtonClicked()

        // Assert
        verify { loginUserUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun welcomeViewModel_login_Password_noUseCaseCall_errorMessage() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        welcomeViewModel.errorMessage.observeForever(errorMessageObserver)

        // Act
        welcomeViewModel.loginButtonClicked()

        // Assert
        verify { loginUserUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun welcomeViewModel_login_callUseCaseAndNotifiesSuccess() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        welcomeViewModel.password.postValue(password)
        val params = slot<LoginUserUseCase.Params>()
        val observer = slot<DisposableMaybeObserver<String?>>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        welcomeViewModel.errorMessage.observeForever(errorObserver)
        welcomeViewModel.userLoggedInSuccessFully.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()
        verify { loginUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onSuccess(uuid)

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun welcomeViewModel_login_callUseCaseAndSetsUUID() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        welcomeViewModel.password.postValue(password)
        val params = slot<LoginUserUseCase.Params>()
        val observer = slot<DisposableMaybeObserver<String?>>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        welcomeViewModel.errorMessage.observeForever(errorObserver)
        welcomeViewModel.userLoggedInSuccessFully.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()
        verify { loginUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onSuccess(uuid)

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorObserver wasNot called }

        assertEquals(uuid, welcomeViewModel.UUID.value)
    }

    @Test
    fun welcomeViewModel_loginNonExistingUser_callUseCaseAndNotifiesFailure() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        welcomeViewModel.password.postValue(password)
        val observer = slot<DisposableMaybeObserver<String?>>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        welcomeViewModel.errorMessage.observeForever(errorObserver)
        welcomeViewModel.userLoggedInSuccessFully.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()
        verify { loginUserUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot called }
    }
}