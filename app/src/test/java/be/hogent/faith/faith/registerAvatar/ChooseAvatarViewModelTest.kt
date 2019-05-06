package be.hogent.faith.faith.registerAvatar

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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChooseAvatarViewModelTest {

    private val name: String = "Boaty McBoatface"
    private val selection: Int = 0
    private val avatarProvider = mockk<ResourceAvatarProvider>()
    private val listOfAvatars = listOf(Avatar(DataFactory.randomString()))
    private var createUserUseCase = mockk<CreateUserUseCase>()

    private lateinit var viewModel: AvatarViewModel

    @Before
    fun setUp() {
        every { avatarProvider.getAvatars() } returns listOfAvatars
        viewModel = AvatarViewModel(avatarProvider, createUserUseCase)
    }

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Test
    fun avatarItemViewModel_setUserName() {
        viewModel.userName.postValue(name)
        assertEquals(name, getValue(viewModel.userName))
    }

    @Test
    fun avatarItemViewModel_setSelectedItem() {
        viewModel.setSelectedItem(selection.toLong())
        assertEquals(selection, getValue(viewModel.selectedItem).toInt())
    }

    @Test
    fun avatarItemViewModel_fetchesAvatarOnConstruction() {
        assertEquals(listOfAvatars, viewModel.avatars.value)
    }

    @Test
    fun eventVM_saveButtonClicked__CallUseCaseWithCorrectParams() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)
        every { createUserUseCase.execute(any(), capture(params)) }

        // Act
        viewModel.nextButtonPressed()

        // Assert
        verify { createUserUseCase.execute(any(), any()) }
        with(params.captured) {
            assertEquals(username, name)
            assertEquals(avatarName, listOfAvatars[selection].avatarName)
        }
    }

    @Test
    fun avatarItemViewModel_nextButtonClicked_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)
        every { createUserUseCase.execute(any(), capture(params)) }

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<User>>(relaxed = true)
        viewModel.userSaveFailed.observeForever(failObserver)
        viewModel.userSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.nextButtonPressed()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failObserver wasNot called }
    }

    @Test
    fun eventVM_saveButtonClicked_callUseCaseAndNotifiesFailure() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)
        every { createUserUseCase.execute(any(), capture(params)) }

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<User>>(relaxed = true)
        viewModel.userSaveFailed.observeForever(failObserver)
        viewModel.userSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.nextButtonPressed()

        // Assert
        verify { failObserver.onChanged(any()) }
        verify { successObserver wasNot called }
    }
}