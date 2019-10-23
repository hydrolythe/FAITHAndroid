package be.hogent.faith.faith.loginOrRegister.registerAvatar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.util.factory.DataFactory
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterAvatarViewModelTest {
    private lateinit var registerAvatarViewModel: RegisterAvatarViewModel
    private val avatarProvider = mockk<ResourceAvatarProvider>(relaxed = true)
    private val listOfAvatars = listOf(Avatar(DataFactory.randomString()))
    private val selection = 0

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        every { avatarProvider.getAvatars() } returns listOfAvatars
        registerAvatarViewModel = RegisterAvatarViewModel(avatarProvider)
    }

    @Test
    fun registerAvatarViewModel_OnConstruction_fetchesAvatars() {
        assertEquals(listOfAvatars, registerAvatarViewModel.avatars.value)
        assertEquals(-1, registerAvatarViewModel.selectedAvatarPosition.value)
    }

    @Test
    fun registerAvatarViewModel_setsSelectedAvatar() {
        registerAvatarViewModel.setSelectedAvatar(selection)
        assertEquals(selection, registerAvatarViewModel.selectedAvatarPosition.value)
    }

    @Test
    fun registerAvatarViewModel_selectedAvatar_WhenNoAvatarIsSelected_ReturnsNull() {
        assertNull(registerAvatarViewModel.selectedAvatar)
    }

    @Test
    fun registerAvatarViewModel_selectedAvatar_WhenAvatarIsSelected_ReturnsAvatar() {
        registerAvatarViewModel.setSelectedAvatar(selection)
        assertNotNull(registerAvatarViewModel.selectedAvatar)
    }

    @Test
    fun registerAvatarViewModel_avatarWasSelectedWhenNoAvatarIsSelected_ReturnsFalse() {
        assertFalse(registerAvatarViewModel.avatarWasSelected())
    }

    @Test
    fun registerAvatarViewModel_avatarWasSelectedWhenAvatarIsSelected_ReturnsTrue() {
        registerAvatarViewModel.setSelectedAvatar(selection)
        assertTrue(registerAvatarViewModel.avatarWasSelected())
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