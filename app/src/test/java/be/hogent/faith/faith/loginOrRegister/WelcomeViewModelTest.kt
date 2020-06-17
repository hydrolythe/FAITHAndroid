package be.hogent.faith.faith.loginOrRegister

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.usecases.user.LoginUserUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.rxjava3.observers.DisposableMaybeObserver
import org.junit.Assert
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
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        welcomeViewModel.userLoggedInState.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()

        // Assert
        verify { loginUserUseCase wasNot called }
        Assert.assertEquals(
            ResourceState.ERROR,
            welcomeViewModel.userLoggedInState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }

    @Test
    fun welcomeViewModel_login_nullPassword_noUseCaseCall_errorMessage() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        welcomeViewModel.userLoggedInState.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()

        // Assert
        verify { loginUserUseCase wasNot called }
        Assert.assertEquals(
            ResourceState.ERROR,
            welcomeViewModel.userLoggedInState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }

    @Test
    fun welcomeViewModel_login_PasswordToShort_noUseCaseCall_errorMessage() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        welcomeViewModel.password.postValue("abc")
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        welcomeViewModel.userLoggedInState.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()

        // Assert
        verify { loginUserUseCase wasNot called }
        Assert.assertEquals(
            ResourceState.ERROR,
            welcomeViewModel.userLoggedInState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }

    @Test
    fun welcomeViewModel_login_callUseCaseAndNotifiesSuccess() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        welcomeViewModel.password.postValue(password)
        val params = slot<LoginUserUseCase.Params>()
        val observer = slot<DisposableMaybeObserver<String?>>()

        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        welcomeViewModel.userLoggedInState.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()
        verify { loginUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onSuccess(uuid)

        // Assert
        verify(atLeast = 2) { successObserver.onChanged(any()) }
        Assert.assertEquals(
            ResourceState.SUCCESS,
            welcomeViewModel.userLoggedInState.value?.status
        )
    }

    @Test
    fun welcomeViewModel_loginNonExistingUser_callUseCaseAndNotifiesFailure() {
        // Arrange
        welcomeViewModel.userName.postValue(username)
        welcomeViewModel.password.postValue(password)
        val observer = slot<DisposableMaybeObserver<String?>>()

        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        welcomeViewModel.userLoggedInState.observeForever(successObserver)

        // Act
        welcomeViewModel.loginButtonClicked()
        verify { loginUserUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        Assert.assertEquals(
            ResourceState.ERROR,
            welcomeViewModel.userLoggedInState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }
}