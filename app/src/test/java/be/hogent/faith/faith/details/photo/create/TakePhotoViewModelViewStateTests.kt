package be.hogent.faith.faith.details.photo.create

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TakePhotoViewModelViewStateTests {
    private lateinit var viewModel: TakePhotoViewModel

    private val createPhotoDetailUseCase: CreatePhotoDetailUseCase = mockk()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = TakePhotoViewModel(createPhotoDetailUseCase)
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
    fun takePhotoVM_photoTaken_buttonsVisibilityChanges() {
        val observer = mockk<Observer<Int>>(relaxed = true)
        viewModel.visibilityTakePhoto.observeForever(observer)
        viewModel.visibilityPhotoTakenNotSaved.observeForever(observer)
        viewModel.visibilityPhotoTakenOrSaved.observeForever(observer)

        viewModel.photoTaken(mockk())

        Assert.assertEquals(View.GONE, viewModel.visibilityTakePhoto.value!!)
        Assert.assertEquals(View.VISIBLE, viewModel.visibilityPhotoTakenNotSaved.value!!)
        Assert.assertEquals(View.VISIBLE, viewModel.visibilityPhotoTakenOrSaved.value!!)
    }

    @Test
    fun takePhotoVM_onOkPhotoButtonClicked_callsListeners() {
        // Arrange
        val okButtonObserver = mockk<Observer<Unit>>()
        every { okButtonObserver.onChanged(any()) } returns Unit
        every { createPhotoDetailUseCase.execute(any(), any()) } returns
        viewModel.okPhotoButtonClicked.observeForever(okButtonObserver)

        // Act
        viewModel.photoTaken(mockk())
        viewModel.onOkPhotoButtonClicked()

        // Assert
        verify { okButtonObserver.onChanged(any()) }
    }

    @Test
    fun takePhotoVM_onNotOkPhotoButtonClicked_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Unit>>()
        every { observer.onChanged(any()) } returns Unit
        viewModel.notOkPhotoButtonClicked.observeForever(observer)

        // Act
        viewModel.photoTaken(mockk())
        viewModel.onNotOkPhotoButtonClicked()

        // Assert
        verify { observer.onChanged(any()) }
    }

    @Test
    fun takePhotoVM_onNotOkPhotoButtonClicked_buttonsVisibilityChanges() {
        // Arrange
        val observer = mockk<Observer<Int>>(relaxed = true)
        viewModel.visibilityTakePhoto.observeForever(observer)
        viewModel.visibilityPhotoTakenNotSaved.observeForever(observer)
        viewModel.visibilityPhotoTakenOrSaved.observeForever(observer)

        // Act
        viewModel.photoTaken(mockk())
        viewModel.onNotOkPhotoButtonClicked()

        // Assert
        Assert.assertEquals(View.VISIBLE, viewModel.visibilityTakePhoto.value!!)
        Assert.assertEquals(View.GONE, viewModel.visibilityPhotoTakenNotSaved.value!!)
        Assert.assertEquals(View.GONE, viewModel.visibilityPhotoTakenOrSaved.value!!)
    }
}