package be.hogent.faith.faith.drawEmotionAvatar

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel.LineWidth
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DrawEmotionViewModelTest {

    private lateinit var viewModel: DrawEmotionViewModel
    private val saveEmotionAvatarUseCase = mockk<SaveEmotionAvatarUseCase>()
    private val event = mockk<Event>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = DrawEmotionViewModel(saveEmotionAvatarUseCase, event)
    }

    @Test
    fun drawEmotionVM_pickNewColor_isSelected() {
        viewModel.pickColor(Color.BLACK)
        assertEquals(Color.BLACK, getValue(viewModel.selectedColor))

        viewModel.pickColor(Color.GREEN)
        assertEquals(Color.GREEN, getValue(viewModel.selectedColor))
    }

    @Test
    fun drawEmotionVM_selectLineWidth_isSelected() {
        viewModel.setLineWidth(LineWidth.MEDIUM)
        assertEquals(LineWidth.MEDIUM, getValue(viewModel.selectedLineWidth))

        viewModel.setLineWidth(LineWidth.THIN)
        assertEquals(LineWidth.THIN, getValue(viewModel.selectedLineWidth))
    }

    @Test
    fun drawEmotionVM_saveImage_callsUseCase() {
        // Arrange
        val params = slot<SaveEmotionAvatarUseCase.Params>()
        every { saveEmotionAvatarUseCase.execute(capture(params)) } returns Completable.complete()

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { saveEmotionAvatarUseCase.execute(any()) }
        assertEquals(event, params.captured.event)
        }

    @Test
    fun drawEmotionVM_saveImage_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        every { saveEmotionAvatarUseCase.execute(any()) } returns Completable.complete()
        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun drawEmotionVM_saveImage_notifiesWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        val errorMessage = "Something failed"

        every { saveEmotionAvatarUseCase.execute(any()) } returns Completable.error(RuntimeException(errorMessage))

        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { failedObserver.onChanged(errorMessage) }
        verify { successObserver wasNot Called }
    }
}