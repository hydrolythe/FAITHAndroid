package be.hogent.faith.faith.emotionCapture.takePhoto

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TakePhotoViewModelTest {
    private lateinit var viewModel: TakePhotoViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = TakePhotoViewModel()
    }

    @Test
    fun takePhotoVM_onTakePhotoButtonClicked_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Unit>>()
        every { observer.onChanged(any()) } returns Unit
        viewModel.takePhotoButtonClicked.observeForever(observer)

        // Act
        viewModel.onTakePhotoButtonClicked()

        // Assert
        verify { observer.onChanged(any()) }
    }
}
