package be.hogent.faith.faith.chooseAvatar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChooseAvatarViewModelTest {

    private val naam: String = "Boaty McBoatface"
    private val selection: Int = 1

    private lateinit var viewModel: AvatarViewModel

    @Before
    fun setUp() {
        viewModel = AvatarViewModel()
    }

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Test
    fun avatarItemViewModel_setUserName() {
        viewModel.userName.postValue(naam)
        Assert.assertEquals(naam, getValue(viewModel.userName))
    }

    @Test
    fun avatarItemViewModel_setSelectedItem() {
        viewModel.setSelectedItem(selection.toLong())
        Assert.assertEquals(selection, getValue(viewModel.selectedItem).toInt())
    }
}