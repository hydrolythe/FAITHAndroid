package be.hogent.faith.faith.drawEmotionAvatar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarItemViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChooseAvatarItemViewModelTest {

    val naam: String = "Boaty McBoatface"
    val selection : Int = 1

    private lateinit var viewModel: AvatarItemViewModel

    @Before
    fun setUp() {
        viewModel = AvatarItemViewModel()
    }

    @get:Rule
    val testRule = InstantTaskExecutorRule()


    @Test
    fun avatarItemViewModel_setUserName() {
        viewModel.userName.postValue(naam)
        Assert.assertEquals(naam,getValue(viewModel.userName))
    }

    @Test
    fun avatarItemViewModel_setSelectedItem(){
        viewModel.setSelectedItem(selection.toLong())
        Assert.assertEquals(selection, getValue(viewModel.selectedItem).toInt())
    }

}