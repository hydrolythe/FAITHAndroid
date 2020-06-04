package be.hogent.faith.faith.loginOrRegister.registerAvatar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.service.usecases.user.InitialiseUserUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterAvatarViewModelTest {
    private lateinit var registerAvatarViewModel: RegisterAvatarViewModel
    private val avatarProvider = mockk<ResourceAvatarProvider>(relaxed = true)
    private val listOfAvatars = listOf(Avatar(DataFactory.randomString()))
    private val selection = 0
    private val selectedSkinColor = SkinColor.dark_brown
    private val initialiseUserUseCase = mockk<InitialiseUserUseCase>(relaxed = true)
    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        every { avatarProvider.getAvatars(SkinColor.blank) } returns listOfAvatars
        registerAvatarViewModel = RegisterAvatarViewModel(avatarProvider, initialiseUserUseCase)
    }

    @Test
    fun registerAvatarViewModel_OnConstruction_fetchesAvatars() {
        Assert.assertEquals(listOfAvatars, registerAvatarViewModel.avatars.value)
        Assert.assertEquals(-1, registerAvatarViewModel.selectedAvatarPosition.value)
    }

    @Test
    fun registerAvatarViewModel_setsSelectedAvatar() {
        registerAvatarViewModel.setSelectedAvatar(selection)
        Assert.assertEquals(selection, registerAvatarViewModel.selectedAvatarPosition.value)
    }

    @Test
    fun registerAvatarViewModel_setsSelectedSkinColor() {
        registerAvatarViewModel.setSelectedSkinColor(selectedSkinColor)
        Assert.assertEquals(selectedSkinColor, registerAvatarViewModel.selectedSkinColor.value)
    }
    @Test
    fun registerAvatarViewModel_selectedAvatar_WhenNoAvatarIsSelected_ReturnsNull() {
        Assert.assertNull(registerAvatarViewModel.selectedAvatar)
    }

    @Test
    fun registerAvatarViewModel_selectedAvatar_WhenAvatarIsSelected_ReturnsAvatar() {
        registerAvatarViewModel.setSelectedAvatar(selection)
        Assert.assertNotNull(registerAvatarViewModel.selectedAvatar)
    }

    @Test
    fun registerAvatarViewModel_selectedSkinColor_WhenSkinColorIsSelected_ReturnsSkinColor() {
        registerAvatarViewModel.setSelectedSkinColor(selectedSkinColor)
        Assert.assertNotNull(registerAvatarViewModel.selectedSkinColor)
    }
    @Test
    fun registerAvatarViewModel_avatarWasSelectedWhenNoAvatarIsSelected_ReturnsFalse() {
        Assert.assertFalse(registerAvatarViewModel.avatarWasSelected())
    }

    @Test
    fun registerAvatarViewModel_avatarWasSelectedWhenAvatarIsSelected_ReturnsTrue() {
        registerAvatarViewModel.setSelectedAvatar(selection)
        Assert.assertTrue(registerAvatarViewModel.avatarWasSelected())
    }

    @Test
    fun registerAvatarViewModel_onFinishRegistrationClickedWhenNoAvatarIsSelected_NotifiesError() {
        // Arrange
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerAvatarViewModel.errorMessage.observeForever(errorMessageObserver)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        registerAvatarViewModel.finishRegistrationClicked.observeForever(successObserver)

        // Act
        registerAvatarViewModel.onFinishRegistrationClicked()

        // Assert
        verify { successObserver wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun registerAvatarViewModel_onFinishRegistrationClickedWhenAvatarIsSelected_NotifiesSuccess() {
        // Arrange
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        registerAvatarViewModel.errorMessage.observeForever(errorMessageObserver)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        registerAvatarViewModel.finishRegistrationClicked.observeForever(successObserver)
        registerAvatarViewModel.setSelectedAvatar(selection)

        // Act
        registerAvatarViewModel.onFinishRegistrationClicked()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorMessageObserver wasNot called }
    }
}