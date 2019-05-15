package be.hogent.faith.faith.loginOrRegister

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.loginOrRegister.registerAvatar.Avatar
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.ResourceAvatarProvider
import be.hogent.faith.service.usecases.CreateUserUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableSingleObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChooseRegisterAvatarViewModelTest {

    private val name: String = "Boaty McBoatface"
    private val selection: Int = 0
    private val avatarProvider = mockk<ResourceAvatarProvider>()
    private val listOfAvatars = listOf(Avatar(DataFactory.randomString()))
    private var createUserUseCase = mockk<CreateUserUseCase>(relaxed = true)

    private lateinit var viewModelRegister: RegisterAvatarViewModel

    @Before
    fun setUp() {
        every { avatarProvider.getAvatars() } returns listOfAvatars
        viewModelRegister = RegisterAvatarViewModel(
            avatarProvider,
            createUserUseCase
        )
    }

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Test
    fun avatarViewModel_setUserName() {
        viewModelRegister.userName.postValue(name)
        assertEquals(name, getValue(viewModelRegister.userName))
    }

    @Test
    fun avatarViewModel_setSelectedItem() {
        viewModelRegister.setSelectedItem(selection.toLong())
        assertEquals(selection, getValue(viewModelRegister.selectedItem).toInt())
    }

    @Test
    fun avatarViewModel_fetchesAvatarOnConstruction() {
        assertEquals(listOfAvatars, viewModelRegister.avatars.value)
    }

    @Test
    fun avatarViewModel_saveButtonClicked__CallUseCaseWithCorrectParams() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        viewModelRegister.setSelectedItem(selection.toLong())
        viewModelRegister.userName.postValue(name)

        // Act
        viewModelRegister.nextButtonPressed()

        // Assert
        verify { createUserUseCase.execute(capture(params), any()) }
        with(params.captured) {
            assertEquals(username, name)
            assertEquals(avatarName, listOfAvatars[selection].avatarName)
        }
    }

    @Test
    fun avatarViewModel_inputOK_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        val observer = slot<DisposableSingleObserver<User>>()

        viewModelRegister.setSelectedItem(selection.toLong())
        viewModelRegister.userName.postValue(name)

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<User>>(relaxed = true)
        viewModelRegister.userSaveFailed.observeForever(failObserver)
        viewModelRegister.userSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModelRegister.nextButtonPressed()
        verify { createUserUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onSuccess(mockk())

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failObserver wasNot called }
    }

    @Test
    fun eventViewModel_UseCaseFails_notifiesFailure() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        val observer = slot<DisposableSingleObserver<User>>()

        viewModelRegister.setSelectedItem(selection.toLong())
        viewModelRegister.userName.postValue(name)

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<User>>(relaxed = true)
        viewModelRegister.userSaveFailed.observeForever(failObserver)
        viewModelRegister.userSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModelRegister.nextButtonPressed()
        verify { createUserUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the error handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { failObserver.onChanged(any()) }
        verify { successObserver wasNot called }
    }

    @Test
    fun eventViewModel_noAvatarChosen_notifiesAndNoUseCaseCalled() {
        // Arrange
        viewModelRegister.userName.postValue(name)

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        viewModelRegister.inputErrorMessageID.observeForever(errorObserver)

        // Act
        viewModelRegister.nextButtonPressed()

        // Assert
        verify { createUserUseCase wasNot called }
        verify { errorObserver.onChanged(any()) }
    }

    @Test
    fun eventViewModel_noNameGiven_notifiesAndNoUseCaseCalled() {
        // Arrange
        viewModelRegister.setSelectedItem(selection.toLong())

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        viewModelRegister.inputErrorMessageID.observeForever(errorObserver)

        // Act
        viewModelRegister.nextButtonPressed()

        // Assert
        verify { createUserUseCase wasNot called }
        verify { errorObserver.onChanged(any()) }
    }
}