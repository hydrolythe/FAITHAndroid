package be.hogent.faith.faith.loginOrRegister

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.loginOrRegister.registerAvatar.Avatar
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.usecases.user.RegisterUserUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert
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

        // Act
        registerUserViewModel.register()

        // Assert
        Assert.assertEquals(
            ResourceState.ERROR,
            registerUserViewModel.userRegisteredState.value?.status
        )
        verify { registerUserUseCase wasNot called }
    }

    @Test
    fun registerUserViewModel_register_whenAvatarIsNotSet_noUseCaseCall_errorMessage() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)

        // Act
        registerUserViewModel.register()

        // Assert
        Assert.assertEquals(
            ResourceState.ERROR,
            registerUserViewModel.userRegisteredState.value?.status
        )
        verify { registerUserUseCase wasNot called }
    }

    @Test
    fun registerUserViewModel_register_callUseCaseWithCorrectParams() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)
        registerUserViewModel.setAvatar(avatar)
        val params = slot<RegisterUserUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)

        registerUserViewModel.userRegisteredState.observeForever(successObserver)

        // Act
        registerUserViewModel.register()
        verify { registerUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onComplete()

        // Assert
        Assert.assertEquals(username, params.captured.username)
        Assert.assertEquals(password, params.captured.password)
        Assert.assertEquals(avatar.avatarName, params.captured.avatar)
    }

    @Test
    fun registerUserViewModel_register_callUseCaseAndNotifiesSuccess() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)
        registerUserViewModel.setAvatar(avatar)
        val params = slot<RegisterUserUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        registerUserViewModel.userRegisteredState.observeForever(successObserver)

        // Act
        registerUserViewModel.register()
        verify { registerUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onComplete()

        // Assert
        Assert.assertEquals(
            ResourceState.SUCCESS,
            registerUserViewModel.userRegisteredState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }

    @Test
    fun registerUserViewModel_registerWhenUseCaseThrowsError_callUseCaseAndNotifiesFailure() {
        // Arrange
        registerUserViewModel.setRegistrationDetails(username, password)
        registerUserViewModel.setAvatar(avatar)
        val params = slot<RegisterUserUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        val successObserver = mockk<Observer<Resource<Unit>>>(relaxed = true)
        registerUserViewModel.userRegisteredState.observeForever(successObserver)

        // Act
        registerUserViewModel.register()
        verify { registerUserUseCase.execute(capture(params), capture(observer)) }
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        Assert.assertEquals(
            ResourceState.ERROR,
            registerUserViewModel.userRegisteredState.value?.status
        )
        verify(atLeast = 2) { successObserver.onChanged(any()) }
    }
}