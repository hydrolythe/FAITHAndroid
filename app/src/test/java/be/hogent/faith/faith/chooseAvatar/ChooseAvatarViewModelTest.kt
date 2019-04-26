package be.hogent.faith.faith.chooseAvatar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.chooseAvatar.fragments.Avatar
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarViewModel
import be.hogent.faith.faith.util.AvatarProvider
import be.hogent.faith.service.usecases.CreateUserUseCase
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ChooseAvatarViewModelTest {

    private val name: String = "Boaty McBoatface"
    private val selection: Int = 0
    private val avatarProvider = mockk<AvatarProvider>()
    private val listOfAvatars = listOf(Avatar(1, "Avatar"))
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
        Assert.assertEquals(name, getValue(viewModel.userName))
    }

    @Test
    fun avatarItemViewModel_setSelectedItem() {
        viewModel.setSelectedItem(selection.toLong())
        Assert.assertEquals(selection, getValue(viewModel.selectedItem).toInt())
    }

    @Test
    fun avatarItemViewModel_fetchesAvatarOnConstruction() {
        Assert.assertEquals(listOfAvatars, viewModel.avatarItems.value)
    }

    @Test
    fun eventVM_saveButtonClicked__CallUseCaseWithCorrectParams() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)
        every { createUserUseCase.execute(capture(params)) } returns Completable.complete()

        // Act
        viewModel.nextButtonPressed()

        // Assert
        verify { createUserUseCase.execute(any()) }
        with(params.captured) {
            assertEquals(username, name)
            assertEquals(avatar, listOfAvatars[selection].imageName)
        }
    }

    @Test
    fun avatarItemViewModel_nextButtonClicked_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val params = slot<CreateUserUseCase.Params>()
        viewModel.setSelectedItem(selection.toLong())
        viewModel.userName.postValue(name)
        every { createUserUseCase.execute(capture(params)) } returns Completable.complete()

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
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
        every { createUserUseCase.execute(capture(params)) } returns Completable.error(IOException())

        val failObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        viewModel.userSaveFailed.observeForever(failObserver)
        viewModel.userSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.nextButtonPressed()

        // Assert
        verify { failObserver.onChanged(any()) }
        verify { successObserver wasNot called }
    }
}