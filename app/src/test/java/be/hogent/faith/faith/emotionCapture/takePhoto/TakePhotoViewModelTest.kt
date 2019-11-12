package be.hogent.faith.faith.emotionCapture.takePhoto

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.details.photo.create.TakePhotoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
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

    @Test
    fun takePhotoVM_setPhotoInCache_changesState() {
        val observer = mockk<Observer<Int>>(relaxed = true)
        viewModel.visibilityTakePhoto.observeForever(observer)
        viewModel.visibilityPhotoTakenNotSaved.observeForever(observer)
        viewModel.visibilityPhotoTakenOrSaved.observeForever(observer)
        viewModel.setPhotoInCache(mockk())
        Assert.assertEquals(View.GONE, viewModel.visibilityTakePhoto.value!!)
        Assert.assertEquals(View.VISIBLE, viewModel.visibilityPhotoTakenNotSaved.value!!)
        Assert.assertEquals(View.VISIBLE, viewModel.visibilityPhotoTakenOrSaved.value!!)
    }

    @Test
    fun takePhotoVM_onOkPhotoButtonClicked_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Unit>>()
        every { observer.onChanged(any()) } returns Unit

        viewModel.okPhotoButtonClicked.observeForever(observer)

        // Act
        viewModel.setPhotoInCache(mockk())
        viewModel.onOkPhotoButtonClicked()

        // Assert
        verify { observer.onChanged(any()) }
    }

    @Test
    fun takePhotoVM_setSavedPhoto_changesVisibilityObservers() {
        val observer = mockk<Observer<Int>>(relaxed = true)
        viewModel.visibilityTakePhoto.observeForever(observer)
        viewModel.visibilityPhotoTakenNotSaved.observeForever(observer)
        viewModel.visibilityPhotoTakenOrSaved.observeForever(observer)
        viewModel.setPhotoInCache(mockk())
        viewModel.setSavedPhoto(mockk())
        Assert.assertEquals(View.GONE, viewModel.visibilityTakePhoto.value!!)
        Assert.assertEquals(View.GONE, viewModel.visibilityPhotoTakenNotSaved.value!!)
        Assert.assertEquals(View.VISIBLE, viewModel.visibilityPhotoTakenOrSaved.value!!)
    }

    @Test
    fun takePhotoVM_onNotOkPhotoButtonClicked_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Unit>>()
        every { observer.onChanged(any()) } returns Unit
        viewModel.notOkPhotoButtonClicked.observeForever(observer)

        // Act
        viewModel.setPhotoInCache(mockk())
        viewModel.onNotOkPhotoButtonClicked()

        // Assert
        verify { observer.onChanged(any()) }
    }

    @Test
    fun takePhotoVM_onNotOkPhotoButtonClicked_changesVisibilityObservers() {
        val observer = mockk<Observer<Int>>(relaxed = true)
        viewModel.visibilityTakePhoto.observeForever(observer)
        viewModel.visibilityPhotoTakenNotSaved.observeForever(observer)
        viewModel.visibilityPhotoTakenOrSaved.observeForever(observer)
        viewModel.setPhotoInCache(mockk())
        viewModel.onNotOkPhotoButtonClicked()
        Assert.assertEquals(View.VISIBLE, viewModel.visibilityTakePhoto.value!!)
        Assert.assertEquals(View.GONE, viewModel.visibilityPhotoTakenNotSaved.value!!)
        Assert.assertEquals(View.GONE, viewModel.visibilityPhotoTakenOrSaved.value!!)
    }
}