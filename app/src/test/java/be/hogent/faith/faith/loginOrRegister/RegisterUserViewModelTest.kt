package be.hogent.faith.faith.loginOrRegister

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.loginOrRegister.registerAvatar.Avatar
import be.hogent.faith.service.usecases.RegisterUserUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterUserViewModelTest {

    private lateinit var registerUserViewModel: RegisterUserViewModel
    private val registerUserUseCase = mockk<RegisterUserUseCase>(relaxed = true)
    private val username = DataFactory.randomString()
    private val password = DataFactory.randomString()
    private val avatar = Avatar(DataFactory.randomString())

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        registerUserViewModel = RegisterUserViewModel(registerUserUseCase)
    }

    @Test
    fun registerUserViewModel_register_whenUserNameIsNotSet_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserViewModel.setAvatar(avatar)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerUserViewModel.errorMessage.observeForever(errorMessageObserver)

        // Act
        registerUserViewModel.register()

        // Assert
        verify { registerUserUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerUserViewModel_register_whenAvatarIsNotSet_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerUserViewModel.errorMessage.observeForever(errorMessageObserver)

        // Act
        registerUserViewModel.register()

        // Assert
        verify { registerUserUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerUserViewModel_register_callUseCaseWithCorrectParams() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)
        registerUserViewModel.setAvatar(avatar)
        val params = slot<RegisterUserUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)

        registerUserViewModel.errorMessage.observeForever(errorObserver)
        registerUserViewModel.userRegisteredState.observeForever(successObserver)

        // Act
        registerUserViewModel.register()
        verify { registerUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onComplete()

        // Assert
        assertEquals(username, params.captured.username)
        assertEquals(password, params.captured.password)
        assertEquals(avatar.avatarName, params.captured.avatar)
    }

    @Test
    fun registerUserViewModel_register_callUseCaseAndNotifiesSuccess() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)
        registerUserViewModel.setAvatar(avatar)
        val params = slot<RegisterUserUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)

        registerUserViewModel.errorMessage.observeForever(errorObserver)
        registerUserViewModel.userRegisteredState.observeForever(successObserver)

        // Act
        registerUserViewModel.register()
        verify { registerUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun registerUserViewModel_registerWhenUseCaseThrowsError_callUseCaseAndNotifiesFailure() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)
        registerUserViewModel.setAvatar(avatar)
        val params = slot<RegisterUserUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)

        registerUserViewModel.errorMessage.observeForever(errorObserver)
        registerUserViewModel.userRegisteredState.observeForever(successObserver)

        // Act
        registerUserViewModel.register()
        verify { registerUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot called }
    }
}