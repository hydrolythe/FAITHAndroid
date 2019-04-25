package be.hogent.faith.faith.emotionCapture.drawEmotionAvatar

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionViewModel.LineWidth
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
    fun drawEmotionVM_selectLineWidth_isSelected() {
        viewModel.setLineWidth(LineWidth.MEDIUM)
        assertEquals(LineWidth.MEDIUM, getValue(viewModel.selectedLineWidth))

        viewModel.setLineWidth(LineWidth.THIN)
        assertEquals(LineWidth.THIN, getValue(viewModel.selectedLineWidth))
    }
}