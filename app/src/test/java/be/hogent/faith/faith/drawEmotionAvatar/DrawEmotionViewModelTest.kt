package be.hogent.faith.faith.drawEmotionAvatar

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DrawEmotionViewModelTest {
    private lateinit var viewModel: DrawEmotionViewModel
    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = DrawEmotionViewModel()
    }

    @Test
    fun drawEmotionVM_pickNewColor_isSelected() {
        viewModel.pickColor(Color.BLACK)
        assertEquals(Color.BLACK, getValue(viewModel.selectedColor))

        viewModel.pickColor(Color.GREEN)
        assertEquals(Color.GREEN, getValue(viewModel.selectedColor))
    }

    @Test
    fun drawEmotionVM_toggleEraser_toggles() {
        val initialValue = getValue(viewModel.eraserSelected)

        viewModel.toggleEraser()
        assertEquals(initialValue.not(), getValue(viewModel.eraserSelected))

        viewModel.toggleEraser()
        assertEquals(initialValue, getValue(viewModel.eraserSelected))
    }

    @Test
    fun drawEmotionVM_selectLineWidth_isSelected() {
        viewModel.setLineWidth(LineWidth.MEDIUM)
        assertEquals(LineWidth.MEDIUM, getValue(viewModel.selectedLineWidth))

        viewModel.setLineWidth(LineWidth.THIN)
        assertEquals(LineWidth.THIN, getValue(viewModel.selectedLineWidth))
    }
}