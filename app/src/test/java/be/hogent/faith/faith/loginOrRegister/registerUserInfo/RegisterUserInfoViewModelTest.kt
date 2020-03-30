package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.usecases.IsUsernameUniqueUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableSingleObserver
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterUserInfoViewModelTest {

    private lateinit var registerUserInfoViewModel: RegisterUserInfoViewModel
    private val isUsernameUniqueUseCase = mockk<IsUsernameUniqueUseCase>(relaxed = true)
    private val username = DataFactory.randomString()
    private val password = DataFactory.randomString()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        registerUserInfoViewModel = RegisterUserInfoViewModel(isUsernameUniqueUseCase)
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClicked_nullUsername_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserInfoViewModel.password.postValue(password)
        registerUserInfoViewModel.passwordRepeated.postValue(password)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerUserInfoViewModel.userNameErrorMessage.observeForever(errorMessageObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()

        // Assert
        verify { isUsernameUniqueUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClicked_usernamewithInvalidCharacter_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserInfoViewModel.userName.postValue("$username@faith.be")
        registerUserInfoViewModel.passwordRepeated.postValue(password)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerUserInfoViewModel.userNameErrorMessage.observeForever(errorMessageObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()

        // Assert
        verify { isUsernameUniqueUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClicked_nullPassword_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserInfoViewModel.userName.postValue(username)
        registerUserInfoViewModel.passwordRepeated.postValue(password)
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerUserInfoViewModel.passwordErrorMessage.observeForever(errorMessageObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()

        // Assert
        verify { isUsernameUniqueUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClicked_PasswordLessThan6positions_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserInfoViewModel.userName.postValue(username)
        registerUserInfoViewModel.password.postValue("test")
        registerUserInfoViewModel.passwordRepeated.postValue("test")
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerUserInfoViewModel.passwordErrorMessage.observeForever(errorMessageObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()

        // Assert
        verify { isUsernameUniqueUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClicked_PasswordRepeatedDifferentFromPassword_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserInfoViewModel.userName.postValue(username)
        registerUserInfoViewModel.password.postValue(password)
        registerUserInfoViewModel.passwordRepeated.postValue("test")
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerUserInfoViewModel.passwordRepeatErrorMessage.observeForever(errorMessageObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()

        // Assert
        verify { isUsernameUniqueUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClicked_callUseCaseAndNotifiesSuccess() {
        // Arrange
        registerUserInfoViewModel.userName.postValue(username)
        registerUserInfoViewModel.password.postValue(password)
        registerUserInfoViewModel.passwordRepeated.postValue(password)
        val params = slot<IsUsernameUniqueUseCase.Params>()
        val observer = slot<DisposableSingleObserver<Boolean>>()

        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        registerUserInfoViewModel.userConfirmedState.observeForever(successObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()
        verify { isUsernameUniqueUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onSuccess(true)

        // Assert
        Assert.assertEquals(
            ResourceState.SUCCESS,
            registerUserInfoViewModel.userConfirmedState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClickedWhenUsernameAlreadyExists_callUseCaseAndNotifiesFailure() {
        // Arrange
        registerUserInfoViewModel.userName.postValue(username)
        registerUserInfoViewModel.password.postValue(password)
        registerUserInfoViewModel.passwordRepeated.postValue(password)
        val params = slot<IsUsernameUniqueUseCase.Params>()
        val observer = slot<DisposableSingleObserver<Boolean>>()

        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        registerUserInfoViewModel.userConfirmedState.observeForever(successObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()
        verify { isUsernameUniqueUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onSuccess(false)

        // Assert
        Assert.assertEquals(
            ResourceState.ERROR,
            registerUserInfoViewModel.userConfirmedState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }

    @Test
    fun registerUserInfoViewModel_onConfirmUserInfoClickedWhenUseCaseThrowsError_callUseCaseAndNotifiesFailure() {
        // Arrange
        registerUserInfoViewModel.userName.postValue(username)
        registerUserInfoViewModel.password.postValue(password)
        registerUserInfoViewModel.passwordRepeated.postValue(password)
        val params = slot<IsUsernameUniqueUseCase.Params>()
        val observer = slot<DisposableSingleObserver<Boolean>>()

        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        registerUserInfoViewModel.userConfirmedState.observeForever(successObserver)

        // Act
        registerUserInfoViewModel.onConfirmUserInfoClicked()
        verify { isUsernameUniqueUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        Assert.assertEquals(
            ResourceState.ERROR,
            registerUserInfoViewModel.userConfirmedState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }
}