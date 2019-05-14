package be.hogent.faith.faith.emotionCapture.editDetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditDetailViewModelTest {

    private lateinit var viewModel: EditDetailViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EditDetailViewModel()
    }

    @Test
    fun editDetailVM_onGoToEmotionAvatarButtonClicked_callsListeners() {
        val observer = mockk<Observer<Unit>>(relaxed = true)
        viewModel.emotionAvatarButtonClicked.observeForever(observer)

        viewModel.onGoToEmotionAvatarButtonClicked()

        verify { observer.onChanged(any()) }
    }
}