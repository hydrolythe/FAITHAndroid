package be.hogent.faith.faith.loginOrRegister

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.TestUtils.getValue
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

class ChooseAvatarViewModelTest {

    private val name: String = "Boaty McBoatface"
    private val selection: Int = 0
    private val avatarProvider = mockk<ResourceAvatarProvider>()
    private val listOfAvatars = listOf(Avatar(DataFactory.randomString()))
    private var createUserUseCase = mockk<CreateUserUseCase>(relaxed = true)

    private lateinit var viewModel: AvatarViewModel

    @Before
    fun setUp() {
        every { avatarProvider.getAvatars() } returns listOfAvatars
        viewModel = AvatarViewModel(avatarProvider, createUserUseCase)
    }

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Test
    fun avatarViewModel_setUserName() {
        viewModel.userName.postValue(name)
        assertEquals(name, getValue(viewModel.userName))
    }

    @Test
    fun avatarViewModel_setSelectedItem() {
        viewModel.setSelectedItem(selection.toLong())
        assertEquals(selection, getValue(viewModel.selectedItem).toInt())
    }

    @Test
    fun avatarViewModel_fetchesAvatarOnConstruction() {
        assertEquals(listOfAvatars, viewModel.avatars.value)
    }

    @Test
    fun avatarViewModel_saveButtonClicked__CallUseCaseWithCorrectParams() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)

        // Act
        viewModel.nextButtonPressed()

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

        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<User>>(relaxed = true)
        viewModel.userSaveFailed.observeForever(failObserver)
        viewModel.userSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.nextButtonPressed()
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

        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<User>>(relaxed = true)
        viewModel.userSaveFailed.observeForever(failObserver)
        viewModel.userSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.nextButtonPressed()
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
        viewModel.userName.postValue(name)

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.inputErrorMessageID.observeForever(errorObserver)

        // Act
        viewModel.nextButtonPressed()

        // Assert
        verify { createUserUseCase wasNot called }
        verify { errorObserver.onChanged(any()) }
    }

    @Test
    fun eventViewModel_noNameGiven_notifiesAndNoUseCaseCalled() {
        // Arrange
        viewModel.setSelectedItem(selection.toLong())

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.inputErrorMessageID.observeForever(errorObserver)

        // Act
        viewModel.nextButtonPressed()

        // Assert
        verify { createUserUseCase wasNot called }
        verify { errorObserver.onChanged(any()) }
    }
}